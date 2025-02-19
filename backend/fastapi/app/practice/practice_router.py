from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from practice.schemas.schemas import PracticeRequest, GetPracticeRequest, BasicPracticeRequest, BasicPracticeResponse
from practice.utils.pratice import practice_check, search_practice, get_basic_practice_result
from database import get_async_db

router = APIRouter()

@router.post("/")
async def post_practice(
    request: PracticeRequest,
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    연습 문제 채점 결과를 반환하는 API 엔드포인트

    Args:


    Returns:
        summary (str): 요약 정보
    """
    return await practice_check(db = db, request = request)

@router.post("/basic")
async def get_practice(
    request: BasicPracticeRequest,
    db: AsyncSession = Depends(get_async_db),
    ) -> BasicPracticeResponse:
    """
    연습 문제 채점 결과를 조회하는 API 엔드포인트

    Args:

    Returns:
        summary (str): 요약 정보
    """
    return await get_basic_practice_result(
        db = db,
        topic = request.topic,
        user_id= request.user_id)
