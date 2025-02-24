<template>
  <div class="w-full min-h-screen bg-white">
    <!-- Header -->
    <header class="h-16 bg-gray-50 px-8 flex items-center">
      <h1 class="text-2xl font-semibold text-gray-700">PREP 평가 결과</h1>
    </header>

    <!-- Content Container -->
    <div class="container mx-auto px-4">
      <!-- Overall Score Card -->
      <div class="py-5">
        <div class="w-full p-6 rounded-2xl bg-red-50">
          <p class="text-gray-600 text-base mb-2">전체 평가</p>
          <div class="flex items-center gap-2 mb-4">
            <span class="text-3xl font-semibold text-gray-800">{{
              resultObj.total
              }}</span>
            <span class="text-sm text-gray-600">/ 100점</span>
          </div>
          <div class="relative h-5 w-full bg-gray-100 rounded-full overflow-hidden">
            <div class="absolute h-full bg-red-400 rounded-full" :style="{ width: `${Number(resultObj.total)}%` }">
            </div>
          </div>
        </div>
      </div>

      <!-- PREP Section Cards Grid -->
      <div class="grid grid-cols-2 gap-8">
        <template v-for="(section, index) in prepSections" :key="index">
          <div class="p-6 rounded-2xl bg-white shadow-lg">
            <div class="flex items-center gap-2 mb-5">
              <h2 class="text-lg font-semibold text-gray-800">
                {{ section.title }}
              </h2>
              <span class="text-sm text-gray-600">({{ section.titleEn }})</span>
            </div>

            <!-- Progress Bar -->
            <div class="flex items-center gap-3 mb-6">
              <div class="relative flex-1 h-4 bg-gray-100 rounded-full overflow-hidden">
                <div class="absolute h-full bg-red-400 rounded-full" :style="{
                  width: `${(section.score / section.maxScore) * 100}%`,
                }"></div>
              </div>
              <span class="text-sm text-gray-600">{{ section.score }}/{{ section.maxScore }}</span>
            </div>

            <!-- Feedback -->
            <div class="space-y-2">
              <p v-for="(feedback, idx) in section.feedback" :key="idx" class="text-sm text-gray-600">
                {{ feedback }}
              </p>
            </div>
          </div>
        </template>
      </div>

      <!-- Action Buttons -->
      <div class="py-6 flex justify-end gap-4">
        <button @click="gotoPrac"
          class="px-6 py-2 rounded-full bg-red-400 text-white font-medium hover:bg-red-500 transition-colors">
          처음으로
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const resultObj = ref([]);

const prepSections = computed(() => [
  {
    title: "주장",
    titleEn: "Point",
    score: resultObj.value.point_score || 0,
    maxScore: 25,
    feedback: [resultObj.value.point_comment || ""],
  },
  {
    title: "근거",
    titleEn: "Reason",
    score: resultObj.value.reason_score || 0,
    maxScore: 30,
    feedback: [resultObj.value.reason_comment || ""],
  },
  {
    title: "예시",
    titleEn: "Example",
    score: resultObj.value.example_score || 0,
    maxScore: 25,
    feedback: [resultObj.value.example_comment || ""],
  },
  {
    title: "재진술",
    titleEn: "Restatement",
    score: resultObj.value.point_restatement_point || 0,
    maxScore: 20,
    feedback: [resultObj.value.point_restatement_comment || ""],
  },
]);
const gotoPrac = () => {
  sessionStorage.removeItem("prepResult");
  router.push("/practice-main");
};
onMounted(() => {
  const stored = sessionStorage.getItem("prepResult");
  if (stored) {
    try {
      resultObj.value = JSON.parse(stored);
      console.log("resultObj:", resultObj.value);
    } catch (e) {
      console.error("result 파싱 오류:", e);
    }
  }
});
</script>
