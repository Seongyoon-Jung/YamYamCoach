<template>
  <div class="col-md-6 mb-4 today-diet-component">
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>오늘의 식단</span>
        <div>
          <button class="btn btn-sm btn-outline-primary" @click="showModal = true">
            {{ modalButtonText }}
          </button>
        </div>
      </div>
      <div class="card-body">
        <div class="row">
          <div v-for="course in courses" :key="course.type" class="col-6 text-center">
            <div v-if="course.dishes && course.dishes.length > 0">
              <img
                :src="getDishImage(course.dishes[0].name)"
                class="rounded mb-2"
                style="width: 120px; height: 80px; object-fit: cover"
                :alt="course.dishes[0].name"
              />
            </div>
            <div class="fw-bold">{{ course.course_name }}</div>
          </div>
        </div>
        <div v-if="courses && courses.length === 0" class="text-muted mt-3">
          오늘의 식단 정보가 없습니다.
        </div>
      </div>
    </div>

    <!-- 코스 선택 모달 -->
    <div class="modal fade" :class="{ show: showModal }" tabindex="-1" v-if="showModal">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">식사 기록 - {{ getCurrentCourseName }}</h5>
            <button type="button" class="btn-close" @click="showModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="row">
              <!-- 왼쪽: 음식 선택 부분 -->
              <div class="col-md-6">
                <div class="meal-selection mb-3">
                  <ul class="nav nav-tabs" id="modalCourseTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                      <button
                        class="nav-link"
                        :class="{ active: selectedCourse === 'A' }"
                        @click="changeSelectedCourse('A')"
                      >
                        A 코스
                      </button>
                    </li>
                    <li class="nav-item" role="presentation">
                      <button
                        class="nav-link"
                        :class="{ active: selectedCourse === 'B' }"
                        @click="changeSelectedCourse('B')"
                      >
                        B 코스
                      </button>
                    </li>
                  </ul>

                  <div class="tab-content mt-3">
                    <div
                      v-for="course in courses"
                      :key="course.type"
                      class="tab-pane fade"
                      :class="{ 'show active': selectedCourse === course.type }"
                    >
                      <div v-if="course.dishes && course.dishes.length > 0">
                        <div class="p-2 border-bottom text-end">
                          <div class="text-muted">총 {{ calculateTotalCalories(course) }}Kcal</div>
                        </div>

                        <div
                          v-for="dish in course.dishes"
                          :key="dish.id"
                          class="meal-item p-3 border-bottom d-flex align-items-center justify-content-between"
                        >
                          <div class="d-flex align-items-center">
                            <div class="form-check">
                              <input
                                class="form-check-input"
                                type="checkbox"
                                :id="`dish-${dish.id}`"
                                :checked="selectedDishes[dish.id.toString()]"
                                @click="toggleDishSelection(dish.id.toString())"
                              />
                              <label class="form-check-label" :for="`dish-${dish.id}`">
                                {{ dish.name }}
                              </label>
                            </div>
                          </div>
                          <div class="text-muted">{{ dish.calorie }}Kcal</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 오른쪽: 영양소 그래프 부분 -->
              <div class="col-md-6">
                <div class="nutrition-charts" style="height: 600px; overflow-y: auto">
                  <h5 class="mb-3">영양소 정보</h5>

                  <!-- 영양소 그래프 - 누적 막대 차트 -->
                  <div class="chart-row">
                    <div
                      class="custom-chart-container"
                      style="height: 250px"
                      :key="`custom-chart-container-${_chartRenderKey}`"
                    >
                      <!-- SVG 기반 누적 막대 그래프 -->
                      <div class="svg-chart">
                        <!-- 칼로리 막대 -->
                        <div class="bar-row d-flex align-items-center mb-3">
                          <div class="bar-label" style="width: 80px">칼로리:</div>
                          <div class="progress flex-grow-1">
                            <div
                              class="progress-bar"
                              :style="{
                                width: `${Math.min(100, (totalNutrientValues.calories / (maxValues.calories * 1.2)) * 100)}%`,
                                backgroundColor: colors[0],
                              }"
                            ></div>
                          </div>
                          <div class="bar-value ml-2" style="width: 80px; text-align: right">
                            {{ totalNutrientValues.calories.toFixed(1) }} kcal
                          </div>
                        </div>

                        <!-- 단백질 막대 -->
                        <div class="bar-row d-flex align-items-center mb-3">
                          <div class="bar-label" style="width: 80px">단백질:</div>
                          <div class="progress flex-grow-1">
                            <div
                              class="progress-bar"
                              :style="{
                                width: `${Math.min(100, (totalNutrientValues.protein / (maxValues.protein * 1.2)) * 100)}%`,
                                backgroundColor: colors[1],
                              }"
                            ></div>
                          </div>
                          <div class="bar-value ml-2" style="width: 80px; text-align: right">
                            {{ totalNutrientValues.protein.toFixed(1) }} g
                          </div>
                        </div>

                        <!-- 탄수화물 막대 -->
                        <div class="bar-row d-flex align-items-center mb-3">
                          <div class="bar-label" style="width: 80px">탄수화물:</div>
                          <div class="progress flex-grow-1">
                            <div
                              class="progress-bar"
                              :style="{
                                width: `${Math.min(100, (totalNutrientValues.carbohydrate / (maxValues.carbohydrate * 1.2)) * 100)}%`,
                                backgroundColor: colors[2],
                              }"
                            ></div>
                          </div>
                          <div class="bar-value ml-2" style="width: 80px; text-align: right">
                            {{ totalNutrientValues.carbohydrate.toFixed(1) }} g
                          </div>
                        </div>

                        <!-- 지방 막대 -->
                        <div class="bar-row d-flex align-items-center mb-3">
                          <div class="bar-label" style="width: 80px">지방:</div>
                          <div class="progress flex-grow-1">
                            <div
                              class="progress-bar"
                              :style="{
                                width: `${Math.min(100, (totalNutrientValues.fat / (maxValues.fat * 1.2)) * 100)}%`,
                                backgroundColor: colors[3],
                              }"
                            ></div>
                          </div>
                          <div class="bar-value ml-2" style="width: 80px; text-align: right">
                            {{ totalNutrientValues.fat.toFixed(1) }} g
                          </div>
                        </div>

                        <!-- 당 막대 -->
                        <div class="bar-row d-flex align-items-center mb-3">
                          <div class="bar-label" style="width: 80px">당:</div>
                          <div class="progress flex-grow-1">
                            <div
                              class="progress-bar"
                              :style="{
                                width: `${Math.min(100, (totalNutrientValues.sugar / (maxValues.sugar * 1.2)) * 100)}%`,
                                backgroundColor: colors[4],
                              }"
                            ></div>
                          </div>
                          <div class="bar-value ml-2" style="width: 80px; text-align: right">
                            {{ totalNutrientValues.sugar.toFixed(1) }} g
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 영양소 합계 정보 -->
                  <div class="nutrition-summary mt-4 p-3">
                    <h6 class="mb-3 text-center">총 영양소 합계</h6>
                    <div class="row">
                      <div class="col-6 col-md-4 mb-2">
                        <div class="nutrient-total">
                          <div class="label">칼로리</div>
                          <div class="value text-primary">
                            {{ totalNutrientValues.calories.toFixed(1) }} kcal
                          </div>
                        </div>
                      </div>
                      <div class="col-6 col-md-4 mb-2">
                        <div class="nutrient-total">
                          <div class="label">단백질</div>
                          <div class="value text-primary">
                            {{ totalNutrientValues.protein.toFixed(1) }} g
                          </div>
                        </div>
                      </div>
                      <div class="col-6 col-md-4 mb-2">
                        <div class="nutrient-total">
                          <div class="label">탄수화물</div>
                          <div class="value text-primary">
                            {{ totalNutrientValues.carbohydrate.toFixed(1) }} g
                          </div>
                        </div>
                      </div>
                      <div class="col-6 col-md-4 mb-2">
                        <div class="nutrient-total">
                          <div class="label">지방</div>
                          <div class="value text-primary">
                            {{ totalNutrientValues.fat.toFixed(1) }} g
                          </div>
                        </div>
                      </div>
                      <div class="col-6 col-md-4 mb-2">
                        <div class="nutrient-total">
                          <div class="label">당</div>
                          <div class="value text-primary">
                            {{ totalNutrientValues.sugar.toFixed(1) }} g
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- 디버깅 정보 (개발 모드에서만 표시) -->
                    <div v-if="isDebugMode" class="mt-3 p-2 bg-light border border-warning rounded">
                      <p class="small mb-1 text-warning"><strong>디버깅 정보 (개발용):</strong></p>
                      <p class="small mb-0">
                        선택된 음식:
                        {{
                          Object.keys(selectedDishes).filter((key) => selectedDishes[key]).length
                        }}개
                      </p>
                      <p class="small mb-0">음식별 영양소 데이터 샘플:</p>
                      <ul class="small">
                        <li
                          v-for="(dish, index) in selectedDishesNutrients"
                          :key="index"
                          v-if="index < 1"
                        >
                          {{ dish.name }}: {{ dish.calories }}kcal, 단백질 {{ dish.protein }}g,
                          탄수화물 {{ dish.carbohydrate }}g, 지방 {{ dish.fat }}g, 당
                          {{ dish.sugar }}g
                        </li>
                      </ul>
                    </div>
                  </div>

                  <!-- 영양소 범례 -->
                  <div class="chart-legend mt-3">
                    <div class="d-flex justify-content-around flex-wrap">
                      <div class="legend-item">
                        <span class="legend-color" :style="{ backgroundColor: colors[0] }"></span>
                        <span>칼로리 (kcal)</span>
                      </div>
                      <div class="legend-item">
                        <span class="legend-color" :style="{ backgroundColor: colors[1] }"></span>
                        <span>단백질 (g)</span>
                      </div>
                      <div class="legend-item">
                        <span class="legend-color" :style="{ backgroundColor: colors[2] }"></span>
                        <span>탄수화물 (g)</span>
                      </div>
                      <div class="legend-item">
                        <span class="legend-color" :style="{ backgroundColor: colors[3] }"></span>
                        <span>지방 (g)</span>
                      </div>
                      <div class="legend-item">
                        <span class="legend-color" :style="{ backgroundColor: colors[4] }"></span>
                        <span>당 (g)</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showModal = false">닫기</button>
            <button v-if="!isEditMode" type="button" class="btn btn-primary" @click="handleSave">
              저장
            </button>
            <button v-else type="button" class="btn btn-primary" @click="handleUpdate">수정</button>
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

