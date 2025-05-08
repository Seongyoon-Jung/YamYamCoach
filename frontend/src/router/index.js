import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store';

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: () => import('../views/HomeView.vue'),
    meta: { title: '홈 | 냠냠코치', requiresSurvey: false },
  },
  {
    path: '/home',
    name: 'HomeLoginView',
    component: () => import('../views/HomeLoginView.vue'),
    meta: { title: '홈 | 냠냠코치', requiresAuth: true },
  },
  {
    path: '/my',
    name: 'MypageView',
    component: () => import('../views/MypageView.vue'),
    meta: { title: '마이페이지 | 냠냠코치', requiresAuth: true },
  },
  {
    path: '/survey',
    name: 'SurveyView',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/SurveyView.vue'),
    meta: { title: '설문조사 | 냠냠코치' },
  },
  {
    path: '/login',
    name: 'LoginView',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/LoginView.vue'),
    meta: { title: '로그인 | 냠냠코치' },
  },
  {
    path: '/signup',
    name: 'SignupView',

    component: () =>
      import(/* webpackChunkName: "login" */ '../views/SignupView.vue'),
    meta: { title: '회원가입 | 냠냠코치' },
  },
  {
    path: '/features',
    name: 'FeaturesView',
    component: () => import('../views/FeaturesView.vue'),
    meta: { title: '기능소개 | 냠냠코치' },
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// 가드
router.beforeEach((to, from, next) => {
  const { username, isSurveyed } = store.state.account;

  // 인증 + 설문 완료 필요 경로
  if (to.meta.requiresAuth) {
    if (username && isSurveyed) {
      return next();
    } else {
      return next({ name: 'LoginView' });
    }
  }

  // 루트 접근 시 설문 완료 상태면 대시보드로 리다이렉트
  if (to.name === 'HomeView' && username && isSurveyed) {
    return next({ name: 'HomeLoginView' });
  }

  next();
});

router.afterEach((to) => {
  document.title = to.meta.title || '냠냠코치';
});

export default router;
