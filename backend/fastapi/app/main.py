from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from audio import audio_router
from ai import ai_router
from transcripts import transcripts_router
from reports import report_router
from practice import practice_router
import ssl
import uvicorn
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

app.include_router(ai_router.router, prefix="python/api/ai", tags=["ai"])
app.include_router(audio_router.router, prefix="python/api/audio", tags=["audio"])
app.include_router(transcripts_router.router, prefix="python/api/transcripts", tags=["transcripts"])
app.include_router(practice_router.router, prefix="python/api/practice", tags=["practice"])
app.include_router(report_router.router, prefix="python/api/report", tags=["report"])



@app.get("/")
async def root():
    return {"message": "Welcome to FastAPI"}
