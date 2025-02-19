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
          <p>
            학생은 교복 자율화가 학생들의 개성을 존중하고 자기표현의 자유를
            보장하는 긍정적인 변화라는 주장을 명확하게 제시했습니다. 또한, 유럽
            일부 국가의 사례를 통해 주장을 뒷받침하고 있습니다.
          </p>
        </div>
      </div>

      <!-- Improvements Card -->
      <div class="analysis-card">
        <div class="card-icon">🔍</div>
        <div class="card-content">
          <h3>보완할 점</h3>
          <p>
            유럽 일부 국가의 사례를 언급했지만, 구체적인 국가나 학교 이름을
            제시하면 더 설득력이 있을 것입니다. 또한, 학업 분위기가 유지된다는
            점을 구체적으로 설명하면 좋겠습니다.
          </p>
        </div>
      </div>

      <!-- Strategy Card -->
      <div class="analysis-card">
        <div class="card-icon">🚀</div>
        <div class="card-content">
          <h3>효과적인 토론 전략</h3>
          <p>
            반대 입장에서 제기될 수 있는 경제적 차이에 대한 우려를 해결하기 위해
            기본적인 복장 가이드라인을 설정하는 방안을 제시한 점은 좋습니다.
            이와 더불어, 이러한 가이드라인이 실제로 어떻게 운영될 수 있는지
            구체적인 예시를 추가하면 주장을 더욱 강화할 수 있습니다.
          </p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
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
