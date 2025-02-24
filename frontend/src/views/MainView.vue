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

    <!-- Progress & Stats Combined Section -->
    <section class="progress-stats-section">
      <div class="progress-stats-wrapper">
        <!-- 왼쪽: 진행 단계 -->
        <div class="stages-section">
          <div class="stages-row">
            <template v-for="(stage, index) in stages" :key="index">
              <div class="stage-item">
                <div class="stage-circle" :style="{ backgroundColor: getStageColor(stage.status) }">
                  <div class="stage-number" :style="{ color: getStageNumberColor(stage.status) }">
                    {{ index + 1 }}
                  </div>
                </div>
                <div class="stage-info">
                  <div class="stage-title">{{ stage.title }}</div>
                  <div class="stage-progress">{{ stage.progress }}</div>
                </div>
              </div>
              <!-- 연결선 (마지막 아이템 제외) -->
              <div v-if="index < stages.length - 1" class="stage-connector"
                :style="{ backgroundColor: getConnectorColor(index) }"></div>
            </template>
          </div>
        </div>

        <!-- 구분선 -->
        <div class="vertical-divider"></div>

        <!-- 오른쪽: 통계 -->
        <div class="stats-row">
          <div v-for="stat in stats" :key="stat.key" class="stat-item">
            <div class="stat-title">{{ stat.title }}</div>
            <button class="stat-value" @click="goToStatDetail(stat.key)">
              {{ stat.value }}
            </button>
            <div class="stat-subtext">{{ stat.subtext }}</div>
          </div>
        </div>
      </div>
    </section>

    <DebateExplorer />
  </main>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import DebateExplorer from "@/components/DebateExplorer.vue";
import { ref } from "vue";

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

const router = useRouter();

const goToStatDetail = (key: string) => {
  router.push(`/stats/${key}`);
};

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
    key: "debate-count",
    title: "참여한 토론",
    value: `${userStats.value.debateCount}회`,
    subtext: `지난 주 대비 +${userStats.value.weeklyDebateIncrease}회`,
  },
  {
    key: "score",
    title: "점수",
    value: `${userStats.value.winRate}점`,
    subtext: `상위 ${userStats.value.percentileRank}%`,
  },
  {
    key: "feedback",
    title: "받은 피드백",
    value: `${userStats.value.feedbackCount}개`,
    subtext: `새 피드백 +${userStats.value.newFeedbacks}개`,
  },
]);

// stages 데이터 추가 (기존 Stats 섹션은 유지)
const stages = ref([
  {
    title: "기본기",
    progress: "3/3 완료",
    status: "completed",
  },
  {
    title: "실전 연습",
    progress: "2/5 진행중",
    status: "current",
  },
  {
    title: "실전 토론",
    progress: "0/3 준비중",
    status: "locked",
  },
]);

// 스테이지 색상 함수
const getStageColor = (status: string) => {
  switch (status) {
    case "completed":
      return "#FF6B6B";
    case "current":
      return "#FF6B6B";
    default:
      return "#F6F6F6";
  }
};

const getStageNumberColor = (status: string) => {
  return status === "locked" ? "#111827" : "#FFFFFF";
};

// 연결선 색상 함수
const getConnectorColor = (index: number) => {
  if (stages.value[index].status === "completed") {
    return "#FF6B6B";
  }
  return "#F6F6F6";
};

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
  height: 7.5rem;
  margin-top: 1.25rem;
  margin-bottom: 2rem;
}

.welcome-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 2.5rem;
}

.tori-character {
  position: relative;
  width: 6.25rem;
  height: 6.25rem;
  border-radius: 50%;
}

.welcome-text {
  margin-left: 3rem;
}

.welcome-text h2 {
  font-weight: bold;
  color: #111827;
  font-size: 1.5rem;
  margin-bottom: 0.75rem;
}

.welcome-text p {
  font-size: 1.125rem;
  color: #4b5563;
}

/* Progress & Stats Section */
.progress-stats-section {
  margin-bottom: 2rem;
}

