import openai
import os
from dotenv import load_dotenv

# 환경 변수 로드
load_dotenv()
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

if not OPENAI_API_KEY:
    raise Exception("OPENAI_API_KEY 환경 변수가 설정되지 않았습니다.")

# ✅ 최신 버전의 OpenAI SDK 초기화
client = openai.OpenAI(api_key=OPENAI_API_KEY)

async def generate_response(prompt: str, system_instruction: str, temperature: float = 0.7):

    """
    OpenAI GPT-4 모델에 프롬프트를 전달하여 응답을 생성하는 함수.

    Args:
        prompt (str): 사용자 입력 프롬프트.
        system_instruction (str): AI 모델에 대한 역할 및 행동 지침.
        temperature (float): 응답 다양성을 조정하는 값 (기본값: 0.7).

    Returns:
        str: 생성된 텍스트 응답.

    Raises:
        Exception: API 호출 중 발생한 오류.
    """
    try:
        # OpenAI API 요청
        response = client.chat.completions.create(
            model="gpt-4o",  # 모델 설정 (gpt-4o 또는 gpt-4 사용 가능)
            messages=[
                {"role": "system", "content": system_instruction},  # 시스템 역할 부여
                {"role": "user", "content": prompt}  # 사용자 입력
            ],
            temperature=temperature,  # 창의성 조정
            max_tokens=2048,  # 응답 최대 길이 설정
            n=1,  # 응답 개수
            stop=None  # 멈출 기준 없음
        )

        # 응답 처리
        if response.choices and response.choices[0].message:
            print(response.choices[0].message.content)
            return response.choices[0].message.content
        else:
            raise ValueError("유효한 응답을 찾을 수 없습니다.")

    except Exception as e:
        raise Exception(f"응답 생성 중 오류가 발생했습니다: {str(e)}")
