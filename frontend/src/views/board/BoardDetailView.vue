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
          :src="board.profileUrl"
          alt="프로필"
          class="rounded-circle me-2"
          width="40"
          height="40"
        />
        <div class="flex-grow-1 text-start">
          <strong>{{ board.username }}</strong>
        </div>
        <div v-if="accountStore.username === board.username">
          <span @click="goToModify" class="btn btn-link small text-muted py-0 px-1">글 수정</span>
          <span @click="handleBoardDelete(index)" class="btn btn-link small text-muted py-0 px-1"
            >삭제</span
          >
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
        조회수 <strong>{{ board.viewCount }}</strong>
      </div>

      <hr />
      <!-- 댓글 영역 -->
      <div>
        <h5 class="mb-3 text-start">
          댓글 <span class="text-danger">{{ comments.length }}</span>
        </h5>

        <!-- 댓글 입력 -->
        <div class="d-flex align-items-center mb-4">
          <input
            v-model="newComment"
            type="form-control text"
            class="form-control me-2"
            placeholder="댓글을 입력하세요."
            @keyup.enter="submitComment"
          />
          <button
            type="button"
            class="btn btn-success px-3"
            style="flex-shrink: 0"
            @click="submitComment"
          >
            확인
          </button>
        </div>

        <!-- 댓글 목록 -->
        <ul class="list-unstyled">
          <li v-for="(comment, index) in comments" :key="index" class="mb-4 border-bottom pb-3">
            <!-- 왼쪽: 프로필 + 본문 -->
            <div class="d-flex align-items-center">
              <img
                :src="comment.profileUrl"
                alt="프로필"
                class="rounded-circle me-3"
                width="40"
                height="40"
              />
              <!-- 이름 + 내용 한 줄 -->
              <div class="flex-grow-1 d-flex">
                <strong class="align-self-center">{{ comment.username }}</strong>
                <textarea
                  v-if="modify[index]"
                  class="mx-3 flex-grow-1"
                  v-model="comment.content"
                  rows="2"
                  @keyup.enter="modifyComment(index)"
                />
                <span v-else class="flex-grow-1 ms-2 text-start">{{ comment.content }}</span>
              </div>

              <button
                v-if="modify[index]"
                type="button"
                class="btn btn-success px-2"
                style="flex-shrink: 0"
                @click="modifyComment(index)"
              >
                수정
              </button>
              <!-- 오른쪽: 날짜 -->
              <div v-else class="text-muted small text-end">
                {{ formatDate(comment.createdAt) }}
                <br />
                <div v-if="accountStore.username === comment.username">
                  <span @click="modifyStart(index)" class="btn btn-link small text-muted py-0 px-1"
                    >수정</span
                  >
                  <span @click="handleDelete(index)" class="btn btn-link small text-muted py-0 px-1"
                    >삭제</span
                  >
                </div>
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
  <!-- ConfirmDialog 컴포넌트를 ref 로 선언 -->
  <ConfirmDialog ref="confirmDialog" />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'
import ConfirmDialog from '@/components/dialog/ConfirmDialog.vue'

const confirmDialog = ref(null)

// pinia store을 사용하겠다
const accountStore = userAccountStore()

const route = useRoute() // 현재 라우트의 정보 (url 파라미터)
const router = useRouter() // 라우팅으로 페이지 전환하기 위함
const board = ref(null)
const comments = ref([])
const newComment = ref('')
const modify = ref([])

onMounted(async () => {
  const id = route.params.id

  try {
    const res = await axios.get(`/api/board/${id}?hit=true`)
    board.value = res.data
    document.title = `${board.value.title} - 냠냠코치`
    modify.value = new Array(board.value.length).fill(false)
  } catch (err) {
    router.push('/error')
  }

  try {
    const res = await axios.get(`/api/comment/${id}`)
    comments.value = res.data
  } catch (err) {
    console.log('실패')
  }
})

const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getFullYear()}년 ${(d.getMonth() + 1).toString().padStart(2, '0')}월 ${d.getDate().toString().padStart(2, '0')}일 ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const submitComment = async function () {
  if (newComment.value === '') {
    return
  }
  const post = {
    boardId: route.params.id,
    username: accountStore.username,
    content: newComment.value,
  }

  try {
    const res = await axios.post('/api/comment', post)
    comments.value.unshift(res.data)
    modify.value.unshift(false)
    newComment.value = ''
  } catch (err) {
    router.push('/error')
  }
}

const modifyStart = function (index) {
  modify.value[index] = true
}

const modifyComment = async function (index) {
  modify.value[index] = false

  const post = {
    commentId: comments.value[index].commentId,
    boardId: route.params.id,
    username: accountStore.username,
    content: comments.value[index].content,
  }

  try {
    const res = await axios.put('/api/comment', post)
    comments.value[index] = res.data
  } catch (err) {
    router.push('/error')
  }
}

async function handleDelete(index) {
  const ok = await confirmDialog.value.open({
    title: '삭제 확인',
    message: `댓글을 삭제하시겠습니까?`,
  })
  if (!ok) return

  const post = {
    commentId: comments.value[index].commentId,
  }

  try {
    const res = await axios.delete('/api/comment', {
      data: post,
    })
    comments.value.splice(index, 1)
  } catch (err) {
    router.push('/error')
  }
}

async function handleBoardDelete(index) {
  const ok = await confirmDialog.value.open({
    title: '삭제 확인',
    message: `게시글을 삭제하시겠습니까?`,
  })
  if (!ok) return

  const post = {
    boardId: board.value.boardId,
    imageUrl: board.value.imageUrl,
  }

  try {
    const res = await axios.delete('/api/board', {
      data: post,
    })
    router.push('/board')
  } catch (err) {
    router.push('/error')
  }
}

const goToModify = () => {
  router.push(`/board/modify/${route.params.id}`)
}
</script>
