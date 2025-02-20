from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from practice.schemas.schemas import PracticeRequest, GetPracticeRequest, BasicPracticeRequest, BasicPracticeResponse
from practice.utils.pratice import practice_check, search_practice, get_basic_practice_result, post_practice_progress
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

@router.post("/progress")
async def post_practice(
    user_id: int, 
    problem_id: int, 
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    문제 풀이 진행 결과를 저장 
    """
    return await post_practice_progress(
        db = db,
        user_id= user_id,
        problem_id = problem_id)