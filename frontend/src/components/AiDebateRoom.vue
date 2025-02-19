<template>
  <div class="debate-container">
    <header class="header">
      <div class="content-wrapper">
        <div class="header-content">
          <div class="topic-info">
            <div class="topic-title-container">
              <h1 class="topic-title">{{ topic.title }}</h1>
              <div class="stance-badge" :class="{
                'stance-agree': stance === 'agree',
                'stance-disagree': stance === 'disagree',
              }">
                {{ stance === "agree" ? "찬성" : "반대" }}
              </div>
            </div>
          </div>

          <div class="stage-progress">
            <div class="progress-bar">
              <div class="progress-background"></div>
              <div class="progress-fill" :style="{ width: `${(currentStageIndex + 1) * 25}%` }"></div>
              <div v-for="(stage, index) in stages" :key="index" class="stage-marker" :class="{
                completed: currentStageIndex >= index,
                active: currentStageIndex === index,
              }" :style="{ left: `${(index / (stages.length - 1)) * 100}%` }">
                <div class="marker-circle">{{ index + 1 }}</div>
                <div class="marker-label">{{ stage.title.split(": ")[1] }}</div>
              </div>
            </div>
          </div>

          <div class="header-timer-container">
            <div class="timer-circle" :class="{ 'preparation-time': isPreparationTime }">
              <svg viewBox="0 0 36 36" class="circular-timer">
                <path d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke="#eee" stroke-width="3" />
                <path d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke="#ff6b6b" stroke-width="3"
                  stroke-dasharray="100, 100" :stroke-dashoffset="timerCircleOffset" />
              </svg>
              <div class="timer-content">
                <div class="timer-icon">⏱️</div>
                <div class="timer-text">
                  {{ isPreparationTime ? "준비 시간" : "발표 시간" }}
                </div>
                <div class="timer-count">
                  {{
                    formatTime(
                      isPreparationTime ? preparationTimeLeft : remainingTime
                    )
                  }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <div class="content-wrapper">
      <div class="main-content">
        <section class="tori-section">
          <div class="tori-avatar">
            <img :src="getImageUrl('Tori.svg')" alt="토리" class="tori-image" @error="handleImageError" />
          </div>
          <div class="question-box">
            <h2>{{ currentStage.question }}</h2>
            <p>{{ currentStage.hint }}</p>
          </div>
        </section>
      </div>
    </div>

    <footer class="controls">
      <div class="content-wrapper">
        <div class="controls-content">
          <button class="mic-button" :class="{ active: isMicActive }" @click="toggleMic" :disabled="isPreparationTime">
            <div class="mic-icon">
              <div v-if="isMicActive" class="mic-waves">
                <span></span><span></span><span></span>
              </div>
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path
                  d="M12 2C11.175 2 10.5 2.675 10.5 3.5V11.5C10.5 12.325 11.175 13 12 13C12.825 13 13.5 12.325 13.5 11.5V3.5C13.5 2.675 12.825 2 12 2Z"
                  fill="currentColor" />
                <path
                  d="M17 8V11.5C17 14.535 14.535 17 11.5 17C8.465 17 6 14.535 6 11.5V8H4V11.5C4 15.35 6.94 18.505 10.5 19.07V22H13.5V19.07C17.06 18.505 20 15.35 20 11.5V8H17Z"
                  fill="currentColor" />
              </svg>
            </div>
            <span class="mic-status">{{
              isMicActive ? "마이크 켜짐" : "마이크 꺼짐"
            }}</span>
          </button>
          <button class="skip-button" @click="handleSkip">
            <span class="skip-text">다음 단계</span>
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="skip-icon">
              <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </button>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";

interface Topic {
  id: number;
  level: number;
  title: string;
  participants: number;
  mainPoints: string[];
  references: string[];
}

interface Stage {
  step: number
  title: string;
  description: string;
  question: string;
  hint: string;
}

let stream: MediaStream | null = null;              // getUserMedia로 얻은 MediaStream
let audioContext: AudioContext | null = null;        // AudioContext 인스턴스
let pcmNode: AudioWorkletNode | null = null;         // AudioWorkletNode
let socket: WebSocket | null = null;                 // WebSocket 연결


async function startAudioProcessing() {
  try {
    // 1. 마이크 스트림 얻기
    stream = await navigator.mediaDevices.getUserMedia({ audio: true });

    // 2. AudioContext 생성
    audioContext = new AudioContext({ sampleRate: 16000 });

    // 3. AudioWorklet 모듈 등록 (파일 경로는 실제 파일 위치에 맞게 조정)
    await audioContext.audioWorklet.addModule('/pcm-processor.js');

    // 4. AudioWorkletNode 생성
    pcmNode = new AudioWorkletNode(audioContext, 'pcm-processor');

    // 5. WebSocket 연결 생성
    socket = new WebSocket(
    `ws://70.12.247.158:8000/api/audio/basic?topic_id=${props.topic.id}&stance=${props.stance}&step=${stages.value[currentStageIndex.value].step}&user_id=2`);

    socket.binaryType = 'arraybuffer';  // 이진 데이터 전송을 위해 설정

    // 6. AudioWorkletNode의 메시지를 WebSocket으로 전송
    pcmNode.port.onmessage = (event) => {

      const pcmArray = new Int16Array(event.data);
      console.log('메인 스레드: PCM 데이터 (Int16Array):', pcmArray);
      if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(event.data);
        console.log('메인 스레드: WebSocket으로 데이터 전송');
      } else {
        console.warn('메인 스레드: WebSocket 연결 상태가 OPEN이 아님:', socket?.readyState);
      }
    };

    // 7. 마이크 스트림을 AudioContext에 연결
    const source = audioContext.createMediaStreamSource(stream);
    source.connect(pcmNode).connect(audioContext.destination);

    console.log('오디오 캡처 및 전송 시작');
  } catch (error) {
    console.error('오디오 처리 시작 중 오류 발생:', error);
  }
}

