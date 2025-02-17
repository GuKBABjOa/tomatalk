from fastapi import APIRouter, WebSocket,Depends
from audio.utils.stt_whisper import audio_transcription
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db

router = APIRouter()

# ✅ WebSocket STT API 엔드포인트
@router.websocket("/ws/stt")
async def websocket_endpoint(websocket: WebSocket, db: AsyncSession = Depends(get_async_db)):
    """
    웹 소켓 연결을 통해 오디오 데이터를 실시간으로 텍스트로 변환하는 API 엔드포인트

    Args:
        websocket (WebSocket): FastAPI WebSocket 인스턴스
        db (AsyncSession): 데이터베이스 연결 객체

    Returns:
        웹 소켓을 통해 변환된 텍스트 데이터를 전송
    """
    await audio_transcription(websocket, db)

# ✅ WebSocket STT API 엔드포인트
@router.get("/ai_bot")
async def websocket_endpoint(websocket: WebSocket, db: AsyncSession = Depends(get_async_db)):
    """
    웹 소켓 연결을 통해 오디오 데이터를 실시간으로 텍스트로 변환하는 API 엔드포인트

    Args:
        websocket (WebSocket): FastAPI WebSocket 인스턴스
        db (AsyncSession): 데이터베이스 연결 객체

    Returns:
        웹 소켓을 통해 변환된 텍스트 데이터를 전송
    """
    await audio_transcription(websocket, db)


