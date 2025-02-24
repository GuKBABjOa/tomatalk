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
        <LoginButton class="action-button" buttonColor="white" :buttonRadius="1.75" buttonPadding="1rem 2.5rem"
          buttonFontSize="1.125rem" buttonTextColor="black" :hasBorder="true" buttonBorder="0.125rem solid #ff6b6b"
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
  background: linear-gradient(to bottom, #fff1f1, #ffffff);

  /* 배경색만 전체 화면 너비 */
  position: relative;
  margin-left: calc(-50vw + 50%);
  margin-right: calc(-50vw + 50%);
  width: 100vw;
  display: flex;
  justify-content: center;
  height: 18.75rem;
  /* 기존 300px → 18.75rem */
  margin-bottom: 12.5rem;
  /* 기존 200px → 12.5rem */
}

.hero>div {
  width: 100%;
  display: flex;
  align-items: center;
  max-width: 31.25rem;
  /* 기존 500px → 31.25rem */
  padding: 0 2rem;
  /* 기존 48px → 3rem */
}

.hero-content {
  width: 100%;
  display: flex;
  margin-left: 0rem;
  /* 기존 90px → 5.625rem */
  flex-direction: column;
  align-items: flex-start;
  position: relative;
  z-index: 1;
}

.hero-title {
  padding-top: 4rem;
  font-size: 3.5rem;
  font-weight: 800;
  color: #111827;
  line-height: 1.5;
}

.hero-title .highlight {
  color: #ff6b6b;
  display: block;
}

.hero-description {
  font-size: 1.4rem;
  /* 기존 1.4rem → 1.25rem */
  margin-top: 1rem;
  /* 기존 20px → 1rem */
  color: #4b5563;
  line-height: 1.7;
}

.hero-buttons {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 1rem;
  /* 기존 20px → 1rem */
  margin-top: 2rem;
  /* 기존 2.5rem → 2rem */
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
  height: 3.75rem;
  /* 기존 60px → 60 / 16 = 3.75rem */
  width: 10.625rem;
  /* 기존 170px → 170 / 16 = 10.625rem */
  border-radius: 1.75rem;
  /* 기존 28px → 28 / 16 = 1.75rem */
  text-decoration: none;
  border-radius: 28px;
  font-family: inherit;
}

.action-button:hover {
  background-color: #ff6b6b;
}

/* 캐릭터 섹션 */
.character-section {
  margin-right: 12.5rem;
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
  width: 18.75rem;
  /* 기존 300px → 300 / 16 = 18.75rem */
  height: 18.75rem;
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

  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);

  transition: all 0.3s ease;
  left: 30%;
  transform: translateX(-50%);
  padding: 1.5rem;
  /* 기존 24px → 1.5rem */
  border-radius: 1rem;
  /* 기존 16px → 1rem */
  width: 23rem;
  /* 기존 400px → 20rem */
  font-size: 1rem;
  /* 기존 20px → 1rem */
}

.highlight {
  color: #ff6b6b;
  white-space: nowrap;
  display: inline;
}

.hover-text.is-visible {
  opacity: 1;
  visibility: visible;
  transform: translateY(-0.5rem);
}

.text-line {
  margin: 0.5rem 0;
  font-size: 0.9px;
  color: #4b5563;
}

.text-line .highlight {
  color: #ff6b6b;
  font-weight: 700;
}

@media (max-width: 48rem) {

  /* 기존 768px → 48rem */
  .hero-title {
    font-size: 2.5rem;
    /* 기존 40px → 2.5rem */
  }

  .hero-description {
    font-size: 1.125rem;
    /* 기존 18px → 1.125rem */
  }

  .secondary-button {
    height: 3rem;
    /* 기존 48px → 3rem */
    width: 8rem;
    /* 기존 128px → 8rem */
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
