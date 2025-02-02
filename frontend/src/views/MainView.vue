<template>
  <nav class="category-header custom-nav">
    <div class="custom-nav-inner">
      <ul class="category-list">
        <li
          v-for="category in categories"
          :key="category.id"
          @click="selectCategory(category)"
          class="category-item custom-link"
          :class="{ 'active-category': selectedCategory.id === category.id }"
        >
          <span class="category-text">{{ category.name }}</span>
          <div class="underline" :class="{ 'active-underline': selectedCategory.id === category.id }"></div>
        </li>
      </ul>
    </div>
  </nav>

  <!-- 선택된 카테고리에 해당하는 리스트 출력 -->
  <section class="category-content">
    <ul class="category-items">
      <li v-for="item in categoryItems" :key="item.id" class="category-item-box">
        <router-link 
          :to="{ name: 'ItemDetail', params: { id: item.id }}" 
          class="item-title"
        >
          {{ item.title }}
        </router-link>
        <div class="item-meta">
          <span class="icon">👍 {{ item.likeCount }}</span>
          <span class="icon">👁️ {{ item.viewCount }}</span>
          <span class="icon">💬 {{ item.reviewCount }}</span>
        </div>
      </li>
    </ul>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useURL } from '@/utils/useURL'; // API URL 유틸

const categories = ref([]);
const selectedCategory = ref(null);
const categoryItems = ref([]);

// 카테고리 목록 불러오기
const fetchCategories = async () => {
  try {
    const response = await fetch('http://localhost:3000/categories');
    if (!response.ok) throw new Error('카테고리 가져오기 실패');
    const data = await response.json();
    categories.value = data;
    selectedCategory.value = categories.value[0] || null;
    if (selectedCategory.value) fetchCategoryItems(selectedCategory.value.id);
  } catch (error) {
    console.error('카테고리 가져오기 오류:', error);
  }
};

// 특정 카테고리의 아이템 불러오기
const fetchCategoryItems = async (categoryId) => {
  try {
    const response = await fetch(`http://localhost:3000/items?categoryId=${categoryId}`);
    if (!response.ok) throw new Error('카테고리 아이템 가져오기 실패');
    const data = await response.json();
    categoryItems.value = data;
  } catch (error) {
    console.error('카테고리 아이템 가져오기 오류:', error);
  }
};

const selectCategory = (category) => {
  selectedCategory.value = category;
  fetchCategoryItems(category.id);
};

onMounted(fetchCategories);
</script>

<style scoped>
/* 전체 컨테이너 스타일 */
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 600px;
  margin: 0 auto;
  background: #fff;
}

/* 헤더 영역 스타일 */
.category-header {
  position: fixed;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 600px;
  height: 60px;
  box-sizing: border-box;
  padding: 0 60px;
  background-color: #ffffff;
  border-bottom: 0.2px solid rgb(230, 229, 229);
  z-index: 1000;
}

.category-list {
  display: flex;
  justify-content: space-between;
  list-style: none;
  padding-top: 20px;
  margin: 0;
}

.category-item {
  transition: transform 0.2s, background-color 0.2s;
  font-size: 18px;
  color: rgb(159, 159, 159);
  cursor: pointer;
  position: relative;
}

.active-category {
  color: black;
  font-weight: bold;
}

.underline {
  width: 150%;
  height: 2px;
  background-color: transparent;
  position: absolute;
  bottom: -12px;
  left: -10px;
  transition: background-color 0.2s ease-in-out;
}

.active-underline {
  background-color: black;
}

/* 컨텐츠 영역 스타일 */
.category-content {
  position: fixed;
  top: 60px;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 600px;
  overflow-y: auto;
  background: #fff;
  padding: 20px 0;
}

.category-items {
  list-style: none;
  padding: 0 20px; /* 좌우 패딩 추가 */
  margin: 0;
  width: 100%;
  box-sizing: border-box;
}

.category-item-box {
  padding: 15px 10px;
  width: 100%;
  box-sizing: border-box;
  border-bottom: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  background: #fff;
}

.item-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
  cursor: pointer;
  background-color: #ffffff;
}

.item-meta {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: gray;
}
</style>