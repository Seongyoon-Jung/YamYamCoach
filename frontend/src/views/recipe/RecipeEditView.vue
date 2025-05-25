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
                  placeholder="재료를 입력하세요 (예: 쌀 2컵, 물 3컵, 소금 1작은술)"
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
                <div v-else-if="recipeImageUrl" class="image-preview mt-3">
                  <img :src="recipeImageUrl" alt="현재 이미지" class="img-fluid rounded" style="max-height: 200px" />
                  <div class="text-muted small mt-1">새 이미지를 업로드하지 않으면 기존 이미지가 유지됩니다.</div>
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
      recipe.ingredientsText = recipeData.ingredients.join(', ')
    } else if (typeof recipeData.ingredients === 'string') {
      recipe.ingredientsText = recipeData.ingredients
    }
    
  } catch (err) {
    console.error('레시피 데이터 로딩 실패:', err)
    error.value = '레시피 정보를 불러오는 중 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}

// 메인 이미지 업로드 처리
const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    imageFile.value = file
    // 파일 미리보기 생성
    imagePreview.value = URL.createObjectURL(file)
  } else {
    imageFile.value = null
    imagePreview.value = null
  }
}

// 폼 제출 처리
const submitRecipe = async () => {
  // 로그인 여부 확인
  if (!accountStore.username) {
    error.value = '레시피를 수정하려면 로그인이 필요합니다.'
    setTimeout(() => {
      router.push('/login')
    }, 2000)
    return
  }

  try {
    isSubmitting.value = true
    error.value = ''

    // 이미지 업로드를 위한 변수
    let imageKey = recipe.imageUrl; // 기본적으로 기존 이미지 URL 유지

    // 새 이미지 파일이 있는 경우 S3에 먼저 업로드
    if (imageFile.value) {
      const file = imageFile.value
      const uuid = crypto.randomUUID()
      const fileName = `uploads/recipe/${uuid}-${file.name}`

      try {
        // 1. presigned PUT URL 요청
        const putUrlRes = await axios.get('/api/s3/put-url', {
          params: { fileName },
        })
        const putUrl = putUrlRes.data

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
      } catch (uploadErr) {
        console.error('이미지 업로드 실패:', uploadErr)
        error.value = '이미지 업로드 중 오류가 발생했습니다.'
        isSubmitting.value = false
        return
      }
    }
    
    // 레시피 데이터 생성
    const recipeData = {
      name: recipe.name,
      description: recipe.description,
      category: recipe.category,
      ingredients: recipe.ingredientsText,
      content: recipe.content,
      imageUrl: imageKey, // S3에 업로드된 이미지 키 (없으면 기존 이미지 URL)
      cookTimeMinutes: recipe.cookTimeMinutes,
      difficulty: recipe.difficulty,
      servings: recipe.servings
    }
    
    // API 요청 (PUT 메서드로 업데이트)
    const response = await axios.put(`/api/recipes/${recipeId.value}`, recipeData)
    
    // 응답 데이터 확인 및 처리
    console.log('수정된 레시피 데이터:', response.data)
    
    alert('레시피가 성공적으로 수정되었습니다!')
    router.push(`/recipe/${recipeId.value}`)
  } catch (err) {
    console.error('레시피 수정 중 오류:', err)
    
    if (err.response && err.response.status === 403) {
      error.value = '자신이 작성한 레시피만 수정할 수 있습니다.'
    } else {
      error.value = '레시피 수정 중 오류가 발생했습니다. 다시 시도해주세요.'
    }
    
    // 에러 상세 정보 로깅
    if (err.response) {
      console.error('서버 응답 오류:', err.response.status, err.response.data)
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
</style> 