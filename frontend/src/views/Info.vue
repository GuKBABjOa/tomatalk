<!-- src/views/Home.vue -->
<template>
  <div>
    <Introduce />
    <ServiceDescription />
    <CurriculumnInfo />
    <CallToAction />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import Introduce from "@/components/Introduce.vue";
import ServiceDescription from "@/components/ServiceDescription.vue";
import CurriculumnInfo from "@/components/CurriculumnInfo.vue";
import CallToAction from "@/components/CallToAction.vue";

export default defineComponent({
  name: "Info",
  components: {
    Introduce,
    ServiceDescription,
    CurriculumnInfo,
    CallToAction,
  },
});
</script>
