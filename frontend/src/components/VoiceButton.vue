<script setup lang="ts">
import { computed } from "vue";
//버튼은 텍스트, 배경색,텍스트색, 너비, 높이, 폰트 크기를 받습니다
const props = defineProps<{
    text: string;
    bgColor?: string;
    textColor?: string;
    width?: string;
    height?: string;
    fontSize?: string;
}>();

// 버튼의 스타일 입니다.
const buttonStyle = computed(() => ({
    backgroundColor: props.bgColor || "#EBEBEB",
    // 버튼의 기본 크기는 타이머와 스킵하기 크기입니다.
    width: props.width || "114px",
    height: props.height || "28px",
}));
//버튼내 텍스트의 스타일 입니다.
const textStyle = computed(() => ({
    //글자는 디폴트 하얀색
    color: props.textColor || "#000",
    //폰트 사이즈를 입력받을때 px가 없으면 px를 붙여줍니다.
    fontSize: props.fontSize?.includes("px") ? props.fontSize : `${props.fontSize || "20"}px`,
}));
</script>

<template>
    <button :style="buttonStyle">
        <div class="button-text" :style="textStyle">{{ text }}</div>
    </button>
</template>

<style scoped>
button {
    border: none;
    color: white;
    padding: 5px 15px;
    border-radius: 10px;
    cursor: pointer;
    transition: opacity 0.3s;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    flex-shrink: 0;
}

button:hover {
    opacity: 0.8;
}

.button-text {
    text-align: center;
    font-family: "Inter", sans-serif;
    font-style: normal;
    font-weight: 800;
    line-height: normal;
    flex-shrink: 0;
    white-space: pre-line;
    
}
</style>
