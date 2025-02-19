from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from reports.utils.ai_report import evaluate_statement

router = APIRouter()

@router.get("/")
async def get_summary(
    debate_id: str,
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
    return await evaluate_statement(db = db, debate_id = debate_id)
