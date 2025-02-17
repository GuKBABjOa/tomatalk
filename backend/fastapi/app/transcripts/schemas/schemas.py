from pydantic import BaseModel

class SummaryRequest(BaseModel):
    devate_id: str
    user_id: int
    current_round: int