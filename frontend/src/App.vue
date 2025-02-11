<script setup>
import Sidebar from "@/components/Sidebar.vue";
</script>

<template>
  <div id="app-container" class="h-screen flex">
    <!-- 사이드바 -->
    <Sidebar class="h-full" />

    <!-- 메인 -->
    <div class="flex-1 flex flex-col">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
#app-container {
  display: flex; /* Sidebar와 Content를 가로로 배치 */
  height: 100vh; /* 전체 화면 높이 */
}

#app-container > .flex-1 {
  background-color: rgb(255, 255, 255); /* 메인 콘텐츠 배경색 */
  overflow-y: auto; /* 스크롤 처리 */
}
</style>
