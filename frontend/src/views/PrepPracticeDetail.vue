<template>
  <div class="max-w-3xl mx-auto p-4 min-h-screen">
    <div v-if="isLoading" class="flex items-center justify-center h-screen">
      <div class="text-2xl font-bold">로딩중...</div>
    </div>
    <!-- Header -->
    <div v-else>
      <div class="flex justify-between items-center mb-4 bg-gray-50 p-4">
        <h1 class="text-2xl font-medium text-gray-700">PREP 구조 작성 연습</h1>

        <!-- Progress Bar -->
        <StepProgressSlider :length="totalSteps" :activeStep="currentStep" />
      </div>

      <!-- Topic Card -->
      <div class="bg-white rounded-2xl p-4 shadow mb-4">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-gray-500 text-sm mb-2">토론 주제</p>
            <h2 class="text-xl text-gray-800 font-semibold">
              {{ currenttopic }}
            </h2>
          </div>
          <!-- Timer -->
          <div class="bg-red-500 rounded-full w-16 h-16 flex flex-col items-center justify-center text-white">
            <p class="font-semibold">{{ formattedTime }}</p>
            <p class="text-xs">남은 시간</p>
          </div>
        </div>
      </div>

      <!-- PREP Form -->
      <div class="bg-white rounded-2xl p-6 shadow mb-4">
        <div class="space-y-6">
          <div v-for="(item, index) in prepItems" :key="index" class="p-4 rounded-lg bg-gray-50">
            <div class="flex items-baseline gap-2 mb-2">
              <h3 class="font-medium text-black">{{ item.title }}</h3>
              <p class="text-sm text-gray-500">{{ item.description }}</p>
            </div>
            <input type="text" v-model="item.value" :placeholder="item.placeholder"
              class="w-full p-3 rounded-lg border border-gray-200 bg-white focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] focus:border-transparent" />
          </div>
        </div>
      </div>

      <!-- Navigation -->
      <div class="flex justify-center">
        <button @click="nextStep" class="px-6 py-2 min-w-[120px] rounded-full bg-red-500 text-white">
          {{ currentStep === totalSteps ? "제출하기" : "다음으로" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, computed } from "vue";
import StepProgressSlider from "@/components/StepProgressSlider.vue";
import { useRouter } from "vue-router";

const backendUrl = import.meta.env.VITE_BACKEND_URL;
const pythonUrl = import.meta.env.VITE_PYTHON_URL;
const isLoading = ref(false);

const router = useRouter();
//백에서 받아오는 주제 데이터
const topics = ref([]);

const currentStep = ref(1);
const totalSteps = 4;

// 각 라운드의 데이터를 저장할 배열
const roundsData = ref([]);

const prepItems = ref([
  {
    title: "주장",
    description: "핵심 주장을 작성하세요",
    placeholder: "주장을 입력하세요",
    value: "",
  },
  {
    title: "근거",
    description: "주장을 뒷받침하는 근거를 제시하세요",
    placeholder: "근거를 입력하세요",
    value: "",
  },
  {
    title: "예시",
    description: "구체적인 사례를 들어보세요",
    placeholder: "예시를 입력하세요",
    value: "",
  },
  {
    title: "주장 재진술",
    description: "주장을 다시 한 번 강조해보세요",
    placeholder: "주장을 다시 한 번 강조해보세요",
    value: "",
  },
]);

// 다음 단계 이동
const nextStep = () => {
  saveCurrentRoundData();
  if (currentStep.value < totalSteps) {
    currentStep.value++;
  } else {
    submitData();
  }
  console.log("currentStep:", currentStep.value);
  console.log("현재 주제:", topics.value[currentStep.value - 1]);
  window.scrollTo({ top: 0, behavior: "smooth" });
};
// 타이머 관련 변수
const timer = ref(300); // 300초 = 5분
let interval = null;

// 타이머 시작 함수
const startTimer = () => {
  // 이전 타이머가 있다면 정리
  if (interval) clearInterval(interval);
  timer.value = 300; // 5분 초기화
  interval = setInterval(() => {
    timer.value--;
    if (timer.value <= 0) {
      clearInterval(interval);
      nextStep(); // 시간이 끝나면 다음 단계로 이동
    }
  }, 1000);
};

const formattedTime = computed(() => {
  const minutes = Math.floor(timer.value / 60);
  const seconds = timer.value % 60;
  // seconds를 항상 2자리로 표시 (예: 00, 05, 09 등)
  return `${minutes}:${seconds.toString().padStart(2, "0")}`;
});

const currenttopic = computed(() => topics.value[currentStep.value - 1]);
// currentStep 변경 시 타이머 재시작
watch(currentStep, () => {
  startTimer();
});
const submitData = async () => {
  isLoading.value = true;
  // 사용자의 실제 user_id (예를 들어 현재 로그인된 사용자 정보에서 가져온다고 가정)
  const currentUserId = 2;

  // roundsData에는 각 라운드의 데이터가 저장되어 있음.
  // roundsData[0] = { topic, point, reason, example, point_restatement } for round1, etc.
  const inputs = {};
  roundsData.value.forEach((round, index) => {
    const roundNo = index + 1; // 1, 2, 3, 4
    inputs[`topic${roundNo}`] = round.topic;
    inputs[`point${roundNo}`] = round.point;
    inputs[`reason${roundNo}`] = round.reason;
    inputs[`example${roundNo}`] = round.example;
    inputs[`point_restatement${roundNo}`] = round.point_restatement;
  });

  const payload = {
    problem_id: "1-1",
    user_id: currentUserId,
    inputs,
  };

  try {
    const response = await fetch(pythonUrl + "/api/practice/", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      throw new Error("데이터 전송 실패");
    }
    const result = await response.json();
    console.log("제출 결과:", result);
    // 결과를 JSON 문자열로 변환
    const resultString = JSON.stringify(result);
    sessionStorage.setItem("prepResult", resultString);
    router.push({ name: "PrepResult" });
  } catch (error) {
    console.error("제출 중 오류 발생:", error);
  } finally {
    isLoading.value = false;
  }
};
const saveCurrentRoundData = () => {
  // currentStep은 1부터 시작하므로 topics[currentStep - 1]가 해당 라운드의 주제
  const roundData = {
    topic: topics.value[currentStep.value - 1],
    point: prepItems.value[0].value,
    reason: prepItems.value[1].value,
    example: prepItems.value[2].value,
    point_restatement: prepItems.value[3].value,
  };
  // roundsData 배열에 추가 (라운드 1이면 roundsData[0], 2이면 roundsData[1] 등)
  roundsData.value.push(roundData);
  console.log("라운드 데이터:", roundsData.value);
  prepItems.value.forEach((item) => (item.value = ""));
};

// 컴포넌트가 마운트될 때 타이머 시작
onMounted(async () => {
  try {
    const response = await fetch(backendUrl + "/api/subjects/random");
    if (!response.ok) {
      throw new Error("주제 데이터를 불러오는데 실패했습니다.");
    }
    const data = await response.json();
    console.log("주제 데이터:", data);
    topics.value = [data.subject1, data.subject2, data.subject3, data.subject4];
  } catch (error) {
    console.error("주제 불러오기 오류:", error);
  }
  startTimer();
});

// 컴포넌트 언마운트 시 타이머 정리
onBeforeUnmount(() => {
  if (interval) clearInterval(interval);
});
</script>

<style scoped></style>
