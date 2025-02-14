<template>
  <main class="main-content">
    <section v-if="!isLoggedIn" class="hero-section">
      <div class="hero-card">
        <img src="@/assets/debate-background.svg" alt="배경" class="background-image"
          style="position: absolute; width: 100%; height: 100%; object-fit: cover; top: 0; left: 0;">
        <div class="hero-text">
          <h1 class="hero-title">논리적 사고와 설득력을 키우는<br>최고의 토론 학습 플랫폼</h1>
          <p class="hero-subtitle">지금 바로 토론의 즐거움을 경험해보세요</p>
        </div>
      </div>
    </section>

    <div class="grid-container">
      <template v-if="isLoggedIn">
        <!-- 기존 로그인 후 UI -->
        <div class="card">
          <h2>세부 능력 평가</h2>
          <div class="chart-container">
            <canvas ref="radarChart"></canvas>
          </div>
        </div>

        <div class="card">
          <h2>종합 점수</h2>
          <div class="chart-container">
            <canvas ref="progressChart"></canvas>
          </div>
        </div>
      </template>
    </div>

    <!-- 진행중인 토론 섹션 (더미 데이터 사용) -->
    <section class="content-section">
      <h2 class="section-title">현재 진행중인 토론</h2>
      <div class="debate-list">
        <!-- DebateCard는 단일 debate prop으로 전달 -->
        <DebateCard v-for="(debate, index) in debateDummy" :key="debate.debateId || index" :debate="debate" />
      </div>
    </section>

    <!-- 최신 칼럼 섹션 -->
    <section class="content-section">
      <h2 class="section-title">최신 칼럼</h2>
      <div class="columns-grid">
        <!-- ColumnCard에 필요한 prop명을 매핑한 후 전달 -->
        <ColumnCard v-for="(column, index) in formattedColumnDummy" :key="column.columnId || index" v-bind="column" />
      </div>
    </section>

  </main>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, computed, onBeforeUnmount } from "vue";
import Chart from "chart.js/auto";
import DebateCard from '@/components/DebateCard.vue'
import ColumnCard from '@/components/ColumnCard.vue'


const isLoggedIn = ref(false);

onMounted(() => {
  const token = localStorage.getItem("token");
  isLoggedIn.value = !!token;
});

const radarChart = ref(null);
const progressChart = ref(null);
let radarChartInstance = null;
let progressChartInstance = null;

const renderCharts = async () => {
  await nextTick();

  // getContext를 사용하여 canvas context 가져오기
  const radarCtx = radarChart.value?.getContext('2d');
  const progressCtx = progressChart.value?.getContext('2d');

  // context가 없으면 렌더링하지 않음
  if (!radarCtx || !progressCtx) return;

  // 기존 차트 인스턴스 정리
  if (radarChartInstance) {
    radarChartInstance.destroy();
    radarChartInstance = null;
  }
  if (progressChartInstance) {
    progressChartInstance.destroy();
    progressChartInstance = null;
  }

  // Radar 차트 설정
  radarChartInstance = new Chart(radarCtx, {
    type: "radar",
    data: {
      labels: ["논리성", "창의성", "의사소통", "문제해결력", "대응력"],
      datasets: [
        {
          label: "능력치",
          data: [85, 70, 90, 75, 80],
          backgroundColor: "rgba(37, 99, 235, 0.2)",
          borderColor: "rgba(37, 99, 235, 0.8)",
          borderWidth: 2,
          pointBackgroundColor: "rgba(37, 99, 235, 1)",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        r: {
          beginAtZero: true,
          max: 100,
          ticks: { stepSize: 20 },
        },
      },
      plugins: {
        legend: { display: false },
      },
    },
  });

  // Progress 차트 설정
  progressChartInstance = new Chart(progressCtx, {
    type: "line",
    data: {
      labels: ["1월", "2월", "3월", "4월", "5월", "6월"],
      datasets: [
        {
          label: "종합 점수",
          data: [65, 70, 75, 80, 85, 88],
          borderColor: "rgba(37, 99, 235, 0.8)",
          backgroundColor: "rgba(37, 99, 235, 0.2)",
          tension: 0.4,
        }
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          beginAtZero: true,
          max: 100,
        }
      }
    }
  });
};

