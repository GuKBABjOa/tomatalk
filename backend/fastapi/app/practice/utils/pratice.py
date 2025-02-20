from ai.utils.practice import generate_chat_response
from ai.utils import basic_practice
from sqlalchemy.ext.asyncio import AsyncSession
from practice.schemas.schemas import PracticeResponse, PracticeRequest, problems, BasicPracticeResponse
from practice.repository.repository import save_practice, get_practice
from transcripts.repository.repository import get_basic_practice_statements

async def practice_check(
        db: AsyncSession,
        request: PracticeRequest 
    ):
    """
    연습하기 결과를 확인하는 함수

    args: 

    returns:

    """
    
    try:
        problem = problems.get(request.problem_id)
  
        if not problem:
            return {"error" : "해당 문제 ID가 존재하지 않습니다."}
        
        required_inputs = problem["input_schema"]
        missing_inputs = [key for key in required_inputs if key not in request.inputs]

        if missing_inputs:
            return {"error": f"누락된 입력 값: {missing_inputs}"}
        
        # 입력 태그와 값 합쳐서 하나의 문자열로 변환
        combined_string = "\n".join([f"{key}: {request.inputs[key]}" for key in required_inputs])
        combined_input = " / ".join([request.inputs[key] for key in required_inputs])
        
        required_outputs = problem["output_schema"]
        temp_response =  await generate_chat_response(request.problem_id, combined_string)
        response = temp_response.split("/")
        output_data = {required_outputs[i]: response[i] for i in range(len(required_outputs))}
        await save_practice(
            db = db,
            problem_id=request.problem_id,
            user_id= request.user_id,
            input= combined_input,
            output= temp_response
        )
        return output_data
    except Exception as e:
        print(f"{e}: 오류 발생")

async def search_practice(
        db: AsyncSession,
        problem_id: str,
        user_id: int,
        ):
    response = get_practice(
        db = db,
        problem_id= problem_id,
        user_id=user_id)
    print(response)
    return None

async def get_basic_practice_result(
        db: AsyncSession,
        topic: str,
        user_id: int
        ) -> BasicPracticeResponse:
    """
    기초 실습 결과를 불러와 응답 Schema에 매핑하는 함수 
    """
    try: 
        # 🔹 기초 연습 불러오기
        temp_response = await get_basic_practice_statements(
            db=db,
            topic=topic,
            user_id=user_id
        )
        
        # 🔹 AI 피드백 생성
        response = await basic_practice.generate_chat_response(text=temp_response)

        # 🔹 "/" 기준으로 분할
        parts = response.split("/")
        
        # 🔹 분할된 값이 부족하면 기본값 설정
        strength = parts[0].strip() if len(parts) > 0 else "No strength provided."
        improvement = parts[1].strip() if len(parts) > 1 else "No improvement provided."
        strategy = parts[2].strip() if len(parts) > 2 else "No strategy provided."

        # 🔹 결과 객체 생성 및 반환
        return BasicPracticeResponse(
            strength=strength,
            improvement=improvement,
            strategy=strategy
        )
    except Exception as e:
        print(f"{e}: 오류 발생")
        return BasicPracticeResponse(
            strength="Error occurred while processing response.",
            improvement="Error occurred while processing response.",
            strategy="Error occurred while processing response."
        )
    

async def post_practice_progress(
        db: AsyncSession,
        user_id: int,
        problem_id: int
        ):
    """
    문제풀이 결과를 저장 
    """
    return None