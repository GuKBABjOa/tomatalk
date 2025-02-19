<!-- DebateProgress.vue -->
<template>
  <div class="progress-container">
    <div class="progress-wrapper">
      <!-- Progress Line Background -->
      <div class="progress-line-bg"></div>

      <!-- Completed Progress Line -->
      <div class="progress-line" :style="{ width: `${getProgressWidth}%` }"></div>

      <!-- Stages -->
      <template v-for="(stage, index) in stages" :key="index">
        <!-- Stage Title & Progress -->
        <div class="stage-info" :style="{
          left: `${getStagePosition(index)}%`,
        }">
          <div class="stage-title" :style="{ color: colors.textPrimary }">
            {{ stage.title }}
          </div>
          <div class="stage-progress" :style="{ color: colors.textSecondary }">
            {{ stage.progress }}
          </div>
        </div>

        <!-- Stage Circle -->
        <div class="stage-circle-wrapper" :style="{
          left: `${getStagePosition(index)}%`,
        }">
          <!-- Pulse Animation for Current Stage -->
          <div v-if="stage.status === 'current'" class="pulse-animation" :class="{ 'pulse-active': isPulseActive }">
          </div>

          <!-- Main Circle -->
          <div class="stage-circle" :style="{ backgroundColor: getStageColor(stage.status) }">
            <div class="stage-inner-circle">
              <span class="stage-number" :style="{ color: getStageTextColor(stage.status) }">
                {{ index + 1 }}
              </span>
            </div>
          </div>

          <!-- Completed Check Mark -->
          <div v-if="stage.status === 'completed'" class="check-mark">
            <div class="check-mark-inner"></div>
          </div>
        </div>
      </template>
    </div>
    <div class="stage-controls">
      <!-- Continue Button -->
      <router-link to="/practice-main" v-if="currentStageIndex !== -1" class="continue-button"
        @click="handleContinue(currentStageIndex)">이어서 학습하기

        <span class="continue-arrow"></span>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";

interface Stage {
  title: string;
  progress: string;
  status: "completed" | "current" | "locked";
  completedTasks: number;
  totalTasks: number;
}

const colors = {
  background: "#ffffff",
  primary: "#ff6b6b",
  pathBg: "#f6f6f6",
  textPrimary: "#111827",
  textSecondary: "#6b7280",
};

const stages = ref<Stage[]>([
  {
    title: "기본기",
    progress: "3/3 완료",
    status: "completed",
    completedTasks: 3,
    totalTasks: 3,
  },
  {
    title: "실전 연습",
    progress: "2/5 진행중",
    status: "current",
    completedTasks: 2,
    totalTasks: 5,
  },
  {
    title: "토론 대회",
    progress: "0/3 준비중",
    status: "locked",
    completedTasks: 0,
    totalTasks: 3,
  },
]);

const isPulseActive = ref(false);
let pulseInterval: ReturnType<typeof setInterval>;

onMounted(() => {
  pulseInterval = setInterval(() => {
    isPulseActive.value = !isPulseActive.value;
  }, 1000);
});

onUnmounted(() => {
  if (pulseInterval) {
    clearInterval(pulseInterval);
  }
});

const getProgressWidth = computed(() => {
  const lastCompletedIndex = stages.value.findIndex(
    (stage) => stage.status === "current"
  );

  if (lastCompletedIndex === -1) return "0%"; // 진행 중인 단계가 없으면 0%

  return `${(lastCompletedIndex / (stages.value.length - 1)) * 100}%`;
});

const currentStageIndex = computed(() =>
  stages.value.findIndex((stage) => stage.status === "current")
);

const getStagePosition = (index: number): number => {
  const totalStages = stages.value.length;
  return (index * 100) / (totalStages - 1);
};

const getStageColor = (status: Stage["status"]): string => {
  switch (status) {
    case "current":
    case "completed":
      return colors.primary;
    default:
      return colors.pathBg;
  }
};

const getStageTextColor = (status: Stage["status"]): string => {
  switch (status) {
    case "current":
    case "completed":
      return colors.primary;
    default:
      return colors.textSecondary;
  }
};

const totalProgress = computed(() => {
  const completed = stages.value.reduce(
    (sum, stage) => sum + stage.completedTasks,
    0
  );
  const total = stages.value.reduce((sum, stage) => sum + stage.totalTasks, 0);
  return Math.round((completed / total) * 100);
});

