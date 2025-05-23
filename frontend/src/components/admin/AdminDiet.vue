<template>
  <div class="container py-5">
    <h3 class="mb-4">급식 메뉴 수정 및 입력</h3>

    <!-- 날짜 선택 -->
    <div class="mb-3 d-flex align-items-center">
      <button class="btn btn-outline-secondary me-2" @click="changeDate(-1)">◀️</button>
      <input type="date" class="form-control me-2" v-model="selectedDate" />
      <button class="btn btn-outline-secondary" @click="changeDate(1)">▶️</button>
    </div>

    <!-- 중식 A코스 입력 -->
    <div class="mb-3">
      <label class="form-label">A코스</label>
      <textarea
        v-model="menuData.aCourse"
        class="form-control"
        rows="2"
        placeholder="메뉴를 입력해주세요"
      ></textarea>
    </div>

    <!-- 중식 B코스 입력 -->
    <div class="mb-3">
      <label class="form-label">B코스</label>
      <textarea
        v-model="menuData.bCourse"
        class="form-control"
        rows="2"
        placeholder="메뉴를 입력해주세요"
      ></textarea>
    </div>

    <!-- 저장 버튼 -->
    <button class="btn btn-primary" @click="saveMenu">저장</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 오늘 날짜 기본값
const getToday = () => new Date().toISOString().slice(0, 10)
const selectedDate = ref(getToday())

// 메뉴 데이터 (중식 A코스, B코스)
const menuData = ref({
  aCourse: '',
  bCourse: '',
})

// 날짜를 하루 단위로 변경하는 함수
function changeDate(days) {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() + days)
  selectedDate.value = date.toISOString().slice(0, 10)
}

// 저장 처리
function saveMenu() {
  console.log('선택된 날짜:', selectedDate.value)
  console.log('A코스:', menuData.value.aCourse)
  console.log('B코스:', menuData.value.bCourse)
  alert('메뉴가 저장되었습니다.')
}
</script>
