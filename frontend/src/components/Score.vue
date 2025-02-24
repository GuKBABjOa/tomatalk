<template>
  <div class="debate-performance-container">
    <div class="performance-card">
      <h1 class="report-title">점수 관련 모달이 들어갈 예정입니다</h1>
      <p class="participant-name">참가자: O2</p>

      <!-- Total Score -->
      <div class="total-score-section">
        <p class="total-score-label">총점</p>
        <p class="total-score">
          {{ totalScore }}
          <span class="total-score-max">/100</span>
        </p>
      </div>

      <!-- Performance Categories -->
      <div class="performance-categories">
        <div
          v-for="(category, index) in performanceCategories"
          :key="index"
          class="category-item"
        >
          <div class="category-header" @click="toggleCategory(index)">
            <span class="category-title">{{ category.label }}</span>
            <span class="category-score" :class="category.color">
              {{ category.score }}점
            </span>
          </div>

          <transition name="slide">
            <div v-if="category.isExpanded" class="category-details">
              <p>{{ category.description }}</p>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const totalScore = ref(85);

const performanceCategories = ref([
  {
    label: "논리력",
    score: 29,
    color: "green-score",
    description:
      "O2의 논리력은 현실적인 문제와 대안을 체계적으로 분석했습니다. 난민 수용의 복잡한 측면을 다각도로 접근하며, 국가 주권과 사회 통합의 관점에서 깊이 있는 논거를 제시했습니다.",
    isExpanded: false,
  },
  {
    label: "표현력",
    score: 18,
    color: "pink-score",
    description:
      "키워드를 활용하여 명확한 메시지를 전달했으나, 감정적 호소나 비유를 통한 표현은 다소 부족했습니다. 더 풍부한 표현 기법을 활용하면 논점의 설득력을 높일 수 있을 것입니다.",
    isExpanded: false,
  },
  {
    label: "전략",
    score: 18,
    color: "yellow-score",
    description:
      "주요 논점을 잘 파악하고 상대방의 주장을 효과적으로 반박했습니다. 그러나 제한된 시간으로 인해 더 깊이 있는 분석과 확장된 논의가 다소 부족했습니다.",
    isExpanded: false,
  },
  {
    label: "상호작용",
    score: 20,
    color: "teal-score",
    description:
      "토론 중 상대방을 존중하며 건설적인 대화를 이끌어냈습니다. 청중의 이해를 돕기 위해 논점을 명확히 설명하고, 다른 참가자들의 의견을 경청하는 태도를 보였습니다.",
    isExpanded: false,
  },
]);

const toggleCategory = (index) => {
  performanceCategories.value[index].isExpanded =
    !performanceCategories.value[index].isExpanded;
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, "Segoe UI",
    Roboto, "Helvetica Neue", Arial, sans-serif;
}

.debate-performance-container {
  background-color: #f9fafb;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.performance-card {
  background-color: white;
  width: 100%;
  max-width: 720px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 40px;
}

.report-title {
  font-size: 36px;
  font-weight: 800;
  color: #2d3748;
  text-align: center;
  margin-bottom: 16px;
}

.participant-name {
  font-size: 22px;
  color: #4a5568;
  text-align: center;
  margin-bottom: 40px;
}

.total-score-section {
  text-align: center;
  margin-bottom: 48px;
}

.total-score-label {
  font-size: 24px;
  color: #2d3748;
  font-weight: 600;
  margin-bottom: 12px;
}

.total-score {
  font-size: 72px;
  font-weight: 800;
  color: #ff6b6b;
}

.total-score-max {
  font-size: 36px;
  color: #718096;
  margin-left: 8px;
}

.performance-categories {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #f7fafc;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.category-header:hover {
  background-color: #edf2f7;
}

.category-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
}

.category-score {
  font-size: 20px;
  font-weight: 600;
}

.category-details {
  padding: 20px;
  background-color: white;
  color: #4a5568;
  line-height: 1.6;
}

.green-score {
  color: #48bb78;
}
.pink-score {
  color: #ed64a6;
}
.yellow-score {
  color: #ecc94b;
}
.teal-score {
  color: #4fd1c5;
}

/* Transition Animations */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
  max-height: 500px;
  overflow: hidden;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
