from fastapi import APIRouter, File, HTTPException, APIRouter, Depends, BackgroundTasks
from typing import Union
from ai.utils.prompt import generate_chat_response
from ai.utils.ai_participant import receive_debate_request, DebateRequest
from database import AsyncSession, get_async_db
import re


router = APIRouter() 

@router.get("/chat")
async def judgement(text: str) -> Union[str, dict]:
    """
    gpt 입력을 받는 함수 

    Returns:
        Union[str, dict]: 변환된 텍스트 또는 오류 메시지
    """
    try:
        # generate_chat_response 함수 호출
        chat_response = generate_chat_response(text)

        # 결과 반환
        return chat_response
    except Exception as e:
        # 에러 발생 시 HTTPException 반환
        raise HTTPException(status_code=500, detail=f"에러 발생: {str(e)}")
    
@router.post("/participant")
async def receive_request(
    request_data: DebateRequest,
    background_tasks: BackgroundTasks,
    db: AsyncSession = Depends(get_async_db) # FastAPI에서 DB 세션 주입    
):
    """
    프론트에서 보낸 주제, 역할, debateId 데이터를 받아 처리
    """
    response = await receive_debate_request(request_data, background_tasks, db)
    return response
