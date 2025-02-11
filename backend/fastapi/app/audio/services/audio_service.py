import os
from tempfile import NamedTemporaryFile
from audio.utils.stt_sr import convert_audio_to_text  # STT 변환 함수

async def process_audio_file(file_data: bytes) -> dict:
    """
    업로드된 오디오 파일을 임시 파일로 저장 후 텍스트로 변환하는 서비스 로직

    Args:
        file_data (bytes): 업로드된 WAV 파일의 바이트 데이터

    Returns:
        dict: 변환된 텍스트 결과
    """
    try:
        # 임시 파일 저장
        with NamedTemporaryFile(delete=False, suffix=".wav") as temp_file:
            temp_file.write(file_data)
            temp_file_path = temp_file.name

        # 오디오를 텍스트로 변환
        result_text = await convert_audio_to_text(temp_file_path)

        # 임시 파일 삭제
        os.remove(temp_file_path)

        # 결과 데이터 생성
        result = ["", "", ""]  # 나중에 처리할 결과 데이터
        return {
            "tts_response": result_text,
            "summation": result[0],
            "comment1": result[1],
            "comment2": result[2]
        }

    except Exception as e:
        raise RuntimeError(f"Error processing audio file: {e}")
