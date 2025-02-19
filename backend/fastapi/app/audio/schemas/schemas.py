from pydantic import BaseModel

class BasicSTTRequest(BaseModel):
    topic: str
    position: str
    question: str 
    user_id: int 