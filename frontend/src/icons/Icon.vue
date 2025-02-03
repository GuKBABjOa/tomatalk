<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
    name: string;
    size?: string;
}>();


const iconPath = computed(() => new URL(`../assets/${props.name}.svg`, import.meta.url).href);
</script>

<template>
    <img :src="iconPath" :alt="name" :width="size || '24px'" :height="size || '24px'" />
</template>
