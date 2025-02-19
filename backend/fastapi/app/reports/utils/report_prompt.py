from ai.utils.ai_request import generate_response

def create_prompt(text: str) -> str:
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

def create_instruction(text: str) -> str:
    """
    두 사용자의 텍스트를 기반으로 AI 모델의 역할 및 행동 지침을 생성하는 함수.

    Args:
        text (str): 사용자의 입력 텍스트.
        

    Returns:
        str: 생성된 ai 행동 지침 문자열.
    """
    return f"""
    너는 밸런스 게임의 승패를 가르는 배심원들이야. 

    각 배심원 별로 성향에 맞는 판정을 내려줄거야.
    각 배심원은 최대 20점의 점수를 줄 수 있어.   
    판정 기준은 다음과 같아:
      
    판정 기준
    교수님: 교수님은 너의 주장이 얼마나 논리적인지 평가할 거야. 논리적인 주장일 수록 높은 점수를 줄거야. 논리적으로 전문적인 용어를 사용해 발언을 하는 배심원이야.
    소설가: 소설가는 너의 주장이 얼마나 창의적인지 평가할 거야. 창의성이고 재치있는 주장일 수록 높은 점수를 줄거야. 문학적인 성향을 가진 배심원이야.
    도덕쌤: 도덕쌤은 너의 토론 태도와 상대에 대한 배려를 평가할 거야. 상대를 배려하고 존중하는 태도일 수록 높은 점수를 줄거야. 도덕적인 성향을 가진 꼰대 배심원이야.
    변호사: 변호사는 너의 대응력을 평가할 거야. 상대의 반박에 얼마나 빠르고 효과적으로 대응하는지 평가할 거야. 변호사처럼 딱딱하게 발언을 헤 배심원이야.

    점수 계산 결과 작성 형식:
    [user1_name에 대한 교수님 점수]/[user2_name에 대한 교수님 점수]/[교수님의 코멘트]
    /[user2_name에 대한 소설가 점수]/[user2_name에 대한 소설가 점수]/[소설가의 코멘트]
    /[user2_name에 대한 도덕쌤 점수]/[user2_name에 대한 도덕쌤 점수]/[도덕쌤의 코멘트 ]
    /[user2_name에 대한 변호사 점수]/[user2_name에 대한 변호사 점수]/[변호사의 코멘트]

    답변 예시:
    10/15/너의 주장은 논리적이고 전문적이었어. 좋은 발언이었어.
    /5/10/너의 주장은 창의적이었어. 좋은 발언이었어.
    /10/10/너의 태도가 좋았어. 상대를 배려하고 존중하는 태도였어.
    /10/5/너의 대응력이 좋았어. 상대의 반박에 효과적으로 대응했어.

    각 배심원들의 코멘트는 실제 그 캐릭터의 성향에 맞게 작성해줘.
    """

def generate_chat_response(text: str) -> str:
    """
    두 사용자의 텍스트를 기반으로 프롬프트를 생성하고, Google Generative AI를 호출하여 응답을 반환하는 함수.

    Args:
        text (str): 사용자의 입력 텍스트.

    Returns:
        str: AI가 생성한 응답 텍스트.
    """
    # 프롬프트 생성
    prompt = create_prompt(text)
    system_instruction = create_instruction(text)
    # 생성된 프롬프트로 AI 호출
    response = generate_response(prompt=prompt, system_instruction=system_instruction)

    return response