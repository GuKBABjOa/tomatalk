<!-- TomaTalk.vue -->
<template>
  <div class="tomatalk-container">
    <!-- Header Section -->
    <div class="header-section">
      <h1 class="title-text">토마톡에서 경험하는 토론 학습</h1>
    </div>

    <!-- Feature Cards Grid -->
    <div class="features-grid">
      <div
        v-for="(feature, index) in features"
        :key="feature.title"
        class="feature-card"
        :class="{ hovered: hoveredCard === index }"
        @mouseenter="hoveredCard = index"
        @mouseleave="hoveredCard = null"
        @click="handleFeatureClick(feature)"
      >
        <div class="preview-area">
          <component :is="feature.preview" />
        </div>

        <div class="content-area">
          <h3 class="card-title">{{ feature.title }}</h3>
          <p class="card-description">{{ feature.description1 }}</p>
          <p class="card-description">{{ feature.description2 }}</p>

          <button class="action-button">
            <span>{{ feature.buttonText }}</span>
            <span class="arrow-icon">→</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineComponent, h, ref, onMounted } from "vue";

// 타입 정의
interface Feature {
  title: string;
  description1: string;
  description2: string;
  buttonText: string;
  preview: any; // 컴포넌트 타입
}

// Preview Components
const DebatePreview = defineComponent({
  name: "DebatePreview",
  setup() {
    return () =>
      h("div", { class: "debate-preview" }, [
        h("div", { class: "message-box ai-message" }, [
          h(
            "div",
            { class: "message-content" },
            '안녕하세요! 오늘의 토론 주제는 "AI 윤리"입니다.'
          ),
        ]),
        h("div", { class: "message-box user-message" }, [
          h(
            "div",
            { class: "message-content" },
            "AI 발전이 인류에게 미치는 영향에 대해 논의해볼까요?"
          ),
        ]),
      ]);
  },
});

const LiveDebatePreview = defineComponent({
  name: "LiveDebatePreview",
  setup() {
    return () =>
      h("div", { class: "live-debate-preview" }, [
        h("div", { class: "video-grid" }, [
          ...Array(4)
            .fill(null)
            .map((_, i) => h("div", { key: i, class: "video-box" })),
        ]),
      ]);
  },
});

const WatchingPreview = defineComponent({
  name: "WatchingPreview",
  setup() {
    return () =>
      h("div", { class: "watching-preview" }, [
        h("div", { class: "debate-item" }, [
          h("div", { class: "debate-title" }, "기후변화와 경제발전"),
          h("div", { class: "debate-status" }, "LIVE"),
          h("div", { class: "debate-participants" }, "참여자 12명"),
        ]),
        h("div", { class: "debate-item" }, [
          h("div", { class: "debate-title" }, "교육 평가 방식의 변화"),
          h("div", { class: "debate-status" }, "LIVE"),
          h("div", { class: "debate-participants" }, "참여자 8명"),
        ]),
      ]);
  },
});

const MyPagePreview = defineComponent({
  name: "MyPagePreview",
  setup() {
    return () =>
      h("div", { class: "mypage-preview" }, [
        h("div", { class: "stats-section" }, [
          h("h4", null, "토론 참여 현황"),
          h("div", { class: "stats-graph" }),
        ]),
        h("div", { class: "certificate-section" }, [
          h("div", { class: "certificate-icon" }),
          h("div", { class: "certificate-info" }, [
            h("div", { class: "certificate-title" }, "토론왕 인증서"),
            h("div", { class: "certificate-date" }, "2024.02.16 획득"),
          ]),
        ]),
      ]);
  },
});

const hoveredCard = ref<null | number>(null);
const isGreeting = ref(false);
const showBubble = ref(true);
const greetingText = "안녕하세요! 토리예요 👋";

// Features data
const features = ref<Feature[]>([
  {
    title: "토론 연습",
    description1: "AI 토리와 함께하는",
    description2: "1:1 맞춤형 토론 연습",
    buttonText: "연습 시작하기",
    preview: DebatePreview,
  },
  {
    title: "실전 토론",
    description1: "다른 사용자들과 함께하는",
    description2: "실시간 화상 토론",
    buttonText: "토론 참여하기",
    preview: LiveDebatePreview,
  },
  {
    title: "진행 중인 토론",
    description1: "실시간으로 진행되는",
    description2: "토론 구경하기",
    buttonText: "구경하기",
    preview: WatchingPreview,
  },
  {
    title: "마이페이지",
    description1: "나의 토론 기록과",
    description2: "실력 인증서 확인",
    buttonText: "기록 보기",
    preview: MyPagePreview,
  },
]);

const getImageUrl = (filename: string): string => {
  return new URL(`../assets/${filename}`, import.meta.url).href;
};

const startGreeting = () => {
  isGreeting.value = true;
  showBubble.value = true;
};

const stopGreeting = () => {
  isGreeting.value = false;
};

const handleFeatureClick = (feature: Feature): void => {
  console.log(`Clicked feature: ${feature.title}`);
};

onMounted(() => {
  showBubble.value = true;
});
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css");

