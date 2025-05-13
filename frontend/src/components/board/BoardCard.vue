<!-- src/components/PostCard.vue -->
<template>
  <div class="card h-100 shadow-sm border-0">
    <!-- 대표 이미지 -->
    <img :src="post.image" class="card-img-top" alt="post image" />

    <!-- 카드 본문 -->
    <div class="card-body">
      <div class="d-flex justify-content-between mb-2">
        <div class="fw-semibold">{{ post.author }}</div>
        <small class="text-muted">{{ post.date }}</small>
      </div>
      <p class="card-text text-truncate-3">{{ post.excerpt }}</p>
    </div>

    <!-- 좋아요 -->
    <div class="card-footer bg-white border-0 d-flex justify-content-end">
      <i class="bi bi-heart"></i>
      <span class="ms-1">{{ post.likes }}</span>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  board: {
    type: Object,
    required: true,
  },
})

// 필요한 필드를 개별 ref 로 분리할 경우
// const { image, author, date, excerpt, likes } = toRefs(props.post)
</script>

<script>
export default {
  name: 'PostCard',
  props: {
    post: {
      type: Object,
      required: true,
    },
  },
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
