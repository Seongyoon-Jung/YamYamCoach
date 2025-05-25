<template>
  <div class="recipe-edit-page container py-5">
    <h2 class="text-center mb-4">레시피 수정</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">불러오는 중...</span>
      </div>
      <p class="mt-3">레시피 정보를 불러오는 중입니다...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger mb-4">
      {{ error }}
    </div>

    <div v-else class="card shadow-sm">
      <div class="card-body">
        <form @submit.prevent="submitRecipe">
          <div class="row">
            <!-- 왼쪽 영역: 기본 정보 -->
            <div class="col-md-4">
              <!-- 음식명 -->
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

              <!-- 카테고리 -->
              <div class="mb-3">
                <label for="recipeCategory" class="form-label">카테고리 <span class="text-danger">*</span></label>
                <select class="form-select" id="recipeCategory" v-model="recipe.category" required>
                  <option value="" disabled>카테고리 선택</option>
                  <option v-for="category in categories" :key="category" :value="category">
                    {{ category }}
                  </option>
                </select>
              </div>

              <!-- 재료 -->
              <div class="mb-3">
                <label for="ingredients" class="form-label">재료 <span class="text-danger">*</span></label>
                <textarea
                  class="form-control"
                  id="ingredients"
                  v-model="recipe.ingredientsText"
                  rows="6"
                  required
                  placeholder="재료를 한 줄에 하나씩 입력하세요&#10;예시:&#10;밀가루 2컵&#10;설탕 1/2컵&#10;소금 1티스푼"
                ></textarea>
              </div>
              
              <!-- 인분 수 -->
              <div class="mb-3">
                <label for="servings" class="form-label">인분 수 <span class="text-danger">*</span></label>
                <div class="input-group">
                  <input 
                    type="number" 
                    class="form-control"
                    id="servings"
                    v-model="recipe.servings"
                    min="1"
                    required
                  />
                  <span class="input-group-text">인분</span>
                </div>
              </div>

              <!-- 조리 정보 -->
              <div class="row mb-3">
                <!-- 조리 시간 -->
                <div class="col-6">
                  <label for="cookTime" class="form-label">조리시간 <span class="text-danger">*</span></label>
                  <div class="input-group">
                    <input 
                      type="number" 
                      class="form-control"
                      id="cookTime"
                      v-model="recipe.cookTimeMinutes"
                      min="1"
                      required
                    />
                    <span class="input-group-text">분</span>
                  </div>
                </div>

                <!-- 난이도 -->
                <div class="col-6">
                  <label for="difficulty" class="form-label">난이도 <span class="text-danger">*</span></label>
                  <select class="form-select" id="difficulty" v-model="recipe.difficulty" required>
                    <option value="" disabled>선택</option>
                    <option value="쉬움">쉬움</option>
                    <option value="보통">보통</option>
                    <option value="어려움">어려움</option>
                  </select>
                </div>
              </div>
            </div>

            <!-- 오른쪽 영역: 설명 및 내용 -->
            <div class="col-md-8">
              <!-- 간단 설명 -->
              <div class="mb-3">
                <label for="recipeDescription" class="form-label">간단 설명 <span class="text-danger">*</span></label>
                <textarea
                  class="form-control"
                  id="recipeDescription"
                  v-model="recipe.description"
                  rows="3"
                  required
                  placeholder="레시피에 대한 간단한 설명을 입력하세요"
                ></textarea>
              </div>

              <!-- 레시피 내용 -->
              <div class="mb-3">
                <label for="recipeContent" class="form-label">내용 <span class="text-danger">*</span></label>
                <textarea
                  class="form-control recipe-content"
                  id="recipeContent"
                  v-model="recipe.content"
                  rows="15"
                  required
                  placeholder="레시피 조리 방법을 자세히 설명해주세요"
                ></textarea>
              </div>

              <!-- 대표 이미지 -->
              <div class="mb-3">
                <label for="recipeImage" class="form-label">대표 이미지</label>
                
                <!-- 현재 이미지 표시 -->
                <div v-if="recipe.imageUrl" class="current-image mb-3">
                  <img :src="recipe.imageUrl" alt="현재 레시피 이미지" class="img-fluid rounded" style="max-height: 200px" />
                  <div class="text-muted small mt-2">현재 이미지</div>
                </div>

                <!-- 새 이미지 업로드 -->
                <div class="new-image-upload">
                  <input
                    type="file"
                    class="form-control"
                    id="recipeImage"
                    @change="handleImageUpload"
                    accept="image/*"
                  />
                  <div class="text-muted small mt-1">새 이미지를 업로드하면 기존 이미지가 교체됩니다.</div>
                </div>

                <!-- 새 이미지 미리보기 -->
                <div v-if="imagePreview" class="image-preview mt-3">
                  <img :src="imagePreview" alt="새 이미지 미리보기" class="img-fluid rounded" style="max-height: 200px" />
                  <div class="text-muted small mt-2">새 이미지 미리보기</div>
                  <button @click="cancelImageUpload" class="btn btn-sm btn-outline-secondary mt-2">
                    <i class="bi bi-x"></i> 새 이미지 취소
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 저장 버튼 -->
          <div class="d-flex justify-content-end mt-4">
            <button type="button" class="btn btn-outline-secondary me-2" @click="$router.push(`/recipe/${recipeId}`)">취소</button>
            <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
              <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              변경사항 저장
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'

const route = useRoute()
const router = useRouter()
const accountStore = userAccountStore()

const recipeId = computed(() => route.params.id)
const loading = ref(true)
const error = ref('')
const isSubmitting = ref(false)
const imagePreview = ref(null)
const imageFile = ref(null)
const recipeImageUrl = ref(null)

// 카테고리 목록
const categories = [
  '한식', '양식', '중식', '일식', '동남아식'
]

