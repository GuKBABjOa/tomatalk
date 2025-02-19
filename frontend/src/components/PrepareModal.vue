<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>토론 준비하기</h2>
        <p>{{ topic.title }}</p>
        <!-- X 버튼 추가 -->
        <button class="close-button" @click="$emit('close')">✕</button>
      </div>

      <!-- 입장 선택 섹션 -->
      <section class="stance-section">
        <h3>1. 나의 입장 선택하기</h3>
        <div class="stance-options">
          <div
            class="stance-card"
            :class="{ active: selectedStance === 'agree' }"
            @click="selectedStance = 'agree'"
          >
            <h4>찬성</h4>
            <p>필요하다고 생각합니다</p>
          </div>

          <div
            class="stance-card"
            :class="{ active: selectedStance === 'disagree' }"
            @click="selectedStance = 'disagree'"
          >
            <h4>반대</h4>
            <p>필요하지 않다고 생각합니다</p>
          </div>
        </div>
      </section>

      <!-- 토론 개요 섹션 -->
      <section class="overview-section">
        <h3>2. 토론 개요 확인하기</h3>
        <div class="overview-content">
          <div class="main-points">
            <h4>주요 논점</h4>
            <ul>
              <li v-for="(point, index) in topic.mainPoints" :key="index">
                {{ point }}
              </li>
            </ul>
          </div>

          <div class="references">
            <h4>참고자료</h4>
            <ul>
              <li v-for="(ref, index) in topic.references" :key="index">
                {{ ref }}
              </li>
            </ul>
          </div>
        </div>
      </section>

      <div class="modal-footer">
        <button
          class="start-button"
          :disabled="!selectedStance"
          @click="startDebate"
        >
          토론 시작하기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const props = defineProps({
  topic: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["close", "start"]);

const selectedStance = ref("");

const startDebate = () => {
  if (selectedStance.value) {
    emit("start", props.topic.id, selectedStance.value);
    emit("close");
  }
};
</script>

<style scoped>
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
  width: 700px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
}

.modal-header p {
  font-size: 16px;
  color: #6b7280;
  margin-bottom: 40px;
}

.stance-section,
.overview-section {
  margin-bottom: 40px;
}

.stance-section h3,
.overview-section h3 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 20px;
}

.stance-options {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.stance-card {
  padding-left: 20px;
  border-radius: 16px;
  border: 2px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
}

.stance-card:hover {
  border-color: #ff6b6b;
}

.stance-card.active {
  border-color: #ff6b6b;
  background-color: #fff1f1;
}

.stance-card h4 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
}

.stance-card p {
  font-size: 14px;
  color: #6b7280;
}

.overview-content {
  background-color: #f8f9fa;
  border-radius: 16px;
  padding: 10px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.overview-content h4 {
  font-size: 16px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 16px;
}

.overview-content ul {
  list-style: none;
  padding: 0;
}

.overview-content li {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 12px;
  padding-left: 20px;
  position: relative;
}

.overview-content li::before {
  content: "•";
  position: absolute;
  left: 0;
  color: #ff6b6b;
}

.modal-footer {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.start-button {
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 12px 48px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.start-button:hover:not(:disabled) {
  background-color: #ff5252;
}

.start-button:disabled {
  background-color: #e5e7eb;
  cursor: not-allowed;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #6b7280;
  position: absolute;
  top: -5px;
  right: 20px;
}

.close-button:hover {
  color: #ff6b6b;
}

.modal-header {
  position: relative; /* 추가 */
}
</style>
