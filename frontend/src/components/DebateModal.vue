<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <button class="close-button" @click="$emit('close')">✕</button>
      <DebateInterface
        v-if="!isDebateCompleted"
        :topic="topic"
        :stance="stance"
        @debate-complete="handleDebateComplete"
      />
      <DebateResult
        v-else
        :topic="topic"
        :stance="stance"
        :analysis="debateAnalysis"
        @retry="handleRetry"
        @close="$emit('close')"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import DebateInterface from "./AiDebateRoom.vue";
import DebateResult from "./AiDebateResult.vue";

const props = defineProps({
  topic: {
    type: Object,
    required: true,
  },
  stance: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(["close"]);

// 토론 완료 상태
const isDebateCompleted = ref(false);

// 토론 분석 결과 데이터
const debateAnalysis = ref({
  strengths: "",
  improvements: "",
  strategy: "",
  recordedAnswers: [],
});

// 토론 완료 핸들러
const handleDebateComplete = (analysis) => {
  debateAnalysis.value = analysis;
  isDebateCompleted.value = true;
};

// 토론 다시하기 핸들러
const handleRetry = () => {
  isDebateCompleted.value = false;
  debateAnalysis.value = {
    strengths: "",
    improvements: "",
    strategy: "",
    recordedAnswers: [],
  };
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
  position: relative;
  width: 80vw; /* 80vw에서 60vw로 축소 */
  height: 90vh; /* 60vh에서 50vh로 축소 */
  background-color: #f8f9fa;
  border-radius: 16px; /* 24px에서 16px로 축소 */
  overflow: hidden;
}

.close-button {
  position: absolute;
  top: 12px; /* 20px에서 12px로 축소 */
  right: 12px; /* 20px에서 12px로 축소 */
  background: white;
  border: none;
  width: 24px; /* 32px에서 24px로 축소 */
  height: 24px; /* 32px에서 24px로 축소 */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px; /* 18px에서 14px로 축소 */
  color: #6b7280;
  cursor: pointer;
  z-index: 1001;
  transition: all 0.2s;
}

.close-button:hover {
  background-color: #f3f4f6;
  color: #111827;
}
</style>
