<template>
    <div class="debate-card">
        <div class="debate-header">
            <h3 class="debate-title">{{ debate.title }}</h3>
            <div class="viewer-count">
                <span class="viewer-icon">👥</span>
                <span class="viewer-number">{{ debate.participants }}명</span>
            </div>
        </div>
        <div class="debate-status">
            <span class="live-badge">
                <span class="red-dot"></span> 실시간 진행 중
            </span>
            <span class="elapsed-time">경과시간: {{ debate.duration }}분</span>
        </div>
        <div class="debate-timing">
            <span class="time-info">⏰ 시작: {{ debate.startTime }}</span>
            <span class="time-info">⏳ 예상 종료: {{ debate.endTime }}</span>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { Debate } from "@/types/debate";

const props = defineProps<{
    debate: Debate;
}>();
</script>

<style scoped>
.debate-card {
    background: white;
    padding: 1.5rem;
    border-radius: 1rem;
    border: 1px solid #e5e7eb;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    transition: all 0.3s ease-in-out;
}

.debate-card:hover {
    border-color: #2563eb;
    cursor: pointer;
}

.debate-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.debate-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #1f2937;
}

.viewer-count {
    display: flex;
    align-items: center;
    font-size: 0.9rem;
    color: #2563eb;
}

.viewer-icon {
    margin-right: 4px;
}

.debate-status {
    display: flex;
    align-items: center;
    gap: 1rem;
    font-size: 0.9rem;
    color: #4b5563;
}

.live-badge {
    display: flex;
    align-items: center;
    background: rgba(83, 127, 247, 0.15);
    color: blue;
    padding: 4px 8px;
    border-radius: 8px;
}

.red-dot {
    width: 8px;
    height: 8px;
    background: #ff3b3b;
    border-radius: 50%;
    margin-right: 5px;
}

.elapsed-time {
    font-size: 0.9rem;
    color: #6b7280;
}

.debate-timing {
    display: flex;
    gap: 1.5rem;
    font-size: 0.9rem;
    color: #4b5563;
}

.time-info {
    display: flex;
    align-items: center;
    gap: 4px;
}
</style>