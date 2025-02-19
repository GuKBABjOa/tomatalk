<template>
  <section class="hero">
    <div class="hero-content">
      <h1 class="hero-title">
        토론 실력을 키우는
        <span class="highlight">가장 스마트한 방법</span>
      </h1>

      <h2 class="hero-description">
        단계별 토론 연습부터 실전 토론까지,<br />
        성장의 달콤함을 지금 경험해보세요.
      </h2>

      <div class="hero-buttons">
        <LoginButton class="action-button" buttonColor="white" :buttonRadius="28" buttonPadding="16px 40px"
          buttonFontSize="16px" buttonTextColor="black" :hasBorder="true" buttonBorder="2px solid #ff6b6b"
          buttonBorderColor="#ff6b6b" hoverColor="#ff6b6b" />
        <router-link to="/info" class="action-button secondary-button">
          더 알아보기
        </router-link>
      </div>
    </div>

    <div class="character-section" @mouseenter="handleMouseEnter" @mouseleave="handleMouseLeave">
      <router-link to="/info" class="tori-character">
        <img :src="getImageUrl('Tori.svg')" alt="토리 이미지" class="Tori" />
      </router-link>

      <div class="hover-text" :class="{ 'is-visible': isHovered }">
        <h3>안녕하세요!🙌</h3>
        <p>
          <span class="no-wrap">토론 마스터 클래스 <strong>토마톡</strong>의 리더
            <span class="highlight"><strong>토리</strong></span>에요🍅
          </span>
        </p>
        <p class="no-wrap">
          체계적인 토론 학습부터 실전 매칭까지, <br />당신의 성장을
          <span class="highlight"><strong>토리</strong></span>가 함께할게요!
        </p>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useKakaoAuth } from "@/composables/useKakaoAuth";
import LoginButton from "@/components/KakaoLoginButton.vue";

const isHovered = ref(false);
const { handleKakaoLogin } = useKakaoAuth();

const getImageUrl = (filename: string): string => {
  return new URL(`../assets/${filename}`, import.meta.url).href;
};

// character-section에 추가할 이벤트 핸들러
const handleMouseEnter = () => {
  isHovered.value = true;
};

const handleMouseLeave = () => {
  isHovered.value = false;
};
</script>

<style scoped>
/* 공통 스타일 */
.hero {
  height: 300px;
  background: linear-gradient(to bottom, #fff1f1, #ffffff);

  /* 배경색만 전체 화면 너비 */
  position: relative;
  margin-left: calc(-50vw + 50%);
  margin-right: calc(-50vw + 50%);
  width: 100vw;
  display: flex;
  justify-content: center;
  margin-bottom: 200px;
}

.hero>div {
  width: 100%;
  max-width: 500px;
  display: flex;
  align-items: center;
  padding: 0 48px;
}

.hero-content {
  width: 100%;
  display: flex;
  margin-left: 90px;
  flex-direction: column;
  align-items: flex-start;
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 56px;
  font-weight: 800;
  color: #111827;
  line-height: 1.2;
}

.hero-title .highlight {
  color: #ff6b6b;
  display: block;
}

.hero-description {
  margin-top: 20px;
  font-size: 22px;
  color: #4b5563;
  line-height: 1.7;
}

.hero-buttons {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 20px;
  margin-top: 40px;
}

.action-button {
  display: flex;
  align-items: center;
  justify-content: center;
}

.secondary-button {
  background-color: white;
  color: #000000;
  border: 2px solid #ff6b6b;
  height: 60px;
  width: 170px;
  text-decoration: none;
  border-radius: 28px;
  font-family: inherit;
}

.action-button:hover {
  background-color: #ff6b6b;
}

/* 캐릭터 섹션 */
.character-section {
  margin-right: 300px;
  position: relative;
  cursor: pointer;
}

/* 캐릭터 애니메이션 */
@keyframes float {
  0% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-10px);
  }

  100% {
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes nod {
  0% {
    transform: rotate(0deg);
  }

  30% {
    transform: rotate(-5deg);
  }

  60% {
    transform: rotate(3deg);
  }

  100% {
    transform: rotate(0deg);
  }
}

.tori-character {
  width: 300px;
  height: 300px;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform-origin: center bottom;
  will-change: transform;
  animation: float 3s ease-in-out infinite, fadeIn 1s ease-out,
    sparkle 2s ease-in-out infinite;
}

.character-section:hover .tori-character {
  animation: nod 0.5s ease-in-out;
}

@keyframes sparkle {
  0% {
    filter: brightness(1);
  }

  50% {
    filter: brightness(1.3);
  }

  100% {
    filter: brightness(1);
  }
}

.no-wrap {
  white-space: nowrap;
}

/* 호버 텍스트 */
.hover-text {
  opacity: 0;
  visibility: hidden;
  position: absolute;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  width: 400px;
  transition: all 0.3s ease;
  left: 30%;
  transform: translateX(-50%);
  font-size: 20px;
}

.highlight {
  color: #ff6b6b;
  white-space: nowrap;
  display: inline;
}

.hover-text.is-visible {
  opacity: 1;
  visibility: visible;
}

.text-line {
  margin: 8px 0;
  font-size: 16px;
  color: #4b5563;
}

.text-line .highlight {
  color: #ff6b6b;
  font-weight: 700;
}

/* 모바일 대응 */
@media (max-width: 768px) {
  .character-section {
    flex-direction: column;
    text-align: center;
  }

  .tori-character {
    animation: fadeIn 0.5s ease-out;
  }

  .character-section:hover .tori-character {
    animation: none;
  }

  .hover-text {
    width: 100%;
    max-width: 100%;
    padding: 16px;
  }
}

/* 접근성 고려 */
@media (prefers-reduced-motion: reduce) {
  .tori-character {
    animation: fadeIn 0.5s ease-out;
  }

  .character-section:hover .tori-character {
    animation: none;
    transform: scale(1.02);
  }

  .hover-text {
    transition: none;
  }
}
</style>
