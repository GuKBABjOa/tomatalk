<!-- PREPIntro.vue -->
<template>
    <div class="max-w-5xl mx-auto p-4 bg-white min-h-screen">
        <!-- Header Section -->
        <div class="bg-gray-50 p-8 rounded-2xl mb-8">
            <div class="flex justify-between items-start">
                <div>
                    <h1 class="text-4xl font-bold text-gray-800 mb-2">
                        PREP으로 시작하는<br />논리적인 토론
                    </h1>
                    <p class="text-gray-600 mt-4">
                        체계적인 논증으로 설득력 있는 토론을 시작해보세요
                    </p>
                </div>
                <div class="bg-gray-100 p-4 rounded-xl text-center">
                    <p class="text-gray-600 mb-1">예상 학습 시간</p>
                    <p class="text-2xl font-bold text-gray-700">30분</p>
                </div>
            </div>
        </div>

        <!-- PREP Structure Cards -->
        <div class="flex justify-between items-center gap-4 mb-12">
            <template v-for="(item, index) in prepSteps" :key="index">
                <div class="flex-1">
                    <div class="bg-white rounded-lg shadow-sm p-6 h-full">
                        <div class="flex flex-col items-center">
                            <div class="w-12 h-12 rounded-full bg-gray-50 flex items-center justify-center mb-4">
                                <span class="text-lg text-gray-600">{{ item.letter }}</span>
                            </div>
                            <h3 class="text-lg font-bold text-gray-700 mb-2">{{ item.title }}</h3>
                            <p class="text-gray-500 text-sm mb-2">{{ item.subtitle }}</p>
                            <p class="text-gray-500 text-xs text-center">
                                {{ item.description1 }}<br />
                                {{ item.description2 }}
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Arrow between cards -->
                <div v-if="index < prepSteps.length - 1" class="flex-shrink-0">
                    <svg class="w-6 h-6 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                    </svg>
                </div>
            </template>
        </div>

        <!-- Start Button -->
        <div class="text-center">
            <button @click="handleStart"
                class="px-12 py-3 bg-red-500 text-white rounded-lg hover:bg-[#ff5b5b] transition-colors">
                시작하기
            </button>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const prepSteps = [
    {
        letter: 'P',
        title: 'Point',
        subtitle: '명확한 주장',
        description1: '핵심 메시지를',
        description2: '간단명료하게'
    },
    {
        letter: 'R',
        title: 'Reason',
        subtitle: '논리적 근거',
        description1: '주장을 뒷받침하는',
        description2: '타당한 이유 제시'
    },
    {
        letter: 'E',
        title: 'Example',
        subtitle: '구체적 예시',
        description1: '근거를 뒷받침하는',
        description2: '실제 사례 제시'
    },
    {
        letter: 'P',
        title: 'Point',
        subtitle: '주장 재강조',
        description1: '근거와 예시로',
        description2: '강화된 주장 마무리'
    }
]

const handleStart = () => {
    router.push({ name: 'PrepDetail' })
}
</script>

<style scoped>
/* 필요한 추가 스타일링이 있다면 여기에 작성 */
</style>