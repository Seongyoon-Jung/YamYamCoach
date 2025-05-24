<template>
  <div class="recipe-create-page container py-5">
    <h2 class="mb-4">간편 레시피 작성</h2>

    <div v-if="error" class="alert alert-danger mb-4">
      {{ error }}
    </div>

    <div class="card shadow-sm">
      <div class="card-body">
        <form @submit.prevent="submitRecipe">
          <!-- 레시피 기본 정보 -->
          <div class="mb-4">
            <div class="mb-3">
              <label for="recipeName" class="form-label">음식명 <span class="text-danger">*</span></label>
              <input
                type="text"
                class="form-control"
                id="recipeName"
                v-model="recipe.name"
                required
                placeholder="음식 이름을 입력하세요"
              />
            </div>

            <!-- 카테고리 선택 (간단하게 유지) -->
            <div class="mb-3">
              <label for="recipeCategory" class="form-label">카테고리 <span class="text-danger">*</span></label>
              <select class="form-select" id="recipeCategory" v-model="recipe.category" required>
                <option value="" disabled selected>카테고리 선택</option>
                <option v-for="category in categories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>
            </div>
          </div>

          <!-- 레시피 이미지 업로드 -->
          <div class="mb-4">
            <label for="recipeImage" class="form-label">대표 이미지</label>
            <input
              type="file"
              class="form-control"
              id="recipeImage"
              @change="handleImageUpload"
              accept="image/*"
            />
            <div v-if="imagePreview" class="image-preview mt-3">
              <img :src="imagePreview" alt="이미지 미리보기" class="img-fluid rounded" style="max-height: 200px" />
            </div>
          </div>

          <!-- 재료 정보 (단순화) -->
          <div class="mb-4">
            <label for="ingredients" class="form-label">재료 <span class="text-danger">*</span></label>
            <textarea
              class="form-control"
              id="ingredients"
              v-model="recipe.ingredientsText"
              rows="3"
              required
              placeholder="재료를 입력하세요 (예: 쌀 2컵, 물 3컵, 소금 1작은술)"
            ></textarea>
          </div>

          <!-- 레시피 내용 -->
          <div class="mb-4">
            <label for="recipeContent" class="form-label">내용 <span class="text-danger">*</span></label>
            <textarea
              class="form-control"
              id="recipeContent"
              v-model="recipe.content"
              rows="6"
              required
              placeholder="레시피 조리 방법을 자세히 설명해주세요"
            ></textarea>
          </div>

          <!-- 저장 버튼 -->
          <div class="d-flex justify-content-between">
            <router-link to="/recipe" class="btn btn-outline-secondary">취소</router-link>
            <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
              <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              레시피 저장
            </button>
          </div>
        </form>
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

// 카테고리 목록
const categories = [
  '한식', '양식', '중식', '일식', '동남아', 
  '채식', '저칼로리', '고단백', '간편요리', '디저트'
]

// 레시피 데이터 초기화 (간소화됨)
const recipe = reactive({
  name: '',
  category: '',
  imageFile: null,
  ingredientsText: '',
  content: ''
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

  try {
    isSubmitting.value = true
    error.value = ''

    // 폼 데이터 생성
    const formData = new FormData()
    
    // 기본 정보 추가
    const recipeData = {
      name: recipe.name,
      category: recipe.category,
      ingredients: recipe.ingredientsText,
      content: recipe.content,
      // 기본값 설정
      cookTimeMinutes: 30,
      difficulty: '보통',
      servings: 2
    }
    
    formData.append('recipeData', JSON.stringify(recipeData))
    
    // 메인 이미지 추가
    if (recipe.imageFile) {
      formData.append('mainImage', recipe.imageFile)
    }
    
    // API 요청
    const response = await axios.post('/api/recipes', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    // 성공 시 상세 페이지로 이동
    router.push(`/recipe/${response.data.id}`)
  } catch (err) {
    console.error('레시피 저장 중 오류:', err)
    error.value = '레시피 저장 중 오류가 발생했습니다. 다시 시도해주세요.'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.form-label {
  font-weight: 500;
}
</style> 