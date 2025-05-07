import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store'

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/HomeView.vue'),
    meta: { title: '홈 | 냠냠코치' }
  },
  {
    path: '/home',
    name: 'HomeLoginView',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/HomeLoginView.vue'),
    meta: { title: '홈 | 냠냠코치' }
  },
  {
    path: '/survey',
    name: 'SurveyView',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/SurveyView.vue'),
    meta: { title: '설문조사 | 냠냠코치' }
  },
  {
    path: '/login',
    name: 'LoginView',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/LoginView.vue'),
    meta: { title: '로그인 | 냠냠코치' }
  },
  {
    path: '/signup',
    name: 'SignupView',

    component: () =>
      import(/* webpackChunkName: "login" */ '../views/SignupView.vue'),
    meta: { title: '회원가입 | 냠냠코치' }
  },
  {
    path: '/features',
    name: 'FeaturesView',
    component: () => import('../views/FeaturesView.vue'),
    meta: { title: '기능소개 | 냠냠코치' }
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// 전역 가드
router.beforeEach((to, from, next) => {
  // 1) 루트(‘/’)로 진입하는데 이미 로그인된 상태고, 설문조사를 완료한 상태라면
  if (to.path === '/' && store.state.account.username !='' && store.state.account.isSurveyed) {
    // 2) ‘DashboardView’(예시)로 리다이렉트
    return next({ name: 'HomeLoginView' })
  }
  // 그 외에는 그냥 진행
  next()
})

router.afterEach((to) => {
  document.title = to.meta.title || '냠냠코치';
});

export default router;
