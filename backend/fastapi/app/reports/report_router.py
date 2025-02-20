from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from reports.utils.ai_report import evaluate_statement
from reports.schemas.schemas import ReportRequest
from reports.repository.repository import get_report, get_report_avg

router = APIRouter()

@router.post("/create")
async def create_report(
    debate_id: str,
    user_id: int,
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    토론 레포트를 생성하는 API 엔드포인트

    Args:
        devate_id (str): 토론 ID
        user_id (int): 사용자 ID
        current_round (int): 현재 라운드

    Returns:
        summary (str): 요약 정보
    """
    return await evaluate_statement(
        db = db,
        debate_id = debate_id,
        user_id = user_id)


@router.post("/get")
async def get_report(
    request: ReportRequest,
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    토론 레포트를 조회하는  API 엔드포인트

    Args:
        devate_id (str): 토론 ID
        user_id (int): 사용자 ID
        current_round (int): 현재 라운드

    Returns:
        summary (str): 요약 정보
    """
   
    return await get_report(
        db = db, 
        debate_id = request.debate_id,
        user_id= request.user_id)

@router.post("/get/avg")
async def get_report_avg(
    user_id: int,
    db: AsyncSession = Depends(get_async_db),
    ):
    """
    점수 평균을 조회하는  API 엔드포인트

    Args:
        user_id (int): 사용자 ID
        current_round (int): 현재 라운드

    Returns:
    """
   
    return await get_report_avg(
        db = db, 
        user_id= user_id)