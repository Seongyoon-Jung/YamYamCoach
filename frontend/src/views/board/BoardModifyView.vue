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

      <!-- 이미지 -->
      <div class="mb-4">
        <label class="form-label">이미지 업로드 (선택)</label>
        <input type="file" class="form-control" @change="handleImageUpload" />
      </div>

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
const imageUrl = ref('')

const handleImageUpload = (e) => {
  imageFile.value = e.target.files[0]
}

// 게시글 정보 불러오기
onMounted(async () => {
  const id = route.params.id
  try {
    const res = await axios.get(`/api/board/${id}?hit=false`)
    title.value = res.data.title
    content.value = res.data.content
    imageUrl.value = res.data.imageUrl
  } catch (err) {
    router.push('/error')
  }
})

// 게시글 수정 요청
const submitUpdate = async () => {
  const post = {
    boardId: route.params.id,
    title: title.value,
    content: content.value,
    imageUrl: imageUrl.value,
  }
  try {
    await axios.put('/api/board', post)
    router.push(`/board/${route.params.id}`)
  } catch (err) {
    //console.log(err)
    router.push('/error')
  }
}
</script>
