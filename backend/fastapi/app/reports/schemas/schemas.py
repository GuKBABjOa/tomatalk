from pydantic import BaseModel
from typing import Optional


class ReportRequest(BaseModel):
    debate_id: Optional[str] = None
    user_id: Optional[int] = None