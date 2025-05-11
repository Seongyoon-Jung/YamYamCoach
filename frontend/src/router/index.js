import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store';

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: () => import('../views/HomeView.vue'),
    meta: { title: '홈'},
  },
  {
    path: '/my',
    name: 'MyView',
    component: () => import('../views/MyView.vue'),
    meta: { title: '마이페이지', requiresAuth: true },
  },
  {
    path: '/my/info',
    name: 'MyInfoView',
    component: () => import('../views/MyInfoView.vue'),
    meta: { title: '내 정보 | 마이페이지', requiresAuth: true },
  },
  {
    path: '/survey',
    name: 'SurveyView',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/SurveyView.vue'),
    meta: { title: '설문조사' },
  },
  {
    path: '/login',
    name: 'LoginView',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/LoginView.vue'),
    meta: { title: '로그인' },
  },
  {
    path: '/signup',
    name: 'SignupView',

    component: () =>
      import(/* webpackChunkName: "login" */ '../views/SignupView.vue'),
    meta: { title: '회원가입' },
  },
  {
    path: '/features',
    name: 'FeaturesView',
    component: () => import('../views/FeaturesView.vue'),
    meta: { title: '기능소개' },
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
  linkActiveClass: "router-link-active",
  linkExactActiveClass: "router-link-exact-active"
});

// 가드
router.beforeEach((to, from, next) => {
  const { username, isSurveyed } = store.state.account;

  if (to.meta.requiresAuth) {
    if (username) {
      return next();
    } else {
      return next({ name: 'LoginView' });
    }
  }

  next();
});

router.afterEach((to) => {
  document.title = to.meta.title + ' - 냠냠코치';
});

export default router;
