<!-- src/views/CommunityPage.vue -->
<template>
  <div class="community-page container py-5">
    <!-- 제목 -->
    <h2 class="text-center mb-4">커뮤니티 게시판</h2>

    <div class="text-end">
      <router-link class="btn btn-success" to="/board/create">작성</router-link>
    </div>

    <!-- 검색 바 -->
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

    <!-- 필터 바 -->
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
      <div v-for="board in filteredPosts" :key="board.id" class="col-md-4">
        <BoardCard :board="board" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from '@/plugins/axios'
import BoardCard from '@/components/board/BoardCard.vue'
import { userAccountStore } from '@/store/account'

const accountStore = userAccountStore()

// 검색어
const searchQuery = ref('')
// 내 게시글만 보기 체크박스
const onlyMine = ref(false)
// 전체 게시글 배열
const boards = ref([])

const sortBy = ref('latest')

const toggleSort = () => {
  sortBy.value = sortBy.value === 'latest' ? 'views' : 'latest'
}

const onInputChange = (e) => {
  searchQuery.value = e.target.value
}

// 백엔드에서 전체 게시글을 가져와 posts 에 할당
onMounted(async () => {
  try {
    const res = await axios.get('/api/board')
    boards.value = res.data
  } catch (err) {
    console.error('게시글 로딩 실패', err)
  }
})

const filteredPosts = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()

  let result = boards.value.filter((board) => {
    if (onlyMine.value && board.username !== accountStore.username) return false

    if (!q) return true

    const title = board.title?.toLowerCase() || ''
    const content = board.excerpt?.toLowerCase() || ''
    const username = board.username?.toLowerCase() || ''

    return title.includes(q) || content.includes(q) || username.includes(q)
  })

  console.log('정렬실행')
  // 정렬 처리 (sortBy.value 대신 sortBy.value를 바로 사용하도록 주의)
  return result.sort((a, b) => {
    if (sortBy.value === 'latest') {
      return new Date(b.createdAt) - new Date(a.createdAt)
    } else {
      return (b.viewCount ?? 0) - (a.viewCount ?? 0)
    }
  })
})
</script>

<style scoped>
.community-page input::placeholder {
  color: #aaa;
}

/* 카드 텍스트 3줄 초과 시 말줄임 */
.text-truncate-3 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

/* 카드 이미지 고정 비율 */
.card-img-top {
  height: 220px;
  object-fit: cover;
}

/* 검색창 그림자와 원형 처리 */
.input-group .form-control {
  border-radius: 50px !important;
}
.input-group .input-group-text {
  border-radius: 50px !important;
  z-index: 2;
}

/* 전체 버튼 그림자 */
.btn-outline-secondary {
  border-radius: 50px;
}
</style>
