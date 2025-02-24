<template>
  <section class="cta">
    <div class="cta-content">
      <h2 class="cta-title">지금 바로 시작하세요</h2>
      <p class="cta-description">토리와 함께 토론의 즐거움을 경험해보세요</p>
      <div class="button-wrapper">
        <LoginButton class="action-button" buttonColor="white" :buttonRadius="1.75" buttonPadding="1rem 2.5"
          buttonFontSize="1rem" buttonTextColor="black" :hasBorder="true" buttonBorder="0.125rem solid #ff6b6b"
          buttonBorderColor="#ff6b6b" hoverColor="#ff6b6b" />
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { useKakaoAuth } from "@/composables/useKakaoAuth";
import LoginButton from "@/components/KakaoLoginButton.vue";

const { handleKakaoLogin } = useKakaoAuth();
</script>

<style scoped>
.cta {
  background-color: white;
  padding: 6.25rem 0;
  text-align: center;
}

.cta-content {
  max-width: 37.5rem;
  margin: 0 auto;
}

.cta-title {
  font-weight: bold;
  color: #111827;
  font-size: 2rem;
  /* 기존 32px → 2rem */
  margin-bottom: 1.25rem;
  /* 기존 20px → 1.25rem */
}

.cta-description {
  font-size: 1.125rem;
  /* 기존 18px → 1.125rem */
  margin-bottom: 1.875rem;
  /* 기존 30px → 1.875rem */
  color: #4b5563;
}

.button-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
