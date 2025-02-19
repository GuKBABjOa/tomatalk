from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from transcripts.models.models import Statement, Summation

async def save_statement(
        db: AsyncSession,
        debate_id: str,
        position: str,
        user_id: int,
        nickname: str,
        round: int,
        statement: str):
    """
    토론 발언을 데이터베이스에 저장하는 함수 (비동기 방식)

    :param debate_id: 토론 ID
    :param topic: 주제
    :param user_id: 발언자 ID
    :param user_nickname: 발언자 닉네임
    :param round: 라운드
    :param statement: 발언 내용
    """
    new_statement = Statement(
        debate_id=debate_id,
        position=position,
        user_id=user_id,
        nickname=nickname,
        round=round,
        statement=statement
     )
    try:
        db.add(new_statement)
        await db.commit()
        await db.refresh(new_statement)
        print("✅ Statement 저장 성공:", new_statement.id)
    except Exception as e:
        await db.rollback()
        print(f"❌ Statement 저장 오류: {e}")
        raise e