// 개발 환경에서는 true, 운영 환경에서는 false로 설정
const isDebugMode = ref(false) // 디버깅 모드 설정 (개발 중에만 true로 변경)
const showModal = ref(false)
const selectedCourse = ref('A')
const courses = ref([])
const selectedDishes = reactive({})
const selectedCourses = reactive({})
const selectAllMeals = ref(false)
const totalCalories = ref(750)
const _chartRenderKey = ref(0)
const userRecords = ref([])
const isEditMode = ref(false)
const isSavingManually = ref(false)
const autoSaveTimeout = ref(null)

const colors = [
  'rgba(255, 99, 132, 0.7)',
  'rgba(54, 162, 235, 0.7)',
  'rgba(255, 206, 86, 0.7)',
  'rgba(75, 192, 192, 0.7)',
  'rgba(153, 102, 255, 0.7)',
]

const getCurrentCourseName = computed(() => {
  const found = courses.value.find((c) => c.type === selectedCourse.value)
  return found ? found.course_name : ''
})

const currentCourseDishes = computed(() => {
  const found = courses.value.find((c) => c.type === selectedCourse.value)
  return found ? found.dishes : []
})

const selectedDishesNutrients = computed(() => {
  const result = []
  const processedDishIds = new Set() // 이미 처리한 음식 ID를 추적하기 위한 Set

  courses.value.forEach((course) => {
    if (!course.dishes) return

    course.dishes.forEach((dish) => {
      if (!dish || !dish.id) return

      const dishId = dish.id.toString()

      // 이미 처리된 음식 ID는 건너뜁니다
      if (processedDishIds.has(dishId)) {
        console.log(
          `음식 [${dish.name}](ID: ${dishId})은 이미 처리되었습니다. 중복 계산을 방지합니다.`,
        )
        return
      }

      if (selectedDishes[dishId]) {
        // 이 음식 ID를 처리된 것으로 표시
        processedDishIds.add(dishId)

        // 콘솔에 개별 음식 영양소 정보 출력
        console.log(`음식 [${dish.name}]의 영양소:`, {
          calorie: dish.calorie,
          protein: dish.protein,
          carbohydrate: dish.carbohydrate,
          fat: dish.fat,
          sugar: dish.sugar,
        })

        result.push({
          name: dish.name || '이름 없음',
          calories: Number(dish.calorie || 0),
          protein: Number(dish.protein || 0),
          carbohydrate: Number(dish.carbohydrate || 0),
          fat: Number(dish.fat || 0),
          sugar: Number(dish.sugar || 0),
        })
      }
    })
  })
  return result
})

