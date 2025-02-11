// MatchingIndicator.vue
<template>
  <div class="indicator-container">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="matching-indicator">
      <circle cx="12" cy="12" r="8" :fill="color" opacity="0.3">
        <animate
          attributeName="opacity"
          values="0.3;0.7;0.3"
          dur="2s"
          repeatCount="indefinite"
        />
      </circle>
      <circle cx="12" cy="12" r="4" :fill="color">
        <animate
          attributeName="r"
          values="4;5;4"
          dur="2s"
          repeatCount="indefinite"
        />
        <animate
          attributeName="opacity"
          values="1;0.8;1"
          dur="2s"
          repeatCount="indefinite"
        />
      </circle>
      <circle cx="12" cy="12" r="11" :stroke="color" stroke-width="2" fill="none" opacity="0.2">
        <animate
          attributeName="r"
          values="11;12;11"
          dur="2s"
          repeatCount="indefinite"
        />
      </circle>
    </svg>
  </div>
</template>

<script>
export default {
  name: 'MatchingIndicator',
  props: {
    color: {
      type: String,
      default: '#00C288'
    },
    size: {
      type: Number,
      default: 24
    }
  }
};
</script>

<style scoped>
.indicator-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.matching-indicator {
  width: v-bind('size + "px"');
  height: v-bind('size + "px"');
}
</style>