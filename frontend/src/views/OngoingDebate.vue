// DebateBubbleDashboard.vue
<template>
  <div class="debate-dashboard">
    <header class="debate-header">
      <h1 class="title">진행중인 토론</h1>
      <p class="subtitle">다양한 주제의 토론에 참여하고 의견을 나눠보세요</p>
    </header>

    <main class="debate-content">
      <!-- 버블 섹션 -->
      <section class="debate-bubbles">
        <div v-for="(category, index) in processedCategories" :key="category.value" class="bubble" :style="{
          backgroundColor: category.color,
          width: `${category.size}px`,
          height: `${category.size}px`,
          left: getBubblePosition(index).x + 'px',
          top: getBubblePosition(index).y + 'px',
        }" @click="selectCategory([category.value])">
          <div class="bubble-content">
            <div class="category-title">{{ category.label }}</div>
            <div class="debate-count">{{ category.debateCount }}개 토론</div>
            <div class="viewer-count">
              {{ formatNumber(category.totalViewers) }}명 관전
            </div>
          </div>
        </div>
      </section>

      <!-- 토론 목록 섹션 -->
      <section class="debate-list">
        <div class="category-header">
          <h2>{{ getCurrentCategoryLabel() }} 토론</h2>
          <div class="sort-options">
            <select v-model="sortOption" class="sort-select">
              <option value="latest">최신순</option>
              <option value="popular">인기순</option>
            </select>
          </div>
        </div>

        <div class="debate-cards">
          <div v-if="isLoading" class="loading">토론 목록을 불러오는 중...</div>
          <div v-else-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>
          <template v-else>
            <div v-for="debate in debates" :key="debate.debateId" class="debate-card">
              <h3>{{ debate.subject }}</h3>
              <p>{{ formatNumber(debate.spectatorsCount) }}명 관전중</p>
              <p>
                시작:
                {{
                  getFormattedTime(debate.startedAtHour, debate.startedAtMinute)
                }}
              </p>
              <p>예상 소요 시간: {{ debate.estimatedTimeMinute }}분</p>
              <span v-if="isPopularDebate(debate)" class="popular-badge">실시간 인기</span>
              <button @click="enterDebate(debate.debateId)" class="enter-button">
                방청하기
              </button>
            </div>
          </template>
          <div ref="sentinel"></div>
          <div v-if="isFetchingMore" class="loading">
            추가 토론 목록을 불러오는 중...
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";
import axios from "axios";

const backendUrl = import.meta.env.VITE_BACKEND_URL;

// 타입 정의
interface DebateInfoResponse {
  debateId: string;
  category: string;
  subject: string;
  status: string;
  startedAtHour: number;
  startedAtMinute: number;
  estimatedTimeMinute: number;
  spectatorsCount: number;
}

interface CursorResponse<T> {
  content: T[];
  size: number;
  first: boolean;
  last: boolean;
}

// 상태 관리
const debates = ref<DebateInfoResponse[]>([]);
const isLoading = ref(true);
const isFetchingMore = ref(false);
const errorMessage = ref<string | null>(null);
const sortOption = ref("latest");
const cursor = ref<number>(9007199254740991);
const size = ref<number>(5);
const selectedCategory = ref(["POLITICS"]);

// 무한 스크롤 관련
const sentinel = ref<HTMLElement | null>(null);
const listContainer = ref<HTMLElement | null>(null);

// 카테고리 정의
const categories = [
  { value: "POLITICS", label: "정치/국제", color: "#ff6b6b", size: 80 },
  { value: "ECONOMY", label: "경제/노동", color: "#ffd43b", size: 80 },
  { value: "ETHICS", label: "윤리/법", color: "#69db7c", size: 80 },
  { value: "SCIENCE", label: "과학/기술", color: "#4dabf7", size: 80 },
  { value: "EDUCATION", label: "교육/사회", color: "#748ffc", size: 80 },
];

const enterDebate = (debateId: string) => {
  window.location.href = "/";
};

// 정렬 옵션 매핑
const sortMapping: Record<string, string> = {
  latest: "LATEST",
  popular: "POPULARITY",
};

// API 호출 함수
const fetchDebates = async (append: boolean = false) => {
  try {
    if (append) {
      isFetchingMore.value = true;
    } else {
      isLoading.value = true;
      cursor.value = 9007199254740991;
    }

    const categoriesParam = selectedCategory.value.join(",");

    const response = await axios.get<CursorResponse<DebateInfoResponse>>(
      backendUrl + "/api/debates", // 전체 URL 사용
      {
        params: {
          cursor: cursor.value,
          size: size.value,
          status: "IN_PROGRESS",
          sortBy: sortMapping[sortOption.value],
          categories: categoriesParam,
        },
      }
    );

    const fetchedDebates = response.data.content;

    if (fetchedDebates && fetchedDebates.length > 0) {
      cursor.value = Number(fetchedDebates[fetchedDebates.length - 1].debateId);
      debates.value = append
        ? [...debates.value, ...fetchedDebates]
        : fetchedDebates;
    }
  } catch (error) {
    errorMessage.value = "토론 목록을 불러오지 못했습니다.";
  } finally {
    isLoading.value = false;
    isFetchingMore.value = false;
  }
};

