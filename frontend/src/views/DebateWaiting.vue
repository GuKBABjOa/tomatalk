<template>
  <main class="debate-preparation-container">
    <!-- Debate Preparation Header -->
    <section class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-info">
          <div class="topic-section">
            <h2>토론 주제</h2>
          </div>
          <div class="flex justify-between">
            <p class="subject">{{ subject }}</p>
            <div class="stance-section">
              <h2>내 입장 : {{ myStance }}</h2>
            </div>
          </div>
        </div>
        <div class="timer-section flex justify-between">
          <h2>곧 토론이 시작됩니다!</h2>
          <div class="timer">{{ formatTime }}</div>
        </div>
      </div>
    </section>

    <div class="main-content py-2">
      <!-- Left Panel -->
      <div class="left-panel">
        <!-- Tabs -->
        <div class="tabs">
          <button @click="currentTab = 'memo'" :class="{ active: currentTab === 'memo' }">
            메모
          </button>
          <button @click="currentTab = 'resource'" :class="{ active: currentTab === 'resource' }">
            자료
          </button>
        </div>

        <!-- Content Area -->
        <div class="content-area">
          <!-- Memo Tab -->
          <div v-if="currentTab === 'memo'" class="memo-input">
            <textarea v-model="memoContent" placeholder="토론을 위한 메모를 자유롭게 작성하세요"></textarea>

            <input v-model="memoTitle" placeholder="메모 제목을 입력하세요" />

            <div class="tag-input">
              <input v-model="newMemoTag" @keyup.enter="addMemoTag" placeholder="# 태그를 입력하세요" />
              <button @click="addMemoTag" class="tagbutton">태그 추가</button>
            </div>

            <div class="tags">
              <span v-for="tag in memoTags" :key="tag" class="tag">
                # {{ tag }}
                <button @click="removeMemoTag(tag)">×</button>
              </span>
            </div>

            <button @click="saveMemo" class="save-button">메모 저장</button>
          </div>

          <!-- Resource Tab -->
          <div v-else class="resource-input">
            <div class="resource-type-selection">
              <button v-for="type in resourceTypes" :key="type.value" @click="selectedResourceType = type.value"
                :class="{ active: selectedResourceType === type.value }">
                {{ type.label }}
              </button>
            </div>

            <input v-if="selectedResourceType === 'url'" v-model="resourceUrl" placeholder="URL을 입력하세요" />

            <input v-if="selectedResourceType === 'document'" type="file" @change="handleFileUpload"
              accept=".pdf,.doc,.docx" />

            <input v-if="selectedResourceType === 'image'" type="file" @change="handleFileUpload" accept="image/*" />

            <input v-model="resourceTitle" placeholder="자료 제목을 입력하세요" />

            <div class="tag-input">
              <input v-model="newResourceTag" @keyup.enter="addResourceTag" placeholder="# 태그를 입력하세요" />
              <button @click="addResourceTag" class="tagbutton">태그 추가</button>
            </div>

            <div class="tags">
              <span v-for="tag in resourceTags" :key="tag" class="tag">
                # {{ tag }}
                <button @click="removeResourceTag(tag)">×</button>
              </span>
            </div>

            <button @click="saveResource" class="save-button">자료 저장</button>
          </div>
        </div>
      </div>

      <!-- Right Panel -->
      <div class="right-panel">
        <div class="search-area">
          <input v-model="searchQuery" placeholder="메모나 자료 검색" />
        </div>
        <h3 class="result">{{ searchQuery ? "검색 결과" : "최근 저장됨" }}</h3>
        <div class="saved-items">
          <div v-for="item in filteredItems" :key="item.id" class="saved-item" :class="{
            'memo-item': item.type === 'memo',
            'resource-item': item.type === 'resource',
          }">
            <div class="item-header">
              <span class="item-title">{{ item.title }}</span>
              <span class="item-type">
                {{
                  item.type === "memo"
                    ? "메모"
                    : getResourceEmoji(item.resourceType || "")
                }}
              </span>
            </div>
            <div class="item-tags">
              <span v-for="tag in item.tags" :key="tag" class="tag" :class="{
                'memo-tag': item.type === 'memo',
                'resource-tag': item.type === 'resource',
              }">
                # {{ tag }}
              </span>
            </div>
            <div class="item-content">
              {{
                item.content
              }}
            </div>
            <div class="item-footer">
              {{ item.createdAt }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from "vue";
import { useWebSocket } from "@/composables/useWebSocket";
import { useRouter } from "vue-router";
import { useSavedItemsStore } from "@/stores/savedItems";
import axios from 'axios'


// Vue Router 사용
const router = useRouter()

// Vue Router의 props로 전달된 debateId를 받음
const props = defineProps<{ debateId: string }>()

// 웹 소켓에서 상태 가져오기
const wsUrl = `${import.meta.env.VITE_WS_URL}/ws/sub/debate/${props.debateId}`
const { debateState } = useWebSocket(wsUrl);

// 현재 상태 가져오기
const currentStatus = computed(() => debateState.value?.status || "PREPARING");

// 상태 변화 감지하여 페이지 이동
// watch(currentStatus, (newStatus) => {
//   if (newStatus === "WAITING") {
//     console.log(`🚀 토론 시작! debate 페이지로 이동합니다. (debateId: ${props.debateId})`);

//     router.push({
//       name: "debate",
//       params: { debateId: props.debateId },
//     })
//   }
// })

const subject = ref<string>("") // 토론 주제
const participant = ref<boolean>(true) // 토론 참여자인 경우 true, 아닐 경우 false
const users = ref<{ userId: number; nickname: string; profileImage: string; position: string; order: number }[]>([])
const fetchDebateRoomInfo = async () => {
  try {
    const token = localStorage.getItem("token") // api 요청 해더에 token 값 필요함
    console.log('토큰 확인:', token)
    if (!token) {
      throw new Error("로그인이 필요합니다.")
    }

    const response = await axios.get(`/api/debates/${props.debateId}/room`, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    })

    if (response.status === 200) {
      subject.value = response.data.subject
      participant.value = response.data.participant
      users.value = response.data.users
    }
  } catch (error: any) {
    console.error("토론방 정보를 불러오는 데 실패했습니다:", error.response?.data || error.message)
  }
}

