<!-- src/components/news/SectionNewsTabs.vue -->
<template>
  <a :href="newsUrl" class="text-decoration-none h-100 d-flex flex-column" target="_blank">
    <div class="card mb-1 text-start border-0 ">
      <div class="row g-0">
        <!-- 이미지 컬럼 -->
        <div class="col-md-2">
          <div class="card-img-wrapper">
            <img
              :src="imageUrl || 'https://via.placeholder.com/600x200'"
              class="card-img"
              alt="뉴스 이미지"
            />
          </div>
        </div>
        <!-- 텍스트 컬럼 -->
        <div class="col-md-10">
          <div class="card-body">
            <h5 class="card-title text-truncate text-bold">{{ title }}</h5>
            <p class="card-text text-truncate-1 text-muted small">{{ description }}</p>
            <p class="card-text">
              <small class="text-muted">{{ date }}</small>
            </p>
          </div>
        </div>
      </div>
    </div>
  </a>
</template>

<script setup>
defineProps({
  title: { type: String, default: '오늘의 뉴스' },
  imageUrl: { type: String, default: '' },
  description: { type: String, required: true },
  newsUrl: { type: String, default: '' },
  date: { type: String, default: '' },
})
</script>

<style scoped>
.card-img-wrapper {
  width: 100%;
  height: 150px;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: background-color 0.3s ease;
}

:global(body.dark-mode) .card-img-wrapper {
  background-color: #2a2a2a !important;
}

.card-img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.text-truncate-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3; /* 3줄까지 보이도록 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
