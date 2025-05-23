<!-- src/components/PostCard.vue -->
<template>
  <div class="card h-100 shadow-sm border-0" @click="goToDetail" style="cursor: pointer">
    <!-- 대표 이미지 -->
    <img :src="board.imageUrl" class="card-img-top" alt="post image" />

    <!-- 카드 본문 -->
    <div class="card-body">
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
          <div class="text-muted small text-start">
            {{ formatDate(board.createdAt) }}
          </div>
        </div>
      </div>
      <hr />
      <div class="text-start">
        <p class="card-text text-start fw-bold text-truncate-1">{{ board.title }}</p>
        <p class="card-text text-truncate-3">{{ board.content }}</p>
      </div>
    </div>

    <div class="mb-4 me-4 text-muted text-end">
      조회수 <strong>{{ board.viewCount }}</strong>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'
import axios from '@/plugins/axios'

const props = defineProps({
  board: {
    type: Object,
    required: true,
  },
})

const router = useRouter()

const imageUrl = ref(null)
const profileUrl = ref(null)

const goToDetail = () => {
  router.push(`/board/${props.board.boardId}`)
}

const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getFullYear()}년 ${(d.getMonth() + 1).toString().padStart(2, '0')}월 ${d.getDate().toString().padStart(2, '0')}일 ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

// onMounted(async () => {
//   if (props.board.imageUrl) {
//     try {
//       const res = await axios.get('/api/s3/get-url', {
//         params: { filename: props.board.imageUrl },
//       })
//       imageUrl.value = res.data
//     } catch (err) {
//       console.error('이미지 presigned URL 가져오기 실패:', err)
//     }
//   }

//   if (props.board.profileUrl) {
//     try {
//       const res = await axios.get('/api/s3/get-url', {
//         params: { filename: props.board.profileUrl },
//       })
//       profileUrl.value = res.data
//     } catch (err) {
//       console.error('프로필 presigned URL 가져오기 실패:', err)
//     }
//   }
// })
</script>

<style scoped>
.card-img-top {
  height: 220px;
  object-fit: cover;
}
.text-truncate-3 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
</style>
