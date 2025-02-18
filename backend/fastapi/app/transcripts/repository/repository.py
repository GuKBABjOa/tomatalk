from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from transcripts.models.models import Statement, Summation
from typing import Optional

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


async def get_summation(
        db: AsyncSession,
        debate_id: str,
        user_id: Optional[int] = None,
        round: Optional[int] = None) -> list:
    """
    주어진 debate_id, user_id, round에 해당하는 Summation 값을 조회하는 함수

    :param db: AsyncSession 데이터베이스 세션
    :param debate_id: 토론 방 ID(필수)
    :param user_id: 사용자 ID(선택)
    :param round: 라운드 번호(선택)
    :return: 조회된 요약 텍스트 리스트 (없을 경우 빈 문자열)
    """
    try:
        query = select(Summation).where(Summation.debate_id == debate_id)

        if user_id:
            query = query.where(Summation.user_id == user_id)

        if round:
            query = query.where(Summation.round == round)

        result = await db.execute(query)
        summation_obj = result.scalars().all()
        return [ summation.nickname + "(" + summation.position + ") : " +summation.summation for summation in summation_obj] if summation_obj else []
    except Exception as e:
        print(f"❌ Summation 조회 오류: {e}")
        raise e
    
async def get_statement(
        db: AsyncSession,
        debate_id: str,
        user_id: Optional[int] = None,
        round: Optional[int] = None) -> list:
    """
    주어진 debate_id, user_id, round에 해당하는 Statement 값을 조회하는 함수

    :param db: AsyncSession 데이터베이스 세션
    :param debate_id: 토론 방 ID(필수)
    :param user_id: 사용자 ID(선택)
    :param round: 라운드 번호(선택)
    :return: 조회된 발언 텍스트 리스트 (없을 경우 빈 문자열)
    """
    try:
        query = select(Statement).where(Statement.debate_id == debate_id)

        if user_id:
            query = query.where(Statement.user_id == user_id)

        if round:
            query = query.where(Statement.round == round)

        result = await db.execute(query)
        print("조회 성공 ")
        statement_obj = result.scalars().all()
        return [statement.nickname + "(" + statement.position + "): " + statement.statement for statement in statement_obj] if statement_obj else []
    except Exception as e:
        print(f"❌ Statement 조회 오류: {e}")
        raise e


