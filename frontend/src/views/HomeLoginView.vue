<template>
  <div class="d-flex pt-3 position-relative">
    <!-- ─── 사이드바 ───────────────────────────────────────────────────────────── -->
    

    <!-- ─── 메인 컨텐츠 ───────────────────────────────────────────────────────── -->
    <main class=" flex-grow-1 overflow-auto p-4">
      <!-- 인사+날짜/시간 -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">안녕하세요, <strong>{{account.username}}</strong>님</h2>
      </div>
      <hr>

      <!-- 차트 + 사이드 정보 -->
      <div class="row gx-4 mb-4">
        <!-- 주차트 -->
        <div class="col-lg-8 mb-4">
          <div class="card shadow-sm h-100">
            <ul class="nav nav-tabs card-header-tabs px-3">
              <li class="nav-item">
                <a class="nav-link active" href="#">칼로리</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="#">탄수화물</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="#">단백질</a>
              </li>
            </ul>
            <div class="card-body">
              <div class="chart-placeholder d-flex align-items-center justify-content-center" style="height: 300px;">
                <!-- 여기에 차트 컴포넌트 삽입 -->
                Line Chart Here
              </div>
            </div>
          </div>
        </div>
        <!-- 사이드 정보 카드들 -->
        <div class="col-lg-4">
          <div class="row gy-4">
             <div class="col-12">
              <div class="card shadow-sm">
                <div class="card-header bg-success text-white">
                  오늘은 수요일 입니다
                </div>
                <div class="card-body p-3">
                  <small><i class="bi bi-calendar3"></i> 2025-05-09  
                    <i class="bi bi-clock ms-3"></i> 8:55 am</small>
                </div>
              </div>
            </div>

            <div class="col-12">
              <div class="card shadow-sm">
    <!-- 헤더 -->
    <div class="card-header bg-white d-flex justify-content-between align-items-center">
      <h6 class="mb-0">정보</h6>
      <a href="#" class="small text-decoration-none">Edit ✎</a>
    </div>

    <!-- 본문 -->
    <div class="card-body p-3">
      <div class="row gx-3 gy-4">
        <!-- 운동시간 -->
        <div class="col-6 text-center">
          <div class="text-muted small mb-1">운동시간</div>
          <div class="d-inline-block" style="width:80px; height:80px;">
            <!-- DonutChart 컴포넌트 자리 -->
            <!-- 예) <DonutChart :value="40" :max="60" suffix="분"/> -->
            <div class="chart-placeholder">Donut</div>
          </div>
          <div class="small text-primary mt-1">+30%</div>
        </div>

        <!-- 물 섭취 -->
        <div class="col-6 text-center">
          <div class="text-muted small mb-1">물 섭취</div>
          <div class="d-inline-block" style="width:80px; height:80px;">
            <!-- DonutChart 컴포넌트 자리 -->
            <!-- 예) <DonutChart :value="0.7" :max="2" suffix="L"/> -->
            <div class="chart-placeholder">Donut</div>
          </div>
          <div class="small text-primary mt-1">+30%</div>
        </div>

        <!-- 오늘 수면 시간 -->
        <div class="col-12">
          <div class="text-muted small mb-1">오늘 수면 시간: 6시간 30분</div>
          <div class="chart-placeholder" style="height:100px;">
            <!-- BarChart 컴포넌트 자리 -->
            Bar Chart
          </div>
        </div>
      </div>
    </div>
  </div>
            </div>
          </div>
        </div>
      </div>  

      <!-- 오늘의 칼럼 & 오늘의 식단 -->
      <div class="row gx-4">
        <div class="col-md-6 mb-4">
          <div class="card shadow-sm h-100">
            <div class="card-header d-flex justify-content-between align-items-center">
              <span>오늘의 칼럼</span>
              <button class="btn btn-sm btn-outline-success"><i class="bi bi-plus"></i></button>
            </div>
            <img src="https://via.placeholder.com/600x200" class="card-img-top" alt="">
            <div class="card-body">
              <h5 class="card-title">현미가 암을 더 생기게 한다?</h5>
              <p class="card-text text-muted">[송무호의 비건뉴스] 77. 현미와 비소, 왜 문제가 됐나?</p>
            </div>
          </div>
        </div>
        <div class="col-md-6 mb-4">
          <div class="card shadow-sm h-100">
            <div class="card-header d-flex justify-content-between align-items-center">
              <span>오늘의 식단</span>
              <button class="btn btn-sm btn-outline-success"><i class="bi bi-plus"></i></button>
            </div>
            <div class="card-body d-flex gap-3 flex-wrap">
              <div v-for="dish in todayDishes" :key="dish.name" class="text-center" style="width: 120px;">
                <img :src="dish.img" class="rounded mb-2" style="width:100%; height:80px; object-fit:cover" alt="">
                <div class="small">{{ dish.name }}</div>
                <i class="bi bi-heart ms-1"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import store from '@/store';

export default {
  name: 'DashboardView',
  data() {
    return {
      account : {
        username : this.$store.state.account.username
      },
      todayDishes: [
        { name: '된장찌개 + 순대튀김강정', img: 'https://via.placeholder.com/120x80' },
        { name: '차이니즈파게티 + 마늘토스트', img: 'https://via.placeholder.com/120x80' }
      ]
    };
  }
};
</script>

<style scoped>
.chart-placeholder {
  background: #f8f9fa;
  border: 1px dashed #ddd;
  color: #888;
  font-style: italic;
}
</style>