.tomatalk-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: "Pretendard", sans-serif;
}

/* Header Section */
.header-section {
  margin-bottom: 48px;
}

.title-text {
  font-size: 32px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 32px;
}

/* Tori Section */
.tori-section {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin: 40px 0;
  position: relative;
}

.tori-container {
  position: relative;
  z-index: 2;
}

.tori-character {
  width: 180px;
  height: auto;
  transform-origin: bottom center;
  transition: all 0.3s ease-in-out;
  position: relative;
  z-index: 2;
}

.tori-greeting {
  animation: bounce 0.5s ease-in-out;
  transform: scale(1.05);
}

.tori-shadow {
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 20px;
  background: radial-gradient(
    ellipse at center,
    rgba(0, 0, 0, 0.1) 0%,
    rgba(0, 0, 0, 0) 70%
  );
  border-radius: 50%;
  transition: all 0.3s ease-in-out;
}

.tori-greeting + .tori-shadow {
  width: 110px;
  opacity: 0.15;
}

/* Speech Bubble */
.speech-bubble-container {
  flex-grow: 1;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.4s ease-out;
}

.bubble-show {
  opacity: 1;
  transform: translateY(0);
}

.speech-bubble {
  position: relative;
  background-color: #fff1f1;
  padding: 24px;
  border-radius: 16px;
  border: 2px solid #cacaca;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.speech-bubble::before {
  content: "";
  position: absolute;
  left: -12px;
  top: 40px;
  width: 24px;
  height: 24px;
  background-color: #fff1f1;
  border-left: 2px solid #cacaca;
  border-bottom: 2px solid #cacaca;
  transform: rotate(45deg);
}

.bubble-content {
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.4s ease-out 0.2s;
}

.content-show {
  opacity: 1;
  transform: translateY(0);
}

.greeting-text {
  font-size: 20px;
  color: #111827;
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
}

.greeting-char {
  display: inline-block;
  opacity: 0;
  animation: fadeInChar 0.3s ease-out forwards;
}

.sub-text {
  font-size: 16px;
  color: #4b5563;
  opacity: 0;
  transform: translateY(5px);
  transition: all 0.4s ease-out 0.4s;
}

.sub-text.fade-in {
  opacity: 1;
  transform: translateY(0);
}

/* Features Grid */
.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

/* Feature Card */
.feature-card {
  background-color: #f6f6f6;
  border: 2px solid #cacaca;
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.feature-card.hovered {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.preview-area {
  background-color: #ffffff;
  border: 1px solid #cacaca;
  border-radius: 8px;
  height: 160px;
  margin-bottom: 20px;
  overflow: hidden;
}

.content-area {
  padding: 0 16px;
}

.card-title {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 12px;
}

.card-description {
  font-size: 18px;
  color: #4b5563;
  margin-bottom: 4px;
}

/* Button Styles */
.action-button {
  width: 100%;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 12px 24px;
  font-size: 16px;
  margin-top: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s ease-in-out;
}

.action-button:hover {
  background-color: #ff5252;
}

.arrow-icon {
  transition: transform 0.2s ease-in-out;
}

.feature-card:hover .arrow-icon {
  transform: translateX(4px);
}

/* Preview Components */
.debate-preview,
.watching-preview {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-box {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.message-content {
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
}

.ai-message .message-content {
  background-color: #fff1f1;
}

.user-message {
  justify-content: flex-end;
}

.user-message .message-content {
  background-color: white;
  border: 1px solid #cacaca;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  padding: 16px;
}

.video-box {
  aspect-ratio: 16/9;
  background-color: #e5e5e5;
  border-radius: 4px;
}

.debate-item {
  background-color: white;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #cacaca;
}

.debate-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.debate-status {
  color: #ff6b6b;
  font-size: 12px;
}

.debate-participants {
  color: #6b7280;
  font-size: 12px;
}

/* MyPage Preview */
.mypage-preview {
  padding: 16px;
}

.stats-section {
  margin-bottom: 12px;
}

.stats-graph {
  height: 60px;
  background-color: #fff1f1;
  border-radius: 4px;
  margin-top: 8px;
}

.certificate-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.certificate-icon {
  width: 48px;
  height: 48px;
  background-color: #fff1f1;
  border-radius: 4px;
}

.certificate-title {
  font-weight: 500;
  font-size: 14px;
}

.certificate-date {
  color: #6b7280;
  font-size: 12px;
}

/* Animations */
@keyframes bounce {
  0%,
  100% {
    transform: scale(1.05) translateY(0);
  }
  50% {
    transform: scale(1.05) translateY(-10px);
  }
}

@keyframes fadeInChar {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .tori-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .tori-character {
    width: 140px;
  }

  .speech-bubble::before {
    left: 50%;
    top: -12px;
    transform: translateX(-50%) rotate(135deg);
  }

  .speech-bubble-container {
    width: 100%;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .title-text {
    font-size: 28px;
  }

  .card-title {
    font-size: 22px;
  }

  .card-description {
    font-size: 16px;
  }
}
</style>
