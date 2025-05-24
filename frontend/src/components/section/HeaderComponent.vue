<template>
  <nav class="navbar navbar-expand-lg navbar-white bg-white border-bottom border-3">
    <div class="container position-relative">
      <!-- 브랜드 + 주요 네비게이션 -->
      <RouterLink class="navbar-brand fw-bold me-4" to="/">SSAFYBAPY</RouterLink>
      <ul class="navbar-nav me-auto mb-2 mb-lg-0 fw-bold">
        <li class="nav-item">
          <RouterLink class="nav-link" to="/">홈</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link" to="/features">기능 소개</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link" to="/board">커뮤니티</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link" to="/recipe">레시피</RouterLink>
        </li>
      </ul>

      <!-- 우측 메뉴 -->
      <div class="collapse navbar-collapse justify-content-end fw-bold" id="navbarNav">
        <ul class="navbar-nav align-items-center">
          <!-- 이슈 뉴스 슬라이드 -->
          <li
            class="nav-item d-flex align-items-center ms-3"
            style="height: 1.5rem; overflow: hidden; width: 500px"
          >
            <router-link
              v-if="isLoggedIn"
              to="/news"
              class="text-decoration-none badge bg-light text-primary me-2"
              >나의 맞춤 뉴스</router-link
            >
            <div style="height: 1.5rem; overflow: hidden; position: relative">
              <transition-group
                name="slide-down"
                tag="div"
                class="position-relative"
                style="height: 1.5rem; line-height: 1.5rem"
              >
                <a
                  v-if="newsList.length"
                  :key="newsList[currentNewsIndex].title"
                  :href="newsList[currentNewsIndex].newsUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="text-dark text-truncate small text-decoration-none d-block"
                  style="white-space: nowrap"
                >
                  {{ newsList[currentNewsIndex].title }}
                </a>
              </transition-group>
            </div>
          </li>

          <!-- 다크 모드 토글 -->
          <li class="nav-item">
            <a class="nav-link" href="#" @click.prevent="toggleDarkMode">
              <i :class="darkMode ? 'bi bi-sun' : 'bi bi-moon'" class="fs-5"></i>
            </a>
          </li>

          <!-- 로그인 상태 -->
          <li v-if="!isLoggedIn" class="nav-item">
            <RouterLink class="btn btn-success text-white px-3" to="/login"> 로그인 </RouterLink>
          </li>
          <li v-else class="nav-item dropdown">
            <a
              class="nav-link dropdown-toggle d-flex align-items-center"
              href="#"
              role="button"
              data-bs-toggle="dropdown"
            >
              <img :src="user.profileUrl" class="rounded-circle" width="32" height="32" />
              <span class="mx-2">{{ user.username }}</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><RouterLink class="dropdown-item" to="/my">마이페이지</RouterLink></li>
              <li><RouterLink class="dropdown-item" to="/support">고객지원</RouterLink></li>
              <li><RouterLink class="dropdown-item" to="/my/info">내 정보 수정</RouterLink></li>
              <li><hr class="dropdown-divider" /></li>
              <li><button class="dropdown-item" @click="logout">로그아웃</button></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { userAccountStore } from '@/store/account'
import axios from '@/plugins/axios'

const router = useRouter()
const accountStore = userAccountStore()

const user = computed(() => accountStore)
const isLoggedIn = computed(() => !!user.value.username)
const darkMode = computed(() => document.body.classList.contains('dark-mode'))

const newsList = ref([])

const currentNewsIndex = ref(0)

function toggleDarkMode() {
  document.body.classList.toggle('dark-mode')
}

async function logout() {
  try {
    await axios.post('/api/auth/logout')
    accountStore.clearAccount()
    router.push({ name: 'HomeView' })
  } catch (e) {
    console.error(e)
  }
}

let newsInterval = null
onMounted(async () => {
  const personaId = accountStore.personaId ?? -1
  try {
    const res = await axios.get(`/api/news/${personaId}`)
    newsList.value = res.data
  } catch (err) {
    console.error(err)
  }

  newsInterval = setInterval(() => {
    currentNewsIndex.value = (currentNewsIndex.value + 1) % newsList.value.length
  }, 5000)
})

onBeforeUnmount(() => {
  clearInterval(newsInterval)
})
</script>

<style scoped>
/* animation 스타일 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition:
    transform 0.5s ease,
    opacity 0.5s ease;
}
.slide-down-enter-from {
  transform: translateY(-100%);
  opacity: 0;
}
.slide-down-leave-to {
  transform: translateY(100%);
  opacity: 0;
}
</style>
