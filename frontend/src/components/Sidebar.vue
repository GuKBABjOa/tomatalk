<template>
  <div>
    <!-- 사이드바 토글 버튼 -->
    <button class="sidebar-toggle" @click="toggleSidebar">☰</button>

    <!-- 사이드바 컨테이너 -->
    <div class="sidebar-container" :class="{ 'sidebar-collapsed': !isSidebarOpen }">
      <!-- 사이드바 헤더: 로고 -->
      <div class="sidebar-header">
        <router-link to="/" class="home-link">
          <img :src="getImageUrl('logo.svg')" alt="Logo" class="nav-logo" />
        </router-link>
      </div>

      <!-- 사이드바 내용 -->
      <div class="sidebar-content">
        <nav class="sidebar-nav">
          <ul>
            <li class="sidebar-item">
              <div class="matching-container">
                <!-- 매칭 시작 버튼 -->
                <!-- 매칭 시작 모달로 바꾸기 -->
                <router-link v-if="!isMatching" to="/match" class="sidebar-link" @click="startMatching">
                  <img :src="getImageUrl('play.svg')" alt="icon" class="nav-icon" />
                  <span class="sidebar-text" :class="{ 'hidden-text': !isSidebarOpen }">
                    시작하기
                  </span>
                </router-link>

                <!-- 매칭 진행 중 상태 -->
                <MatchingStatus v-if="isMatching" :isSidebarOpen="isSidebarOpen" :matchComplete="matchComplete"
                  :participants="participants" :cancelState="cancelState" @cancel="handleCancel" />
              </div>
            </li>

            <!--메뉴-->
            <li v-for="item in menuItems" :key="item.name" class="sidebar-item">
              <router-link :to="item.path" class="sidebar-link">
                <img :src="getImageUrl(item.icon)" alt="icon" class="nav-icon" />
                <span class="sidebar-text" :class="{ 'hidden-text': !isSidebarOpen }">
                  {{ item.name }}
                </span>
              </router-link>
            </li>
          </ul>
        </nav>
      </div>

      <!-- 로그인/로그아웃 영역 -->
      <div class="sidebar-footer">
        <div v-if="isLoggedIn" class="sidebar-auth-item" @click="handleLogout">
          <img src="@/assets/logout.svg" alt="로그아웃" class="nav-icon" />
          <span class="sidebar-text" :class="{ 'hidden-text': !isSidebarOpen }">
            로그아웃
          </span>
        </div>
        <div v-else class="login-container">
          <button class="kakao-login-btn" @click="handleKakaoLogin">
            <img :src="getImageUrl('kakao.svg')" alt="카카오 로그인" class="kakao-icon" />
            <span class="sidebar-text" :class="{ 'hidden-text': !isSidebarOpen }">
              카카오톡으로 시작하기
            </span>
          </button>
        </div>
      </div>
    </div>

    <!-- 매칭 완료 모달 -->
    <!-- 주제 선정 API 호출-->
    <MatchingSuccessModal v-if="isModalOpen" :countdown="countdown" :participantsData="participantsData"
      @close="closeModal" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import MatchingStatus from "@/components/MatchingStatus.vue";
import MatchingSuccessModal from "@/components/MatchingSuccessModal.vue";

type MenuItem = {
  name: string;
  path: string;
  icon: string;
};

type Participant = {
  name: string;
  image: string | null;
};

const router = useRouter();
const isSidebarOpen = ref<boolean>(true);
const isMatching = ref<boolean>(false);
const matchComplete = ref<boolean>(false);
const participants = ref<number>(1);
const cancelState = ref<'initial' | 'confirm' | 'completing' | 'completed'>('initial');
const isModalOpen = ref<boolean>(false);
const countdown = ref<number>(5);
const isLoggedIn = ref<boolean>(false);

let joinInterval: number | null = null;
let countdownInterval: number | null = null;

const menuItems = ref<MenuItem[]>([
  { name: "진행 중인 토론", path: "/", icon: "discussion_list.svg" },
  { name: "칼럼게시판", path: "/", icon: "column.svg" },
  { name: "마이페이지", path: "/", icon: "user.svg" },
]);

const participantsData = ref<Participant[]>([
  { name: "P1(나)", image: null },
  { name: "P2", image: null },
  { name: "P3", image: null },
  { name: "P4", image: null }
]);

const getImageUrl = (filename: string): string => {
  return new URL(`../assets/${filename}`, import.meta.url).href;
};

const toggleSidebar = (): void => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

const startMatching = (): void => {
  isMatching.value = true;
  matchComplete.value = false;
  participants.value = 1;

  joinInterval = setInterval(() => {
    if (participants.value < 4) {
      participants.value++;
    } else {
      if (joinInterval !== null) {
        clearInterval(joinInterval);
        joinInterval = null;
      }
      handleMatchComplete();
    }
  }, 3000);
};

