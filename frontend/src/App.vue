<script setup>
import { computed } from "vue"
import { useRoute } from "vue-router"
import Sidebar from "@/components/Sidebar.vue";

const route = useRoute()

// Sidebar를 숨겨야 하는 경로 확인, 토론 페이지에선 사이드바가 없다.
const hideSidebar = computed(() => {
  return route.path.startsWith("/debateroom/")
})
</script>

<template>
  <div id="app-container" class="h-screen flex">
    <!-- 사이드바 (조건부 렌더링) -->
    <Sidebar v-if="!hideSidebar" class="h-full" />

    <!-- 메인 -->
    <div class="flex-1 flex flex-col">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
#app-container {
  display: flex;
  /* Sidebar와 Content를 가로로 배치 */
  height: 100vh;
  /* 전체 화면 높이 */
}

#app-container>.flex-1 {
  background-color: rgb(255, 255, 255);
  /* 메인 콘텐츠 배경색 */
  overflow-y: auto;
  /* 스크롤 처리 */
}
</style>
