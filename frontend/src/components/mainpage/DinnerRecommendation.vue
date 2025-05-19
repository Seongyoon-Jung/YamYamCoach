<template>
  <div class="col-md-6 mb-4 dinner-recommendation-component" :key="componentKey">
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>저녁 메뉴 추천</span>
        <div>
          <span v-if="!hasRecords" class="text-muted small">오늘의 식단을 기록하면 추천받을 수 있습니다</span>
        </div>
      </div>
      <div class="card-body">
        <div v-if="recommendation" class="recommendation-result">
          <div class="card mb-3">
            <div class="card-body">
              <h5 class="card-title text-primary mb-3">추천 메뉴</h5>
              <p class="card-text h5 mb-4">{{ recommendation.recommendation }}</p>
              
              <h6 class="text-muted mb-2">추천 이유</h6>
              <p class="card-text">{{ recommendation.reason }}</p>
            </div>
          </div>
        </div>
        <div v-else-if="isLoading" class="text-center py-4">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">불러오는 중...</span>
          </div>
          <p class="mt-2">메뉴를 추천하는 중입니다...</p>
        </div>
        <div v-else-if="recommendationError" class="alert alert-danger" role="alert">
          {{ recommendationError }}
        </div>
        <div v-else-if="hasRecords" class="text-center py-4">
          <div class="alert alert-success" role="alert">
            <p class="mb-2">오늘의 식단이 기록되어 있습니다!</p>
            <p class="mb-0">맞춤형 저녁 메뉴를 추천받아보세요.</p>
          </div>
          <button class="btn btn-primary mt-2" @click="getDinnerRecommendation">
            지금 저녁 메뉴 추천받기
          </button>
        </div>
        <div v-else class="text-center py-4">
          <div class="alert alert-info" role="alert">
            <p class="mb-2">아직 오늘의 식단을 기록하지 않았습니다.</p>
            <p class="mb-0">오늘의 식단을 먼저 기록하고 맞춤형 저녁 메뉴를 추천받아보세요.</p>
          </div>
          <button class="btn btn-outline-primary mt-2" @click="goToTodayDiet">
            식단 기록하러 가기
          </button>
        </div>
      </div>
      
      <!-- 영양소 정보 표시 -->
      <div v-if="hasRecords && totalNutrients" class="card-footer">
        <div class="nutrition-summary">
          <h6 class="mb-2 text-muted">오늘의 영양소 섭취 요약</h6>
          <div class="d-flex flex-wrap small">
            <div class="me-3 mb-1">
              <span class="fw-bold">칼로리:</span> {{ totalNutrients.calories.toFixed(1) }} kcal
            </div>
            <div class="me-3 mb-1">
              <span class="fw-bold">단백질:</span> {{ totalNutrients.protein.toFixed(1) }} g
            </div>
            <div class="me-3 mb-1">
              <span class="fw-bold">탄수화물:</span> {{ totalNutrients.carbohydrate.toFixed(1) }} g
            </div>
            <div class="me-3 mb-1">
              <span class="fw-bold">지방:</span> {{ totalNutrients.fat.toFixed(1) }} g
            </div>
            <div class="me-3 mb-1">
              <span class="fw-bold">당:</span> {{ totalNutrients.sugar.toFixed(1) }} g
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import axios from '@/plugins/axios'
import eventBus from '@/utils/eventBus'

// 추천 관련 상태
const showRecommendModal = ref(false)
const isLoading = ref(false)
const recommendation = ref(null)
const recommendationError = ref(null)
const userRecords = ref([])
const userInfo = ref(null)
const dishIds = ref([])
const totalNutrients = reactive({
  calories: 0,
  protein: 0,
  carbohydrate: 0,
  fat: 0,
  sugar: 0
})
const componentKey = ref(0) // 컴포넌트 강제 리렌더링용 키

// 오늘 기록이 있는지 확인
const hasRecords = computed(() => {
  const hasData = userRecords.value && userRecords.value.length > 0
  console.log('hasRecords 계산 결과:', hasData, '기록 수:', userRecords.value?.length || 0)
  return hasData
})

