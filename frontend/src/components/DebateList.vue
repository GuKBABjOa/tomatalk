<template>
  <div class="debate-list">
    <p v-if="isLoading">토론 목록을 불러오는 중...</p>
    <p v-else-if="errorMessage" class="error-message">{{ errorMessage }}</p>

    <div v-else v-for="(debate, index) in filteredDebates" :key="index" class="debate-item">
      <div class="debate-header">
        <h3 class="debate-title">{{ debate.title }}</h3>
        <div class="viewer-count">
          <span class="viewer-icon">👥</span>
          <span class="viewer-number">{{ debate.participants }}명</span>
        </div>
      </div>

      <div class="debate-status">
        <span class="live-badge">
          <span class="red-dot"></span> 실시간 진행 중
        </span>
        <span class="elapsed-time">경과시간: {{ debate.duration }}분</span>
      </div>

      <div class="debate-timing">
        <span class="time-info">⏰ 시작: {{ debate.startTime }}</span>
        <span class="time-info">⏳ 예상 종료: {{ debate.endTime }}</span>
      </div>
    </div>
    <!-- 스크롤 감지를 위한 sentinel 요소 -->
    <div ref="sentinel"></div>

    <p v-if="isFetchingMore">추가 토론 목록을 불러오는 중...</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onBeforeUnmount } from "vue";
import axios from "axios";
import type { Debate } from "@/types/debate";

const debates = ref<Debate[]>([]); // 진행 중인 토론 데이터
const isLoading = ref(true); // 로딩 상태
const isFetchingMore = ref(false); // 추가 데이터 불러오는 중인지 여부
const errorMessage = ref<string | null>(null); // 에러 메시지

// 커서와 사이즈 (cursor는 백엔드에서 제공하는 방식에 맞게 처리)
const cursor = ref<number>(9007199254740991);  // 초기값, Swagger에서 제시한 값
const size = ref<number>(5);  // 한 번에 불러올 데이터 개수


// 참조할 DOM 요소
const listContainer = ref<HTMLElement | null>(null);
const sentinel = ref<HTMLElement | null>(null);


const fetchDebates = async (append: boolean = false) => {
  try {
    // 처음 로딩이 아닌 경우 isFetchingMore를 사용
    if (append) {
      isFetchingMore.value = true;
    } else {
      isLoading.value = true;
    }
    // GET 요청: 백엔드가 GET 요청에서 body를 받지 않는다면 query parameters를 사용
    const response = await axios.get("http://localhost:8080/api/debates", {
      params: {
        cursor: cursor.value,
        size: size.value,
        status: "COMPLETED"
      },
    });
    // 응답 형식: { content: [ ... ] }
    const fetchedDebates = response.data.content;
    console.log(fetchedDebates);

    const transformedDebates = fetchedDebates.map((debate: any) => {
      const title = debate.subject;
      console.log(title);

      const participants = debate.spectatorsCount;
      console.log(participants);

      const startTime = `${debate.startedAtHour}:${String(debate.startedAtMinute).padStart(2, '0')}`;
      console.log(startTime);

      let hour = debate.startedAtHour;
      let minute = debate.startedAtMinute + debate.estimatedTimeMinute;
      hour += Math.floor(minute / 60);
      minute = minute % 60;
      const endTime = `${hour}:${String(minute).padStart(2, '0')}`;
      console.log(endTime);

      // 현재 시간과 debate의 시작 시간 차이를 분 단위로 계산
      const now = new Date();
      const debateStart = new Date();
      // 오늘 날짜에 debate의 시작 시간을 설정 (초, 밀리초는 0)
      debateStart.setHours(debate.startedAtHour, debate.startedAtMinute, 0, 0);
      // 차이를 분 단위로 계산
      let duration = Math.floor((now.getTime() - debateStart.getTime()) / 60000);
      // 만약 아직 시작 전이라면 duration이 음수가 될 수 있으므로 0으로 처리
      if (duration < 0) {
        duration = 0;
      }

      return {
        ...debate,
        title,           // 새로 매핑된 제목
        participants,    // 새로 매핑된 참여자 수
        startTime,
        endTime,
        duration,
      };
    });

    // 커서 업데이트: 마지막 요소의 debateId (또는 백엔드에서 제공하는 다음 커서 값)
    if (fetchedDebates && fetchedDebates.length > 0) {
      // 예시: 마지막 debate의 debateId를 숫자로 변환해서 사용 (백엔드에 맞게 수정 필요)
      const lastDebate = fetchedDebates[fetchedDebates.length - 1];
      cursor.value = Number(lastDebate.debateId);
    }

    // append 옵션에 따라 기존 데이터에 추가하거나 초기화
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
// IntersectionObserver를 사용하여 sentinel이 보이면 추가 데이터를 요청
let observer: IntersectionObserver | null = null;
const filteredDebates = computed(() => debates.value);
const createObserver = () => {
  if (sentinel.value) {
    observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        // sentinel이 보이면 추가 데이터 요청 (debates 배열 길이가 size보다 많거나 같을 때)
        if (entry.isIntersecting && debates.value.length >= size.value) {
          fetchDebates(true);
        }
      });
    }, {
      root: listContainer.value,
      threshold: 1.0,
    });
    observer.observe(sentinel.value);
  }
};

onMounted(() => {
  fetchDebates();
  createObserver();
});

onBeforeUnmount(() => {
  if (observer && sentinel.value) {
    observer.unobserve(sentinel.value);
  }
});
</script>

<style scoped>
/* 전체 리스트 스타일 */
.debate-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* 개별 토론 카드 스타일 */
.debate-item {
  background: white;
  padding: 1.5rem;
  border-radius: 1rem;
  border: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  transition: all 0.3s ease-in-out;
}

.debate-item:hover {
  border-color: #2563eb;
  cursor: pointer;
}

/* 헤더 스타일 (제목 & 참여자 수) */
.debate-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.debate-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1f2937;
}

/* 참여자 수 스타일 */
.viewer-count {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
  color: #2563eb;
}

.viewer-icon {
  margin-right: 4px;
}

/* 실시간 진행 중 상태 */
.debate-status {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.9rem;
  color: #4b5563;
}

/* "실시간 진행 중" 배지 */
.live-badge {
  display: flex;
  align-items: center;
  background: rgba(83, 127, 247, 0.15);
  color: blue;
  padding: 4px 8px;
  border-radius: 8px;
}

.red-dot {
  width: 8px;
  height: 8px;
  background: #ff3b3b;
  border-radius: 50%;
  margin-right: 5px;
}

/* 경과시간 스타일 */
.elapsed-time {
  font-size: 0.9rem;
  color: #6b7280;
}

/* 시간 정보 (시작 & 예상 종료) */
.debate-timing {
  display: flex;
  gap: 1.5rem;
  font-size: 0.9rem;
  color: #4b5563;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
