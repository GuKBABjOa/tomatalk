 📌 가상 환경 설정 (`venv` 사용)
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

## 🔹 의존성 패키지 설치(반드시 가상 환경 실행 후 해당 가상환경 내에서 실행)
```sh
pip install -r requirements.txt
```

## 🔹 가상 환경 비활성화
```sh
deactivate
```

# 📌 FastAPI 애플리케이션 실행
## 🔹 디렉토리 이동 
-fastapi -> app으로 이동 

## 🔹 개발 서버 실행
```sh
uvicorn main:app --host 0.0.0.0 --port 8000 --reload