<!-- DebateReportMain.vue -->
<template>
  <div class="app-container">
    <!-- 메인 컨텐츠 -->
    <main class="main-content">
      <!-- 헤더 -->
      <header class="header">
        <h1 class="greeting">안녕하세요, {{ userName }}님</h1>

        <p class="feedback">최근 토론에서 논리적 전개가 훌륭했어요!</p>
        <p class="unread-notice">읽지 않은 레포트 3개가 있습니다</p>
      </header>

      <!-- 읽지 않은 레포트 섹션 -->
      <section class="unread-reports">
        <h2 class="section-title">읽지 않은 레포트</h2>
        <div class="slider-container">
          <button class="nav-button prev" @click="slidePrev">←</button>
          <div class="report-cards" ref="slider">
            <div
              v-for="(report, index) in unreadReports"
              :key="index"
              class="report-card"
            >
              <div class="report-highlight"></div>
              <div class="report-content">
                <h3 class="report-type">{{ report.type }}</h3>
                <p class="report-date">{{ report.date }}</p>
                <p class="report-detail">주제: {{ report.topic }}</p>
                <p class="report-detail">입장: {{ report.position }}</p>
                <p class="report-detail">총점: {{ report.totalScore }}점</p>
                <button class="view-button" @click="openModal(report)">
                  바로보기
                </button>
              </div>
            </div>
          </div>
          <button class="nav-button next" @click="slideNext">→</button>
        </div>
      </section>

      <!-- 전체 레포트 섹션 -->
      <section class="all-reports">
        <h2 class="section-title">전체 레포트</h2>
        <div class="filter-buttons">
          <button
            v-for="filter in filters"
            :key="filter"
            :class="['filter-button', { active: selectedFilter === filter }]"
            @click="handleFilterSelect(filter)"
          >
            {{ filter }}
          </button>
        </div>

        <!-- 읽은 레포트 리스트 -->
        <div class="read-reports">
          <div
            v-for="(report, index) in readReports"
            :key="index"
            class="read-report-card"
          >
            <h3 class="read-report-type">{{ report.type }}</h3>
            <p class="read-report-date">{{ report.date }}</p>
          </div>
        </div>
      </section>
    </main>
  </div>
  <Report v-if="isModalOpen" :report="selectedReport" @close="closeModal" />
</template>
<script setup>
import { ref, onMounted } from "vue";
import Report from "@/components/Report.vue";
const slider = ref(null);
const userName = ref("진성일");
// script setup 부분
const selectedReport = ref({
  topic: "",
  date: "",
  totalScore: 0,
  percentile: "",
  categories: [
    {
      name: "논리력",
      score: 29,
      color: "#48BB78",
      description: `현실적인 문제와 대안을 체계적으로 분석했습니다.`,
    },
    {
      name: "표현력",
      score: 18,
      color: "#ED64A6",
      description: `키워드를 활용하여 명확한 메시지를 전달했으나...`,
    },
    {
      name: "전략",
      score: 18,
      color: "#ECC94B",
      description: `주요 논점을 잘 파악하고...`,
    },
    {
      name: "상호작용",
      score: 20,
      color: "#4FD1C5",
      description: `토론 중 상대방을 존중하며...`,
    },
  ],
});

