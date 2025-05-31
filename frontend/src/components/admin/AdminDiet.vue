<template>
  <div class="container py-5">
    <form @submit.prevent="handleSave">
      <!-- 헤더 -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="mb-0">급식 메뉴 수정 및 입력</h3>
        <button class="btn btn-primary" type="submit">
          {{ hasData ? '수정' : '입력' }}
        </button>
      </div>

      <!-- 날짜 선택 -->
      <div class="mb-3 d-flex align-items-center">
        <button class="btn btn-outline-secondary me-2" type="button" @click="changeDate(-1)">
          ◀️
        </button>
        <input type="date" class="form-control me-2" v-model="selectedDate" />
        <button class="btn btn-outline-secondary" type="button" @click="changeDate(1)">▶️</button>
      </div>

      <!-- A코스 & B코스 -->
      <div class="row mb-3">
        <!-- A코스 -->
        <div class="card col-md-6">
          <h6 class="m-3">A코스 대표 이미지</h6>
          <div
            class="w-100 mb-2 border rounded d-flex align-items-center justify-content-center"
            style="height: 120px; background-color: #f8f9fa"
          >
            <img
              v-if="aCourseImageUrl"
              :src="aCourseImageUrl"
              class="img-fluid"
              style="max-height: 100%; max-width: 100%"
            />
            <span v-else class="text-muted">이미지가 없습니다</span>
          </div>

          <label class="form-label">A코스</label>
          <div v-for="(item, index) in menuData.aCourse" :key="'a-' + index" class="mb-2">
            <input
              v-model="menuData.aCourse[index]"
              :placeholder="index === 0 ? '대표 메뉴 (필수)' : `메뉴 ${index + 1}`"
              :required="index === 0"
              type="text"
              class="form-control"
            />
          </div>
          <div class="text-end">
            <button type="button" class="btn btn-sm btn-outline-secondary" @click="addACourse">
              A코스 메뉴 추가
            </button>
          </div>
        </div>

        <!-- B코스 -->
        <div class="card col-md-6">
          <h6 class="m-3">B코스 대표 이미지</h6>
          <div
            class="w-100 mb-2 border rounded d-flex align-items-center justify-content-center"
            style="height: 120px; background-color: #f8f9fa"
          >
            <img
              v-if="bCourseImageUrl"
              :src="bCourseImageUrl"
              class="img-fluid"
              style="max-height: 100%; max-width: 100%"
            />
            <span v-else class="text-muted">이미지가 없습니다</span>
          </div>

          <label class="form-label">B코스</label>
          <div v-for="(item, index) in menuData.bCourse" :key="'b-' + index" class="mb-2">
            <input
              v-model="menuData.bCourse[index]"
              :placeholder="index === 0 ? '대표 메뉴 (필수)' : `메뉴 ${index + 1}`"
              :required="index === 0"
              type="text"
              class="form-control"
            />
          </div>
          <div class="text-end">
            <button type="button" class="btn btn-sm btn-outline-secondary" @click="addBCourse">
              B코스 메뉴 추가
            </button>
          </div>
        </div>
      </div>

      <!-- 이미지 크롭퍼 -->
      <ImageCropper ref="cropperRef" @cropped="handleCropped" />

      <div v-if="latestCroppedImage" class="mb-4 d-flex justify-content-center gap-2">
        <button class="btn btn-outline-primary" type="button" @click="setAsRepresentative('A')">
          A코스 대표 이미지 선택
        </button>
        <button class="btn btn-outline-success" type="button" @click="setAsRepresentative('B')">
          B코스 대표 이미지 선택
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import axios from '@/plugins/axios'
import ImageCropper from '@/components/admin/ImageCropper.vue'

const getToday = () => new Date().toISOString().slice(0, 10)
const selectedDate = ref(getToday())

const defaultMenu = () => Array(7).fill('')
const menuData = ref({ aCourse: defaultMenu(), bCourse: defaultMenu() })

const aCourseImageUrl = ref('')
const bCourseImageUrl = ref('')
const aCourseImageFile = ref(null)
const bCourseImageFile = ref(null)
const aOriginImage = ref('')
const bOriginImage = ref('')
const latestCroppedImage = ref('')
const latestCroppedFile = ref(null)
const cropperRef = ref(null)
const hasData = ref(false)

function changeDate(days) {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() + days)
  selectedDate.value = date.toISOString().slice(0, 10)
}

function addACourse() {
  menuData.value.aCourse.push('')
}

function addBCourse() {
  menuData.value.bCourse.push('')
}

function handleCropped({ dataUrl, file }) {
  latestCroppedImage.value = dataUrl
  latestCroppedFile.value = file
  alert('이미지 크롭 완료! A/B 대표 이미지 버튼을 눌러 등록하세요.')
}

