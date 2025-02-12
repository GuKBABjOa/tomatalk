<template>
    <div class="app">

        <main class="main-content">
            <h1 class="title">토론 아카이브</h1>
            <p class="subtitle">지난 토론들의 기록을 칼럼으로 확인하세요.</p>
            <!-- SearchBar 내부에서 정렬 기능 포함 -->
            <SearchBar v-model="searchQuery" :sortOption="sortOption" @update:sortOption="sortOption = $event"
                placeholder="토론 주제, 키워드로 검색..." :sortOptions="[
                    { value: 'latest', label: '최신순' },
                    { value: 'popular', label: '인기순' },
                ]" :categories="[
                    { value: 'politics', label: '정치/국제' },
                    { value: 'economy', label: '경제/노동' },
                    { value: 'ethics', label: '윤리/법' },
                    { value: 'science', label: '과학/기술' },
                    { value: 'education', label: '교육/사회' }
                ]" :initialSelectedCategories="[selectedCategory]" />
            <!-- 필터링된 칼럼 카드 목록 -->
            <div v-if="filteredColumns.length > 0" class="columns-grid">
                <ColumnCard v-for="(column, index) in filteredColumns" :key="`${selectedCategory}-${index}`"
                    v-bind="column" />
            </div>
            <div v-else>
                <p>선택된 카테고리에 해당하는 칼럼이 없습니다.</p>
            </div>
        </main>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import SearchBar from "@/components/SearchBar.vue";
import ColumnCard from "@/components/ColumnCard.vue";
// URL 쿼리에서 선택된 카테고리 읽어오기
const route = useRoute();
const selectedCategory = ref<string>('');
onMounted(() => {
    if (route.query.category) {
        selectedCategory.value = route.query.category as string;
    }
});


const searchQuery = ref("");
const sortOption = ref("latest");

// 더미 데이터: 여러 카테고리별 칼럼 데이터
const categories = ref([
    {
        id: 'politics',
        label: '정치/국제',
        columns: [
            { id: 1, title: '정치 이슈 A', description: '정치 이슈 A에 대한 내용입니다.', likes: 15, date: '2024-03-01' },
            { id: 2, title: '정치 이슈 B', description: '정치 이슈 B에 대한 내용입니다.', likes: 22, date: '2024-03-02' },
            // 추가 카드...
        ]
    },
    {
        id: 'economy',
        label: '경제/노동',
        columns: [
            { id: 3, title: '경제 이슈 A', category: "economy", description: '경제 이슈 A에 대한 내용입니다.', likes: 30, date: '2024-03-03' },
            { id: 4, title: '경제 이슈 B', category: "economy", description: '경제 이슈 B에 대한 내용입니다.', likes: 18, date: '2024-03-04' },
            // 추가 카드...
        ]
    },
    {
        id: 'ethics',
        label: '윤리/법',
        columns: [
            { id: 5, title: '윤리 이슈 A', description: '윤리 이슈 A에 대한 내용입니다.', likes: 12, date: '2024-03-05' },
            { id: 6, title: '윤리 이슈 B', description: '윤리 이슈 B에 대한 내용입니다.', likes: 25, date: '2024-03-06' }
        ]
    },
    {
        id: 'science',
        label: '과학/기술',
        columns: [
            { id: 7, title: '과학 이슈 A', description: '과학 이슈 A에 대한 내용입니다.', likes: 40, date: '2024-03-07' },
            { id: 8, title: '과학 이슈 B', description: '과학 이슈 B에 대한 내용입니다.', likes: 35, date: '2024-03-08' }
        ]
    },
    {
        id: 'education',
        label: '교육/사회',
        columns: [
            { id: 9, title: '교육 이슈 A', description: '교육 이슈 A에 대한 내용입니다.', likes: 10, date: '2024-03-09' },
            { id: 10, title: '교육 이슈 B', description: '교육 이슈 B에 대한 내용입니다.', likes: 20, date: '2024-03-10' }
        ]
    }
]);

// 선택된 카테고리에 따른 칼럼 목록 필터링
const filteredColumns = computed(() => {
    if (!selectedCategory.value) {
        // 선택된 카테고리가 없으면 빈 배열 혹은 전체를 반환할 수 있음 (여기서는 빈 배열로)
        return [];
    }
    // 선택된 카테고리와 일치하는 카테고리 객체 찾기
    const cat = categories.value.find(category => category.id === selectedCategory.value);
    return cat ? cat.columns : [];
});
</script>

<style scoped>
/* 제목과 검색바 간격 조절 */
.header-section {
    margin-bottom: 1.5rem;
    /* 검색창과의 거리 */
}

/* 제목 스타일 */
.title {
    font-size: 1.75rem;
    /* 기존보다 크게 */
    font-weight: 700;
    /* 더 굵게 */
    color: #1f2937;
    /* 어두운 색 */
    margin-bottom: 0.25rem;
    /* 부제목과 간격 조정 */
}

/* 부제목 스타일 */
.subtitle {
    font-size: 1rem;
    color: #6b7280;
    /* 회색 톤 */
    margin-bottom: 1.5rem;
    /* 검색창과 간격 조정 */
}

.app {
    display: flex;
    background-color: #f1f5f9;
    min-height: 100vh;
}

.main-content {
    margin-left: 280px;
    padding: 2rem;
    flex-grow: 1;
    height: 100vh;
    overflow: auto;
}

.grid-container {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    margin-bottom: 1.5rem;
}

.card {
    background: white;
    padding: 1.5rem;
    border-radius: 1rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.columns-grid {
    display: flex;
    gap: 1.5rem;
    flex-wrap: wrap;
}

.columns-grid>* {
    flex: 0 0 calc((100% - 3rem) / 4);
}
</style>