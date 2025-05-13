<template>
  <div class="community-page container py-5">
    <!-- 제목 -->
    <h2 class="text-center mb-4">집밥일기</h2>

    <!-- 검색 바 -->
    <div class="d-flex justify-content-center mb-3">
      <div class="input-group w-75">
        <input
          type="text"
          class="form-control rounded-pill border-0 shadow-sm"
          placeholder="요리나 재료를 검색해보세요!"
        />
        <span class="input-group-text bg-white border-0 shadow-sm rounded-pill ms-n5">
          <i class="bi bi-search"></i>
        </span>
      </div>
    </div>

    <!-- 카테고리 버튼 -->
    <div class="d-flex justify-content-center mb-4">
      <button class="btn btn-outline-secondary shadow-sm">
        <i class="bi bi-box me-1"></i>전체
      </button>
    </div>

    <!-- 필터 바 -->
    <div
      class="d-flex justify-content-between align-items-center bg-light p-2 rounded shadow-sm mb-4"
    >
      <div>
        <input type="checkbox" id="onlyMine" class="form-check-input me-2" />
        <label for="onlyMine" class="form-check-label">내 집밥일기만 보기</label>
      </div>
      <button class="btn btn-link text-decoration-none">
        <i class="bi bi-sorta-down-alt me-1"></i>인기순
      </button>
    </div>

    <!-- 게시글 그리드 -->
    <div class="row g-4">
      <div v-for="post in posts" :key="post.id" class="col-md-4">
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
      </div>
    </div>
  </div>
</template>

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
