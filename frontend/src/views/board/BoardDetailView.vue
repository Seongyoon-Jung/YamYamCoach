<template>
  <div class="container py-5">
    <div v-if="board">
      <h2 class="mb-4">{{ board.title }}</h2>
      <img :src="board.imageUrl" class="img-fluid mb-3" />
      <!-- 날짜 -->
      <h4 class="mb-3 text-start">{{ formatDate(board.createdAt) }}</h4>
      <hr />
      <!-- 사용자 정보 -->
      <div class="d-flex align-items-center mb-2">
        <img
          src="/default-avatar.png"
          alt="프로필"
          class="rounded-circle me-2"
          width="40"
          height="40"
        />
        <div>
          <strong>{{ board.userName }}</strong>
        </div>
      </div>
      <!-- 내용 -->
      <p class="fs-5 text-start">{{ board.content }}</p>

      <!-- 태그 -->
      <div class="mb-3">
        <span v-for="tag in board.tags" :key="tag" class="badge bg-light text-dark border me-1">
          {{ tag }}
        </span>
      </div>

      <!-- 좋아요 -->
      <div class="mb-4 text-muted text-start">
        좋아요 <strong>{{ board.likeCount }}</strong>
      </div>

      <hr />
      <!-- 댓글 영역 -->
      <div>
        <h5 class="mb-3 text-start">
          댓글 <span class="text-danger">{{ comments.length }}</span>
        </h5>

        <!-- 댓글 입력 -->
        <div class="d-flex align-items-center mb-4">
          <button class="btn btn-link p-0 me-2">
            <i class="bi bi-camera" style="font-size: 1.4rem"></i>
          </button>
          <input
            v-model="newComment"
            type="text"
            class="form-control me-2"
            placeholder="댓글을 입력하세요."
          />
          <button class="btn btn-dark px-3" @click="submitComment">확인</button>
        </div>

        <!-- 댓글 목록 -->
        <ul class="list-unstyled">
          <li v-for="(comment, index) in comments" :key="index" class="mb-4 border-bottom pb-3">
            <div class="d-flex justify-content-between align-items-start">
              <!-- 왼쪽: 프로필 + 본문 -->
              <div class="d-flex">
                <img
                  :src="comment.avatarUrl || '/default-avatar.png'"
                  alt="프로필"
                  class="rounded-circle me-3"
                  width="40"
                  height="40"
                />
                <div>
                  <!-- 이름 + 내용 한 줄 -->
                  <div>
                    <strong>{{ comment.userName }}</strong>
                    <span class="ms-2">{{ comment.content }}</span>
                  </div>
                </div>
              </div>

              <!-- 오른쪽: 날짜 -->
              <div class="text-muted small">
                {{ formatDate(comment.createdAt) }}
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <div v-else>
      <p>게시글을 불러오는 중입니다...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/plugins/axios'

const route = useRoute() // 현재 라우트의 정보 (url 파라미터)
const router = useRouter() // 라우팅으로 페이지 전환하기 위함
const board = ref(null)
const comments = ref([])
const newComment = ref('')

onMounted(async () => {
  const id = route.params.id

  try {
    const res = await axios.get(`/api/board/${id}`)
    board.value = res.data
    document.title = `${board.value.title} - 냠냠코치`
  } catch (err) {
    router.push('/error')
  }

  try {
    const res = await axios.get(`/api/board/${id}/comments`)
    comments.value = res.data
    console.log(res.data)
  } catch (err) {
    console.log('실패')
  }
})

const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getFullYear()}.${(d.getMonth() + 1).toString().padStart(2, '0')}.${d.getDate().toString().padStart(2, '0')}`
}
</script>
