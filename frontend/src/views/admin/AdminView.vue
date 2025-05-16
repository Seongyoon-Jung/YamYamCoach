<template>
  <div class="container py-4">
    <!-- 이미지 업로드 -->
    <input type="file" accept="image/*" @change="handleFile" class="form-control mb-3" />

    <!-- 미리보기 -->
    <div v-if="previewUrl" class="mb-3 text-center">
      <img :src="previewUrl" alt="미리보기" class="img-fluid" style="max-height: 300px" />
    </div>

    <!-- 추출된 텍스트 -->
    <div v-if="text" class="alert alert-info">{{ text }}</div>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue'
import Tesseract from 'tesseract.js'

const imageFile = ref(null)
const previewUrl = ref('')
const text = ref('')

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

// 미리보기 URL 해제
onBeforeUnmount(() => {
  if (previewUrl.value) URL.revokeObjectURL(previewUrl.value)
})
</script>
