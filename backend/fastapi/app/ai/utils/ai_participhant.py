import openai
import os
import uuid
import edge_tts # Microsoft Edge 기반 tts 라이브러리리
from dotenv import load_dotenv
from pydantic import BaseModel
from ai.utils.summation import generate_summary
from transcripts.repository.repository import get_summation, save_statement, save_summation
from database import AsyncSession
from typing import List
from fastapi import BackgroundTasks
from fastapi.responses import FileResponse
import re

class DebateRequest(BaseModel):
    topic: str
    role: str
    round: int
    debateId: str

async def receive_debate_request(
        request_data: DebateRequest,
        background_tasks: BackgroundTasks, # 백그라운드에서 삭제 작업 실행
        db: AsyncSession
    ):
    """
    AI 참여자의 순서가 왔을 때, 토론의 주제, AI 참여자의 역할, 토론의 ID값을 받고
    이전 발언 요약 내용을 조회한 후 이 정보들을 기반으로 AI 답변을 생성
    이후 답변을 전처리, TTS로 만든 mp3 파일 반환 작업
    """
    try:
        # 기존 발언 요약 조회
        previous_summaries: List[str] = await get_summation(db, request_data.debateId)
        
        # AI 참여자의 주장 생성
        ai_argument = await generate_ai_argument(
            request_data.topic, request_data.role, previous_summaries
        )

        # TTS에 friendly하게 전처리 적용
        processed_ai_argument = preprocess_text_for_tts(ai_argument)

        # AI 참여자의 답변 DB(statement 테이블)에 저장, user_id는 일단 0으로 저장
        await save_statement(
            db, request_data.debateId, request_data.role, 0, "AI참여자", request_data.round, processed_ai_argument
        )

        # AI 참여자의 답변을 요약으로 만들기
        ai_summary = await generate_summary(processed_ai_argument)
        await save_summation(
            db, request_data.debateId, request_data.role, 0, "AI참여자", request_data.round, ai_summary
        )

        # AI 응답을 TTS 변환
        audio_path = await text_to_speach(processed_ai_argument)

        # 파일 반환 후 자동 삭제
        background_tasks.add_task(delete_file, audio_path)

        return FileResponse(audio_path, media_type="audio/mpeg", filename="response.mp3")
    
    except Exception as e:
        print(f'AI 참여자 응답 생성 오류: {e}')
        raise e

# 환경 변수 로드
load_dotenv()
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

if not OPENAI_API_KEY:
    raise Exception("OPENAI_API_KEY 환경 변수가 설정되지 않았습니다.")

# 최신 버전의 OpenAI SDK 초기화
client = openai.OpenAI(api_key=OPENAI_API_KEY)

async def generate_ai_argument(topic: str, role: str, previous_summaries: List[str]) -> str:
    """
    OpenAI API를 사용하여 AI 참여자의 역할(찬성/반대)에 맞는 주장을 생성하는 함수
    
    :param topic: 토론 주제
    :param role: AI 참여자의 역할 (찬성 / 반대)
    :param previous_summaries: 이전 요약 내용 리스트
    :return: AI가 생성한 주장
    """
    prompt = f"""
    토론 주제: {topic}
    현재 토론 요약 내용:
    {chr(10).join(previous_summaries)}

    당신은 '{role}' 입장을 가진 AI 토론 참여자입니다.
    위 내용을 참고하여 {role} 입장에서 논리적이고 설득력 있는 주장을 생성하세요.
    """

    try:
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[{"role": "system", "content": "당신은 논리적인 AI 토론 참여자입니다."},
                      {"role": "user", "content": prompt}]
        )

        ai_response = response.choices[0].message.content.strip()
        return ai_response
    except Exception as e:
        print(f"OpenAI API 요청 오류: {e}")
        return "AI 주장을 생성하는 데 실패했습니다."


def preprocess_text_for_tts(text: str) -> str:
    """
    TTS 변환 전에 텍스트를 전처리하여 음성 합성에 적합한 형태로 변환하는 함수
    
    :param text: 원본 텍스트
    :return: TTS-friendly 텍스트
    """
    # 1. 개행 문자 (`\n`)를 자연스러운 쉼표 또는 마침표로 변경
    text = text.replace("\n\n", ". ")  # 문단 나눔을 마침표로 변경
    text = text.replace("\n", ", ")  # 일반 개행은 쉼표로 변환

    # 2. 특수 문자 제거 (필요한 경우)
    text = re.sub(r'[\"*#_()\[\]{}<>]', '', text)  # 큰따옴표, 특수 문자 제거

    # 3. 너무 긴 문장을 잘라서 자연스럽게 조정
    sentences = re.split(r'(?<=[.!?])\s+', text)  # 문장을 마침표, 물음표 기준으로 분할
    processed_sentences = []
    
    for sentence in sentences:
        if len(sentence) > 200:  # 문장이 너무 길면 쉼표 기준으로 한번 더 나누기
            sub_sentences = sentence.split(", ")
            for sub in sub_sentences:
                if len(sub) > 150:  # 여전히 길다면 일부 생략
                    sub = sub[:150] + "..."
                processed_sentences.append(sub)
        else:
            processed_sentences.append(sentence)

    return " ".join(processed_sentences)  # 문장들을 다시 합치기

# 음성 파일 저장 경로
OUTPUT_DIR = "ai/output/"

def delete_file(file_path: str):
    """파일을 반환한 후 삭제하는 함수"""
    try:
        if os.path.exists(file_path):
            os.remove(file_path)
            print(f"파일 삭제 완료: {file_path}")
    except Exception as e:
        print(f"파일 삭제 오류: {e}")

async def text_to_speach(text: str) -> str:
    """
    TTS를 사용하여 텍스트를 음성 파일로 변환하는 함수.

    :param text: 변환할 텍스트
    :return: 생성된 MP3 파일 경로
    """
    os.makedirs(OUTPUT_DIR, exist_ok=True) # 저장 폴더 없다면 생성

    file_name = f'{uuid.uuid4()}.mp3' # 고유한 파일명 생성
    file_path = os.path.join(OUTPUT_DIR, file_name)

    # TTS 음성 설정
    tts = edge_tts.Communicate(text, "ko-KR-SunHiNeural")
    await tts.save(file_path)

    return file_path