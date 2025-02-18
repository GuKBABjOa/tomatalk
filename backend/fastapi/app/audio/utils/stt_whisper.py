from fastapi import WebSocket
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import WebSocket, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from transcripts.repository.repository import save_statement
from ai.utils.summation import generate_summary
from transcripts.repository.repository import save_summation
from pydub import AudioSegment
import whisper
import numpy as np
import torch
import json

SAMPLE_RATE = 16000

# ✅ Whisper 모델 로드 (medium 이상 권장)
device = "cuda" if torch.cuda.is_available() else "cpu"
model = whisper.load_model("medium", device="cpu")

async def audio_transcription(websocket: WebSocket, db: AsyncSession):
    """
    오디오 데이터를 수신하고 Whisper 모델을 사용하여 텍스트로 변환하는 WebSocket 핸들러
    """
    await websocket.accept()
    print("✅ WebSocket 연결됨!")

    metadata = await websocket.receive_text()
    metadata = json.loads(metadata)
    debate_id = metadata.get("debate_id", "unknown")
    position = metadata.get("position", "unknown")
    user_id = metadata.get("user_id", "unknown")
    nickname = metadata.get("nickname", "unknown")
    round = metadata.get("round", 0)
    print(f"📌 방: {debate_id}, 주제: {position} / 발언자: {nickname} / 라운드: {round}")

    # ✅ WebSocket 세션별 개별 상태 변수 (전역 변수 제거)
    audio_buffer = []
    statement = ""

    try:
        while True:
            data = await websocket.receive_bytes()
            if not data:
                print("⚠ 수신된 데이터 없음.")
                break  

            # PCM 데이터를 NumPy 배열로 변환
            audio_chunk = np.frombuffer(data, dtype=np.int16)
            audio_buffer.append(audio_chunk)

            # 20초 분량의 오디오 데이터가 쌓이면 변환 실행
            if len(audio_buffer) * 4096 / 160000 > 20:
                text = await process_audio(audio_buffer, websocket)
                if text:
                    statement += text  # ✅ WebSocket 연결별 statement 유지
                audio_buffer = []
    except Exception as e:
        print(f"오디오 변환 오류: {e}")
    finally:
        print("🔴 WebSocket 연결이 끊어졌습니다. 변환된 발언 저장을 시도합니다...")
        await save_statement(db, debate_id, position, user_id, nickname, round, statement)  # ✅ 개별 statement 저장
        response = await generate_summary(statement)  # ✅ 개별 summary 저장
        await save_summation(db, debate_id, position, user_id, nickname, round, response)  # ✅ 개별 summary 저장
        await close_websocket_safely(websocket)


async def process_audio(audio_buffer, websocket):
    """
    오디오 버퍼를 처리하고 Whisper 모델을 사용하여 텍스트로 변환하는 함수
    """
    if not audio_buffer:
        return ""

    try:
        # PCM 데이터를 하나의 NumPy 배열로 변환
        audio_data = np.concatenate(audio_buffer, axis=0)
    except ValueError:
        print("버퍼 조합 오류: 데이터가 손상됨")
        return ""

    # ✅ NaN 및 Inf 값 제거
    audio_data = np.nan_to_num(audio_data, nan=0.0, posinf=1.0, neginf=-1.0)

    # ✅ 오버플로우 방지 (16-bit PCM 변환)
    audio_data = np.clip(audio_data, -32768, 32767).astype(np.int16)

    # ✅ 공백 데이터 필터링
    if np.abs(audio_data).mean() < 100:
        print("⚠ 잡음 또는 공백 데이터 감지, 변환 생략")
        return ""

    if len(audio_data) < SAMPLE_RATE:  # 1초 이하 데이터 무시
        return ""

    # ✅ PCM 데이터를 Whisper가 처리할 수 있는 `numpy` 배열로 변환
    whisper_input = pcm_to_numpy(audio_data.tobytes(), SAMPLE_RATE)

    # ✅ Whisper 모델로 변환 실행
    try:
        result = model.transcribe(
            whisper_input,
            language="ko",
            temperature=0.2,
            no_speech_threshold=0.3,
            fp16=False
        )
        text = result["text"].strip()
        if text:
            print(f"변환된 텍스트: {text}")
            await websocket.send_json({"transcription": text})
        return text
    except Exception as e:
        print(f"Whisper 변환 오류: {e}")
        return ""

# PCM 데이터를 Whisper가 처리할 수 있는 `numpy` 배열로 변환하는 함수
def pcm_to_numpy(pcm_data, sample_rate=16000):
    """
    PCM 데이터를 Whisper 모델에 직접 입력할 수 있는 `numpy` 배열로 변환
    """
    audio_segment = AudioSegment(
        data=pcm_data,
        sample_width=2,  # 16-bit PCM
        frame_rate=sample_rate,
        channels=1
    )

    samples = np.array(audio_segment.get_array_of_samples()).astype(np.float32) / 32768.0

    # Whisper가 처리할 수 있도록 고정 길이 유지 (10초)
    target_length = sample_rate * 10  # 10초
    if len(samples) < target_length:
        padded_samples = np.zeros(target_length, dtype=np.float32)
        padded_samples[:len(samples)] = samples
        samples = padded_samples
    else:
        samples = samples[:target_length]

    return whisper.pad_or_trim(samples)  # Whisper에 적절한 길이 맞춤

async def close_websocket_safely(websocket):
    """
    WebSocket이 종료될 때 안전하게 닫고 메시지를 출력하는 함수
    """
    try:
        if websocket.client_state in ["CONNECTED", "CONNECTING"]:
            print("✅ WebSocket 정상 종료 중...")
            await websocket.close(code=1000, reason="Normal Closure")
            print("✅ WebSocket이 정상적으로 종료되었습니다.")
        else:
            print("⚠ WebSocket이 이미 종료된 상태입니다.")
    except Exception as e:
        print(f"❌ WebSocket 닫기 오류 발생: {e}")