async function fetchPracticeResult() {
  try {
    const response = await fetch("http://70.12.247.158:8000/api/practice/basic", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        topic: props.topic.title, // topic.title 전송
        user_id: 2,               // user_id 전송 (고정값 2)
      }),
    });

    if (!response.ok) {
      throw new Error("서버 응답 오류");
    }

    // BasicPracticeResponse 구조에 맞게 데이터 파싱
    const data = await response.json();
    console.log("채점 결과:", data);

    const result = {
      strengths: data.strength,
      improvements: data.improvement,
      strategy: data.strategy,
      recordedAnswers: recordedAnswers.value,
    };

    return result;
  } catch (error) {
    console.error("채점 결과 조회 실패:", error);
    const result = {
      strengths: "채점 결과를 가져오는 데 실패했습니다.",
      improvements: "",
      strategy: "",
      recordedAnswers: [],
    };

    return result;
  }
}

function stopAudioProcessing() {
  // 1. WebSocket 연결 종료
  if (socket) {
    if (socket.readyState === WebSocket.OPEN) {
      socket.close();
      console.log('WebSocket 연결 종료');
    }
    socket = null;
  }

  // 2. AudioWorkletNode 연결 해제
  if (pcmNode) {
    pcmNode.disconnect();
    pcmNode = null;
    console.log('AudioWorkletNode 연결 해제');
  }


  // 3. AudioContext 종료
  if (audioContext) {
    audioContext.close().then(() => {
      console.log('AudioContext 종료');
    }).catch((error) => {
      console.error('AudioContext 종료 중 오류 발생:', error);
    });
    audioContext = null;
  }

  // 4. MediaStream의 모든 트랙 중지 (마이크 끄기)
  if (stream) {
    stream.getTracks().forEach((track) => track.stop());
    stream = null;
    console.log('MediaStream 트랙 중지(마이크 끄기)');
  }
}


const props = defineProps({
  topic: {
    type: Object as () => Topic,
    required: true,
    default: () => ({
      title: "",
      mainPoints: [],
    }),
  },
  stance: {
    type: String,
    required: true,
    default: "agree",
  },
});

const emit = defineEmits(["debate-complete"]);

