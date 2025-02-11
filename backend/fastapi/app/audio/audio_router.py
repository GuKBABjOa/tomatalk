from fastapi import APIRouter, UploadFile, File, HTTPException, WebSocket, Header
import os
from tempfile import NamedTemporaryFile
from typing import Union
from audio.services.audio_service import process_audio_file  # 비즈니스 로직 호출
# from utils.stt.stt_vosk import vosk_wav_to_text  # vosk 라이브러리 사용
# from audio.utils.stt_whisper import audio_transcription

router = APIRouter()

@router.post("/stt")
async def speech_to_text(
    file1: UploadFile = File(...),
    name: str = Header(None)
):
    """
    WAV 파일을 받아서 STT 변환 후 텍스트 반환 API

    Args:
        file1 (UploadFile): 업로드된 WAV 파일

    Returns:
        dict: 변환된 텍스트 결과
    """
    if not file1.filename.endswith(".wav"):
        raise HTTPException(status_code=400, detail="WAV 파일 형식만 입력 가능합니다.")

    try:
        file_data = await file1.read()  # ✅ 비동기적으로 파일 읽기
        result = await process_audio_file(file_data)  # ✅ 서비스 로직 호출
        return result

    except RuntimeError as e:
        raise HTTPException(status_code=500, detail=str(e))

# @router.websocket("/ws/stt")
# async def websocket_endpoint(websocket: WebSocket):
#     """
#     웹 소켓 연결을 통해 오디오 데이터를 실시간으로 텍스트로 변환하는 API 엔드포인트

#     Args:
#         websocket (WebSocket): FastAPI WebSocket 인스턴스

#     Returns:
#         웹 소켓을 통해 변환된 텍스트 데이터를 전송
#     """
#     await websocket.accept()
#     print("✅ WebSocket 연결됨!")

#     try:
#        await audio_transcription(websocket)

#     except Exception as e:
#         print(f"❌ 오류 발생: {e}")

#     finally:
#         await websocket.close()
#         print("✅ WebSocket 연결 종료됨")


