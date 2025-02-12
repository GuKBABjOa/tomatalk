<template>
  <div class="app">

    <main class="main-content">
      <h1 class="title">진행 중인 토론</h1>
      <p class="subtitle">현재 진행 중인 토론 목록입니다.</p>
      <!-- SearchBar 내부에서 정렬 기능 포함 -->
      <SearchBar v-model="searchQuery" :sortOption="sortOption" @update:sortOption="sortOption = $event"
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


      <DebateList :searchQuery="searchQuery" :sortOption="sortOption" />
    </main>
  </div>
</template>

<script setup>
import { ref } from "vue";
//import Sidebar from "@/components/Sidebar.vue";
import SearchBar from "@/components/SearchBar.vue";
import DebateList from "@/components/DebateList.vue";

const searchQuery = ref("");
const sortOption = ref("latest");
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