const totalNutrientValues = computed(() => {
  const result = selectedDishesNutrients.value.reduce(
    (totals, dish) => {
      totals.calories += dish.calories
      totals.protein += dish.protein
      totals.carbohydrate += dish.carbohydrate
      totals.fat += dish.fat
      totals.sugar += dish.sugar
      return totals
    },
    { calories: 0, protein: 0, carbohydrate: 0, fat: 0, sugar: 0 },
  )

  // 합계 계산 결과를 로그에 출력
  console.log('영양소 합계 계산 결과:', result)

  return result
})

const maxValues = computed(() => {
  // 사용자 요청에 따라 고정 최대값 설정
  return {
    calories: 2000, // 일일 권장 칼로리
    protein: 100, // 일일 권장 단백질(g)
    carbohydrate: 300, // 일일 권장 탄수화물(g)
    fat: 50, // 일일 권장 지방(g)
    sugar: 50, // 일일 권장 당(g)
  }
})

const fetchTodayCourses = async () => {
  try {
    // 오늘의 코스 정보 가져오기
    const response = await axios.get('/api/courses/today')
    console.log('오늘의 코스 정보:', response.data)

    // 중복 음식 ID 처리를 위한 임시 저장소
    const seenDishIds = new Set()
    const duplicatedDishIds = new Set()

    // 모든 코스에서 중복된 음식 ID 찾기
    response.data.courses.forEach((course) => {
      if (course.dishes && course.dishes.length > 0) {
        course.dishes.forEach((dish) => {
          if (dish.id) {
            const dishId = dish.id.toString()
            if (seenDishIds.has(dishId)) {
              duplicatedDishIds.add(dishId)
              console.log(`중복된 음식 ID 발견: ${dishId} (${dish.name})`)
            } else {
              seenDishIds.add(dishId)
            }
          }
        })
      }
    })

    // 중복이 있으면 사용자에게 알림
    if (duplicatedDishIds.size > 0) {
      console.warn(
        `중복된 음식이 ${duplicatedDishIds.size}개 발견되었습니다. 영양소 계산 시 중복을 제거합니다.`,
      )
    }

    // 중복 음식 처리 완료 후 코스 정보 저장
    courses.value = response.data.courses

    // 디버깅용 - 첫 번째 코스의 첫 번째 음식 영양소 정보 확인
    if (
      courses.value &&
      courses.value.length > 0 &&
      courses.value[0].dishes &&
      courses.value[0].dishes.length > 0
    ) {
      const sampleDish = courses.value[0].dishes[0]
      console.log('음식 영양소 샘플:', {
        dish_name: sampleDish.name,
        calorie: sampleDish.calorie,
        protein: sampleDish.protein,
        carbohydrate: sampleDish.carbohydrate,
        fat: sampleDish.fat,
        sugar: sampleDish.sugar,
      })
    }

    // 모든 선택 상태를 false로 초기화 (완전히 삭제하지 않음)
    for (const key in selectedDishes) {
      selectedDishes[key] = false
    }

    // 사용자의 이전 기록 가져오기
    const recordsResponse = await axios.get('/api/meal-records/today')
    console.log('사용자의 이전 기록:', recordsResponse.data)
    userRecords.value = recordsResponse.data || []

    // 사용자의 이전 기록에 있는 음식들 체크 표시
    if (userRecords.value.length > 0) {
      userRecords.value.forEach((record) => {
        const dishId = record.dishId || record.dish_id
        if (dishId) {
          console.log('체크할 음식 ID:', dishId)
          selectedDishes[dishId.toString()] = true
        }
      })

      // 코스 선택 상태 업데이트
      courses.value.forEach((course) => {
        const allSelected =
          course.dishes &&
          course.dishes.every((dish) => dish && dish.id && selectedDishes[dish.id.toString()])
        selectedCourses[course.type] = allSelected
      })

      // 첫 번째 기록의 코스 타입을 기본으로 설정
      if (userRecords.value[0] && userRecords.value[0].courseType) {
        selectedCourse.value = userRecords.value[0].courseType
      }
    }

    // isEditMode 설정
    isEditMode.value = userRecords.value.length > 0

    // 차트 갱신
    _chartRenderKey.value++

    console.log('초기화 완료:', {
      isEditMode: isEditMode.value,
      selectedCourse: selectedCourse.value,
      selectedDishes: { ...selectedDishes },
      courses: courses.value.map((c) => ({
        type: c.type,
        name: c.course_name,
        dishes: c.dishes?.length || 0,
      })),
    })
  } catch (error) {
    console.error('오늘의 코스 조회 실패:', error)
    isEditMode.value = false
  }
}