// openModal 함수 수정
const openModal = (report) => {
  document.body.style.overflow = "hidden";
  selectedReport.value = {
    ...report,
    percentile: "상위 15%",
    categories: [
      {
        name: "논리력",
        score: 27,
        color: "#48BB78",
        description: `찬성과 반대 측이 명확한 주장과 근거를 제시함. 사례를 활용하여 주장에 대한 현실적 정당성을 부여함 (예: 부유한 사람들의 행복 지수, 로또 당첨자의 불행 사례). 
데이터 인용이 부족함. 구체적인 통계나 연구 결과를 인용하면 더 설득력이 높아질 것.`,
      },
      {
        name: "표현력",
        score: 25,
        color: "#ED64A6",
        description: `문장이 너무 길거나 복잡하지 않고, 이해하기 쉬움. 강조할 부분이 명확하여 전달력이 좋음. 일부 문장에서 너무 일반적인 표현이 반복됨. "돈이 많으면 기회가 많아진다", "돈이 많다고 무조건 행복한 것은 아니다" 같은 문장은 보다 구체적인 사례나 강한 표현으로 바꿀 필요가 있음.`,
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
  };
  isModalOpen.value = true;
};

// 현재 선택된 메뉴
const selectedMenu = ref("실전 연습");
const isModalOpen = ref(false);

const closeModal = () => {
  document.body.style.overflow = "auto";
  isModalOpen.value = false;
};
const slideNext = () => {
  if (slider.value) {
    const cardWidth = 380; // 카드 너비 + gap
    const visibleCards = 3; // 한 번에 보여질 카드 수
    slider.value.scrollBy({
      left: cardWidth * visibleCards,
      behavior: "smooth",
    });
  }
};

const slidePrev = () => {
  if (slider.value) {
    const cardWidth = 380; // 카드 너비 + gap
    const visibleCards = 3; // 한 번에 보여질 카드 수
    slider.value.scrollBy({
      left: -(cardWidth * visibleCards),
      behavior: "smooth",
    });
  }
};

// 읽지 않은 레포트 데이터
const unreadReports = ref([
  {
    type: "실전 토론 레포트",
    date: "2025.02.21 15:29",
    topic: "행복은 돈으로부터 오는가",
    position: "찬성",
    totalScore: 87,
  },
  {
    type: "실전 연습 레포트",
    date: "2025.02.19 11:15",
    topic: "AI 윤리 문제, 개발자가 책임져야 하는가",
    position: "반대",
    totalScore: 78,
  },
  {
    type: "실전 연습 레포트",
    date: "2025.02.19 11:15",
    topic: "토론 수업을 도입하여야 하는가",
    position: "찬성",
    totalScore: 78,
  },
  {
    type: "실전 연습 레포트",
    date: "2025.02.19 11:15",
    topic: "논리적 사고 연습",
    position: "반대",
    totalScore: 78,
  },
  {
    type: "실전 연습 레포트",
    date: "2025.02.19 11:15",
    topic: "논리적 사고 연습",
    position: "반대",
    totalScore: 78,
  },
  {
    type: "실전 연습 레포트",
    date: "2025.02.19 11:15",
    topic: "논리적 사고 연습",
    position: "반대",
    totalScore: 78,
  },
]);

// 필터 옵션
const filters = ref(["전체", "기초 이론", "실전 연습", "실전 토론"]);
const selectedFilter = ref("실전 연습");

// 읽은 레포트 데이터
const readReports = ref([
  {
    type: "기초 이론 레포트",
    date: "2025.02.17 10:20",
  },
]);

// 메뉴 선택 핸들러
const handleMenuSelect = (menu) => {
  selectedMenu.value = menu;
};

// 필터 선택 핸들러
const handleFilterSelect = (filter) => {
  selectedFilter.value = filter;
};
</script>

<style scoped>
/* 기본 설정 */
.app-container {
  display: flex;
  width: 100%;
  min-height: 100vh;
  font-family: "Pretendard", sans-serif;
  position: relative;
}

.logo {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 1.8rem;
}

/* 프로필 */
.profile {
  display: flex;
  align-items: center;
  margin-bottom: 1.875rem;
}

.profile-image {
  width: 2.5rem;
  width: 2.5rem;
  background-color: #f3f4f6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 1rem;
  margin-right: 0.625rem;
}

.profile-name {
  font-size: 1rem;
  color: #1a1a1a;
  font-weight: 500;
}

/* 메뉴 */
.menu-list {
  display: flex;
  flex-direction: column;
  gap: 0.625rem;
}

.menu-item {
  width: 9.375rem;
  height: 2.5rem;
  border-radius: 0.5rem;
  border: none;
  text-align: left;
  padding: 0 1.25rem;
  font-size: 0.875rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
}

.menu-item:hover {
  background-color: #f3f4f6;
}

.menu-item.active {
  background-color: #ff6b6b;
  color: #ffffff;
}

/* 메인 컨텐츠 */
.main-content {
  flex: 1;
  padding-top: 2.5rem;
  width: calc(100% - 15rem); /* 전체 너비에서 사이드바 너비 제외 */
  min-height: 100vh;
}

.header {
  margin-bottom: 2.5rem;
}

.greeting {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 0.625rem;
}

.feedback {
  font-size: 1rem;
  color: #6b7280;
  margin-bottom: 0.3125rem;
}

.unread-notice {
  font-size: 0.875rem;
  color: #ff6b6b;
}

/* 섹션 타이틀 */
.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 1.25rem;
}

/* 슬라이더 */
.slider-container {
  position: relative;
  padding: 0 3.75rem;
  margin: 0 -2.5rem;
  width: calc(100% + 5rem);
}

.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 2.5rem;
  height: 2.5rem;
  background-color: #ffffff;
  border: 0.0625rem solid #e5e7eb;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.nav-button:hover {
  background-color: #f3f4f6;
}

