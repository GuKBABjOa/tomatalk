<template>
  <div class="layout">
    <!-- 헤더 -->
    <header class="h-14 flex items-center px-4">
      <template v-if="headerType === 'back'">
        <button @click="goBack" class="button">
          <img src="@/assets/Back.svg" alt="Back Icon" class="icon" />
        </button>
      </template>
    </header>

    <!-- 메인 콘텐츠 -->
    <main class="main-content">
      <slot></slot>
    </main>

    <!-- 네브바 -->
    <NavBar v-if="showNavBar" />
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router';
import { computed } from 'vue';
import NavBar from '@/components/NavBar.vue';

const router = useRouter();
const route = useRoute();

// meta 정보를 computed로 가져와서 반응형으로 사용
const headerType = computed(() => route.meta.headerType || 'none');
const showNavBar = computed(() => route.meta.showNavBar !== false);

const goBack = () => {
  router.back();
};
</script>

<style scoped>
.button {
  all: unset;
  cursor: pointer;
}

.layout {
  display: flex;
  flex-direction: column;
  height: 100vh; /* 화면 전체 높이 */
  padding-bottom: 60px; /* 네비게이션 바 높이만큼 여백 */
  overflow: hidden; /* 스크롤바를 layout 외부로 제한 */
}

.main-content {
  flex: 1; /* 메인 콘텐츠가 남은 공간을 차지 */
  overflow: auto; /* 스크롤 가능 */
  margin-top: 60px; /* 헤더 높이만큼 여백 추가 */
}


.icon {
  width: 25px;
  height: 25px;
  padding-top: 10px;
}

/* 헤더 정렬 및 패딩 */
header {
  display: flex; /* 플렉스 박스 */
  align-items: center; /* 수직 정렬 */
  justify-content: space-between; /* 양쪽 끝으로 정렬 */
  padding: 10px 0 0 16px; /* 좌우 여백 추가 */
}
</style>