// 로그인 한 사용자 Id 받아오기
const myUserId = localStorage.getItem("userId") || "";

const myStance = computed(() => {
  const participants = debateState.value?.participants || [];

  // 내 userId와 일치하는 참가자 찾기
  const myParticipant = participants.find((p: { userId: string }) => p.userId === myUserId);

  // 입장 (찬성 or 반대) 반환, 없으면 "알 수 없음"
  return myParticipant ? myParticipant.position : "찬성";
});

// 메모 관련 복구 ---------------------------------------------------------
const savedItemsStore = useSavedItemsStore(); // pinia에서 저장된 메모 리스트 가져오기
const filteredItems = computed(() => {
  const query = searchQuery.value.toLowerCase().trim();

  if (!query) return savedItemsStore.savedItems;

  return savedItemsStore.savedItems.filter((item) => {
    const title = item.title?.toLowerCase() || "";
    const content = item.content?.toLowerCase() || "";
    const tags = item.tags || [];

    return (
      title.includes(query) ||
      content.includes(query) ||
      tags.some((tag: string) => tag.toLowerCase().includes(query))
    );
  });
});


// 기존 코드 -------------------------------------------------------------
const timeLeft = ref(20); // 남은 시간 (초)
const timer = ref<NodeJS.Timeout | null>(null);

// // 남은 시간을 MM:SS 형식으로 변환
const formatTime = computed(() => {
  const minutes = Math.floor(timeLeft.value / 60);
  const seconds = timeLeft.value % 60;
  return `${minutes}:${seconds.toString().padStart(2, "0")}`;
});

// time update 함수
// const updateTimeLeft = () => {
//   if (!debateState.value || !debateState.value.currentSpeakEndTime) {
//     timeLeft.value = 0;
//     return;
//   }

//   const endTime = new Date(debateState.value.currentSpeakEndTime).getTime();
//   const now = new Date().getTime();
//   timeLeft.value = Math.max(0, Math.floor((endTime - now) / 1000));
// };

// 타이머 시작 함수 (1초마다 업데이트)
// const startTimer = () => {
//   updateTimeLeft(); // 초기값 설정

//   if (timer.value) clearInterval(timer.value); // 기존 타이머 제거

//   timer.value = setInterval(() => {
//     updateTimeLeft();
//     if (timeLeft.value <= 0) {
//       if (timer.value !== null) {
//         clearInterval(timer.value);
//       }
//       timer.value = null;
//       console.log("⏳ 준비 시간이 종료되었습니다!");
//     }
//   }, 1000);
// };
const startTimer = () => {
  timeLeft.value = 20; // 20초로 고정

  if (timer.value) clearInterval(timer.value); // 기존 타이머 제거

  timer.value = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--;
    } else {
      clearInterval(timer.value!);
      timer.value = null;
    }
  }, 1000);
};

