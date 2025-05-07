<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container">
      <!-- 브랜드 + 주요 네비게이션 -->
      <router-link class="navbar-brand fw-bold me-4" to="/">냠냠코치</router-link>
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <router-link class="nav-link" to="/">홈</router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" to="/features">기능 소개</router-link>
        </li>
      </ul>

      <!-- 토글버튼 (모바일) -->
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <!-- 우측 액션 아이콘 & 로그인 -->
      <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
        <ul class="navbar-nav align-items-center">
          <!-- 검색 아이콘 -->
          <li class="nav-item">
            <a class="nav-link" href="#">
              <i class="bi bi-search fs-5"></i>
            </a>
          </li>
          <!-- 다크 모드 토글 -->
          <li class="nav-item">
            <a class="nav-link" href="#" @click.prevent="toggleDarkMode">
              <i class="bi bi-moon fs-5"></i>
            </a>
          </li>
          <!-- 로그인 / 로그아웃 -->
          <li class="nav-item ms-3">
            <router-link
              v-if="$store.state.account.username === ''"
              class="nav-link btn btn-primary text-white px-3"
              to="/login"
            >
              로그인
            </router-link>
            <a
              v-else
              class="nav-link btn btn-primary text-white px-3"
              @click="logout"
            >
              로그아웃
            </a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import axios from '@/plugins/axios'
import store from '@/store'

export default {
  name: 'HeaderComponent',
  methods: {
    logout() {
      axios
        .post('/api/auth/logout')
        .then((res) => {
          this.$store.commit('clearAccount')
          alert('로그아웃이 완료되었습니다.')
          this.$router.push({ name: 'HomeView' })
        })
        .catch((err) => {
          console.log(err)
          alert('로그아웃에 실패하였습니다.')
        })
    },
    toggleDarkMode() {
      document.body.classList.toggle('dark-mode')
    }
  }
}
</script>

<style scoped>
/* nav-link 로고 옆 간격 맞춤 */
.navbar-brand {
  margin-right: 1rem;
}
</style>
