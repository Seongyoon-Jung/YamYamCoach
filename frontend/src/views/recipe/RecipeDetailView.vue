<template>
  <div class="recipe-detail-page container py-5">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">불러오는 중...</span>
      </div>
      <p class="mt-3">레시피 정보를 불러오는 중입니다...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger text-center" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i>
      {{ error }}
      <div class="mt-3">
        <router-link to="/recipe" class="btn btn-outline-secondary">
          <i class="bi bi-arrow-left me-2"></i> 레시피 목록으로 돌아가기
        </router-link>
      </div>
    </div>

    <div v-else-if="recipe" class="recipe-detail">
      <!-- 뒤로 가기 버튼 -->
      <div class="mb-4">
        <div class="d-flex justify-content-between align-items-center">
          <router-link to="/recipe" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-2"></i> 목록으로 돌아가기
          </router-link>
          
          <!-- 작성자 본인인 경우에만 수정/삭제 버튼 표시 -->
          <div v-if="isAuthor" class="d-flex gap-2">
            <router-link :to="`/recipe/edit/${recipe.id}`" class="btn btn-primary">
              <i class="bi bi-pencil me-1"></i> 수정
            </router-link>
            <button @click="deleteRecipe" class="btn btn-danger">
              <i class="bi bi-trash me-1"></i> 삭제
            </button>
          </div>
        </div>
      </div>

      <!-- 레시피 헤더 -->
      <div class="recipe-header mb-5">
        <div class="row align-items-center">
          <div class="col-md-8">
            <span class="badge bg-primary mb-2">{{ recipe.category }}</span>
            <h1 class="fw-bold mb-3">{{ recipe.name }}</h1>
            <p class="lead">{{ recipe.description }}</p>
            
            <div class="recipe-meta d-flex flex-wrap gap-4 text-muted mb-3">
              <div><i class="bi bi-clock me-2"></i> {{ formatCookTime(recipe.cookTimeMinutes) }}</div>
              <div><i class="bi bi-bar-chart me-2"></i> {{ recipe.difficulty }}</div>
              <div><i class="bi bi-heart me-2"></i> {{ recipe.likes }}명이 좋아합니다</div>
            </div>
            
            <div class="author-info d-flex align-items-center">
              <img 
                :src="recipe.authorProfileUrl || '/images/default-profile.jpg'" 
                class="rounded-circle me-2" 
                width="40" 
                height="40" 
                alt="작성자 프로필"
              />
              <div>
                <div class="fw-bold">{{ recipe.authorName }}</div>
                <div class="text-muted small">{{ formatDate(recipe.createdAt) }}</div>
              </div>
            </div>
          </div>
          
          <div class="col-md-4">
            <img 
              :src="recipe.imageUrl || '/images/default-recipe.jpg'" 
              class="img-fluid rounded shadow recipe-main-image" 
              alt="레시피 이미지"
            />
          </div>
        </div>
      </div>

      <!-- 레시피 본문 -->
      <div class="row">
        <div class="col-md-4">
          <!-- 재료 목록 -->
          <div class="ingredients-list card mb-4">
            <div class="card-header bg-light">
              <h5 class="mb-0">재료 <small class="text-muted">({{ recipe.servings }}인분)</small></h5>
            </div>
            <div class="card-body">
              <ul class="list-group list-group-flush">
                <li v-for="(ingredient, index) in recipe.ingredients" :key="index" class="list-group-item d-flex justify-content-between align-items-center">
                  <span>{{ ingredient }}</span>
                  <span v-if="recipe.amounts && recipe.amounts[index]" class="text-muted">{{ recipe.amounts[index] }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
        
        <div class="col-md-8">
          <!-- 조리 단계 -->
          <div class="cooking-steps card mb-4">
            <div class="card-header bg-light">
              <h5 class="mb-0">조리 방법</h5>
            </div>
            <div class="card-body">
              <div v-for="(step, index) in recipe.steps" :key="index" class="cooking-step mb-4">
                <div class="step-header d-flex align-items-center mb-2">
                  <div class="step-number me-3">{{ index + 1 }}</div>
                  <h6 class="mb-0" v-if="step.title">{{ step.title }}</h6>
                </div>
                <p>{{ step.description || step }}</p>
                <img 
                  v-if="step.imageUrl" 
                  :src="step.imageUrl" 
                  class="img-fluid rounded step-image" 
                  :alt="`조리 단계 ${index + 1} 이미지`"
                />
              </div>
            </div>
          </div>
          
          <!-- 조리 팁 -->
          <div v-if="recipe.tips && recipe.tips.length > 0" class="cooking-tips card mb-4">
            <div class="card-header bg-light">
              <h5 class="mb-0">조리 팁</h5>
            </div>
            <div class="card-body">
              <ul class="list-group list-group-flush">
                <li v-for="(tip, index) in recipe.tips" :key="index" class="list-group-item">
                  <i class="bi bi-lightbulb me-2 text-warning"></i> {{ tip }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 레시피 태그 -->
      <div v-if="recipe.tags && recipe.tags.length > 0" class="recipe-tags mb-4">
        <div class="d-flex flex-wrap gap-2">
          <span v-for="tag in recipe.tags" :key="tag" class="badge bg-secondary">
            # {{ tag }}
          </span>
        </div>
      </div>
      
      <!-- 좋아요 버튼 -->
      <div class="like-section text-center mb-5">
        <button class="btn btn-outline-danger" @click="toggleLike">
          <i :class="liked ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
          {{ liked ? '좋아요 취소' : '좋아요' }}
        </button>
        <div class="text-muted mt-2">{{ recipe.likes }}명이 이 레시피를 좋아합니다</div>
      </div>
      
      <!-- 관련 레시피 -->
      <div v-if="relatedRecipes.length > 0" class="related-recipes">
        <h5 class="mb-3">비슷한 레시피</h5>
        <div class="row g-3">
          <div v-for="relatedRecipe in relatedRecipes" :key="relatedRecipe.id" class="col-md-4">
            <div class="card h-100 shadow-sm" @click="goToRecipe(relatedRecipe.id)">
              <img 
                :src="relatedRecipe.imageUrl || '/images/default-recipe.jpg'" 
                class="card-img-top related-recipe-image" 
                alt="관련 레시피 이미지"
              />
              <div class="card-body">
                <h6 class="card-title">{{ relatedRecipe.name }}</h6>
                <p class="card-text text-truncate-2 small">{{ relatedRecipe.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'

const route = useRoute()
const router = useRouter()
const accountStore = userAccountStore()

const recipeId = computed(() => route.params.id)

const recipe = ref(null)
const loading = ref(true)
const error = ref(null)
const liked = ref(false)
const relatedRecipes = ref([])

// 현재 사용자가 작성자인지 확인
const isAuthor = computed(() => {
  if (!recipe.value || !accountStore.userId) return false
  return recipe.value.userId === accountStore.userId || 
         recipe.value.authorId === accountStore.userId
})

// 레시피 삭제 처리
const deleteRecipe = async () => {
  if (!confirm('정말로 이 레시피를 삭제하시겠습니까?')) return
  
  try {
    await axios.delete(`/api/recipes/${recipeId.value}`)
    alert('레시피가 삭제되었습니다.')
    router.push('/recipe')
  } catch (err) {
    console.error('레시피 삭제 실패:', err)
    alert('레시피 삭제 중 오류가 발생했습니다.')
  }
}

// 레시피 데이터 가져오기
const fetchRecipe = async () => {
  loading.value = true
  try {
    const response = await axios.get(`/api/recipes/${recipeId.value}`)
    // nutrition 기본값 추가
    recipe.value = {
      ...response.data,
      nutrition: response.data.nutrition || {
        calories: 0,
        protein: 0,
        carbs: 0,
        fat: 0
      }
    }
    
    // 관련 레시피 가져오기
    fetchRelatedRecipes()
  } catch (err) {
    console.error('레시피 데이터 로딩 실패:', err)
    error.value = '레시피 정보를 불러오는 중 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}

// 관련 레시피 가져오기
const fetchRelatedRecipes = async () => {
  try {
    // 백엔드에 관련 레시피 API가 구현되지 않았으므로 같은 카테고리 레시피를 불러옵니다.
    if (recipe.value && recipe.value.category) {
      const response = await axios.get(`/api/recipes/category/${recipe.value.category}`)
      // 현재 레시피를 제외한 최대 3개의 레시피 표시
      relatedRecipes.value = response.data
        .filter(r => r.id !== recipe.value.id)
        .slice(0, 3)
    } else {
      relatedRecipes.value = []
    }
  } catch (err) {
    console.error('관련 레시피 로딩 실패:', err)
    relatedRecipes.value = [] // 실패 시 빈 배열 설정
  }
}

// 좋아요 상태 가져오기
const fetchLikeStatus = async () => {
  try {
    const response = await axios.get(`/api/recipes/${recipeId.value}/like-status`)
    liked.value = response.data.liked
  } catch (err) {
    console.error('좋아요 상태 로딩 실패:', err)
    liked.value = false
  }
}

// 좋아요 토글
const toggleLike = async () => {
  try {
    if (liked.value) {
      await axios.delete(`/api/recipes/${recipeId.value}/like`)
      recipe.value.likes--
    } else {
      await axios.post(`/api/recipes/${recipeId.value}/like`)
      recipe.value.likes++
    }
    liked.value = !liked.value
  } catch (err) {
    console.error('좋아요 처리 중 오류:', err)
  }
}

// 다른 레시피로 이동
const goToRecipe = (id) => {
  router.push(`/recipe/${id}`)
}

// 조리 시간 포맷팅
const formatCookTime = (minutes) => {
  if (minutes < 60) {
    return `${minutes}분`
  } else {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}시간 ${mins}분` : `${hours}시간`
  }
}

// 날짜 포맷팅
const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getFullYear()}년 ${(d.getMonth() + 1).toString().padStart(2, '0')}월 ${d.getDate().toString().padStart(2, '0')}일`
}

onMounted(() => {
  fetchRecipe()
  fetchLikeStatus()
})
</script>

<style scoped>
.recipe-main-image {
  width: 100%;
  object-fit: cover;
  max-height: 400px;
}

.step-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: #007bff;
  color: white;
  border-radius: 50%;
  font-weight: bold;
}

.step-image {
  max-height: 300px;
  margin-top: 1rem;
}

.text-truncate-2 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.nutrition-item {
  padding: 1rem;
  border-radius: 8px;
  background-color: #f8f9fa;
}

.related-recipe-image {
  height: 150px;
  object-fit: cover;
}

.related-recipes .card {
  cursor: pointer;
  transition: transform 0.2s;
}

.related-recipes .card:hover {
  transform: translateY(-5px);
}
</style> 