<template>
  <!-- 에러 페이지 전용 전체 화면 -->
  <div v-if="route.name === 'Error'" class="error-layout min-vh-100">
    <router-view />
  </div>

  <!-- 일반 사용자 화면: ROLE_ADMIN이 아니거나, 현재 경로가 /admin으로 시작하지 않는 경우 -->
  <div v-else-if="!isAdmin || !isAdminRoute" id="app" class="d-flex flex-column min-vh-100">
    <HeaderComponent />
    <main class="container bg-light flex-grow-1 px-0">
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
import { useRoute } from 'vue-router'
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
#error-layout,
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
</style>
