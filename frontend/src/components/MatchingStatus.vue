<template>
  <div class="matching-status-container">
    <div class="matching-card">
      <!-- 확장된 뷰 -->
      <div class="expanded-content">
        <!-- 상단 헤더 섹션 -->
        <section class="header-section">
          <div class="header-main">
            <div class="category-info">
              <span class="label">주제 카테고리</span>
              <h2 class="category-title">
                {{ matchingDetails.category || "기본 카테고리" }}
                <span class="emoji">🔬</span>
              </h2>
            </div>
            <div class="status-info">
              <h3 class="status-title">함께 토론할 친구를 찾고 있어요!</h3>
              <p class="status-subtitle">
                {{ matchingDetails.format || "기본 형식" }} 토론을 위해 4명의
                참가자가 필요해요
              </p>
            </div>
          </div>
        </section>

        <!-- 참가자 섹션 -->
        <section class="participants-section">
          <div class="participants-header">
            <span class="label">현재 매칭된 인원</span>
            <span class="count">{{ matchedCount }}/4명</span>
          </div>
          <div class="participants-list">
            <TransitionGroup name="participant">
              <div v-for="(participant, index) in participants" :key="index" class="participant"
                :class="{ active: participant }">
                {{ participant || "?" }}
              </div>
            </TransitionGroup>
          </div>
        </section>

        <!-- 진행 상태바 -->
        <div class="progress-section">
          <div class="progress-container">
            <div class="progress-bar" :style="{ width: `${(matchedCount / 4) * 100}%` }"></div>
          </div>
        </div>

        <!-- 하단 취소 버튼 -->
        <div class="action-section">
          <button @click="handleCancel" class="cancel-button">
            매칭 취소하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Client } from "@stomp/stompjs";
import { useMatchingStore } from "@/stores/matchingStore";
import axios from "axios";
const backendUrl = import.meta.env.VITE_BACKEND_URL;
const wsUrl = import.meta.env.VITE_WS_URL;

const matchingStore = useMatchingStore();
const token = ref(localStorage.getItem("token"));
const router = useRouter();
const participants = ref(0);
const isModalOpen = ref(false);
const countdown = ref(5);
const debateId = ref(null);
const categoryCode = ref("POLITICS");
const matchingDetails = computed(() => matchingStore.matchingDetails || null);
const matchedCount = computed(() => matchingStore.matchedCount || 0);


console.log("matchingStore:", matchingStore);
console.log("matchingStore.isMatching:", matchingStore.isMatching);

