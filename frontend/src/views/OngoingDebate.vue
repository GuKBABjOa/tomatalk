<template>
  <div class="app">

    <main class="main-content">
      <h1 class="title">진행 중인 토론</h1>
      <p class="subtitle">현재 진행 중인 토론 목록입니다.</p>
      <!-- SearchBar 내부에서 정렬 기능 포함 -->
      <SearchBar v-model:query="searchQuery" :sortOption="sortOption" @update:sort="sortOption = $event"
        :selectedCategory="selectedCategory" @update:category="selectedCategory = $event"
        placeholder="토론 주제, 키워드로 검색..." :sortOptions="[
          { value: 'latest', label: '최신순' },
          { value: 'popular', label: '인기순' },
        ]" :categories="[
          { value: 'politics', label: '정치/국제' },
          { value: 'economy', label: '경제/노동' },
          { value: 'ethics', label: '윤리/법' },
          { value: 'science', label: '과학/기술' },
          { value: 'education', label: '교육/사회' }
        ]" />


      <!-- 토론 카드 리스트 -->
      <div class="debate-list">
        <p v-if="isLoading">토론 목록을 불러오는 중...</p>
        <p v-else-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div v-else>
          <DebateCard v-for="(debate, index) in filteredDebates" :key="index" :debate="debate" />
        </div>
        <!-- 무한 스크롤용 sentinel 요소 -->
        <div ref="sentinel"></div>
        <p v-if="isFetchingMore">추가 토론 목록을 불러오는 중...</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onBeforeUnmount, watch } from "vue";
import axios from "axios";
import SearchBar from "@/components/SearchBar.vue";
import DebateCard from "@/components/DebateCard.vue";
import type { Debate } from "@/types/debate";

// SearchBar에서 사용하는 값
const searchQuery = ref("");
const sortOption = ref("latest");
//sortOption을 매핑핑
const sortMapping: Record<string, string> = {
  latest: "LATEST",
  popular: "POPULARITY"  // popular 옵션은 POPULARITY로 변환
};

// 토론 데이터 및 상태 변수들
const debates = ref<Debate[]>([]);
const isLoading = ref(true);
const isFetchingMore = ref(false);
const errorMessage = ref<string | null>(null);

// 커서와 한 번에 불러올 데이터 개수
const cursor = ref<number>(9007199254740991); // 초기값 (예시)
const size = ref<number>(5);

// 선택된 카테고리 (필요하다면 SearchBar와 연동하거나 기본값 사용)
const selectedCategory = ref(["politics"]);


// 무한 스크롤 관련 DOM 요소
const listContainer = ref<HTMLElement | null>(null);
const sentinel = ref<HTMLElement | null>(null);

// 토론 데이터를 불러오는 API 함수
const fetchDebates = async (append: boolean = false) => {
  try {
    if (append) {
      isFetchingMore.value = true;
    } else {
      isLoading.value = true;
      // 검색 또는 정렬 옵션이 변경되면 cursor 초기화
      cursor.value = 9007199254740991;
    }
    //카테고리를 여러개 선택하면 배열 -> 배열 처리하는 부분
    const categoriesParam =
      selectedCategory.value.length > 0
        ? selectedCategory.value.map((cat: string) => cat.toUpperCase()).join(",")
        : undefined;

    const response = await axios.get("http://localhost:8080/api/debates", {
      params: {
        cursor: cursor.value,
        size: size.value,
        status: "IN_PROGRESS",
        sortBy: sortOption.value ? sortMapping[sortOption.value] : undefined,
        categories: categoriesParam,
        // searchQuery가 있을 경우 추가 파라미터로 전달 (API 스펙에 맞게 조정)
        query: searchQuery.value ? searchQuery.value : undefined,
      },
    });

    const fetchedDebates = response.data.content;

    const transformedDebates = fetchedDebates.map((debate: any) => {
      const title = debate.subject;
      const participants = debate.spectatorsCount;
      const startTime = `${debate.startedAtHour}:${String(debate.startedAtMinute).padStart(2, '0')}`;

      // 종료 시간 계산
      let hour = debate.startedAtHour;
      let minute = debate.startedAtMinute + debate.estimatedTimeMinute;
      hour += Math.floor(minute / 60);
      minute = minute % 60;
      const endTime = `${hour}:${String(minute).padStart(2, '0')}`;

      // 경과 시간 계산 (분 단위)
      const now = new Date();
      const debateStart = new Date();
      debateStart.setHours(debate.startedAtHour, debate.startedAtMinute, 0, 0);
      let duration = Math.floor((now.getTime() - debateStart.getTime()) / 60000);
      if (duration < 0) {
        duration = 0;
      }

      return {
        ...debate,
        title,
        participants,
        startTime,
        endTime,
        duration,
      };
    });

    // 커서 업데이트: 마지막 토론의 debateId (백엔드 스펙에 맞게 수정)
    if (fetchedDebates && fetchedDebates.length > 0) {
      const lastDebate = fetchedDebates[fetchedDebates.length - 1];
      cursor.value = Number(lastDebate.debateId);
    }

    if (append) {
      debates.value = debates.value.concat(transformedDebates);
    } else {
      debates.value = transformedDebates;
    }
  } catch (error) {
    console.error("Error fetching debates:", error);
    errorMessage.value = "토론 목록을 불러오지 못했습니다.";
  } finally {
    isLoading.value = false;
    isFetchingMore.value = false;
  }
};

// 필터링된 토론 목록 (필요에 따라 추가 필터링 로직 구현 가능)
const filteredDebates = computed(() => debates.value);

// IntersectionObserver로 무한 스크롤 구현
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

// 초기 데이터 로드
onMounted(() => {
  fetchDebates();
  createObserver();
});

// 화면 종료 시 Observer 해제
onBeforeUnmount(() => {
  if (observer && sentinel.value) {
    observer.unobserve(sentinel.value);
  }
});

// 검색어나 정렬 옵션 변경 시 데이터 새로 불러오기
watch([searchQuery, sortOption, selectedCategory], () => {
  // 옵션 변경 시 기존 데이터를 초기화하고 새로 불러옴
  fetchDebates(false);
}, { deep: true });
</script>

<style scoped>
/* 제목과 검색바 간격 조절 */
.header-section {
  margin-bottom: 1.5rem;
  /* 검색창과의 거리 */
}

/* 제목 스타일 */
.title {
  font-size: 1.75rem;
  /* 기존보다 크게 */
  font-weight: 700;
  /* 더 굵게 */
  color: #1f2937;
  /* 어두운 색 */
  margin-bottom: 0.25rem;
  /* 부제목과 간격 조정 */
}

/* 부제목 스타일 */
.subtitle {
  font-size: 1rem;
  color: #6b7280;
  /* 회색 톤 */
  margin-bottom: 1.5rem;
  /* 검색창과 간격 조정 */
}

.app {
  display: flex;
  background-color: #f1f5f9;
  min-height: 100vh;
}

.main-content {
  margin-left: 280px;
  padding: 2rem;
  flex-grow: 1;
  height: 100vh;
  overflow: auto;
}

.grid-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.card {
  background: white;
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.chart-container {
  height: 300px;
  width: 100%;
}

.discussion-card {
  background: #f8fafc;
  padding: 1rem;
  border-radius: 0.75rem;
}
</style>