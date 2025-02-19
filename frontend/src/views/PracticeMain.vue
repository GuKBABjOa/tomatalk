<template>
  <div class="debate-practice">
    <!-- Header with Progress Overview -->
    <div class="header-container">
      <div class="header-content">
        <div class="header-top">
          <div class="header-title">
            <h1>토론 연습하기</h1>
            <p>기초부터 심화까지, 체계적인 토론 학습</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Learning Path -->
    <div class="learning-container">
      <div class="learning-content">
        <!-- Tab Navigation -->
        <div class="tab-navigation">
          <button @click="activeTab = 'theory'" :class="['tab-button', { active: activeTab === 'theory' }]">
            <GraduationCap :size="20" />
            기초 이론
          </button>
          <button @click="activeTab = 'ai'" :class="['tab-button', { active: activeTab === 'ai' }]">
            <Sword :size="20" />
            실전 연습
          </button>
        </div>

        <!-- Theory Content -->
        <div v-if="activeTab === 'theory'" class="theory-content">
          <!-- 기초 단계 -->
          <div class="stage">
            <div class="stage-header">
              <h3>1단계: 토론의 이해</h3>
              <p>토론의 기본 개념과 요소 학습</p>
            </div>
            <div class="modules">
              <ModuleItem title="토론의 기본 이해" description="토론의 정의, 목적, 유형, 규칙 학습" :progress="0" :completed="false"
                :locked="true" />
              <ModuleItem title="논리적 사고의 기초" description="연역법, 귀납법, 논리적 오류 이해하기" :progress="0" :completed="false"
                :locked="true" />
            </div>
          </div>

          <!-- 표현 단계 -->
          <div class="stage">
            <div class="stage-header">
              <h3>2단계: 토론 표현력</h3>
              <p>효과적인 주장과 설득 기술 습득</p>
            </div>
            <div class="modules">
              <ModuleItem title="주장과 근거" description="PREP/CEDA 기법을 활용한 논리 구성" :progress="0" :completed="false" />
              <ModuleItem title="설득력 있는 말하기" description="톤, 속도, 제스처 등 비언어적 표현" :progress="0" :completed="false"
                :locked="true" />
            </div>
          </div>

          <!-- 분석 단계 -->
          <div class="stage">
            <div class="stage-header">
              <h3>3단계: 분석과 대응</h3>
              <p>상대방 주장 분석과 효과적인 반론</p>
            </div>
            <div class="modules">
              <ModuleItem title="주장 분석하기" description="상대방 논리구조 파악과 약점 찾기" :progress="0" :completed="false"
                :locked="true" />
              <ModuleItem title="반론과 재반론" description="효과적인 반론 구성과 즉각 대응" :progress="0" :completed="false"
                :locked="true" />
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'ai'">
          <AIDebate />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Sword, GraduationCap } from "lucide-vue-next";
import ModuleItem from "@/components/ModuleItem.vue";
import AIDebate from "@/views/AIDebate.vue";

// 초기값을 'theory'로 설정하여 처음 진입시 theory 탭이 보이도록 함
const activeTab = ref < "theory" | "ai" > ("theory");
</script>

<style scoped>
.debate-practice {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header-container {
  margin-bottom: 32px;
}

.header-content {
  background-color: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-title h1 {
  font-size: 24px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
}

.header-title p {
  color: #6b7280;
  font-size: 14px;
}

.learning-container {
  max-width: 1200px;
  margin: 0 auto;
}

.learning-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e5e7eb;
}

.tab-navigation {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.tab-button {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-button:hover {
  border-color: #ff6b6b;
  background-color: #fff1f1;
}

.tab-button.active {
  background-color: #ff6b6b;
  border-color: #ff6b6b;
  color: white;
}

.theory-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.stage {
  background-color: white;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.stage-header {
  margin-bottom: 16px;
}

.stage-header h3 {
  font-size: 18px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 8px;
}

.stage-header p {
  font-size: 14px;
  color: #6b7280;
}

.modules {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
