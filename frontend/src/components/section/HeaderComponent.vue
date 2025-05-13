<template>
  <nav class="navbar navbar-expand-lg navbar-white bg-white fixed-top border-bottom border-3">
    <div class="container">
      <!-- 브랜드 + 주요 네비게이션 -->
      <RouterLink class="navbar-brand fw-bold me-4" to="/">냠냠코치</RouterLink>
      <ul class="navbar-nav me-auto mb-2 mb-lg-0 fw-bold">
        <li class="nav-item">
          <RouterLink class="nav-link text-decoration-none" to="/">홈</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link text-decoration-none" to="/features">기능 소개</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link text-decoration-none" to="/board">커뮤니티</RouterLink>
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

      <!-- 우측 액션 아이콘 & 로그인/프로필 -->
      <div class="collapse navbar-collapse justify-content-end fw-bold" id="navbarNav">
        <ul class="navbar-nav align-items-center">
          <!-- 검색 아이콘 -->
          <li class="nav-item me-3">
            <a class="nav-link" href="/">
              <i class="bi bi-search fs-5"></i>
            </a>
          </li>
          <!-- 다크 모드 토글 -->
          <li class="nav-item me-3">
            <a class="nav-link" href="/" @click.prevent="toggleDarkMode">
              <i :class="darkMode ? 'bi bi-sun' : 'bi bi-moon'" class="fs-5"></i>
            </a>
          </li>

          <!-- 로그인 전 -->
          <li v-if="!isLoggedIn" class="nav-item">
            <RouterLink class="btn btn-success text-white px-3" to="/login"> 로그인 </RouterLink>
          </li>

          <!-- 로그인 후: 프로필 드롭다운 -->
          <li v-else class="nav-item dropdown">
            <a
              class="nav-link dropdown-toggle d-flex align-items-center"
              href="#"
              role="button"
              data-bs-toggle="dropdown"
            >
              <img
                src="/default-avatar.png"
                alt="avatar"
                class="rounded-circle"
                width="32"
                height="32"
              />
              <span class="mx-2">{{ user.username }}</span>
            </a>

            <ul class="dropdown-menu dropdown-menu-end">
              <li>
                <RouterLink class="dropdown-item" to="/my">마이페이지</RouterLink>
              </li>
              <li>
                <RouterLink class="dropdown-item" to="/support">고객지원</RouterLink>
              </li>
              <li>
                <RouterLink class="dropdown-item" to="/my/info">내 정보 수정</RouterLink>
              </li>
              <li><hr class="dropdown-divider" /></li>
              <li>
                <button class="dropdown-item" @click="logout">로그아웃</button>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'

const router = useRouter()
const accountStore = userAccountStore()

const user = computed(() => accountStore)
const isLoggedIn = computed(() => !!user.value.username)
const darkMode = computed(() => document.body.classList.contains('dark-mode'))

function toggleDarkMode() {
  document.body.classList.toggle('dark-mode')
}

async function logout() {
  try {
    await axios.post('/api/auth/logout')
    accountStore.clearAccount()
    router.push({ name: 'HomeView' })
  } catch (e) {
    alert('로그아웃에 실패했습니다.')
  }
}
</script>

<style scoped>
.navbar-brand {
  margin-right: 1rem;
}
.dropdown-header {
  padding: 0.5rem 1rem;
  white-space: normal;
}
</style>
