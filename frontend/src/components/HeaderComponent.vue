<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container">
      <!-- 브랜드 + 주요 네비게이션 -->
      <router-link class="navbar-brand fw-bold me-4" to="/"
        >냠냠코치</router-link
      >
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

      <!-- 우측 액션 아이콘 & 로그인/프로필 -->
      <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
        <ul class="navbar-nav align-items-center">
          <!-- 검색 아이콘 -->
          <li class="nav-item me-3">
            <a class="nav-link" href="#">
              <i class="bi bi-search fs-5"></i>
            </a>
          </li>
          <!-- 다크 모드 토글 -->
          <li class="nav-item me-3">
            <a class="nav-link" href="#" @click.prevent="toggleDarkMode">
              <i
                :class="darkMode ? 'bi bi-sun' : 'bi bi-moon'"
                class="fs-5"
              ></i>
            </a>
          </li>

          <!-- 로그인 전 -->
          <li v-if="!isLoggedIn" class="nav-item">
            <router-link
              class="nav-link btn btn-primary text-white px-3"
              to="/login"
            >
              로그인
            </router-link>
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
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li class="dropdown-header">
                {{ user.username }} 님 안녕하세요 !<br />
                <small class="text-muted">{{ user.email }}</small>
              </li>
              <li><hr class="dropdown-divider" /></li>
              <li>
                <router-link class="dropdown-item" to="/my"
                  >마이페이지</router-link
                >
              </li>
              <li>
                <router-link class="dropdown-item" to="/support"
                  >고객지원</router-link
                >
              </li>
              <li>
                <router-link class="dropdown-item" to="/my/info"
                  >내 정보 수정</router-link
                >
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

<script>
import axios from '@/plugins/axios';
import { mapState } from 'vuex';

export default {
  name: 'HeaderComponent',
  computed: {
    ...mapState({
      user: (state) => state.account, // { userId, username, isSurveyed, email? }
    }),
    isLoggedIn() {
      return !!this.user.username;
    },
    darkMode() {
      return document.body.classList.contains('dark-mode');
    },
  },
  methods: {
    logout() {
      axios
        .post('/api/auth/logout')
        .then(() => {
          this.$store.commit('clearAccount');
          this.$router.push({ name: 'HomeView' });
        })
        .catch((err) => {
          console.error(err);
          alert('로그아웃에 실패했습니다.');
        });
    },
    toggleDarkMode() {
      document.body.classList.toggle('dark-mode');
    },
  },
};
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
