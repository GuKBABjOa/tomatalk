<template>
  <div class="debate-result">
    <!-- Title Section with Tori -->
    <section class="result-header">
      <div class="header-content">
        <div class="tori-character">
          <img :src="getImageUrl('Tori.svg')" alt="토리 이미지" class="tori" />
        </div>
        <div class="header-text">
          <h2>토론 연습 결과</h2>
          <p>탁월한 분석력과 논리적인 전개가 돋보였어요!</p>
        </div>
      </div>
    </section>

    <!-- Analysis Cards -->
    <section class="analysis-cards">
      <!-- Strengths Card -->
      <div class="analysis-card">
        <div class="card-icon">💪</div>
        <div class="card-content">
          <h3>강점 분석</h3>
          <p>{{ analysis.strengths || "분석된 강점이 없습니다." }}</p>
        </div>
      </div>

      <!-- Improvements Card -->
      <div class="analysis-card">
        <div class="card-icon">🔍</div>
        <div class="card-content">
          <h3>보완할 점</h3>
          <p>{{ analysis.improvements || "분석된 보완할 점이 없습니다." }}</p>
        </div>
      </div>

      <!-- Strategy Card -->
      <div class="analysis-card">
        <div class="card-icon">🚀</div>
        <div class="card-content">
          <h3>효과적인 토론 전략</h3>
          <p>{{ analysis.strategy || "분석된 전략이 없습니다." }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from "vue";

// ✅ 부모 컴포넌트에서 `analysis` props를 받아 사용
defineProps({
  analysis: {
    type: Object,
    required: true,
    default: () => ({
      strengths: "",
      improvements: "",
      strategy: "",
    }),
  },
});

const getImageUrl = (filename: string): string => {
  return new URL(`../assets/${filename}`, import.meta.url).href;
};
</script>

<style scoped>
.debate-result {
  margin: 0 auto;
  padding: 20px;
  background: #ffffff;
}

/* Header Section */
.result-header {
  background: none;
  border-radius: 16px;
  height: 60px;
  padding-top: 10px;
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 40px;
}

.tori-character {
  position: relative;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tori {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.header-text {
  margin-left: 24px;
}

.header-text h2 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
}

.header-text p {
  font-size: 18px;
  color: #4b5563;
}

/* Analysis Cards */
.analysis-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.analysis-card {
  background: rgb(246, 246, 246);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #ff6b6b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.card-content {
  margin-left: 16px;
  flex-grow: 1;
}

.card-content h3 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 12px;
}

.card-content p {
  font-size: 16px;
  color: #4b5563;
  line-height: 1.5;
}

/* Responsive Design */
@media (max-width: 768px) {
  .header-content {
    padding: 0 20px;
  }

  .header-text h2 {
    font-size: 20px;
  }

  .header-text p {
    font-size: 16px;
  }

  .analysis-card {
    padding: 20px;
  }
}
</style>
