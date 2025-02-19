<template>
  <div class="debate-container">
    <!-- Header -->
    <header class="header">
      <h1 class="title">실전 토론 시작하기</h1>

      <!-- Step Indicators -->
      <div class="steps">
        <div class="step active">
          <div class="step-circle">1</div>
          <span class="step-text">토론 방식 선택</span>
        </div>
        <div class="step-line"></div>
        <div class="step">
          <div class="step-circle">2</div>
          <span class="step-text">주제 선택</span>
        </div>
        <div class="step-line"></div>
        <div class="step">
          <div class="step-circle">3</div>
          <span class="step-text">상대 매칭하기</span>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <div class="content">
      <!-- Format Cards -->
      <div class="format-cards">
        <div
          v-for="format in formats"
          :key="format.id"
          :class="['format-card', { selected: selectedFormat === format.id }]"
          @click="selectFormat(format.id)"
        >
          <div class="format-icon">{{ format.emoji }}</div>
          <div class="format-info">
            <h3>{{ format.title }}</h3>
            <p>{{ format.shortDescription }}</p>
          </div>
          <div class="format-details">
            <span>• 진행 시간: {{ format.duration }}</span>
            <span>• 난이도: {{ "⭐".repeat(format.difficulty) }}</span>
          </div>
        </div>
      </div>

      <!-- Format Details -->
      <div class="format-detail-panel" v-if="currentFormat">
        <h2>{{ currentFormat.title }}</h2>

        <!-- Description -->
        <section class="detail-section">
          <h3>토론 설명</h3>
          <p>{{ currentFormat.description }}</p>
        </section>

        <!-- Process -->
        <section class="detail-section">
          <h3>진행 방식</h3>
          <div class="process-box">
            <div
              v-for="(step, index) in currentFormat.process"
              :key="index"
              class="process-step"
            >
              {{ index + 1 }}. {{ step }}
            </div>
          </div>
        </section>

        <!-- Tips -->
        <section class="detail-section">
          <h3>토리의 Tip</h3>
          <div class="tips-box">
            <div
              v-for="(tip, index) in currentFormat.tips"
              :key="index"
              class="tip"
            >
              💡 {{ tip }}
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- Next Button -->
    <button class="next-button" :disabled="!selectedFormat" @click="goToNext">
      <span class="arrow">→</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useMatchingStore } from "@/stores/matchingStore";

const router = useRouter();
interface DebateFormat {
  id: string;
  title: string;
  emoji: string;
  description: string;
  duration: string;
  difficulty: number;
  shortDescription: string;
  process: string[];
  tips: string[];
}

const formats: DebateFormat[] = [
  {
    id: "casual",
    title: "캐쥬얼 토론",
    emoji: "🗣️",
    description:
      "토마톡의 캐쥬얼 토론은 기존 토론의 장점을 결합하여 누구나 쉽게 참여할 수 있도록 설계되었습니다. 찬성과 반대 양측이 각각의 주장을 펼치고 서로의 의견을 교환하며, 관중들의 실시간 피드백을 받을 수 있습니다.",
    duration: "30분",
    difficulty: 2,
    shortDescription: "쉽고 재미있게 시작하는 토론",
    process: [
      "준비 시간 (5분): 주제 확인 및 논리 구성",
      "입론 (각 3분): 찬성/반대 측의 주장 발표",
      "반론 (각 2분): 상대방 주장에 대한 반론",
      "최종 발언 (각 2분): 마지막 주장 정리",
    ],
    tips: [
      "입론에서는 핵심 주장을 먼저 말하고, 그 근거를 차례대로 제시하세요.",
      "반론할 때는 상대방의 주장 중 가장 약한 부분을 공략하세요.",
      "감정적인 표현보다는 논리적인 근거를 제시하세요.",
    ],
  },
  {
    id: "bp",
    title: "BP 토론",
    emoji: "🏛️",
    description: "영국 의회식 토론 방식으로 진행되는 전문적인 토론입니다.",
    duration: "45분",
    difficulty: 4,
    shortDescription: "영국 의회식 토론",
    process: [
      "준비 시간 (15분)",
      "찬성 측 제1 발언 (7분)",
      "반대 측 제1 발언 (7분)",
      "찬성 측 제2 발언 (7분)",
      "반대 측 제2 발언 (7분)",
    ],
    tips: [
      "각 발언은 POI(Point of Information)를 포함할 수 있습니다.",
      "첫 1분과 마지막 1분은 POI가 금지됩니다.",
      "심사 기준: 내용, 방법, 태도를 종합적으로 평가합니다.",
    ],
  },
];

const selectedFormat = ref<string>("casual");

const currentFormat = computed(() =>
  formats.find((format) => format.id === selectedFormat.value)
);

const matchingStore = useMatchingStore();

const selectFormat = (formatId: string) => {
  selectedFormat.value = formatId;
  matchingStore.setFormat(formatId); // 선택한 토론 방식 저장
};

const goToNext = () => {
  router.push("/category-selection");
};

onMounted(() => {
  matchingStore.setFormat("casual");
});
</script>

<style scoped>
.debate-container {
  margin: 0 auto;
  font-family: "Pretendard", sans-serif;
}

.header {
  background: linear-gradient(180deg, #fff1f1 0%, #ffffff 100%);
  padding: 48px 48px;
  padding-left: 90px;
  margin-bottom: 40px;
}

.title {
  font-size: 32px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 20px;
}

.steps {
  display: flex;
  align-items: center;
  gap: 20px;
}

.step {
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-circle {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e5e7eb;
  color: white;
  font-size: 12px;
}

.step.active .step-circle {
  background: #ff6b6b;
}

.step-line {
  width: 30px;
  height: 2px;
  background: #e5e7eb;
}

.content {
  display: flex;
  gap: 20px;
  padding: 0 48px;
}

.format-cards {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.format-card {
  padding: 24px;
  border-radius: 16px;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.format-card.selected {
  background: #fff1f1;
  border: 2px solid #ff6b6b;
}

.format-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #fff1f1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 16px;
}

.format-card.selected .format-icon {
  background: #ff6b6b;
}

.format-info h3 {
  font-size: 20px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 4px;
}

.format-info p {
  font-size: 16px;
  color: #6b7280;
}

.format-details {
  margin-top: 16px;
  display: flex;
  gap: 20px;
  color: #4b5563;
  font-size: 14px;
}

.format-detail-panel {
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.detail-section {
  width: 700px;
  margin-bottom: 40px;
}

.detail-section h3 {
  font-size: 16px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 16px;
}

.process-box,
.tips-box {
  background: #fff1f1;
  padding: 20px;
  border-radius: 8px;
}

.process-step,
.tip {
  margin-bottom: 12px;
  color: #4b5563;
  font-size: 14px;
  line-height: 1.6;
}

.next-button {
  position: absolute;
  right: 10px;
  top: 50%;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #ff6b6b;
  border: none;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.next-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
}

.arrow {
  font-size: 24px;
}
</style>
