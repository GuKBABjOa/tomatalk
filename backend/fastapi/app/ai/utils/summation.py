from ai.utils.ai_request import generate_response
from sqlalchemy.ext.asyncio import AsyncSession
from transcripts.repository.repository import save_summation

async def create_prompt(text: str) -> str:
    """
    두 사용자의 텍스트를 기반으로 프롬프트를 생성하는 함수.

    Args:
        text (str): 사용자의 입력 텍스트.

    Returns:
        str: 생성된 프롬프트 문자열.
    """
    return f"""
    {text}
    """

async def create_instruction() -> str:
    """
    두 사용자의 텍스트를 기반으로 AI 모델의 역할 및 행동 지침을 생성하는 함수.

    Args:
        text (str): 사용자의 입력 텍스트.
        

    Returns:
        str: 생성된 ai 행동 지침 문자열.
    """
    return f"""
    너는 다음 발언을 요약해줘. 
    """

async def generate_summary(text: str):
    """
    두 사용자의 텍스트를 기반으로 프롬프트를 생성하고, Google Generative AI를 호출하여 응답을 반환하는 함수.

    Args:
        text (str): 사용자의 입력 텍스트.

    Returns:
        str: AI가 생성한 응답 텍스트.
    """
    # 프롬프트 생성
    prompt = await create_prompt(text)
    system_instruction = await create_instruction()
    # 생성된 프롬프트로 AI 호출
    response = await generate_response(prompt=prompt, system_instruction=system_instruction)
    print("✅ 요약 저장 완료!")
    return response