const getDishImage = (dishName) =>
  `https://via.placeholder.com/120x80?text=${encodeURIComponent(dishName)}`

const calculateTotalCalories = (course) => {
  return course.dishes.reduce((total, dish) => {
    return total + Number(dish.calorie || 0)
  }, 0)
}

// 중복 기록 정리 함수 추가
const cleanupRecords = async () => {
  try {
    const response = await axios.post('/api/meal-records/cleanup')
    if (response.data.success) {
      console.log('기록 정리 완료:', response.data.message)
      return true
    }
    return false
  } catch (error) {
    console.error('기록 정리 중 오류:', error)
    return false
  }
}

// 신규 저장 핸들러
const handleSave = async () => {
  console.log('handleSave 호출됨')
  isSavingManually.value = true
  try {
    const selectedMeals = []
    const processedDishIds = new Set() // 중복 제거를 위한 Set

    courses.value.forEach((course) => {
      course.dishes.forEach((dish) => {
        const dishId = dish.id.toString()
        // 이미 처리된 음식은 건너뜁니다
        if (selectedDishes[dishId] && !processedDishIds.has(dishId)) {
          processedDishIds.add(dishId) // 처리된 음식으로 표시
          selectedMeals.push({
            schedule_id: course.schedule_id || getScheduleIdForCourse(course.type),
            dish_id: parseInt(dish.id),
          })
        }
      })
    })

    if (selectedMeals.length === 0) {
      alert('최소 하나 이상의 음식을 선택해주세요.')
      isSavingManually.value = false
      return
    }

    console.log('신규 저장 API 호출 (/api/meal-records)')
    console.log('저장할 음식 개수:', selectedMeals.length, '(중복 제거 후)')
    const response = await axios.post('/api/meal-records', {
      courseType: selectedCourse.value,
      meals: selectedMeals,
    })

    if (response.data.success) {
      alert(response.data.message || '식단이 성공적으로 저장되었습니다.')
      showModal.value = false
      isEditMode.value = true
      fetchTodayCourses() // 데이터 갱신

      // 이벤트 발생 - 영양 데이터 업데이트 알림 (setTimeout으로 지연 적용)
      setTimeout(() => {
        console.log('식단 데이터 업데이트 이벤트 발생')
        eventBus.emit('meal-data-updated')
      }, 500)
    } else {
      alert(response.data.message || '식단 저장에 실패했습니다.')
    }
  } catch (error) {
    console.error('식단 저장 실패:', error)
    alert(error.response?.data?.message || '식단 저장 중 오류가 발생했습니다.')
  } finally {
    isSavingManually.value = false
  }
}

