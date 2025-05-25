<template>
  <!-- 에러 페이지 전용 전체 화면 -->
  <div v-if="route.name === 'Error'" class="error-layout min-vh-100">
    <router-view />
  </div>

  <!-- 일반 사용자 화면: ROLE_ADMIN이 아니거나, 현재 경로가 /admin으로 시작하지 않는 경우 -->
  <div v-else-if="!isAdmin || !isAdminRoute" id="app" class="d-flex flex-column min-vh-100">
    <HeaderComponent />
    <main class="container bg-light flex-grow-1 px-0">
      <router-view />
    </main>
    <FooterComponent />
  </div>

  <!-- 관리자 화면: ROLE_ADMIN이면서, /admin 경로인 경우 -->
  <div v-else id="app" class="d-flex flex-column min-vh-100">
    <AdminHeaderComponent />
    <main class="bg-light flex-grow-1">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import HeaderComponent from '@/components/section/HeaderComponent.vue'
import AdminHeaderComponent from '@/components/section/AdminHeaderComponent.vue'
import FooterComponent from '@/components/section/FooterComponent.vue'
import { useRoute } from 'vue-router'
import { userAccountStore } from '@/store/account'
import { computed, watch } from 'vue'

const route = useRoute()
const accountStore = userAccountStore()

// 관리자 여부
const isAdmin = computed(() => accountStore.role === 'ROLE_ADMIN')

// 현재 경로가 /admin 또는 /admin/로 시작하는지 확인
const isAdminRoute = computed(() => route.path.startsWith('/admin'))

// admin 페이지 접속 시 라이트모드로 전환
watch(
  () => route.path,
  (newPath) => {
    if (newPath.startsWith('/admin')) {
      document.body.classList.remove('dark-mode')
    }
  },
  { immediate: true }
)
</script>

<style>
#error-layout,
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983 !important;
}

/* 다크모드 스타일 */
body.dark-mode {
  background-color: #1a1a1a;
  color: #fff;
}

/* 기본 요소 스타일 */
body.dark-mode #app {
  color: #e0e0e0;
  background-color: #1a1a1a;
}

body.dark-mode .bg-light,
body.dark-mode .bg-white {
  background-color: #2a2a2a !important;
}

/* container 배경색 수정 - navbar, footer 제외 */
body.dark-mode .container:not(.navbar .container):not(footer .container) {
  background-color: #333 !important;
}

body.dark-mode .navbar-white {
  background-color: #2a2a2a !important;
}

/* main 배경색 수정 */
body.dark-mode main,
body.dark-mode main.container {
  background-color: #333 !important;
}

/* 텍스트 색상 */
body.dark-mode .text-dark,
body.dark-mode .nav-link,
body.dark-mode .navbar-nav .nav-link,
body.dark-mode .dropdown-toggle,
body.dark-mode .navbar-brand,
body.dark-mode h1,
body.dark-mode h2,
body.dark-mode h3,
body.dark-mode h4,
body.dark-mode h5,
body.dark-mode h6,
body.dark-mode p,
body.dark-mode span:not(.badge):not(.text-danger):not(.text-success):not(.text-info),
body.dark-mode label,
body.dark-mode .form-label {
  color: #e0e0e0 !important;
}

/* 중요 텍스트 색상 - 더 높은 우선순위를 위해 마지막에 배치 */
body.dark-mode .text-danger,
body.dark-mode span.text-danger,
body.dark-mode .warning.text-danger {
  color: #ff4d4d !important;
}

body.dark-mode .text-success,
body.dark-mode span.text-success,
body.dark-mode .warning.text-success {
  color: #4caf50 !important;
}

body.dark-mode .text-info,
body.dark-mode span.text-info,
body.dark-mode .warning.text-info {
  color: #5bc0de !important;
}

body.dark-mode .text-black {
  color: #fff !important;
}

