<template>
  <main class="dashboard">
    <!-- Welcome Section -->
    <section class="welcome-section">
      <div class="welcome-content">
        <!-- Tori Character -->
        <div class="tori-character">
          <img :src="getImageUrl('Tori.svg')" alt="토리 이미지" class="Tori" />
        </div>
        <div class="welcome-text">
          <h2>{{ userName }}님, 중급 토론가까지 앞으로 2단계!</h2>
          <p>
            오늘도 열심히 해봐요 💪 토론 실력이 날이 갈수록 좋아지고 있어요!
          </p>
        </div>
      </div>
    </section>

    <StudyProgress />

    <!-- Stats Section -->
    <section class="stats-section">
      <div v-for="(stat, index) in stats" :key="index" class="stat-card">
        <h3>{{ stat.title }}</h3>
        <p class="stat-value">{{ stat.value }}</p>
        <div class="stat-divider"></div>
        <p class="stat-subtext">{{ stat.subtext }}</p>
      </div>
    </section>

    <DebateExplorer />
  </main>
</template>

<script setup lang="ts">
import { onMounted } from "vue";

interface UserStats {
  debateCount: number;
  winRate: number;
  feedbackCount: number;
  weeklyDebateIncrease: number;
  percentileRank: number;
  newFeedbacks: number;
}
interface PopularDebate {
  title: string;
  level: string;
  spectators: number;
}
StudyProgress;
import DebateExplorer from "@/components/DebateExplorer.vue";
import StudyProgress from "@/components/StudyProgress.vue";
import { ref } from "vue";

const getImageUrl = (filename: string): string => {
  return new URL(`../assets/${filename}`, import.meta.url).href;
};

const userName = ref<string>(localStorage.getItem("username") || "김토론");
// interface Props {
//   userName: string | null; // null 허용하도록 수정
// }

// const props = defineProps<Props>();

const userStats = ref<UserStats>({
  debateCount: 24,
  winRate: 67,
  feedbackCount: 42,
  weeklyDebateIncrease: 3,
  percentileRank: 30,
  newFeedbacks: 2,
});

const stats = ref([
  {
    title: "참여한 토론",
    value: `${userStats.value.debateCount}회`,
    subtext: `지난 주 대비 +${userStats.value.weeklyDebateIncrease}회`,
  },
  {
    title: "승률",
    value: `${userStats.value.winRate}%`,
    subtext: `상위 ${userStats.value.percentileRank}%`,
  },
  {
    title: "받은 피드백",
    value: `${userStats.value.feedbackCount}개`,
    subtext: `새 피드백 +${userStats.value.newFeedbacks}개`,
  },
]);

const quickActions = ref([
  {
    title: "AI 토론 연습 시작하기",
    subtitle: "현재 진행중인 연습: 28명",
  },
  {
    title: "실전 토론 참여하기",
    subtitle: "대기 중인 토론: 5개",
  },
  {
    title: "토론 구경하기",
    subtitle: "현재 진행중: 12개",
  },
]);

const popularDebates = ref<PopularDebate[]>([
  {
    title: "AI 기술 발전이 일자리에 미치는 영향",
    level: "중급 토론",
    spectators: 38,
  },
  {
    title: "환경보호를 위한 개인의 실천방안",
    level: "고급 토론",
    spectators: 27,
  },
]);
</script>

<style scoped>
.dashboard {
  margin: 0 auto;
  background: #ffffff;
  min-height: 100vh;
}

/* Welcome Section */
.welcome-section {
  background: none;
  border-radius: 16px;
  border: 2px solid #cacaca; /* 테두리 두께와 색상 설정 */
  height: 120px;
  margin-top: 20px;
  margin-bottom: 32px;
}

.welcome-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 40px;
}

.tori-character {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
}

.welcome-text {
  margin-left: 48px;
}

.welcome-text h2 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 12px;
}

.welcome-text p {
  font-size: 18px;
  color: #4b5563;
}

/* Stats Section */
.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 48px;
}

.stat-card {
  background: rgb(246, 246, 246);
  border-radius: 16px;
  padding: 32px;
  height: 160px;
}

.stat-card h3 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 16px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #ff6b6b;
  margin-bottom: 16px;
}

.stat-divider {
  border-top: 1px solid #e5e7eb;
  margin-bottom: 16px;
}

.stat-subtext {
  font-size: 16px;
  color: #6b7280;
}

/* Quick Start Section */
.quick-start-section {
  margin-bottom: 48px;
}

.quick-start-section h2 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 24px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.action-card {
  background: #000000;
  border-radius: 16px;
  padding: 32px;
  height: 100px;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}

.action-card:hover {
  background: #ff5151;
}

.action-card h3 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}

.action-card p {
  font-size: 16px;
  opacity: 0.9;
}

/* Popular Debates Section */
.popular-debates-section h2 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 24px;
}

.debates-container {
  background: white;
  border-radius: 16px;
  padding: 32px;
}

.debate-card {
  background: #fff1f1;
  border-radius: 8px;
  height: 80px;
  margin-bottom: 16px;
}

.debate-card:last-child {
  margin-bottom: 0;
}

.debate-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 24px;
}

.debate-content h3 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 4px;
}

.debate-content p {
  font-size: 16px;
  color: #6b7280;
}

.watch-button {
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 10px 48px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-family: inherit;
}

.watch-button:hover {
  background: #ff5151;
}
</style>
