<template>
  <div class="container my-4" @dragover.prevent @drop.prevent="onDrop">
    <!-- 파일 선택 버튼 -->
    <input
      type="file"
      accept="image/*"
      ref="fileInput"
      @change="onFileChange"
      style="display: none"
    />
    <!-- 사진 선택 + 크롭 버튼을 나란히 정렬 -->
    <div v-if="imageUrl" class="mb-4 d-flex justify-content-center gap-2">
      <button type="button" class="btn btn-outline-primary" @click="triggerFileSelect">
        사진 선택 또는 드래그&드롭
      </button>
      <button type="button" class="btn btn-primary" @click="cropImage">크롭</button>
    </div>

    <!-- 사진 선택만 있을 경우 (초기 화면) -->
    <div v-else class="mb-4 text-center">
      <button type="button" class="btn btn-outline-primary" @click="triggerFileSelect">
        사진 선택 또는 드래그&드롭
      </button>
    </div>

    <!-- Cropper + 크롭된 이미지 나란히 -->
    <div v-if="imageUrl" class="row mb-3 justify-content-center align-items-start">
      <!-- 왼쪽: Cropper -->
      <div class="col-md-6 p-2 border rounded overflow-hidden text-center">
        <Cropper
          ref="cropper"
          :src="imageUrl"
          :view-mode="1"
          :guides="true"
          :auto-crop-area="0.8"
          :drag-mode="'crop'"
          :background="false"
          :zoomable="true"
          :scalable="true"
          style="max-height: 400px; width: 100%; display: block"
        />
      </div>

      <!-- 오른쪽: 크롭된 이미지 미리보기 -->
      <div v-if="croppedImageUrl" class="col-md-5 text-center p-2">
        <h5 class="mb-3">크롭된 이미지</h5>
        <img
          :src="croppedImageUrl"
          class="img-fluid rounded border"
          alt="Cropped Image"
          style="max-height: 300px; object-fit: contain"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Cropper from 'vue-cropperjs'
import 'cropperjs/dist/cropper.css'

// 이벤트 선언
const emit = defineEmits(['cropped'])

const fileInput = ref(null)
const cropper = ref(null)
const imageUrl = ref('') // 선택된 원본 이미지
const croppedImageUrl = ref('') // 크롭된 이미지 미리보기용

// 🔽 외부에서 초기화할 수 있도록 메서드 정의
function resetCropper() {
  imageUrl.value = ''
  croppedImageUrl.value = ''
}
defineExpose({ resetCropper })

// 파일 선택 트리거
function triggerFileSelect() {
  fileInput.value.click()
}

// 파일을 base64로 로드
function loadImage(file) {
  const reader = new FileReader()
  reader.onload = () => {
    imageUrl.value = reader.result
    croppedImageUrl.value = ''
  }
  reader.readAsDataURL(file)
}

// 파일 선택 시
function onFileChange(e) {
  const file = e.target.files[0]
  if (file) loadImage(file)
}

// 드래그 드롭 시
function onDrop(e) {
  const file = e.dataTransfer.files[0]
  if (file) loadImage(file)
}

// 이미지 크롭 → 상위 컴포넌트에 전달
function cropImage() {
  if (!cropper.value) return
  const canvas = cropper.value.getCroppedCanvas()
  if (!canvas) return

  const dataUrl = canvas.toDataURL('image/png')
  croppedImageUrl.value = dataUrl

  canvas.toBlob((blob) => {
    if (!blob) return
    const file = new File([blob], 'cropped.png', { type: blob.type })
    emit('cropped', {
      file,
      dataUrl,
    })
  }, 'image/png')
}
</script>

<style scoped>
img {
  max-height: 300px;
  object-fit: contain;
}
</style>
