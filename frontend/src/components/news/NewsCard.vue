<!-- src/components/NewsCard.vue -->
<template>
  <div class="card news-card shadow-sm rounded-4 border-0">
    <a :href="newsUrl" class="text-decoration-none h-100 d-flex flex-column" target="_blank">
      <!-- 이미지 래퍼 추가 -->
      <div class="news-card__img-wrapper rounded-top-4">
        <img
          :src="imageUrl || 'https://via.placeholder.com/600x200'"
          class="news-card__img"
          alt="뉴스 이미지"
        />
      </div>

      <div class="card-body news-card__body text-start">
        <h5 class="card-title text-truncate-2 text-bold">{{ title }}</h5>
        <p class="card-text small text-muted text-truncate-3 text-muted">{{ description }}</p>
      </div>
    </a>
  </div>
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
.news-card {
  /* 카드 높이는 auto, 내부 이미지만 고정 */
}

/* 1) 이미지 래퍼: 높이를 고정하고 overflow:hidden */
.news-card__img-wrapper {
  width: 100%;
  height: 120px; /* 필요에 따라 조절 */
  background-color: #f0f0f0; /* 이미지 비율에 따라 생기는 여백용 배경 */
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 2) 실제 img: contain 으로 잘림 없이 영역 안에 맞추기 */
.news-card__img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  object-position: center;
}

/* 본문 */
.news-card__body {
  padding: 0.75rem;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 타이틀/설명 줄임 */
.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
