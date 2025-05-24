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
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <!-- 카테고리 뱃지 -->
              <span class="badge bg-primary mb-2">{{ recipe.category }}</span>
              
              <!-- 레시피 제목 -->
              <h5 class="card-title mb-2">{{ recipe.name }}</h5>
              
              <!-- 간단한 설명 -->
              <p class="card-text text-truncate-2 mb-3">{{ recipe.description }}</p>
              
              <!-- 레시피 메타 정보 -->
              <div class="recipe-meta d-flex gap-3 text-muted small">
                <span><i class="bi bi-clock me-1"></i> {{ formatCookTime(recipe.cookTimeMinutes) }}</span>
                <span><i class="bi bi-bar-chart me-1"></i> {{ recipe.difficulty }}</span>
                <span><i class="bi bi-fire me-1"></i> {{ recipe.nutrition.calories }}kcal</span>
                <span><i class="bi bi-heart me-1"></i> {{ recipe.likes }}</span>
              </div>
            </div>
            
            <!-- 작성자 정보 -->
            <div class="text-end">
              <div class="d-flex align-items-center justify-content-end">
                <img
                  :src="recipe.authorProfileUrl || '/images/default-profile.jpg'"
                  alt="프로필"
                  class="rounded-circle me-2"
                  width="30"
                  height="30"
                />
                <span class="text-muted small">{{ recipe.authorName }}</span>
              </div>
              <div class="text-muted small mt-1">
                {{ formatDate(recipe.createdAt) }}
              </div>
            </div>
          </div>
          
          <!-- 레시피 요약 영양 정보 -->
          <div class="nutrition-summary mt-3">
            <div class="row">
              <div class="col-md-6">
                <!-- 재료 리스트 (최대 4개만 표시) -->
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
              <div class="col-md-6">
                <!-- 영양소 정보 -->
                <div class="d-flex justify-content-end gap-3 text-muted small">
                  <span>단백질: {{ recipe.nutrition.protein }}g</span>
                  <span>탄수화물: {{ recipe.nutrition.carbs }}g</span>
                  <span>지방: {{ recipe.nutrition.fat }}g</span>
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
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  recipe: {
    type: Object,
    required: true
  }
})

const router = useRouter()

// 상세 페이지로 이동
const goToDetail = () => {
  router.push(`/recipe/${props.recipe.id}`)
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

.nutrition-summary {
  border-top: 1px solid #eee;
  padding-top: 0.5rem;
}

@media (max-width: 768px) {
  .recipe-image {
    height: 150px;
    width: 100%;
  }
}
</style> 