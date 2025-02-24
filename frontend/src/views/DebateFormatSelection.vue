<template>
  <div class="debate-container">
    <!-- Header -->
    <header class="header">
      <div class="header-content">
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
      </div>
    </header>

    <!-- Main Content -->
    <div class="content">
      <!-- Format Cards -->
      <div class="format-cards">
        <div v-for="format in formats" :key="format.id"
          :class="['format-card', { selected: selectedFormat === format.id }]" @click="selectFormat(format.id)">
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
            <div v-for="(step, index) in currentFormat.process" :key="index" class="process-step">
              {{ index + 1 }}. {{ step }}
            </div>
          </div>
        </section>

        <!-- Tips -->
        <section class="detail-section2">
          <h3>토리의 Tip</h3>
          <div class="tips-box">
            <div v-for="(tip, index) in currentFormat.tips" :key="index" class="tip">
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
    title: "Casual 토론",
    emoji: "🗣️",
    description:
      "토론은 4명이 참가하며, 랜덤으로 찬성(2명)과 반대(2명)이 배정됩니다. 하지만 같은 진영이라도 팀이 아니며, 기여도에 따라 순위가 차등 결정됩니다. 같은 진영의 주장이라도 논리적으로 맞지 않다면 지적할 수 있습니다. 논리적으로 사고하고 설득력 있는 주장을 펼쳐보세요!",
    duration: "30분",
    difficulty: 2,
    shortDescription: "토론 실력을 키우는 실전 방식",
    process: [
      "참가자 4명이 랜덤으로 찬성(2명)과 반대(2명) 진영으로 배정됩니다.",
      "같은 진영이지만 팀이 아니며, 기여도에 따라 순위가 매겨집니다.",
      "준비 시간 (5분): 주제를 확인하고 논리를 구성합니다.",
      "입론 (각 5분): 찬성 1명 → 반대 1명 → 찬성 1명 → 반대 1명 순서로 발언합니다.",
      "두 번째 주장 (각 3분): 동일한 순서로 추가 주장을 펼칩니다.",
      "같은 진영의 주장이라도 논리적으로 맞지 않다면 지적할 수 있습니다.",
    ],
    tips: [
      "같은 진영이라도 경쟁 관계이므로 논리적으로 우위를 점하세요.",
      "기여도가 높은 참가자가 더 높은 평가를 받습니다.",
      "상대방뿐만 아니라 같은 진영의 논리적 오류도 지적할 수 있습니다.",
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
  width: 100vw;
  margin-left: calc(-50vw + 50%);
  background: linear-gradient(180deg, #fff1f1 0%, #ffffff 100%);
  padding: 48px 0px;
  /* padding-left: 90px; */
  margin-bottom: 40px;
}

.header-content {
  width: 75%;
  margin: 0 auto;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
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

.step-text {
  font-size: 14px;
  color: #6b7280;
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
  margin-bottom: 0.25rem;
}

.format-cards {
  max-width: 300px;
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
  font-size: 0.75rem;
}

.format-detail-panel {
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.detail-section {
  max-width: 700px;
  margin-bottom: 40px;
}

.detail-section2 {
  max-width: 700px;
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
  position: fixed;
  right: 3vw;
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
