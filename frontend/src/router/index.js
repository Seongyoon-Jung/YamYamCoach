import { createRouter, createWebHistory } from 'vue-router'
import { userAccountStore } from '@/store/account' // ✅ 올바른 스토어 import

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: () => import('../views/HomeView.vue'),
    meta: { title: '홈' },
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
    component: () => import('../views/SurveyView.vue'),
    meta: { title: '설문조사' },
  },
  {
    path: '/login',
    name: 'LoginView',
    component: () => import('../views/LoginView.vue'),
    meta: { title: '로그인' },
  },
  {
    path: '/signup',
    name: 'SignupView',
    component: () => import('../views/SignupView.vue'),
    meta: { title: '회원가입' },
  },
  {
    path: '/features',
    name: 'FeaturesView',
    component: () => import('../views/FeaturesView.vue'),
    meta: { title: '기능소개' },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  linkActiveClass: 'router-link-active',
  linkExactActiveClass: 'router-link-exact-active',
})

// ✅ 올바르게 Pinia store 사용
router.beforeEach((to, from, next) => {
  const accountStore = userAccountStore()

  if (to.meta.requiresAuth) {
    if (accountStore.username) {
      return next()
    } else {
      return next({ name: 'LoginView' })
    }
  }

  next()
})

router.afterEach((to) => {
  document.title = `${to.meta.title} - 냠냠코치`
})

export default router
