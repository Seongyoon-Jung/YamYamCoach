<template>
  <main class="mypage-page position-relative pb-5">
    <!-- Page Header -->
    <section class="container pt-5 mt-4">
      <h2 class="fw-bold mb-4">마이페이지</h2>
      <div class="d-flex align-items-center gap-3 mb-4">
        <img
          :src="user.avatarUrl || '/default-avatar.png'"
          alt="Avatar"
          class="rounded-circle"
          width="60"
          height="60"
        />
        <div>
          <h5 class="mb-0">{{ user.username }}</h5>
          <small class="text-muted">구독 {{ user.subscriptions }}</small>
        </div>
        <button class="btn btn-outline-secondary ms-auto">
          <i class="bi bi-gear-fill"></i> 개인정보 수정
        </button>
        <button class="btn btn-link text-secondary p-2">
          <i class="bi bi-three-dots-vertical"></i>
        </button>
      </div>
    </section>

    <!-- Stats Cards -->
    <section class="container mb-4">
      <div class="row g-3">
        <!-- Level Card -->
        <div class="col-lg-8">
          <div class="card p-4 shadow-sm">
            <div class="d-flex align-items-center">
              <div class="level-icon me-3">
                <!-- 임시 헥사곤 + 물고기 아이콘 -->
                <div
                  class="hexagon d-flex align-items-center justify-content-center"
                >
                  <i class="bi bi-fish fs-2 text-primary"></i>
                </div>
              </div>
              <div>
                <div class="d-flex align-items-center mb-2">
                  <span class="badge bg-success me-2">LV.{{ user.level }}</span>
                  <h5 class="mb-0">{{ user.title }}</h5>
                </div>
                <div>{{ user.xp }} XP</div>
                <div class="progress mt-2" style="height: 6px">
                  <div
                    class="progress-bar bg-success"
                    :style="{ width: user.xpProgress + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Coin Card -->
        <div class="col-lg-4">
          <div class="card p-4 shadow-sm text-center">
            <h6 class="text-muted mb-2">내 펄</h6>
            <div class="fs-3 fw-bold text-primary">{{ user.coins }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Quick Actions -->
    <section class="container">
      <div class="row g-3">
        <div class="col-md-6">
          <button class="btn btn-outline-primary w-100 py-3">
            <i class="bi bi-calendar-check-fill me-2"></i> 출석체크
          </button>
        </div>
        <div class="col-md-6">
          <button class="btn btn-outline-primary w-100 py-3">
            <i class="bi bi-people-fill me-2"></i> 커뮤니티 활동
          </button>
        </div>
      </div>
    </section>
  </main>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'MyPage',
  computed: {
    ...mapState({
      user: (state) => ({
        avatarUrl: state.account.avatarUrl,
        username: state.account.username,
        subscriptions: state.account.subscriptions || 0,
        level: state.account.level || 1,
        title: state.account.title || '초보자',
        xp: state.account.xp || 0,
        xpProgress: state.account.xpProgress || 0,
        coins: state.account.coins || 0,
      }),
    }),
  },
};
</script>

<style scoped>
.mypage-page {
  background: #f8f9fa;
  min-height: 100vh;
}

/* floating mission button */
.mission-btn {
  position: absolute;
  top: 1rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

/* hexagon icon */
.hexagon {
  width: 60px;
  height: 60px;
  background: #e9ecef;
  clip-path: polygon(
    25% 5.77%,
    75% 5.77%,
    100% 50%,
    75% 94.23%,
    25% 94.23%,
    0% 50%
  );
  border-radius: 4px;
}
.level-icon .hexagon {
  background: #f1f3f5;
}
</style>
