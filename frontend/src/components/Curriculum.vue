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
    ];

    return { steps };
  },
});
</script>

<style scoped>
.curriculum {
  background-color: #fef2f2;
  padding: 3rem;
  padding-left: 5rem;
  padding-right: 5rem;
}

.section-title {
  text-align: center;
  font-weight: bold;
  color: #111827;
  font-size: 2rem;
  /* 기존 32px → 2rem */
  margin-bottom: 5rem;
  /* 기존 80px → 5rem */
}

.steps {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 3rem;
  /* 기존 20px → 1.25rem */
}

.step-card {
  background: white;
  padding: 2rem;
  /* 기존 32px → 2rem */
  border-radius: 1rem;
  /* 기존 16px → 1rem */
  height: 18.75rem;
  /* 기존 300px → 18.75rem */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.step-emoji {
  font-size: 2.5rem;
  /* 기존 40px → 2.5rem */
  margin-bottom: 1.25rem;
  /* 기존 20px → 1.25rem */
  display: block;
}

.step-number {
  font-size: 1.25rem;
  /* 기존 20px → 1.25rem */
  margin-bottom: 0.5rem;
  /* 기존 8px → 0.5rem */
  font-weight: bold;
  color: #111827;
}

.step-title {
  font-weight: bold;
  color: #ff6b6b;
  font-size: 1.125rem;
  /* 기존 18px → 1.125rem */
  margin-bottom: 1.25rem;
  /* 기존 20px → 1.25rem */
}

.step-features {
  list-style: none;
  padding: 0;
}

.step-features li {
  color: #4b5563;
  font-size: 0.875rem;
  /* 기존 14px → 0.875rem */
  margin-bottom: 0.5rem;
  /* 기존 8px → 0.5rem */
  padding-left: 0.75rem;
  /* 기존 12px → 0.75rem */
  position: relative;
}

.step-features li::before {
  content: "•";
  position: absolute;
  left: 0;
  color: #4b5563;
}
</style>
