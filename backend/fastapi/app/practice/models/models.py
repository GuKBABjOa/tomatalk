from sqlalchemy import Column, Integer, BigInteger, String, Text, Boolean, TIMESTAMP, func
from database import Base

# 참여자 발언 데이터 모델
class Practice(Base):
    __tablename__ = "practice"

    id = Column(BigInteger, primary_key=True, autoincrement=True)  # 고유 ID
    problem_id = Column(String(255), nullable=False)  # 토론 ROOM ID
    user_id = Column(BigInteger, nullable=False)  # 사용자 ID (큰 숫자 지원)
    input = Column(Text, nullable=False)
    output = Column(Text, nullable=False)
    is_deleted = Column(Boolean, nullable=False, default=False)  # 소프트 삭제 처리
    created_at = Column(TIMESTAMP, server_default=func.current_timestamp())  # 생성 시각

