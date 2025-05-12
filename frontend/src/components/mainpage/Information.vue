<template>
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
            <div class="d-inline-block" style="width: 80px; height: 80px">
              <!-- DonutChart 컴포넌트 자리 -->
              <!-- 예) <DonutChart :value="40" :max="60" suffix="분"/> -->
              <div class="chart-placeholder">Donut</div>
            </div>
            <div class="small text-primary mt-1">+30%</div>
          </div>

          <!-- 물 섭취 -->
          <div class="col-6 text-center">
            <div class="text-muted small mb-1">물 섭취</div>
            <div class="d-inline-block" style="width: 80px; height: 80px">
              <!-- DonutChart 컴포넌트 자리 -->
              <!-- 예) <DonutChart :value="0.7" :max="2" suffix="L"/> -->
              <div class="chart-placeholder">Donut</div>
            </div>
            <div class="small text-primary mt-1">+30%</div>
          </div>

          <!-- 오늘 수면 시간 -->
          <div class="col-12">
            <div class="text-muted small mb-1">오늘 수면 시간: 6시간 30분</div>
            <div class="chart-placeholder" style="height: 100px">
              <!-- BarChart 컴포넌트 자리 -->
              Bar Chart
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup></script>
