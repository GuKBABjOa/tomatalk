from sqlalchemy import Column, Integer, BigInteger, String, Text, Boolean, TIMESTAMP, func
from database import Base

# 참여자 발언 데이터 모델
class Statement(Base):
    __tablename__ = "statement"

    id = Column(BigInteger, primary_key=True, autoincrement=True)  # 고유 ID
    debate_id = Column(String(255), nullable=False)  # 토론 ROOM ID
    position = Column(Text, nullable=False)  # 토론 주제
    user_id = Column(BigInteger, nullable=False)  # 사용자 ID (큰 숫자 지원)
    nickname = Column(String(255), nullable=False)  # 사용자 닉네임
    round = Column(Integer, nullable=False, default=0)  # 현재 라운드
    statement = Column(Text, nullable=False)  # 발언 내용
    is_deleted = Column(Boolean, nullable=False, default=False)  # 소프트 삭제 처리
    created_at = Column(TIMESTAMP, server_default=func.current_timestamp())  # 생성 시각
    updated_at = Column(TIMESTAMP, server_default=func.current_timestamp(), onupdate=func.current_timestamp())  # 수정 시각

class Summation(Base):
    __tablename__ = "summation"
    id = Column(BigInteger, primary_key=True, autoincrement=True)  # 고유 ID
    debate_id = Column(String(255), nullable=False)  # 토론 ROOM ID
    position = Column(Text, nullable=False)  # 토론 주제
    user_id = Column(BigInteger, nullable=False)  # 사용자 ID (큰 숫자 지원)
    nickname = Column(String(255), nullable=False)  # 사용자 닉네임
    round = Column(Integer, nullable=False, default=0)  # 현재 라운드
    summation = Column(Text, nullable=False)  # 발언 내용
    is_deleted = Column(Boolean, nullable=False, default=False)  # 소프트 삭제 처리
    created_at = Column(TIMESTAMP, server_default=func.current_timestamp())  # 생성 시각
    updated_at = Column(TIMESTAMP, server_default=func.current_timestamp(), onupdate=func.current_timestamp())  # 수정 시각

