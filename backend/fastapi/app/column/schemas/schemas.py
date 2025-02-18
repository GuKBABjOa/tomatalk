from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class ColumnListRequest(BaseModel):
    user_id: Optional[int] = None  # ✅ 특정 사용자의 좋아요 여부 확인 (없을 수도 있음)
    category: Optional[str] = None
    keyword: Optional[str] = None
    cursor: Optional[str] = None
    limit: int = 10
    sort: str = "created_at"



class LikeRequest(BaseModel):
    column_id: int # 컬럼 id
    user_id: int #유저 id 

class LikeResponse(BaseModel):
    success: bool  # ✅ 성공 여부
    message: str  # ✅ 응답 메시지
