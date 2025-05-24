<template>
  <div class="recipe-page container py-5">
    <!-- 제목 -->
    <h2 class="text-center mb-4">레시피 검색</h2>

    <!-- 작성 버튼 추가 -->
    <div class="text-end mb-3">
      <router-link class="btn btn-success" to="/recipe/create">레시피 작성</router-link>
    </div>

    <!-- 검색 바 -->
    <div class="d-flex justify-content-center mb-3">
      <div class="input-group w-75">
        <input
          :value="searchQuery"
          @input="onInputChange"
          type="text"
          class="form-control rounded-pill border-0 shadow-sm"
          placeholder="레시피 이름, 재료, 카테고리 등을 검색해보세요!"
        />
        <span class="input-group-text bg-white border-0 shadow-sm rounded-pill ms-n5">
          <i class="bi bi-search"></i>
        </span>
      </div>
    </div>

    <!-- 필터 바 -->
    <div
      class="d-flex justify-content-between align-items-center bg-light p-3 rounded shadow-sm mb-4"
    >
      <div class="d-flex gap-3">
        <!-- 카테고리 필터 -->
        <div class="dropdown">
          <button 
            class="btn btn-outline-secondary dropdown-toggle" 
            type="button" 
            id="categoryDropdown" 
            data-bs-toggle="dropdown" 
            aria-expanded="false"
          >
            {{ selectedCategory || '카테고리' }}
          </button>
          <ul class="dropdown-menu" aria-labelledby="categoryDropdown">
            <li><a class="dropdown-item" href="#" @click.prevent="selectCategory('')">전체</a></li>
            <li v-for="category in categories" :key="category">
              <a class="dropdown-item" href="#" @click.prevent="selectCategory(category)">{{ category }}</a>
            </li>
          </ul>
        </div>

        <!-- 조리 시간 필터 -->
        <div class="dropdown">
          <button 
            class="btn btn-outline-secondary dropdown-toggle" 
            type="button" 
            id="timeDropdown" 
            data-bs-toggle="dropdown" 
            aria-expanded="false"
          >
            {{ selectedTime || '조리 시간' }}
          </button>
          <ul class="dropdown-menu" aria-labelledby="timeDropdown">
            <li><a class="dropdown-item" href="#" @click.prevent="selectTime('')">전체</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent="selectTime('15분 이내')">15분 이내</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent="selectTime('30분 이내')">30분 이내</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent="selectTime('1시간 이내')">1시간 이내</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent="selectTime('1시간 이상')">1시간 이상</a></li>
          </ul>
        </div>

        <!-- 난이도 필터 -->
        <div class="dropdown">
          <button 
            class="btn btn-outline-secondary dropdown-toggle" 
            type="button" 
            id="difficultyDropdown" 
            data-bs-toggle="dropdown" 
            aria-expanded="false"
          >
            {{ selectedDifficulty || '난이도' }}
          </button>
          <ul class="dropdown-menu" aria-labelledby="difficultyDropdown">
            <li><a class="dropdown-item" href="#" @click.prevent="selectDifficulty('')">전체</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent="selectDifficulty('쉬움')">쉬움</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent="selectDifficulty('보통')">보통</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent="selectDifficulty('어려움')">어려움</a></li>
          </ul>
        </div>
      </div>

      <!-- 정렬 옵션 -->
      <div class="dropdown">
        <button 
          class="btn btn-link text-decoration-none dropdown-toggle" 
          type="button"
          id="sortDropdown" 
          data-bs-toggle="dropdown" 
          aria-expanded="false"
        >
          {{ getSortText() }}
        </button>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="sortDropdown">
          <li><a class="dropdown-item" href="#" @click.prevent="sortBy = 'popular'">인기순</a></li>
          <li><a class="dropdown-item" href="#" @click.prevent="sortBy = 'latest'">최신순</a></li>
          <li><a class="dropdown-item" href="#" @click.prevent="sortBy = 'calorie_asc'">칼로리 낮은순</a></li>
          <li><a class="dropdown-item" href="#" @click.prevent="sortBy = 'calorie_desc'">칼로리 높은순</a></li>
        </ul>
      </div>
    </div>

    <!-- 레시피 리스트 -->
    <div v-if="filteredRecipes.length > 0" class="recipe-list">
      <div v-for="recipe in filteredRecipes" :key="recipe.id" class="recipe-item mb-3">
        <RecipeListItem :recipe="recipe" />
      </div>

      <!-- 더 보기 버튼 -->
      <div class="text-center mt-4" v-if="canLoadMore">
        <button class="btn btn-outline-primary px-4" @click="loadMore">
          더 보기 <i class="bi bi-chevron-down"></i>
        </button>
      </div>
    </div>

    <!-- 검색 결과가 없는 경우 -->
    <div v-else class="text-center py-5">
      <div class="mb-3">
        <i class="bi bi-search fs-1 text-muted"></i>
      </div>
      <h4 class="text-muted">검색 결과가 없습니다</h4>
      <p class="text-muted">다른 검색어나 필터 조건을 사용해보세요</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from '@/plugins/axios'
