async def get_basic_topic(id:int) ->str:
    result = ""
    if(id == 1):
        result = "교복 자율화, 찬성? 반대?"
    elif(id == 2):
        result = "급식 메뉴 선택제 도입, 어떻게 생각하시나요?"
    else:
        result = "본인의 생각을 자유롭게 얘기해보세요"
    return result
