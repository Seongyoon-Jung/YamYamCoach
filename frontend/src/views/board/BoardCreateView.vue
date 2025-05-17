<!-- src/views/BoardCreatePage.vue -->
<template>
  <div class="container py-5" style="max-width: 800px">
    <h2 class="mb-4 text-center">게시글 작성</h2>

    <form @submit.prevent="submitPost">
      <!-- 제목 -->
      <div class="mb-3">
        <label for="title" class="form-label">제목</label>
        <input
          v-model="title"
          type="text"
          class="form-control"
          id="title"
          placeholder="제목을 입력하세요"
          required
        />
      </div>

      <!-- 내용 -->
      <div class="mb-3">
        <label for="content" class="form-label">내용</label>
        <textarea
          v-model="content"
          class="form-control"
          id="content"
          rows="8"
          placeholder="내용을 입력하세요"
          required
        ></textarea>
      </div>

      <!-- 이미지 업로드 -->
      <div class="mb-4">
        <label class="form-label">이미지 업로드 (선택)</label>
        <input class="form-control" type="file" @change="handleImageUpload" />
      </div>

      <!-- 버튼 -->
      <div class="text-end">
        <button type="submit" class="btn btn-primary">등록</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'

const router = useRouter()

const title = ref('')
const content = ref('')
const imageFile = ref(null)
const accountStore = userAccountStore()

const handleImageUpload = (e) => {
  imageFile.value = e.target.files[0]
}

const submitPost = async () => {
  try {
    // imageFile 처리해주고 이미지 주소를 보내주어야함

    const data = {
      username: accountStore.username,
      title: title.value,
      content: content.value,
      imageFile: null,
    }

    console.log(data)
    await axios.post('/api/board', data)

    router.push('/board') // 게시판 목록으로 이동
  } catch (err) {
    console.error('게시글 등록 실패', err)
  }
}
</script>

<style scoped>
textarea {
  resize: none;
}
</style>
