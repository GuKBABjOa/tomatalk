from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from transcripts.repository.repository import get_summation
from transcripts.schemas.schemas import SummaryRequest
router = APIRouter()

@router.post("/")
async def get_summary(
    request: SummaryRequest,
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    토론 발언 요약 정보를 반환하는 API 엔드포인트

    Args:
        devate_id (str): 토론 ID
        user_id (int): 사용자 ID
        current_round (int): 현재 라운드

    Returns:
        summary (str): 요약 정보
    """
    return await get_summation(db, request.devate_id, request.user_id, request.current_round)
