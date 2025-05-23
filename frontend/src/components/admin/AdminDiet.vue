<template>
  <div class="container py-5">
    <h3 class="mb-4">급식 메뉴 수정 및 입력</h3>

    <!-- 날짜 선택 -->
    <div class="mb-3 d-flex align-items-center">
      <button class="btn btn-outline-secondary me-2" @click="changeDate(-1)">◀️</button>
      <input type="date" class="form-control me-2" v-model="selectedDate" />
      <button class="btn btn-outline-secondary" @click="changeDate(1)">▶️</button>
    </div>

    <div class="row mb-3">
      <!-- A코스 -->
      <div class="col-md-6">
        <label class="form-label">A코스</label>
        <div v-for="(item, index) in menuData.aCourse" :key="'a-' + index" class="mb-2">
          <input
            v-model="menuData.aCourse[index]"
            type="text"
            class="form-control"
            :placeholder="`A코스 메뉴 ${index + 1}`"
          />
        </div>
      </div>

      <!-- B코스 -->
      <div class="col-md-6">
        <label class="form-label">B코스</label>
        <div v-for="(item, index) in menuData.bCourse" :key="'b-' + index" class="mb-2">
          <input
            v-model="menuData.bCourse[index]"
            type="text"
            class="form-control"
            :placeholder="`B코스 메뉴 ${index + 1}`"
          />
        </div>
      </div>
    </div>

    <!-- 저장 버튼 -->
    <button class="btn btn-primary" @click="saveMenu">저장</button>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import axios from '@/plugins/axios'

// 오늘 날짜를 YYYY-MM-DD 형식으로 반환
const getToday = () => new Date().toISOString().slice(0, 10)
const selectedDate = ref(getToday())

// 기본 7칸 메뉴 필드
const defaultMenu = () => Array(7).fill('')

// 메뉴 데이터 (A코스, B코스는 각각 7칸으로 초기화)
const menuData = ref({
  aCourse: defaultMenu(),
  bCourse: defaultMenu(),
})

// 날짜 하루 단위 변경
function changeDate(days) {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() + days)
  selectedDate.value = date.toISOString().slice(0, 10)
}

// 저장 처리
function saveMenu() {
  const cleanedA = menuData.value.aCourse.filter((item) => item.trim() !== '')
  const cleanedB = menuData.value.bCourse.filter((item) => item.trim() !== '')

  console.log('선택된 날짜:', selectedDate.value)
  console.log('A코스:', cleanedA)
  console.log('B코스:', cleanedB)
  alert('메뉴가 저장되었습니다.')
}

// 날짜별 메뉴 불러오기
async function fetchMenuByDate(date) {
  try {
    const res = await axios.get('/api/foods', {
      params: { date },
    })

    const result = {
      aCourse: [],
      bCourse: [],
    }

    res.data.forEach((entry) => {
      if (entry.course === 'A') result.aCourse = entry.food
      if (entry.course === 'B') result.bCourse = entry.food
    })

    // 부족한 경우 7칸 채우기
    if (result.aCourse.length < 7) {
      result.aCourse = [...result.aCourse, ...Array(7 - result.aCourse.length).fill('')]
    }

    if (result.bCourse.length < 7) {
      result.bCourse = [...result.bCourse, ...Array(7 - result.bCourse.length).fill('')]
    }

    menuData.value = result
  } catch (err) {
    console.error('메뉴 로딩 실패:', err)
    menuData.value = {
      aCourse: defaultMenu(),
      bCourse: defaultMenu(),
    }
  }
}

// 날짜 변경 시 메뉴 불러오기
watch(selectedDate, (newDate) => {
  fetchMenuByDate(newDate)
})

// 최초 진입 시 오늘 날짜 기준 메뉴 불러오기
onMounted(() => {
  fetchMenuByDate(selectedDate.value)
})
</script>
