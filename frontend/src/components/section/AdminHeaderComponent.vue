<template>
  <nav class="navbar navbar-expand-lg navbar-white bg-white fixed-top border-bottom border-3">
    <div class="container-fluid">
      <!-- 브랜드 -->
      <RouterLink class="navbar-brand fw-bold me-4" to="/">냠냠코치</RouterLink>

      <!-- 우측: 사용자 이름 + 로그아웃 -->
      <div class="collapse navbar-collapse justify-content-end fw-bold" id="navbarNav">
        <ul class="navbar-nav align-items-center">
          <li class="nav-item d-flex align-items-center">
            <img
              src="/default-avatar.png"
              alt="avatar"
              class="rounded-circle me-2"
              width="32"
              height="32"
            />
            <span class="me-3">{{ user.username }}</span>
            <button class="btn btn-outline-dark btn-sm" @click="logout">로그아웃</button>
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

async function logout() {
  try {
    await axios.post('/api/auth/logout')
    accountStore.clearAccount()
    router.push({ name: 'HomeView' })
  } catch (e) {}
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
