<template>
  <div class="video-container flex justify-center">
    <video ref="videoElement" autoplay playsinline></video>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";

const props = defineProps({
  streamManager: Object, // OpenVidu StreamManager
});

const videoElement = ref<HTMLVideoElement | null>(null);

onMounted(() => {
  console.log("🎥 UserVideo 컴포넌트가 마운트됨");
  if (props.streamManager && videoElement.value) {
    console.log("✅ 스트림 바인딩 시도", props.streamManager);
    props.streamManager.addVideoElement(videoElement.value);
  }
});

// 스트림이 변경될 때마다 비디오 업데이트
watch(
  () => props.streamManager,
  (newStream) => {
    if (newStream && videoElement.value) {
      console.log("🎥 새로운 스트림 감지", newStream);
      newStream.addVideoElement(videoElement.value);
    }
  }
);
</script>

<style scoped>
.video-container {
  /* width: 100%; */
  /* height: 100%; */
  /* background: black; */
}
video {
  /* width: 100%; */
  height: 100%;
  object-fit: cover;
}
</style>
