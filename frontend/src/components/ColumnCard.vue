<template>
    <column class="column-card">
        <img :src="imageUrl" alt="Column thumbnail" style="width: 100%; height: 200px; object-fit: cover;" />
        <div style="padding: 1.5rem;">
            <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 1rem;">
                <span :class="['category-badge', categoryClass]">{{ categoryLabel }}</span>
                <button class="like-button">
                    ❤️ {{ likes }}
                </button>
            </div>
            <h3 style="font-size: 1.25rem; font-weight: 600; color: #0f172a; margin-bottom: 0.75rem;">
                {{ title }}
            </h3>
            <p style="color: #64748b; margin-bottom: 1rem; line-height: 1.5;">
                {{ description }}
            </p>
            <div
                style="display: flex; justify-content: space-between; align-items: center; color: #94a3b8; font-size: 0.875rem;">
                <span>{{ date }}</span>
            </div>
        </div>
    </column>
</template>

<script setup>
import { computed } from 'vue'
import { defineProps } from 'vue'

const props = defineProps({
    imageUrl: {
        type: String,
        default: '/api/placeholder/400/200'
    },
    // category prop의 값은 'politics', 'economy', 'ethics', 'science', 'education' 중 하나여야 합니다.
    category: {
        type: String,
        default: 'politics'
    },
    title: {
        type: String,
        required: true
    },
    description: {
        type: String,
        required: true
    },
    likes: {
        type: Number,
        default: 0
    },
    date: {
        type: String,
        default: ''
    }
})

const categoryLabel = computed(() => {
    switch (props.category) {
        case 'politics':
            return '정치/국제'
        case 'economy':
            return '경제/노동'
        case 'ethics':
            return '윤리/법'
        case 'science':
            return '과학/기술'
        case 'education':
            return '교육/사회'
        default:
            return props.category
    }
})

const categoryClass = computed(() => {
    // props.category 값을 그대로 CSS 클래스명으로 사용합니다.
    return props.category
})
</script>

<style scoped>
.column-card {
    background: white;
    border-radius: 0.5rem;
    border: 1px solid #e2e8f0;
    transition: all 0.2s;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.column-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    cursor: pointer;
}

.category-badge {
    display: inline-block;
    padding: 0.25rem 0.75rem;
    border-radius: 1rem;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

/* 카테고리별 스타일 */
.category-badge.politics {
    background-color: #fbe9e7;
    color: #d84315;
}

.category-badge.economy {
    background-color: #e8f5e9;
    color: #43a047;
}

.category-badge.ethics {
    background-color: #e3f2fd;
    color: #1e88e5;
}

.category-badge.science {
    background-color: #f3e5f5;
    color: #8e24aa;
}

.category-badge.education {
    background-color: #fffde7;
    color: #f9a825;
}

.like-button {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;
    border: 1px solid #e2e8f0;
    border-radius: 0.375rem;
    background: white;
    cursor: pointer;
    transition: all 0.2s;
}

.like-button:hover {
    background: #fee2e2;
    border-color: #ef4444;
    color: #ef4444;
}
</style>