const handleCancel = async (): Promise<void> => {
  if (cancelState.value === 'initial') {
    cancelState.value = 'confirm';
    setTimeout(() => {
      if (cancelState.value === 'confirm') {
        cancelState.value = 'initial';
      }
    }, 5000);
    return;
  }

  if (cancelState.value === 'confirm') {
    cancelState.value = 'completing';

    if (joinInterval !== null) {
      clearInterval(joinInterval);
      joinInterval = null;
    }

    await new Promise(resolve => setTimeout(resolve, 300));
    cancelState.value = 'completed';

    setTimeout(() => {
      isMatching.value = false;
      matchComplete.value = false;
      participants.value = 0;
      cancelState.value = 'initial';
    }, 800);
  }
};

const handleMatchComplete = async (): Promise<void> => {
  matchComplete.value = true;
  cancelState.value = 'initial';
  isModalOpen.value = true;
  countdown.value = 5;

  if (joinInterval !== null) {
    clearInterval(joinInterval);
    joinInterval = null;
  }

  countdownInterval = setInterval(() => {
    if (countdown.value > 1) {
      countdown.value--;
    } else {
      if (countdownInterval !== null) {
        clearInterval(countdownInterval);
        countdownInterval = null;
      }
      isModalOpen.value = false;
      router.push('/discussion-room');
    }
  }, 1000);

  setTimeout(() => {
    isMatching.value = false;
    matchComplete.value = false;
    participants.value = 0;
    cancelState.value = 'initial';
  }, 5000);
};

const closeModal = (): void => {
  isModalOpen.value = false;
  isMatching.value = false;
  matchComplete.value = false;
  participants.value = 0;
  cancelState.value = 'initial';

  if (countdownInterval !== null) {
    clearInterval(countdownInterval);
    countdownInterval = null;
  }
};

const handleKakaoLogin = async (): Promise<void> => {
  try {
    const response = await axios.get<{ requestUrl?: string }>(`http://localhost:8080/api/auth/login/kakao/url`);
    if (response.data?.requestUrl) {
      console.log("카카오 로그인 URL:", response.data.requestUrl);
      window.location.href = response.data.requestUrl;
    } else {
      console.error("카카오 로그인 URL을 가져오지 못했습니다.");
    }
  } catch (error) {
    console.error("카카오 로그인 요청 중 오류 발생:", error);
  }
};

const handleLogout = (): void => {
  console.log("로그아웃 시도");
  isLoggedIn.value = false;
};
</script>

<style scoped>
.sidebar-container {
  position: fixed;
  inset: 0 auto 0 0;
  width: 16rem;
  background-color: #111729;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease-in-out;
  font-family: Arial, sans-serif;
}

.sidebar-collapsed {
  width: 4rem;
}

/* 사이드바가 접힌 상태*/
.sidebar-collapsed .sidebar-link {
  justify-content: center;
  padding: 1rem 0;
  width: 100%;
  border-radius: 0;
  margin: 0;
  margin-top: 1rem;
}

.sidebar-collapsed .sidebar-link:hover {
  background-color: #3B62E2;
  width: 100%;
}

.sidebar-collapsed .sidebar-text {
  display: none;
}

/* 사이드바가 열린 상태 */
.sidebar-link {
  position: relative;
  padding: 1rem;
  margin: 0 0.8rem;
  text-decoration: none;
  color: white;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  border-radius: 8px;
  transition: background-color 0.2s ease-in-out;
  width: calc(100% - 2rem);
}

.sidebar-link:hover {
  background-color: #3B62E2;
}

.sidebar-toggle {
  position: fixed;
  left: 0.2rem;
  top: 0.3rem;
  background-color: #111729;
  color: white;
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-size: 1.5rem;
  z-index: 1000;
  border: none;
}

.nav-logo {
  width: 90%;
  height: auto;
  object-position: center;
  background-color: white;
}

.home-link {
  background-color: transparent;
}

.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-item {
  margin: 0.6rem 0;
  width: 100%;
}

.nav-icon {
  width: 24px;
  height: 24px;
  min-width: 24px;
  margin-left: 0;
}

.sidebar-text {
  opacity: 1;
  transform: translateX(0);
  transition: opacity 0.3s ease-in-out, transform 0.3s ease-in-out;
  white-space: nowrap;
  font-family: Arial, sans-serif;
  font-size: 14px;
}

.hidden-text {
  opacity: 0;
  transform: translateX(-10px);
  position: absolute;
  pointer-events: none;
}

.sidebar-footer {
  padding: 1rem;
  margin-top: auto;
}

.kakao-login-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0.75rem;
  background-color: #FEE500;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s ease-in-out;
}

.kakao-login-btn:active {
  transform: translateY(1px);
}

.kakao-icon {
  width: 24px;
  height: 24px;
}

.sidebar-auth-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background-color: transparent;
  border: 1px solid #4A5568;
  border-radius: 12px;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
}

.sidebar-auth-item:hover {
  background-color: #3B62E2;
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    opacity: 0.5;
  }

  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }

  100% {
    transform: scale(0.95);
    opacity: 0.5;
  }
}

.sidebar-collapsed .sidebar-footer {
  padding: 0.5rem;
}
</style>
