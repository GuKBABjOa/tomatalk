<template>
    <div class="profile-avatar" :style="{ width: size + 'px', height: size + 'px' }">
      <img
        :src="imageSrc || defaultImage"
        alt="프로필 이미지"
        class="profile-image"
        :style="{ width: size + 'px', height: size + 'px' }"
      />
    </div>
  </template>
  
  <script setup>
  import { computed } from "vue";
  
  defineProps({
    imageSrc: {
      type: String,
      default: null, // 기본적으로 이미지 없음
    },
    size: {
      type: Number,
      default: 50, // 기본 크기 50px
    },
  });
  
  const defaultImage = computed(() => new URL("@/assets/defaultImage.svg", import.meta.url).href);
  </script>
  
  <style scoped>
  .profile-avatar {
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  }
  
  .profile-image {
    border-radius: 50%;
    object-fit: cover;
  }
  </style>
  