<template>
  <div class="recipe-create-page container-fluid py-4">
    <!-- 상단 제목 및 버튼 -->
    <div class="text-center mb-5">
      <h2 class="mb-0 fw-bold">레시피 작성</h2>
    </div>

    <div class="d-flex justify-content-end mb-4">
      <button type="button" class="btn btn-outline-secondary me-2" @click="$router.push('/recipe')">취소</button>
      <button type="button" class="btn btn-primary" @click="submitRecipe" :disabled="isSubmitting">
        <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        저장
      </button>
    </div>

    <hr class="mb-5">

    <div v-if="error" class="alert alert-danger mb-4">
      {{ error }}
    </div>

    <div class="row">
      <!-- 왼쪽 영역: 기본 정보 -->
      <div class="col-md-4">
        <div class="d-flex flex-column gap-3">
          <!-- 음식명 -->
          <div class="form-group">
            <label for="recipeName" class="form-label">음식명</label>
            <input
              type="text"
              class="form-control"
              id="recipeName"
              v-model="recipe.name"
              required
              placeholder="음식 이름을 입력하세요"
            />
          </div>
          
          <!-- 카테고리 -->
          <div class="form-group">
            <label for="recipeCategory" class="form-label">카테고리</label>
            <select class="form-select" id="recipeCategory" v-model="recipe.category" required>
              <option value="" disabled selected>카테고리 선택</option>
              <option v-for="category in categories" :key="category" :value="category">
                {{ category }}
              </option>
            </select>
          </div>

          <!-- 재료 -->
          <div class="form-group">
            <label for="ingredients" class="form-label">재료</label>
            <textarea
              class="form-control"
              id="ingredients"
              v-model="recipe.ingredientsText"
              rows="6"
              required
              placeholder="재료를 한 줄에 하나씩 입력하세요&#10;예시:&#10;밀가루 2컵&#10;설탕 1/2컵&#10;소금 1티스푼"
            ></textarea>
          </div>

          <!-- 인분수 -->
          <div class="form-group">
            <label class="form-label">인분수</label>
            <div class="input-group">
              <input type="number" class="form-control" v-model="recipe.servings" min="1">
              <span class="input-group-text">인분</span>
            </div>
          </div>

          <!-- 조리시간과 난이도를 한 줄에 -->
          <div class="row">
            <div class="col-md-6">
              <div class="form-group">
                <label class="form-label">조리시간</label>
                <div class="input-group">
                  <input type="number" class="form-control" v-model="recipe.cookTimeMinutes" min="1">
                  <span class="input-group-text">분</span>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-group">
                <label class="form-label">난이도</label>
                <select class="form-select" v-model="recipe.difficulty">
                  <option value="쉬움">쉬움</option>
                  <option value="보통">보통</option>
                  <option value="어려움">어려움</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 이미지 업로드 -->
          <div class="form-group">
            <label for="recipeImage" class="form-label">대표 이미지</label>
            <input
              type="file"
              class="form-control"
              id="recipeImage"
              @change="handleImageUpload"
              accept="image/*"
            />
            <div v-if="imagePreview" class="image-preview mt-3">
              <img :src="imagePreview" alt="이미지 미리보기" class="img-fluid rounded" />
            </div>
          </div>
        </div>
      </div>

      <!-- 오른쪽 영역: 설명 및 내용 -->
      <div class="col-md-8">
        <!-- 간단설명 -->
        <div class="mb-4">
          <h5 class="mb-3">간단설명</h5>
          <textarea
            class="form-control"
            v-model="recipe.description"
            rows="3"
            required
            placeholder="레시피에 대한 간단한 설명을 작성해주세요"
            style="resize: none;"
          ></textarea>
        </div>

        <!-- 내용 -->
        <div>
          <h5 class="mb-3">내용</h5>
          <textarea
            class="form-control"
            v-model="recipe.content"
            rows="15"
            required
            placeholder="레시피 조리 방법을 자세히 설명해주세요"
            style="resize: none;"
          ></textarea>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'

const router = useRouter()
const accountStore = userAccountStore()
const error = ref('')
const isSubmitting = ref(false)
const imagePreview = ref(null)

// 카테고리 목록 (5개만)
const categories = [
  '한식', '중식', '일식', '양식', '동남아식'
]

// 레시피 데이터 초기화 (간소화됨)
const recipe = reactive({
  name: '',
  category: '',
  imageFile: null,
  ingredientsText: '',
  description: '',
  content: '',
  cookTimeMinutes: 30,
  difficulty: '보통',
  servings: 2
})

// 메인 이미지 업로드 처리
const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    recipe.imageFile = file
    const reader = new FileReader()
    reader.onload = (e) => {
      imagePreview.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// 폼 제출 처리
const submitRecipe = async () => {
  // 로그인 여부 확인
  if (!accountStore.username) {
    error.value = '레시피를 작성하려면 로그인이 필요합니다.'
    setTimeout(() => {
      router.push('/login')
    }, 2000)
    return
  }

  // 폼 유효성 검사
  if (!recipe.name || !recipe.category || !recipe.ingredientsText) {
    error.value = '필수 입력 항목을 모두 입력해주세요 (음식명, 카테고리, 재료)'
    return
  }

  try {
    isSubmitting.value = true
    error.value = ''
    console.log('레시피 저장 시작', recipe)

    // 폼 데이터 생성
    const formData = new FormData()
    
    // 기본 정보 추가
    const recipeData = {
      name: recipe.name,
      category: recipe.category,
      ingredients: recipe.ingredientsText,
      description: recipe.description || '',
      content: recipe.content || '',
      cookTimeMinutes: recipe.cookTimeMinutes || 30,
      difficulty: recipe.difficulty || '보통',
      servings: recipe.servings || 2
    }
    
    console.log('레시피 데이터:', recipeData)
    formData.append('recipeData', JSON.stringify(recipeData))
    
    // 메인 이미지 추가
    if (recipe.imageFile) {
      formData.append('mainImage', recipe.imageFile)
      console.log('이미지 파일 추가됨')
    }
    
    // API 요청
    const response = await axios.post('/api/recipes', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'X-USER-ID': accountStore.userId  // 명시적으로 사용자 ID 헤더 추가
      },
      withCredentials: true  // 쿠키 전송 확인
    })
    
    console.log('레시피 저장 성공:', response.data)
    // 성공 시 상세 페이지로 이동
    router.push(`/recipe/${response.data.id}`)
  } catch (err) {
    console.error('레시피 저장 중 오류:', err)
    if (err.response) {
      console.error('서버 응답:', err.response.status, err.response.data)
      if (err.response.status === 401) {
        error.value = '로그인이 필요합니다. 로그인 페이지로 이동합니다.'
        setTimeout(() => {
          router.push('/login')
        }, 2000)
      } else {
        error.value = `레시피 저장 중 오류가 발생했습니다: ${err.response.data || '다시 시도해주세요.'}`
      }
    } else {
      error.value = '네트워크 오류가 발생했습니다. 다시 시도해주세요.'
    }
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.recipe-create-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.form-label {
  font-weight: 500;
  color: #495057;
  margin-bottom: 0.5rem;
}

.form-control, .form-select {
  border-radius: 0.5rem;
  border: 1px solid #dee2e6;
}

.form-control:focus, .form-select:focus {
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
}

.btn {
  border-radius: 0.5rem;
  padding: 0.5rem 1.5rem;
}

.image-preview img {
  max-height: 150px;
  object-fit: cover;
  width: 100%;
}

hr {
  opacity: 0.8;
  border-color: #dee2e6;
}
</style> 