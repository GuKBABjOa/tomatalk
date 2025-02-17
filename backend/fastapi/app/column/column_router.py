from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from column.utils.column import create_cloumn

router = APIRouter()

@router.get("/")
async def get_column_list(
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    칼럼 리스트를 불러오는 API 엔드포인트(페이지네이션) 

    Args:
        current_round (int): 현재 라운드

    Returns:
    """
    return None

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
    debate_id: str, 
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    칼럼 좋아요를 누르는 엔드 포인트 

    Args:
        debate_id(int): 토론방 조회  

    Returns:
        None
    """
    await create_cloumn(debate_id, db)