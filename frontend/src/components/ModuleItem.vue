<template>
  <div class="module-item">
    <div class="module-content">
      <div class="icon-container">
        <BarChart3 :size="20" class="icon" />
      </div>
      <div class="info-container">
        <div class="title-row">
          <h4>{{ title }}</h4>
          <div v-if="completed" class="status completed">완료</div>
          <div v-if="locked" class="status locked">잠김</div>
        </div>
        <p class="description">{{ description }}</p>
        <div v-if="!locked" class="progress-container">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: `${progress}%` }" />
          </div>
          <div class="progress-footer">
            <div class="progress-text">{{ progress }}% 완료</div>
            <!-- 이부분에 원래 버튼 -->
          </div>
        </div>
        <!-- 여기가 옮긴 버튼 -->
        <div class="button-container">
          <button :class="[
            'action-button',
            {
              completed: completed,
              'in-progress': !completed && progress > 0,
              'not-started': !completed && progress === 0,
              disabled: locked,
            },
          ]" @click="handleClick">
            {{ buttonText }}
            <ChevronRight :size="16" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { BarChart3, ChevronRight } from "lucide-vue-next";

interface Props {
  title: string;
  description: string;
  progress: number;
  completed?: boolean;
  locked?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  completed: false,
  locked: false,
});

const buttonText = computed(() => {
  if (props.completed) return "다시 학습하기";
  if (props.progress > 0) return "이어서 학습하기";
  return "학습 시작하기";
});

function handleClick() {
  if (props.locked) {
    alert("아직 준비중 입니다.");
    return;
  }
}
</script>

<style scoped>
.module-item {
  position: relative;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
  background: white;
}

.module-item.locked {
  background: #f8f9fa;
}

.module-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.icon-container {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #fff1f1;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon {
  color: #ff6b6b;
}

.info-container {
  flex: 1;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-row h4 {
  font-size: 16px;
  font-weight: bold;
  color: #111827;
}

.status {
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;
}

.status.completed {
  background: #fff1f1;
  color: #ff6b6b;
}



.description {
  font-size: 14px;
  color: #6b7280;
  margin-top: 8px;
}

.progress-container {
  margin-top: 16px;
}

.progress-bar {
  height: 8px;
  background: #f8f9fa;
  border-radius: 9999px;
  overflow: hidden;
  margin-bottom: 12px;
}

.progress-fill {
  height: 100%;
  background: #ff6b6b;
  border-radius: 9999px;
  transition: width 0.3s ease;
}

.progress-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.progress-text {
  font-size: 12px;
  color: #6b7280;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
}

.action-button.disabled.not-started {
  background: #f8f9fa;
  color: #6b7280;
  cursor: not-allowed;
}

.action-button.completed {
  background: #fff1f1;
  color: #ff6b6b;
}

.action-button.completed:hover {
  background: #ffe5e5;
}

.action-button.in-progress {
  background: #fff1f1;
  color: #ff6b6b;
}

.action-button.in-progress:hover {
  background: #ffe5e5;
}

.action-button.not-started {
  background: #fff1f1;
  color: #ff6b6b;
}

.action-button.not-started:hover {
  background: #ffe5e5;
}

.status.locked {
  background: #f8f9fa;
  color: #6b7280;
}

/* 새로 만든 스타일 */
.button-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
