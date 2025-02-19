from fastapi import APIRouter, File, HTTPException
from typing import Union
from sqlalchemy.ext.asyncio import AsyncSession
from analytics.repository.repository import get_speechrate


router = APIRouter() 

@router.get("/speed")
async def speechrate(
    db: AsyncSession,
    user_id:int):
    """
    사용자의 발화 속도를 불러오는 API(평균값)

    args:
        발화자의 id 

    Returns:

    """
    # 결과 반환
    return get_speechrate(db, user_id)