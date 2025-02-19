from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from practice.models.models import Practice
from typing import Optional

async def save_practice(
        db: AsyncSession,
        problem_id: str,
        user_id: int,
        input: str, 
        output: str):
    """
    연습 문제 결과를 저장하는 함수 
    """
    
    try:
        new_practice = Practice(
            problem_id = problem_id,
            user_id = user_id,
            input = input,
            output = output,
        )
        db.add(new_practice)
        await db.commit()
        await db.refresh(new_practice)
        print("✅ Statement 저장 성공:", new_practice.id)
    except Exception as e:
        await db.rollback()
        print(f"❌ Statement 저장 오류: {e}")
        raise e

async def get_practice(
        db: AsyncSession,
        problem_id: str,
        user_id: int
        ) -> list:
    """
    문제풀이 결과 조회 
    """
    try:
        query = select(Practice).where(Practice.problem_id == problem_id)
        query = query.where(Practice.user_id == user_id)

        result = await db.execute(query)
        practice_obj = result.scalars().first()
        return practice_obj if practice_obj else []
    except Exception as e:
        print(f"❌ Summation 조회 오류: {e}")
        raise e