function setAsRepresentative(course) {
  if (!latestCroppedImage.value || !latestCroppedFile.value) {
    alert('먼저 이미지를 크롭해주세요.')
    return
  }

  if (course === 'A') {
    aCourseImageUrl.value = latestCroppedImage.value
    aCourseImageFile.value = latestCroppedFile.value
    aOriginImage.value = latestCroppedImage.value
  } else if (course === 'B') {
    bCourseImageUrl.value = latestCroppedImage.value
    bCourseImageFile.value = latestCroppedFile.value
    bOriginImage.value = latestCroppedImage.value
  }

  latestCroppedImage.value = ''
  latestCroppedFile.value = null
}

const handleSave = async () => {
  await saveMenu(hasData.value ? 'put' : 'post')
}

const saveMenu = async (method = 'put') => {
  // 빈칸도 포함
  const rawA = [...menuData.value.aCourse]
  const rawB = [...menuData.value.bCourse]

  const post = { diet: [] }

  try {
    let imageKeyA = aCourseImageFile.value
    if (aCourseImageFile.value !== aOriginImage.value && aCourseImageFile.value) {
      const uuid = crypto.randomUUID()
      const fileName = `uploads/diet/${uuid}.png`
      const putUrlRes = await axios.get('/api/s3/put-url', { params: { fileName } })
      await fetch(putUrlRes.data, {
        method: 'PUT',
        body: aCourseImageFile.value,
        headers: { 'Content-Type': aCourseImageFile.value.type },
      })
      imageKeyA = fileName
    }

    let imageKeyB = bCourseImageFile.value
    if (bCourseImageFile.value !== bOriginImage.value && bCourseImageFile.value) {
      const uuid = crypto.randomUUID()
      const fileName = `uploads/diet/${uuid}.png`
      const putUrlRes = await axios.get('/api/s3/put-url', { params: { fileName } })
      await fetch(putUrlRes.data, {
        method: 'PUT',
        body: bCourseImageFile.value,
        headers: { 'Content-Type': bCourseImageFile.value.type },
      })
      imageKeyB = fileName
    }

    post.diet.push({ date: selectedDate.value, course: 'A', food: rawA, imgUrl: imageKeyA })
    post.diet.push({ date: selectedDate.value, course: 'B', food: rawB, imgUrl: imageKeyB })
    console.log(post)
    await axios[method]('/api/foods', post)
    aOriginImage.value = aCourseImageFile.value
    bOriginImage.value = bCourseImageFile.value
    if (method === 'post') hasData.value = true
    cropperRef.value?.resetCropper()
    alert('메뉴가 저장되었습니다.')
  } catch (err) {
    console.error('식단 저장 실패:', err)
  }
}

async function fetchMenuByDate(date) {
  try {
    const res = await axios.get('/api/foods', { params: { date } })
    const result = { aCourse: [], bCourse: [] }
    let hasAnyData = false

    for (const entry of res.data) {
      if (entry.course === 'A') {
        hasAnyData = true
        result.aCourse = entry.food
        if (entry.imgUrl) {
          const imgRes = await axios.get('/api/s3/get-url', { params: { filename: entry.imgUrl } })
          aCourseImageUrl.value = imgRes.data
          aCourseImageFile.value = entry.imgUrl
          aOriginImage.value = entry.imgUrl
        }
      } else if (entry.course === 'B') {
        hasAnyData = true
        result.bCourse = entry.food
        if (entry.imgUrl) {
          const imgRes = await axios.get('/api/s3/get-url', { params: { filename: entry.imgUrl } })
          bCourseImageUrl.value = imgRes.data
          bCourseImageFile.value = entry.imgUrl
          bOriginImage.value = entry.imgUrl
        }
      }
    }

    hasData.value = hasAnyData
    result.aCourse = [...result.aCourse, ...Array(7 - result.aCourse.length).fill('')]
    result.bCourse = [...result.bCourse, ...Array(7 - result.bCourse.length).fill('')]
    menuData.value = result
  } catch (err) {
    console.error('메뉴 불러오기 실패:', err)
    hasData.value = false
    menuData.value = { aCourse: defaultMenu(), bCourse: defaultMenu() }
  }
}

watch(selectedDate, (newDate) => {
  aCourseImageUrl.value = ''
  bCourseImageUrl.value = ''
  aCourseImageFile.value = null
  bCourseImageFile.value = null
  cropperRef.value?.resetCropper()
  fetchMenuByDate(newDate)
})

onMounted(() => {
  fetchMenuByDate(selectedDate.value)
})
</script>
