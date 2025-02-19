<template>
  <main class="container mx-auto px-4 py-8">
    <!-- 화제의 토론 -->
    <HotDebate :hot-debate="hotDebate" :total-spectators="totalSpectators" @spectate="handleSpectate" />

    <!-- 주제별 토론 -->
    <DebateCategories :categories="categories" @select-category="handleCategorySelect" />
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { Debate } from "@/types/debate.ts";
import type { DebateCategory } from "@/types/DebateCategory.ts";
import HotDebate from "./HotDebate.vue";
import DebateCategories from "./DebateCategories.vue";

const hotDebate = ref<Debate | null>(null);
const totalSpectators = ref(0);
const categories = ref<DebateCategory[]>([
  {
    id: "politics",
    name: "정치",
    activeDebateCount: 5,
    color: "#4A90E2",
  },
  {
    id: "economics",
    name: "경제",
    activeDebateCount: 3,
    color: "#50E3C2",
  },
  {
    id: "society",
    name: "사회",
    activeDebateCount: 4,
    color: "#F5A623",
  },
  {
    id: "science",
    name: "과학",
    activeDebateCount: 2,
    color: "#7ED321",
  },
]);

// 실제 구현시에는 API 호출로 대체
onMounted(async () => {
  // 데모 데이터
  hotDebate.value = {
    id: "1",
    title: "AI 기술 발전이 일자리에 미치는 영향",
    category: "technology",
    duration: 0,             // 혹은 적절한 값
    spectatorCount: 38,
    participants: 0,         // 혹은 적절한 값
    startTime: "2024-02-15T15:00:00",
    endTime: "",             // 혹은 적절한 값
  };

  totalSpectators.value = 152;
});

const handleSpectate = (debateId: string) => {
  console.log(`Spectating debate: ${debateId}`);
  // 방청 로직 구현
};

const handleCategorySelect = (categoryId: string) => {
  console.log(`Selected category: ${categoryId}`);
  // 카테고리별 토론 목록 조회 로직 구현
};
</script>