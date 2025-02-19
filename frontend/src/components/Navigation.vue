<template>
  <nav class="nav">
    <div class="logo">
      <router-link to="/" class="home-link">
        <div class="logo-circle">
          <div class="logo-inner-circle"></div>
          <div class="logo-core"></div>
        </div>
        <span class="logo-text">토마톡</span>
      </router-link>
    </div>

    <div class="menu">
      <router-link to="/practice-main" class="menu-item">토론 연습하기</router-link>
      <div v-if="matchingStore.isMatching" @click="toggleMatchingStatus" class="menu-item matching-active">
        <span>매칭 진행중</span>
        <div class="matching-indicator"></div>
      </div>
      <router-link v-else to="/debate-select" class="menu-item">실전 토론</router-link>
      <router-link to="/debate-list" class="menu-item">토론 방청하기</router-link>
    </div>

    <div class="sidebar-footer">
      <!-- 로그인 상태일 때 로그아웃 버튼 -->
      <button v-if="isLoggedIn" class="sidebar-auth-item" @click="handleLogout">
        <span class="sidebar-text"> 로그아웃 </span>
      </button>

      <!-- 로그인 상태가 아닐 때 카카오 로그인 버튼 -->
      <div v-else class="login-container">
        <LoginButton buttonColor="#ff6b6b" buttonRadius="20" buttonPadding="10px 22px" buttonFontSize="14px"
          buttonTextColor="white" />
      </div>
    </div>
  </nav>
  <MatchingStatus v-if="matchingStore.isMatching && matchingStore.isExpanded" />
</template>

<script setup>
import { useKakaoAuth } from "@/composables/useKakaoAuth";
import LoginButton from "@/components/KakaoLoginButton.vue";
import MatchingStatus from "@/components/MatchingStatus.vue";
import { useMatchingStore } from "@/stores/matchingStore";

const matchingStore = useMatchingStore();
const { isLoggedIn, handleLogout } = useKakaoAuth();
const toggleMatchingStatus = () => {
  // MatchingStatus 컴포넌트의 isExpanded 토글
  matchingStore.toggleMatchingExpanded();
};
</script>

<style scoped>
.nav {
  background-color: #ffffff;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 48px;
  margin: 0 auto;
  /* 추가: 가운데 정렬을 유지 */
  max-width: 1280px;
  /* 추가: App.vue와 동일한 최대 너비 */
}

.menu {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.home-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  gap: 8px;
}

.logo-text {
  color: #ff6b6b;
  font-size: 24px;
  font-weight: bold;
}

.logo-circle {
  width: 32px;
  height: 32px;
  background-color: #ff6b6b;
  border-radius: 50%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-inner-circle {
  width: 24px;
  height: 24px;
  background-color: #fff1f1;
  border-radius: 50%;
}

.logo-core {
  width: 16px;
  height: 16px;
  background-color: #ff6b6b;
  border-radius: 50%;
  position: absolute;
}

.menu {
  display: flex;
  gap: 20px;
  align-items: center;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.menu-item {
  padding: 8px 20px;
  border-radius: 18px;
  color: #000000;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  text-decoration: none;
  transition: color 0.3s, background-color 0.3s;
}

.menu:hover .menu-item {
  color: #b0b0b0;
  /* 기본적으로 회색 */
}

.menu-item:hover {
  color: #000 !important;
  /* 해당 메뉴만 검정색 */
}

.sidebar-auth-item {
  background-color: #fcfcfc;
  color: #000000;
  border: 2px solid #ff6b6b;
  height: 47px;
  width: 120px;
  text-decoration: none;
  border-radius: 20px;
  font-family: inherit;
  cursor: pointer;
}

.sidebar-auth-item:hover {
  background-color: #ff6b6b;
}

.matching-active {
  background-color: #fff1f1;
  color: #ff6b6b !important;
  font-weight: 600;
  position: relative;
  padding-right: 35px !important;
  /* 인디케이터 공간 확보 */
  border: 2px solid #ff6b6b;
}

.matching-active:hover {
  background-color: #ffe8e8;
}

.matching-indicator {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #ff6b6b;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    transform: translateY(-50%) scale(1);
    opacity: 1;
  }

  50% {
    transform: translateY(-50%) scale(1.2);
    opacity: 0.7;
  }

  100% {
    transform: translateY(-50%) scale(1);
    opacity: 1;
  }
}
</style>
