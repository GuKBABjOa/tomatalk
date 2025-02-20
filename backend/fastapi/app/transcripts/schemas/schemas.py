from typing import Optional
from pydantic import BaseModel

class SummaryRequest(BaseModel):
    devate_id: str
    user_id: Optional[int] = None  # 선택적 필드 (기본값 None)
    current_round: Optional[int] = None  # 선택적 필드 (기본값 None)