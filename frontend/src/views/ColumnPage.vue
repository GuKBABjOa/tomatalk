<template>
    <!-- 메인 컨텐츠 영역 -->
    <div class="columns-page">
        <!-- 각 카테고리별 섹션 -->
        <div v-for="category in categories" :key="category.id" class="category-section">
            <!-- 카테고리 헤더: 제목과 '더보기' 버튼 -->
            <div class="category-header">
                <h2 class="category-title">{{ category.label }}</h2>
                <a href="#" class="more-link" @click.prevent="seeMore(category)">더 보기 &gt;</a>
            </div>

            <!-- 해당 카테고리의 ColumnCard 그리드 -->
            <div class="scroll-wrapper">
                <!-- 왼쪽 화살표 버튼 -->
                <button class="arrow left-arrow" v-if="category.columns.length >= 4"
                    @click="scrollLeft($event)">‹</button>

                <!-- 해당 카테고리의 ColumnCard 그리드 -->
                <div class="columns-grid">
                    <ColumnCard v-for="column in category.columns.slice(0, 5)" :key="column.id" v-bind="column" />
                </div>

                <!-- 오른쪽 화살표 버튼 -->
                <button class="arrow right-arrow" v-if="category.columns.length >= 4"
                    @click="scrollRight($event)">›</button>
            </div>
        </div>
    </div>

</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
//   import SearchBar from '@/components/SearchBar.vue'
import ColumnCard from '@/components/ColumnCard.vue'

const router = useRouter();

// 각 카테고리에 해당하는 더미 데이터 예시
const categories = ref([
    {
        id: 'politics',
        label: '정치/국제',
        columns: [
            { id: 1, title: '정치 이슈 A', description: '정치 이슈 A에 대한 내용입니다.', likes: 15, date: '2024-03-01' },
            { id: 2, title: '정치 이슈 B', description: '정치 이슈 B에 대한 내용입니다.', likes: 22, date: '2024-03-02' },
            { id: 1, title: '정치 이슈 A', description: '정치 이슈 A에 대한 내용입니다.', likes: 15, date: '2024-03-01' },
            { id: 2, title: '정치 이슈 B', description: '정치 이슈 B에 대한 내용입니다.', likes: 22, date: '2024-03-02' },
            // 추가 카드...
        ]
    },
    {
        id: 'economy',
        label: '경제/노동',
        columns: [
            { id: 3, category: "economy", title: '경제 이슈 A', description: '경제 이슈 A에 대한 내용입니다.', likes: 30, date: '2024-03-03' },
            { id: 4, category: "economy", title: '경제 이슈 B', description: '경제 이슈 B에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 18, date: '2024-03-04' },
            { id: 3, category: "economy", title: '경제 이슈 A', description: '경제 이슈 A에 대한 내용입니다.', likes: 30, date: '2024-03-03' },
            { id: 4, category: "economy", title: '경제 이슈 B', description: '경제 이슈 B에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 18, date: '2024-03-04' },
            { id: 3, category: "economy", title: '경제 이슈 A', description: '경제 이슈 A에 대한 내용입니다.', likes: 30, date: '2024-03-03' },
            { id: 4, category: "economy", title: '경제 이슈 B', description: '경제 이슈 B에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 18, date: '2024-03-04' },

            // 추가 카드...
        ]
    },
    {
        id: 'ethics',
        label: '윤리/법',
        columns: [
            { id: 5, category: "ethics", title: '윤리 이슈 A', description: '윤리 이슈 A에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 12, date: '2024-03-05' },
            { id: 6, category: "ethics", title: '윤리 이슈 B', description: '윤리 이슈 B에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 25, date: '2024-03-06' }
            // 추가 카드...
        ]
    },
    {
        id: 'science',
        label: '과학/기술',
        columns: [
            { id: 7, category: "science", title: '과학 이슈 A', description: '과학 이슈 A에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 40, date: '2024-03-07' },
            { id: 8, category: "science", title: '과학 이슈 B', description: '과학 이슈 B에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 35, date: '2024-03-08' }
            // 추가 카드...
        ]
    },
    {
        id: 'education',
        label: '교육/사회',
        columns: [
            { id: 9, category: "education", title: '교육 이슈 A', description: '교육 이슈 A에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 10, date: '2024-03-09' },
            { id: 10, category: "education", title: '교육 이슈 B', description: '교육 이슈 B에 대한 내용입니다.', imageUrl: '/api/placeholder/400/200', likes: 20, date: '2024-03-10' }
            // 추가 카드...
        ]
    }
])

// '더보기' 버튼 클릭 시 실행할 함수 (필요에 따라 라우터 이동 등을 구현)
function seeMore(category) {
    router.push({ name: 'ColumnPageSearch', query: { category: category.id } });
}
// 카드 이동할때 오른쪽 왼쪽으로 움직이는 함수
function scrollLeft(event) {
    const container = event.currentTarget.parentElement.querySelector('.columns-grid')
    container.scrollBy({ left: -320, behavior: 'smooth' })
}

function scrollRight(event) {
    const container = event.currentTarget.parentElement.querySelector('.columns-grid')
    container.scrollBy({ left: 320, behavior: 'smooth' })
}
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