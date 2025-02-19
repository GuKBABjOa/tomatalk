<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <h2>토리와 함께하는 토론 미리보기</h2>

      <!-- 토론 진행 예시 -->
      <div class="preview-section">
        <div class="chat-container">
          <div class="chat-message tori">
            <img
              :src="getImageUrl('Tori.svg')"
              alt="토리 이미지"
              class="tori-avatar-small"
            />
            <div class="message">
              안녕하세요! 저는 토론 코치 토리입니다. 교복 자율화에 대해 어떻게
              생각하시나요? 그 이유도 함께 말씀해주세요.
            </div>
          </div>

          <div class="chat-message user">
            <div class="avatar">나</div>
            <div class="message">
              저는 교복 자율화에 찬성합니다. 학생들이 자유롭게 옷을 선택하면서
              개성을 표현하고 자율성을 기를 수 있다고 생각해요.
            </div>
          </div>

          <div class="chat-message tori">
            <img
              :src="getImageUrl('Tori.svg')"
              alt="토리 이미지"
              class="tori-avatar-small"
            />
            <div class="message">
              말씀하신 의견을 뒷받침할 수 있는 구체적인 사례나 경험을 들어
              설명해주실 수 있나요?
            </div>
          </div>
        </div>
      </div>

      <!-- 진행 방식 설명 -->
      <div class="explanation-section">
        <h3>토론은 4단계로 진행됩니다</h3>
        <ul>
          <li>
            <span class="step">1</span>
            <div class="step-content">
              <strong>주장 세우기</strong>
              <p>주제에 대한 자신의 입장과 이유를 설명합니다</p>
            </div>
          </li>
          <li>
            <span class="step">2</span>
            <div class="step-content">
              <strong>근거 찾기</strong>
              <p>구체적인 사례나 경험을 통해 주장을 뒷받침합니다</p>
            </div>
          </li>
          <li>
            <span class="step">3</span>
            <div class="step-content">
              <strong>다른 관점 고려하기</strong>
              <p>반대 입장의 우려사항과 해결방안을 생각합니다</p>
            </div>
          </li>
          <li>
            <span class="step">4</span>
            <div class="step-content">
              <strong>주장 강화하기</strong>
              <p>지금까지의 대화를 바탕으로 자신의 입장을 다시 정리합니다</p>
            </div>
          </li>
        </ul>
      </div>

      <div class="modal-footer">
        <button class="close-button" @click="$emit('close')">알겠습니다</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineEmits(["close"]);
const getImageUrl = (filename) => {
  return new URL(`../assets/${filename}`, import.meta.url).href;
};
</script>

<style scoped>
.tori-avatar-small {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 24px;
  padding: 40px;
  width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h2 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 30px;
}

.preview-section {
  margin-bottom: 40px;
}

.chat-container {
  background-color: #f8f9fa;
  border-radius: 16px;
  padding: 20px;
}

.chat-message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.chat-message:last-child {
  margin-bottom: 0;
}

.chat-message .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  margin-right: 16px;
  flex-shrink: 0;
}

.chat-message.tori .avatar {
  background-color: #ff6b6b;
  color: white;
}

.chat-message.user .avatar {
  background-color: #e5e7eb;
  color: #6b7280;
}

.chat-message .message {
  background-color: white;
  padding: 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  color: #111827;
  max-width: 80%;
}

.chat-message.tori .message {
  background-color: white;
  border: 1px solid #e5e7eb;
}

.chat-message.user .message {
  background-color: #ff6b6b;
  color: white;
}

.explanation-section {
  margin-top: 40px;
}

.explanation-section h3 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 20px;
}

.explanation-section ul {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.explanation-section li {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 12px;
}

.step {
  width: 24px;
  height: 24px;
  background-color: #ff6b6b;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  margin-right: 12px;
  flex-shrink: 0;
}

.step-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.step-content strong {
  color: #111827;
  font-size: 16px;
}

.step-content p {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.modal-footer {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.close-button {
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 12px 48px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.close-button:hover {
  background-color: #ff5252;
}
</style>
