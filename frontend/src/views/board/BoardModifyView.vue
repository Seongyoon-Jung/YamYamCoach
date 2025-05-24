<template>
  <div class="container py-5" style="max-width: 800px">
    <h2 class="mb-4 text-center">게시글 수정</h2>

    <form @submit.prevent="submitUpdate">
      <!-- 제목 -->
      <div class="mb-3">
        <label for="title" class="form-label">제목</label>
        <input v-model="title" type="text" class="form-control" id="title" required />
      </div>

      <!-- 내용 -->
      <div class="mb-3">
        <label for="content" class="form-label">내용</label>
        <textarea v-model="content" class="form-control" id="content" rows="8" required></textarea>
      </div>

      <!-- 기존 이미지 미리보기 -->
      <div v-if="imageUrl && !imageFile" class="mb-3">
        <label class="form-label">현재 이미지</label><br />
        <img :src="imageUrl" alt="기존 이미지" style="max-width: 200px" />
      </div>

      <!-- 새 이미지 업로드 -->
      <div class="mb-3">
        <label class="form-label">새 이미지 업로드 (선택)</label>
        <input type="file" class="form-control" @change="handleImageUpload" />
      </div>

      <!-- 새 이미지 미리보기 -->
      <div v-if="imagePreviewUrl" class="mb-3">
        <label class="form-label">새 이미지 미리보기</label><br />
        <img :src="imagePreviewUrl" style="max-width: 200px" />
      </div>

      <!-- 버튼 -->
      <div class="text-end">
        <button type="submit" class="btn btn-primary">수정 완료</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/plugins/axios'

const route = useRoute()
const router = useRouter()

const title = ref('')
const content = ref('')
const imageFile = ref(null)
const imageUrl = ref('') // 사용자에게 보여줄 presigned URL
const imagePreviewUrl = ref('')
const originalImgKey = ref('') // 기존 S3 이미지 key (uploads/board/~~.png)

// 파일 선택 핸들러
const handleImageUpload = (e) => {
  const file = e.target.files[0]
  imageFile.value = file
  if (file) {
    imagePreviewUrl.value = URL.createObjectURL(file)
  } else {
    imagePreviewUrl.value = ''
  }
}

// 게시글 불러오기
onMounted(async () => {
  const id = route.params.id
  try {
    const res = await axios.get(`/api/board/${id}?hit=false`)
    title.value = res.data.title
    content.value = res.data.content
    originalImgKey.value = res.data.imageUrl

    if (res.data.imageUrl) {
      try {
        const presignedRes = await axios.get('/api/s3/get-url', {
          params: { filename: res.data.imageUrl },
        })
        imageUrl.value = presignedRes.data
      } catch (e) {
        console.warn('presigned URL 요청 실패:', e)
        imageUrl.value = ''
      }
    }
  } catch (err) {
    router.push('/error')
  }
})

// 게시글 수정 제출
const submitUpdate = async () => {
  let newImageKey = originalImgKey.value

  // 새 이미지가 선택된 경우 → presigned URL 업로드
  if (imageFile.value) {
    try {
      const uuid = crypto.randomUUID()
      const fileName = `uploads/board/${uuid}.png`
      const putUrlRes = await axios.get('/api/s3/put-url', {
        params: { fileName },
      })
      const presignedUrl = putUrlRes.data

      await fetch(presignedUrl, {
        method: 'PUT',
        body: imageFile.value,
        headers: {
          'Content-Type': imageFile.value.type,
        },
      })

      newImageKey = fileName
    } catch (e) {
      console.error('이미지 업로드 실패:', e)
      return
    }
  }

  const boardData = {
    boardId: route.params.id,
    title: title.value,
    content: content.value,
    imageUrl: newImageKey,
  }

  try {
    await axios.put('/api/board', boardData)
    router.push(`/board/${route.params.id}`)
  } catch (err) {
    console.error('게시글 수정 실패', err)
    router.push('/error')
  }
}
</script>
