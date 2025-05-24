import { createRouter, createWebHistory } from 'vue-router'
import { userAccountStore } from '@/store/account' // ✅ 올바른 스토어 import
import RecipeView from '@/views/recipe/RecipeView.vue'
import RecipeDetailView from '@/views/recipe/RecipeDetailView.vue'
import RecipeCreateView from '@/views/recipe/RecipeCreateView.vue'

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: () => import('../views/home/HomeView.vue'),
    meta: { title: '홈' },
  },
  {
    path: '/admin',
    name: 'AdminView',
    component: () => import('../views/admin/AdminView.vue'),
    meta: { title: '관리자페이지', requiresAuth: true, requiresRole: true },
    children: [
      {
        path: '',
        component: () => import('../components/admin/AdminDashboard.vue'),
      },
      {
        path: 'users',
        component: () => import('../components/admin/AdminUsers.vue'),
      },
      {
        path: 'diet',
        component: () => import('../components/admin/AdminDiet.vue'),
      },
      {
        path: 'diet-upload',
        component: () => import('../components/admin/AdminDietUpload.vue'),
      },
    ],
  },
  {
    path: '/news',
    name: 'NewsView',
    component: () => import('../views/news/NewsView.vue'),
    meta: { title: '내 맞춤 뉴스', requiresAuth: true },
  },
  {
    path: '/my',
    name: 'MyView',
    component: () => import('../views/user/MyView.vue'),
    meta: { title: '마이페이지', requiresAuth: true },
  },
  {
    path: '/my/info',
    name: 'MyInfoView',
    component: () => import('../views/user/MyInfoView.vue'),
    meta: { title: '내 정보 | 마이페이지', requiresAuth: true },
  },
  {
    path: '/board',
    name: 'BoardView',
    component: () => import('../views/board/BoardView.vue'),
    meta: { title: '커뮤니티' },
  },
  {
    path: '/board/create',
    name: 'BoardCreateView',
    component: () => import('../views/board/BoardCreateView.vue'),
    meta: { title: '작성', requiresAuth: true },
  },
  {
    path: '/board/:id',
    name: 'BoardDetailView',
    component: () => import('../views/board/BoardDetailView.vue'),
    props: true,
    meta: { requiresAuth: true },
  }, // 상세 페이지 라우트
  {
    path: '/board/modify/:id',
    name: 'BoardModifyView',
    component: () => import('../views/board/BoardModifyView.vue'),
    props: true,
    meta: { requiresAuth: true },
  },
  {
    path: '/survey',
    name: 'SurveyView',
    component: () => import('../views/user/SurveyView.vue'),
    meta: { title: '설문조사', requiresAuth: true },
  },
  {
    path: '/login',
    name: 'LoginView',
    component: () => import('../views/user/LoginView.vue'),
    meta: { title: '로그인' },
  },
  {
    path: '/signup',
    name: 'SignupView',
    component: () => import('../views/user/SignupView.vue'),
    meta: { title: '회원가입' },
  },
  {
    path: '/features',
    name: 'FeaturesView',
    component: () => import('../views/others/FeaturesView.vue'),
    meta: { title: '기능소개' },
  },
  {
    path: '/error',
    name: 'Error',
    component: () => import('../views/error/ErrorView.vue'),
    meta: { title: '에러' },
  },
  {
    path: '/recipe',
    name: 'Recipe',
    component: RecipeView,
    meta: { requiresAuth: false }
  },
  {
    path: '/recipe/:id',
    name: 'RecipeDetail',
    component: RecipeDetailView,
    meta: { requiresAuth: false }
  },
  {
    path: '/recipe/create',
    name: 'RecipeCreateView',
    component: RecipeCreateView
  },
  {
    path: '/recipe/:recipeId',
    name: 'RecipeDetailView',
    component: RecipeDetailView
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/error',
    meta: { title: '에러' },
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
    if (to.meta.requiresRole) {
      if (accountStore.role === 'ROLE_ADMIN') {
        return next()
      } else {
        return next({ name: 'notFound' })
      }
    } else {
      if (accountStore.username) {
        return next()
      } else {
        return next({ name: 'LoginView' })
      }
    }
  }

  next()
})

router.afterEach((to) => {
  document.title = `${to.meta.title} - 싸피밥피`
})

export default router
