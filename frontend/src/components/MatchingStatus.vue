// MatchingStatus.vue
<template>
  <div class="matching-status">
    <!-- 축소된 상태일 때 보여질 인디케이터 -->
    <div v-if="!isSidebarOpen" class="collapsed-indicator">
      <MatchingIndicator :color="'#00C288'" :size="32" />
    </div>

    <!-- 기존 매칭 상태 표시 (확장된 상태) -->
    <div v-else class="matching-container">
      <!-- 매칭 헤더 -->
      <div class="matching-header flex items-center gap-4">
        <!-- 상태 표시 템플릿 -->
        <div class="status-indicator">
          <!-- 매칭 중일 때 -->
          <div v-if="!matchComplete" class="indicator-wrapper">
            <MatchingIndicator 
              color="#00C288"
              :size="24"
            />
          </div>
          <!-- 매칭 완료일 때 -->
          <div v-else class="check-mark">
            <svg viewBox="0 0 24 24">
              <path 
                d="M6 12 L10 16 L18 8" 
                stroke="white" 
                stroke-width="2" 
                fill="none"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <animate
                  attributeName="stroke-dasharray"
                  from="0,24"
                  to="24,24"
                  dur="0.3s"
                  fill="freeze"
                />
              </path>
            </svg>
          </div>
        </div>
        <span class="status-text">
          {{ matchComplete ? '매칭 완료!' : '토론 상대를 찾고 있어요' }}
        </span>
      </div>

      <!-- 선택된 옵션들 -->
      <div class="options-wrapper">
        <div class="option-group">
          <span class="option-label">토론 방식</span>
          <div class="option-value">
            BP토론
          </div>
        </div>
        <div class="option-group">
          <span class="option-label">주제 카테고리</span>
          <div class="option-value">
            사회
          </div>
        </div>
      </div>

      <!-- 참가자 상태 -->
      <div class="participants-section">
        <div class="participants-text" v-if="participants < 4">
          {{ 4 - participants }}명을 기다리고 있어요
        </div>
        <div class="participants-text" v-else>
          참가자가 다 모였어요!
        </div>
        <div class="progress-bar-container">
          <div class="progress-bar-background"></div>
          <div 
            class="progress-bar-fill"
            :style="{ width: `${(participants / 4) * 100}%` }"
          />
        </div>
      </div>

      <!-- 매칭 취소 버튼 -->
      <button 
        v-if="!matchComplete"
        class="cancel-button"
        :class="cancelButtonClasses"
        @click="emit('cancel')"
        :disabled="cancelState === 'completed' || cancelState === 'completing'"
        >
        <div class="button-content" :class="{ 'fade-out': cancelState === 'completing' }">
            <template v-if="cancelState === 'initial'">
            매칭 취소하기
            </template>
            <template v-if="cancelState === 'confirm'">
            매칭을 취소하시겠습니까?
            </template>
        </div>
        <div class="completion-content" :class="{ 'fade-in': cancelState === 'completed' || cancelState === 'completing' }">
            <div class="check-icon-wrapper">
            <svg class="check-icon" viewBox="0 0 24 24">
                <path 
                d="M4 12 L10 18 L20 6" 
                stroke="white" 
                stroke-width="2" 
                fill="none"
                >
                <animate
                    attributeName="stroke-dasharray"
                    from="0,30"
                    to="30,30"
                    dur="0.3s"
                    fill="freeze"
                    begin="0.15s"
                />
                </path>
            </svg>
            취소 완료
            </div>
        </div>
        </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import MatchingIndicator from './MatchingIndicator.vue';

interface Props {
  isSidebarOpen: boolean;
  participants: number;
  cancelState: 'initial' | 'confirm' | 'completing' | 'completed';
}

const props = defineProps<Props>();
const matchComplete = computed(() => props.participants >= 4);
const emit = defineEmits<{
  (e: 'cancel'): void;
}>();

const cancelButtonClasses = computed(() => ({
  'cancel-hover': props.cancelState === 'confirm',
  'cancel-completing': props.cancelState === 'completing',
  'cancel-completed': props.cancelState === 'completed'
}));
</script>

<style scoped>
.matching-status {
  margin: 0 1rem;
  overflow: hidden;
  background-color: none;
}

.status-text {
  color: white;
}

.collapsed-indicator {
  justify-content: center;
  padding: 1rem 0;
  width: 100%;
  border-radius: 0;
  margin: 0;
}

.matching-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.status-indicator {
  position: relative;
  width: 24px;
  height: 24px;
}

.indicator-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-mark {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #00C288;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: scaleIn 0.3s ease-out;
}

.check-mark svg {
  width: 16px;
  height: 16px;
}

.options-wrapper {
  display: flex;
  justify-content: center;
  gap: 2rem;
  background-color: #242B42;
  border-radius: 8px;
  padding: 1rem;
  width: 100%;
}

.option-label {
  color: #ffffff;
  font-size: 12px;
  display: block;
  margin-bottom: 0.5rem;
  text-align: center;
}

.option-value {
  background-color: #3B62E2;
  color: white;
  font-size: 12px;
  padding: 0.5rem 1rem;
  border-radius: 999px;
  text-align: center;
}

.participants-section {
  margin-bottom: 2rem;
}

.participants-text {
  color: white;
  font-size: 14px;
  text-align: center;
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.progress-bar-container {
  position: relative;
  height: 8px;
  margin: 0 1rem;
}

.progress-bar-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background-color: #242B42;
  border-radius: 4px;
}

.progress-bar-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background-color: #3B62E2;
  border-radius: 4px;
  transition: width 0.3s ease-in-out;
}

.cancel-button {
  width: calc(100% - 2rem);
  margin: 0 1rem;
  padding: 0.75rem;
  background-color: #666666;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  min-height: 42px;
}
/* 새로 추가할 스타일들 */
.button-content {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.3s ease-in-out; /* 0.3s로 통일 */
  opacity: 1; /* 초기 투명도 설정 */
}

.completion-content {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}

.fade-out {
  opacity: 0;
}

.fade-in {
  opacity: 1;
}

.cancel-button:hover:not(:disabled) {
  background-color: #DC2626;
}

.cancel-button.cancel-hover {
  background-color: #991B1B;
}

.cancel-button.cancel-completing {
  background-color: #991B1B;
  cursor: not-allowed;
}

.cancel-button.cancel-completed {
  background-color: #00C288;
  cursor: not-allowed;
}

.check-icon-wrapper {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.check-icon {
  width: 18px;
  height: 18px;
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

@keyframes scaleIn {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}
</style>