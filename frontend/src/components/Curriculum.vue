<template>
  <section class="curriculum">
    <h2 class="section-title">토론 마스터를 향한 체계적인 커리큘럼</h2>

    <div class="steps">
      <div class="step-card" v-for="step in steps" :key="step.step">
        <span class="step-emoji">{{ step.emoji }}</span>
        <div class="step-number">STEP {{ step.step }}</div>
        <h3 class="step-title">{{ step.title }}</h3>
        <ul class="step-features">
          <li v-for="feature in step.features" :key="feature">{{ feature }}</li>
        </ul>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import { defineComponent } from "vue";

interface Step {
  step: number;
  emoji: string;
  title: string;
  features: string[];
}

export default defineComponent({
  name: "Curriculum",
  setup() {
    const steps: Step[] = [
      {
        step: 1,
        emoji: "🎯",
        title: "토론 기초 다지기",
        features: ["토론의 기본 규칙", "논리적 사고 훈련", "주장과 근거 구성"],
      },
      {
        step: 2,
        emoji: "💡",
        title: "AI 토론 연습",
        features: ["AI 토론봇과 1:1 토론", "실시간 피드백"],
      },
      {
        step: 3,
        emoji: "🎓",
        title: "실전 토론 도전",
        features: ["실시간 매칭 토론", "관중 피드백 시스템", "토론 기록 분석"],
      },
      {
        step: 4,
        emoji: "🏆",
        title: "토론 마스터 인증",
        features: [
          "단계별 인증서 발급",
          "토론 마스터 등급",
          "전문 토론가 인증",
        ],
      },
    ];

    return { steps };
  },
});
</script>

<style scoped>
.curriculum {
  background-color: #fef2f2;
  padding: 48px;
}

.section-title {
  text-align: center;
  font-size: 32px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 80px;
}

.steps {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.step-card {
  background: white;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  height: 300px;
}

.step-emoji {
  font-size: 40px;
  display: block;
  margin-bottom: 20px;
}

.step-number {
  font-size: 20px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
}

.step-title {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
  margin-bottom: 20px;
}
.step-features {
  list-style: none;
  padding: 0;
}

.step-features li {
  color: #4b5563;
  font-size: 14px;
  margin-bottom: 8px;
  position: relative;
  padding-left: 12px;
}

.step-features li::before {
  content: "•";
  position: absolute;
  left: 0;
  color: #4b5563;
}
</style>
