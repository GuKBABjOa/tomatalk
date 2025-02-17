from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker, declarative_base
import dotenv
import os

# 환경 변수 로드
dotenv.load_dotenv()

USER_NAME = os.getenv("USER_NAME")
PASSWORD = os.getenv("PASSWORD")
HOST_NAME = os.getenv("HOST_NAME", "localhost")
PORT = int(os.getenv("PORT", 3306))
DB_NAME = os.getenv("DB_NAME")

# 비동기 MySQL 데이터베이스 URL (aiomysql 사용)
ASYNC_SQLALCHEMY_DATABASE_URL = f"mysql+aiomysql://{USER_NAME}:{PASSWORD}@{HOST_NAME}:{PORT}/{DB_NAME}"

# 비동기 엔진 생성 (연결 풀 설정 포함)
async_engine = create_async_engine(
    ASYNC_SQLALCHEMY_DATABASE_URL,
    echo=True,  # SQL 실행 로그 출력 (디버깅용)
    pool_size=10,  # 최대 연결 개수
    max_overflow=20  # 초과 연결 개수
)

# 비동기 세션 팩토리 생성
AsyncSessionLocal = sessionmaker(
    bind=async_engine,
    class_=AsyncSession,
    expire_on_commit=False
)

# ORM 모델 기반 클래스
Base = declarative_base()

# FastAPI 의존성 주입을 위한 비동기 DB 세션 함수
async def get_async_db():
    async with AsyncSessionLocal() as session:
        yield session  # 요청이 끝나면 자동으로 세션 종료