body.dark-mode .text-black-50 {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 카드 스타일 */
body.dark-mode .card {
  background-color: #2a2a2a;
  border-color: #404040;
}

body.dark-mode .card-header {
  background-color: #333;
  border-bottom-color: #444;
}

body.dark-mode .card-footer {
  background-color: #333;
  border-top-color: #444;
}

/* 테이블 스타일 */
body.dark-mode .table {
  color: #e0e0e0;
}

body.dark-mode .table td,
body.dark-mode .table th {
  border-color: #444;
}

/* 입력 필드 스타일 */
body.dark-mode .form-control,
body.dark-mode .form-select {
  background-color: #333;
  border-color: #404040;
  color: #fff;
}

body.dark-mode .form-control:focus,
body.dark-mode .form-select:focus {
  background-color: #404040;
  border-color: #666;
  color: #fff;
}

/* 드롭다운 메뉴 */
body.dark-mode .dropdown-menu {
  background-color: #2a2a2a;
  border-color: #444;
}

body.dark-mode .dropdown-item {
  color: #e0e0e0;
}

body.dark-mode .dropdown-item:hover,
body.dark-mode .dropdown-item:focus {
  background-color: #404040;
  color: #fff;
}

body.dark-mode .dropdown-divider {
  border-color: #444;
}

/* 모달 스타일 */
body.dark-mode .modal-content {
  background-color: #2a2a2a;
  color: #e0e0e0;
}

body.dark-mode .modal-header,
body.dark-mode .modal-footer {
  border-color: #444;
}

/* 버튼 스타일 */
body.dark-mode .btn-outline-dark {
  color: #e0e0e0;
  border-color: #e0e0e0;
}

body.dark-mode .btn-outline-dark:hover {
  background-color: #e0e0e0;
  color: #2a2a2a;
}

body.dark-mode .btn-light {
  background-color: #404040;
  border-color: #444;
  color: #e0e0e0;
}

body.dark-mode .btn-light:hover {
  background-color: #505050;
  border-color: #666;
  color: #fff;
}

/* 리스트 그룹 스타일 */
body.dark-mode .list-group-item {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

/* 배지 스타일 */
body.dark-mode .badge.bg-light:not(.news-badge) {
  background-color: #404040 !important;
  color: #e0e0e0 !important;
}

/* 나의 맞춤뉴스 배지 스타일 */
body.dark-mode .news-badge {
  background-color: #333 !important;
}

/* 보더 색상 */
body.dark-mode .border,
body.dark-mode .border-top,
body.dark-mode .border-bottom,
body.dark-mode .border-start,
body.dark-mode .border-end {
  border-color: #444 !important;
}

/* 네비게이션 탭 */
body.dark-mode .nav-tabs {
  border-color: #444;
}

body.dark-mode .nav-tabs .nav-link {
  color: #e0e0e0;
}

body.dark-mode .nav-tabs .nav-link.active {
  background-color: #2a2a2a;
  border-color: #444;
  color: #fff;
}

/* 페이지네이션 */
body.dark-mode .pagination .page-link {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

body.dark-mode .pagination .page-item.active .page-link {
  background-color: #0d6efd;
  border-color: #0d6efd;
  color: #fff;
}

/* 알림 메시지 */
body.dark-mode .alert {
  background-color: #2a2a2a;
  border-color: #444;
  color: #e0e0e0;
}

/* 툴팁 */
body.dark-mode .tooltip .tooltip-inner {
  background-color: #404040;
  color: #e0e0e0;
}

/* 스크롤바 스타일 */
body.dark-mode ::-webkit-scrollbar {
  width: 12px;
}

body.dark-mode ::-webkit-scrollbar-track {
  background: #1a1a1a;
}

body.dark-mode ::-webkit-scrollbar-thumb {
  background-color: #444;
  border-radius: 6px;
  border: 3px solid #1a1a1a;
}

body.dark-mode ::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}

/* 텍스트 스타일 추가 */
body.dark-mode strong,
body.dark-mode b,
body.dark-mode .fw-bold,
body.dark-mode .fw-bolder {
  color: #fff !important;
}

body.dark-mode .text-muted {
  color: #9ca3af !important;
}

/* 프로그레스 바 스타일 */
body.dark-mode .progress {
  background-color: #404040;
}

body.dark-mode .progress-bar {
  background-color: #0d6efd;
}

/* 캘린더 스타일 */
body.dark-mode .calendar-modern-container {
  background-color: #2a2a2a !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

body.dark-mode .calendar-modern-day {
  color: #9ca3af !important;
}

body.dark-mode .calendar-modern-cell {
  color: #e0e0e0 !important;
}

body.dark-mode .calendar-modern-cell.other-month {
  color: #666 !important;
}

body.dark-mode .calendar-modern-cell.today {
  color: #0d6efd !important;
}

body.dark-mode .calendar-modern-cell.selected {
  background-color: rgba(13, 110, 253, 0.2);
}

body.dark-mode .calendar-modern-header {
  color: #e0e0e0;
}

body.dark-mode .calendar-nav-btn {
  color: #e0e0e0;
}

body.dark-mode .calendar-month-label {
  color: #fff;
}

/* 추가 텍스트 강조 스타일 */
body.dark-mode .small {
  color: #9ca3af;
}

body.dark-mode .text-warning {
  color: #f59e0b !important;
}

body.dark-mode .text-info {
  color: #5bc0de !important;
}

body.dark-mode .text-primary {
  color: #60a5fa !important;
}

/* 폼 유효성 검사 메시지 */
body.dark-mode .invalid-feedback {
  color: #ef4444;
}

body.dark-mode .valid-feedback {
  color: #10b981;
}

/* 체크박스와 라디오 버튼 */
body.dark-mode .form-check-input {
  background-color: #404040;
  border-color: #666;
}

body.dark-mode .form-check-input:checked {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

/* 인풋 그룹 */
body.dark-mode .input-group-text {
  background-color: #404040;
  border-color: #444;
  color: #e0e0e0;
}

/* 아코디언 */
body.dark-mode .accordion-item {
  background-color: #2a2a2a;
  border-color: #444;
}

body.dark-mode .accordion-button {
  background-color: #2a2a2a;
  color: #e0e0e0;
}

body.dark-mode .accordion-button:not(.collapsed) {
  background-color: #333;
  color: #fff;
}

body.dark-mode .accordion-button::after {
  filter: invert(1);
}

/* 뱃지 추가 스타일 */
body.dark-mode .badge.bg-secondary {
  background-color: #4b5563 !important;
}

body.dark-mode .badge.bg-success {
  background-color: #059669 !important;
}

body.dark-mode .badge.bg-danger {
  background-color: #dc2626 !important;
}

body.dark-mode .badge.bg-warning {
  background-color: #d97706 !important;
  color: #fff !important;
}

body.dark-mode .badge.bg-info {
  background-color: #0284c7 !important;
}

/* 커스텀 스크롤바 스타일 개선 */
body.dark-mode .custom-scrollbar::-webkit-scrollbar {
  width: 8px;
}

body.dark-mode .custom-scrollbar::-webkit-scrollbar-track {
  background: #1a1a1a;
}

body.dark-mode .custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #444;
  border-radius: 4px;
}

body.dark-mode .custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}

/* 뉴스 컴포넌트 스타일 */
body.dark-mode .news-card {
  background-color: #2a2a2a;
  border-color: #444;
}

body.dark-mode .news-card__img-wrapper {
  background-color: #333;
}

body.dark-mode .news-card__body {
  color: #e0e0e0;
}

body.dark-mode .news-card .card-title {
  color: #fff;
}

body.dark-mode .news-card .card-text {
  color: #9ca3af;
}

/* 뉴스 섹션 스타일 */
body.dark-mode [style*="background-color: #eaeef3"] {
  background-color: #1a1a1a !important;
}

body.dark-mode [style*="background: rgba(255, 255, 255, 0.85)"] {
  background: rgba(26, 26, 26, 0.85) !important;
}

/* 뉴스 네비게이션 버튼 */
body.dark-mode .btn-outline-secondary {
  color: #fff;
  border-color: #666;
}

body.dark-mode .btn-outline-secondary:hover:not(:disabled) {
  background-color: #404040;
  border-color: #888;
}

body.dark-mode .btn-outline-secondary:disabled {
  color: #666;
  border-color: #444;
}

/* 뉴스 카드 호버 효과 */
body.dark-mode .news-card:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.3);
}

