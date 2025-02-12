<template>
  <div class="search-container">
    <div class="search-bar-container">
      <input v-model="localQuery" type="text" :placeholder="placeholder" class="search-bar" />
      <button class="search-btn" @click="handleSearch">검색</button>
    </div>

    <div class="filter-row">
      <!-- 정렬 드롭다운 -->
      <div class="sort-container" v-if="sortOptions.length > 0">
        <label for="sortOption">정렬 기준:</label>
        <select id="sortOption" v-model="sortOption" @change="updateSort">
          <option v-for="option in sortOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </div>

      <!-- 카테고리 칩 (여러 개 선택 가능) -->
      <div class="category-chips" v-if="categories && categories.length">
        <button v-for="category in categories" :key="category.value"
          :class="['chip', { active: selectedCategories.includes(category.value) }]"
          @click="toggleCategory(category.value)">
          {{ category.label }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, watch } from "vue";

const props = defineProps({
  query: String,
  sort: String,
  placeholder: {
    type: String,
    default: "토론 주제, 키워드로 검색..."
  },
  sortOptions: {
    type: Array,
    default: () => [
      { value: "latest", label: "최신순" },
      { value: "popular", label: "인기순" }
    ]
  },
  // 카테고리 옵션 (칩 형태로 표시)
  categories: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(["update:query", "update:sort", "update:category"]);

const localQuery = ref(props.query);
const sortOption = ref(props.sort);
// 멀티 선택을 위한 배열로 관리
const selectedCategories = ref([]);

const handleSearch = () => {
  emit("update:query", localQuery.value);
};

// 정렬 변경 시 부모에 반영
const updateSort = () => {
  emit("update:sort", sortOption.value);
};

// 카테고리 토글 (선택 또는 해제)
const toggleCategory = (value) => {
  if (selectedCategories.value.includes(value)) {
    // 이미 선택되어 있다면 제거
    selectedCategories.value = selectedCategories.value.filter(cat => cat !== value);
  } else {
    // 선택되지 않았다면 추가
    selectedCategories.value = [...selectedCategories.value, value];
  }
  // 부모로 선택된 카테고리 배열 전달
  emit("update:category", selectedCategories.value);
};

watch(() => props.query, (newVal) => {
  localQuery.value = newVal;
});

watch(() => props.sort, (newVal) => {
  sortOption.value = newVal;
});
</script>

<style scoped>
.search-container {
  background: white;
  padding: 1.5rem;
  border-radius: 1rem;
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.search-bar-container {
  display: flex;
  gap: 1rem;
}

.search-bar {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 0.5rem;
}

.search-btn {
  padding: 0.75rem 1rem;
  background: #2563eb;
  color: white;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #1d4ed8;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 1rem;
}

/* 정렬 옵션 스타일 */
.sort-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.sort-container label {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 600;
}

.sort-container select {
  padding: 0.5rem 0.75rem;
  border-radius: 0.5rem;
  border: 1px solid #d1d5db;
  background: white;
  font-size: 0.875rem;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.sort-container select:hover {
  border-color: #2563eb;
}

.sort-container select:focus {
  border-color: #2563eb;
  outline: none;
  box-shadow: 0 0 5px rgba(37, 99, 235, 0.5);
}

/* 카테고리 칩 스타일 */
.category-chips {
  display: flex;
  gap: 0.5rem;
}

.chip {
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 1rem;
  background: #f3f4f6;
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s;
}

.chip:hover {
  background: #e5e7eb;
}

.chip.active {
  background: #5b5bee;
  color: white;
  border-color: #5b5bee;
}
</style>
