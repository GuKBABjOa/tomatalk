// MatchingSuccessModal.vue
<template>
  <!-- 매칭 완료 안내 모달 -->
  <Transition name="modal">
    <div class="modal-overlay" @click="$emit('close')">
      <div class="modal-content" @click.stop>
        <!-- 메인 텍스트 -->
        <h3 class="modal-title">모든 참가자가 준비되었습니다</h3>

        <!-- 프로필 영역 -->
        <div class="profile-container">
          <template v-for="(participant, index) in participantsData" :key="participant.name">
            <div class="profile-item" :class="{ 'current-user': participant.name === 'YOU' }">
              <ProfileImage :imageSrc="participant.image" :size="50" />
              <span class="profile-name">{{ participant.name }}</span>
            </div>
            <div v-if="index < participantsData.length - 1" class="connector"></div>
          </template>
        </div>

        <!-- 토론 정보 -->
        <div class="discussion-info">
          <span class="info-label">토론 주제</span>
          <p class="info-content">인공지능 윤리와 규제의 필요성</p>
        </div>

        <!-- 타이머 영역 -->
        <div class="timer-section">
          <p class="timer-text">잠시 후 토론이 시작됩니다</p>
          <div class="timer-container">
            <div class="countdown">
              <svg class="timer-circle" viewBox="0 0 50 50">
                <circle class="timer-circle-bg" r="20" cx="25" cy="25" />
                <circle 
                  class="timer-circle-progress" 
                  r="20" 
                  cx="25" 
                  cy="25"
                  :style="{
                    strokeDashoffset: `${(1 - countdown / 5) * 125.6}px`
                  }"
                />
              </svg>
              <span class="timer-number">{{ countdown }}</span>
            </div>
          </div>
          <p class="auto-enter-text">{{ countdown }}초 후 자동 입장</p>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script>
import ProfileImage from "@/components/ProfileImage.vue";

export default {
  components: {
    ProfileImage,
  },
  props: {
    countdown: {
      type: Number,
      required: true
    },
    participantsData: {
      type: Array,
      required: true
    }
  },
  emits: ['close']
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(17, 23, 41, 0.75);
  display: grid;
  place-items: center;
  z-index: 50;
}

.modal-content {
  width: 400px;
  padding: 2rem 1.5rem;
  background-color: #1A2037;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.modal-title {
  color: white;
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin: 0;
}

/* 프로필 섹션 */
.profile-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 0;
}

.profile-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.profile-name {
  margin-top: 4px;
  color: white;
  font-size: 12px;
  text-align: center;
}

.connector {
  width: 22px;
  height: 2px;
  background-color: #242B42;
}

/* 토론 정보 섹션 */
.discussion-info {
  background-color: #242B42;
  padding: 1rem;
  border-radius: 10px;
}

.info-label {
  color: #d4d4d4;
  font-size: 14px;
  display: block;
  margin-bottom: 0.5rem;
}

.info-content {
  color: white;
  font-size: 16px;
  font-weight: bold;
  margin: 0;
}

/* 타이머 섹션 */
.timer-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.timer-text {
  color: #d4d4d4;
  font-size: 14px;
  margin: 0;
}

.timer-container {
  position: relative;
  width: 50px;
  height: 50px;
}

.countdown {
  position: relative;
  display: grid;
  place-items: center;
}

.timer-circle {
  transform: rotate(-90deg);
  width: 50px;
  height: 50px;
}

.timer-circle-bg {
  fill: none;
  stroke: #242B42;
  stroke-width: 3;
}

.timer-circle-progress {
  fill: none;
  stroke: #3B62E2;
  stroke-width: 3;
  stroke-linecap: round;
  stroke-dasharray: 125.6;
  transition: stroke-dashoffset 1s linear;
}

.timer-number {
  position: absolute;
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.auto-enter-text {
  color: white;
  font-size: 14px;
  margin: 0;
}

/* 트랜지션 애니메이션 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>