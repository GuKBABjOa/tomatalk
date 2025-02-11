import speech_recognition as sr
import asyncio

async def convert_audio_to_text(audio_file_path: str) -> str:
    """
    WAV 파일을 입력으로 받으면 텍스트로 변환한다 (비동기 방식).

    Args:
        audio_file_path (str): WAV 파일의 경로 

    Returns:
        str: 오디오 파일에서 추출한 텍스트 데이터 
    """
    recognizer = sr.Recognizer()

    def process_audio():
        with sr.AudioFile(audio_file_path) as source:
            print(f"음성 파일 {audio_file_path} 로드 중...")
            recognizer.adjust_for_ambient_noise(source)  # 파일의 주변 소음 보정
            return recognizer.record(source)  # 전체 파일 읽기

    # 비동기 실행을 위한 이벤트 루프 가져오기
    loop = asyncio.get_running_loop()
    audio = await loop.run_in_executor(None, process_audio)  # ✅ `await` 추가

    # Google Web Speech API로 음성 인식
    try:
        print(f"음성 인식 중 ({audio_file_path})...")
        text = recognizer.recognize_google(audio, language="ko-KR")  # ✅ `await` 사용한 객체
        print(f"인식된 텍스트 ({audio_file_path}): {text}")
        return text
    except sr.UnknownValueError:
        return "음성을 이해할 수 없습니다."
    except sr.RequestError as e:
        return f"Google Web Speech API 요청 실패: {e}"

