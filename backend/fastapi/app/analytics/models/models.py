from sqlalchemy import Column, Integer, BigInteger, String, Text, Boolean, TIMESTAMP, func, Float
from database import Base

class SpeechRate(Base):
    __tablename__ = "speech_rate"
    id = Column(BigInteger, primary_key=True, autoincrement=True)  # 고유 ID
    user_id = Column(BigInteger, nullable=False)  # 유저 ID
    duration = Column(Float, nullable=False)
    word_count = Column(Integer, nullable=False)
    wps = Column(Float, nullable=False)
    wpm = Column(Float, nullable=False)
    is_deleted = Column(Boolean, nullable=False, default=False)
    created_at = Column(TIMESTAMP, server_default=func.current_timestamp())