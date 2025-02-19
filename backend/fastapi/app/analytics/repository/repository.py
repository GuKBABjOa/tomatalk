from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy import func
from analytics.models.models import SpeechRate

async def save_speechrate(
    db:AsyncSession,
    user_id: int,
    duration: float,
    word_count: int,
    wps: float,
    wpm: float
    ):
    """
    DB에 발화 속도를 저장하는 함수 

    args:

    returns: 

    """
    new_speechrate = SpeechRate(
        user_id = user_id,
        duration = duration,
        word_count = word_count,
        wps = wps,
        wpm = wpm
    )

    try:
        db.add(new_speechrate)
        await db.commit()
        await db.refresh(new_speechrate)
        print(f"✅ SpeechRate 저장 성공: {new_speechrate.id}")
        return new_speechrate
    except Exception as e:
        await db.rollback()
        print(f"❌ SpeechRate 저장 오류: {e}")
        raise e
    

async def get_speechrate(db: AsyncSession, user_id: int):
    """
    특정 사용자(user_id)의 최근 12개 발화 속도의 평균값을 구하는 함수

    Args:
        db (AsyncSession): 데이터베이스 세션
        user_id (int): 사용자 ID

    Returns:
        dict: 최근 12개 발화 속도의 평균값 (WPS, WPM)
    """
    try:
        result = await db.execute(
            select(
                func.avg(SpeechRate.words_per_second).label("avg_wps"),
                func.avg(SpeechRate.words_per_minute).label("avg_wpm")
            )
            .where(SpeechRate.user_id == user_id)
            .order_by(SpeechRate.created_at.desc())  # 최신 데이터 기준 정렬
            .limit(12)  # 최근 12개만 계산
        )
        averages = result.fetchone()
        
        return {
            "average_words_per_second": round(averages.avg_wps, 2) if averages.avg_wps else None,
            "average_words_per_minute": round(averages.avg_wpm, 2) if averages.avg_wpm else None
        }

    except Exception as e:
        await db.rollback()
        print(f"❌ 데이터 조회 오류: {e}")
        raise e