// 수정 핸들러
const handleUpdate = async () => {
  console.log('handleUpdate 호출됨')
  isSavingManually.value = true
  try {
    const selectedMeals = []
    const processedDishIds = new Set() // 중복 제거를 위한 Set

    courses.value.forEach((course) => {
      course.dishes.forEach((dish) => {
        const dishId = dish.id.toString()
        // 이미 처리된 음식은 건너뜁니다
        if (selectedDishes[dishId] && !processedDishIds.has(dishId)) {
          processedDishIds.add(dishId) // 처리된 음식으로 표시
          selectedMeals.push({
            schedule_id: course.schedule_id || getScheduleIdForCourse(course.type),
            dish_id: parseInt(dish.id),
          })
        }
      })
    })

    console.log('수정 API 호출 (/api/meal-records/update)')
    console.log('저장할 음식 개수:', selectedMeals.length, '(중복 제거 후)')
    const response = await axios.post('/api/meal-records/update', {
      courseType: selectedCourse.value,
      meals: selectedMeals,
    })

    if (response.data.success) {
      alert(response.data.message || '식단이 성공적으로 수정되었습니다.')
      showModal.value = false
      fetchTodayCourses() // 데이터 갱신

      // 이벤트 발생 - 영양 데이터 업데이트 알림 (setTimeout으로 지연 적용)
      setTimeout(() => {
        console.log('식단 데이터 업데이트 이벤트 발생')
        eventBus.emit('meal-data-updated')
      }, 500)
    } else {
      alert(response.data.message || '식단 수정에 실패했습니다.')
    }
  } catch (error) {
    console.error('식단 수정 실패:', error)
    alert(error.response?.data?.message || '식단 수정 중 오류가 발생했습니다.')
  } finally {
    isSavingManually.value = false
  }
}

