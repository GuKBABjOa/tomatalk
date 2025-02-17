from sqlalchemy.ext.asyncio import AsyncSession
from transcripts.repository.repository import get_statement
from ai.utils.column import generate_chat_response
from column.repository.repository import save_column

async def create_cloumn(
        debate_id:str,
        db: AsyncSession,
        ):
    
    statement_list = await get_statement(db, debate_id)
    combined_text = "\n".join(statement_list)
    column_text = await generate_chat_response(combined_text)
    column_text_list = column_text.split("/")
    # 리스트를 딕셔너리로 변환
    column_data = {
        "title": column_text_list[0],
        "subtitle": column_text_list[1],
        "discussion_overview": column_text_list[2],
        "participant_1_name": column_text_list[3],
        "participant_1_statement": column_text_list[4],
        "participant_2_name": column_text_list[5],
        "participant_2_statement": column_text_list[6],
        "participant_3_name": column_text_list[7],
        "participant_3_statement": column_text_list[8],
        "participant_4_name": column_text_list[9],
        "participant_4_statement": column_text_list[10],
        "conclusion": column_text_list[11],
        "author_opinion": column_text_list[12]
    }
    # **kwargs로 가독성 높이기
    await save_column(db, **column_data)

