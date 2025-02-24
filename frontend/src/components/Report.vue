<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-container" @click.stop>
      <!-- 상단 헤더 -->
      <header class="header">
        <div class="header-content">
          <button class="close-button" @click="$emit('close')">×</button>

          <h1 class="header-title">{{ report.topic }}</h1>
        </div>
      </header>

      <!-- 메인 컨텐츠 -->
      <main class="main-content">
        <h2 class="score-number">{{ report.totalScore }}점</h2>
        <p class="bad-word">클린한 발언자</p>

        <!-- 카테고리별 평가 -->
        <section class="categories-section">
          <h2 class="section-title">카테고리별 평가</h2>
          <div class="categories-grid">
            <div
              v-for="category in report.categories"
              :key="category.name"
              class="category-card"
            >
              <div
                class="category-indicator"
                :style="{ backgroundColor: category.color }"
              ></div>
              <div class="category-content">
                <div class="category-header">
                  <h3 class="category-title">{{ category.name }}</h3>
                  <span
                    class="category-score"
                    :style="{ color: category.color }"
                  >
                    {{ category.score }}점
                  </span>
                </div>
                <p class="category-description">{{ category.description }}</p>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  report: {
    type: Object,
    default: () => ({
      topic: "행복은 돈으로부터 오는가?",
      date: "2025.02.19 15:29",
      totalScore: 87,
      percentile: "상위 15%",
      categories: [
        {
          name: "논리력",
          score: 27,
          color: "#48BB78",
          description: `찬성과 반대 측이 명확한 주장과 근거를 제시함. 
사례를 활용하여 주장에 대한 현실적 정당성을 부여함 (예: 부유한 사람들의 행복 지수, 로또 당첨자의 불행 사례). 
데이터 인용이 부족함. 구체적인 통계나 연구 결과를 인용하면 더 설득력이 높아질 것.`,
        },
        {
          name: "표현력",
          score: 25,
          color: "#ED64A6",
          description: `문장이 너무 길거나 복잡하지 않고, 이해하기 쉬움. 
강조할 부분이 명확하여 전달력이 좋음. 
일부 문장에서 너무 일반적인 표현이 반복됨. "돈이 많으면 기회가 많아진다", "돈이 많다고 무조건 행복한 것은 아니다" 같은 문장은 보다 구체적인 사례나 강한 표현으로 바꿀 필요가 있음.`,
        },
        {
          name: "전략",
          score: 18,
          color: "#ECC94B",
          description: `서론-본론-결론 구조가 잘 갖춰져 있음. 
찬성 측이 지나치게 현실적인 접근을 하고 있고, 반대 측은 다소 추상적인 가치 중심 논리를 펼치고 있음. 상대방의 논리를 더욱 구체적으로 겨냥하면 좋을 것.`,
        },
        {
          name: "상호작용",
          score: 17,
          color: "#4FD1C5",
          description: `반박이 명확하게 이루어져 있고, 논점이 유지됨. 
반박이 지나치게 일반적이어서 "핵심 논리를 깨뜨리는" 강력한 논증이 부족함.`,
        },
      ],
    }),
  },
});

defineEmits(["close"]);
</script>

<style scoped>
.close-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6b7280;
  cursor: pointer;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  overflow: hidden;
}

.modal-container {
  background-color: #f8f9fa;
  width: 67.5%;
  max-width: 120vh;
  max-height: 80vh;
  border-radius: 12px;
  overflow-y: auto;
}

.header {
  background-color: #ffffff;
  height: 5rem;
  box-shadow: 0 0.15rem 0.3rem rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
}

.header-content {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 2rem;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.back-button {
  background: none;
  border: none;
  color: #6b7280;
  font-size: 1rem;
  cursor: pointer;
}

.header-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.main-content {
  max-width: 960px;
  margin: 0 auto;
  padding: 3rem;
  position: relative;
}

.bad-word {
  position: absolute;
  top: 50px;
  right: 50px;
  font-size: 1.5rem;
  font-weight: 600;
  color: green;
}

.total-score-section {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 1rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 1.5rem 0;
}

.score-content {
  display: flex;
  align-items: baseline;
  gap: 0.75rem;
}

.score-number {
  font-size: 3rem;
  font-weight: 700;
  color: #ff6b6b;
  display: flex;
  align-items: center;
  text-align: center; /* 텍스트 중앙 정렬 */
  justify-content: center; /* 가운데 정렬 추가 */
  flex-direction: column; /* 세로 정렬 */
}
.score-details {
  display: flex;
  flex-direction: column;
}

.score-max {
  font-size: 1.5rem;
  color: #9ca3af;
}

.score-percentile {
  font-size: 1.125rem;
  color: #4b5563;
  margin-top: 0.375rem;
}

.categories-section {
  margin-top: 3rem;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

.category-card {
  background-color: #ffffff;
  border-radius: 9px;
  display: flex;
  height: 12rem;
  box-shadow: 0 0.075rem 0.225rem rgba(0, 0, 0, 0.08);
}

.category-indicator {
  width: 0.3rem;
  border-radius: 0.15rem 0 0 0.15rem;
}

.category-content {
  flex: 1;
  padding: 2.25rem;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.category-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.category-score {
  font-size: 1.5rem;
  font-weight: 600;
}

.category-description {
  font-size: 0.875rem;
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
  white-space: pre-line;
}
</style>
