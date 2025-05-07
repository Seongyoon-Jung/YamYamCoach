import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store'

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: () => import('../views/HomeView.vue'),
    meta: { title: '홈 | 냠냠코치', requiresSurvey: false }
  },
  {
    path: '/',
    name: 'HomeLoginView',
    component: () => import('../views/HomeLoginView.vue'),
    meta: { title: '홈 | 냠냠코치', requiresSurvey: true }
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

router.beforeEach((to, from, next) => {
  // 설문 완료가 필요한 홈(홈로그인뷰)로 진입 시
  if (to.meta.requiresSurvey) {
    if (store.state.account.username && store.state.account.isSurveyed) {
      return next()
    } else {
      return next({ name: 'SurveyView' })
    }
  }

  // 루트(‘/’) 접근인데 이미 설문 완료 상태면 HomeLoginView로
  if (
    to.name === 'HomeView' &&
    store.state.account.username &&
    store.state.account.isSurveyed
  ) {
    return next({ name: 'HomeLoginView' })
  }

  next()
})


router.afterEach((to) => {
  document.title = to.meta.title || '냠냠코치';
});

export default router;
