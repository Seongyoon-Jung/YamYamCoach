<template>
  <section class="features-section py-16 bg-gradient-to-r from-green-50 to-blue-50">
    <div class="container mx-auto px-4">
      <h2 class="text-4xl font-bold text-center mb-12 text-gray-800">
        YamYamCoach 프로젝트 소개
      </h2>
      
      <div class="space-y-8">
        <!-- 메인 페이지 -->
        <div class="feature-section bg-white p-8 rounded-xl shadow-lg">
          <div class="flex items-center mb-4">
            <div class="icon-wrapper mr-4">
              <i class="fas fa-home text-4xl text-green-500"></i>
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-gray-800">메인 페이지</h3>
              <code class="text-sm text-gray-500">/</code>
            </div>
          </div>
          <p class="text-gray-600 mb-4">
            YamYamCoach의 시작 페이지입니다. 인기 레시피, 최신 레시피, 추천 레시피를 한눈에 확인할 수 있습니다.
          </p>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
            <div class="bg-gray-50 p-4 rounded-lg">
              <h4 class="font-medium text-gray-700">주요 기능</h4>
              <ul class="list-disc list-inside text-gray-600 mt-2">
                <li>인기 레시피 슬라이더</li>
                <li>최신 레시피 목록</li>
                <li>카테고리별 빠른 검색</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 레시피 페이지 -->
        <div class="feature-section bg-white p-8 rounded-xl shadow-lg">
          <div class="flex items-center mb-4">
            <div class="icon-wrapper mr-4">
              <i class="fas fa-utensils text-4xl text-blue-500"></i>
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-gray-800">레시피</h3>
              <code class="text-sm text-gray-500">/recipe/*</code>
            </div>
          </div>
          <p class="text-gray-600 mb-4">
            다양한 레시피를 검색하고 확인할 수 있는 공간입니다.
          </p>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
            <div class="bg-gray-50 p-4 rounded-lg">
              <h4 class="font-medium text-gray-700">레시피 목록</h4>
              <code class="text-sm text-gray-500">/recipe/list</code>
              <ul class="list-disc list-inside text-gray-600 mt-2">
                <li>필터링 및 검색 기능</li>
                <li>카테고리별 분류</li>
                <li>정렬 옵션</li>
              </ul>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <h4 class="font-medium text-gray-700">레시피 상세</h4>
              <code class="text-sm text-gray-500">/recipe/:id</code>
              <ul class="list-disc list-inside text-gray-600 mt-2">
                <li>조리 단계 가이드</li>
                <li>재료 정보</li>
                <li>좋아요 및 저장</li>
                <li>관련 레시피 추천</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 커뮤니티 -->
        <div class="feature-section bg-white p-8 rounded-xl shadow-lg">
          <div class="flex items-center mb-4">
            <div class="icon-wrapper mr-4">
              <i class="fas fa-comments text-4xl text-yellow-500"></i>
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-gray-800">커뮤니티</h3>
              <code class="text-sm text-gray-500">/board/*</code>
            </div>
          </div>
          <p class="text-gray-600 mb-4">
            요리 경험과 팁을 공유하는 커뮤니티 공간입니다.
          </p>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
            <div class="bg-gray-50 p-4 rounded-lg">
              <h4 class="font-medium text-gray-700">게시판</h4>
              <code class="text-sm text-gray-500">/board/list</code>
              <ul class="list-disc list-inside text-gray-600 mt-2">
                <li>요리 팁 공유</li>
                <li>질문과 답변</li>
                <li>자유 게시판</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 마이페이지 -->
        <div class="feature-section bg-white p-8 rounded-xl shadow-lg">
          <div class="flex items-center mb-4">
            <div class="icon-wrapper mr-4">
              <i class="fas fa-user text-4xl text-purple-500"></i>
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-gray-800">마이페이지</h3>
              <code class="text-sm text-gray-500">/user/*</code>
            </div>
          </div>
          <p class="text-gray-600 mb-4">
            개인화된 사용자 공간입니다.
          </p>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
            <div class="bg-gray-50 p-4 rounded-lg">
              <h4 class="font-medium text-gray-700">프로필 관리</h4>
              <code class="text-sm text-gray-500">/user/profile</code>
              <ul class="list-disc list-inside text-gray-600 mt-2">
                <li>프로필 정보 수정</li>
                <li>저장한 레시피 관리</li>
                <li>활동 내역 확인</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: 'Features',
  mounted() {
    const script = document.createElement('script')
    script.src = 'https://kit.fontawesome.com/your-kit-code.js'
    document.head.appendChild(script)
  }
}
</script>

<style scoped>
.feature-section {
  transition: all 0.3s ease;
}

.feature-section:hover {
  transform: translateY(-2px);
}

.icon-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.05);
}

code {
  padding: 2px 6px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.05);
}
</style> 