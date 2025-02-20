from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from reports.models.models import Report
from typing import Optional
from sqlalchemy.sql import func
from typing import List

async def save_report(
        db: AsyncSession,
        debate_id: str,
        user_id: int,
        total: int,
        total_explanation: str,
        reasoning: int,
        reasoning_explanation: str, 
        expression: int,
        expression_explanation: str, 
        strategy: int, 
        strategy_explanation: str, 
        interaction: int,
        interaction_explanation: str
        ):
    """
    레포트를 데이터베이스에 저장하는 함수 (비동기 방식)
    """
    new_report = Report(
        debate_id= debate_id,
        user_id = user_id,
        total = total,
        total_explanation= total_explanation,
        reasoning = reasoning,
        reasoning_explanation = reasoning_explanation,
        expression = expression,
        expression_explanation = expression_explanation,
        strategy = strategy, 
        strategy_explanation = strategy_explanation,
        interaction = interaction, 
        interaction_explanation = interaction_explanation
     )
    
    try:
        db.add(new_report)
        await db.commit()
        await db.refresh(new_report)
        print("✅ Statement 저장 성공:", new_report.id)
    except Exception as e:
        await db.rollback()
        print(f"❌ Statement 저장 오류: {e}")
        raise e
    
async def get_report(
        db: AsyncSession,
        debate_id: Optional[int] = None,
        user_id: Optional[int] = None) -> list:
    """
    주어진 debate_id, user_id에 해당하는 report 값을 조회하는 함수
    """
    try:
        query = select(Report)
        if debate_id:
            query = query.where(Report.debate_id == debate_id)

        if user_id:
            query = query.where(Report.user_id == user_id)

        result = await db.execute(query)
        report_obj = result.scalars().all()
        return [ [report.id, report.total, report.reasoning, report.expression, report.strategy, report.interaction] for report in report_obj] if report_obj else []
    except Exception as e:
        print(f"❌ report 조회 오류: {e}")
        raise e
    
async def get_report_avg(
    db: AsyncSession,
    user_id: int
) -> List[List[float]]:
    """
    특정 유저의 점수 평균 값을 조회하는 함수

    :param db: AsyncSession 데이터베이스 세션
    :param user_id: 사용자 ID
    :return: [[평균 total, 평균 reasoning, 평균 expression, 평균 strategy, 평균 interaction]] (없을 경우 빈 리스트)
    """
    try:
        # ✅ 평균 점수 계산 쿼리
        query = select(
            func.avg(Report.total),
            func.avg(Report.reasoning),
            func.avg(Report.expression),
            func.avg(Report.strategy),
            func.avg(Report.interaction)
        ).where(Report.user_id == user_id)

        result = await db.execute(query)
        avg_scores = result.fetchone()  # ✅ 평균 점수 결과 가져오기

        # ✅ 평균 점수를 2차원 리스트 형태로 변환
        return [[
            avg_scores[0] or 0,  # 평균값이 None이면 0으로 처리
            avg_scores[1] or 0,
            avg_scores[2] or 0,
            avg_scores[3] or 0,
            avg_scores[4] or 0
        ]] if avg_scores else []
    
    except Exception as e:
        print(f"❌ report 평균 조회 오류: {e}")
        raise e