<template>
  <section class="cta">
    <div class="cta-content">
      <h2 class="cta-title">지금 바로 시작하세요</h2>
      <p class="cta-description">토리와 함께 토론의 즐거움을 경험해보세요</p>
      <div class="button-wrapper">
        <LoginButton class="action-button" buttonColor="white" :buttonRadius="28" buttonPadding="16px 40px"
          buttonFontSize="16px" buttonTextColor="black" :hasBorder="true" buttonBorder="2px solid #ff6b6b"
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
  padding: 100px 0;
  text-align: center;
}

.cta-content {
  max-width: 600px;
  margin: 0 auto;
}

.cta-title {
  font-size: 32px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 20px;
}

.cta-description {
  font-size: 18px;
  color: #4b5563;
  margin-bottom: 30px;
}

.button-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