// 모달 타이틀 버튼 텍스트
const modalButtonText = computed(() => {
  return userRecords.value.length > 0 ? '식단 수정하기' : '식단 기록하기'
})

// 다른 코스에서 음식이 선택되었는지 확인하는 함수
const isOtherCourseSelected = (courseType) => {
  // 모든 코스의 음식 선택을 허용하도록 항상 false 반환
  return false
}

// 체크박스 토글 함수 - v-model 대신 직접 토글 처리
const toggleDishSelection = (dishId) => {
  const id = dishId.toString()
  selectedDishes[id] = !selectedDishes[id]

  // 실제로 체크된 음식들의 영양소 정보 콘솔에 출력
  const selectedDishes_list = []

  courses.value.forEach((course) => {
    if (!course.dishes) return

    course.dishes.forEach((dish) => {
      if (!dish || !dish.id) return

      if (selectedDishes[dish.id.toString()]) {
        selectedDishes_list.push({
          id: dish.id,
          name: dish.name,
          calorie: dish.calorie,
          protein: dish.protein,
          carbohydrate: dish.carbohydrate,
          fat: dish.fat,
          sugar: dish.sugar,
        })
      }
    })
  })

  console.log('현재 선택된 음식들:', selectedDishes_list)

  // 바 차트 즉시 갱신
  _chartRenderKey.value++
}

// 코스 변경 시 기존 선택을 초기화하고 코스 변경
const changeSelectedCourse = (newCourse) => {
  // 기존 선택을 완전히 초기화
  Object.keys(selectedDishes).forEach((key) => {
    selectedDishes[key] = false
  })

  selectedCourse.value = newCourse
  _chartRenderKey.value++ // 차트 갱신

  // 현재 선택된 코스의 기존 기록만 체크
  if (userRecords.value && userRecords.value.length > 0) {
    userRecords.value.forEach((record) => {
      if (record.courseType === newCourse) {
        const dishId = record.dishId || record.dish_id
        if (dishId) {
          selectedDishes[dishId.toString()] = true
        }
      }
    })
  }

  // 콘솔에 현재 상태 출력
  console.log(`코스 변경됨: ${newCourse}`)
  console.log('선택된 음식:', selectedDishes)
}

// 초기 코스 설정 (사용자의 기존 기록 기반)
const setInitialCourse = () => {
  if (userRecords.value && userRecords.value.length > 0) {
    // 기존 기록이 있으면 해당 코스 선택
    const firstRecord = userRecords.value[0]
    if (firstRecord.courseType) {
      selectedCourse.value = firstRecord.courseType
      console.log(`기존 기록 기반으로 ${selectedCourse.value} 코스 선택됨`)
    }
  } else {
    // 기본값은 A 코스
    selectedCourse.value = 'A'
  }
}

onMounted(() => {
  fetchTodayCourses().then(() => {
    // 초기 코스 설정
    setInitialCourse()
  })

  // DinnerRecommendation에서 보내는 이벤트 수신
  eventBus.on('open-today-diet-modal', () => {
    showModal.value = true
  })

  // DinnerRecommendation 컴포넌트에서 영양소 정보 요청 시 처리
  eventBus.on('request-today-diet-nutrients', () => {
    console.log('영양소 정보 요청 이벤트 수신됨')
    // 현재 계산된 영양소 정보 전송
    eventBus.emit('today-diet-nutrients', {
      calories: totalNutrientValues.value.calories || 0,
      protein: totalNutrientValues.value.protein || 0,
      carbohydrate: totalNutrientValues.value.carbohydrate || 0,
      fat: totalNutrientValues.value.fat || 0,
      sugar: totalNutrientValues.value.sugar || 0,
    })
    console.log('영양소 정보 전송됨:', totalNutrientValues.value)
  })

  // 정리 함수 반환
  return cleanup
})

// 컴포넌트 언마운트 시 이벤트 리스너 정리
const cleanup = () => {
  eventBus.off('open-today-diet-modal')
  eventBus.off('request-today-diet-nutrients')
}

