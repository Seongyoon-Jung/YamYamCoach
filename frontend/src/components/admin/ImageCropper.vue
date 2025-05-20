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
    <div class="mb-3 text-center">
      <button class="btn btn-outline-primary" @click="triggerFileSelect">
        사진 선택 또는 드래그&드롭
      </button>
    </div>

    <!-- Cropper 보여주는 영역: 중앙 정렬 -->
    <div v-if="imageUrl" class="row mb-3 justify-content-center">
      <div class="col-auto p-0 border rounded overflow-hidden">
        <Cropper
          ref="cropper"
          :src="imageUrl"
          :view-mode="1"
          :guides="true"
          :auto-crop-area="0.8"
          :drag-mode="crop"
          :background="true"
          :zoomable="true"
          :scalable="true"
          style="max-width: 100%; display: block"
        />
      </div>
    </div>

    <!-- 크롭 버튼 -->
    <div v-if="imageUrl" class="text-center mb-4">
      <button class="btn btn-primary" @click="cropImage">크롭</button>
    </div>

    <!-- 결과 이미지 -->
    <div v-if="croppedImageUrl" class="text-center">
      <h5>크롭된 이미지</h5>
      <img :src="croppedImageUrl" class="img-fluid rounded" alt="Cropped Image" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Cropper from 'vue-cropperjs'
import 'cropperjs/dist/cropper.css'

const emit = defineEmits(['cropped']) // 이벤트 정의

const fileInput = ref(null)
const cropper = ref(null)
const imageUrl = ref('')
const croppedImageUrl = ref('')

function triggerFileSelect() {
  fileInput.value.click()
}

function loadImage(file) {
  const reader = new FileReader()
  reader.onload = () => {
    imageUrl.value = reader.result
    croppedImageUrl.value = ''
  }
  reader.readAsDataURL(file)
}

function onFileChange(e) {
  const file = e.target.files[0]
  if (file) loadImage(file)
}

function onDrop(e) {
  const file = e.dataTransfer.files[0]
  if (file) loadImage(file)
}

function cropImage() {
  if (!cropper.value) return
  const canvas = cropper.value.getCroppedCanvas()
  if (canvas) {
    croppedImageUrl.value = canvas.toDataURL('image/png')
  }
  canvas.toBlob((blob) => {
    if (!blob) return
    const file = new File([blob], 'cropped.png', { type: blob.type })
    // 부모에게 file을 전달
    emit('cropped', file)
  }, 'image/png')
}
</script>
