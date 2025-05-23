<template>
  <div class="container py-5">
    <h3 class="mb-4">급식 메뉴 수정 및 입력</h3>

    <!-- 날짜 선택 -->
    <div class="mb-3 d-flex align-items-center">
      <button class="btn btn-outline-secondary me-2" @click="changeDate(-1)">◀️</button>
      <input type="date" class="form-control me-2" v-model="selectedDate" />
      <button class="btn btn-outline-secondary" @click="changeDate(1)">▶️</button>
    </div>
    <!-- A코스 & B코스 입력 필드 -->
    <div class="row mb-3">
      <!-- A코스 -->
      <div class="card col-md-6">
        <h6 class="m-3">A코스 대표 이미지</h6>
        <img v-if="aCourseImage" :src="aCourseImage" class="img-fluid rounded border h-50" />
        <br />
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
      <div class="card col-md-6">
        <h6 class="m-3">B코스 대표 이미지</h6>
        <img v-if="bCourseImage" :src="bCourseImage" class="img-fluid rounded border h-50" />
        <br />
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

    <!-- 크롭 컴포넌트 -->
    <ImageCropper ref="cropperRef" @cropped="handleCropped" />

    <div v-if="latestCroppedImage" class="mb-4 d-flex justify-content-center gap-2">
      <button class="btn btn-outline-primary" @click="setAsRepresentative('A')">
        A코스 대표 이미지 선택
      </button>
      <button class="btn btn-outline-success" @click="setAsRepresentative('B')">
        B코스 대표 이미지 선택
      </button>
    </div>

    <!-- 저장 버튼 -->
    <button class="btn btn-primary mt-4" @click="saveMenu">저장</button>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import axios from '@/plugins/axios'
import ImageCropper from '@/components/admin/ImageCropper.vue'

// 날짜 관련
const getToday = () => new Date().toISOString().slice(0, 10)
const selectedDate = ref(getToday())

// 메뉴 기본 구조
const defaultMenu = () => Array(7).fill('')
const menuData = ref({
  aCourse: defaultMenu(),
  bCourse: defaultMenu(),
})

// 대표 이미지 상태
const aCourseImage = ref('')
const bCourseImage = ref('')
const latestCroppedImage = ref('') // 최근 크롭된 이미지 저장
const cropperRef = ref(null)

// 날짜 이동
function changeDate(days) {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() + days)
  selectedDate.value = date.toISOString().slice(0, 10)
}

// 크롭 후 이미지 받기
function handleCropped({ dataUrl }) {
  latestCroppedImage.value = dataUrl
  alert('이미지 크롭 완료! A/B 대표 이미지 버튼을 눌러 등록하세요.')
}

// A코스 또는 B코스 대표 이미지 설정
function setAsRepresentative(course) {
  if (!latestCroppedImage.value) {
    alert('먼저 이미지를 크롭해주세요.')
    return
  }

  if (course === 'A') {
    aCourseImage.value = latestCroppedImage.value
  } else if (course === 'B') {
    bCourseImage.value = latestCroppedImage.value
  }
  latestCroppedImage.value = ''
}

// 저장 처리
function saveMenu() {
  const cleanedA = menuData.value.aCourse.filter((item) => item.trim() !== '')
  const cleanedB = menuData.value.bCourse.filter((item) => item.trim() !== '')

  console.log('날짜:', selectedDate.value)
  console.log('A코스:', cleanedA)
  console.log('B코스:', cleanedB)
  console.log('A코스 대표 이미지:', aCourseImage.value)
  console.log('B코스 대표 이미지:', bCourseImage.value)

  alert('메뉴가 저장되었습니다.')
}

// 날짜별 메뉴 불러오기
async function fetchMenuByDate(date) {
  try {
    const res = await axios.get('/api/foods', { params: { date } })

    const result = {
      aCourse: [],
      bCourse: [],
    }

    res.data.forEach((entry) => {
      if (entry.course === 'A') result.aCourse = entry.food
      if (entry.course === 'B') result.bCourse = entry.food
    })

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

// 날짜 변경 시 호출
watch(selectedDate, (newDate) => {
  aCourseImage.value = null
  bCourseImage.value = null
  latestCroppedImage.value = ''
  cropperRef.value?.resetCropper()
  fetchMenuByDate(newDate)
})

// 최초 로딩
onMounted(() => {
  fetchMenuByDate(selectedDate.value)
})
</script>