/* 뉴스 로딩 메시지 */
body.dark-mode .loading-overlay {
  background-color: rgba(26, 26, 26, 0.6);
}

body.dark-mode .spinner {
  border-color: #333;
  border-top-color: #0d6efd;
}

/* 뉴스 섹션 탭 */
body.dark-mode .section-news-tab {
  background-color: #2a2a2a;
}

body.dark-mode .section-news-tab:hover {
  background-color: #333;
}

/* 뉴스 이미지 래퍼 */
body.dark-mode .card-img-wrapper {
  background-color: #2a2a2a !important;
}

/* 뉴스 날짜 텍스트 */
body.dark-mode .news-date {
  color: #9ca3af;
}

/* 뉴스 설명 텍스트 */
body.dark-mode .text-truncate-1,
body.dark-mode .text-truncate-2,
body.dark-mode .text-truncate-3 {
  color: #9ca3af;
}

/* 뉴스 링크 스타일 */
body.dark-mode a.text-decoration-none {
  color: inherit;
}

body.dark-mode a.text-decoration-none:hover {
  color: #fff;
}

/* 뉴스 페이지 배경 */
body.dark-mode .bg-light.flex-grow-1 {
  background-color: #1a1a1a !important;
}

/* 뉴스 카드 그림자 */
body.dark-mode .shadow-sm {
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.3) !important;
}

/* 뉴스 버튼 스타일 */
body.dark-mode .btn-success {
  background-color: #059669;
  border-color: #047857;
}

body.dark-mode .btn-success:hover {
  background-color: #047857;
  border-color: #065f46;
}

/* 뉴스 disabled 오버레이 */
body.dark-mode .disabled-overlay {
  filter: blur(4px) brightness(0.7);
}

/* 뉴스 끝 메시지 */
body.dark-mode .w-100.py-3.text-center span {
  color: #9ca3af;
}

/* 네비게이션 링크 다크모드 스타일 */
body.dark-mode nav a,
body.dark-mode .navbar-nav .nav-link {
  color: #e0e0e0;
}

body.dark-mode nav a.router-link-exact-active,
body.dark-mode .navbar-nav .router-link-active,
body.dark-mode .navbar-nav .router-link-exact-active {
  color: #42b983 !important;
}

/* 뉴스 헤더 스타일 */
body.dark-mode .news-header {
  background-color: #2a2a2a !important;
}

body.dark-mode .survey-notice {
  background-color: #FFFFFFD9 !important;
}
</style>
