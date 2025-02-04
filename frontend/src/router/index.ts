import { createRouter, createWebHistory } from 'vue-router';
import DefaultLayout from '@/components/DefaultLayout.vue';
import MainView from '@/views/MainView.vue';
import MatchingView from '@/views/MatchingView.vue'

//import PostDetail from '@/views/PostDetail.vue';

const routes = [
  {
    path: '/',
    name: 'main',
    component: MainView,
    meta: {
      layout: DefaultLayout,
      headerType: '',
      showNavBar: true,
    },
  },
  {
    path: '/match',
    name: 'match',
    component: MatchingView,
    meta: {
      // 필요에 따라 DefaultLayout이나 다른 레이아웃 지정
      layout: DefaultLayout,
      headerType: '',
      showNavBar: true,
    },
  },
  // {
  //   path: '/:id',
  //   name: 'ItemDetail',
  //   component: PostDetail,
  //   meta: {
  //     layout: DefaultLayout,
  //     headerType: '',
  //     showNavBar: true, 
  //   },
  // }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