const currentStageIndex = ref<number>(0);
const preparationTime = 15;
const preparationTimeLeft = ref<number>(preparationTime);
const isPreparationTime = ref<boolean>(true);
const isMicActive = ref<boolean>(false);
const remainingTime = ref<number>(45);
const recordedAnswers = ref<string[]>([]);

const currentStage = computed(() => stages.value[currentStageIndex.value]);

const timerCircleOffset = computed(() => {
  const totalTime = isPreparationTime.value ? preparationTime : 45;
  const currentTime = isPreparationTime.value
    ? preparationTimeLeft.value
    : remainingTime.value;
  return 100 - (currentTime / totalTime) * 100;
});

const stages = computed<Stage[]>(() => [
  {
    step: 1, 
    title: "1단계: 주장 세우기",
    description: "자신의 입장을 명확하게 밝히고 핵심 주장을 제시하세요",
    question: props.topic?.title ?? "주제에 대한 당신의 입장은 무엇인가요?",
    hint: `${props.stance === "agree" ? "찬성" : "반대"
      } 입장에서 주장을 명확히 설명해주세요.`,
  },
  {
    step: 2,
    title: "2단계: 근거 찾기",
    description: "주장을 뒷받침하는 구체적인 근거를 제시하세요",
    question: "당신의 주장을 뒷받침하는 구체적인 사례나 근거는 무엇인가요?",
    hint: `${props.topic?.mainPoints?.[0] ?? "주요 논점"
      }와 관련된 구체적인 사례나 근거를 들어 설명해주세요.`,
  },
  {
    step: 3, 
    title: "3단계: 다른 관점 고려하기",
    description: "반대 입장의 주장과 근거를 고려하고 대응하세요",
    question:
      "반대 입장에서 제기할 수 있는 주장은 무엇이며, 이에 대해 어떻게 생각하시나요?",
    hint: `${props.stance === "agree" ? "반대" : "찬성"
      } 입장에서 제기할 수 있는 의견을 고려하고 답변해주세요.`,
  },
  {
    step: 4, 
    title: "4단계: 주장 강화하기",
    description: "앞선 논의를 종합하여 최종 주장을 강화하세요",
    question: "지금까지의 논의를 종합하여, 최종 입장을 말씀해주세요.",
    hint: "근거와 반론을 고려한 균형 잡힌 시각으로 최종 주장을 정리해주세요.",
  },
]);

let preparationTimer: ReturnType<typeof setInterval>;
let speakTimer: ReturnType<typeof setInterval>;

const startPreparationTimer = (): void => {
  preparationTimer = setInterval(() => {
    if (preparationTimeLeft.value > 0) {
      preparationTimeLeft.value--;
    } else {
      clearInterval(preparationTimer);
      isPreparationTime.value = false;
      startSpeakTimer();
    }
  }, 1000);
};

const startSpeakTimer = (): void => {
  isMicActive.value = true;
  startAudioProcessing();
  speakTimer = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--;
    } else {
      handleTimerComplete();
    }
  }, 1000);
};

const handleTimerComplete = async (): Promise<void> => {
  clearInterval(speakTimer);
  isMicActive.value = false;

  if (currentStageIndex.value < stages.value.length - 1) {
    goToNextStage();
  } else {
    stopAudioProcessing();

    // 5초 대기
    await new Promise((resolve) => setTimeout(resolve, 5000));

    // API 요청 결과를 기다린 후 emit 실행
    const result = await fetchPracticeResult();
    emit("debate-complete", result);
  }
};

const handleSkip = async (): Promise<void> => {
  if (!recordedAnswers.value[currentStageIndex.value]) {
    recordedAnswers.value[currentStageIndex.value] = "사용자가 스킵함";
  }

  clearInterval(preparationTimer);
  clearInterval(speakTimer);

  if (currentStageIndex.value === stages.value.length - 1) {
    if (!isPreparationTime.value) {
      stopAudioProcessing();

      // 5초 대기
      await new Promise((resolve) => setTimeout(resolve, 5000));

      // API 요청 결과를 기다린 후 emit 실행
      const result = await fetchPracticeResult();
      emit("debate-complete", result);
    } else {
      isPreparationTime.value = false;
      startSpeakTimer();
    }
  } else {
    if (isPreparationTime.value) {
      isPreparationTime.value = false;
      startSpeakTimer();
    } else {
      goToNextStage();
    }
  }
};

