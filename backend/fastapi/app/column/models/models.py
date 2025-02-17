from sqlalchemy import Column, Integer, BigInteger, String, Text, Boolean, TIMESTAMP, func
from database import Base

class DebateColumn(Base):
    __tablename__ = "debate_column"

    id = Column(BigInteger, primary_key=True, autoincrement=True)  # 고유 ID
    title = Column(String(255), nullable=False)  # 칼럼 제목
    subtitle = Column(String(255), nullable=False)  # 부제목
    category = Column(String(255), nullable=False) # 카테고리 
    discussion_overview = Column(Text, nullable=False)  # 토론 개요

    participant_1_name = Column(String(255), nullable=False)  # 참가자 1 이름
    participant_1_statement = Column(Text, nullable=False)  # 참가자 1 발언
    participant_2_name = Column(String(255), nullable=False)  # 참가자 2 이름
    participant_2_statement = Column(Text, nullable=False)  # 참가자 2 발언
    participant_3_name = Column(String(255), nullable=False)  # 참가자 3 이름
    participant_3_statement = Column(Text, nullable=False)  # 참가자 3 발언
    participant_4_name = Column(String(255), nullable=False)  # 참가자 4 이름
    participant_4_statement = Column(Text, nullable=False)  # 참가자 4 발언

    conclusion = Column(Text, nullable=False)  # 토론 결론
    author_opinion = Column(Text, nullable=False)  # 칼럼 작성자의 개인 의견

    like_count = Column(BigInteger, nullable=False, default=0)
    is_deleted = Column(Boolean, nullable=False, default=False)  # 소프트 삭제 처리
    created_at = Column(TIMESTAMP, server_default=func.current_timestamp())  # 생성 시각