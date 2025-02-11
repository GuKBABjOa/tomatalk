# from fastapi.testclient import TestClient
# from backend.fastapi.app.main import app  # FastAPI 앱 가져오기

# client = TestClient(app)
# def test_audio_upload():
#     """오디오 업로드 API 테스트"""
#     files = {"file": ("test_audio.mp3", b"dummy content", "audio/mpeg")}
#     response = client.post("/upload-audio/", files=files)
#     assert response.status_code == 201
#     assert "file_name" in response.json()