<template>
  <Transition name="modal">
    <div class="modal-overlay" @click="$emit('close')">
      <div class="modal-content" @click.stop>
        <h3 class="modal-title">모든 참가자가 준비되었습니다</h3>
        <div class="profile-container">
          <template
            v-for="(participant, index) in participantsData"
            :key="participant.name"
          >
            <div
              class="profile-item"
              :class="{ 'current-user': participant.name === 'YOU' }"
            >
              <ProfileImage :src="participant.image" :size="50" />
              <span class="profile-name">{{ participant.name }}</span>
            </div>
            <div
              v-if="index < participantsData.length - 1"
              class="connector"
            ></div>
          </template>
        </div>
        <div class="discussion-info">
          <span class="info-label">토론 주제</span>
          <p class="info-content">{{ discussionTopic }}</p>
        </div>
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
                    strokeDashoffset: `${(1 - countdown / 5) * 125.6}px`,
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

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import ProfileImage from "@/components/ProfileImage.vue";
import { useMatchingStore } from "@/stores/matchingStore";

const matchingStore = useMatchingStore();

interface Participant {
  name: string;
  image: string | null;
}

interface Props {
  initialCountdown: number;
  debateId: string | number | null; // null 허용하도록 수정
}

const props = defineProps<Props>();
const emit = defineEmits(["close", "startDiscussion", "resetMatching"]);

const countdown = ref(props.initialCountdown);
const router = useRouter();
const participantsData = ref<Participant[]>([]);
const discussionTopic = ref<string>("");

onMounted(async () => {
  // 토론방 정보 가져오기
  discussionTopic.value = matchingStore.matchingDetails.subject;
  const interval = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(interval);
      emit("startDiscussion");
      emit("resetMatching");
      router.push(`/debate/${props.debateId}`); // debateId 사용하여 라우팅
    }
  }, 1000);
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(255, 255, 255, 0.9);
  display: grid;
  place-items: center;
  z-index: 50;
}

.modal-content {
  width: 400px;
  padding: 2rem 1.5rem;
  background-color: #ffffff;
  border-radius: 16px;
  border: 2px solid #cacaca;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.modal-title {
  color: #111827;
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin: 0;
}

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
  margin-top: 8px;
  color: #4b5563;
  font-size: 14px;
  text-align: center;
}

.connector {
  width: 22px;
  height: 2px;
  background-color: #e5e7eb;
}

.discussion-info {
  background-color: rgb(246, 246, 246);
  padding: 1.5rem;
  border-radius: 16px;
}

.info-label {
  color: #6b7280;
  font-size: 16px;
  display: block;
  margin-bottom: 0.5rem;
}

.info-content {
  color: #111827;
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

.timer-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.timer-text {
  color: #4b5563;
  font-size: 16px;
  margin: 0;
}

.timer-container {
  position: relative;
  width: 60px;
  height: 60px;
}

.countdown {
  position: relative;
  display: grid;
  place-items: center;
}

.timer-circle {
  transform: rotate(-90deg);
  width: 60px;
  height: 60px;
}

.timer-circle-bg {
  fill: none;
  stroke: #e5e7eb;
  stroke-width: 4;
}

.timer-circle-progress {
  fill: none;
  stroke: #ff6b6b;
  stroke-width: 4;
  stroke-linecap: round;
  stroke-dasharray: 125.6;
  transition: stroke-dashoffset 1s linear;
}

.timer-number {
  position: absolute;
  color: #111827;
  font-size: 24px;
  font-weight: bold;
}

.auto-enter-text {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.modal-enter-to,
.modal-leave-from {
  opacity: 1;
  transform: scale(1);
}
</style>
