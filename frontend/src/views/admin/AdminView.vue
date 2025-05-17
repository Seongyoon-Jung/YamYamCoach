<template>
  <div class="admin-page">
    <div class="container py-5">
      <h2 class="mb-4">관리자 페이지</h2>
      
      <!-- 테스트 데이터 생성 섹션 -->
      <div class="card mb-4">
        <div class="card-header">
          <h5>테스트 데이터 관리</h5>
        </div>
        <div class="card-body">
          <div class="mb-3">
            <button class="btn btn-primary me-2" @click="generateUserDietHistory">
              사용자 식단 기록 생성 (지난 4일)
            </button>
            <div v-if="dataGenerationResult" class="mt-3 alert" :class="{'alert-success': dataGenerationSuccess, 'alert-danger': !dataGenerationSuccess}" role="alert">
              {{ dataGenerationResult }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- OCR 이미지 업로드 섹션 -->
      <div class="card mb-4">
        <div class="card-header">
          <h5>이미지 OCR</h5>
        </div>
        <div class="card-body">
          <!-- 이미지 업로드 -->
          <input type="file" accept="image/*" @change="handleFile" class="form-control mb-3" />

          <!-- 미리보기 -->
          <div v-if="previewUrl" class="mb-3 text-center">
            <img :src="previewUrl" alt="미리보기" class="img-fluid" style="max-height: 300px" />
          </div>

          <!-- 추출된 텍스트 -->
          <div v-if="text" class="alert alert-info">{{ text }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue'
import Tesseract from 'tesseract.js'
import axios from '@/plugins/axios'

// OCR 관련 상태
const imageFile = ref(null)
const previewUrl = ref('')
const text = ref('')

// 테스트 데이터 생성 관련 상태
const dataGenerationResult = ref('')
const dataGenerationSuccess = ref(false)

// 파일 선택 시
const handleFile = (e) => {
  const file = e.target.files[0]
  if (!file) return

  imageFile.value = file
  previewUrl.value = URL.createObjectURL(file)

  extractText(file)
}

// OCR 처리
const extractText = async (file) => {
  text.value = '텍스트 추출 중...'
  try {
    const {
      data: { text: result },
    } = await Tesseract.recognize(file, 'eng+kor', {
      logger: (m) => console.log(m), // 진행 상황 로깅
    })
    text.value = result
  } catch (err) {
    text.value = '텍스트 추출 실패'
    console.error(err)
  }
}

// 사용자 식단 기록 생성
const generateUserDietHistory = async () => {
  try {
    // 로딩 상태 표시
    dataGenerationResult.value = '식단 기록 생성 중...'
    dataGenerationSuccess.value = false
    
    // 지난 4일간의 식단 기록을 직접 생성하기 위한 API 호출
    const response = await axios.post('/api/admin/generate-diet-history', {
      userId: 1,
      days: 4
    })
    
    // 성공 메시지 표시
    dataGenerationResult.value = response.data.message || '사용자 식단 기록이 성공적으로 생성되었습니다.'
    dataGenerationSuccess.value = true
  } catch (err) {
    console.error('식단 기록 생성 오류:', err)
    dataGenerationResult.value = err.response?.data?.message || '식단 기록 생성 중 오류가 발생했습니다.'
    dataGenerationSuccess.value = false
  }
}

// 미리보기 URL 해제
onBeforeUnmount(() => {
  if (previewUrl.value) URL.revokeObjectURL(previewUrl.value)
})
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.card {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

.btn-primary:hover {
  background-color: #0069d9;
  border-color: #0062cc;
}
</style>
