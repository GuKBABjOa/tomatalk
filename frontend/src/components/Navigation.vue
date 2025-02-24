<template>
  <nav class="nav">
    <div class="nav-container">
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
          <LoginButton class="action-button" buttonColor="white" buttonRadius="1.75rem" buttonPadding="1rem 2.5rem"
            buttonFontSize="1.125rem" buttonTextColor="black" hasBorder="true" buttonBorder="0.125rem solid #ff6b6b"
            buttonBorderColor="#ff6b6b" hoverColor="#ff6b6b" />
        </div>
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
}

.menu {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.nav-container {
  height: 6rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 0 auto;
  /* 추가: 가운데 정렬을 유지 */
  width: 75%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.home-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  gap: 0.5rem;
}

.logo-text {
  color: #ff6b6b;
  font-size: 1.5rem;
  font-weight: bold;
}

.logo-circle {
  width: 2rem;
  height: 2rem;
  background-color: #ff6b6b;
  border-radius: 50%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-inner-circle {
  width: 1.5rem;
  height: 1.5rem;
  background-color: #fff1f1;
  border-radius: 50%;
}

.logo-core {
  width: 1rem;
  height: 1rem;
  background-color: #ff6b6b;
  border-radius: 50%;
  position: absolute;
}

.menu {
  display: flex;
  gap: 1.25rem;
  align-items: center;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.menu-item {
  color: #000000;
  padding: 0.5rem 1.25rem;
  border-radius: 1.125rem;
  font-size: 1.125rem;
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
  background-color: white;
  color: black;
  border: 0.125rem solid #ff6b6b;
  height: 3.5rem;
  width: 10rem;
  border-radius: 1.75rem;
  font-size: 1.125rem;
  font-family: inherit;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s, color 0.3s;
}

.sidebar-auth-item:hover {
  background-color: #ff6b6b;
  color: white;
}

.matching-active {
  background-color: #fff1f1;
  color: #ff6b6b !important;
  font-weight: 600;
  position: relative;
  padding-right: 2.1875rem;
  border: 2px solid #ff6b6b;
}

.matching-active:hover {
  background-color: #ffe8e8;
}

.matching-indicator {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 0.75rem;
  width: 0.5rem;
  height: 0.5rem;
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
