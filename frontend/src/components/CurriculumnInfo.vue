// DebateJourney.vue
<template>
  <div class="journey-container">
    <div class="title-section">
      <h1>토론 마스터가 되는 여정</h1>
      <p>4단계 학습을 통해 토론 실력을 체계적으로 키워보세요</p>
    </div>

    <div class="timeline" :class="{ 'timeline-visible': isVisible }">
      <div class="progress-line"></div>

      <div
        v-for="(step, index) in steps"
        :key="index"
        class="journey-step"
        :style="{
          '--step-delay': `${0.2 * (index + 1)}s`,
          '--step-color': step.color,
        }"
      >
        <div class="step-circle">
          <span class="step-number">{{ step.number }}</span>
          <span class="step-icon">{{ step.icon }}</span>
        </div>

        <div class="step-card">
          <div class="card-header">
            <h2>{{ step.title }}</h2>
            <p class="subtitle">{{ step.subtitle }}</p>
          </div>

          <div class="content-box">
            <ul>
              <li v-for="(point, pointIndex) in step.points" :key="pointIndex">
                {{ point }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";

interface StepData {
  number: number;
  title: string;
  subtitle: string;
  points: string[];
  icon: string;
  color: string;
}

const steps: StepData[] = [
  {
    number: 1,
    title: "기초 다지기",
    subtitle: "토리와 첫 토론 시작",
    points: ["토론의 기본 구조 이해하기", "주장과 근거 구성 연습하기"],
    icon: "📚",
    color: "#4F46E5",
  },
  {
    number: 2,
    title: "실전 연습",
    subtitle: "AI와 함께하는 토론",
    points: ["반론 전략 학습하기", "논리적 사고력 훈련하기"],
    icon: "🤖",
    color: "#7C3AED",
  },
  {
    number: 3,
    title: "실전 토론",
    subtitle: "다른 유저와 토론",
    points: ["실시간 토론에 참여하기", "발언 분석 레포트로 피드백 받기"],
    icon: "👥",
    color: "#EC4899",
  },
];

const isVisible = ref(false);

onMounted(() => {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          isVisible.value = true;
          observer.disconnect();
        }
      });
    },
    {
      threshold: 0.2,
    }
  );

  const container = document.querySelector(".journey-container");
  if (container) {
    observer.observe(container);
  }
});
</script>

<style scoped>
.journey-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 40px;
  font-family: "Pretendard", sans-serif;
  background-color: #ffffff;
  box-sizing: border-box;
}

.title-section {
  text-align: center;
  margin-bottom: 80px;
}

.title-section h1 {
  font-size: 36px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 16px;
  letter-spacing: -0.02em;
}

.title-section p {
  font-size: 18px;
  color: #6b7280;
  line-height: 1.5;
}

.timeline {
  position: relative;
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding-top: 32px;
}

.progress-line {
  position: absolute;
  top: 48px;
  left: 60px;
  right: 60px;
  height: 3px;
  background: linear-gradient(90deg, #4f46e5, #7c3aed, #ec4899, #f59e0b);
  transform-origin: left center;
  transform: scaleX(0);
  transition: transform 2s ease-out;
}

.timeline-visible .progress-line {
  transform: scaleX(1);
}

.journey-step {
  position: relative;
  flex: 1;
  min-width: 240px;
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.5s ease-out, transform 0.5s ease-out;
  transition-delay: var(--step-delay);
}

.timeline-visible .journey-step {
  opacity: 1;
  transform: translateY(0);
}

.step-circle {
  position: relative;
  width: 64px;
  height: 64px;
  margin: 0 auto 40px;
  background-color: var(--step-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 2;
  transition: transform 0.3s ease;
}

.step-circle:hover {
  transform: scale(1.1);
}

.step-number {
  position: absolute;
  font-size: 20px;
  font-weight: 700;
  color: white;
  opacity: 1;
  transition: opacity 0.3s ease;
}

.step-icon {
  position: absolute;
  font-size: 24px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.step-circle:hover .step-number {
  opacity: 0;
}

.step-circle:hover .step-icon {
  opacity: 1;
}

.step-card {
  background-color: #ffffff;
  border-radius: 20px;
  padding: 32px;
  height: 280px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.step-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.card-header {
  margin-bottom: 24px;
}

.step-card h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
  letter-spacing: -0.01em;
}

.subtitle {
  font-size: 16px;
  color: #6b7280;
  line-height: 1.5;
}

.content-box {
  background-color: #f9fafb;
  border-radius: 12px;
  padding: 20px;
}

.content-box ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.content-box li {
  font-size: 15px;
  color: #374151;
  margin-bottom: 12px;
  padding-left: 24px;
  position: relative;
  line-height: 1.5;
}

.content-box li:last-child {
  margin-bottom: 0;
}

.content-box li::before {
  content: "";
  position: absolute;
  left: 0;
  top: 8px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: var(--step-color);
}

@media (max-width: 1024px) {
  .timeline {
    flex-direction: column;
    gap: 40px;
    align-items: center;
  }

  .progress-line {
    left: 50%;
    width: 3px;
    top: 0;
    bottom: 0;
    transform-origin: top center;
    transform: scaleY(0);
  }

  .timeline-visible .progress-line {
    transform: scaleY(1);
  }

  .journey-step {
    width: 100%;
    max-width: 480px;
  }
}

@media (max-width: 640px) {
  .journey-container {
    padding: 40px 20px;
  }

  .title-section h1 {
    font-size: 28px;
  }

  .title-section p {
    font-size: 16px;
  }

  .step-card {
    padding: 24px;
    height: auto;
  }
}
</style>