.nav-button.prev {
  left: 1.25rem;
}
.nav-button.next {
  right: 1.25rem;
}

/* 레포트 카드 */
.report-cards {
  display: flex;
  gap: 1.25rem;
  overflow-x: auto;
  scroll-behavior: smooth;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding: 0.625rem 0;
  width: 100%;
}

.report-cards::-webkit-scrollbar {
  display: none;
}

.report-card {
  position: relative;
  width: 22.5rem;
  min-width: 22.5rem;
  height: 12.5rem;
  background-color: #ffffff;
  border-radius: 0.75rem;
  box-shadow: 0 0.125rem 0.5rem rgba(0, 0, 0, 0.08);
  display: flex;
  transition: transform 0.2s ease;
}

.report-card:hover {
  transform: translateY(-0.125rem);
}

.report-highlight {
  width: 0.25rem;
  height: 100%;
  background-color: #ff6b6b;
  border-radius: 0.125rem 0 0 0.125rem;
}

.report-content {
  padding: 1.25rem;
  flex: 1;
}

.report-type {
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 0.3125rem;
}
.report-date {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 0.9375rem;
}
.report-detail {
  font-size: 0.875rem;
  color: #4b5563;
  margin-bottom: 0.3125rem;
}

/* 버튼 스타일 */
.view-button {
  position: absolute;
  bottom: 1.25rem;
  right: 1.25rem;
  width: 6.25rem;
  height: 2rem;
  background-color: #f3f4f6;
  border: none;
  border-radius: 1rem;
  font-size: 0.875rem;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.2s ease;
}
.view-button:hover {
  background-color: #ff6b6b;
  color: #ffffff;
}

.filter-buttons {
  display: flex;
  gap: 0.625rem;
  margin-bottom: 1.25rem;
}

.filter-button {
  height: 2rem;
  padding: 0 1.25rem;
  border-radius: 1rem;
  border: none;
  background-color: #f3f4f6;
  color: #6b7280;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-button.active {
  background-color: #ff6b6b;
  color: #ffffff;
}

/* 읽은 레포트 */
.read-reports {
  display: flex;
  flex-direction: column;
  gap: 0.625rem;
}

.read-report-card {
  width: 100%;
  max-width: 57.5rem;
  background-color: #ffffff;
  border-radius: 0.75rem;
  padding: 1.25rem;
  box-shadow: 0 0.125rem 0.5rem rgba(0, 0, 0, 0.08);
}

.read-report-type {
  font-size: 1rem;
  color: #6b7280;
  margin-bottom: 0.3125rem;
}

.read-report-date {
  font-size: 0.875rem;
  color: #9ca3af;
}
</style>
