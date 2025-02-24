from fastapi import APIRouter, File, HTTPException
from typing import Union
from ai.utils.prompt import generate_chat_response
from ai.utils.utils import basic_ai_bot
import re
from ai.utils.bad_word_check import predict_bad_word # 욕설 예측 함수


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
    
@router.get("/basic/bot")
async def get_basic_ai_bot_response() -> Union[str, dict]:
    """
    실전 연습 AI 응답을 받는 API 요청

    Returns:
        Union[str, dict]: 변환된 텍스트 또는 오류 메시지
    """
    return basic_ai_bot()
