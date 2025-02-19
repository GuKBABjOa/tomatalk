async def get_question(step: int) -> str:
    result = ""
    if(step == 1):
        result = "주제에 대한 당신의 입장은 무엇인가요?"
    elif(step == 2):
        result = "당신의 주장을 뒷바침하는 구체적인 사례나 근거를 들어 설명해주세요."
    elif(step == 3):
        result = "반대 입장에서 제기할 수 있는 주장은 무엇이며, 이에 대해 어떻게 생각하시나요?"
    elif(step == 4):
        result = "지금까지의 논의를 종합하여, 최종 입장을 말씀해주세요."
    else:
        result = "당신의 주장을 마음껏 펼쳐보세요."
    return result