watch(isLoggedIn, async (newVal) => {
  if (newVal) {
    await nextTick();
    renderCharts();
  }
}, { flush: 'post' });
// 컴포넌트 unmount 시 차트 인스턴스 정리
onBeforeUnmount(() => {
  if (radarChartInstance) {
    radarChartInstance.destroy();
  }
  if (progressChartInstance) {
    progressChartInstance.destroy();
  }
});
// 더미 데이터: 진행중인 토론 (최신 3개)
const debateDummy = ref([
  {
    debateId: "1",
    title: "토론 제목 1",
    participants: 20,
    duration: 15, // 분 단위
    startTime: "10:00",
    endTime: "10:30"
  },
  {
    debateId: "2",
    title: "토론 제목 2",
    participants: 35,
    duration: 20,
    startTime: "11:00",
    endTime: "11:30"
  },
  {
    debateId: "3",
    title: "토론 제목 3",
    participants: 15,
    duration: 10,
    startTime: "12:00",
    endTime: "12:20"
  }
]);

// 더미 데이터: 최신 칼럼 (최신 4개)
// API 응답과 동일한 필드명: columnId, title, summary, bookmarkCount, createdAt, category
const columnDummy = ref([
  {
    columnId: "101",
    title: "칼럼 제목 1",
    summary: "칼럼 내용 1에 대한 요약 텍스트입니다. 이 내용은 실제로는 조금 더 길 수도 있습니다.",
    bookmarkCount: 120,
    createdAt: "2025-02-12T12:40:38.0382165",
    category: "POLITICS"
  },
  {
    columnId: "102",
    title: "칼럼 제목 2",
    summary: "칼럼 내용 2에 대한 요약 텍스트입니다. 이 내용은 실제 칼럼의 일부분을 보여줍니다.",
    bookmarkCount: 80,
    createdAt: "2025-02-11T11:30:20.1234567",
    category: "ECONOMY"
  },
  {
    columnId: "103",
    title: "칼럼 제목 3",
    summary: "칼럼 내용 3에 대한 요약 텍스트입니다. 긴 텍스트의 일부만 표시하도록 자를 수 있습니다.",
    bookmarkCount: 95,
    createdAt: "2025-02-10T10:20:15.6543210",
    category: "SCIENCE"
  },
  {
    columnId: "104",
    title: "칼럼 제목 4",
    summary: "칼럼 내용 4에 대한 요약 텍스트입니다. 독자가 제목만 보고도 내용을 유추할 수 있도록 합니다.",
    bookmarkCount: 110,
    createdAt: "2025-02-09T09:10:05.9876543",
    category: "ETHICS"
  }
]);


const formattedColumnDummy = computed(() => {
  return columnDummy.value.map(column => ({
    columnId: column.columnId,
    title: column.title,
    // 올바른 prop명: summary
    summary: column.summary,
    // bookmarkCount로 그대로 전달
    bookmarkCount: column.bookmarkCount,
    // createdAt으로 그대로 전달
    createdAt: column.createdAt,
    // category는 그대로 전달 (소문자 처리)
    category: column.category.toLowerCase()
  }));
});

</script>

<style scoped>
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

.chart-container {
  height: 300px;
  width: 100%;
}

.discussion-card {
  background: #f8fafc;
  padding: 1rem;
  border-radius: 0.75rem;
}

.hero-section {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin-bottom: 2rem;
}

.hero-card {
  width: 100%;
  height: 500px;
  border-radius: 1rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
}

.hero-text {
  text-align: center;
  color: white;
  z-index: 1;
  max-width: 800px;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  line-height: 1.3;
  margin-bottom: 1.5rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.hero-subtitle {
  font-size: 1.5rem;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* Debate 카드와 Column 카드의 그리드 레이아웃 */
.debate-list {
  background: white;
  padding: 1.5rem;
  border-radius: 1rem;
  border: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  transition: all 0.3s ease-in-out;
}

.columns-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-top: 1.5rem;
}
</style>