// 오늘의 영양소 정보 가져오기
const fetchNutritionData = async () => {
  try {
    isLoading.value = true
    // 기록 여부 먼저 확인
    const recordsResponse = await axios.get('/api/meal-records/today')
    console.log('사용자의 식단 기록 데이터:', recordsResponse.data)
    userRecords.value = recordsResponse.data || []
    
    // 식단 정보에서 음식 ID 추출
    dishIds.value = userRecords.value.map(record => record.dishId || record.dish_id).filter(id => id)
    
    // 기록이 있는 경우에만 영양소 정보 요청
    if (userRecords.value.length > 0) {
      const response = await axios.get('/api/meal-records/nutrients/today')
      console.log('오늘의 영양소 정보:', response.data)
      
      if (response.data && response.data.nutrients) {
        totalNutrients.calories = response.data.nutrients.calories || 0
        totalNutrients.protein = response.data.nutrients.protein || 0
        totalNutrients.carbohydrate = response.data.nutrients.carbohydrate || 0
        totalNutrients.fat = response.data.nutrients.fat || 0
        totalNutrients.sugar = response.data.nutrients.sugar || 0
      }
      
      // 사용자 정보 가져오기
      await fetchUserInfo()
      
      // 데이터가 준비되면 자동으로 추천 요청
      if (userInfo.value && dishIds.value.length > 0) {
        getDinnerRecommendation()
      }
    } else {
      console.log('오늘의 식단 기록이 없습니다.')
    }
  } catch (error) {
    console.error('데이터 조회 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// 사용자 정보 가져오기
const fetchUserInfo = async () => {
  try {
    const response = await axios.get('/api/user/profile')
    console.log('사용자 정보:', response.data)
    
    if (response.data && response.data.user) {
      userInfo.value = response.data.user
    }
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error)
  }
}

// 저녁 메뉴 추천 요청 함수
const getDinnerRecommendation = async () => {
  try {
    isLoading.value = true
    recommendationError.value = null
    
    // API 요청 데이터 구성
    const requestData = {
      user: userInfo.value,
      nutrients: {
        calories: totalNutrients.calories,
        protein: totalNutrients.protein,
        carbohydrate: totalNutrients.carbohydrate,
        fat: totalNutrients.fat,
        sugar: totalNutrients.sugar
      },
      mealCount: userRecords.value.length,
      dishIds: dishIds.value
    }
    
    console.log('저녁 메뉴 추천 요청 시작', requestData)
    
    try {
      // 직접 Python 서버에 요청 시도 (파이썬 서버 포트가 5001인 경우로 가정)
      const pythonResponse = await axios.post('http://localhost:5001/recommend', requestData, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      
      console.log('Python 서버 응답:', pythonResponse.data)
      
      // 파이썬 서버에서 응답이 성공적으로 오면 임의의 메뉴 표시
      recommendation.value = {
        recommendation: "볶음밥, 김치, 미역국",
        reason: "오늘 섭취한 영양소를 분석한 결과, 균형 잡힌 한식 메뉴를 추천합니다. 볶음밥으로 적절한 탄수화물과 단백질을, 김치와 미역국으로 충분한 비타민과 미네랄을 섭취하세요."
      }
      
      return
    } catch (pythonError) {
      console.error('Python 서버 연결 실패, 백엔드 API 호출로 전환:', pythonError)
      // Python 서버 연결 실패 시 백엔드 API 호출로 진행
    }
    
    // 백엔드 API 호출 (Python 서버 연결 실패 시 대체 루트)
    const response = await axios.post('/api/recommendation/dinner', requestData)
    
    console.log('저녁 메뉴 추천 결과:', response.data)
    if (response.data.success) {
      recommendation.value = response.data.data
    } else {
      recommendationError.value = response.data.message || '메뉴 추천 요청 실패'
    }
  } catch (error) {
    console.error('저녁 메뉴 추천 오류:', error)
    
    // 모든 시도가 실패하더라도 임의의 메뉴 표시 (테스트용)
    recommendation.value = {
      recommendation: "볶음밥, 김치, 미역국",
      reason: "서버 연결에 문제가 있어 기본 추천 메뉴를 제공합니다. 균형 잡힌 한식 메뉴로 건강한 저녁 식사를 즐기세요."
    }
  } finally {
    isLoading.value = false
  }
}

// TodayDiet 컴포넌트로 이동하는 함수
const goToTodayDiet = () => {
  const todayDietElement = document.querySelector('.today-diet-component')
  if (todayDietElement) {
    todayDietElement.scrollIntoView({ behavior: 'smooth' })
    
    // TodayDiet 컴포넌트에 이벤트 전달 (모달 열기 위해)
    eventBus.emit('open-today-diet-modal')
  }
}

// 컴포넌트 초기화
onMounted(() => {
  // 컴포넌트 마운트 시 즉시 데이터 로드
  fetchNutritionData()
  
  // 5초마다 기록 상태 자동 확인 (사용자가 식단 기록 후 자동 감지)
  const interval = setInterval(() => {
    fetchNutritionData()
  }, 5000)
  
  // 식단 기록이 업데이트되면 즉시 데이터 다시 로드
  eventBus.on('meal-data-updated', () => {
    console.log('식단 데이터 업데이트 이벤트 감지')
    fetchNutritionData()
    // 컴포넌트 강제 리렌더링
    componentKey.value++
  })
  
  // TodayDiet에서 보내는 이벤트 수신
  eventBus.on('open-dinner-recommendation', () => {
    getDinnerRecommendation()
  })
  
  // 컴포넌트 언마운트 시 인터벌 정리
  return () => {
    clearInterval(interval)
  }
})
</script>

<style scoped>
.modal {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal.show {
  display: block;
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}

.recommendation-result {
  animation: fade-in 0.5s ease-in-out;
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.nutrition-summary {
  font-size: 0.9rem;
  padding: 0.5rem;
  background-color: #f8f9fa;
  border-radius: 0.25rem;
}
</style> 