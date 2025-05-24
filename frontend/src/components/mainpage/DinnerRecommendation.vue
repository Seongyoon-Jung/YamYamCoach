<template>
  <div
    class="dinner-recommendation-component h-100"
    :key="componentKey"
    style="height: 100%; overflow-y: auto"
  >
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>저녁 메뉴 추천</span>
        <div>
          <span v-if="!hasRecords" class="text-muted small"
            >오늘의 식단을 기록하면 추천받을 수 있습니다</span
          >
        </div>
      </div>
      <div class="card-body">
        <div v-if="recommendation && rank1Recommendations.length > 0" class="recommendation-result">
          <div class="card mb-3 overflow-visible">
            <div class="card-body position-relative">
              <h5 class="card-title text-primary mb-3">추천 메뉴</h5>
              <p class="card-text h5 mb-4">{{ rank1Names }}</p>
              
              <!-- 추천 더보기 버튼을 오른쪽 하단에 배치 -->
              <div class="d-flex justify-content-end mt-3">
                <div class="position-relative">
                  <button 
                    class="btn btn-outline-secondary btn-sm"
                    style="font-size: 0.8rem;"
                    @mouseover="handleButtonHover"
                    @mouseleave="handleButtonLeave"
                    ref="buttonRef"
                  >
                    <i class="bi bi-three-dots"></i> 다른 추천
                  </button>
                  
                  <!-- Portal을 사용하여 팝업을 body에 직접 마운트 -->
                  <Teleport to="body">
                    <div 
                      v-if="showMoreRecommendations" 
                      class="more-recommendations-popup"
                      :style="[popupPosition, { visibility: isHoverPathValid ? 'visible' : 'hidden' }]"
                      @mouseover="handlePopupHover"
                      @mouseleave="handlePopupLeave"
                    >
                      <div v-if="rank2Recommendations.length > 0" class="recommendation-item">
                        <h6 class="text-primary fw-bold mb-2">2순위 추천 메뉴</h6>
                        <div class="recommendation-content">
                          <div class="food-items">
                            <span 
                              v-for="(food, index) in rank2Recommendations" 
                              :key="food.id"
                              class="food-item"
                              @click="openRecipe(food.name)"
                            >
                              {{ food.name }}
                              <i class="bi bi-box-arrow-up-right ms-1"></i>
                              <span v-if="index < rank2Recommendations.length - 1">, </span>
                            </span>
                          </div>
                        </div>
                      </div>
                      <hr v-if="rank2Recommendations.length > 0 && rank3Recommendations.length > 0" class="my-2">
                      <div v-if="rank3Recommendations.length > 0" class="recommendation-item">
                        <h6 class="text-primary fw-bold mb-2">3순위 추천 메뉴</h6>
                        <div class="recommendation-content">
                          <div class="food-items">
                            <span 
                              v-for="(food, index) in rank3Recommendations" 
                              :key="food.id"
                              class="food-item"
                              @click="openRecipe(food.name)"
                            >
                              {{ food.name }}
                              <i class="bi bi-box-arrow-up-right ms-1"></i>
                              <span v-if="index < rank3Recommendations.length - 1">, </span>
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </Teleport>
                </div>
              </div>
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

      <!-- 추천 이후에는 rank=1 음식의 레시피 링크만 표시 -->
      <div v-if="recommendation && rank1Recommendations.length > 0" class="card-footer">
        <div class="recipe-links">
          <h6 class="mb-2 text-primary">추천 메뉴 레시피</h6>
          <div class="d-flex flex-wrap">
            <div v-for="(food, index) in rank1Recommendations" :key="food.id" class="me-3 mb-2">
              <a
                :href="getRecipeLink(food.name)"
                target="_blank"
                class="btn btn-sm btn-outline-success"
              >
                <i class="bi bi-book me-1"></i>{{ food.name }}
              </a>
            </div>
          </div>
        </div>
      </div>
      <div v-else-if="hasRecords && totalNutrients && !recommendation" class="card-footer">
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
import { ref, reactive, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import axios from '@/plugins/axios'
import eventBus from '@/utils/eventBus'

// 추천 관련 상태
const isLoading = ref(false)
const recommendation = ref(null)
const recommendationError = ref(null)
const userRecords = ref([])
const dishIds = ref([])
const userInfo = ref(null) // 실제 사용자 정보
const totalNutrients = reactive({
  calories: 0,
  protein: 0,
  carbohydrate: 0,
  fat: 0,
  sugar: 0,
})
const componentKey = ref(0) // 컴포넌트 강제 리렌더링용 키
const todayDietNutrients = ref(null) // TodayDiet 컴포넌트로부터 받은 영양소 정보

// 추천 더보기 상태 관리
const showMoreRecommendations = ref(false)
const buttonRef = ref(null)
const popupPosition = ref({
  top: '0px',
  left: '0px'
})

// hover 상태 관리를 위한 변수 추가
const isHoverPathValid = ref(false)
let hoverTimeout = null

// hover 핸들러 함수들
const handleButtonHover = () => {
  showMoreRecommendations.value = true
  clearTimeout(hoverTimeout)
  hoverTimeout = setTimeout(() => {
    isHoverPathValid.value = true
  }, 100)
}

const handleButtonLeave = () => {
  clearTimeout(hoverTimeout)
  hoverTimeout = setTimeout(() => {
    if (!isHoverPathValid.value) {
      showMoreRecommendations.value = false
    }
  }, 300)
}

const handlePopupHover = () => {
  clearTimeout(hoverTimeout)
  isHoverPathValid.value = true
}

const handlePopupLeave = () => {
  isHoverPathValid.value = false
  clearTimeout(hoverTimeout)
  hoverTimeout = setTimeout(() => {
    showMoreRecommendations.value = false
  }, 300)
}

// 음식 이름에 따른 레시피 검색 링크 생성
const getRecipeLink = (foodName) => {
  // 10000개 레시피 사이트에서 검색하는 URL
  return `https://www.10000recipe.com/recipe/list.html?q=${encodeURIComponent(foodName)}`
}

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
    console.log('영양소 및 식단 데이터 가져오기 시작...')

    // 기록 여부 먼저 확인
    const recordsResponse = await axios.get('/api/meal-records/today')
    console.log('식단 기록 API 응답:', recordsResponse.data)
    userRecords.value = recordsResponse.data || []

    // 식단 정보에서 음식 ID 추출
    dishIds.value = userRecords.value
      .map((record) => record.dishId || record.dish_id)
      .filter((id) => id)
    console.log('추출된 음식 ID 목록:', dishIds.value)

    // 기록이 있는 경우에만 영양소 정보 요청
    if (userRecords.value.length > 0) {
      console.log('영양소 정보 요청 시작...')
      const response = await axios.get('/api/meal-records/nutrients/today')
      console.log('영양소 정보 API 응답:', response.data)

      if (response.data && response.data.nutrients) {
        console.log('영양소 정보 적용 전:', totalNutrients)
        Object.assign(totalNutrients, response.data.nutrients)
        console.log('영양소 정보 적용 후:', totalNutrients)
      } else {
        console.warn('영양소 정보가 없거나 형식이 다릅니다:', response.data)
      }

      // 사용자 정보 가져오기
      await fetchUserInfo()
    } else {
      console.log('오늘의 식단 기록이 없습니다. 영양소 정보를 가져오지 않습니다.')
      recommendation.value = null
      // 기본 영양소 데이터 설정
      Object.assign(totalNutrients, {
        calories: 0,
        protein: 0,
        carbohydrate: 0,
        fat: 0,
        sugar: 0,
      })
    }
  } catch (error) {
    console.error('영양소 데이터 조회 실패:', error)
    console.error('에러 상세:', error.response || error.message)
  } finally {
    isLoading.value = false
  }
}