const resetTimers = (): void => {
  clearInterval(preparationTimer);
  clearInterval(speakTimer);
  preparationTimeLeft.value = preparationTime;
  remainingTime.value = 45;
  isPreparationTime.value = true;
  isMicActive.value = false;
  stopAudioProcessing();
  startPreparationTimer();
};

const goToNextStage = (): void => {
  stopAudioProcessing(); // 자동으로 WebSocket 종료
  if (currentStageIndex.value < stages.value.length - 1) {
    currentStageIndex.value++;
    resetTimers();
  }
};

const formatTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins.toString().padStart(2, "0")}:${secs
    .toString()
    .padStart(2, "0")}`;
};
const toggleMic = (): void => {
  if (!isPreparationTime.value) {
    isMicActive.value = !isMicActive.value;
  }
};

const getImageUrl = (filename: string): string => {
  try {
    return new URL(`../assets/${filename}`, import.meta.url).href;
  } catch (error) {
    console.error("Error loading image:", error);
    return "";
  }
};

const handleImageError = (e: Event): void => {
  const target = e.target as HTMLImageElement;
  target.style.display = "none";
  if (target.parentElement) {
    target.parentElement.innerHTML = "<span>토리</span>";
  }
};

const handleSpeechResult = (text: string): void => {
  recordedAnswers.value[currentStageIndex.value] = text;
};

onMounted(() => {
  startPreparationTimer();
});

onUnmounted(() => {
  clearInterval(preparationTimer);
  clearInterval(speakTimer);
  stopAudioProcessing();
});
</script>

<style scoped>
/* 전체적인 테마 색상 변수 정의 */
:root {
  --primary-color: #ff6b6b; /* 서비스 주요 색상 */
  --primary-dark: #ff4f4f;  /* 활성화 및 호버 시 사용할 어두운 색상 */
  --bg-light: #f1f5f9;      /* 버튼 기본 배경색 */
  --text-default: #1f2937;  /* 기본 텍스트 색상 */
  --secondary-color: #4b5563;
  --accent-color: #60a5fa;
  --agree-color: #10b981;
  --disagree-color: #ef4444;
  --light-bg: #f9fafb;
  --card-bg: #ffffff;
  --text-primary: #1f2937;
  --text-secondary: #4b5563;
  --border-color: #e5e7eb;
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 헤더 스타일 개선 */
.header {
  /* ✅ 토마토 계열 컬러로 그라디언트 적용 */
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4f4f 100%);
  padding: 2rem 0 1.6rem;
  color: white;
  box-shadow: var(--shadow-md);
  width: 100%;
}

.header-content {
  text-align: center;
}

.topic-title-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  margin-bottom: 1.2rem;
}

.topic-title {
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.02em;
}


/* 찬성/반대 뱃지 개선 */
.stance-badge {
  padding: 0.4rem 0.8rem;
  border-radius: 9999px;
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  box-shadow: var(--shadow-sm);
}

.stance-agree {
  background-color: var(--agree-color);
  color: white;
}

.stance-disagree {
  background-color: var(--disagree-color);
  color: white;
}

/* 진행 상태바 개선 */
.stage-progress {
  /* 바를 중앙 정렬하고, 마커를 위해 아래쪽 여백 확보 */
  margin: 1.5rem auto;
  width: 80%;        /* 원하는 비율 or 고정 px 값 사용 */
  max-width: 800px;  /* 최대 너비 */
  position: relative;
  padding-bottom: 3rem; /* 마커 아래 라벨 공간 */
  overflow: visible;     /* 마커가 밖으로 나올 수 있으므로 visible */
}

.progress-bar {
  /* 진행 바 자체는 100%로 늘려서 stage-progress 안을 꽉 채움 */
  position: relative;
  width: 100%;
  height: 12px;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  overflow: visible;
}

.progress-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 12px;
  background-color: #ffffff;
  border-radius: 6px;
  transition: width 0.3s ease;
}

/* 스테이지 마커 */
.stage-marker {
  /* 마커는 진행 바 안에 절대 배치되며,
     JavaScript에서 left: (index / (stages.length - 1)) * 100% 로 계산 */
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  top: 50%; /* 바의 수직 중앙 */
  transform: translate(-50%, -50%); /* 가로 중앙 -50%, 세로 중앙 -50% */
  gap: 0.4rem;
}

/* 마커 원(단계 번호가 들어감) */
.marker-circle {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
  transition: all 0.3s ease;
}

/* 완료 상태 */
.stage-marker.completed .marker-circle {
  background-color: #ffffff;
  color: #000000;
}

/* 활성 상태 */
.stage-marker.active .marker-circle {
  background-color: #ffffff;
  color: #000000;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.3);
}

/* 마커 라벨(단계명) */
.marker-label {
  font-size: 0.7rem;
  font-weight: 500;
  color: white;
  white-space: nowrap; /* 글자가 줄바꿈 없이 표시되도록 */
}


/* 타이머 디자인 개선 */
.header-timer-container {
  display: flex;
  justify-content: center;
  margin-top: 1.2rem;
}

.timer-circle {
  position: relative;
  width: 120px;
  height: 120px;
  filter: drop-shadow(0 4px 3px rgba(0, 0, 0, 0.07));
}

.preparation-time .circular-timer path:nth-child(2) {
  stroke: #fcd34d !important;
}

.circular-timer {
  position: absolute;
  top: 0;
  left: 0;
  transform: rotate(-90deg);
  width: 100%;
  height: 100%;
}

.circular-timer path:nth-child(1) {
  stroke-width: 3px;
  stroke: rgba(255, 255, 255, 0.2);
}

.circular-timer path:nth-child(2) {
  stroke-width: 4px;
  stroke: white;
  transition: stroke-dashoffset 1s linear;
}

.timer-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
  font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, sans-serif;
}

.timer-icon {
  font-size: 1.4rem;
}

.timer-text {
  font-size: 0.8rem;
  color: white;
  opacity: 0.95;
  font-weight: 500;
}

.timer-count {
  font-size: 1.3rem;
  font-weight: 700;
  color: white;
  letter-spacing: 0.02em;
}

/* 메인 콘텐츠 영역 개선 */
.debate-container {
  background-color: var(--light-bg);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  max-width: 1024px;
  width: 100%;
  margin: 0 auto;
  padding: 0 1.5rem;
}

.main-content {
  padding: 1.5rem 0;
  flex-grow: 1;
}

/* 토리 질문 섹션 개선 */
.tori-section {
  display: flex;
  gap: 1.2rem;
  margin-bottom: 1.5rem;
  align-items: flex-start;
}

.tori-avatar {
  width: 48px;
  height: 48px;
  background-color: #e0e7ff; /* 기존 파란 배경 유지 */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
  border: 2px solid #c7d2fe;
}

.tori-image {
  max-width: 75%;
  max-height: 75%;
}

/* 말 풍선 스타일의 질문 박스 */
.question-box {
  flex-grow: 1;
  background-color: #ffe9e9; /* ✅ 옅은 핑크 배경 */
  padding: 1.3rem 1.5rem;
  border-radius: 0.8rem;
  box-shadow: var(--shadow-md);
  border: 1px solid #ffc5c5; /* ✅ 핑크색 테두리 */
  position: relative;
}

/* 말 풍선 화살표 부분 */
.question-box::before {
  content: '';
  position: absolute;
  left: -10px;
  top: 16px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 10px solid #ffe9e9; /* ✅ 박스 배경과 같은 핑크 */
  filter: drop-shadow(-3px 1px 1px rgba(0, 0, 0, 0.05));
}

.question-box h2 {
  font-size: 1.1rem;
  margin-bottom: 0.7rem;
  color: var(--text-primary, #1f2937); /* 기본 텍스트 컬러 */
  font-weight: 600;
}

.question-box p {
  font-size: 0.95rem;
  color: var(--text-secondary, #4b5563); /* 서브 텍스트 컬러 */
  line-height: 1.5;
}


/* 컨트롤 영역 개선 */
.controls {
  background-color: var(--card-bg);
  padding: 1.2rem 0;
  border-top: 1px solid var(--border-color);
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 10;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.controls-content {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}

/* 마이크 버튼 개선 */
.mic-button {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  padding: 0.8rem 1.6rem;
  border: none;
  border-radius: 9999px;
  background-color: #f1f5f9;  /* 기본 배경 */
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.mic-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 활성 상태: 서비스 컬러 #ff6b6b 사용 */
.mic-button.active {
  background-color: #ff6b6b;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.35);
}

/* 호버 시 기본 상태 */
.mic-button:hover:not(:disabled) {
  background-color: #e2e8f0;
  transform: translateY(-2px);
}

/* 활성 상태에서 호버 시: 어두운 서비스 컬러 #ff4f4f 사용 */
.mic-button.active:hover:not(:disabled) {
  background-color: #ff4f4f;
}

.mic-button:active:not(:disabled) {
  transform: translateY(0);
}

/* 마이크 아이콘 */
.mic-icon {
  position: relative;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mic-icon svg {
  width: 20px;
  height: 20px;
  color: #64748b;
  transition: color 0.3s ease;
}

/* 활성 상태에서 아이콘 색상 변경 */
.mic-button.active .mic-icon svg {
  color: #ffffff;
}

/* mic-status 텍스트 - 항상 잘 보이도록 */
.mic-status {
  font-size: 0.85rem;
  font-weight: 500;
  color: #1f2937;
  transition: color 0.3s ease;
}

.mic-button.active .mic-status {
  color: #ffffff;
}

/* 마이크 웨이브 애니메이션 개선 */
.mic-waves {
  position: absolute;
  width: 100%;
  height: 100%;
  top: -2px;
  left: -2px;
  pointer-events: none;
}

.mic-waves span {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: 2px solid rgba(255, 255, 255, 0.7);
  border-radius: 50%;
  animation: waves 2s infinite cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
}

@keyframes waves {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  100% {
    transform: scale(2.2);
    opacity: 0;
  }
}

.mic-waves span:nth-child(2) {
  animation-delay: 0.4s;
}

.mic-waves span:nth-child(3) {
  animation-delay: 0.8s;
}

.mic-status {
  font-size: 0.85rem;
  font-weight: 500;
  color: #64748b;
  transition: color 0.3s ease;
}

.mic-button.active .mic-status {
  color: white;
}

/* 스킵 버튼 개선 */
.skip-button {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  padding: 0.8rem 1.6rem;
  border: none;
  border-radius: 9999px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-sm);
}

.skip-button:hover:not(:disabled) {
  background-color: #f1f5f9;
  transform: translateY(-2px);
}

.skip-button:active:not(:disabled) {
  transform: translateY(0);
}

.skip-text {
  font-size: 0.85rem;
  font-weight: 500;
  color: #475569;
}

.skip-icon {
  width: 16px;
  height: 16px;
  color: #475569;
}

/* 반응형 디자인 개선 */
@media (max-width: 768px) {
  .timer-circle {
    width: 100px;
    height: 100px;
  }

  .timer-icon {
    font-size: 1.1rem;
  }

  .timer-text {
    font-size: 0.75rem;
  }

  .timer-count {
    font-size: 1rem;
  }

  .stage-marker {
    width: 70px;
  }

  .marker-circle {
    width: 24px;
    height: 24px;
    font-size: 0.8rem;
  }

  .marker-label {
    font-size: 0.65rem;
  }

  .progress-bar {
    margin-left: 50px;
    margin-right: 50px;
  }

  .tori-avatar {
    width: 40px;
    height: 40px;
  }

  .question-box h2 {
    font-size: 1rem;
  }

  .question-box p {
    
    font-size: 0.85rem;
  }
}

</style>
