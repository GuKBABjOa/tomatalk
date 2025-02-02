import { createRouter, createWebHistory } from 'vue-router';
import DefaultLayout from '@/components/DefaultLayout.vue';
import MainView from '@/views/MainView.vue';
import PostDetail from '@/views/PostDetail.vue';

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
