<template>
  <section class="features">
    <h2 class="section-title">
      여러분의 토론 실력 성장을 위한 <br />모든 과정을 지원해드려요
    </h2>

    <div class="feature-cards">
      <div class="feature-card" v-for="feature in features" :key="feature.title">
        <div class="feature-card">
          <div class="feature-header">
            <span class="feature-emoji">{{ feature.emoji }}</span>
            <h3 class="feature-title">{{ feature.title }}</h3>
          </div>
          <p class="feature-description">{{ feature.description }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
interface Feature {
  emoji: string;
  title: string;
  description: string;
}

const features: Feature[] = [
  {
    emoji: "🤖",
    title: "AI 토론 연습",
    description: "AI 토론 연습으로 부담 없이 실력을 키워보세요",
  },
  {
    emoji: "🗣️",
    title: "실전 토론",
    description: "다양한 주제로 실제 토론을 경험해보세요",
  },
  {
    emoji: "📈",
    title: "성장 기록",
    description: "내 토론 스타일을 분석받고 발전시켜보세요",
  },
];
</script>

<style scoped>
.features {
  padding-bottom: 5rem;
}

.section-title {
  text-align: center;
  font-weight: bold;
  color: #111827;
  font-size: 2rem;
  /* 기존 32px → 2rem */
  margin-bottom: 3.75rem;
  /* 기존 60px → 3.75rem */
  line-height: 1.5;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.25rem;
  /* 기존 20px → 1.25rem */
}

.feature-card {
  background: rgb(247, 246, 246);
  padding: 0.75rem;
  /* 기존 12px → 0.75rem */
  border-radius: 1rem;
  /* 기존 16px → 1rem */
  display: flex;
  flex-direction: column;
  /* 전체 배치를 세로 정렬 */
  align-items: flex-start;
  /* 내부 요소 왼쪽 정렬 */
}

.feature-header {
  display: flex;
  /* 아이콘과 제목을 가로 정렬 */
  align-items: center;
  /* 수직 중앙 정렬 */
  gap: 0.75rem;
  /* 기존 12px → 0.75rem */
  margin-bottom: 0.75rem;
  /* 기존 12px → 0.75rem */
}

.feature-emoji {
  font-size: 2.5rem;
  /* 기존 40px → 2.5rem */
  flex-shrink: 0;
  /* 아이콘 크기 유지 */
}

.feature-title {
  font-size: 1.5rem;
  /* 기존 24px → 1.5rem */
  font-weight: bold;
  color: #111827;
  margin: 0;
  /* 기본 마진 제거 */
}

.feature-description {
  color: #4b5563;
  font-size: 1rem;
  /* 기존 16px → 1rem */
  line-height: 1.5;
}
</style>