// `currentSpeakEndTime`이 변경될 때 타이머 재시작
// watch(() => debateState.value?.currentSpeakEndTime, (newTime) => {
//   if (!newTime) return; // newTime이 없으면 실행 안 함
//   console.log("⏳ 새로운 발언 시간이 설정됨:", newTime);
//   startTimer();
// });

onMounted(() => {
  console.log("20초 후 토론 페이지로 이동합니다.")

  fetchDebateRoomInfo();
  startTimer();

  setTimeout(() => {
    console.log('드가자')
    router.push({
      name: "debateRoom",
      params: { debateId: props.debateId }, // debateId 전달
    });
  }, 20000); // 20초 후 이동
});

onUnmounted(() => {
  if (timer.value) clearInterval(timer.value);
});

// Tab management
const currentTab = ref<"memo" | "resource">("memo");

// Memo-related states
const memoContent = ref("");
const memoTitle = ref("");
const memoTags = ref<string[]>([]);
const newMemoTag = ref("");

// Resource-related states
const resourceTypes = [
  { value: "url", label: "🔗 URL" },
  { value: "document", label: "📄 문서" },
  { value: "image", label: "🖼️ 이미지" },
];
const selectedResourceType = ref<string>("url");
const resourceUrl = ref("");
const resourceTitle = ref("");
const resourceTags = ref<string[]>([]);
const newResourceTag = ref("");
const uploadedFile = ref<File | null>(null);

// Search-related states
const searchQuery = ref("");

// Memo tag management
const addMemoTag = () => {
  if (newMemoTag.value.trim()) {
    memoTags.value.push(newMemoTag.value.trim());
    newMemoTag.value = "";
  }
};

const removeMemoTag = (tag: string) => {
  memoTags.value = memoTags.value.filter((t) => t !== tag);
};

// Resource tag management
const addResourceTag = () => {
  if (newResourceTag.value.trim()) {
    resourceTags.value.push(newResourceTag.value.trim());
    newResourceTag.value = "";
  }
};

const removeResourceTag = (tag: string) => {
  resourceTags.value = resourceTags.value.filter((t) => t !== tag);
};

// File upload handler
const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    uploadedFile.value = target.files[0];
  }
};

// 메모와 자료 저장 로직직
const saveMemo = () => {
  if (!memoTitle.value.trim()) {
    alert("메모 제목을 입력해주세요");
    return;
  }

  const newMemo = {
    id: Date.now().toString(),
    title: memoTitle.value,
    content: memoContent.value,
    tags: memoTags.value,
    type: "memo" as const,
    createdAt: new Date().toLocaleDateString("ko-KR"),
  };

  savedItemsStore.addMemo(newMemo);

  // Reset memo inputs
  memoContent.value = "";
  memoTitle.value = "";
  memoTags.value = [];
};

const saveResource = () => {
  if (!resourceTitle.value.trim()) {
    alert("자료 제목을 입력해주세요");
    return;
  }

  const newResource = {
    id: Date.now().toString(),
    title: resourceTitle.value,
    content:
      selectedResourceType.value === "url"
        ? resourceUrl.value
        : uploadedFile.value?.name || "",
    tags: resourceTags.value,
    type: "resource" as const,
    resourceType: selectedResourceType.value,
    createdAt: new Date().toLocaleDateString("ko-KR"),
  };

  savedItemsStore.addResource(newResource);

  // Reset resource inputs
  resourceUrl.value = "";
  resourceTitle.value = "";
  resourceTags.value = [];
  uploadedFile.value = null;
  selectedResourceType.value = "url";
};

// Helper method for resource type emoji
const getResourceEmoji = (type: string) => {
  switch (type) {
    case "url":
      return "🔗";
    case "document":
      return "📄";
    case "image":
      return "🖼️";
    default:
      return "📁";
  }
};
</script>

<!-- style 섹션만 수정이 필요합니다 -->
<style scoped>
.debate-preparation-container {
  max-width: 1280px;
  margin: 0 auto;
  background: #ffffff;
  max-height: 100vh;
}

.welcome-section {
  background: none;
  border-radius: 16px;
  border: 2px solid #cacaca;
  padding: 20px;
  margin-top: 20px;
  margin-bottom: 20px;
}

