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

async def save_summation(
        db: AsyncSession,
        debate_id: str,
        position: str,
        user_id: int,
        nickname: str,
        round: int,
        summation: str):
    """
    토론 발언을 데이터베이스에 저장하는 함수 (비동기 방식)

    :param debate_id: 토론 ID
    :param topic: 주제
    :param user_id: 발언자 ID
    :param user_nickname: 발언자 닉네임
    :param round: 라운드
    :param summation: 발언 내용
    """
    try:
        new_entry = Summation(
            debate_id=debate_id,
            position=position,
            user_id=user_id,
            nickname=nickname,
            round=round,
            summation=summation
        )
        db.add(new_entry)
        await db.commit()  # ✅ 저장 보장
        await db.refresh(new_entry)  # ✅ 저장된 객체 최신 상태로 갱신
        print("✅ Summation 저장 성공:", new_entry.id)
    except Exception as e:
        await db.rollback()  # ❌ 오류 발생 시 롤백
        print(f"❌ Summation 저장 오류: {e}")
        raise e
