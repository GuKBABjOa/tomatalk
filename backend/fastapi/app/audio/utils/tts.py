import os
import uuid
import edge_tts # Microsoft Edge 기반 tts 라이브러리리
from dotenv import load_dotenv
import re

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