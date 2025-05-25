<template>
  <div class="recipe-list-item card shadow-sm border-0" @click="goToDetail">
    <div class="row g-0">
      <!-- 레시피 이미지 -->
      <div class="col-md-3">
        <img 
          :src="recipe.imageUrl || '/images/default-recipe.jpg'" 
          class="img-fluid rounded-start recipe-image" 
          alt="레시피 이미지"
        />
      </div>
      
      <!-- 레시피 정보 -->
      <div class="col-md-9">
        <div class="card-body d-flex flex-column justify-content-center align-items-center position-relative" style="min-height: 180px;">
          <div class="w-100 text-center">
            <!-- 레시피 제목 -->
            <h5 class="card-title mb-2">{{ recipe.name }}</h5>
            <!-- 레시피 메타 정보 -->
            <div class="recipe-meta d-flex justify-content-center gap-3 text-muted small mb-2">
              <span><i class="bi bi-clock me-1"></i> {{ formatCookTime(recipe.cookTimeMinutes) }}</span>
              <span><i class="bi bi-bar-chart me-1"></i> {{ recipe.difficulty }}</span>
              <!-- 카테고리 뱃지 -->
              <span class="badge bg-primary mb-3">{{ recipe.category }}</span>
            </div>
            <!-- 간단한 설명 -->
            <p class="card-text text-truncate-2 mb-3">{{ recipe.description }}</p>
          </div>
          <!-- 좋아요 버튼 (구분선 위, 오른쪽 정렬) -->
          <div class="w-100 d-flex justify-content-end mb-2">
            <button class="btn btn-outline-danger d-flex align-items-center gap-1 like-btn" @click.stop="toggleLike">
              <i :class="liked ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
              <span class="like-count text-danger ms-1">{{ Number.isFinite(recipe.likes) ? recipe.likes : 0 }}</span>
            </button>
          </div>
          <!-- 재료+작성자+날짜 행은 그대로 아래에 유지 -->
          <div class="recipe-summary mt-3 w-100">
            <div class="row g-3">
              <div class="col-md-7">
                <div class="ingredients-preview">
                  <span class="text-muted small me-2">재료:</span>
                  <span v-for="(ingredient, index) in displayIngredients" :key="index" class="ingredient-tag">
                    {{ ingredient }}
                  </span>
                  <span v-if="recipe.ingredients.length > 4" class="ingredient-more">
                    +{{ recipe.ingredients.length - 4 }}
                  </span>
                </div>
              </div>
              <div class="col-md-5">
                <div class="d-flex align-items-center justify-content-end">
                  <img
                    :src="recipe.authorProfileUrl || '/images/default-profile.jpg'"
                    alt="프로필"
                    class="rounded-circle me-2"
                    width="24"
                    height="24"
                  />
                  <span class="text-muted small me-2">{{ recipe.authorName }}</span>
                  <span class="text-muted small">{{ formatDate(recipe.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'

const props = defineProps({
  recipe: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const liked = ref(false)
const accountStore = userAccountStore()

// 현재 사용자가 작성자인지 확인
const isAuthor = computed(() => {
  if (!props.recipe || !accountStore.userId) return false
  return props.recipe.userId === accountStore.userId || 
         props.recipe.authorId === accountStore.userId
})

// 상세 페이지로 이동
const goToDetail = () => {
  router.push(`/recipe/${props.recipe.id}`)
}

// 수정 페이지로 이동
const editRecipe = () => {
  router.push(`/recipe/edit/${props.recipe.id}`)
}

// 레시피 삭제 처리
const deleteRecipe = async () => {
  if (!confirm('정말로 이 레시피를 삭제하시겠습니까?')) return
  
  try {
    await axios.delete(`/api/recipes/${props.recipe.id}`)
    alert('레시피가 삭제되었습니다.')
    // 페이지 새로고침하여 목록 갱신
    window.location.reload()
  } catch (err) {
    console.error('레시피 삭제 실패:', err)
    alert('레시피 삭제 중 오류가 발생했습니다.')
  }
}

// 좋아요 상태 확인
const checkLikeStatus = async () => {
  try {
    const response = await axios.get(`/api/recipes/${props.recipe.id}/like-status`)
    liked.value = response.data.liked
  } catch (err) {
    console.error('좋아요 상태 확인 실패:', err)
    liked.value = false
  }
}

// 좋아요 토글
const toggleLike = async () => {
  try {
    if (liked.value) {
      await axios.delete(`/api/recipes/${props.recipe.id}/like`)
      props.recipe.likes--
    } else {
      await axios.post(`/api/recipes/${props.recipe.id}/like`)
      props.recipe.likes++
    }
    liked.value = !liked.value
  } catch (err) {
    console.error('좋아요 처리 중 오류:', err)
  }
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
  return `${d.getFullYear()}.${(d.getMonth() + 1).toString().padStart(2, '0')}.${d.getDate().toString().padStart(2, '0')}`
}

// 표시할 재료 (최대 4개)
const displayIngredients = computed(() => {
  return props.recipe.ingredients.slice(0, 4)
})

onMounted(() => {
  checkLikeStatus()
})
</script>

<style scoped>
.recipe-list-item {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  overflow: hidden;
}

.recipe-list-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1) !important;
}

.recipe-image {
  height: 100%;
  object-fit: cover;
  min-height: 180px;
}

.text-truncate-2 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.ingredient-tag {
  display: inline-block;
  background-color: #f0f0f0;
  padding: 0.15rem 0.5rem;
  border-radius: 50px;
  margin-right: 0.3rem;
  margin-bottom: 0.3rem;
  font-size: 0.8rem;
}

.ingredient-more {
  display: inline-block;
  color: #6c757d;
  font-size: 0.8rem;
}

.recipe-summary {
  border-top: 1px solid #eee;
  padding-top: 0.75rem;
}

.like-btn {
  font-size: 1rem;
  padding: 0.2rem 0.8rem;
  border-radius: 1.5rem;
  background: transparent;
  border-width: 1.2px;
  height: 36px;
  line-height: 1;
  box-shadow: none !important;
}

.like-btn:active, .like-btn:focus {
  outline: none;
  box-shadow: none !important;
}

.like-count {
  font-weight: bold;
  font-size: 1.05rem;
  background: none !important;
  border: none !important;
  padding: 0;
}

.card-body {
  position: relative;
}

.card-body .card-title {
  font-weight: bold;
}

@media (max-width: 768px) {
  .recipe-image {
    height: 150px;
    width: 100%;
  }
  
  .recipe-summary .row {
    flex-direction: column;
  }
  
  .recipe-summary .col-md-5 {
    margin-top: 0.5rem;
  }
  .card-body {
    min-height: 120px !important;
  }
  .like-btn {
    bottom: 0.5rem !important;
    right: 0.5rem !important;
    padding: 0.2rem 0.8rem;
    font-size: 0.95rem;
  }
}
</style> 