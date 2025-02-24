<template>
  <button class="start-button" :style="{
    backgroundColor: buttonColor,
    borderRadius: buttonRadius,
    padding: buttonPadding,
    fontSize: buttonFontSize,
    color: buttonTextColor,
    border: hasBorder ? buttonBorder : 'none',
    borderColor: hasBorder ? buttonBorderColor : 'transparent',
  }" @click="handleClick" @mouseover="applyHoverStyle" @mouseleave="resetStyle" ref="buttonRef">
    <img :src="getImageUrl('kakao.svg')" alt="카카오 로그인" class="kakao-icon" />
    시작하기
  </button>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useKakaoAuth } from "@/composables/useKakaoAuth";

const { handleKakaoLogin } = useKakaoAuth();
const emit = defineEmits(["click"]);
const handleClick = async () => {
  emit("click");
  await handleKakaoLogin();
};

const props = defineProps<{
  buttonColor?: string;
  buttonRadius?: number;
  buttonPadding?: string;
  buttonFontSize?: string;
  buttonTextColor?: string;
  hasBorder?: boolean;
  buttonBorder?: string;
  buttonBorderColor?: string;
  hoverColor?: string;
}>();

const buttonRef = ref<HTMLButtonElement | null>(null);

const applyHoverStyle = () => {
  if (buttonRef.value && props.hoverColor) {
    buttonRef.value.style.backgroundColor = props.hoverColor;
  }
};

const resetStyle = () => {
  if (buttonRef.value) {
    buttonRef.value.style.backgroundColor = props.buttonColor || "";
    buttonRef.value.style.color = props.buttonTextColor || "";
  }
};

const getImageUrl = (filename: string): string => {
  return new URL(`../assets/${filename}`, import.meta.url).href;
};
</script>

<style scoped>
.start-button {
  height: 3.75rem;
  /* 기존 60px → 3.75rem */
  width: 10.625rem;
  /* 기존 170px → 10.625rem */
  border-radius: 1.75rem;
  /* 기존 28px → 1.75rem */
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: inherit;
  text-decoration: none;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.kakao-icon {
  width: 1.3rem;
  /* 기존 28px → 1.75rem */
  height: 1.3rem;
}
</style>
