<template>
  <div id="app" class="d-flex flex-column min-vh-100">
    <HeaderComponent />
    <main class="container bg-light flex-grow-1 pt-5">
      <router-view />
    </main>
    <FooterComponent />
  </div>
</template>

<script setup>
import HeaderComponent from '@/components/section/HeaderComponent.vue'
import FooterComponent from '@/components/section/FooterComponent.vue'
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
