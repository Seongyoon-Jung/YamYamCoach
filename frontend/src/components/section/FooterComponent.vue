<template>
  <footer class="footer navbar-white bg-white border-top border-3">
    <div class="container py-4">
      <div class="row align-items-center">
        <!-- 회사 정보 -->
        <div class="col-lg-8 mb-3 mb-lg-0 text-start">
          <h5 class="fw-bold mb-3">SSAFYBAPY</h5>
          <p class="text-secondary mb-1 small">건강한 식습관을 위한 최고의 선택</p>
          <p class="text-secondary mb-1 small">대표: 정성윤, 박태원</p>
          <p class="text-secondary mb-1 small">사업자등록번호: 123-45-67890</p>
          <p class="text-secondary mb-0 small">주소: 서울특별시 강남구 테헤란로 212 (역삼동) 멀티캠퍼스</p>
        </div>
        
        <!-- 빠른 링크 -->
        <div class="col-lg-4 text-lg-end">
          <div class="d-flex justify-content-lg-end gap-4">
            <RouterLink to="/" class="text-decoration-none text-secondary small">홈</RouterLink>
            <RouterLink to="/board" class="text-decoration-none text-secondary small">커뮤니티</RouterLink>
            <RouterLink to="/recipe" class="text-decoration-none text-secondary small">레시피</RouterLink>
            <RouterLink to="/news" class="text-decoration-none text-secondary small">뉴스</RouterLink>
          </div>
        </div>
      </div>
      
      <!-- 저작권 -->
      <div class="border-top mt-4 pt-4">
        <p class="text-secondary text-center mb-0 small">
          &copy; {{ new Date().getFullYear() }} SSAFYBAPY. All rights reserved.
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer {
  background-color: #fff !important;
}

.footer a:hover {
  color: var(--bs-primary) !important;
  transition: color 0.2s ease;
}
</style>