const stompClient = new Client({
  brokerURL: wsUrl + "/ws",
  connectHeaders: { Authorization: `Bearer ${token.value}` },
  debug: (str) => console.log(str),
  reconnectDelay: 5000,
  onConnect: () => {
    console.log("WebSocket Connected");
    const userId = localStorage.getItem("id");

    // First subscription - for general matching updates
    stompClient.subscribe(`/user/${userId}/matching`, async (MESSAGE) => {
      const data = JSON.parse(MESSAGE.body);
      console.log("Received Message from matching topic:", data);
      console.log(data.messageType);
      switch (data.messageType) {
        case "MATCHING_UPDATE":
          if (data.payload && data.payload.waitingUserCount !== undefined) {
            console.log(
              "Updated waiting user count:",
              data.payload.waitingUserCount
            );
            matchingStore.setMatchedCount(data.payload.waitingUserCount);
          }
          break;

        case "MATCHING_SUCCESS":
          if (data.payload) {
            matchingStore.setDebateId(data.payload.debateId);
            localStorage.setItem("openviduToken", data.payload.openviduToken);
            // Log token for debugging
            console.log("Authorization Token:", token.value);

            // Fetch debate room info after successful matching
            try {
              const response = await axios.get(
                `${backendUrl}/api/debates/${data.payload.debateId}/room`,
                {
                  headers: {
                    Authorization: `Bearer ${token.value}`,
                    "Content-Type": "application/json",
                  },
                }
              );

              // console.log('API Response:', response);

              // Save participants info
              const subject = response.data.subject;
              const participants = response.data.users.map((user) => ({
                name: user.nickname,
                image: user.profileImageUrl,
              }));
              console.log("response_p:", response.data.users);
              console.log(participants);
              // Complete matching process
              matchingStore.completeMatching(subject, participants);
              handleMatchComplete();
              stompClient.deactivate();
            } catch (error) {
              // Handle 401 error specifically
              if (error.response && error.response.status === 401) {
                console.error("Unauthorized. Please check your token.");
                // You may want to log the user out or prompt them to reauthenticate
              } else {
                console.error(
                  "Failed to fetch debate room information:",
                  error
                );
              }
            }
          }
          break;

        case "CANCEL_SUCCESS":
          if (data.payload) {
            // Handle cancel success
            matchingStore.cancelMatching();
            participants.value = 0;
            stompClient.deactivate();
            console.log("Matching cancelled successfully");
          }
          break;

        default:
          console.log("Unhandled message type:", data.messageType);
          break;
      }
    });
    stompClient.publish({
      destination: `/pub/matching.join/${categoryCode.value}`,
      headers: {
        Authorization: `Bearer ${token.value}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        messageType: "JOIN",
        payload: { userId: userId },
      }),
    });
  },
  onDisconnect: () => console.log("WebSocket Disconnected"),
});

const startMatching = (topicCategoryCode) => {
  categoryCode.value = topicCategoryCode;
  console.log("소켓 연결 시도");
  stompClient.activate();
};

const handleMatchComplete = () => {
  // matchComplete.value = true;
  isModalOpen.value = true;
  countdown.value = 5;

  const countdownInterval = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(countdownInterval);
      isModalOpen.value = false;
      router.push(`/debate/${matchingStore.matchingDetails.debateId}`);
    }
  }, 1000);
};

const handleCancel = () => {
  const userId = localStorage.getItem("id");

  if (!userId) {
    console.error("User ID not found");
    return;
  }

  stompClient.publish({
    destination: `/pub/matching.cancel/${categoryCode.value}`,
    headers: {
      Authorization: `Bearer ${token.value}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      messageType: "CANCEL",
      payload: { userId },
    }),
  });
};

onMounted(() => {
  if (matchingStore.isMatching) {
    startMatching(matchingStore.matchingDetails.category);
  }
});
</script>

<style scoped>
.matching-status-container {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  width: 560px;
  /* 700px의 80% */
  z-index: 1000;
}

.matching-card {
  height: 290px;
  /* 360px의 80% */
  background: linear-gradient(135deg, #ffffff, #fff5f5);
  border: 1px solid rgba(255, 107, 107, 0.3);
  border-radius: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
  backdrop-filter: blur(8px);
}

/* 확장된 뷰 스타일 */
.expanded-content {
  padding-left: 30px;
  padding-right: 30px;
  padding-top: 30px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 헤더 섹션 스타일 */
.header-section {
  margin-bottom: 8px;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
}

.category-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a202c;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.emoji {
  font-size: 32px;
}

.status-info {
  text-align: center;
  max-width: 320px;
}

.status-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 12px;
}

.status-subtitle {
  font-size: 15px;
  color: #64748b;
  line-height: 1.6;
}

/* 참가자 섹션 스타일 */
.participants-section {
  display: flex;
  flex-direction: column;
  padding-top: 20px;
  z-index: 2;
}

.participants-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.label {
  font-size: 15px;
  color: #64748b;
  font-weight: 500;
}

.count {
  font-size: 15px;
  font-weight: 600;
  color: #ff6b6b;
}

.participants-list {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.participant {
  width: 51px;
  /* 64px의 80% */
  height: 51px;
  /* 64px의 80% */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  /* 폰트 사이즈도 약간 줄임 */
  font-weight: 500;
  background: #e2e8f0;
  color: #94a3b8;
  transition: all 0.3s ease;
}

.participant.active {
  background: linear-gradient(135deg, #ff6b6b, #ff8787);
  color: white;
  box-shadow: 0 6px 16px rgba(255, 107, 107, 0.2);
}

/* 진행 상태바 스타일 */
.progress-section {
  padding: 8px 0;
}

.progress-container {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(to right, #ff6b6b, #ff8787);
  border-radius: 4px;
  transition: width 0.5s ease;
}

/* 액션 섹션 스타일 */
.action-section {
  margin-top: 8px;
}

.cancel-button {
  width: 100%;
  padding: 13px;
  border: 2px solid #ff6b6b;
  border-radius: 16px;
  background: white;
  color: #ff6b6b;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-button:hover {
  background: #fff1f1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.1);
}

/* 축소된 뷰 스타일 */
.collapsed-content {
  height: 90px;
  padding: 0 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.collapsed-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.collapsed-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a202c;
  margin: 0;
}

.mini-progress {
  width: 100px;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

/* 애니메이션 */
.participant-enter-active,
.participant-leave-active {
  transition: all 0.3s ease;
}

.participant-enter-from,
.participant-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

.participant-move {
  transition: transform 0.3s ease;
}

.matching-status-container.expanded {
  height: 360px;
}
</style>