// 레시피 데이터 초기화
const recipe = reactive({
  name: '',
  description: '',
  category: '',
  ingredientsText: '',
  content: '',
  imageUrl: '',
  cookTimeMinutes: 30,
  difficulty: '보통',
  servings: 2
})

// S3에서 이미지 URL 가져오기
const getPresignedUrl = async (key) => {
  if (!key) return null;
  try {
    const res = await axios.get('/api/s3/get-url', {
      params: { filename: key }
    });
    return res.data;
  } catch (err) {
    console.error('이미지 URL 가져오기 실패:', err);
    return null;
  }
};

// 레시피 데이터 불러오기
const fetchRecipeData = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await axios.get(`/api/recipes/${recipeId.value}`)
    const recipeData = response.data
    
    // 레시피 작성자 확인
    if (recipeData.authorId !== accountStore.userId && 
        recipeData.userId !== accountStore.userId) {
      error.value = '자신이 작성한 레시피만 수정할 수 있습니다.'
      setTimeout(() => {
        router.push(`/recipe/${recipeId.value}`)
      }, 2000)
      return
    }
    
    // 데이터 설정
    recipe.name = recipeData.name || ''
    recipe.description = recipeData.description || ''
    recipe.category = recipeData.category || ''
    recipe.imageUrl = recipeData.imageUrl || ''
    recipe.content = recipeData.content || ''
    recipe.cookTimeMinutes = recipeData.cookTimeMinutes || 30
    recipe.difficulty = recipeData.difficulty || '보통'
    recipe.servings = recipeData.servings || 2
    
    // 이미지 URL S3 presigned URL로 변환
    if (recipe.imageUrl) {
      recipeImageUrl.value = await getPresignedUrl(recipe.imageUrl)
    }
    
    // 재료 처리
    if (Array.isArray(recipeData.ingredients)) {
      recipe.ingredientsText = recipeData.ingredients.join('\n');
    } else if (typeof recipeData.ingredients === 'string') {
      // 이미 문자열인 경우 그대로 사용 (개행 문자 포함)
      recipe.ingredientsText = recipeData.ingredients;
    }
    
  } catch (err) {
    console.error('레시피 데이터 로딩 실패:', err)
    error.value = '레시피 정보를 불러오는 중 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}

// 이미지 업로드 처리
const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    imageFile.value = file
    // 파일 미리보기 생성
    imagePreview.value = URL.createObjectURL(file)
  }
}

// 이미지 업로드 취소
const cancelImageUpload = () => {
  imageFile.value = null
  imagePreview.value = null
  // input 필드 초기화
  const input = document.getElementById('recipeImage')
  if (input) input.value = ''
}

// 폼 제출 처리
const submitRecipe = async () => {
  try {
    isSubmitting.value = true
    error.value = ''

    // FormData 생성
    const formData = new FormData()
    
    // 레시피 데이터 추가
    const recipeData = {
      name: recipe.name,
      category: recipe.category,
      ingredients: recipe.ingredientsText,
      description: recipe.description,
      content: recipe.content,
      cookTimeMinutes: recipe.cookTimeMinutes,
      difficulty: recipe.difficulty,
      servings: recipe.servings,
      imageUrl: recipe.imageUrl // 기존 이미지 URL 유지
    }
    
    console.log('수정할 레시피 데이터:', recipeData)
    formData.append('recipeData', JSON.stringify(recipeData))
    
    // 새 이미지가 있는 경우에만 추가
    if (imageFile.value) {
      formData.append('mainImage', imageFile.value)
    }
    
    // API 요청 공통 헤더
    const headers = {
      'X-USER-ID': accountStore.userId
    }
    
    // 이미지가 있는 경우와 없는 경우 요청 방식 분리
    let response
    if (imageFile.value) {
      // 이미지가 있는 경우 - FormData로 전송
      response = await axios.post(`/api/recipes/${recipeId.value}`, formData, {
        headers: {
          ...headers,
          'Content-Type': 'multipart/form-data'
        },
        withCredentials: true
      })
      console.log('이미지 포함 업데이트 응답:', response.data)
    } else {
      // 이미지가 없는 경우 - JSON으로 전송
      response = await axios.put(`/api/recipes/${recipeId.value}`, recipeData, {
        headers: headers,
        withCredentials: true
      })
      console.log('일반 업데이트 응답:', response.data)
    }

    alert('레시피가 성공적으로 수정되었습니다!')
    router.push(`/recipe/${recipeId.value}`)
  } catch (err) {
    console.error('레시피 수정 중 오류:', err)
    if (err.response) {
      console.error('서버 응답:', err.response.status, err.response.data)
      if (err.response.status === 401) {
        error.value = '로그인이 필요합니다. 로그인 페이지로 이동합니다.'
        setTimeout(() => {
          router.push('/login')
        }, 2000)
      } else {
        error.value = `레시피 수정 중 오류가 발생했습니다: ${err.response.data || '다시 시도해주세요.'}`
      }
    } else {
      error.value = '네트워크 오류가 발생했습니다. 다시 시도해주세요.'
    }
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  fetchRecipeData()
})
</script>

<style scoped>
.form-label {
  font-weight: 500;
}

.recipe-edit-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

textarea {
  resize: none;
}

.recipe-content {
  min-height: 400px;
}

.image-preview img {
  object-fit: cover;
  width: 100%;
  max-height: 200px;
}

.current-image {
  border: 1px solid #dee2e6;
  padding: 1rem;
  border-radius: 0.5rem;
  background-color: #f8f9fa;
}

.image-preview {
  border: 1px solid #dee2e6;
  padding: 1rem;
  border-radius: 0.5rem;
  background-color: #f8f9fa;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
</style> 