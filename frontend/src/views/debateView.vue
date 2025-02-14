<template>
  <div class="grid grid-cols-4 min-h-screen">
    <div class="col-span-3 grid grid-rows-3 max-h-screen">
      <div class="bg-black row-span-1 p-4">
        <!-- 사회자 -->
        <div class="relative grid bg-blue-900 min-h-full p-4 rounded-md content-between">
          <p class="text-3xl text-white mb-4">{{ currentModeratorMessage }}</p>
          <p class="text-2xl text-white">{{ gameState.gameTopicDetail }}</p>
          <button class="absolute top-4 right-4 bg-red-500 p-3 rounded-md text-white" @click="stopStreaming">발언
            종료</button>
          <button class="absolute top-4 left-4 bg-green-500 p-3 rounded-md text-white" @click="startStreaming">발언
            시작</button>
          <div class="rounded-md bg-blue-500 p-4 text-white">
            <div class="grid grid-cols-2 justify-between">
              <p>{{ speakerType }} 발언자: {{ Speaker }}</p>
              <p class="text-right">남은 시간: {{ formattedTime }}</p>
            </div>
            <div class="relative w-full h-4 bg-blue-300 rounded-md overflow-hidden mt-2">
              <!-- 시간 슬라이더 -->
              <div class="absolute top-0 left-0 h-full bg-blue-600" :style="{ width: progressWidth + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="bg-black row-span-2 p-4 text-white">
        <!-- 토론자 -->
        <div class="h-full grid grid-cols-2 grid-rows-2 gap-4">
          <!-- 찬성 1번 -->
          <div
            class="bg-gray-800 col-span-1 row-span-1 rounded-md relative flex items-center justify-center max-h-full">
            <UserVideo class="video" :streamManager="pro1Stream" />
            <div class="absolute bottom-4 left-4">
              {{ pro1?.nickname }} (찬성 1번)
            </div>
          </div>
          <!-- 반대 1번 -->
          <div
            class="bg-gray-800 col-span-1 row-span-1 rounded-md relative flex items-center justify-center max-h-full">
            <UserVideo class="video" :streamManager="con1Stream" />
            <div class="absolute bottom-4 left-4">
              {{ con1?.nickname }} (반대 1번)
            </div>
          </div>
          <!-- 찬성 2번 -->
          <div
            class="bg-gray-800 col-span-1 row-span-1 rounded-md relative flex items-center justify-center max-h-full">
            <UserVideo class="video" :streamManager="pro2Stream" />
            <div class="absolute bottom-4 left-4">
              {{ pro2?.nickname }} (찬성 2번)
            </div>
          </div>
          <!-- 반대 2번 -->
          <div
            class="bg-gray-800 col-span-1 row-span-1 rounded-md relative flex items-center justify-center max-h-full">
            <UserVideo class="video" :streamManager="con2Stream" />
            <div class="absolute bottom-4 left-4">
              {{ con2?.nickname }} (반대 2번)
            </div>
          </div>
          <!-- <div
            v-for="(stream, index) in allStreams"
            :key="index"
            class="bg-gray-800 col-span-1 row-span-1 rounded-md relative flex items-center justify-center max-h-full"
          > -->
          <!-- 캠 화면 (OpenVidu 스트림) -->
          <!-- <user-video
              :stream-manager="stream"
              class="video rounded-md"
            /> -->

          <!-- 참여자 이름 -->
          <!-- <div class="absolute bottom-4 left-4">
              {{ stream.stream.connection.data }}
            </div> -->

          <!-- 경고 횟수에 따른 노란딱지 -->
          <!-- <div v-if="participant.warningCount > 0" class="absolute bottom-4 right-4 flex gap-1">
              <span v-for="n in participant.warningCount" :key="n" class="text-yellow-400">🟨</span>
            </div> -->
          <!-- </div> -->
        </div>
      </div>
    </div>

    <!-- 우측 사이드 패널 (채팅, 메모장, 버튼) -->
    <div class="col-span-1 bg-black text-white relative" v-if="participant">
      <div class="memo p-4 rounded-md h-full flex flex-col">
        <p class="text-lg font-semibold mb-2">📝 개인 메모</p>
        <textarea v-model="memo"
          class="w-full h-full bg-gray-900 p-2 rounded-md text-white focus:outline-none focus:ring-2 focus:ring-blue-400 overflow-y-auto custom-scrollbar"
          placeholder="여기에 메모하세요..."></textarea>
      </div>
      <!-- 마이크, 캠, 나가기 버튼 -->
      <div class="flex absolute bottom-0 w-full p-4 justify-center gap-4">
        <button
          class="text-white w-12 h-12 flex items-center justify-center rounded-full shadow-md hover:bg-blue-600 transition"
          @click="toggleMicrophone" :class="isMicOn ? 'bg-blue-500 hover:bg-blue-600' : 'bg-red-500 hover:bg-red-600'">
          📢
        </button>
        <button @click="toggleCamera" :class="isCamOn ? 'bg-blue-500 hover:bg-blue-600' : 'bg-red-500 hover:bg-red-600'"
          class="text-white w-12 h-12 flex items-center justify-center rounded-full shadow-md transition">
          🎥
        </button>
        <button @click="openModal"
          class="bg-red-500 hover:bg-red-600 text-white w-12 h-12 flex items-center justify-center rounded-full shadow-md transition">
          🚪
        </button>

        <!-- 퇴장 모달 창 -->
        <div v-if="isModalOpen" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
          <div class="bg-white p-6 rounded-lg shadow-lg w-80 text-center">
            <p class="text-lg font-semibold text-black">정말 나가시겠습니까?</p>
            <div class="mt-4 flex justify-around">
              <button @click="confirmExit" class="bg-red-500 text-white px-4 py-2 rounded-md">나가기</button>
              <button @click="closeModal" class="bg-gray-300 px-4 py-2 rounded-md">취소</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="col-span-1 bg-black text-white relative">
      <div class="memo p-4 rounded-md h-full flex flex-col">
        <p class="text-lg font-semibold mb-2">📝 발언 내용 요약</p>
        <div
          class="w-full h-full bg-gray-900 p-2 rounded-md text-white focus:outline-none focus:ring-2 focus:ring-blue-400 overflow-y-auto custom-scrollbar">
          <div v-for="(summary, index) in debateSummary" :key="index">
            <p>{{ summary.round }}라운드 {{ summary.position }} 측 발언자: {{ summary.nickname }}</p>
            <p>{{ summary.text }}</p>
          </div>
        </div>
        <!-- <textarea
          v-model="memo"
          class="w-full h-full bg-gray-900 p-2 rounded-md text-white focus:outline-none focus:ring-2 focus:ring-blue-400 overflow-y-auto custom-scrollbar"
          placeholder="요약된 발언 내용입니다."
        ></textarea> -->
      </div>
      <div class="flex absolute bottom-0 w-full p-4 justify-center gap-4">
        <button @click="openModal"
          class="bg-red-500 hover:bg-red-600 text-white w-12 h-12 flex items-center justify-center rounded-full shadow-md hover:bg-red-600 transition">🚪</button>
      </div>
      <div v-if="isModalOpen" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
        <div class="bg-white p-6 rounded-lg shadow-lg w-80 text-center">
          <p class="text-lg font-semibold text-black">정말 나가시겠습니까?</p>
          <div class="mt-4 flex justify-around">
            <button @click="confirmExit" class="bg-red-500 text-white px-4 py-2 rounded-md">나가기</button>
            <button @click="closeModal" class="bg-gray-300 px-4 py-2 rounded-md">취소</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import axios from 'axios'
