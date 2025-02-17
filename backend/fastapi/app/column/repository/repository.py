from sqlalchemy.ext.asyncio import AsyncSession
from column.models.models import DebateColumn  # DebateColumn 모델 임포트

async def save_column(
        db: AsyncSession,
        title: str,
        subtitle: str,
        discussion_overview: str,
        participant_1_name: str,
        participant_1_statement: str,
        participant_2_name: str,
        participant_2_statement: str,
        participant_3_name: str,
        participant_3_statement: str,
        participant_4_name: str,
        participant_4_statement: str,
        conclusion: str,
        author_opinion: str
    ):
    """
    토론 칼럼 데이터를 데이터베이스에 저장하는 비동기 함수

    :param db: 비동기 데이터베이스 세션
    :param title: 칼럼 제목
    :param subtitle: 부제목
    :param discussion_overview: 토론 개요
    :param participant_1_name: 참가자 1 이름
    :param participant_1_statement: 참가자 1 발언
    :param participant_2_name: 참가자 2 이름
    :param participant_2_statement: 참가자 2 발언
    :param participant_3_name: 참가자 3 이름
    :param participant_3_statement: 참가자 3 발언
    :param participant_4_name: 참가자 4 이름
    :param participant_4_statement: 참가자 4 발언
    :param conclusion: 토론 결론
    :param author_opinion: 칼럼 작성자의 개인 의견
    """

    new_column = DebateColumn(
        title=title,
        subtitle=subtitle,
        discussion_overview=discussion_overview,
        participant_1_name=participant_1_name,
        participant_1_statement=participant_1_statement,
        participant_2_name=participant_2_name,
        participant_2_statement=participant_2_statement,
        participant_3_name=participant_3_name,
        participant_3_statement=participant_3_statement,
        participant_4_name=participant_4_name,
        participant_4_statement=participant_4_statement,
        conclusion=conclusion,
        author_opinion=author_opinion
    )

    try:
        db.add(new_column)
        await db.commit()
        await db.refresh(new_column)
        print(f"✅ DebateColumn 저장 성공: {new_column.id}")
        return new_column
    except Exception as e:
        await db.rollback()
        print(f"❌ DebateColumn 저장 오류: {e}")
        raise e
