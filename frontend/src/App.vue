<template>
  <!-- 일반 사용자 화면: ROLE_ADMIN이 아니거나, 현재 경로가 /admin으로 시작하지 않는 경우 -->
  <div v-if="!isAdmin || !isAdminRoute" id="app" class="d-flex flex-column min-vh-100">
    <HeaderComponent />
    <main class="container bg-light flex-grow-1 pt-5">
      <router-view />
    </main>
    <FooterComponent />
  </div>

  <!-- 관리자 화면: ROLE_ADMIN이면서, /admin 경로인 경우 -->
  <div v-else id="app" class="d-flex flex-column min-vh-100">
    <AdminHeaderComponent />
    <main class="bg-light flex-grow-1 pt-5">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import HeaderComponent from '@/components/section/HeaderComponent.vue'
import AdminHeaderComponent from '@/components/section/AdminHeaderComponent.vue'
import FooterComponent from '@/components/section/FooterComponent.vue'
import { useRoute, useRouter } from 'vue-router'
import { userAccountStore } from '@/store/account'
import { computed } from 'vue'

const route = useRoute()
const accountStore = userAccountStore()

// 관리자 여부
const isAdmin = computed(() => accountStore.role === 'ROLE_ADMIN')

// 현재 경로가 /admin 또는 /admin/로 시작하는지 확인
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983 !important;
}

/* dark-mode 전역 스타일 */
/* body.dark-mode {
  background-color: #121212;
  color: #eee;
}
body.dark-mode .navbar {
  background-color: #1f1f1f !important;
}
body.dark-mode .card {
  background-color: #1e1e1e;
  color: #ddd;
} */
/* 원하는 다른 컴포넌트 스타일도 추가 */
</style>
