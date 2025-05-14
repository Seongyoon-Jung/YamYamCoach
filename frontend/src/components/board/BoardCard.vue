<!-- src/components/PostCard.vue -->
<template>
  <div class="card h-100 shadow-sm border-0" @click="goToDetail" style="cursor: pointer">
    <!-- 대표 이미지 -->
    <img :src="board.imageUrl" class="card-img-top" alt="post image" />

    <!-- 카드 본문 -->
    <div class="card-body">
      <div class="d-flex justify-content-between mb-2">
        <div class="fw-semibold">{{ board.userName }}</div>
        <small class="text-muted">{{ board.createdAt }}</small>
      </div>
      <p class="card-text text-truncate-3">{{ board.content }}</p>
    </div>

    <!-- 좋아요 -->
    <div class="card-footer bg-white border-0 d-flex justify-content-end">
      <i class="bi bi-heart"></i>
      <span class="ms-1">{{ board.likeCount }}</span>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  board: {
    type: Object,
    required: true,
  },
})

const router = useRouter()

const goToDetail = () => {
  router.push(`/board/${props.board.boardId}`)
}
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
