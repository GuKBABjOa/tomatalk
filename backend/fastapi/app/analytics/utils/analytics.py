from analytics.repository.repository import save_speechrate
from sqlalchemy.ext.asyncio import AsyncSession

async def speech_rate(
        db:AsyncSession,
        user_id: int,
        duration: float,
        text: str
        ):
    """
    발화 속도를 계산하는 함수

    Args:
        duration (float): 발화 시간 (초, `time.time()` 차이로 계산)
        text (str): 발화된 문장

    Returns:
        dict: 초당 단어 수(WPS), 분당 단어 수(WPM)
    """
    if duration <= 0:
        return {"error": "Duration must be greater than 0"}

    word_count = len(text.split())  # 단어 개수 계산
    wps = word_count / duration  # 초당 단어 수
    wpm = wps * 60  # 분당 단어 수

    save_speechrate(db, user_id, duration, word_count, wps, wpm)