const emit = defineEmits<{
  (e: "continue", stageIndex: number): void;
}>();

const handleContinue = (index: number) => {
  emit("continue", index);
};
</script>

<style scoped>
.progress-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.progress-wrapper {
  position: relative;
  width: 100%;
  max-width: 1000px;
  height: 320px;
  background-color: #ffffff;
  border-radius: 16px;
  padding: 0 60px;
}

.progress-line-bg,
.progress-line {
  position: absolute;
  top: 140px;
  /* 원과 같은 높이로 정렬 */
  left: 0;
  width: 100%;
  height: 8px;
  background-color: #f6f6f6;
  border-radius: 4px;
  transform: translateY(-50%);
}

.progress-line {
  background-color: #ff6b6b;
  width: 0%;
  transition: width 1s ease;
  /* 애니메이션 추가 */
  animation: expandProgress 1s forwards;
}

.stage-info {
  position: absolute;
  top: 50px;
  transform: translateX(-50%);
  text-align: center;
  width: 120px;
  z-index: 2;
}

.stage-title {
  font-size: 18px;
  font-weight: bold;
  white-space: nowrap;
}

.stage-progress {
  margin-bottom: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.stage-circle-wrapper {
  position: absolute;
  top: 140px;
  transform: translate(-50%, -50%);
  z-index: 2;
}

.pulse-animation {
  position: absolute;
  width: 100px;
  /* 기존 64px → 100px */
  height: 100px;
  /* 기존 64px → 100px */
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #ff6b6b;
  opacity: 0.2;
  animation: pulseEffect 2s infinite ease-in-out;
  /* SVG와 동일한 애니메이션 적용 */
}

@keyframes pulseEffect {
  0% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.3;
  }

  50% {
    transform: translate(-50%, -50%) scale(1.5);
    /* 더 확장 */
    opacity: 0;
  }

  100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.3;
  }
}

.pulse-animation.pulse-active {
  animation: pulse 1s ease-in-out;
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.3;
  }

  50% {
    transform: translate(-50%, -50%) scale(1.25);
    opacity: 0;
  }

  100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.3;
  }
}

.stage-circle {
  position: relative;
  width: 80px;
  /* 기존 64px → 80px로 증가 */
  height: 80px;
  /* 기존 64px → 80px로 증가 */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stage-inner-circle {
  width: 65px;
  /* 기존 48px → 60px로 증가 */
  height: 65px;
  /* 기존 48px → 60px로 증가 */
  background-color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stage-number {
  font-size: 20px;
  /* 기존 16px → 20px */
  font-weight: bold;
}

.check-mark {
  position: absolute;
  top: -30px;
  right: -9px;
  width: 18px;
  height: 18px;
  background-color: #ff6b6b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 2;
}

.check-mark-inner {
  width: 8px;
  height: 8px;
  border-right: 2px solid white;
  border-bottom: 2px solid white;
  transform: rotate(45deg) translate(-1px, -1px);
}

.stage-controls {
  position: absolute;
  top: 440px;
  right: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100px;
}

.total-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.total-progress-title {
  font-size: 14px;
  font-weight: bold;
  color: #111827;
}

.total-progress-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-progress-bar {
  width: 200px;
  height: 6px;
  background-color: #f6f6f6;
  border-radius: 3px;
  overflow: hidden;
}

.total-progress-fill {
  height: 100%;
  background-color: #ff6b6b;
  border-radius: 3px;
  transition: width 1s ease;
}

.total-progress-number {
  font-size: 14px;
  font-weight: bold;
  color: #ff6b6b;
}

.continue-button {
  background-color: #ff6b6b;
  color: white;
  font-size: 14px;
  font-weight: bold;
  padding: 10px 40px;
  border-radius: 20px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  height: 30px;
}

.continue-button:hover {
  filter: brightness(0.95);
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.continue-arrow {
  position: relative;
  width: 10px;
  height: 2px;
  background-color: white;
}

.continue-arrow::after {
  content: "";
  position: absolute;
  right: -1px;
  top: -3px;
  width: 6px;
  height: 6px;
  border-right: 2px solid white;
  border-top: 2px solid white;
  transform: rotate(45deg);
}

@keyframes expandProgress {
  from {
    width: 0%;
  }

  to {
    width: v-bind(getProgressWidth);
  }
}
</style>