import { Client } from '@stomp/stompjs'
import { OpenVidu, Stream } from "openvidu-browser"
import UserVideo from "@/components/UserVideo.vue"

// Vue Router의 props로 전달된 debateId를 받음
const props = defineProps<{ debateId: string }>()

// 토론 방 정보를 받아오자
const subject = ref<string>("") // 토론 주제
const participant = ref<boolean>(false) // 토론 참여자인 경우 true, 아닌 경우 false
// 토론 참여자 정보: 4명 들어올 예정, nickname=참여자이름 profileImage=프사 position=입장 order=입장별발언순서
const users = ref<{ nickname: string; profileImage: string; position: string; order: number }[]>([])
const fetchDebateRoomInfo = async () => { // API 호출 함수
  try {
    const token = localStorage.getItem("token")
    if (!token) {
      throw new Error("로그인이 필요합니다.")
    }

    const response = await axios.get(`/api/debates/${props.debateId}/roominfo`, {
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

const OV = ref<OpenVidu | null>(null);
const session = ref<any>(null);
const publisher = ref<any>(null);
const subscribers = ref<any[]>([]);
const isMicOn = ref(false); // 마이크 초기 값 OFF
const isCamOn = ref(true); // 카메라 초기 값 ON

// 실시간 STT 적용해보자 fastAPI 위스퍼 사용--------------
const STT_WS_URL = "ws://localhost:8000/audio/ws/stt";
const ws = ref<WebSocket | null>(null); // 웹 소켓에 연결
const audioContext = ref<AudioContext | null>(null);
const mediaStream = ref<MediaStream | null>(null);
const processorNode = ref<AudioWorkletNode | null>(null);
const isStreaming = ref<boolean>(false);
const transcription = ref<string>("");
function connectWebSocket() {  // 승우 형 방식이랑 다르게 webSocket 연결
  // if (ws) ws.close();
  console.log("webSocket 연결 시작")
  ws.value = new WebSocket(STT_WS_URL)

  ws.value.onopen = () => console.log("실시간 STT WebSocket 연결됨")
  ws.value.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      transcription.value = data.transcription || ""
    } catch (error) {
      console.error("JSON 파싱 오류: ", error)
    }
  }
  ws.value.onerror = (error) => console.error("WebSocket 오류: ", error)
  ws.value.onclose = () => {
    console.log("WebSocket 종료됨")
    if (isStreaming.value) setTimeout(connectWebSocket, 3000) // 3초 후 재연결
  }
}
async function startStreaming() {
  if (isStreaming.value) return;
  isStreaming.value = true;

  audioContext.value = new AudioContext({ sampleRate: 16000 })
  mediaStream.value = await navigator.mediaDevices.getUserMedia({ audio: true })
  const source = audioContext.value.createMediaStreamSource(mediaStream.value)

  // AudioWorklet 동적 로드
  const workletCode = `
    class PCMProcessor extends AudioWorkletProcessor {
      process(inputs) {
        if (inputs.length > 0 && inputs[0].length > 0) {
          let input = inputs[0][0];
          let pcmData = new Int16Array(input.length);
          let maxAmplitude = 0;
          
          for (let i = 0; i < input.length; i++) {
            let sample = Math.max(-1, Math.min(1, input[i]));
            if (isNaN(sample) || !isFinite(sample)) sample = 0;
            pcmData[i] = Math.floor(sample * 32768);
            if (Math.abs(pcmData[i]) > maxAmplitude) maxAmplitude = Math.abs(pcmData[i]);
          }

          if (maxAmplitude > 400) {
            this.port.postMessage(pcmData.buffer);
          }
        }
        return true;
      }
    }
    registerProcessor("pcm-processor", PCMProcessor);
  `;
  const blob = new Blob([workletCode], { type: "application/javascript" })
  const url = URL.createObjectURL(blob)
  await audioContext.value.audioWorklet.addModule(url)

  processorNode.value = new AudioWorkletNode(audioContext.value, "pcm-processor")

  connectWebSocket();
  processorNode.value.port.onmessage = (event) => {
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
      ws.value.send(event.data)
    }
  }

  source.connect(processorNode.value)
  console.log("PCM 스트리밍 시작합니다잇")
}
function stopStreaming() {
  // 오디오 스트리밍 중지
  console.log("오디오 스트리밍 중지")
  if (!isStreaming.value) return // 오디오 스트리밍 안하고 있다면 돌아가
  isStreaming.value = false

  if (mediaStream.value) {
    mediaStream.value.getTracks().forEach((track) => track.stop())
  }
  if (audioContext.value) {
    audioContext.value.close();
    audioContext.value = null
  }
  if (processorNode.value) {
    processorNode.value.disconnect();
    processorNode.value = null;
  }
  if (ws.value) {
    ws.value.close();

    console.log("음성 스트리밍 중지됨")
  }
}
// 보광님 코드 적용 끝--------------------------------

// const recorder = ref<MediaRecorder | null>(null); // MediaRecorder 객체를 저장하는 변수, 녹음을 제어함
// const audioChunks = ref<Blob[]>([]); // 녹음된 오디오 데이터를 저장하는 배열
// const audioStream = ref<MediaStream | null>(null); // 오디오 스트림을 저장하는 변수
// const audioURL = ref<string | null>(null); // 녹음된 오디오의 URL을 저장하는 변수
// const audioPlayer = ref<HTMLAudioElement | null>(null); // 오디오를 재생할 HTMLAudioElement를 저장하는 변수
// const startRecording = async () => {
// 실시간 Streaming도 테스트용으로 넣어봤음, 같이 돌려봐유
//   try {
//     startStreaming();
//   } catch (error) {
//     console.error("스트리밍 실패", error)
//   }
//   // 녹음을 시작하는 함수
//   try {
//     const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
//     audioStream.value = stream;
//     const options = { mimeType: 'audio/webm;codecs=opus' };
//     recorder.value = new MediaRecorder(stream, options);

//     recorder.value.ondataavailable = (event) => {
//       audioChunks.value.push(event.data);
//     };

//     recorder.value.start();
//     console.log("🎙 녹음 시작");
//   } catch (error) {
//     console.error("❌ 녹음 시작 실패:", error);
//   }
// };
// const stopRecording = async () => {
//   try {
//     stopStreaming();
//   } catch (error) {
//     console.error("음성 스트리밍 종료 실패:", error)
//   }
//   if (!recorder.value) return;

//   recorder.value.stop();
//   recorder.value.onstop = async () => {
//     console.log("🛑 녹음 종료");
//     if (audioChunks.value.length > 0) {
//       const audioBlob = new Blob(audioChunks.value, { type: 'audio/webm;codecs=opus' });

//       // 브라우저에서 재생할 URL 생성
//       audioURL.value = URL.createObjectURL(audioBlob);

//       // 오디오 플레이어에 URL 적용
//       if (audioPlayer.value) {
//         audioPlayer.value.src = audioURL.value;
//         audioPlayer.value.play();
//       }

//       sendAudioToBackend(audioBlob);
//     }
//     audioChunks.value = [];
//     if (audioStream.value) {
//       audioStream.value.getTracks().forEach(track => track.stop());
//       audioStream.value = null;
//     }
//   };
// };
// const sendAudioToBackend = async (audioBlob: Blob) => {
// 녹음한 음성을 백엔드에 전송하는 로직
//   try {
//     const formData = new FormData();
//     formData.append("audio", audioBlob, "recording.webm");

//     const response = await fetch("http://localhost:8000/audio/stt", {
//       method: "POST",
//       body: formData,
//     });

//     if (!response.ok) {
//       throw new Error("서버 업로드 실패");
//     }
//     console.log("✅ 서버 업로드 성공");
//   } catch (error) {
//     console.error("❌ 서버 전송 오류:", error);
//   }
// };
// const downloadAudio = () => {
//   if (!audioURL.value) return;

//   const a = document.createElement("a");
//   a.href = audioURL.value;
//   a.download = "recording.webm";
//   document.body.appendChild(a);
//   a.click();
//   document.body.removeChild(a);
// };

// 참가자의 비디오 스트림 매칭
const findUserStream = (userNickName: string) => {
  if (publisher.value?.stream?.connection?.data.includes(userNickName)) {
    return publisher.value
  }
  return subscribers.value.find(sub => sub.stream.connection.data.includes(userNickName)) || null
}

// 참가자 정보 & OpenVidu 스트림 매칭
const pro1 = computed(() => users.value.find(user => user.position === "찬성" && user.order === 1));
const pro2 = computed(() => users.value.find(user => user.position === "찬성" && user.order === 2));
const con1 = computed(() => users.value.find(user => user.position === "반대" && user.order === 1));
const con2 = computed(() => users.value.find(user => user.position === "반대" && user.order === 2));

const pro1Stream = computed(() => pro1.value ? findUserStream(pro1.value.nickname) : null);
const pro2Stream = computed(() => pro2.value ? findUserStream(pro2.value.nickname) : null);
const con1Stream = computed(() => con1.value ? findUserStream(con1.value.nickname) : null);
const con2Stream = computed(() => con2.value ? findUserStream(con2.value.nickname) : null);

const toggleMicrophone = () => {
  if (publisher.value) {
    isMicOn.value = !isMicOn.value;
    publisher.value.publishAudio(isMicOn.value)
  }
};
const toggleCamera = () => {
  if (publisher.value) {
    isCamOn.value = !isCamOn.value;
    publisher.value.publishVideo(isCamOn.value);
  }
}

// 내 스트림을 메인으로 표시
// const mainStreamManager = ref<any>(null);

// OpenVidu 세션 참가 함수
const joinSession = async () => {
  console.log("🔗 OpenVidu 세션에 연결 시도");

  OV.value = new OpenVidu();
  session.value = OV.value.initSession();

  session.value.on("streamCreated", (event: any) => {
    const subscriber = session.value.subscribe(event.stream);

    // 중복 참가자 방지
    // if (!subscribers.value.some((sub) => sub.stream.connection.connectionId === subscriber.stream.connection.connectionId)) {
    //   subscribers.value.push(subscriber);
    // }
    subscribers.value.push(subscriber); // 참가자 명단에 추가
  });

  session.value.on("streamDestroyed", (event: any) => {
    subscribers.value = subscribers.value.filter((sub) => sub !== event.stream.streamManager);
  });

  // OpenVidu 토큰 가져오기 (테스트용으로 SessionA 고정이지만, 백엔드 연결하고 나면 props.debateId)
  const token = await getToken("SessionA");
  // const token = await getToken(props.debateId);

  await session.value.connect(token, { clientData: "사용자" });

  publisher.value = OV.value.initPublisher(undefined, {
    // audioSource: isMicOn.value,
    // videoSource: isCamOn.value,
    // publishAudio: isMicOn.value,
    // publishVideo: isCamOn.value,
    audioSource: true,
    videoSource: true,
    publishAudio: true,
    publishVideo: true,
    resolution: "640x480",
    frameRate: 30,
    insertMode: "APPEND",
    mirror: true,
  });

  session.value.publish(publisher.value);
  // mainStreamManager.value = publisher.value;
};

// OpenVidu 서버에서 토큰 가져오기
const getToken = async (sessionId: string) => {
  const sessionResponse = await createSession(sessionId);
  return await createToken(sessionResponse);
};
// 백엔드 준비되면 이걸로, OpenVidu 토큰값쓰나?
// const getToken = async () => {
//   const response = await axios.post(`/api/debates/${debateId}/get-token`);
//   return response.data.token;
// };

// 내 캠도 subscribers 리스트에 포함하도록 allStreams 배열 생성
const allStreams = computed(() => { // 지금은 쓸모 없음음
  // subscribers에서 중복된 스트림 제거
  const uniqueSubscribers = subscribers.value.filter(
    (sub, index, self) =>
      index === self.findIndex((s) => s.stream.connection.connectionId === sub.stream.connection.connectionId)
  );

  return publisher.value ? [publisher.value, ...uniqueSubscribers] : uniqueSubscribers;
});

const createSession = async (sessionId: string) => {
  try {
    const response = await axios.post("http://localhost:5000/api/sessions", {
      customSessionId: sessionId,
    });
    console.log("생성된 세션 ID:", response.data);
    return response.data;
  } catch (error: any) {
    if (error.response?.status === 409) {
      console.log("기존 세션에 참여:", sessionId);
      return sessionId;  // 세션이 이미 존재하면 그대로 사용
    } else {
      console.error("세션 생성 오류", error);
      return null;
    }
  }
};

const createToken = async (sessionId: string) => {
  try {
    const response = await axios.post(
      `http://localhost:5000/api/sessions/${sessionId}/connections`, {});
    console.log("📌 생성된 토큰:", response.data);
    return response.data;
  } catch (error) {
    console.error("🚨 토큰 생성 오류", error);
  }
};

const leaveSession = () => {
  if (session.value) {
    session.value.disconnect();
  }

  OV.value = null;
  session.value = null;
  publisher.value = null;
  subscribers.value.length = 0;
  // mainStreamManager.value = null;
};

interface GameState {
  moderator: string;
  gameTopic: string;
  gameTopicDetail: string;
  currentSpeaker: string; // 현재 발언자
  nextSpeaker: string; // 다음 발언자
  currentRound: number;
  remainingTime: number;
  totalTime: number;
  playerStances: Record<string, any>;
  status: string;
  player1: string;
  stance1: string;
  player2: string;
  stance2: string;
  showModal: boolean;
  isDone: boolean;
}

const gameState = ref<GameState>({
  moderator: "",
  gameTopic: "",
  gameTopicDetail: "",
  currentSpeaker: "",
  nextSpeaker: "",
  currentRound: 0,
  remainingTime: 0,
  totalTime: 0,
  playerStances: {},
  status: "",
  player1: "Player 1",
  stance1: "Stance 1",
  player2: "Player 2",
  stance2: "Stance 2",
  showModal: false,
  isDone: false,
})

const serverURL = ref<string>("ws://localhost:8080/ws")
// WebSocket 클라이언트 생성
const stompClient = new Client({
  brokerURL: serverURL.value, // WebSocket 서버 URL
  reconnectDelay: 5000, // 5초 후 재연결
  onConnect: () => {
    console.log("WebSocket Connected!")
    // 게임 ID를 'SessionA'로 가정하고 해당 채널 구독
    stompClient.subscribe(`/sub/debate/${props.debateId}`, (message) => {
      const data = JSON.parse(message.body)
      handleMessage(data)
      console.log("받은 데이터:", data.type, data.payload)
    })
  },
  onDisconnect: () => {
    console.log("WebSocket Disconnected!")
  },
})

// 메시지 핸들러
const handleMessage = (data: any) => {
  switch (data.type) {
    case "TOPIC_ANNOUNCE":
      console.log("📝 게임 주제 발표:", data.payload);
      gameState.value.status = data.type;
      gameState.value.gameTopic = data.payload.topic;
      gameState.value.gameTopicDetail = data.payload.topicDetail;
      gameState.value.currentSpeaker = data.payload.currentSpeaker;
      gameState.value.currentRound = data.payload.currentRound;
      gameState.value.nextSpeaker = data.payload.currentSpeaker; // 다음 발언자는 현재 발언자로 설정
      gameState.value.remainingTime = data.payload.remainingTime;
      gameState.value.playerStances = data.payload.playerStances;
      gameState.value.player1 = data.payload.currentSpeaker;
      gameState.value.player2 = Object.keys(data.payload.playerStances).find(player => player !== data.payload.currentSpeaker) || "";
      gameState.value.stance1 = data.payload.playerStances[data.payload.currentSpeaker]?.stance || "";
      gameState.value.stance2 = data.payload.playerStances[gameState.value.player2]?.stance || "";
      showMessageSequentially(); // 사회자 안내문구 출력 시작
      break;

    case "TURN_UPDATE":
      console.log("🔄 턴 업데이트:", data.payload);
      gameState.value.status = data.type;
      gameState.value.gameTopic = data.payload.topic;
      gameState.value.gameTopicDetail = data.payload.topicDetail;
      gameState.value.currentRound = data.payload.currentRound;
      gameState.value.currentSpeaker = data.payload.currentSpeaker;
      gameState.value.nextSpeaker = Object.keys(data.payload.playerStances).find(player => player !== data.payload.currentSpeaker) || "";
      gameState.value.remainingTime = data.payload.remainingTime;
      gameState.value.playerStances = data.payload.playerStances;
      gameState.value.totalTime = data.payload.totalTime;
      progress.value = (gameState.value.remainingTime / gameState.value.totalTime) * 100
      poiRequested.value = false; // 턴이 변경되면 POI 신청 초기화
      break;

    case "COUNT_DOWN":
      console.log("⏳ 카운트다운:", data.payload);
      gameState.value.status = data.type;
      gameState.value.remainingTime = data.payload.remainingTime;
      gameState.value.totalTime = data.payload.totalTime;
      progress.value = (gameState.value.remainingTime / gameState.value.totalTime) * 100

      // 요약 관련
      summaryUserNickname.value = data.payload.currentSpeaker
      summaryPosition.value = data.payload.playerStances[data.payload.currentSpeaker]["stance"]
      summaryRound.value = data.payload.currentRound
      break;

    case "GAME_END":
      console.log("🏁 게임 종료:", data.payload);
      gameState.value.status = data.type;
      // gameState.value.status = data.payload.status;
      gameState.value.isDone = true;
      break;

    case "POI_REQUEST":
      console.log("POI 신청 감지");
      poiRequested.value = true; // POI 신청 감지
      break;

    default:
      console.warn("⚠️ 알 수 없는 메시지:", data);
  }
};

// 단계에 맞게 사회자 안내 문구를 꾸며보자 ----------------------------------
const currentModeratorMessage = ref<string>(""); // 현재 안내 문구
const messageStep = ref<number>(0); // 초기 안내 문구 단계 관리
const poiRequested = ref<boolean>(false); // POI 요청 여부
const poiEligible = computed(() => gameState.value.remainingTime > 59 || gameState.value.remainingTime < 1); // POI 가능 여부

// 초기 안내 문구 리스트
const announceMessages = ref([
  "참가자 여러분, 환영합니다!",
  "", // 주제는 동적으로 설정됨
  "참가자 여러분께 5분간 준비 시간을 드립니다. 자료 검색 기능을 활용해 토론을 준비하세요.",
  "화면 오른쪽의 메모장을 활용하여 정리할 수 있습니다."
]);

// 안내 문구를 순차적으로 표시하는 함수
const showMessageSequentially = () => {
  messageStep.value = 0;

  const interval = setInterval(() => {
    if (messageStep.value < announceMessages.value.length) {
      currentModeratorMessage.value = announceMessages.value[messageStep.value]; // ✅ ref 배열로 접근
      messageStep.value++;
    } else {
      clearInterval(interval);
    }
  }, 2000); // 2초마다 변경
};

// WebSocket 메시지 감지해서 `TOPIC_ANNOUNCE` 시 안내 시작
watch(
  () => gameState.value.status,
  (newVal) => {
    if (newVal === "TOPIC_ANNOUNCE") {
      announceMessages.value[1] = `오늘의 토론 주제는 다음과 같습니다: ${gameState.value.gameTopic}`; // 🎯 게임 주제 삽입
      showMessageSequentially(); // 문구 안내 시작
    }
  }
);

// 발언 안내 (TURN_UPDATE 시 표시)
const updateModeratorMessage = () => {
  currentModeratorMessage.value = `${gameState.value.playerStances[gameState.value.currentSpeaker]?.stance || "알 수 없음"}측 ${gameState.value.currentRound
    }번째 연사, ${gameState.value.currentSpeaker} 발언을 시작해 주세요.`;
};

// 카운트 다운 안내 (COUNT_DOWN 시 표시)
const countdownModeratorMessage = () => {
  currentModeratorMessage.value = `${gameState.value.nextSpeaker} 발언을 준비해 주세요.`;
}

// 토론 종료 안내 (GAME_END 시 표시)
const endModeratorMessage = () => {
  currentModeratorMessage.value = "이제 토론이 마무리되었습니다. 모두 수고하셨습니다!"
}

// POI 안내
const updatePOIMessage = () => {
  currentModeratorMessage.value = poiEligible.value
    ? "발언 시작 후 1분과 종료 1분 전에는 질문을 받을 수 없습니다."
    : "POI 신청이 들어왔습니다. 수락하시겠습니까?";
};

// POI 요청 감지하여 안내 문구 변경
watch(poiRequested, (newVal) => {
  if (newVal) {
    updatePOIMessage(); // POI 안내로 변경
  } else {
    updateModeratorMessage(); // 다시 발언 안내로 복구
  }
});

// 발언 요약 관련 로직
interface DebateSummary {
  nickname: string;
  position: string;
  round: number;
  text: string;
}
const debateSummary = ref<DebateSummary[]>([
  {
    nickname: "방성준",
    position: "중립",
    round: 1,
    text: "다들 화이팅 합시다!"
  },
]) // 각 발언 요약을 모아놓을 배열
const summaryUserNickname = ref<string>("") // 발언 요약 유저 이름, 발언 시작할 때 값 변경
const summaryPosition = ref<string>("") // 발언 요약 유저 입장, 발언 시작할 때 값 변경
const summaryRound = ref<number>(0) // 발언 요약 라운드, 발언 시작할 때 값 변경
const summaryText = ref<string>("") // 발언 요약 text
const requestSummary = async () => { // 요약을 요청하는 함수
  try {
    const response = await axios.post('마법같은 요약 요청할 주소', {
      // 토론 진행 웹소켓 연결해보고 바뀔 수도 있음
      // 토론 방 Id props.debateId
      // 발언자 ID userId
      // 진행 라운드 summaryRound.value
      "debateId": props.debateId,
      // "user_id": 발언자 ID,
      "currentRound": summaryUserNickname.value
    })
    summaryText.value = response.data.summary
    debateSummary.value.push({
      nickname: summaryUserNickname.value,
      position: summaryPosition.value,
      round: summaryRound.value,
      text: summaryText.value
    })
  } catch {
    console.log('요약 중 에러 발생')
  }
}

// WebSocket 이벤트 감지하여 안내 문구 변경
watch(
  () => gameState.value.status,
  (newVal, oldVal) => {
    console.log(`🔄 상태 변경: ${oldVal} → ${newVal}`);

    switch (newVal) {
      case "TOPIC_ANNOUNCE":
        console.log("📢 토론 주제 발표 시작");
        showMessageSequentially(); // 초기 안내 시작
        break;
      case "TURN_UPDATE":
        console.log("🎙️ 발언 순서 변경");
        updateModeratorMessage(); // 발언 안내로 변경

        // 카운트다운이 끝나고 새로운 TURN_UPDATE가 시작될 때 실행할 함수
        if (oldVal === "COUNT_DOWN") {
          console.log("✅ 카운트다운 종료 후 새로운 턴이 시작됨!");
          requestSummary(); // 지난 발언 요약 요청
        }
        break;
      case "COUNT_DOWN":
        console.log("⏳ 카운트다운 진행 중");
        countdownModeratorMessage(); // 카운트다운 시작하면 다음 발언자 안내로 변경
        break;
      case "GAME_END":
        console.log("🏁 토론 종료");
        endModeratorMessage(); // 토론 종료 안내
        break;
      default:
        console.warn(`⚠️ 예상치 못한 상태: ${newVal}`);
    }
  }
);

// ------------------------------------------------------------------

const speakerType = ref('다음'); // 타이머 슬라이더에 들어갈 발언자 안내 문구
const Speaker = computed(() => {
  // gameState가 로드되기 전이면 안전하게 빈 문자열 반환
  if (!gameState.value || !gameState.value.currentSpeaker) {
    return '';
  }
  // 발언자 정보에 맞게 발언자 표시 변경
  if (speakerType.value === '현재') {
    return gameState.value.currentSpeaker;
  } else {
    return gameState.value.nextSpeaker;
  }
});
// 게임 상태가 TURN_UPDATE일 때, 발언자 안내 문구 '현재' 아니라면 '다음음'
watch(
  () => gameState.value.status,
  (newVal, oldVal) => {
    if (newVal !== oldVal) {
      console.log("게임 상태 변경:", newVal);
      if (newVal === "TURN_UPDATE") {
        speakerType.value = '현재';
      } else {
        speakerType.value = '다음';
      }
    }
  },
  { immediate: true } // 처음 로딩될 때 한 번 실행
);

// `remainTime` 변경 감지
watch(
  () => gameState.value.remainingTime,
  (newVal, oldVal) => {
    if (newVal !== oldVal) {
      console.log("남은 시간:", newVal);
      console.log(gameState.value);
    }
  }
)

// const microphoneAccess = ref(<boolean | null>null);
// 컴포넌트가 마운트될 때 WebSocket 연결
onMounted(() => {
  fetchDebateRoomInfo();
  stompClient.activate();
});

onMounted(async () => {
  // console.log("🔄 OpenVidu 세션 자동 연결 시작...");

  // if (session.value) {
  //   console.log("🚪 기존 세션 해제 후 재연결");
  //   leaveSession(); // ✅ 기존 세션 정리
  //   await new Promise(resolve => setTimeout(resolve, 500)); // 기존 세션 정리 후 딜레이 추가
  // }

  // await joinSession();  // 새로운 세션 연결
  await fetchDebateRoomInfo()
  if (participant.value) {
    await joinSession()
  } else {
    console.log("방청객은 OpenVidu 세션에 연결하지 않음")
  }
});

// 컴포넌트가 언마운트될 때 WebSocket 연결 해제
onUnmounted(() => {
  stompClient.deactivate();
  leaveSession();
});

// 토론 퇴장 버튼 관련
const isModalOpen = ref(false);
const openModal = () => {
  isModalOpen.value = true;
  console.log(isModalOpen.value)
}
const closeModal = () => {
  isModalOpen.value = false;
}
const confirmExit = () => {
  console.log("사용자가 방을 나갔습니다!");
  closeModal();
  leaveSession();
}

// 개인 메모 저장
const memo = ref('')

// 남은 시간 (초 단위, 소수점 포함)
const progress = ref<number>(100);

// "분:초" 포맷 변환 (computed)
const formattedTime = computed(() => {
  const minutes = Math.floor(gameState.value.remainingTime / 60)
  const seconds = Math.floor(gameState.value.remainingTime % 60)
  return `${minutes}:${seconds.toString().padStart(2, '0')}` // 1자리 초일 경우 '0' 추가 (예: 1:05)
})

// 남은 시간을 퍼센트로 변환하여 progress bar에 반영
const progressWidth = computed(() => (gameState.value.remainingTime / gameState.value.totalTime) * 100)
</script>

<style scoped>
/* 📝 메모장 높이 */
.memo {
  height: 92%;
}

/* textarea 크기 고정 */
textarea {
  resize: none;
}

/* 🖥️ 스크롤바 스타일 */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: #2d2d2d;
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #4a90e2;
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #357ac9;
}

.video {
  /* width: 100%; */
  height: 100%;
  /* 부모 요소의 높이에 맞추기 */
  object-fit: cover;
}
</style>