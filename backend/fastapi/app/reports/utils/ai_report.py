import faiss
import numpy as np
import json
from sentence_transformers import SentenceTransformer
from ai.utils.report import generate_chat_response
from transcripts.repository.repository import get_statement
from sqlalchemy.ext.asyncio import AsyncSession
from reports.repository.repository import save_report

# Load the multilingual SentenceTransformer model
model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")

# Load the FAISS index from disk
index = faiss.read_index("reports/utils/debate_statements.index")

# Assuming you have already populated the file_names list elsewhere
# If it's not populated elsewhere, you'll need to populate it as per your setup.
# For example:
file_names = ['../documents/debatabase\\Economy_global_free_trade.txt', '../documents/debatabase\\Economy_national_minimum_wage.txt', '../documents/debatabase\\Economy_nuclear.txt', '../documents/debatabase\\Education_foreign_language.txt', '../documents/debatabase\\Education_home_schooling.txt', '../documents/debatabase\\International_forcibly_reunify_korea.txt', '../documents/debatabase\\International_illegal_immigrants_repatriation.txt', '../documents/debatabase\\International_israel_palestine_two_state.txt', '../documents/debatabase\\International_tibet_independence.txt', '../documents/debatabase\\Law_criminal_dna_database.txt', '../documents/debatabase\\Law_death_penalty.txt', '../documents/debatabase\\Law_income_relative_fines.txt', '../documents/debatabase\\Law_self_determination.txt', '../documents/debatabase\\Politics_more_women_in_parliament.txt', '../documents/debatabase\\Science_ban_anonymous_posting.txt', '../documents/debatabase\\Science_colonize_moon.txt', '../documents/debatabase\\Science_government_open_source.txt', '../documents/debatabase\\Science_internet_censorship.txt', '../documents/debatabase\\Society_age_of_consent.txt', '../documents/debatabase\\Society_one_child_policy.txt']  


async def evaluate_statement(
    db: AsyncSession,
    debate_id: str,
    user_id: int
    ) -> str:

    """
    사용자의 발언을 통해 레포트를 생성하는 함수 
    args: 
        statement: 참여자 발언 

    return:  
    """
    statement_list = await get_statement(
        db = db,
        debate_id = debate_id)
    statement = "/n".join(statement_list)

    # Vectorize the input statement using the same multilingual model
    query_vector = np.array([model.encode(statement)], dtype=np.float32)
    
    # Retrieve the 5 most similar statements from the FAISS index
    distances, indices = index.search(query_vector, 5)
    
    similar_statements = []
    
    # Ensure indices are within the range of file_names
    for idx in indices[0]:
        if idx != -1 and idx < len(file_names):  # Check that index is valid
            similar_statements.append(file_names[idx])  # Get the filenames of similar statements
                                                                    
    # If no similar statements found, provide a message indicating this
    if not similar_statements:
        return "No similar statements found."
    
    # Build a string from the similar statements
    similar_text = "\n".join([f"- {stmt}" for stmt in similar_statements])
    print("유사 텍스트:"+similar_text)
    # Call the new ChatCompletion API interface (v1.0.0+)
    temp_response = await generate_chat_response(user_id, statement, similar_text)
    response = temp_response.split("/")
    await save_report(
        db=db,
        debate_id=debate_id,
        user_id=user_id,
        total=response[0],
        total_explanation=response[1],
        reasoning=response[2],
        reasoning_explanation=response[3],
        expression=response[4],
        expression_explanation=response[5],
        strategy=response[6],
        strategy_explanation=response[7],
        interaction= response[8],
        interaction_explanation=response[9]
        )
    return response

