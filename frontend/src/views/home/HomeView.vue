<template>
  <div v-if="!isLoggedIn">
    <!-- Hero Section -->
    <header class="hero">
      <div class="hero-overlay text-center">
        <div class="container">
          <h1 class="display-4 fw-bold">당신만을 위한 식단 코치</h1>
          <p class="lead mt-3 mb-4">재미있는 설문을 통해 나에게 꼭 맞는 식습관을 찾아보세요!</p>
          <router-link to="/login" class="btn btn-lg btn-success" v-if="username == ''"
            >로그인 하기{{ username }}</router-link
          >
          <router-link
            to="/survey"
            class="btn btn-lg btn-success"
            v-if="username != '' && !isSurveyed"
            >지금 설문하기</router-link
          >
        </div>
      </div>
    </header>

    <!-- Features Section -->
    <section class="py-5 bg-light">
      <div class="container">
        <h2 class="section-title text-center mb-5">냠냠코치 기능</h2>
        <div class="row text-center">
          <div class="col-md-4">
            <div class="mb-3 fs-1">🥗</div>
            <h5>개인 맞춤형 식단</h5>
            <p>설문을 기반으로 나의 건강 상태에 맞는 식단을 추천해줍니다.</p>
          </div>
          <div class="col-md-4">
            <div class="mb-3 fs-1">📊</div>
            <h5>식습관 분석</h5>
            <p>흥미롭게 식습관을 진단하고 결과를 시각화합니다.</p>
          </div>
          <div class="col-md-4">
            <div class="mb-3 fs-1">📅</div>
            <h5>일간/주간 관리</h5>
            <p>나의 식단을 캘린더로 관리하고, 쉽게 실천할 수 있어요.</p>
          </div>
        </div>
      </div>
    </section>
  </div>

  <div v-else>
    <div class="d-flex position-relative">
      <!-- ─── 사이드바 ───────────────────────────────────────────────────────────── -->

      <!-- ─── 메인 컨텐츠 ───────────────────────────────────────────────────────── -->
      <main class="flex-grow-1 p-4">
        <!-- 인사+날짜/시간 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2 class="mb-0">
            안녕하세요, <strong>{{ username }}</strong
            >님
          </h2>
        </div>
        <hr />

        <!-- 차트 + 사이드 정보 -->
        <!-- 전체를 화면 높이만큼 채움 -->
        <div class="row gx-3">
          <!-- 왼쪽 (8칸) -->
          <div class="col-md-8 d-flex flex-column" style="gap: 1rem">
            <!-- TodayDiet + DinnerRecommendation (가로 1:1) -->
            <div class="d-flex" style="flex: 2; gap: 1rem">
              <div class="flex-fill">
                <TodayDiet ref="TodayDiet" class="h-100" />
              </div>
              <div class="flex-fill">
                <DinnerRecommendation ref="DinnerRecommendation" class="h-100" />
              </div>
            </div>

            <!-- MainChart -->
            <div style="flex: 3">
              <MainChart ref="MainChart" class="h-100" />
            </div>
          </div>

          <!-- 오른쪽 (4칸) -->
          <div class="col-md-4 d-flex flex-column" style="gap: 1rem">
            <div style="flex: 1">
              <Information ref="Information" class="h-100" />
            </div>
            <div style="flex: 3">
              <Calendar ref="Calendar" class="h-100" />
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import MainChart from '@/components/mainpage/MainChart.vue'
import Calendar from '@/components/mainpage/Calendar.vue'
import Information from '@/components/mainpage/Information.vue'
import TodayColumn from '@/components/mainpage/TodayColumn.vue'
import TodayDiet from '@/components/mainpage/TodayDiet.vue'
import DinnerRecommendation from '@/components/mainpage/DinnerRecommendation.vue'

import { computed } from 'vue'
import { userAccountStore } from '@/store/account'

// pinia store을 사용하겠다
const accountStore = userAccountStore()

const username = computed(() => accountStore.username)
const isSurveyed = computed(() => accountStore.isSurveyed)
const isLoggedIn = computed(() => !!username.value)
</script>

<!-- scoped는 이 페이지에서만 css를 설정할때 씀 -->
<style scoped>
.hero {
  background: url('https://images.unsplash.com/photo-1551218808-94e220e084d2') no-repeat center
    center;
  background-size: cover;
  color: white;
  height: 100vh;
  display: flex;
  align-items: center;
}

.hero-overlay {
  background-color: rgba(0, 0, 0, 0.5);
  padding: 80px 20px;
  width: 100%;
}

.section-title {
  font-size: 2rem;
  font-weight: 600;
}

.h-466 {
  height: 466px;
}

.h-320 {
  height: 320px;
}

.h-100 {
  height: 100%;
}
</style>