from pydantic import BaseModel
from typing import Dict, Any, Optional

problems = {
    "1-1": {
        "input_schema": [ "topic1","point1", "reason1", "example1", "point_restatement1",
                         "topic2","point2", "reason2", "example2", "point_restatement2",
                         "topic3","point3", "reason3", "example3", "point_restatement3",
                         "topic4","point4", "reason4", "example4", "point_restatement4"],
        "output_schema": [
            "point_score",
            "point_comment",
            "reason_score",
            "reason_comment",
            "example_score",
            "example_comment",
            "point_restatement_point",
            "point_restatement_comment",
            "total",
        ]
    },
    "2-1": {
        "input_schema": [ "topic1","point1", "reason1", "example1", "point_restatement1",
                         "topic2","point2", "reason2", "example2", "point_restatement2",
                         "topic3","point3", "reason3", "example3", "point_restatement3",
                         "topic4","point4", "reason4", "example4", "point_restatement4"],
        "output_schema": [
            "point_score",
            "point_comment",
            "reason_score",
            "reason_comment",
            "example_score",
            "example_comment",
            "point_restatement_point",
            "point_restatement_comment",
            "total",
        ]
    }
}


class PracticeRequest(BaseModel):
    problem_id: str
    user_id: int
    inputs: Dict[str, Any]

class GetPracticeRequest(BaseModel):
    problem_id: str
    user_id: int
    

# 응답 모델 (입력이 출력에 포함될 수도 있고 아닐 수도 있음)
class PracticeResponse(BaseModel):
    problem_id: str  # 문제 ID
    topic: str  # 문제 주제
    inputs: Optional[Dict[str, Any]] = None  # ✅ 응답에서 inputs 포함 여부 결정 가능
    outputs: Dict[str, Any]  # 문제별 출력 값 (점수, 코멘트 등)
    
# 기초 연습 요청 schema 
class BasicPracticeRequest(BaseModel):
    topic: str
    user_id: int

# 기초 연습 응답 schema 
class BasicPracticeResponse(BaseModel):
    strength: str 
    improvement: str
    strategy: str 
