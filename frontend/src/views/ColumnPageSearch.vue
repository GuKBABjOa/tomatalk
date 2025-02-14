<template>
    <div class="app">
        <main class="main-content">
            <h1 class="title">토론 아카이브</h1>
            <p class="subtitle">지난 토론들의 기록을 칼럼으로 확인하세요.</p>
            <!-- SearchBar: 검색어, 정렬, 카테고리 선택 -->
            <SearchBar v-model:query="searchQuery" :sortOption="sortOption" @update:sort="sortOption = $event"
                :initialSelectedCategories="initialSelectedCategories"
                @update:category="initialSelectedCategories = $event" placeholder="토론 주제, 키워드로 검색..." :sortOptions="[
                    { value: 'latest', label: '최신순' },
                    { value: 'popular', label: '인기순' }
                ]" :categories="[
                    { value: 'politics', label: '정치/국제' },
                    { value: 'economy', label: '경제/노동' },
                    { value: 'ethics', label: '윤리/법' },
                    { value: 'science', label: '과학/기술' },
                    { value: 'education', label: '교육/사회' }
                ]" />

            <!-- 칼럼 카드 목록 -->
            <div class="columns-grid">
                <ColumnCard v-for="(column, index) in columns" :key="column.columnId || index" v-bind="column" />
            </div>

            <!-- 무한 스크롤 감지를 위한 sentinel 요소 -->
            <div ref="sentinel"></div>

            <!-- 로딩 상태 표시 -->
            <p v-if="isFetchingMore">추가 칼럼을 불러오는 중...</p>
            <p v-if="!isLast && !isLoading && columns.length === 0">조건에 해당하는 칼럼이 없습니다.</p>
        </main>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import SearchBar from '@/components/SearchBar.vue';
import ColumnCard from '@/components/ColumnCard.vue';

// URL 쿼리나 초기 선택값으로부터 카테고리 설정 (예: ?category=politics)
const route = useRoute();
const selectedCategory = ref<string>('');

// 만약 URL에서 초기 선택 카테고리가 있다면, 이를 배열 형태로 저장 (백엔드는 복수의 카테고리를 콤마로 구분해서 받음)
const initialSelectedCategories = ref<string[]>([]);
onMounted(() => {
    if (route.query.category) {
        // 예: 'politics'를 받으면 배열로 저장 (백엔드는 대문자 값 기대 → 변환 시 필요)
        const cat = (route.query.category as string).toLowerCase();
        selectedCategory.value = cat;
        initialSelectedCategories.value = [cat];
    }
});

// 검색어와 정렬 옵션 상태
const searchQuery = ref('');
const sortOption = ref('latest');
// sortOption 매핑: 'latest' → 'LATEST', 'popular' → 'POPULARITY'
const sortMapping: Record<string, string> = {
    latest: "LATEST",
    popular: "POPULARITY"
};

// 칼럼 데이터 및 페이지네이션 관련 상태
const columns = ref<any[]>([]);
const isLoading = ref(true);
const isFetchingMore = ref(false);
const isLast = ref(false); // 마지막 페이지 여부 판단
let cursor = 9007199254740991; // 초기 커서 (백엔드 스펙에 따른 고정 값)

// API 호출 함수 (append가 true면 추가 데이터, 아니면 새로 불러옴)
async function fetchColumns(append: boolean = false) {
    try {
        if (append) {
            isFetchingMore.value = true;
        } else {
            isLoading.value = true;
            // 초기 요청이면 cursor 초기화
            cursor = 9007199254740991;
            isLast.value = false;
        }
        // 선택된 카테고리는 initialSelectedCategories 또는 selectedCategory (배열 또는 단일 값)로 처리
        // 여기서는 부모의 SearchBar에서 여러 개 선택할 수도 있으므로, initialSelectedCategories를 우선 사용
        let categoriesParam: string | undefined;
        if (initialSelectedCategories.value.length > 0) {
            categoriesParam = initialSelectedCategories.value
                .map(cat => cat.toUpperCase())
                .join(',');
        } else if (selectedCategory.value) {
            categoriesParam = selectedCategory.value.toUpperCase();
        }

        const response = await axios.get("http://localhost:8080/api/columns", {
            params: {
                cursor,
                size: 8,
                sortBy: sortMapping[sortOption.value] || undefined,
                categories: categoriesParam,
                query: searchQuery.value || undefined,
            },
        });

        // 예시 응답: content, first, last, 등 (여기서는 content와 last 만 사용)
        const fetchedColumns = response.data.content;
        if (append) {
            columns.value = columns.value.concat(fetchedColumns);
        } else {
            columns.value = fetchedColumns;
        }
        // 커서 업데이트: API에서 마지막 데이터의 columnId를 이용해 커서를 갱신하는 등 백엔드 스펙에 맞게 처리
        if (fetchedColumns && fetchedColumns.length > 0) {
            // 예시: 마지막 항목의 columnId를 숫자로 변환하여 cursor로 사용
            cursor = Number(fetchedColumns[fetchedColumns.length - 1].columnId);
        }
        // 마지막 페이지 여부: 백엔드가 last 플래그를 제공한다면 사용 (여기서는 response.data.last)
        isLast.value = response.data.last || false;
    } catch (error) {
        console.error("Error fetching columns:", error);
        // 에러 발생 시 빈 배열로 처리
        if (!append) columns.value = [];
    } finally {
        isLoading.value = false;
        isFetchingMore.value = false;
    }
}

// 무한 스크롤을 위한 IntersectionObserver 설정
const sentinel = ref<HTMLElement | null>(null);
let observer: IntersectionObserver | null = null;
function createObserver() {
    if (sentinel.value) {
        observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting && !isLoading.value && !isFetchingMore.value && !isLast.value) {
                    // 스크롤 시 추가 데이터 요청
                    fetchColumns(true);
                }
            });
        }, { threshold: 1.0 });
        observer.observe(sentinel.value);
    }
}

onMounted(() => {
    fetchColumns();
    createObserver();
});

onBeforeUnmount(() => {
    if (observer && sentinel.value) {
        observer.unobserve(sentinel.value);
    }
});

// 검색어나 정렬 옵션, 또는 선택된 카테고리 변경 시 새로 데이터를 불러옴
watch([searchQuery, sortOption, selectedCategory, initialSelectedCategories], () => {
    fetchColumns();
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
    flex: 0 0 calc((100% - 4.5rem) / 4);
}
</style>