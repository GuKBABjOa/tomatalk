<template>
  <main class="debate-dashboard">
    <!-- 첫 방문자 안내 -->
    <div class="first-visit-banner" v-if="!hideFirstVisitBanner">
      <div class="banner-content">
        <img :src="getImageUrl('Tori.svg')" alt="토리 이미지" class="tori-avatar-small" />
        <p>토리와 함께하는 AI 토론이 처음이신가요?</p>
        <button class="preview-button-small" @click="showPreviewModal = true">
          사용 방법 보기
        </button>
      </div>
    </div>

    <!-- 난이도 선택 탭 -->
    <div class="level-tabs">
      <button class="level-tab" :class="{ active: selectedLevel === 1 }" @click="selectedLevel = 1">
        <div class="tab-badge level-1">1</div>
        기초 다지기
      </button>
      <button class="level-tab" :class="{ active: selectedLevel === 2 }" @click="selectedLevel = 2">
        <div class="tab-badge level-2">2</div>
        실전 토론하기
      </button>
    </div>

    <!-- 선택된 난이도 설명 -->
    <div class="level-description" v-if="selectedLevel === 1">
      <h3>기초 다지기</h3>
      <p>토리의 질문에 답하며 논리적 사고력을 키워요</p>
      <p class="time">소요 시간: 5분</p>
    </div>
    <div class="level-description" v-if="selectedLevel === 2">
      <h3>실전 토론하기</h3>
      <p>토리와 서로 다른 입장에서 토론을 진행해요</p>
      <p class="time">소요 시간: 5분</p>
    </div>

    <!-- 토론 주제 리스트 -->
    <section class="topics-section">
      <div class="topic-list">
        <div v-for="topic in filteredTopics" :key="topic.id" class="topic-card">
          <div class="topic-content">
            <h3>{{ topic.title }}</h3>
          </div>
          <button class="prepare-button" :class="{ locked: selectedLevel === 2 }" @click="handlePrepareClick(topic)">
            준비하기
          </button>
        </div>
      </div>
    </section>

    <!-- 모달 컴포넌트들 -->
    <PreviewModal v-if="showPreviewModal" @close="showPreviewModal = false" />
    <PrepareModal v-if="showPrepareModal" :topic="selectedTopic" @close="showPrepareModal = false"
      @start="startDebate" />
    <DebateModal v-if="showDebateModal" :topic="selectedTopic" :stance="selectedStance"
      @close="showDebateModal = false" />
  </main>
</template>

<script setup>
import { ref, computed } from "vue";
import PreviewModal from "@/components/PreviewModal.vue";
import PrepareModal from "@/components/PrepareModal.vue";
import DebateModal from "@/components/DebateModal.vue";

const showPreviewModal = ref(false);
const showPrepareModal = ref(false);
const showDebateModal = ref(false);
const selectedTopic = ref(null);
const selectedStance = ref("");
const selectedLevel = ref(1);
const hideFirstVisitBanner = ref(false);

const getImageUrl = (filename) => {
  return new URL(`../assets/${filename}`, import.meta.url).href;
};

const startDebate = (topic, stance) => {
  selectedTopic.value = topics.value.find((t) => t.id === topic);
  selectedStance.value = stance;
  showDebateModal.value = true;
};

const topics = ref([
  {
    id: 1,
    level: 1,
    title: "교복 자율화, 찬성? 반대?",
    participants: 12,
    mainPoints: [
      "학생 개성 표현의 자유",
      "학교 소속감과 정체성",
      "경제적 영향과 비용",
    ],
    references: ["2023년 학생 복지 만족도 조사", "교복 자율화 시행 학교 사례"],
  },
  {
    id: 2,
    level: 1,
    title: "급식 메뉴 선택제 도입, 어떻게 생각하시나요?",
    participants: 8,
    mainPoints: ["학생 선호도 반영", "영양 균형 고려", "급식 운영의 효율성"],
    references: ["학교 급식 만족도 조사", "선택식 급식 운영 사례"],
  },
  {
    id: 3,
    level: 2,
    title: "학원 자율 등원제, 도입해야 할까요?",
    participants: 15,
    mainPoints: ["학습 효율성", "학생 자기주도성", "사교육 영향"],
    references: ["자율 등원제 시행 효과 연구", "학생 스트레스 관련 조사"],
  },
  {
    id: 4,
    level: 2,
    title: "디지털 교과서 전면 도입, 찬성하시나요?",
    participants: 10,
    mainPoints: ["학습 효과성", "환경적 영향", "디지털 격차"],
    references: ["디지털 교과서 시범 학교 결과", "학생 건강 영향 연구"],
  },
  {
    id: 5,
    level: 2,
    title: "방과후 학교 의무화, 어떻게 보시나요?",
    participants: 13,
    mainPoints: ["교육 기회 평등", "학생 자유시간", "학교 운영 부담"],
    references: ["방과후 학교 효과성 분석", "학부모 만족도 조사"],
  },
]);

const filteredTopics = computed(() => {
  return topics.value.filter((topic) => topic.level === selectedLevel.value);
});

const openPrepareModal = (topic) => {
  selectedTopic.value = topic;
  showPrepareModal.value = true;
};

const handlePrepareClick = (topic) => {
  if (selectedLevel.value === 1) {
    openPrepareModal(topic);
  } else {
    alert("아직 준비중 입니다.");
  }
};
</script>

<style scoped>
.debate-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.first-visit-banner {
  background-color: #fff1f1;
  border-radius: 12px;
  padding: 16px 24px;
  margin-bottom: 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.tori-avatar-small {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.preview-button-small {
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 16px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.level-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.level-tab {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.level-tab.active {
  border-color: #ff6b6b;
  background-color: #fff1f1;
}

.tab-badge {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.tab-badge.level-1 {
  background-color: #ff6b6b;
}

.tab-badge.level-2 {
  background-color: #ffd43b;
}

.level-description {
  background-color: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 32px;
}

.level-description h3 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
}

.level-description p {
  color: #6b7280;
  font-size: 14px;
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.topic-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: white;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.topic-content h3 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
}

.topic-content p {
  font-size: 14px;
  color: #6b7280;
}

.prepare-button {
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 12px 32px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.prepare-button:hover {
  background-color: #ff5252;
}

.prepare-button.locked {
  background: #f8f9fa;
  color: #6b7280;
  cursor: not-allowed;
}
</style>
