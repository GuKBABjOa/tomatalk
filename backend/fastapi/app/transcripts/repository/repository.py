from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from transcripts.models.models import Statement, Summation, BasicPractice
from sqlalchemy.exc import SQLAlchemyError
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
    except SQLAlchemyError as e:
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
    except SQLAlchemyError as e:
        await db.rollback()  # ❌ 오류 발생 시 롤백
        print(f"❌ Summation 저장 오류: {e}")
        raise e

async def save_basic_practice(
        db: AsyncSession,
        topic: str,
        position: str,
        question: str,
        user_id: int,
        statement: str):
    """
    기초 연습 발언을 데이터베이스에 저장 또는 업데이트하는 함수 (비동기 방식)

    - 동일한 topic, user_id, question 값을 가진 데이터가 이미 존재하면 업데이트
    - 존재하지 않으면 새로 생성하여 저장

    :param topic: 토론 주제
    :param position: 찬성/반대 입장
    :param question: 질문 내용
    :param user_id: 사용자 ID
    :param statement: 발언 내용
    :return: 저장 또는 업데이트된 BasicPractice 객체
    """
    try:
        # 기존 데이터 조회
        result = await db.execute(
            select(BasicPractice)
            .where(BasicPractice.topic == topic, 
                   BasicPractice.user_id == user_id, 
                   BasicPractice.question == question)
        )
        existing_practice = result.scalars().first()

        if existing_practice:
            # 데이터가 존재하면 업데이트
            existing_practice.position = position
            existing_practice.statement = statement

            print(f"🔄 기존 데이터 업데이트: ID {existing_practice.id}")
        else:
            # 존재하지 않으면 새로운 행 추가
            existing_practice = BasicPractice(
                topic=topic,
                position=position,
                question=question,
                user_id=user_id,
                statement=statement
            )
            db.add(existing_practice)
            print("✅ 새로운 BasicPractice 저장")

        # 변경 사항 저장
        await db.commit()
        await db.refresh(existing_practice)

        return existing_practice

    except SQLAlchemyError as e:
        await db.rollback()
        print(f"❌ BasicPractice 저장/업데이트 오류: {e}")
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
        return [statement.nickname + "(" + str(statement.user_id) +","+ statement.position + "): " + statement.statement for statement in statement_obj] if statement_obj else []
    except Exception as e:
        print(f"❌ Statement 조회 오류: {e}")
        raise e

async def get_basic_practice_statements(db: AsyncSession, topic: str, user_id: int) -> str:
    """
    특정 topic과 user_id에 해당하는 발언 데이터를 조회하여 문자열로 변환

    :param db: 데이터베이스 세션
    :param topic: 토론 주제
    :param user_id: 사용자 ID
    :return: "{question1}:{statement1}/{question2}:{statement2}" 형식의 문자열
    """
    try:
        # 데이터 조회
        result = await db.execute(
            select(BasicPractice)
            .where(BasicPractice.topic == topic, BasicPractice.user_id == user_id)
            .order_by(BasicPractice.created_at)  # 생성 시각 기준 정렬
        )
        practices = result.scalars().all()  # 여러 개의 데이터 가져오기

        # 조회된 데이터가 없으면 빈 문자열 반환
        if not practices:
            return ""

        # "{question1}:{statement1}/{question2}:{statement2}" 형식으로 변환
        formatted_string = "/".join(f"{p.question}:{p.statement}" for p in practices)

        print("✅ 데이터 변환 성공:", formatted_string)
        return topic+"/"+formatted_string

    except Exception as e:
        print(f"❌ 데이터 조회 오류: {e}")
        return ""
