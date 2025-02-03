<script setup>
import { RouterView } from 'vue-router';
</script>

<template>
  <div id="app-container" class="h-screen">
    <component :is="$route.meta.layout || 'div'" class="h-full">
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </component>
  </div>
</template>

<style>
#app {
  display: flex;
  justify-content: center;
}

#app-container {
  background-color: rgb(255, 255, 255);
}

@media (min-width: 390px) {
  #app-container {
    width: 600px;
  }
}
</style>
