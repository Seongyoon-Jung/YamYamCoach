<template>
  <div class="community-page container py-5">
    <h2 class="text-center mb-4">커뮤니티 게시판</h2>

    <div class="text-end mb-3">
      <router-link class="btn btn-success" to="/board/create">작성</router-link>
    </div>

    <!-- 검색 -->
    <div class="d-flex justify-content-center mb-3">
      <div class="input-group w-75">
        <input
          :value="searchQuery"
          @input="onInputChange"
          type="text"
          class="form-control rounded-pill border-0 shadow-sm"
          placeholder="작성자, 제목, 내용을 검색해보세요!"
        />
        <span class="input-group-text bg-white border-0 shadow-sm rounded-pill ms-n5">
          <i class="bi bi-search"></i>
        </span>
      </div>
    </div>

    <!-- 필터 -->
    <div
      class="d-flex justify-content-between align-items-center bg-light p-2 rounded shadow-sm mb-4"
    >
      <div>
        <input v-model="onlyMine" type="checkbox" id="onlyMine" class="form-check-input me-2" />
        <label for="onlyMine" class="form-check-label">내 글만 보기</label>
      </div>
      <button class="btn btn-link text-decoration-none" @click="toggleSort">
        <i class="bi bi-sort-down-alt me-1"></i>
        {{ sortBy === 'latest' ? '최신순' : '조회순' }}
      </button>
    </div>

    <!-- 게시글 그리드 -->
    <div class="row g-4">
      <div v-for="board in loadedBoards" :key="board.id" class="col-md-4">
        <BoardCard :board="board" />
      </div>

      <div ref="scrollObserver" class="text-center py-3">
        <LoadingSpinner v-if="isLoading" />
        <span v-else-if="isEnd">마지막 게시글입니다.</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import axios from '@/plugins/axios'
import BoardCard from '@/components/board/BoardCard.vue'
import LoadingSpinner from '@/components/loading/LoadingSpinner.vue'
import { userAccountStore } from '@/store/account'

const accountStore = userAccountStore()

const searchQuery = ref('')
const onlyMine = ref(false)
const sortBy = ref('latest')

const boards = ref([]) // 전체 게시글
const loadedBoards = ref([]) // 화면에 보이는 부분
const page = ref(0)
const pageSize = 6
const isLoading = ref(false)
const isEnd = ref(false)

const scrollObserver = ref(null)
let observer = null

const onInputChange = (e) => {
  searchQuery.value = e.target.value
}
const toggleSort = () => {
  sortBy.value = sortBy.value === 'latest' ? 'views' : 'latest'
}

const filteredPosts = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  let result = boards.value.filter((board) => {
    if (onlyMine.value && board.username !== accountStore.username) return false

    if (!q) return true
    const title = board.title?.toLowerCase() || ''
    const content = board.content?.toLowerCase() || ''
    const username = board.username?.toLowerCase() || ''

    return title.includes(q) || content.includes(q) || username.includes(q)
  })

  return result.sort((a, b) => {
    if (sortBy.value === 'latest') return new Date(b.createdAt) - new Date(a.createdAt)
    else return (b.viewCount ?? 0) - (a.viewCount ?? 0)
  })
})

const loadNextPage = (silent = false) => {
  if (isLoading.value || isEnd.value) return
  if (!silent) isLoading.value = true

  const start = page.value * pageSize
  const end = start + pageSize
  const chunk = filteredPosts.value.slice(start, end)

  if (chunk.length === 0) {
    isEnd.value = true
    isLoading.value = false
    return
  }

  loadedBoards.value.push(...chunk)
  page.value++

  setTimeout(() => {
    if (!silent) isLoading.value = false
    if (scrollObserver.value && observer) {
      observer.unobserve(scrollObserver.value)
      observer.observe(scrollObserver.value)
    }
  }, 1000)
}

onMounted(async () => {
  try {
    const res = await axios.get('/api/board')
    const data = res.data

    const withUrls = await Promise.all(
      data.map(async (board) => {
        const imageRes = board.imageUrl
          ? await axios.get('/api/s3/get-url', { params: { filename: board.imageUrl } })
          : { data: null }

        const profileRes = board.profileUrl
          ? await axios.get('/api/s3/get-url', { params: { filename: board.profileUrl } })
          : { data: null }

        return {
          ...board,
          imageUrl: imageRes.data,
          profileUrl: profileRes.data,
        }
      }),
    )

    boards.value = withUrls
    loadNextPage()
  } catch (err) {
    console.error('게시글 로딩 실패', err)
  }

  observer = new IntersectionObserver(
    (entries) => {
      if (entries[0].isIntersecting && !isLoading.value && !isEnd.value) {
        loadNextPage()
      }
    },
    { threshold: 1.0 },
  )

  if (scrollObserver.value) {
    observer.observe(scrollObserver.value)
  }
})

onBeforeUnmount(() => {
  if (observer && scrollObserver.value) observer.unobserve(scrollObserver.value)
})

// 필터나 검색어가 변경될 때마다 초기화하고 다시 로딩
watch([filteredPosts, searchQuery, onlyMine, sortBy], () => {
  page.value = 0
  isEnd.value = false
  loadedBoards.value = []
  loadNextPage(true) // 🔸 silent=true → 로딩 스피너 표시 X
})
</script>

<style scoped>
.community-page input::placeholder {
  color: #aaa;
}
.card-img-top {
  height: 220px;
  object-fit: cover;
}
.input-group .form-control,
.input-group .input-group-text {
  border-radius: 50px !important;
}
</style>
