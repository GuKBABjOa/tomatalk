<template>
    <!-- 메인 컨텐츠 영역 -->
    <div class="columns-page">
        <!-- 각 카테고리별 섹션 -->
        <div v-for="category in categoriesData" :key="category.id" class="category-section">
            <!-- 카테고리 헤더: 제목과 '더보기' 버튼 -->
            <div class="category-header">
                <h2 class="category-title">{{ category.label }}</h2>
                <a href="#" class="more-link" @click.prevent="seeMore(category)">더 보기 &gt;</a>
            </div>

            <!-- 해당 카테고리의 ColumnCard 그리드 -->
            <div class="scroll-wrapper">
                <!-- 왼쪽 화살표 버튼 -->
                <button class="arrow left-arrow"
                    v-if="columnsByCategory[category.id] && columnsByCategory[category.id].length >= 4"
                    @click="scrollLeft($event)">‹</button>

                <!-- 해당 카테고리의 ColumnCard 그리드 -->
                <div class="columns-grid">
                    <ColumnCard v-for="column in columnsByCategory[category.id] || []" :key="column.columnId"
                        v-bind="column" />
                </div>

                <!-- 오른쪽 화살표 버튼 -->
                <button class="arrow right-arrow"
                    v-if="columnsByCategory[category.id] && columnsByCategory[category.id].length >= 4"
                    @click="scrollRight($event)">›</button>
            </div>
        </div>
    </div>

</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import ColumnCard from '@/components/ColumnCard.vue';

const router = useRouter();
//카테고리 정보
const categoriesData = [
    { id: 'POLITICS', label: '정치/국제' },
    { id: 'ECONOMY', label: '경제/노동' },
    { id: 'ETHICS', label: '윤리/법' },
    { id: 'SCIENCE', label: '과학/기술' },
    { id: 'EDUCATION', label: '교육/사회' }
];

// 각 카테고리별 칼럼 데이터를 저장할 객체
const columnsByCategory = ref<Record<string, any[]>>({});



// API 요청에 사용할 기본 파라미터
const cursor = 9007199254740991;
const size = 5;
const sortBy = "LATEST";

// 각 카테고리에 해당하는 더미 데이터 예시
// 각 카테고리별 데이터를 API로부터 받아오는 함수
async function fetchColumnsForCategory(category: string) {
    try {
        const response = await axios.get("http://localhost:8080/api/columns", {
            params: {
                cursor,
                size,
                sortBy,
                categories: category, // 단일 카테고리 값 전송
                // 필요한 경우 query 파라미터 추가 가능
            },
        });
        // API 응답의 content를 해당 카테고리의 데이터로 할당
        columnsByCategory.value[category] = response.data.content;
    } catch (error) {
        console.error(`Error fetching columns for category ${category}:`, error);
        // 에러 처리: 빈 배열 할당하거나 별도 에러 상태를 관리할 수 있음
        columnsByCategory.value[category] = [];
    }
}
// 모든 카테고리에 대해 API 요청을 병렬로 실행
async function fetchAllColumns() {
    await Promise.all(categoriesData.map(cat => fetchColumnsForCategory(cat.id)));
}

// '더보기' 버튼 클릭 시 실행할 함수 (해당 카테고리 페이지로 이동)
function seeMore(category: { id: string; label: string }) {
    router.push({ name: 'ColumnPageSearch', query: { category: category.id } });
}
// 좌우 화살표 클릭 시 해당 섹션의 그리드 컨테이너를 스크롤
function scrollLeft(event: Event) {
    const target = event.currentTarget as HTMLElement;
    const container = target.parentElement?.querySelector('.columns-grid') as HTMLElement;
    if (container) {
        container.scrollBy({ left: -320, behavior: 'smooth' });
    }
}

function scrollRight(event: Event) {
    const target = event.currentTarget as HTMLElement;
    const container = target.parentElement?.querySelector('.columns-grid') as HTMLElement;
    if (container) {
        container.scrollBy({ left: 320, behavior: 'smooth' });
    }
}

onMounted(() => {
    fetchAllColumns();
});
</script>

<style scoped>
.columns-page {
    margin-left: 280px;
    padding: 2rem;
    flex-grow: 1;
    height: 100vh;
    overflow: auto;
}

.category-section {
    margin-bottom: 3rem;
}

.category-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 10rem;
    /* 좌우에 10rem 패딩 */
    margin-bottom: 1rem;
    padding-bottom: 0.5rem;
    position: relative;
    /* 가상 요소의 위치 기준으로 사용 */
    /* 기존의 border-bottom은 제거 */
}

.category-header::after {
    content: "";
    position: absolute;
    bottom: 0;
    left: 10rem;
    /* 좌측 패딩 만큼 시작 */
    width: calc(100% - 20rem);
    /* 전체 너비에서 좌우 패딩(10rem*2)을 뺌 */
    border-bottom: 1px solid #0f172a;
    /* 구분선 스타일 */
}

.category-title {
    font-size: 1.5rem;
    color: #0f172a;
    margin: 0;
    font-weight: 700;
}

.more-link {
    color: #848383;
    text-decoration: none;
    cursor: pointer;
    font-size: 1rem;
}

.more-link:hover {
    font-weight: 700;
    text-decoration: none;
}

/* 스크롤 컨테이너 */
.scroll-wrapper {
    position: relative;
    padding: 0 10rem;
    overflow: hidden;
}

/* 카드 그리드: flex 레이아웃 */
.columns-grid {
    display: flex;
    gap: 1.5rem;
    /* 양쪽 padding은 화살표와 겹치지 않게 */
    scroll-behavior: smooth;
    /* 스크롤바 감추기 */
    overflow-x: hidden;
}

/* 각 카드에 고정 너비 지정 */
.columns-grid>* {
    flex: 0 0 auto;
    width: 300px;
}

/* 화살표 버튼 스타일 */
.arrow {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    z-index: 10;
    background: rgba(0, 0, 0, 0.5);
    border: none;
    cursor: pointer;
    font-size: 2rem;
    width: 3rem;
    height: 3rem;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    border-radius: 50%;
    opacity: 0.8;
    transition: opacity 0.3s;
}

.arrow:hover {
    opacity: 1;
}

.left-arrow {
    left: 0.5rem;
}

.right-arrow {
    right: 0.5rem;
}
</style>