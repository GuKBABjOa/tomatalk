import pymysql
from dotenv import load_dotenv
import os

# 환경 변수 로드
load_dotenv()
USER_NAME = os.getenv("USER_NAME")
PASSWORD = os.getenv("PASSWORD")
HOST_NAME = os.getenv("HOST_NAME" "localhost")
PORT = int(os.getenv("PORT", 3306))
DB_NAME = os.getenv("DB_NAME")

# 데이터베이스 연결 설정
connection = pymysql.connect(
    host=HOST_NAME,
    port=PORT,
    user=USER_NAME,
    password=PASSWORD,
    db=DB_NAME,
    charset='utf8',
    autocommit=True  # 자동 커밋 설정
)

async def save_statement(room_id: str, user_id: str, round: int, statement: str):
    """
    토론 발언을 데이터베이스에 저장하는 함수

    :param room_id: 토론방 ID
    :param user_id: 사용자 ID
    :param round: 라운드 번호
    :param statement: 사용자가 한 발언 내용
    """
    print("💾 save_statement 호출됨")

    # 입력 데이터 유효성 검사
    if not all([room_id, user_id, round, statement]):
        print("⚠️ 오류: 입력값이 부족합니다.")
        return

    try:
        with connection.cursor() as cursor:
            # SQL 실행
            sql = """
            INSERT INTO statement (room_id, user_id, round, statement)
            VALUES (%s, %s, %s, %s)
            """
            cursor.execute(sql, (room_id, user_id, round, statement))

        print("✅ 데이터 저장 완료")
    except pymysql.MySQLError as e:
        print(f"❌ 데이터 저장 오류: {e}")
    finally:
        cursor.close()

