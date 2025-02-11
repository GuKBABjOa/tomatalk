# from pydub import AudioSegment
# import whisper
# import numpy as np
# import os
# import io
# import torch

# audio_buffer = []
# SAMPLE_RATE = 16000

# # ✅ Whisper 모델 로드 (medium 이상 권장)
# device = "cuda" if torch.cuda.is_available() else "cpu"
# model = whisper.load_model("small", device=device)

# async def audio_transcription(websocket):
#      """
#         오디오 데이터를 수신하고 Whisper 모델을 사용하여 텍스트로 변환하는 함수

#         Args:
#             websocket (WebSocket): FastAPI WebSocket 연결 객체

#         Returns:    
#             None
#      """
#      global audio_buffer
#      while True:
#             data = await websocket.receive_bytes()

#             if not data:
#                 print("⚠ 수신된 데이터 없음.")
#                 break  

#             # PCM 데이터를 Numpy 배열로 변환
#             audio_chunk = np.frombuffer(data, dtype=np.int16)
#             audio_buffer.append(audio_chunk)

#             # 5초 분량의 오디오 데이터가 쌓이면 변환 실행
#             if len(audio_buffer) * 4096 / 160000 > 25:
#                 await process_audio(websocket)
#                 audio_buffer = []

# async def process_audio(websocket):
#     """"
#         오디오 버퍼를 처리하고 Whisper 모델을 사용하여 텍스트로 변환하는 함수

#         Args:   
#             websocket (WebSocket): FastAPI WebSocket 연결 객체

#         Returns:
#             None
#     """
#     global audio_buffer 

#     if not audio_buffer:
#         return

#     try:
#         # PCM 데이터를 하나의 NumPy 배열로 변환
#         audio_data = np.concatenate(audio_buffer, axis=0)
#     except ValueError:
#         print("버퍼 조합 오류: 데이터가 손상됨")
#         audio_buffer = []  # 초기화 후 종료
#         return

#     # ✅ NaN 및 Inf 값 제거
#     audio_data = np.nan_to_num(audio_data, nan=0.0, posinf=1.0, neginf=-1.0)

#     # ✅ 오버플로우 방지 (16-bit PCM 변환)
#     audio_data = np.clip(audio_data, -32768, 32767).astype(np.int16)

#     # ✅ 공백 데이터 필터링
#     if np.abs(audio_data).mean() < 100:
#         print("⚠ 잡음 또는 공백 데이터 감지, 변환 생략")
#         audio_buffer = []  # 버퍼 초기화 후 종료
#         return

#     # ✅ PCM 데이터를 Whisper가 처리할 수 있는 `numpy` 배열로 변환
#     whisper_input = pcm_to_numpy(audio_data.tobytes(), SAMPLE_RATE)
    
#     # ✅ Whisper 모델로 변환 실행
#     try:
#         result = model.transcribe(whisper_input, language="ko", no_speech_threshold=0.4)
#         text = result["text"].strip()

#         if text:
#             print(f"변환된 텍스트: {text}")
#             await websocket.send_json({"transcription": text})
#     except Exception as e:
#         print(f"Whisper 변환 오류: {e}")

#     # ✅ 오디오 버퍼 초기화
#     audio_buffer = []

# # PCM 데이터를 Whisper가 처리할 수 있는 `numpy` 배열로 변환하는 함수
# def pcm_to_numpy(pcm_data, sample_rate=16000):
#     """
#     PCM 데이터를 Whisper 모델에 직접 입력할 수 있는 `numpy` 배열로 변환

#     Args:
#         pcm_data (bytes): PCM 오디오 데이터
#         sample_rate (int): 샘플링 레이트 (기본 16000Hz)

#     Returns:
#         numpy.ndarray: Whisper 모델이 직접 처리할 수 있는 오디오 데이터
#     """
#     # PCM 데이터를 WAV 형식으로 변환
#     audio_segment = AudioSegment(
#         data=pcm_data,
#         sample_width=2,  # 16-bit PCM
#         frame_rate=sample_rate,
#         channels=1
#     )

#     # PCM 데이터를 float32로 변환
#     samples = np.array(audio_segment.get_array_of_samples()).astype(np.float32) / 32768.0

#     # Whisper가 처리할 수 있도록 고정 길이 유지 (10초)
#     target_length = sample_rate * 10  # 10초
#     if len(samples) < target_length:
#         # 부족한 부분은 0으로 채우기 (패딩)
#         padded_samples = np.zeros(target_length, dtype=np.float32)
#         padded_samples[:len(samples)] = samples  # 기존 데이터 유지
#         samples = padded_samples
#     else:
#         # 길면 자르기
#         samples = samples[:target_length]

#     return whisper.pad_or_trim(samples)  # Whisper에 적절한 길이 맞춤
