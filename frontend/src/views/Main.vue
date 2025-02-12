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

    <!-- 예정된 토론 대회 -->
    <div class="card">
      <h2>예정된 토론 대회</h2>
      <div class="grid-container">
        <div class="discussion-card">
          <h3>토론 대회1</h3>
          <p>주제: 인공지능 윤리</p>
          <p>날짜: 2025.02.15</p>
          <p>참가자: 4명</p>
        </div>
        <div class="discussion-card">
          <h3>토론 대회2</h3>
          <p>주제: 기후변화 대응</p>
          <p>날짜: 2025.02.20</p>
          <p>참가자: 6명</p>
        </div>
      </div>
    </div>

  </main>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from "vue";
import Chart from "chart.js/auto";

const isLoggedIn = ref(false);
const radarChart = ref(null);
const progressChart = ref(null);
let radarChartInstance = null;
let progressChartInstance = null;

const renderCharts = async () => {
  await nextTick();

  if (radarChart.value && progressChart.value) {
    // 기존 차트 삭제 (메모리 누수 방지)
    if (radarChartInstance) radarChartInstance.destroy();
    if (progressChartInstance) progressChartInstance.destroy();

    radarChartInstance = new Chart(radarChart.value, {
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
        plugins: { legend: { display: false } },
      },
    });

    progressChartInstance = new Chart(progressChart.value, {
      type: "line",
      data: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월"],
        datasets: [{ label: "종합 점수", data: [65, 70, 75, 80, 85, 88] }],
      },
    });
  }
};

onMounted(renderCharts);
watch(isLoggedIn, (newVal) => {
  if (newVal) renderCharts();
});

const handleLogin = () => {
  isLoggedIn.value = !isLoggedIn.value;
};

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
</style>