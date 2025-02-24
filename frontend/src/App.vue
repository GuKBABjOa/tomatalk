<template>
  <div class="app-container flex justify-center">
    <!-- 전체를 감싸는 컨테이너 추가 -->
    <div class="app">
      <Navigation class="nav" v-if="!hideNavbar" />
      <div class="app-content" :class="{ 'no-navbar': hideNavbar }">
        <main>
          <router-view :userName="userName" />
        </main>
      </div>
      <Footer v-if="!hideNavbar" />
    </div>

    <!-- 매칭 완료 모달 -->
    <div v-if="matchingStore.isMatched" class="matching-modal">
      <MatchingCompleteModal :initialCountdown="5" :debateId="matchingStore.matchingDetails.debateId"
        :participantsData="matchingStore.matchingDetails.participants" @close="handleClose"
        @startDiscussion="startDiscussion" @resetMatching="resetMatching" />
    </div>
  </div>
</template>

<script setup lang="ts">
import Navigation from "@/components/Navigation.vue";
import MatchingCompleteModal from "./components/MatchingCompleteModal.vue";
import Footer from "@/components/Footer.vue";
import { useMatchingStore } from "@/stores/matchingStore";
import { useRouter, useRoute } from "vue-router";
import { ref } from "vue";
import { computed } from "vue";

const matchingStore = useMatchingStore();
const router = useRouter();
// 매칭 상태 초기화
const resetMatching = () => {
  matchingStore.matchingDetails.participants = []; // ✅ 참가자 리스트 초기화
  matchingStore.isMatching = false; // ✅ 매칭 중 상태 초기화
  matchingStore.isMatched = false; // ✅ 매칭 완료 상태 초기화
  console.log("매칭 상태가 초기화되었습니다.");
};
const userName = ref<string>("김토론");
// 토론 시작 시 라우팅
const startDiscussion = () => {
  const debateId = matchingStore.matchingDetails.debateId;
  resetMatching(); // 매칭 상태 초기화 후 이동
  router.push({
    name: "debatePrepare",
    params: { debateId: debateId }, // debateId 전달
  });
};

const handleClose = () => {
  matchingStore.isMatched = false;
};

const route = useRoute();
const hiddenPaths = ["/debate/", "/debate-prepare/"];

const hideNavbar = computed(() => {
  return hiddenPaths.some((path) => route.path.startsWith(path));
});
</script>

<style>
@import url("https://fonts.googleapis.com/css2?family=Pretendard:wght@400;600;800&display=swap");

html {
  font-size: 12px;
  /* 기본 16px의 75%로 줄이기 */
}

.app-container {
  box-sizing: border-box;
  position: relative;
  width: 100%;
  /* 화면 전체를 사용 */
  min-height: 100vh;
  display: flex;
  justify-content: center;
  /* 수평 중앙 정렬 */
  align-items: center;
  /* 수직 중앙 정렬 */
}

.nav {
  width: 100%;
  /* 원하는 너비 */
}

.app {
  position: relative;
  width: 75%;
  /* 원하는 너비 */
  max-width: 1280px;


  font-family: "Pretendard", sans-serif;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-content {
  padding-top: 60px;
  /* Match the height of the navigation bar */
}

main {
  flex: 1;
}

.matching-container {
  font-family: "Pretendard", sans-serif;
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
}

.matching-modal {
  font-family: "Pretendard", sans-serif;
}

.no-navbar {
  padding-top: 0;
}
</style>
