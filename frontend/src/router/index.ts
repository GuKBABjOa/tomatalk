import Main from '@/views/Main.vue'
import ColumnPage from '@/views/ColumnPage.vue'
import OngoingDebate from '@/views/OngoingDebate.vue'
import { createRouter, createWebHistory } from 'vue-router'
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
    {
      path: '/column',
      name: 'column',
      component: ColumnPage
    }
  ],
})

export default router
