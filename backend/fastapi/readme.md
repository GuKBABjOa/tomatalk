# 🚀 FastAPI 프로젝트

이 프로젝트는 **FastAPI 기반의 오디오 처리 API**를 제공합니다.  
STT(Speech-to-Text) 변환 기능을 포함하며, Whisper 및 SpeechRecognition을 활용한 오디오 변환 기능을 지원합니다.

---

## 📌 프로젝트 구조
```bash
fastapi/
│── assets/                # 오디오 샘플 파일 저장 폴더
│   ├── testaudio.wav      # 테스트용 오디오 파일
│── audio/                 # 오디오 관련 기능 모듈
│   ├── utils/             # 유틸리티 모듈
│   │   ├── stt_sr.py      # SpeechRecognition 기반 STT 변환 모듈
│   │   ├── stt_whisper.py # Whisper 기반 STT 변환 모듈
│   │   ├── __init__.py    # 패키지 초기화
│   ├── audio_router.py    # 오디오 관련 FastAPI 라우터
│── test/                  # 테스트 코드
│   ├── test_audio.py      # 오디오 처리 관련 테스트
│   ├── test_main.py       # FastAPI 기본 API 테스트
│   ├── __init__.py        # 테스트 패키지 초기화
│── .gitignore             # Git에서 제외할 파일 목록
│── dockerfile             # Docker 배포 설정
│── main.py                # FastAPI 실행 메인 파일
│── readme.md              # 프로젝트 설명
│── requirements.txt       # Python 패키지 목록

# 📌 가상 환경 설정 (`venv` 사용)
이 프로젝트는 Python의 **가상 환경(`venv`)**을 사용하여 의존성을 격리하고 관리합니다.

## 🔹 가상 환경 생성
```sh
python -m venv venv
```

## 🔹 가상 환경 활성화
### ✅ Windows
```sh
venv\Scripts\activate
```
### ✅ Mac / Linux
```sh
source venv/bin/activate
```

## 🔹 의존성 패키지 설치
```sh
pip install -r requirements.txt
```

## 🔹 가상 환경 비활성화
```sh
deactivate
```

---

# 📌 FastAPI 애플리케이션 실행
## 🔹 개발 서버 실행
```sh
uvicorn main:app --reload
```
- `--reload` 옵션을 추가하면 코드 변경 시 자동으로 서버가 재시작됩니다.
- 서버가 실행되면 **[http://127.0.0.1:8000/docs](http://127.0.0.1:8000/docs)** 에서 Swagger UI를 통해 API 테스트 가능!

---

# 📌 API 테스트 방법
## 🔹 Swagger UI 사용
브라우저에서 **[http://127.0.0.1:8000/docs](http://127.0.0.1:8000/docs)** 접속 후 API 테스트

## 🔹 cURL을 사용한 API 테스트
```sh
curl -X 'POST' \
  'http://127.0.0.1:8000/upload-audio/' \
  -H 'accept: application/json' \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@testaudio.wav'
```

---

# 📌 테스트 실행
이 프로젝트는 **`pytest`**를 사용하여 API 테스트를 자동화합니다.

## 🔹 `pytest` 설치
```sh
pip install pytest
```

## 🔹 모든 테스트 실행
```sh
pytest
```

✅ **테스트가 통과하면 프로젝트가 정상적으로 동작하는 것입니다.**