.progress-stats-wrapper {
  background: #ffffff;
  border: 1px solid #f1f1f1;
  border-radius: 1rem;
  padding: 2rem 3rem;
  display: flex;
  align-items: center;
  min-height: 7.5rem;
  gap: 3rem;
}

/* 스테이지 섹션 */
.stages-section {
  flex: 1.2;
}

.stages-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stage-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
}

.stage-circle {
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stage-number {
  color: #ffffff;
  font-weight: bold;
  font-size: 1.25rem;
}

.stage-info {
  text-align: center;
}

.stage-title {
  font-weight: 600;
  color: #111827;
  margin-bottom: 0.25rem;
}

.stage-progress {
  font-size: 0.875rem;
  color: #6b7280;
}

.stage-connector {
  width: 5rem;
  height: 2px;
  margin: 0 0.5rem;
}

/* 구분선 */
.vertical-divider {
  width: 1px;
  height: 5rem;
  background: #e9ecef;
  margin: 0 1rem;
}

/* 통계 섹션 */
.stats-row {
  flex: 2;
  display: flex;
  /* 항목들을 가로로 배치 */
  justify-content: space-between;
  gap: 2rem;
}

.stat-item {
  flex: 1;
  display: flex;
  /* 추가: 내부 요소들을 세로로 배치 */
  flex-direction: column;
  /* 추가: 세로 방향으로 설정 */
  text-align: left;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: bold;
  color: #ff6b6b;
  margin-bottom: 0.5rem;
  /* 간격 조정 */
  order: 2;
  /* 추가: 순서 설정 */
}

.stat-title {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  margin-bottom: 0.5rem;
  order: 1;
  /* 추가: 순서 설정 */
}

.stat-subtext {
  font-size: 0.875rem;
  color: #6b7280;
  order: 3;
  /* 추가: 순서 설정 */
}

/* 차트 스타일 */
.stat-chart {
  height: 2rem;
  margin: 0.75rem 0;
}

.bar-chart::before,
.bar-chart::after {
  content: "";
  display: block;
  width: 0.5rem;
  height: 1.25rem;
  background: #ffe3e3;
  margin-right: 0.25rem;
}

.bar-chart::after {
  height: 1.75rem;
  background: #ff6b6b;
}

.line-chart {
  position: relative;
  overflow: hidden;
}

.line-chart::before {
  content: "";
  display: block;
  width: 100%;
  height: 2px;
  background: #ff6b6b;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}

.dot-chart::before,
.dot-chart::after {
  content: "";
  display: block;
  width: 0.5rem;
  height: 0.5rem;
  border-radius: 50%;
  background: #ff6b6b;
  margin-right: 0.5rem;
}

.dot-chart::after {
  background: #ffe3e3;
}

/* Quick Start Section */
.quick-start-section {
  margin-bottom: 3rem;
}

.quick-start-section h2 {
  font-weight: bold;
  color: #111827;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.25rem;
}

.action-card {
  background: #000000;
  border-radius: 1rem;
  padding: 2rem;
  height: 6.25rem;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}

.action-card:hover {
  background: #ff5151;
}

.action-card h3 {
  font-weight: bold;
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
}

.action-card p {
  font-size: 1rem;
  opacity: 0.9;
}

/* Popular Debates Section */
.popular-debates-section h2 {
  font-weight: bold;
  color: #111827;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.debates-container {
  background: white;
  border-radius: 1rem;
  padding: 2rem;
}

.debate-card {
  background: #fff1f1;
  border-radius: 0.5rem;
  height: 5rem;
  margin-bottom: 1rem;
}

.debate-card:last-child {
  margin-bottom: 0;
}

.debate-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 1.5rem;
}

.debate-content h3 {
  font-size: 1.125rem;
  margin-bottom: 0.25rem;
  font-weight: bold;
  color: #111827;
}

.debate-content p {
  font-size: 1rem;
  color: #6b7280;
}

.watch-button {
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 1.25rem;
  padding: 0.625rem 3rem;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
  font-family: inherit;
}

.watch-button:hover {
  background: #ff5151;
}
</style>
