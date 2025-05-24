<!-- src/layouts/AdminLayout.vue -->
<template>
  <div class="d-flex">
    <!-- 왼쪽 사이드바 -->
    <nav class="bg-dark text-white p-3" style="width: 200px; min-height: 100vh">
      <h4 class="mb-4">Admin</h4>
      <ul class="nav flex-column">
        <li class="nav-item mb-2">
          <router-link to="/admin" class="nav-link text-white">점심 메뉴 관리</router-link>
        </li>
        <li class="nav-item mb-2">
          <router-link to="/admin/diet-upload" class="nav-link text-white"
            >점심 메뉴 자동 입력</router-link
          >
        </li>
        <!-- 더 추가 가능 -->
      </ul>
    </nav>

    <!-- 오른쪽 컨텐츠 영역 -->
    <div class="flex-grow-1 p-4">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
nav {
  position: sticky;
  top: 0;
}
</style>
