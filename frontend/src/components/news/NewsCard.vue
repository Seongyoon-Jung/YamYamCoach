<!-- src/components/NewsCard.vue -->
<template>
  <a :href="newsUrl" class="text-decoration-none">
    <img
      :src="imageUrl || 'https://via.placeholder.com/600x200'"
      class="card-img-top fixed-img"
      alt="뉴스 이미지"
    />
    <div class="card-body">
      <h5 class="card-title">{{ title }}</h5>
      <p class="card-text text-muted">{{ description }}</p>
    </div>
  </a>
</template>

<script setup>
defineProps({
  title: { type: String, default: '오늘의 뉴스' },
  imageUrl: { type: String, default: '' },
  description: { type: String, required: true },
  newsUrl: { type: String, default: '' },
})
</script>

<style scoped>
.fixed-img {
  width: 50%;
  height: 200px;
  object-fit: cover;
  border-top-left-radius: 0.375rem;
  border-top-right-radius: 0.375rem;
}
</style>
