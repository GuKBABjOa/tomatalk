from fastapi import APIRouter, WebSocket
from audio.utils.stt_whisper import audio_transcription

router = APIRouter()

@router.get("/summary")
async def websocket_endpoint(devate_id: str):
    """
    토론 발언 요약 정보를 저장하는 API 엔드포인트

    Args:
        devate_id (str): 토론 ID

    Returns:
        None
    """
    return {"message": "Welcome to FastAPI"}