// 사용자 정보 가져오기
const fetchUserInfo = async () => {
  try {
    console.log('사용자 정보 요청 시작...')
    const response = await axios.get('/api/user/profile')
    console.log('사용자 정보 응답 전체:', response)

    if (response.data && response.data.success && response.data.user) {
      console.log('사용자 정보 가져오기 성공:', response.data.user)

      const userData = response.data.user

      // 페르소나 ID가 있으면 질병 태그 정보도 함께 가져오도록 추가 요청
      if (userData.personaId) {
        try {
          console.log('사용자의 페르소나 ID:', userData.personaId)
          const personaResponse = await axios.get(`/api/personas/${userData.personaId}`)

          if (personaResponse.data && personaResponse.data.success) {
            console.log('페르소나 정보 가져오기 성공:', personaResponse.data.persona)
            // diseaseTags 정보 추가
            userData.diseaseTags = personaResponse.data.persona.diseaseTags || ''
            console.log('추가된 질병 태그 정보:', userData.diseaseTags)
          }
        } catch (personaError) {
          console.warn('페르소나 정보 조회 실패, 기본값 사용:', personaError)
          userData.diseaseTags = ''
        }
      } else {
        userData.diseaseTags = ''
      }

      userInfo.value = userData
      return true
    } else {
      console.warn('사용자 정보를 가져올 수 없습니다. 응답:', response.data)
      // 기본 사용자 정보 설정
      userInfo.value = {
        gender: true, // true는 여성, false는 남성
        age: 30,
        height: 165,
        weight: 60,
        targetWeight: 55,
        diseaseTags: '',
      }
      console.log('기본 사용자 정보 사용:', userInfo.value)
    }
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error)
    console.error('에러 상세 정보:', error.response || error.message)
    // 기본 사용자 정보 설정
    userInfo.value = {
      gender: true, // true는 여성, false는 남성
      age: 30,
      height: 165,
      weight: 60,
      targetWeight: 55,
      diseaseTags: '',
    }
    console.log('오류로 인한 기본 사용자 정보 사용:', userInfo.value)
  }
}