// 선택된 코스가 변경되었을 때
watch(
  () => selectedCourse.value,
  (newValue) => {
    console.log(`코스 변경 감지: ${newValue}`)
    // 차트 갱신
    _chartRenderKey.value++
  },
)

// 선택된 음식들이 변경되었을 때 (깊은 감시 필요)
watch(
  selectedDishes,
  () => {
    // 코스 선택 상태 업데이트
    courses.value.forEach((course) => {
      if (course.dishes) {
        const all = course.dishes.every(
          (dish) => dish && dish.id && selectedDishes[dish.id.toString()],
        )
        const none = course.dishes.every(
          (dish) => dish && dish.id && !selectedDishes[dish.id.toString()],
        )
        selectedCourses[course.type] = all ? true : none ? false : selectedCourses[course.type]
      }
    })

    selectAllMeals.value = courses.value.every((c) => selectedCourses[c.type])

    // 차트 갱신
    _chartRenderKey.value++
  },
  { deep: true },
)

// 모달 열릴 때 데이터 새로 로드
watch(
  () => showModal.value,
  (val) => {
    if (val) {
      // 모달이 열릴 때마다 항상 최신 데이터 로드
      fetchTodayCourses()
    }
  },
)

// computed 속성 추가 - 기록이 있는지 확인
const hasRecords = computed(() => {
  // 비동기 데이터가 로드되기 전에는 false 반환
  if (!courses.value || courses.value.length === 0) return false

  // userRecords가 있으면 true
  return userRecords.value && userRecords.value.length > 0
})

// 수동으로 schedule_id 할당 (만약 course 객체에 schedule_id가 없다면)
const getScheduleIdForCourse = (courseType) => {
  const today = new Date()
  const formattedDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
  const baseId = (Number(formattedDate.replace(/-/g, '')) % 10) + 1 // 예시 로직
  // 실제로는 백엔드에서 올바른 schedule_id를 받아와야 합니다.
  // 이 로직은 프론트엔드에서 임시로 ID를 생성하는 것이므로,
  // 백엔드의 course_schedule 테이블의 ID 생성 방식과 일치하거나,
  // API 응답에 schedule_id가 포함되어야 합니다.
  console.warn(
    `Frontend generated schedule_id for courseType ${courseType}. This should ideally come from the backend.`,
  )
  return courseType === 'A' ? baseId : baseId + 1 // 예시: A코스는 홀수, B코스는 짝수
}
</script>

<style scoped>
.dish-item {
  width: 120px;
  text-align: center;
}

.dish-image {
  width: 100%;
  height: 80px;
  overflow: hidden;
  margin-bottom: 8px;
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-info {
  font-size: 0.9rem;
}

.dish-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.dish-calorie {
  color: #666;
  font-size: 0.8rem;
}

.modal {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal.show {
  display: block;
}

.nav-tabs .nav-link {
  color: #495057;
  cursor: pointer;
}

.nav-tabs .nav-link.active {
  color: #007bff;
  font-weight: 500;
}

.meal-item {
  transition: background-color 0.2s;
}

.meal-item:hover {
  background-color: #f8f9fa;
}

.form-check-label {
  cursor: pointer;
  padding-left: 4px;
}

.custom-chart-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  overflow-y: auto;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

.dish-bar-group {
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.dish-name {
  font-weight: 600;
  font-size: 0.9rem;
}

.progress {
  height: 10px;
  background-color: #f3f3f3;
  border-radius: 10px;
}

.bar-row {
  font-size: 0.85rem;
}

.bar-label {
  color: #666;
}

.bar-value {
  color: #333;
  font-weight: 500;
}

.no-data-message {
  color: #999;
  font-style: italic;
}

.chart-legend {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  margin: 0 8px 8px 0;
}

.legend-color {
  display: inline-block;
  width: 14px;
  height: 14px;
  margin-right: 5px;
  border-radius: 2px;
}

.nutrition-summary {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
}

.nutrient-total {
  text-align: center;
}

.nutrient-total .value {
  font-weight: 600;
  font-size: 1.1rem;
}

.selected-dish-item {
  background-color: #f9f9f9;
  border-radius: 4px;
}

.selected-dishes-list {
  max-height: 120px;
  overflow-y: auto;
  border: 1px solid #eee;
  border-radius: 4px;
}

.nutrient-total {
  text-align: center;
}

.nutrient-total .label {
  font-size: 0.85rem;
  color: #666;
}

.nutrient-total .value {
  font-weight: 600;
  font-size: 1.05rem;
}
</style>
