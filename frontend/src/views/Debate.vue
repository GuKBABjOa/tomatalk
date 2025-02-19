<template>
  <main class="debate-discussion-container">
    <!-- 상단 상태 바 -->
    <div class="status-bar">
      <div class="round-label">{{ round }} 라운드</div>
      <div class="side-label">{{ currentSpeakerPosition }} 측</div>
      <h1 class="debate-topic">
        {{ subject }}
      </h1>
    </div>

    <!-- 메인 콘텐츠 영역 -->
    <div class="main-content">
      <!-- 왼쪽 섹션: 메인 발언자 및 참가자 영상 -->
      <div class="video-section">
        <!-- 메인 발언자 큰 영상 (4:3 비율) -->
        <div class="main-speaker-video">
          <!-- <video v-if="currentSpeaker" autoplay ref="mainVideoRef"></video> -->
          <video autoplay playsinline muted="false" ref="mainVideoRef"></video>
          <!-- <div class="video-placeholder">
            <span v-if=!participants.length>발언자가 없습니다.</span>
          </div> -->
          <div class="speaker-timer">
            <svg class="timer-circle" viewBox="0 0 60 60">
              <circle class="timer-bg" cx="30" cy="30" r="25" />
              <circle class="timer-progress" cx="30" cy="30" r="25" :style="{
                strokeDasharray: '157', // 원형 타이머 전체 둘래
                strokeDashoffset: (timerProgress / 100) * 157
              }" />
            </svg>
            <span class="timer-text">{{ formattedTime }}</span>
          </div>
        </div>

        <!-- 다른 참가자들 작은 영상들 (4:3 비율) -->
        <div class="participant-videos">
          <div v-for="participant in otherParticipants"
            :key="participant.stream?.connection?.connectionId || participant.nickname" class="participant-video">
            <video class="rounded-2xl" v-if="participant.stream" autoplay muted></video>
            <div v-else class="video-placeholder">
              <span>{{ participant.nickname }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="notes-container">
        <!-- 오른쪽 섹션: 검색 및 메모 -->
        <div class="notes-section" :class="{ visible: isNotesVisible }">
          <!-- Add Button -->
          <div class="add-button-container">
            <button @click="openModal" class="add-button">
              <span class="plus-icon">+</span> 새로운 메모/자료 추가
            </button>
          </div>

          <!-- Modal -->
          <div v-if="isModalOpen" class="modal-overlay">
            <div class="modal-container">
              <div class="modal-header">
                <h3>
                  {{ currentTab === "memo" ? "새 메모 작성" : "새 자료 추가" }}
                </h3>
                <button @click="closeModal" class="close-button">×</button>
              </div>

              <!-- Tabs -->
              <div class="tabs modal-tabs">
                <button @click="currentTab = 'memo'" :class="{ active: currentTab === 'memo' }">
                  메모
                </button>
                <button @click="currentTab = 'resource'" :class="{ active: currentTab === 'resource' }">
                  자료
                </button>
              </div>

              <!-- Content Area -->
              <div class="modal-content">
                <!-- 메모 입력 폼 -->
                <div v-if="currentTab === 'memo'" class="memo-input">
                  <textarea v-model="memoContent" placeholder="토론을 위한 메모를 자유롭게 작성하세요"></textarea>

                  <input v-model="memoTitle" placeholder="메모 제목을 입력하세요" />

                  <div class="tag-input">
                    <input v-model="newMemoTag" @keyup.enter="addMemoTag" placeholder="# 태그를 입력하세요" />
                    <button @click="addMemoTag">태그 추가</button>
                  </div>

                  <div class="tags">
                    <span v-for="tag in memoTags" :key="tag" class="tag">
                      # {{ tag }}
                      <button @click="removeMemoTag(tag)">×</button>
                    </span>
                  </div>
                </div>

                <!-- 자료 입력 폼 -->
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

                  <input v-if="selectedResourceType === 'image'" type="file" @change="handleFileUpload"
                    accept="image/*" />

                  <input v-model="resourceTitle" placeholder="자료 제목을 입력하세요" />

                  <div class="tag-input">
                    <input v-model="newResourceTag" @keyup.enter="addResourceTag" placeholder="# 태그를 입력하세요" />
                    <button @click="addResourceTag">태그 추가</button>
                  </div>

                  <div class="tags">
                    <span v-for="tag in resourceTags" :key="tag" class="tag">
                      # {{ tag }}
                      <button @click="removeResourceTag(tag)">×</button>
                    </span>
                  </div>
                </div>
              </div>

              <div class="modal-footer">
                <button @click="closeModal" class="cancel-button">취소</button>
                <button @click="handleSave" class="save-button modal-save">
                  저장
                </button>
              </div>
            </div>
          </div>
          <div class="search-area">
            <input v-model="searchQuery" placeholder="메모나 자료 검색" class="search-input" />
          </div>
          <h3 class="result">{{ searchQuery ? "검색 결과" : "메모" }}</h3>
          <div class="saved-items">
            <div v-for="item in filteredItems" :key="item.id" class="saved-item"
              :class="{ 'memo-item': item.type === 'memo', 'resource-item': item.type === 'resource' }">
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
                <span v-for="tag in [...item.tags]" :key="tag" class="tag"
                  :class="{ 'memo-tag': item.type === 'memo', 'resource-tag': item.type === 'resource' }">
                  # {{ tag }}
                </span>
              </div>
              <div class="item-content">
                {{
                  item.content.length > 100
                    ? item.content.slice(0, 100) + "..."
                    : item.content
                }}
              </div>
              <div class="item-footer">
                {{ item.createdAt }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 타임라인 -->
    <div class="timeline">
      <div class="timeline-track">
        <div class="progress-bar" :style="{ '--progress-width': progressBarWidth }"></div>
        <div v-for="(stage, index) in timelineStages" :key="index" class="timeline-stage" :class="getStageClass(index)">
          <div class="stage-point"></div>
          <div class="stage-label">
            <span class="stage-title">{{ stage.title }}</span>
            <span class="stage-subtitle">{{ stage.subtitle }}</span>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { onMounted, ref, watch, computed, onUnmounted } from "vue"
import axios from 'axios'
import { OpenVidu, Session, Stream } from "openvidu-browser"
import { useWebSocket } from "@/composables/useWebSocket"
import { useTimer } from "@/composables/useTimer"
// import { useMatchingStore } from "@/stores/matchingStore"
// import { log } from "console"

const wsUrl = import.meta.env.VITE_WS_URL + "/ws";

// const matchingStore = useMatchingStore()
// 수정 : Debater 타입 선언
interface Debater {
  userId: string,
  nickname: string,
  profileImageUrl: string,
  position: string,
  order: number,
  participant: boolean
  connectionId: string
  stream: any; // 구체적인 타입을 알고 있다면 사용
  placeholder?: boolean;
  addVideoElement?: (element: HTMLVideoElement) => void;
}

onMounted(() => {
  fetchDebateRoomInfo() // 입장하면 방 정보를 찾아와라
  joinSession() // OpenVidu 세션 참가

  watch(currentSpeaker, (newSpeaker) => {
    if (newSpeaker && mainVideoRef.value) {
      newSpeaker.addVideoElement(mainVideoRef.value)
    }
  })
  // 수정 : watch 타입 단언
  watch(
    () => otherParticipants.value as Debater[],
    (newParticipants: Debater[]) => {
      const videoElements = document.querySelectorAll(".participant-video video") as NodeListOf<HTMLVideoElement>;

      newParticipants
        .filter((participant): participant is Debater => !participant.placeholder) // 유효한 Debater만 필터링
        .forEach((participant, index) => {
          if (videoElements[index] && participant.addVideoElement) {
            participant.addVideoElement(videoElements[index]);
          }
        });
    }
  );

})
// 수정: unmount 시 cleanup
onUnmounted(() => {
  // Clean up video elements
  if (mainVideoRef.value) {
    mainVideoRef.value.srcObject = null;
  }

  // Clean up subscribers
  subscribers.value.forEach(subscriber => {
    if (subscriber.stream) {
      subscriber.stream.dispose();
    }
  });

  // Proper session cleanup
  leaveSession();
});

const mainVideoRef = ref<HTMLVideoElement | null>(null)

// Vue Router의 props로 전달된 debateId를 받음
const props = defineProps<{ debateId: string }>()

// 토론 방 정보를 받아오는 함수(토론 참여자인지, 방청객 인지 구분하고 토론 주제 확인)
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

// OpenVidu 세션 참가
const OV = ref<OpenVidu | null>(null)
const session = ref<Session | null>(null)
const subscribers = ref<any[]>([])
const publisher = ref<any>(null)
const mySessionId = ref<string>("SessionA")
const myUserName = ref<string>("Participant")
const userId = localStorage.getItem("id")
const isSpeaker = ref<boolean>(false)

const participants = computed(() => latestDebateState.value.participants || []);
// const clientData = computed(() => {
//     const { clientData } = getConnectionData();
//     return clientData;
//   });

//   function getConnectionData() {
//     const { connection } = props.streamManager.stream;
//     return JSON.parse(connection.data);
//   }

const joinSession = async () => {
  try {
    console.log("OpenVidu 세션에 연결 시도")
    OV.value = new OpenVidu()
    session.value = OV.value.initSession()

    // 스트림 생성 시 참가자 추가
    session.value.on("streamCreated", (event: any) => {
      const subscriber = session.value!.subscribe(event.stream, undefined);
      subscriber.subscribeToAudio(true);
      subscribers.value.push(subscriber); // 참가자 명단에 추가
      console.log("session.value:", session.value);

    })
    // console.log(session.value.connection.connectionId);
    // localStorage에서 토큰 가져오기
    const openviduToken = getToken()
    // OpenVidu 세션에 연결

    await session.value.connect(openviduToken, { clientData: localStorage.getItem("id") })

    // 비디오 스트림 생성
    // 수정 : participants 리스트에 있는 user만 publish
    const isPublisher = computed(() => {
      return participants.value.some((participant: Debater) => participant.userId === userId);
    });
    if (isPublisher) {
      publisher.value = OV.value.initPublisher(undefined, {
        audioSource: undefined,
        videoSource: undefined,
        publishAudio: true,
        publishVideo: true,
        resolution: "640x480",
        frameRate: 30,
        insertMode: "APPEND",
        mirror: true,
      })
      console.log("Publisher 객체:", publisher.value);

      session.value.publish(publisher.value)
    }
    console.log(session.value.connection.connectionId)
    console.log("OpenVidu 세션 연결 완료")
  } catch (error) {
    console.error("OpenVidu 세션 연결 중 오류 발생:", error)
  }
}

const getToken = () => {
  const openviduToken = localStorage.getItem("openviduToken")
  if (!openviduToken) {
    throw new Error("토큰이 존재하지 않습니다. 먼저 매칭 페이지에서 토큰을 받아와야 합니다.")
  }
  return openviduToken
}

const leaveSession = () => {  // 세션 종료 함수
  if (session.value) {
    session.value.disconnect();
  }
  session.value = null;
  OV.value = null;
  subscribers.value = [];
  publisher.value = null;
  console.log("OpenVidu 세션 종료")
}

// 마이크 & 카메라 ON/OFF 함수
const toggleAudio = () => {
  if (publisher.value) {
    publisher.value.publishAudio(!publisher.value.stream.audioActive);
  }
};

const toggleVideo = () => {
  if (publisher.value) {
    publisher.value.publishVideo(!publisher.value.stream.videoActive);
  }
};


// 토론 진행 정보 구독
// const wsUrl = `ws://localhost:8000/ws/debate/${props.debateId}`
const { messages, isConnected, debateState } = useWebSocket(wsUrl)
const latestDebateState = computed(() => debateState.value || { // 가장 최근의 토론 상태를 가져오기
  sequence: 0,
  status: "WAITING",
  currentSpeakerId: null,
  currentSpeakEndTime: null,
  nextSpeakerId: null,
  canInterrupt: false,
  isInterrupted: false,
  interruptSpeakerId: null,
  interruptEndTime: null,
  participants: []
})

const currentSpeakerPosition = computed(() => { // 현재 발언자 입장
  const currentSpeakerId = latestDebateState.value.currentSpeakerId
  const participants = latestDebateState.value.participants
  const currentPosition = participants.find((p: { userId: number }) => p.userId === currentSpeakerId)
  return currentPosition ? currentPosition.position : "찬성"
})

const nextSpeakerNickname = computed(() => {
  const nextSpeakerId = latestDebateState.value.nextSpeakerId;
  const participants = latestDebateState.value.participants;

  // 참가자 목록에서 nextSpeakerId와 일치하는 사람 찾기
  const nextSpeaker = participants.find((p: { userId: number }) => p.userId === nextSpeakerId);

  return nextSpeaker ? nextSpeaker.nickname : "다음 발언자 없음";
})
const currentSpeakerNickname = computed(() => {
  const currentSpeakerId = latestDebateState.value.currentSpeakerId
  const participants = latestDebateState.value.participants

  // 참가자 목록에서 currentSpeakerId와 일치하는 사람 찾기
  const currentSpeaker = participants.find((p: { userId: number }) => p.userId === currentSpeakerId)
  return currentSpeaker ? currentSpeaker.nickname : "현재 발언자 없음"
})

const chairman = computed(() => {
  switch (latestDebateState.value.status) {
    case "READY":
      return ["모든 참여자가 들어오길 기다리는 중입니다.", "잠시만 기다려주세요"];
    case "PREPARING":
      return [
        "곧 발언이 시작됩니다.",
        `${nextSpeakerNickname ? nextSpeakerNickname : "알 수 없음"} 님은 준비 해주세요`
      ]
    case "WAITING":
      return [
        "곧이어 다음 발언이 시작됩니다.",
        `${currentSpeakerNickname ? currentSpeakerNickname : "알 수 없음"} 님은 준비 해주세요`]
    case "STARTED":
      return ["토론이 시작되었습니다!", "첫 번째 발언자가 발표를 시작합니다."];
    case "SPEECHING":
      return ["현재 발언 중입니다.", "지금 발언자의 의견을 경청해주세요."];
    case "SKIP":
      return ["발언이 건너뛰어졌습니다.", "다음 발언자가 준비 중입니다."];
    case "FINISHED":
      return ["토론이 종료되었습니다.", "모두 수고하셨습니다!"];
    default:
      return ["상태를 불러오는 중...", "잠시만 기다려주세요"];
  }
})

// 라운드와 단계별 바 구현
const round = ref<number>(1)
const step = ref<number>(0)
let resetDone = false

const timelineStages = ref([
  { title: "찬성 입론", subtitle: "주장 제시" },
  { title: "반대 입론", subtitle: "대응 논리" },
  { title: "찬성 반론", subtitle: "반박 및 보완" },
  { title: "반대 반론", subtitle: "최종 반박" },
]);

const getStageClass = (index: number) => {
  if (index < step.value) return "completed"; // 지나간 단계는 completed
  if (index === step.value) return "current"; // 현재 단계는 current
  return ""; // 나머지는 기본 스타일
};

watch(() => latestDebateState.value.status, (newStatus) => {
  if (newStatus === "SPEECHING") {
    step.value++; // "SPEECHING" 상태가 될 때마다 step 증가

    if (step.value === 4) {
      if (!resetDone) {
        round.value = 2; // round 값을 2로 변경
        step.value = 0;  // step을 0으로 초기화 (첫 번째 반복만)
        resetDone = true; // 이후에는 다시 초기화되지 않도록 설정
        console.log("step 초기화 & round 증가");
      } else {
        console.log("step이 4지만 이미 초기화된 적이 있어 유지");
        step.value = 3
      }
    }

    console.log(`현재 step: ${step.value}, round: ${round.value}`);
    subscribers.value.forEach((subscriber, index) => {
      console.log(`🎧 Subscriber[${index}] audio 설정 확인:`);
      console.log("subscribeToAudio 상태:", subscriber.subscribeToAudio);
      console.log("subscriber.stream.audioActive:", subscriber.stream.audioActive);
    });

  }

  // `round`가 변경되었을 때 step 초기화 (단, 한 번만 실행)
  if (round.value === 2 && step.value === 4 && !resetDone) {
    step.value = 0;
    console.log("타임라인 초기화 (라운드 2 시작)");
  }
});

// 수정 : 발언권 자동관리
watch(() => latestDebateState.value, (newState) => {
  if (!publisher.value) return; // publisher가 없는 경우 안전하게 종료
  const newSpeakerId = newState.currentSpeakerConnectionId;
  if (newState.status === "SPEECHING" && newState.currentSpeakerId === userId) {
    publisher.value.publishAudio = true;  // 현재 사용자가 발언자이면 마이크 활성화
  } else {
    publisher.value.publishAudio = false; // 현재 사용자가 발언자가 아니면 마이크 비활성화
  }
  console.log(`발언자 업데이트됨: ${newSpeakerId}, 사용자(${userId}) 마이크: ${publisher.value.publishAudio}`);
}, { deep: true });



const progressBarWidth = computed(() => {
  return `${(step.value / 3) * 100}%`; // step이 0~3 사이에서 0%, 33%, 66%, 100%로 변경
});

// 현재 발언자의 캠을 메인 영역에, 나머지 참가자는 작은 영역에 배치하기
const currentSpeaker = computed(() => {
  subscribers.value.find(subscriber =>
    console.log('am I currentSpeaker?:', subscriber.stream.connection.connectionId === latestDebateState.value.currentSpeakerConnectionId)
  )
  return subscribers.value.find(subscriber =>
    subscriber.stream.connection.connectionId === latestDebateState.value.currentSpeakerConnectionId
  ) || publisher.value; // 만약 본인이 발언 중이면 publisher 사용
}
);


// 수정 : otherParticipants 구분
const otherParticipants = computed(() => {
  // const filteredParticipants = subscribers.value.filter(subscriber =>
  //   subscriber.stream.connection.connectionId !== latestDebateState.value.currentSpeakerConnectionId
  // );
  const filteredParticipants: Debater[] = latestDebateState.value.participants.filter((participant: Debater) =>
    participant.connectionId !== latestDebateState.value.currentSpeakerConnectionId);
  // console.log('oP:', otherParticipants)
  // 참가자가 없을 경우 기본 UI를 위한 placeholder 추가
  return filteredParticipants.length > 0 ? filteredParticipants : [
    { nickname: "참가자 없음", stream: null, placeholder: true },
    { nickname: "참가자 없음", stream: null, placeholder: true },
    { nickname: "참가자 없음", stream: null, placeholder: true }
  ];
});
// 타이머 관련
// 현재 발언 종료 시간 (백엔드에서 받아옴)
const currentSpeakEndTime = computed(() => latestDebateState.value.currentSpeakEndTime);

// useTimer 훅 사용
const { remainingTime, timerProgress, formattedTime } = useTimer(currentSpeakEndTime);

// ----------------------------------------------------------

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

// Save methods
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

  savedItemsStore.addMemo(newMemo)

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

// Modal state
const isModalOpen = ref(false);

// Modal methods
const openModal = () => {
  isModalOpen.value = true;
  // Reset form data when opening modal
  memoContent.value = "";
  memoTitle.value = "";
  memoTags.value = [];
  resourceUrl.value = "";
  resourceTitle.value = "";
  resourceTags.value = [];
  uploadedFile.value = null;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const handleSave = () => {
  if (currentTab.value === "memo") {
    saveMemo();
  } else {
    saveResource();
  }
  closeModal();
};

// 기존 imports 아래에 추가
const isNotesVisible = ref(false);

// 토글 함수 추가
const toggleNotes = () => {
  isNotesVisible.value = !isNotesVisible.value;
};

import { useSavedItemsStore } from "@/stores/savedItems";
const savedItemsStore = useSavedItemsStore() // pinia에서 저장된 메모 리스트 가져오기

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

<style scoped>
.debate-discussion-container {
  max-width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 1vh 2vw;
  display: flex;
  flex-direction: column;
  font-family: "Pretendard", sans-serif;
}

/* 상단 상태 바 */
.status-bar {
  display: flex;
  align-items: center;
  background-color: white;
  border-bottom: 1px solid #cacaca;
  /* border-radius: 16px; */
  padding: 1vh 1vw;
  margin-bottom: 1vh;
  height: 8vh;
}

.round-label {
  background-color: #ff6b6b;
  color: white;
  padding: 10px 20px;
  border-radius: 25px;
  margin-right: 20px;
}

.side-label {
  background-color: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
  padding: 10px 20px;
  border-radius: 25px;
  margin-right: 20px;
}

.debate-topic {
  font-size: clamp(16px, 2vw, 20px);
  color: #212529;
}

/* 모달 입력 폼 스타일 */
.memo-input textarea,
.resource-input textarea {
  width: 100%;
  height: 200px;
  margin-bottom: 16px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  resize: vertical;
}

.memo-input input,
.resource-input input {
  width: 100%;
  padding: 12px;
  margin-bottom: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
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

.tag-input {
  display: flex;
  margin-bottom: 16px;
}

.tag-input input {
  flex: 1;
  margin-right: 10px;
  margin-bottom: 0;
}

.tag-input button {
  padding: 12px 20px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.notes-container {
  max-width: 100%;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.notes-container::-webkit-scrollbar {
  display: none;
}


.modal-content .tags {
  margin-bottom: 16px;
}

.modal-content .tag {
  display: inline-flex;
  align-items: center;
  background: #fff1f1;
  color: #ff6b6b;
  margin-right: 8px;
  margin-bottom: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
}

.modal-content .tag button {
  background: none;
  border: none;
  color: #ff6b6b;
  margin-left: 6px;
  cursor: pointer;
  font-size: 16px;
}

/* 메인 콘텐츠 영역 */
.main-content {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 0fr;
  gap: 1vw;
  height: 75vh;
  overflow: hidden;
  transition: grid-template-columns 0.3s ease;
  grid-template-columns: 1fr 300px;
}

.main-content.notes-expanded {
  grid-template-columns: 3fr 1fr;
}

/* 비디오 섹션 */
.video-section {
  display: flex;
  flex-direction: column;
  gap: 1vh;
}

.main-speaker-video {
  background-color: #e9ecef;
  border-radius: 1rem;
  height: 50vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.video-placeholder span {
  color: #666;
}

.speaker-timer {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 60px;
  height: 60px;
  background: transparent;
}

.timer-circle {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.timer-circle circle {
  fill: none;
  stroke-width: 4;
}

.timer-circle .timer-bg {
  stroke: rgba(0, 0, 0, 0.1);
}

.timer-circle .timer-progress {
  stroke: #ff6b6b;
  stroke-dasharray: 157;
  /* 원의 둘레 (2 * π * 반지름) */
  /* stroke-dashoffset: 39.25;  */
  /* 75% 진행 상태 (157 * 0.25) */
  transition: stroke-dashoffset 1s linear;
}

.timer-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 14px;
  color: #333;
}

.add-button-container {
  margin-bottom: 20px;
}

.add-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 12px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.plus-icon {
  font-size: 20px;
  margin-right: 8px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 24px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: bold;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
}

.modal-tabs {
  margin-bottom: 20px;
}

.modal-content {
  margin-bottom: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.cancel-button {
  padding: 12px 24px;
  background: #f3f4f6;
  border: none;
  border-radius: 8px;
  color: #6b7280;
  cursor: pointer;
}

.modal-save {
  width: auto;
  padding: 12px 24px;
}

.participant-videos {
  display: flex;
  height: 24vh;
  gap: 1vw;
}

.participant-video {
  flex: 1;
  background-color: #e9ecef;
  border-radius: 1rem;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 메모 토글 버튼 */
.notes-toggle {
  position: fixed;
  right: 2vw;
  top: 12vh;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #ff6b6b;
  color: white;
  border: none;
  cursor: pointer;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.notes-toggle.active {
  right: calc(25% + 2vw);
}

/* 메모 섹션 */
.notes-section {
  background-color: white;
  border: 2px solid #e9ecef;
  border-radius: 16px;
  padding: 1vh 1vw;
  height: 100%;
  overflow: hidden;
  transform-origin: right;
  transition: transform 0.3s ease;
  scrollbar-width: none;
  -ms-overflow-style: none;
  opacity: 1;
  /* visible 클래스 없이도 항상 보이도록 */
  visibility: visible;
  transform: none;
  /* transform 제거 */
}

.notes-section.visible {
  transform: scaleX(1);
  opacity: 1;
  visibility: visible;
}

.notes-section::-webkit-scrollbar {
  display: none;
}

.search-area {
  display: flex;
  margin-bottom: 1vh;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
}

.search-input {
  flex-grow: 1 1 auto;
  padding: 1vh 1vw;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  /* min-width: 0; */
  width: 100%;
}

.search-button {
  padding: 1vh 1vw;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  flex-shrink: 0;
}

.saved-items {
  overflow-y: auto;
  max-height: 70%;
  border-radius: 8px;
  -ms-overflow-style: none;
  padding-right: 0.25rem;
}

/* 스크롤바 스타일 */
.saved-items::-webkit-scrollbar {
  width: 6px;
  margin-left: 5px;
}

.saved-items::-webkit-scrollbar-track {
  opacity: 0;
  border-radius: 10px;
}

.saved-items::-webkit-scrollbar-thumb {
  background: rgb(246, 246, 246);
  border-radius: 1rem;
}

.result {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 1vh;
}

.saved-item {
  border-radius: 8px;
  padding: 1vh 1vw;
  margin-bottom: 1vh;
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

/* 타임라인 */
.timeline {
  margin-top: 1vh;
  padding: 1vh 2vw;
  height: 12vh;
}

.timeline-track {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.9rem;
}

.progress-bar {
  position: absolute;
  top: 20px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #e9ecef;
  z-index: 1;
}

.progress-bar::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: var(--progress-width, 0%);
  /* 동적 변경 */
  height: 100%;
  background-color: #ff6b6b;
  transition: width 0.5s ease-in-out;
  /* 부드러운 애니메이션 */
}

.timeline-stage {
  position: relative;
  text-align: center;
  z-index: 2;
}

.stage-point {
  width: 12px;
  height: 12px;
  background-color: #e9ecef;
  border-radius: 50%;
  margin: 0 auto 8px;
}

.timeline-stage.completed .stage-point {
  background-color: #ff6b6b;
}

.timeline-stage.current .stage-point {
  background-color: #ff6b6b;
  animation: pulse 1.5s infinite;
}

.stage-label {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: max-content;
}

.stage-title {
  display: block;
  color: #495057;
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 4px;
}

.stage-subtitle {
  display: block;
  color: #666;
  font-size: 12px;
}

@keyframes pulse {

  0%,
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(255, 107, 107, 0.4);
  }

  50% {
    transform: scale(1.2);
    box-shadow: 0 0 0 6px rgba(255, 107, 107, 0);
  }
}

/* 모바일 반응형 */
@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .main-content.notes-expanded {
    grid-template-columns: 0fr 1fr;
  }

  .video-section {
    height: 50vh;
  }

  .main-speaker-video {
    height: 30vh;
  }

  .participant-videos {
    height: 18vh;
  }

  .notes-section {
    width: 100%;
  }

  .notes-toggle.active {
    right: 2vw;
  }

  .timeline {
    height: 10vh;
  }

  .stage-subtitle {
    display: none;
  }

  .stage-title {
    font-size: 12px;
  }

  .main-content.notes-expanded .video-section {
    display: none;
  }
}
</style>
