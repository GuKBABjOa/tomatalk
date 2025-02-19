<!-- src/views/Home.vue -->
<template>
  <div>
    <Hero />
    <Features />
    <Curriculum />
    <CallToAction />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Hero from "@/components/Hero.vue";
import Features from "@/components/Features.vue";
import Curriculum from "@/components/Curriculum.vue";
import CallToAction from "@/components/CallToAction.vue";

export default defineComponent({
  name: "Home",
  components: {
    Hero,
    Features,
    Curriculum,
    CallToAction,
  },
});
</script>
