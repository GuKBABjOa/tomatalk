<template>
  <div class="category-selection">
    <!-- Header Section -->
    <header class="header">
      <div class="header-content">
        <h1 class="title">주제 카테고리 선택</h1>

        <!-- Step Indicators -->
        <div class="steps">
          <div class="step">
            <div class="step-circle">1</div>
            <span class="step-text">토론 방식</span>
          </div>
          <div class="step-line"></div>
          <div class="step active">
            <div class="step-circle">2</div>
            <span class="step-text">주제 카테고리</span>
          </div>
          <div class="step-line"></div>
          <div class="step">
            <div class="step-circle">3</div>
            <span class="step-text">상대 매칭하기</span>
          </div>
        </div>
      </div>
    </header>

    <!-- Info Box -->
    <div class="info-box">
      <p>
        💡 토론 주제는 매칭이 완료된 후 공개됩니다. 준비 시간이 주어지니
        걱정하지 마세요!
      </p>
    </div>

    <!-- Category Grid -->
    <div class="category-grid">
      <div v-for="category in categories" :key="category.id" class="category-card"
        :class="{ selected: selectedCategory === category.id }" @click="handleCategoryClick(category.id)">
        <div class="icon-circle">
          <span class="icon">{{ category.icon }}</span>
        </div>
        <h2 class="category-title">{{ category.title }}</h2>
        <p class="category-description">{{ category.description }}</p>
      </div>
    </div>

    <button class="before-button" @click="goToBefore">
      <span class="arrow">←</span>
    </button>
    <button class="next-button" :disabled="!selectedCategory" @click="goToNext">
      <span class="arrow">→</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useMatchingStore } from "@/stores/matchingStore";
const router = useRouter();
const matchingStore = useMatchingStore();

interface Category {
  id: string;
  icon: string;
  title: string;
  description: string;
}

const categories: Category[] = [
  {
    id: "SCIENCE",
    icon: "💡",
    title: "과학/기술",
    description: "AI, 우주, 환경, 생명과학",
  },
  {
    id: "POLITICS",
    icon: "⚖️",
    title: "사회/정치",
    description: "정책, 제도, 법률, 인권",
  },
  {
    id: "ECONOMY",
    icon: "💰",
    title: "경제",
    description: "금융, 산업, 노동, 복지",
  },
  {
    id: "EDUCATION",
    icon: "📚",
    title: "교육/문화",
    description: "교육제도, 예술, 미디어",
  },
  {
    id: "ETHICS",
    icon: "🤔",
    title: "윤리/철학",
    description: "도덕, 가치관, 철학적 담론",
  },
];

const selectedCategory = ref<string>("");

const selectCategory = (categoryId: string) => {
  selectedCategory.value = categoryId;
};

const emit = defineEmits<{
  (e: "select", categoryId: string): void;
}>();

const handleCategoryClick = (categoryId: string) => {
  selectCategory(categoryId);
  emit("select", categoryId);
  matchingStore.setCategory(categoryId); // 선택한 카테고리 저장
};

const goToBefore = () => {
  router.push("/debate-select");
};

const goToNext = () => {
  if (!selectedCategory.value) return;
  matchingStore.startMatching();
  router.push("/");
};
</script>

<style scoped>
.header {
  width: 100vw;
  margin-left: calc(-50vw + 50%);
  background: linear-gradient(180deg, #fff1f1 0%, #ffffff 100%);
  padding: 48px 0;
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

.subtitle {
  font-size: 18px;
  color: #6b7280;
  margin-bottom: 24px;
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
  background-color: #e5e7eb;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.step.active .step-circle {
  background-color: #ff6b6b;
}

.step-text {
  font-size: 14px;
  color: #6b7280;
}

.step-line {
  width: 30px;
  height: 2px;
  background-color: #e5e7eb;
}

.category-grid {
  margin-left: 100px;
  margin-right: 100px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.category-card {
  /* width: 270px; */
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.category-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff1f1;
  opacity: 0.5;
  border-radius: 16px;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.selected {
  border: 2px solid #ff6b6b;
}

.icon-circle {
  width: 80px;
  height: 80px;
  background: #ff6b6b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  position: relative;
}

.icon {
  font-size: 36px;
}

.category-title {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
  position: relative;
}

.category-description {
  font-size: 16px;
  color: #6b7280;
  position: relative;
}

.info-box {
  height: auto;
  background: #efefef;
  border-radius: 8px;
  padding: 1px;
  /* 기존 padding: 20px에서 조정 */
  margin: 20px auto;
  /* 기존 margin-top: 40px 제거 */
  max-width: 1080px;
  /* 중앙 정렬을 위한 최대 너비 설정 */
  text-align: center;
  /* 텍스트 중앙 정렬 */
}

.info-box p {
  color: #ff6b6b;
  font-size: 1rem;
  padding-top: 1rem;
  padding-bottom: 1rem;
}

.before-button {
  position: absolute;
  left: 10px;
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

.before-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
}

.next-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
}

.arrow {
  font-size: 24px;
}
</style>
