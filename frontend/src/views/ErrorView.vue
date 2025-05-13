<!-- src/components/CuteErrorPage.vue -->
<template>
  <div class="error-page d-flex flex-column justify-content-center align-items-center text-center">
    <!-- 귀여운 고양이 이모지 (또는 SVG) -->
    <div class="emoji mb-4">😿</div>
    <h1 class="display-1 fw-bold mb-3">404</h1>
    <h2 class="mb-3">앗! 길을 잃었어요…</h2>
    <p class="mb-4 text-muted">
      요청하신 페이지를 찾을 수 없어요.<br />
      다른 곳으로 돌아가 보실래요?
    </p>
    <router-link to="/" class="btn btn-primary btn-lg">
      <i class="bi bi-house-door-fill me-2"></i>홈으로 가기
    </router-link>
  </div>
</template>

<script>
export default {
  name: 'CuteErrorPage',
}
</script>

<style scoped>
.error-page {
  height: 80vh;
  padding: 2rem;
  background: #f8f9fa;
}

/* 이모지 흔들리는 애니메이션 */
@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  20%,
  60% {
    transform: translateX(-10px);
  }
  40%,
  80% {
    transform: translateX(10px);
  }
}
.emoji {
  font-size: 5rem;
  animation: shake 2s infinite;
}

/* 404 숫자 컬러 강조 */
h1.display-1 {
  color: #ff6b81;
}

/* 버튼 호버 효과 */
.btn-primary {
  transition:
    background-color 0.3s,
    transform 0.3s;
}
.btn-primary:hover {
  background-color: #ff6b81;
  transform: translateY(-2px);
}
</style>
