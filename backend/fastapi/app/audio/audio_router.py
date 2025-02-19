from fastapi import APIRouter, WebSocket,Depends , UploadFile, File, HTTPException,  Form
from audio.utils.stt_whisper import audio_transcription, audio_transcription_practice, stt_file
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from audio.schemas.schemas import BasicSTTRequest
import logging

router = APIRouter()
logger = logging.getLogger(__name__)

# ✅ WebSocket STT API 엔드포인트
@router.websocket("/stt")
async def websocket_endpoint(websocket: WebSocket, db: AsyncSession = Depends(get_async_db)):
    """
    웹 소켓 연결을 통해 오디오 데이터를 실시간으로 텍스트로 변환하는 API 엔드포인트(토론론)

    Args:
        websocket (WebSocket): FastAPI WebSocket 인스턴스
        db (AsyncSession): 데이터베이스 연결 객체

    Returns:
        웹 소켓을 통해 변환된 텍스트 데이터를 전송
    """
    await audio_transcription(websocket, db)

# ✅ 기초 연습 함수 
@router.websocket("/basic")
async def websocket_endpoint2(websocket: WebSocket, db: AsyncSession = Depends(get_async_db)):
    """
    웹 소켓 연결을 통해 오디오 데이터를 실시간으로 텍스트로 변환하는 API 엔드포인트(기초연습습)

    Args:
        websocket (WebSocket): FastAPI WebSocket 인스턴스
        db (AsyncSession): 데이터베이스 연결 객체

    Returns:
        웹 소켓을 통해 변환된 텍스트 데이터를 전송
    """
    query_params = websocket.query_params
    topic_id = int(query_params.get("topic_id"))
    stance = query_params.get("stance")
    step = int(query_params.get("step"))
    user_id = int(query_params.get("user_id"))

    logger.info(f"WebSocket 연결: topic_id={topic_id}, stance={stance},step={step}, user_id={user_id}")
    await audio_transcription_practice(
        websocket= websocket,
        db = db,
        topic_id=topic_id,
        stance= stance,
        step= step,
        user_id= user_id)


# # ✅ 기초 연습 함수 
@router.post("/basic/post")
async def get_basic_practice_audio(
    file: UploadFile = File(...),
    topic: str = Form(...),
    position: str = Form(...),
    question: str = Form(...),
    user_id: int = Form(...),
    db: AsyncSession = Depends(get_async_db)
):
    """
    WAV 음성 파일을 받아 Whisper STT 변환을 실행 후 저장하는 API.

    Args:
        file (UploadFile): 업로드된 WAV 파일
        topic (str): 토론 주제
        position (str): 찬성/반대 여부
        question (str): 질문 내용
        user_id (int): 사용자 ID
        db (AsyncSession): 데이터베이스 연결 객체

    Returns:
        변환된 텍스트 데이터 반환
    """
    return await stt_file(file, topic, position, question, user_id, db)