// 저녁 메뉴 추천 요청 함수
const getDinnerRecommendation = async () => {
  try {
    isLoading.value = true
    recommendationError.value = null
    recommendation.value = null // 이전 추천 결과 초기화

    // 사용자 정보와 영양소 정보 업데이트
    console.log('추천 요청 전 데이터 새로고침 시작...')
    await fetchNutritionData()

    // TodayDiet 컴포넌트에서 영양소 정보 요청
    eventBus.emit('request-today-diet-nutrients')

    // 잠시 대기하여 TodayDiet 컴포넌트가 응답할 시간을 줌
    await new Promise((resolve) => setTimeout(resolve, 300))

    // 사용자 정보 확인 및 업데이트
    if (!userInfo.value) {
      console.log('사용자 정보가 없어 가져오기 시도...')
      await fetchUserInfo()
    }

    console.log('추천에 사용될 사용자 정보:', userInfo.value)
    console.log('API에서 가져온 영양소 정보:', totalNutrients)
    console.log('TodayDiet에서 가져온 영양소 정보:', todayDietNutrients.value)
    console.log('추천에 사용될 음식 ID 목록:', dishIds.value)
    console.log('추천에 사용될 식사 횟수:', userRecords.value.length)

    // 사용할 영양소 정보 결정 (TodayDiet 정보가 있으면 그것을 우선 사용)
    const nutrientsToUse = todayDietNutrients.value || totalNutrients

    // API 요청 데이터 구성 - 실제 사용자 정보 사용
    const requestData = {
      user: userInfo.value,
      nutrients: {
        calories: nutrientsToUse.calories || 0,
        protein: nutrientsToUse.protein || 0,
        carbohydrate: nutrientsToUse.carbohydrate || 0,
        fat: nutrientsToUse.fat || 0,
        sugar: nutrientsToUse.sugar || 0,
      },
    }

    console.log('추천 요청 데이터:', JSON.stringify(requestData, null, 2))

    // Python 서버로 직접 요청
    console.log('Python 서버 직접 호출 시도...')
    const pythonResponse = await axios.post('http://localhost:5001/recommend', requestData, {
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: false,
    })

    console.log('Python 서버 응답:', pythonResponse.data)

    // 응답 처리
    if (pythonResponse.data) {
      // API 응답을 recommendation 상태에 저장
      recommendation.value = pythonResponse.data
      console.log('추천 데이터:', recommendation.value)
    } else {
      recommendationError.value = '추천 메뉴를 받아오지 못했습니다. 서버 응답을 확인해주세요.'
    }
  } catch (error) {
    console.error('메뉴 추천 요청 실패:', error)
    recommendationError.value = '메뉴 추천 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
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

// rank=1인 추천만 추출하는 computed (API 응답 recommendations만 사용)
const rank1Recommendations = computed(() => {
  if (!recommendation.value || !recommendation.value.recommendations) return []
  return recommendation.value.recommendations.filter((item) => Number(item.rank) === 1)
})

// 음식 이름만 쉼표로 이어붙인 문자열
const rank1Names = computed(() => {
  return rank1Recommendations.value.map((food) => food.name).join(', ')
})

// rank별 추천 computed 속성 추가
const rank2Recommendations = computed(() => {
  if (!recommendation.value || !recommendation.value.recommendations) return []
  return recommendation.value.recommendations.filter((item) => Number(item.rank) === 2)
})

const rank3Recommendations = computed(() => {
  if (!recommendation.value || !recommendation.value.recommendations) return []
  return recommendation.value.recommendations.filter((item) => Number(item.rank) === 3)
})

// rank별 이름 computed 속성 추가
const rank2Names = computed(() => {
  return rank2Recommendations.value.map((food) => food.name).join(', ')
})

const rank3Names = computed(() => {
  return rank3Recommendations.value.map((food) => food.name).join(', ')
})

// 레시피 링크 열기 함수
const openRecipe = (foodName) => {
  const recipeUrl = getRecipeLink(foodName)
  window.open(recipeUrl, '_blank')
}

// 팝업 위치 계산 함수
const updatePopupPosition = () => {
  if (!buttonRef.value) return
  
  const buttonRect = buttonRef.value.getBoundingClientRect()
  const screenWidth = window.innerWidth
  
  // 오른쪽에 충분한 공간이 있는지 확인
  if (buttonRect.right + 220 < screenWidth) {
    // 오른쪽에 표시
    popupPosition.value = {
      position: 'fixed',
      top: `${buttonRect.top}px`,
      left: `${buttonRect.right + 10}px`
    }
  } else {
    // 왼쪽에 표시
    popupPosition.value = {
      position: 'fixed',
      top: `${buttonRect.top}px`,
      left: `${buttonRect.left - 210}px`
    }
  }
}

// 이벤트 리스너 등록 및 해제
onMounted(() => {
  window.addEventListener('scroll', updatePopupPosition)
  window.addEventListener('resize', updatePopupPosition)
})

onUnmounted(() => {
  window.removeEventListener('scroll', updatePopupPosition)
  window.removeEventListener('resize', updatePopupPosition)
  clearTimeout(hoverTimeout)
})

// showMoreRecommendations가 변경될 때마다 위치 업데이트
watch(showMoreRecommendations, (newValue) => {
  if (newValue) {
    nextTick(() => {
      updatePopupPosition()
    })
  }
})

// 컴포넌트 초기화
onMounted(async () => {
  try {
    // 사용자 정보 먼저 로드
    await fetchUserInfo()
    // 컴포넌트 마운트 시 초기 데이터 로드
    await fetchNutritionData()

    // 식단 기록이 업데이트될 때만 데이터 다시 로드
    eventBus.on('meal-data-updated', async () => {
      console.log('식단 데이터 업데이트 이벤트 감지')
      await fetchNutritionData()
    })

    // TodayDiet에서 보내는 추천 요청 이벤트 수신
    eventBus.on('open-dinner-recommendation', () => {
      getDinnerRecommendation()
    })

    // TodayDiet에서 보내는 영양소 정보 수신
    eventBus.on('today-diet-nutrients', (nutrients) => {
      console.log('TodayDiet로부터 영양소 정보 수신:', nutrients)
      todayDietNutrients.value = nutrients
    })
  } catch (error) {
    console.error('컴포넌트 초기화 중 오류:', error)
  }

  // 이벤트 리스너 정리
  return () => {
    eventBus.off('meal-data-updated')
    eventBus.off('open-dinner-recommendation')
    eventBus.off('today-diet-nutrients')
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

.card-header {
  min-height: 64px;
  height: 64px;
  display: flex;
  align-items: center;
}

.more-recommendations-popup {
  z-index: 9999;
  min-width: 280px;
  max-width: 350px;
  padding: 1rem;
  background-color: white;
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.recommendation-item {
  margin-bottom: 0.5rem;
}

.recommendation-item:last-child {
  margin-bottom: 0;
}

.recommendation-content {
  font-size: 0.95rem;
}

.food-items {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
  color: #495057;
  line-height: 1.4;
}

.food-item {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  transition: all 0.2s ease;
}

.food-item:hover {
  color: #0d6efd;
  background-color: #f8f9fa;
}

.food-item i {
  font-size: 0.75rem;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.food-item:hover i {
  opacity: 1;
}

/* 호버 경로를 위한 투명 영역 */
.more-recommendations-popup::after {
  content: '';
  position: absolute;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  z-index: -1;
}

@media (max-width: 768px) {
  .more-recommendations-popup {
    position: fixed;
    top: 50% !important;
    left: 50% !important;
    transform: translate(-50%, -50%);
    margin: 0;
    width: 90%;
    max-width: 320px;
    max-height: 80vh;
    overflow-y: auto;
  }

  .more-recommendations-popup::before {
    display: none;
  }
}

.recipe-links {
  margin-top: 0.5rem;
}

.recipe-links .btn {
  font-size: 0.85rem;
  padding: 0.25rem 0.75rem;
}

.card.overflow-visible {
  overflow: visible !important;
}

.card-body.position-relative {
  overflow: visible !important;
}
</style>

