<template>
  <nav class="h-16 custom-nav">
    <div class="custom-nav-inner">
      <router-link to="/" class="custom-link">
        <img src="@/assets/Home.svg" alt="Home Icon" class="nav-icon" :class="{ 'active-icon': route.path === '/' }" />
      </router-link>
      <router-link to="/" class="custom-link">
        <img src="@/assets/Play.svg" alt="Play Icon" class="nav-icon" :class="{ 'active-icon': route.path === '/' }" />
      </router-link>
      <router-link to="/" class="custom-link">
        <img src="@/assets/User.svg" alt="User Icon" class="nav-icon w-6 h-6" :class="{ 'active-icon': route.path === '/' }" />
      </router-link>
    </div>
  </nav>
</template>

<script setup>
import { useRoute } from 'vue-router';

const route = useRoute();
</script>

<style scoped>
/* 네비게이션 바 스타일 */
.custom-nav {
  position: fixed; /* 네비게이션 바를 화면 하단에 고정 */
  bottom: 0; /* 하단 기준 위치 */
  left: 50%; /* 수평 중앙 정렬 */
  transform: translateX(-50%); /* 정확히 가운데 위치하도록 이동 */
  width: 100%; /* 화면 전체 너비 */
  max-width: 600px; /* 최대 너비 제한 */
  height: 60px; /* 네비게이션 바 높이 */
  box-sizing: border-box; /* 패딩과 테두리를 크기에 포함 */
  padding: 0 60px;
  background-color: #ffffff;
  border-top: 0.2px solid rgb(230, 229, 229);
}

/* 네비게이션 내부 정렬 */
.custom-nav-inner {
  display: flex; /* 플렉스 컨테이너로 설정 */
  justify-content: space-between; /* 자식 요소들을 양쪽 끝과 중앙에 정렬 */
  height: 100%; /* 부모의 높이를 모두 채움 */
}

/* 네비게이션 링크 스타일 */
.custom-link {
  transition: transform 0.2s, background-color 0.2s; /* 호버 시 애니메이션 추가 */
  display: flex; /* 플렉스 컨테이너로 설정 */
  align-items: center; /* 자식 요소를 수직 중앙 정렬 */
}

/* 네비게이션 링크 호버 효과 */
.custom-link:hover {
  background-color: rgba(255, 255, 255, 0.2); /* 밝은 배경 추가 */
}

/* 링크 아이콘 스타일 */
.nav-icon {
  display: block; /* 이미지가 블록 요소로 취급되어 여백 제거 */
  width: 26px; /* 아이콘 너비 */
  height: 26px; /* 아이콘 높이 */
  opacity: 0.5; /* 기본적으로 흐리게 표시 */
  transition: opacity 0.2s ease-in-out; /* 부드러운 전환 효과 */
}

/* 활성화된 아이콘 스타일 */
.active-icon {
  opacity: 1; /* 활성화된 아이콘은 뚜렷하게 표시 */
}
</style>