// 카테고리별 통계 계산
const categoryStats = computed(() => {
  const stats: Record<string, any> = {};
  categories.forEach((category) => {
    stats[category.value] = {
      ...category,
      debateCount: 0,
      totalViewers: 0,
    };
  });

  debates.value.forEach((debate) => {
    if (stats[debate.category]) {
      stats[debate.category].debateCount++;
      stats[debate.category].totalViewers += debate.spectatorsCount;
    }
  });

  return stats;
});

// 버블 크기와 색상이 처리된 카테고리 데이터
const processedCategories = computed(() => {
  const categoryArray = Object.values(categoryStats.value);
  categoryArray.sort((a, b) => b.totalViewers - a.totalViewers);

  const maxViewers = Math.max(...categoryArray.map((c) => c.totalViewers || 0));
  const maxSize = 160;
  const minSize = 80;

  return categoryArray.map((category, index) => ({
    ...category,
    size: category.totalViewers
      ? Math.max(minSize, (category.totalViewers / maxViewers) * maxSize)
      : minSize,
  }));
});

// 유틸리티 함수들
const formatNumber = (num: number) => {
  return num.toLocaleString("ko-KR");
};

const getFormattedTime = (hour: number, minute: number) => {
  return `${hour}:${String(minute).padStart(2, "0")}`;
};

const getBubblePosition = (index: number) => {
  const row = Math.floor(index / 2);
  const col = index % 2;
  return {
    x: 100 + col * 200,
    y: 100 + row * 180,
  };
};

const getCurrentCategoryLabel = () => {
  const category = categories.find(
    (c) => c.value === selectedCategory.value[0]
  );
  return category ? category.label : "전체";
};

const isPopularDebate = (debate: DebateInfoResponse) => {
  return debate.spectatorsCount >= 50;
};

const selectCategory = (category: string[]) => {
  selectedCategory.value = category;
};

// IntersectionObserver 설정
let observer: IntersectionObserver | null = null;
const createObserver = () => {
  if (sentinel.value) {
    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting && debates.value.length >= size.value) {
            fetchDebates(true);
          }
        });
      },
      {
        root: listContainer.value,
        threshold: 1.0,
      }
    );
    observer.observe(sentinel.value);
  }
};

// 컴포넌트 라이프사이클 훅
onMounted(() => {
  fetchDebates();
  createObserver();
});

onBeforeUnmount(() => {
  if (observer && sentinel.value) {
    observer.unobserve(sentinel.value);
  }
});

// 워쳐
watch(
  [sortOption, selectedCategory],
  () => {
    fetchDebates(false);
  },
  { deep: true }
);
</script>

<style scoped>
.debate-dashboard {
  padding: 2rem;
  background-color: #f8fafc;
}

.debate-header {
  background: white;
  border-radius: 16px;
  padding: 10px 40px;
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 16px;
  color: #6b7280;
}

.debate-content {
  display: flex;
  background: white;
  border-radius: 16px;
  overflow: hidden;
}

.debate-bubbles {
  flex: 1;
  padding: 20px;
  position: relative;
  height: 600px;
  background: #ffffff;
}

.bubble {
  position: absolute;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.bubble:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.2);
}

.bubble-content {
  text-align: center;
  color: white;
  padding: 10px;
}

.category-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
}

.debate-count {
  font-size: 14px;
  margin-bottom: 4px;
}

.viewer-count {
  font-size: 12px;
}

.debate-list {
  flex: 1;
  padding: 40px;
  border-left: 1px solid #e5e7eb;
  overflow-y: auto;
  height: 600px;
  scrollbar-width: none;
  /* Firefox */
  -ms-overflow-style: none;
  /* IE and Edge */
}

.debate-list::-webkit-scrollbar {
  display: none;
  /* Chrome, Safari, Opera */
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.sort-select {
  padding: 8px 16px;
  border-radius: 8px;
  background-color: white;
  color: #374151;
  cursor: pointer;
}

.debate-card {
  background: rgb(249, 249, 249);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  position: relative;
  transition: all 0.3s ease;
}

.debate-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.enter-button {
  position: absolute;
  bottom: 20px;
  right: 20px;
  padding: 8px 20px;
  background-color: rgb(249, 249, 249);
  color: #374151;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.enter-button:hover {
  background-color: white;
  color: #111827;
  border-color: #d1d5db;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.popular-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  background: #ff6b6b;
  color: white;
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(255, 107, 107, 0.2);
}

.loading,
.error-message {
  text-align: center;
  padding: 20px;
  color: #6b7280;
}

.error-message {
  color: #ef4444;
}
</style>