import RecipeListItem from '@/components/recipe/RecipeListItem.vue'

// 검색어
const searchQuery = ref('')
// 필터 상태
const selectedCategory = ref('')
const selectedTime = ref('')
const selectedDifficulty = ref('')
// 정렬 방식
const sortBy = ref('popular')
// 레시피 데이터
const recipes = ref([])
// 페이지네이션
const displayCount = ref(10)
const totalCount = ref(0)

// 카테고리 목록 (API에서 받아올 수도 있음)
const categories = [
  '한식', '양식', '중식', '일식', '동남아', 
  '채식', '저칼로리', '고단백', '간편요리', '디저트'
]

// 검색어 입력 핸들러
const onInputChange = (e) => {
  searchQuery.value = e.target.value
}

// 카테고리 선택
const selectCategory = (category) => {
  selectedCategory.value = category
}

// 조리시간 선택
const selectTime = (time) => {
  selectedTime.value = time
}

// 난이도 선택
const selectDifficulty = (difficulty) => {
  selectedDifficulty.value = difficulty
}

// 정렬 텍스트 표시
const getSortText = () => {
  switch (sortBy.value) {
    case 'popular': return '인기순'
    case 'latest': return '최신순'
    case 'calorie_asc': return '칼로리 낮은순'
    case 'calorie_desc': return '칼로리 높은순'
    default: return '정렬'
  }
}

// 필터링된 레시피
const filteredRecipes = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  
  let result = recipes.value.filter(recipe => {
    // 검색어 필터링
    if (q && !(
      recipe.name.toLowerCase().includes(q) || 
      recipe.ingredients.some(i => i.toLowerCase().includes(q)) ||
      recipe.category.toLowerCase().includes(q)
    )) {
      return false
    }
    
    // 카테고리 필터링
    if (selectedCategory.value && recipe.category !== selectedCategory.value) {
      return false
    }
    
    // 조리시간 필터링
    if (selectedTime.value) {
      const cookTimeMinutes = recipe.cookTimeMinutes
      
      if (selectedTime.value === '15분 이내' && cookTimeMinutes > 15) return false
      if (selectedTime.value === '30분 이내' && cookTimeMinutes > 30) return false
      if (selectedTime.value === '1시간 이내' && cookTimeMinutes > 60) return false
      if (selectedTime.value === '1시간 이상' && cookTimeMinutes < 60) return false
    }
    
    // 난이도 필터링
    if (selectedDifficulty.value && recipe.difficulty !== selectedDifficulty.value) {
      return false
    }
    
    return true
  })
  
  // 정렬 적용
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'popular':
        return b.likes - a.likes
      case 'latest':
        return new Date(b.createdAt) - new Date(a.createdAt)
      case 'calorie_asc':
        return a.nutrition.calories - b.nutrition.calories
      case 'calorie_desc':
        return b.nutrition.calories - a.nutrition.calories
      default:
        return 0
    }
  })
  
  // 페이지네이션 적용
  totalCount.value = result.length
  return result.slice(0, displayCount.value)
})

// 더 불러올 수 있는지 여부
const canLoadMore = computed(() => {
  return displayCount.value < totalCount.value
})

// 더 보기 버튼 클릭 핸들러
const loadMore = () => {
  displayCount.value += 10
}

// 레시피 데이터 가져오기
const fetchRecipes = async () => {
  try {
    const response = await axios.get('/api/recipes')
    recipes.value = response.data
  } catch (error) {
    console.error('레시피 데이터 로딩 실패:', error)
  }
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchRecipes()
})
</script>

<style scoped>
.recipe-page input::placeholder {
  color: #aaa;
}

.recipe-list {
  animation: fadeIn 0.5s ease;
}

.recipe-item {
  transition: transform 0.2s;
}

.recipe-item:hover {
  transform: translateY(-2px);
}

/* 검색창 그림자와 원형 처리 */
.input-group .form-control {
  border-radius: 50px !important;
}

.input-group .input-group-text {
  border-radius: 50px !important;
  z-index: 2;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style> 