.topic-section h2 {
  margin-bottom: 0;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  /* height: 100%; */
  padding: 0 20px;
}

.welcome-info {
  flex: 2;
}

/* .topic-section,
.stance-section {
  margin-bottom: 16px;
} */

.timer-section {
  padding-left: 1.5rem;
  padding-right: 1.5rem;
}

.timer-section h2 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
}

.topic-section h2 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
}

.stance-section {
  display: flex;
  align-items: flex-end;
}

.stance-section h2 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
}

.subject {
  font-size: 24px;
  color: #ff6b6b;
  font-weight: bold;
}

.stance-select {
  padding: 8px 16px;
  border-radius: 8px;
  border: 2px solid #ff6b6b;
  font-size: 16px;
  color: #ff6b6b;
  background: white;
  cursor: pointer;
}

.timer-section {
  flex: 1;
  text-align: center;
  background: #fff1f1;
  padding: 16px;
  border-radius: 8px;
  margin-left: 20px;
}

.timer {
  font-size: 1.5rem;
  font-weight: bold;
  color: #ff6b6b;
}

.main-content {
  display: flex;
  gap: 20px;
  max-height: 100vh;
}

.left-panel {
  flex: 2;
  background: rgb(246, 246, 246);
  border-radius: 16px;
  padding: 32px;
}

.right-panel {
  flex: 1;
  background: white;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid #e5e7eb;
}

.tabs {
  display: flex;
  margin-bottom: 24px;
}

.tabs button {
  flex: 1;
  padding: 12px;
  background: #f3f4f6;
  border: none;
  font-size: 16px;
  font-weight: bold;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.tabs button.active {
  background: #ff6b6b;
  color: white;
}

.content-area textarea {
  width: 100%;
  height: 180px;
  margin-bottom: 16px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  resize: none;
}

.content-area input {
  width: 100%;
  padding: 12px;
  margin-bottom: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.tag-input {
  display: flex;
}

.tag-input input {
  flex: 1;
  margin-right: 10px;
  margin-bottom: 1rem;
}

.tag-input button {
  background: #ff6b6b;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  cursor: pointer;
}

.tags .tag {
  background: #fff1f1;
  color: #ff6b6b;
  margin-right: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  display: inline-flex;
  align-items: center;
  font-size: 14px;
  margin-bottom: 12px;
}

.tags .tag button {
  background: none;
  border: none;
  color: #ff6b6b;
  margin-left: 6px;
  cursor: pointer;
}

.save-button {
  width: 100%;
  background: #ff6b6b;
  color: white;
  border: none;
  padding: 16px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.save-button:hover {
  background: #ff5151;
}

.resource-type-selection {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.resource-type-selection button {
  flex: 1;
  padding: 12px;
  background: #f3f4f6;
  border: none;
  border-radius: 8px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.resource-type-selection button.active {
  background: #ff6b6b;
  color: white;
}

.search-area {
  display: flex;
  margin-bottom: 24px;
}

.search-area input {
  flex: 1;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px 0 0 8px;
}

.result {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 16px;
}

.saved-items {
  overflow-y: auto;
  max-height: 83%;
  scrollbar-width: none;
  -ms-overflow-style: none;
  border-radius: 8px;
}

.saved-item {
  background: #fff1f1;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  overflow-y: auto;
}

/* 메모 스타일 */
.memo-item {
  background: #fff1f1;
  /* 연한 핑크 */
}

/* 자료 스타일 */
.resource-item {
  background: #f0f9ff;
  /* 연한 블루 */
}

/* 메모 태그 스타일 */
.memo-tag {
  background: #ff6b6b;
  /* 진한 핑크 */
  color: white;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 12px;
  margin-right: 6px;
}

/* 자료 태그 스타일 */
.resource-tag {
  background: #3b82f6;
  /* 진한 블루 */
  color: white;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 12px;
  margin-right: 6px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.item-title {
  font-size: 16px;
  font-weight: bold;
  color: #111827;
}

.item-type {
  color: #6b7280;
}

.item-tags {
  margin-bottom: 12px;
}

.item-tags .tag {
  /* background: #ff6b6b; */
  color: white;
  margin-right: 8px;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 12px;
}

.item-content {
  color: #4b5563;
  margin-bottom: 12px;
}

.item-footer {
  color: #6b7280;
  font-size: 14px;
}

.tagbutton {
  height: 100%;
  margin-bottom: 8px;
}
</style>
