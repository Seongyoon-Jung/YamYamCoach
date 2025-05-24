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
      <div class="mb-3">
        <label class="form-label">이미지 업로드 (선택)</label>
        <input class="form-control" type="file" @change="handleImageUpload" />
      </div>

      <!-- 이미지 미리보기 -->
      <div v-if="imagePreviewUrl" class="mb-4">
        <label class="form-label">이미지 미리보기</label><br />
        <img :src="imagePreviewUrl" alt="미리보기 이미지" style="max-width: 200px" />
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
const imagePreviewUrl = ref('') // 미리보기용 ref
const accountStore = userAccountStore()

const handleImageUpload = (e) => {
  const file = e.target.files[0]
  imageFile.value = file

  // 파일 존재하면 미리보기 설정
  if (file) {
    imagePreviewUrl.value = URL.createObjectURL(file)
  } else {
    imagePreviewUrl.value = ''
  }
}

const submitPost = async () => {
  try {
    let imageKey = null

    if (imageFile.value) {
      const file = imageFile.value
      const uuid = crypto.randomUUID()
      const fileName = `uploads/board/${uuid}.png`

      // 1. presigned PUT URL 요청
      const putUrlRes = await axios.get('/api/s3/put-url', {
        params: { fileName },
      })
      const putUrl = putUrlRes.data
      console.log(putUrl)
      // 2. S3에 직접 업로드
      await fetch(putUrl, {
        method: 'PUT',
        body: file,
        headers: {
          'Content-Type': file.type,
        },
      })

      // 3. 업로드 성공 후 fileName 저장
      imageKey = fileName
    }

    // 4. 게시글 데이터 전송
    const dto = {
      username: accountStore.username,
      title: title.value,
      content: content.value,
      imageUrl: imageKey, // 이미지 없으면 null
    }

    await axios.post('/api/board', dto)

    router.push('/board')
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
