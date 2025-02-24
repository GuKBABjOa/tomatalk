import { createRouter, createWebHistory } from "vue-router";
import LandingView from "@/views/LandingView.vue";
import MainView from "@/views/MainView.vue";
import Info from "@/views/Info.vue";
import practiceMain from "@/views/PracticeMain.vue";
import AIDebate from "@/views/AIDebate.vue";
import CategorySelection from "@/views/CategorySelection.vue";
import DebateFormatSelection from "@/views/DebateFormatSelection.vue";
import OngoingDebate from "@/views/OngoingDebate.vue";
import DebateWaiting from "@/views/DebateWaiting.vue";
import Debate from "@/views/Debate.vue";
import DebateWatch from "@/views/DebateForAudience.vue";
import Feedback from "@/views/DebateReportMain.vue";
import DebateCount from "@/components/DebateCount.vue";
import Score from "@/components/Score.vue";
import PrepExplain from "@/views/PrepExplain.vue";
import PrepPracticeDetail from "@/views/PrepPracticeDetail.vue";
import PrepResult from "@/views/PrepResult.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "root",
      component: () => {
        return new Promise((resolve) => {
          const token = localStorage.getItem("token");
          resolve(token ? MainView : LandingView);
        });
      },
    },
    {
      path: "/info",
      name: "info",
      component: Info,
    },
    {
      path: "/practice-main",
      name: "practice-main",
      component: practiceMain,
    },
    {
      path: "/ai-debate",
      name: "AIDebate",
      component: AIDebate,
    },
    {
      path: "/debate-select",
      name: "debateSelection",
      component: DebateFormatSelection,
    },
    {
      path: "/category-selection",
      name: "CategorySelection",
      component: CategorySelection,
    },
    {
      path: "/debate-list",
      name: "debateList",
      component: OngoingDebate,
    },
    {
      path: "/debate-prepare/:debateId",
      name: "debatePrepare",
      component: DebateWaiting,
      props: true, // debateId props로 전달
    },
    {
      path: "/debate/:debateId",
      name: "debateRoom",
      component: Debate,
      props: true, // debateId를 props로 전달
    },
    {
      path: "/debate-watch",
      name: "debateWatchRoom",
      component: DebateWatch,
    },
    {
      path: "/stats/debate-count",
      name: "debateCount",
      component: DebateCount,
    },
    {
      path: "/stats/score",
      name: "score",
      component: Score,
    },
    {
      path: "/stats/feedback",
      name: "feedback",
      component: Feedback,
    },
    {
      path: "/prep/explain",
      name: "PrepExplain",
      component: PrepExplain,
    },
    {
      path: "/prep/detail",
      name: "PrepDetail",
      component: PrepPracticeDetail,
    },
    {
      path: "/prep/result",
      name: "PrepResult",
      component: PrepResult,
    },
    {
      path: "/prep",
      redirect: "/prep/explain",
    },
  ],
});

export default router;
