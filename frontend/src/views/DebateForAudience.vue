<template>
  <div class="debate-container">
    <!-- Header -->
    <header class="header">
      <div class="header-content">
        <div class="round-badge">1라운드</div>
        <h1 class="debate-title">
          인공지능 기술이 일자리를 대체하는 것은 불가피한가?
        </h1>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Left Column -->
      <div class="left-column">
        <!-- Video Area -->
        <div class="video-area">
          <div class="video-player">
            <span class="video-placeholder">실시간 토론 영상</span>
          </div>
          <div class="current-speaker">
            <div class="speaker-badge agree">찬성</div>
            <div class="speaker-info">
              <div class="speaker-name">김토론 (찬성 1)</div>
              <div class="speaker-status">
                첫 번째 주장 • 입론 진행 중 • 2:45 남음
              </div>
            </div>
          </div>
        </div>

        <!-- Timeline Section -->
        <div class="timeline-section">
          <div class="timeline-header">
            <h2>토론 진행 상황</h2>
            <p>클릭하여 각 발언 내용을 확인할 수 있습니다</p>
          </div>

          <!-- Timeline and Summary Container -->
          <div class="timeline-summary-container">
            <!-- Argument Selection Tabs - 위치 변경 -->
            <div class="timeline-column">
              <div class="argument-tabs">
                <button
                  :class="[
                    'argument-tab',
                    { active: currentArgument === 'first' },
                  ]"
                  @click="handleArgumentChange('first')"
                >
                  첫 번째 주장
                </button>
                <button
                  :class="[
                    'argument-tab',
                    { active: currentArgument === 'second' },
                  ]"
                  @click="handleArgumentChange('second')"
                >
                  두 번째 주장
                </button>
              </div>

              <!-- Timeline -->
              <div class="argument-timeline">
                <div class="timeline">
                  <div class="timeline-track">
                    <div class="timeline-progress"></div>
                  </div>
                  <div class="timeline-points">
                    <div
                      v-for="(point, id) in [
                        'agree1',
                        'disagree1',
                        'agree2',
                        'disagree2',
                      ]"
                      :key="id"
                      :class="[
                        'point',
                        {
                          done: id < point.indexOf(currentPoint),
                          current: id === point.indexOf(currentPoint),
                        },
                      ]"
                      @click="handleTimelineClick(point)"
                      style="cursor: pointer"
                    >
                      <div class="point-marker"></div>
                      <div class="point-label">
                        {{ timelineData[point].speaker }}
                      </div>
                      <div class="point-time">
                        {{ timelineData[point].time }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- Summary Column - 위치 변경 -->
            <!-- Summary Column -->
            <div class="summary-column">
              <div class="speech-summary">
                <div class="summary-header">
                  <div class="summary-badge">
                    {{ timelineData[currentPoint].speaker }}
                  </div>
                  <span class="summary-time">{{
                    timelineData[currentPoint].time
                  }}</span>
                </div>
                <p class="summary-content">
                  {{ timelineData[currentPoint].content }}
                </p>
                <div class="summary-tags">
                  <span
                    v-for="(tag, i) in timelineData[currentPoint].tags"
                    :key="i"
                    class="tag"
                  >
                    {{ tag }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Analysis Panel -->
      <div class="analysis-panel">
        <div class="analysis-tabs">
          <button
            :class="['tab', { active: currentTab === 'analysis' }]"
            @click="handleTabChange('analysis')"
          >
            분석 노트
          </button>
          <button
            :class="['tab', { active: currentTab === 'template' }]"
            @click="handleTabChange('template')"
          >
            분석 템플릿
          </button>
        </div>

        <!-- Quick Input -->
        <div class="quick-input">
          <h3>빠른 메모</h3>
          <textarea
            v-model="quickNote"
            placeholder="발언에 대한 생각을 자유롭게 기록해보세요"
          ></textarea>
          <button class="submit-button" @click="handleQuickNoteSubmit">
            작성
          </button>
        </div>

        <!-- Saved Notes -->
        <div class="saved-notes">
          <template v-if="currentTab === 'analysis'">
            <div
              v-for="(note, index) in mockTemplates.analysis"
              :key="index"
              :class="['note', note.type]"
            >
              <div class="note-header">
                <span class="note-badge"
                  >{{ note.type.toUpperCase() }} 분석</span
                >
                <span class="note-time"
                  >• {{ new Date().toLocaleTimeString() }}</span
                >
              </div>
              <p class="note-content">{{ note.content }}</p>
            </div>
          </template>
          <template v-else>
            <div
              v-for="(template, index) in mockTemplates.template"
              :key="index"
              :class="['note', template.type]"
            >
              <div class="note-header">
                <span class="note-badge">{{
                  template.type.toUpperCase()
                }}</span>
              </div>
              <p class="note-content">{{ template.content }}</p>
            </div>
          </template>
        </div>
      </div>
    </main>
  </div>
</template>
// 템플릿 script 섹션에 추가할 코드
<script setup>
import { ref } from "vue";

// Mock 데이터에 타임라인 데이터 추가
const timelineData = {
  agree1: {
    speaker: "찬성 1",
    time: "5:00",
    content:
      "AI 기술의 발전 속도와 비용 효율성을 고려할 때 일자리 대체는 불가피함",
    tags: ["통계 자료", "산업 사례"],
  },
  disagree1: {
    speaker: "반대 1",
    time: "5:00",
    content: "AI는 보완재로서 역할을 할 뿐, 완전한 대체는 불가능함",
    tags: ["역사적 사례", "전문가 의견"],
  },
  agree2: {
    speaker: "찬성 2",
    time: "5:00",
    content: "단순 반복적인 업무부터 점진적 대체가 진행 중",
    tags: ["현장 사례", "연구 결과"],
  },
  disagree2: {
    speaker: "반대 2",
    time: "5:00",
    content: "새로운 일자리 창출 효과가 더 클 것",
    tags: ["통계 예측", "전문가 분석"],
  },
};

// 현재 선택된 타임라인 포인트 상태 추가
const currentPoint = ref("agree1");

// 타임라인 포인트 클릭 핸들러 추가
const handleTimelineClick = (point) => {
  currentPoint.value = point;
};

const mockTemplates = {
  analysis: [
    { type: "prep", content: "PREP 분석 노트 내용" },
    { type: "logic", content: "논리성 분석 노트 내용" },
    { type: "quick", content: "빠른 분석 노트 내용" },
  ],
  template: [
    { type: "structure", content: "구조화 템플릿" },
    { type: "evidence", content: "증거 분석 템플릿" },
    { type: "counter", content: "반론 구성 템플릿" },
  ],
};

// 상태 관리
const currentArgument = ref("first");
const currentTab = ref("analysis");
const quickNote = ref("");
const savedNotes = ref([]);

// 이벤트 핸들러
const handleArgumentChange = (argument) => {
  currentArgument.value = argument;
};

const handleTabChange = (tab) => {
  currentTab.value = tab;
};

const handleQuickNoteSubmit = () => {
  if (quickNote.value.trim()) {
    const newNote = {
      type: "quick",
      content: quickNote.value,
      time: new Date().toLocaleTimeString(),
    };
    savedNotes.value = [newNote, ...savedNotes.value];
    quickNote.value = "";
    // TODO: API 연동 시 서버로 전송하는 로직 추가
  }
};
</script>

<style scoped>
.point {
  cursor: pointer;
}

.point:hover .point-marker {
  background-color: #ef4444;
  transform: scale(1.2);
  transition: all 0.2s ease;
}
/* Quick Input 버튼 스타일 */
.submit-button {
  margin-top: 8px;
  padding: 6px 12px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-family: inherit;
}

.submit-button:hover {
  background-color: #4338ca;
}
/* Layout & Container Styles */
.debate-container {
  width: 100%;
  height: 900px;
  background-color: #f9fafb;
}

.main-content {
  display: flex;
}

.left-column {
  width: 960px;
}

/* Header Styles */
.header {
  height: 72px;
  background-color: white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.header-content {
  padding: 20px 120px;
  display: flex;
  align-items: center;
  gap: 40px;
}

.round-badge {
  width: 80px;
  height: 32px;
  background-color: #ff4444;
  border-radius: 16px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
}

.debate-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

/* Video Area Styles */
.video-area {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.video-player {
  height: 380px;
  background-color: #111827;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-placeholder {
  color: white;
  font-size: 16px;
}

/* Current Speaker Styles */
.current-speaker {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.speaker-badge {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.speaker-badge.agree {
  background-color: #fee2e2;
  color: #ef4444;
}

.speaker-info {
  display: flex;
  flex-direction: column;
}

.speaker-name {
  font-size: 14px;
  font-weight: 500;
  color: #111827;
}

.speaker-status {
  font-size: 12px;
  color: #6b7280;
}

/* Timeline Section Styles */
.timeline-section {
  background-color: none;
  padding: 24px;
}

.timeline-header h2 {
  font-size: 16px;
  font-weight: 500;
  color: #111827;
}

.timeline-header p {
  font-size: 13px;
  color: #6b7280;
}

/* Argument Tab Styles */
.argument-tab {
  padding: 8px 20px;
  border-radius: 20px;
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid #e5e7eb;
  background-color: white;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
}

.argument-tab.active {
  font-family: inherit;
  background-color: #ef4444;
  color: white;
  border-color: #ef4444;
}

.timeline-summary-container {
  display: flex;
  gap: 40px;
  padding: 14px;
  height: 200px;
}

.timeline-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.argument-tabs {
  display: flex;
  gap: 12px;
}

/* summary 관련 스타일은 이전 답변과 동일 */

.timeline {
  position: relative;
}

.timeline-track {
  height: 4px;
  background-color: #f3f4f6;
  border-radius: 2px;
  width: 520px;
}

.timeline-progress {
  height: 100%;
  width: 130px;
  background-color: #ef4444;
  border-radius: 2px;
}

.timeline-points {
  display: flex;
  justify-content: space-between;
  width: 520px;
  position: absolute;
  top: 16px;
}

/* Timeline Point Styles */
.point {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.point-marker {
  width: 12px;
  height: 12px;
  border-radius: 6px;
  background-color: #e5e7eb;
}

.point.done .point-marker,
.point.current .point-marker {
  background-color: #ef4444;
}

.point-label {
  font-size: 12px;
  color: #6b7280;
  margin-top: 8px;
}

.point-time {
  font-size: 11px;
  color: #6b7280;
}

.summary-column {
  width: 360px;
  padding-bottom: 10px;
}

.speech-summary {
  margin-top: 0;
  height: 100%; /* 높이 100% 설정 */
  background-color: #fef2f2;
  border-radius: 8px;
  padding: 20px;
  overflow-y: auto; /* 내용이 넘칠 경우 스크롤 표시 */
  display: flex;
  flex-direction: column;
}

.summary-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.summary-badge {
  background-color: #ef4444;
  color: white;
  padding: 4px 16px;
  border-radius: 12px;
  font-size: 12px;
}

.summary-time {
  font-size: 12px;
  color: #6b7280;
}

.summary-content {
  font-size: 14px;
  font-weight: 500;
  color: #111827;
  flex: 1; /* 남은 공간 모두 차지 */
  overflow-y: auto; /* 내용이 넘칠 경우 스크롤 */
  margin-bottom: 12px;
}

.summary-tags {
  display: flex;
  gap: 8px;
}

.tag {
  background-color: white;
  padding: 4px 16px;
  border-radius: 12px;
  font-size: 12px;
  color: #6b7280;
}

/* Analysis Panel Styles */
.analysis-panel {
  width: 320px;
  height: 870px;
  background-color: white;
  padding-left: 20px;
  padding-right: 20px;
}

.analysis-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.tab {
  width: 136px;
  height: 32px;
  border-radius: 8px;
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  border: none;
  background-color: #f3f4f6;
  color: #6b7280;
  cursor: pointer;
}

.tab.active {
  font-family: inherit;
  background-color: #eef2ff;
  color: #4f46e5;
}

/* Quick Input Styles */
.quick-input {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.quick-input h3 {
  font-size: 13px;
  font-weight: 500;
  color: #111827;
  margin-bottom: 12px;
}

.quick-input textarea {
  font-family: inherit;
  width: 100%;
  height: 48px;
  border-radius: 6px;
  border: none;
  font-size: 12px;
  color: #9ca3af;
  resize: none;
}

/* Saved Notes Styles */
.saved-notes {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.note {
  padding: 16px;
  border-radius: 8px;
}

.note.prep {
  background-color: #eef2ff;
}

.note.logic {
  background-color: #fef2f2;
}

.note.quick {
  background-color: #ecfdf5;
}

.note-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.note-badge {
  padding: 4px 12px;
  border-radius: 10px;
  font-size: 11px;
  color: white;
}

.note.prep .note-badge {
  background-color: #818cf8;
}

.note.logic .note-badge {
  background-color: #ef4444;
}

.note-time {
  font-size: 11px;
  color: #6b7280;
}

.note-content {
  font-size: 13px;
}

.note.prep .note-content,
.note.logic .note-content {
  color: #111827;
  font-weight: 500;
}

.note.quick .note-content {
  color: #065f46;
}
</style>
