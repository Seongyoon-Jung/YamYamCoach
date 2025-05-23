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
    <div class="d-flex pt-3 position-relative">
      <!-- ─── 사이드바 ───────────────────────────────────────────────────────────── -->

      <!-- ─── 메인 컨텐츠 ───────────────────────────────────────────────────────── -->
      <main class="flex-grow-1 overflow-auto p-4">
        <!-- 인사+날짜/시간 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2 class="mb-0">
            안녕하세요, <strong>{{ username }}</strong
            >님
          </h2>
        </div>
        <hr />

        <!-- 차트 + 사이드 정보 -->
        <div class="row">
          <!-- 1행 1열: TodayDiet + DinnerRecommendation -->
          <div class="col-md-8 mb-4">
            <div class="row h-466">
              <div class="col-md-6">
                <div class="h-100">
                  <TodayDiet ref="TodayDiet" />
                </div>
              </div>
              <div class="col-md-6">
                <div class="h-100">
                  <DinnerRecommendation ref="DinnerRecommendation" />
                </div>
              </div>
            </div>
          </div>
          <!-- 1행 2열: Information -->
          <div class="col-md-4 mb-0">
            <div class="h-320">
              <Information ref="Information" />
            </div>
          </div>
        </div>

        <div class="row">
          <!-- 2행 1열: MainChart -->
          <div class="col-md-8 mb-4">
            <MainChart ref="MainChart" />
          </div>
          <!-- 2행 2열: Calendar -->
          <div class="col-md-4">
            <Calendar ref="Calendar" />
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
