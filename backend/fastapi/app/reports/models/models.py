from sqlalchemy import Column, Integer, BigInteger, String, Text, Boolean, TIMESTAMP, func
from database import Base

# 참여자 발언 데이터 모델
class Report(Base):
    __tablename__ = "report"

    id = Column(BigInteger, primary_key=True, autoincrement=True)  # 고유 ID
    debate_id = Column(String(255), nullable=False)  # 토론 ROOM ID
    user_id = Column(BigInteger, nullable=False)  # 사용자 ID (큰 숫자 지원)
    total = Column(Integer, nullable=False),
    total_explanation = Column(Text, nullable=False)
    reasoning = Column(Integer, nullable=False)
    reasoning_explanation = Column(Text, nullable=False)
    expression = Column(Integer, nullable=False)
    expression_explanation = Column(Text, nullable=False)
    strategy = Column(Integer, nullable=False)
    strategy_explanation = Column(Text, nullable=False)
    interaction = Column(Integer, nullable=False)
    interaction_explanation = Column(Text, nullable=False)
    is_deleted = Column(Boolean, nullable=False, default=False)  # 소프트 삭제 처리
    created_at = Column(TIMESTAMP, server_default=func.current_timestamp())  # 생성 시각

