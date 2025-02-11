from fastapi.testclient import TestClient
from backend.fastapi.app.main import app  # FastAPI 앱 가져오기

client = TestClient(app)

def test_read_root():
    """루트 경로('/') 테스트"""
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hello, World!"}

