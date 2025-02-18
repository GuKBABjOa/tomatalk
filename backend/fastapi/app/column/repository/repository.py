from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy import func, and_, or_, desc, update, insert
from column.models.models import DebateColumn, Likes 
from typing import Optional
from column.schemas.schemas import LikeResponse

async def save_column(
        db: AsyncSession,
        title: str,
        subtitle: str,
        category: str,
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
    :param categry: 카테고리
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
        category = category,
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

async def get_column_list(
    db: AsyncSession,
    user_id: Optional[int] = None,  # ✅ 사용자 ID 추가 (좋아요 여부 확인용)
    category: Optional[str] = None,
    keyword: Optional[str] = None,
    cursor: Optional[str] = None,
    limit: int = 10,
    sort: str = "created_at"
):
    """
    ✅ 칼럼 리스트 조회 API (좋아요 개수 + 사용자 좋아요 여부 포함)
    - 무한 스크롤 (커서 기반 페이징)
    - 필터링 (카테고리, 키워드 검색)
    - 정렬 지원 (기본 최신순)
    - 좋아요 개수 및 특정 사용자의 좋아요 여부 포함
    """

    # ✅ 기본 쿼리 (칼럼 정보 + 좋아요 개수 계산)
    query_stmt = select(
        DebateColumn.id,
        DebateColumn.category,
        DebateColumn.title,
        DebateColumn.subtitle,
        func.count(Likes.id).label("like_count"),  # ✅ 좋아요 개수 계산
        DebateColumn.created_at
    ).outerjoin(
        Likes, (Likes.debate_column_id == DebateColumn.id) & (Likes.is_deleted == False)
    ).where(DebateColumn.is_deleted == False)

    # ✅ 카테고리 필터 적용
    if category:
        query_stmt = query_stmt.where(DebateColumn.category == category)

    # ✅ 키워드 검색 적용
    if keyword:
        query_stmt = query_stmt.where(
            or_(
                DebateColumn.title.ilike(f"%{keyword}%"),
                DebateColumn.subtitle.ilike(f"%{keyword}%"),
                DebateColumn.category.ilike(f"%{keyword}%")
            )
        )

    # ✅ 커서 기반 페이징 (정렬 기준 필드 + id)
    if cursor:
        cursor_values = cursor.split(",")  # ex) ["2024-02-14T12:00:00", "100"]
        sort_column = getattr(DebateColumn, sort)
        id_column = DebateColumn.id

        query_stmt = query_stmt.where(
            or_(
                sort_column < cursor_values[0],  # 정렬 기준 필드 비교
                and_(sort_column == cursor_values[0], id_column < cursor_values[1])  # 동일한 값이면 ID 비교
            )
        )

    # ✅ 정렬 기준 적용 (기본 최신순, ID 추가 정렬)
    query_stmt = query_stmt.group_by(DebateColumn.id).order_by(desc(getattr(DebateColumn, sort)), desc(DebateColumn.id)).limit(limit)

    # 실행 및 결과 반환
    result = await db.execute(query_stmt)
    columns = result.mappings().all()

    # ✅ 특정 사용자가 좋아요를 눌렀는지 여부 추가 (user_id 있을 경우)
    liked_columns = {}
    if user_id:
        liked_stmt = select(Likes.debate_column_id).where(
            Likes.user_id == user_id,
            Likes.debate_column_id.in_([col["id"] for col in columns]),
            Likes.is_deleted == False
        )
        liked_result = await db.execute(liked_stmt)
        liked_columns = {row[0] for row in liked_result.fetchall()}  # ✅ 좋아요 누른 게시물 ID 리스트

    # 다음 커서 설정
    if columns:
        last_column = columns[-1]  # ✅ 마지막 칼럼 객체 가져오기
        next_cursor = f"{last_column[sort]},{last_column['id']}"
    else:
        next_cursor = None

    return {
        "columns": [
            {
                "id": col["id"],
                "category": col["category"],
                "title": col["title"],
                "subtitle": col["subtitle"],
                "like_count": col["like_count"],  # ✅ 좋아요 개수 포함
                "liked": col["id"] in liked_columns if user_id else None,  # ✅ 특정 사용자의 좋아요 여부
                "created_at": col["created_at"].date()
            } for col in columns
        ],
        "next_cursor": next_cursor,
        "has_next": len(columns) == limit
    }


async def get_column(db: AsyncSession, id: int):
    """
    특정 ID의 칼럼을 조회하는 함수

    :param db: 비동기 데이터베이스 세션
    :param id: 검색할 칼럼의 ID
    :return: 칼럼 객체 (없으면 None 반환)
    """
    try:
        result = await db.execute(select(DebateColumn).where(DebateColumn.id == id, DebateColumn.is_deleted == False))
        column = result.scalars().first()
        return column
    except Exception as e:
        await db.rollback()
        print(f"❌ 데이터 조회 오류: {e}")
        raise e
    
async def update_likes(
    db: AsyncSession,
    column_id: int,
    user_id: int,
    is_like: bool
) -> LikeResponse:
    """
    칼럼 좋아요/좋아요 취소 처리 함수 (트랜잭션 없음).

    Args:
        db (AsyncSession): 데이터베이스 세션
        column_id (int): 컬럼 ID
        user_id (int): 유저 ID
        is_like (bool): 좋아요 여부 (True: 좋아요, False: 좋아요 취소)

    Returns:
        LikeResponse: 성공 여부 및 메시지 반환
    """
    try:
        # 좋아요 상태 조회
        stmt = select(Likes).where(Likes.user_id == user_id, Likes.debate_column_id == column_id)
        result = await db.execute(stmt)
        like_entry = result.scalar_one_or_none()

        if is_like:
            if like_entry is None:
                # 좋아요 처음 누름 → DB 저장
                await db.execute(insert(Likes).values(user_id=user_id, debate_column_id=column_id, is_deleted=False))

            elif like_entry.is_deleted:
                # 기존 좋아요가 취소된 상태 → 다시 활성화
                await db.execute(update(Likes).where(
                    Likes.user_id == user_id, Likes.debate_column_id == column_id
                ).values(is_deleted=False))

        else:
            if like_entry and not like_entry.is_deleted:
                # 좋아요 활성화 상태에서 취소 처리
                await db.execute(update(Likes).where(
                    Likes.user_id == user_id, Likes.debate_column_id == column_id
                ).values(is_deleted=True))
        await db.commit()
        return LikeResponse(success=True, message="좋아요 상태가 변경되었습니다.")

    except SQLAlchemyError as e:
        await db.rollback()
        print(f"❌ 데이터베이스 오류: {e}")
        return LikeResponse(success=False, message="데이터베이스 오류 발생")
    
