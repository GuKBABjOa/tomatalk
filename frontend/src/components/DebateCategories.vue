# DebateCategories.vue
<template>
  <div class="debate-container">
    <h2 class="title">🎯 진행 중인 토론을 주제별로 모아놨어요</h2>

    <div class="content-wrapper">
      <!-- 왼쪽 버블 영역 -->
      <div class="bubbles-container">
        <div v-for="category in categories" :key="category.value" class="category-bubble"
          :class="{ selected: selectedCategory === category.value }" @click="selectCategory(category.value)">
          <div class="bubble-content">
            <span class="category-name">{{ category.label }}</span>
            <span class="debate-count">{{ categoryStats[category.value]?.debateCount || 0 }}개
              진행중</span>
          </div>
        </div>
      </div>

      <!-- 오른쪽 리스트 영역 -->
      <div class="debates-container">
        <template v-if="selectedCategory">
          <div class="debate-list">
            <div v-if="isLoading" class="loading">
              토론 목록을 불러오는 중...
            </div>
            <div v-else-if="errorMessage" class="error-message">
              {{ errorMessage }}
            </div>
            <template v-else>
              <div v-for="debate in debates" :key="debate.debateId" class="debate-item">
                <span class="debate-label">{{
                  getCurrentCategoryLabel()
                }}</span>
                <span class="debate-title">{{ debate.subject }}</span>
                <span class="participants">{{ formatNumber(debate.spectatorsCount) }}명 참여</span>
                <button @click="enterDebate(debate.debateId)" class="enter-button">
                  방청하기
                </button>
              </div>
            </template>
          </div>
          <div ref="sentinel"></div>
          <div v-if="isFetchingMore" class="loading">
            추가 토론 목록을 불러오는 중...
          </div>
        </template>
        <div v-else class="empty-state">카테고리를 선택해주세요</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import axios from "axios";
const backendUrl = import.meta.env.VITE_BACKEND_URL;
// 타입 정의
interface DebateInfoResponse {
  debateId: string;
  category: string;
  subject: string;
  status: string;
  spectatorsCount: number;
  startedAtHour: number; // 추가
  startedAtMinute: number; // 추가
  estimatedTimeMinute: number; // 추가
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
const cursor = ref<number | null>(null);
const size = ref<number>(5);
const selectedCategory = ref<string | null>("POLITICS");
const sentinel = ref<HTMLElement | null>(null);

// 카테고리 정의
const categories = [
  { value: "POLITICS", label: "정치", color: "#ff6b6b" },
  { value: "ECONOMY", label: "경제", color: "#ffd43b" },
  { value: "ETHICS", label: "윤리", color: "#69db7c" },
  { value: "SCIENCE", label: "과학", color: "#4dabf7" },
  { value: "EDUCATION", label: "사회", color: "#748ffc" },
];

const enterDebate = async (debateId: string) => {
  try {
    // TODO: 소켓 연결 로직 구현
    console.log(`Connecting to debate: ${debateId}`);
    window.location.href = `/debate`;
    // 여기에 소켓 연결 로직이 들어갈 예정
    // 1. 소켓 연결
    // 2. 방청자 정보 전송
    // 3. 실시간 토론 데이터 수신 시작
  } catch (error) {
    console.error("Failed to connect to debate:", error);
    // 에러 처리 로직 추가 예정
  }
};
// API 호출 함수
const fetchDebates = async (append: boolean = false) => {
  try {
    if (!selectedCategory.value) return;

    if (append) {
      isFetchingMore.value = true;
    } else {
      isLoading.value = true;
    }

    const response = await axios.get<CursorResponse<DebateInfoResponse>>(
      backendUrl + "/api/debates",
      {
        params: {
          cursor: cursor.value,
          size: size.value,
          status: "IN_PROGRESS",
          categories: selectedCategory.value,
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
      debateCount: 0,
    };
  });

  debates.value.forEach((debate) => {
    if (stats[debate.category]) {
      stats[debate.category].debateCount++;
    }
  });

  return stats;
});

// 유틸리티 함수들
const formatNumber = (num: number) => {
  return num.toLocaleString("ko-KR");
};

const getCurrentCategoryLabel = () => {
  const category = categories.find((c) => c.value === selectedCategory.value);
  return category ? category.label : "전체";
};

const selectCategory = (category: string) => {
  selectedCategory.value = category;
  fetchDebates();
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
        threshold: 1.0,
      }
    );
    observer.observe(sentinel.value);
  }
};

// 컴포넌트 라이프사이클 훅
onMounted(() => {
  createObserver();
});

onBeforeUnmount(() => {
  if (observer && sentinel.value) {
    observer.unobserve(sentinel.value);
  }
});
</script>

<style scoped>
.debate-container {
  padding: 2rem;
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 2rem;
}

.content-wrapper {
  display: flex;
  gap: 2rem;
}

/* 왼쪽 버블 영역 */
.bubbles-container {
  flex: 1;
  position: relative;
  height: 600px;
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-content: flex-start;
}

.category-bubble {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background-color: rgba(255, 107, 107, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 1rem;
}

.category-bubble:hover {
  transform: scale(1.05);
}

.category-bubble.selected {
  box-shadow: 0 0 0 4px rgba(255, 107, 107, 0.5);
}

.bubble-content {
  text-align: center;
}

.category-name {
  display: block;
  color: #ff6b6b;
  font-weight: bold;
  font-size: 1.125rem;
  margin-bottom: 0.5rem;
}

.debate-count {
  display: block;
  color: #666;
  font-size: 0.875rem;
}

/* 오른쪽 리스트 영역 */
.debates-container {
  flex: 1;
  padding: 1rem;
}

.debate-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.debate-item {
  background-color: #f5f5f5;
  padding: 1rem;
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.debate-label {
  background-color: #ff6b6b;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.875rem;
}

.debate-title {
  flex-grow: 1;
  font-weight: bold;
  color: #333;
}

.participants {
  color: #666;
  font-size: 0.875rem;
}

.empty-state {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 1rem;
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

.enter-button {
  font-family: inherit;
  background-color: #ff4b4b;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  border: none;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
}

.enter-button:hover {
  background-color: #ff3333;
}
</style>
