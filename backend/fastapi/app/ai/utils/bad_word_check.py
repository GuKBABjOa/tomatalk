import torch
from transformers import BertForSequenceClassification, AutoTokenizer

repo_id = "happypigs7/check_badword"
# 모델 및 토크나이저 로드
tokenizer = AutoTokenizer.from_pretrained(repo_id)
model = BertForSequenceClassification.from_pretrained(repo_id)
model.eval() # 추론 모드 설정

# 긴 문장을 512 토큰 이하로 나누는 함수
def split_text(text, max_length=512):
    tokens = tokenizer.tokenize(text)  # 문장을 토큰으로 변환
    chunks = [tokens[i:i+max_length] for i in range(0, len(tokens), max_length)]  # 512개씩 분할
    return [" ".join(chunk) for chunk in chunks]  # 다시 문자열로 변환

# 개별 청크를 예측하고 전체 결과를 종합하는 함수
def predict_bad_word(text: str):
    chunks = split_text(text)  # 긴 문장을 여러 개의 512 토큰 이하 청크로 나눔
    all_results = []  # 모든 청크의 결과 저장

    for chunk in chunks:
        inputs = tokenizer(chunk, return_tensors="pt", padding=True, truncation=True, max_length=512)
        
        with torch.no_grad():
            outputs = model(**inputs)

        # 확률값 변환
        probabilities = torch.sigmoid(outputs.logits).squeeze().tolist()

        # 카테고리 라벨 (10개 클래스)
        categories = ["여성/가족", "남성", "성소수자", "인종/국적", "연령", "지역", "종교", "기타 혐오", "악플/욕설", "clean"]

        # 결과 정리
        results = [{"label": label, "score": score} for label, score in zip(categories, probabilities)]
        highest_label = max(results, key=lambda x: x["score"])  # 가장 높은 확률을 가진 카테고리

        all_results.append(highest_label)  # 각 청크의 결과 저장

    # 문서 전체에서 가장 높은 점수를 가진 카테고리 찾기
    final_prediction = max(all_results, key=lambda x: x["score"])
    bad_word = False
    if final_prediction["label"] != "clean":
        bad_word = True

    return {
        "text": text,
        "prediction": final_prediction["label"],
        "score": final_prediction["score"],
        "details": all_results,  # 모든 청크의 결과 반환
        "bad_word": bad_word
    }