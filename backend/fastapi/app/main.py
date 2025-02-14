from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from audio import audio_router
from ai import ai_router
from transcripts import transcripts_router
from dotenv import load_dotenv
import os



app = FastAPI()

# 세션 미들웨어 추가 (비밀키는 실제 환경에서 안전하게 관리)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],  # 허용할 HTTP 메서드
    allow_headers=["*"],  # 허용할 HTTPS 헤더
)

app.include_router(ai_router.router, prefix="/api/ai", tags=["ai"])
app.include_router(audio_router.router, prefix="/api/audio", tags=["audio"])
app.include_router(transcripts_router.router, prefix="/api/transcripts", tags=["transcripts"])

# 환경 변수 로드
load_dotenv()

USER_NAME = os.getenv("USER_NAME")
PASSWORD = os.getenv("PASSWORD")
HOST_NAME = os.getenv("HOST_NAME", "localhost")
PORT = int(os.getenv("PORT", 3306))
DB_NAME = os.getenv("DB_NAME")

@app.get("/")
async def root():
    return {"message": "Welcome to FastAPI"}
