import Main from '@/views/Main.vue'
import { createRouter, createWebHistory } from 'vue-router'
import OngoingDebate from '@/views/OngoingDebate.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: Main,
    },
    {
      path: "/debate",
      name: "debate",
      component: OngoingDebate
    },
  ]
})

export default router
