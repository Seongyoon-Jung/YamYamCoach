<template>
  <div class="dashboard">
    <!-- Profile Section -->
    <section class="profile-row py-4 bg-white border-bottom">
      <div class="container">
        <div class="d-flex align-items-center gap-3">
          <img
            :src="user.avatarUrl || '/default-avatar.png'"
            alt="Profile"
            class="rounded-circle"
            width="60"
            height="60"
          />
          <div>
            <span class="mb-0 fs-2">{{ user.username }}</span>
            <span class="fs-5"> 님 안녕하세요!</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Cards -->
    <section class="stats-row py-4 bg-white">
      <div class="container">
        <div class="row g-3">
          <div class="col-md-3" v-for="(stat, i) in stats" :key="i">
            <div class="card text-center shadow-sm p-3">
              <div class="stat-label text-uppercase text-muted mb-2">
                {{ stat.label }}
              </div>
              <div class="stat-value h2 fw-bold">
                {{ stat.value }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Big Charts -->
    <section class="charts-row py-4 bg-light">
      <div class="container">
        <div class="row g-3">
          <div class="col-lg-8">
            <div class="card shadow-sm p-3">
              <div class="card-header">
                <h5 class="mb-0">월별 식습관 지표</h5>
              </div>
              <div
                class="chart-placeholder d-flex align-items-center justify-content-center"
              >
                <!-- 여기에 실제 LineChart 컴포넌트 삽입 -->
                차트 영역
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="card shadow-sm p-3">
              <div class="card-header">
                <h5 class="mb-0">진행률</h5>
              </div>
              <div
                class="chart-placeholder d-flex align-items-center justify-content-center"
              >
                <!-- 여기에 실제 DonutChart 컴포넌트 삽입 -->
                도넛 차트
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Small Charts / Info Cards -->
    <section class="small-charts-row py-4 bg-white">
      <div class="container">
        <div class="row g-3">
          <div class="col-md-3" v-for="n in 4" :key="n">
            <div class="card shadow-sm p-3">
              <div class="card-header">
                <h6 class="mb-0">세부 지표 {{ n }}</h6>
              </div>
              <div
                class="chart-placeholder small-chart d-flex align-items-center justify-content-center"
              >
                <!-- 여기에 실제 SmallChart 컴포넌트 삽입 -->
                작은 차트
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card shadow-sm p-3 h-100">
              <div class="card-header">
                <h6 class="mb-0">알림 및 진행</h6>
              </div>
              <ul class="list-unstyled mb-0">
                <li
                  v-for="i in 3"
                  :key="i"
                  class="d-flex justify-content-between align-items-center py-2 border-bottom"
                >
                  <span>알림 항목 {{ i }}</span>
                  <button class="btn btn-sm btn-outline-primary">보기</button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'DashboardView',
  computed: {
    ...mapState({
      user: (state) => state.account, // { username, avatarUrl, role, ... }
    }),
  },
  data() {
    return {
      user: {
        username: this.$store.state.account.username,
      },
      stats: [
        { label: '완료율', value: '87%' },
        { label: '활동 수', value: '1,503' },
        { label: '칼로리', value: '2,450' },
        { label: '걸음 수', value: '4,220' },
      ],
    };
  },
};
</script>

<style scoped>
/* .profile-row {
    border-bottom: 1px solid #e9ecef;
  } */
</style>
