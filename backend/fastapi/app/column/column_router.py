from fastapi import APIRouter, Depends, Query
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from column.utils.column import create_cloumn
from column.repository.repository import get_column, get_column_list, update_likes
from column.schemas.schemas import LikeRequest, LikeResponse, ColumnListRequest

router = APIRouter()

@router.get("/")
async def get_column_detail(
    id: int,
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    칼럼 상세 정보를 불러오는 API 엔드포인트 

    Args:
        current_round (int): 현재 라운드

    Returns:
    """
    return await get_column(db, id)
    

@router.post("/list")
async def get_column_list_api(
    request: ColumnListRequest,  # ✅ 요청 스키마 사용
    db: AsyncSession = Depends(get_async_db)
):
    return await get_column_list(
        db=db,
        user_id=request.user_id,
        category=request.category,
        keyword=request.keyword,
        cursor=request.cursor,
        limit=request.limit,
        sort=request.sort
    )


@router.post("/")
async def create_column(
    debate_id: str, 
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    칼럼을 생성하는 API 엔드포인트 

    Args:
        debate_id(int): 토론방 조회  

    Returns:
        None
    """
    await create_cloumn(debate_id, db)

@router.post("/like")
async def column_like(
    request: LikeRequest,
    db: AsyncSession = Depends(get_async_db),
    ) -> LikeResponse:
    """
    칼럼 좋아요 표시 check API  

    Args:
        debate_id(str): 토론방 아이디   
        user_id(int): user 아이디 
    Returns:
        
    """
    return await update_likes(db, request.column_id,request.user_id, True)

@router.post("/unlike")
async def column_unlike(
    request: LikeRequest,
    db: AsyncSession = Depends(get_async_db),
    ) -> LikeResponse:
    """
    칼럼 좋아요 해제제 API  

    Args:
        debate_id(str): 토론방 아이디   
        user_id(int): user 아이디 
    Returns:
        
    """
    return await update_likes(db, request.column_id,request.user_id, False)

    