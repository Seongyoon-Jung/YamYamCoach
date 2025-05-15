<template>
  <div class="col-md-6 mb-4">
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>오늘의 식단</span>
        <button class="btn btn-sm btn-outline-primary" @click="showModal = true">
          {{ modalButtonText }}
        </button>
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
            <h5 class="modal-title">
              {{ isEditMode ? '식사기록 수정하기' : '식사기록하기' }} - {{ getCurrentCourseName }}
            </h5>
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
                      class="tab-pane"
                      :class="{ active: selectedCourse === course.type }"
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
                                v-model="selectedDishes[dish.id]"
                                @change="_chartRenderKey++"
                                :disabled="isOtherCourseSelected(course.type)"
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
            <button type="button" class="btn btn-secondary" @click="showModal = false">취소</button>
            <button type="button" class="btn btn-primary" @click="saveMealRecord">
              {{ isEditMode ? '수정하기' : '저장하기' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import axios from '@/plugins/axios'

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
  courses.value.forEach((course) => {
    course.dishes.forEach((dish) => {
      if (selectedDishes[dish.id]) {
        result.push({
          name: dish.name,
          calories: Number(dish.calorie || 0),
          protein: Number(dish.protein || 0),
          carbohydrate: Number(dish.carbohydrate || 0),
          fat: Number(dish.fat || 0),
          sugar: Number(dish.sugarG || 0),
        })
      }
    })
  })
  return result
})

const totalNutrientValues = computed(() => {
  return selectedDishesNutrients.value.reduce(
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
})

const maxValues = computed(() => {
  // 사용자 요청에 따라 고정 최대값 설정
  // 칼로리는 2000, 나머지는 300으로 설정
  return {
    calories: 2000,
    protein: 300,
    carbohydrate: 300,
    fat: 300,
    sugar: 300,
  }
})

const fetchTodayCourses = async () => {
  try {
    // 오늘의 코스 정보 가져오기
    const response = await axios.get('/api/courses/today')
    courses.value = response.data.courses

    // 사용자의 이전 기록 가져오기
    try {
      const recordsResponse = await axios.get('/api/meal-records/today')
      userRecords.value = recordsResponse.data || []
      console.log('사용자의 이전 기록:', userRecords.value)

      // 이전 기록이 있으면 수정 모드로 설정
      isEditMode.value = userRecords.value.length > 0
    } catch (error) {
      console.error('사용자 기록 조회 실패:', error)
      userRecords.value = []
      isEditMode.value = false
    }

    // 모든 선택 상태 초기화
    courses.value.forEach((course) => {
      selectedCourses[course.type] = false
      course.dishes.forEach((dish) => {
        selectedDishes[dish.id] = false
      })
    })

    // 사용자의 이전 기록에 있는 음식들 체크 표시
    if (userRecords.value.length > 0) {
      userRecords.value.forEach((record) => {
        // console.log를 추가하여 실제 데이터 확인
        console.log('검사할 기록:', record)
        // 백엔드에서 dishId로 반환될 수 있으므로 둘 다 확인
        const dishId = record.dishId || record.dish_id
        console.log('사용할 dish ID:', dishId)
        if (dishId) {
          // 문자열로 변환하여 비교 (키가 문자열인 경우가 있을 수 있음)
          selectedDishes[dishId.toString()] = true
        }
      })

      // 코스 선택 상태 업데이트
      courses.value.forEach((course) => {
        const allSelected = course.dishes.every((dish) => selectedDishes[dish.id])
        selectedCourses[course.type] = allSelected
      })
    }
  } catch (error) {
    console.error('오늘의 코스 조회 실패:', error)
  }
}

const getDishImage = (dishName) =>
  `https://via.placeholder.com/120x80?text=${encodeURIComponent(dishName)}`

const calculateTotalCalories = (course) => course.dishes.reduce((t, d) => t + d.calorie, 0)

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

// saveMealRecord 함수 수정
const saveMealRecord = async () => {
  try {
    // 선택된 음식들만 필터링
    const selectedMeals = []

    console.log('selectedDishes 상태:', selectedDishes)
    let checkedCount = 0
    Object.keys(selectedDishes).forEach((key) => {
      if (selectedDishes[key]) checkedCount++
    })
    console.log('선택된 체크박스 수:', checkedCount)

    // 현재 날짜 구하기 (서버와 일치시키기 위함)
    const today = new Date()
    const formattedDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    console.log('현재 날짜:', formattedDate)

    // 수동으로 schedule_id 할당
    const getScheduleIdForCourse = (courseType) => {
      // 오늘 날짜의 A코스는 항상 홀수, B코스는 항상 짝수로 설정
      // 실제 데이터베이스 값과 일치하도록 적절히 조정 필요
      const baseId = (Number(formattedDate.replace(/-/g, '')) % 10) + 1
      return courseType === 'A' ? baseId : baseId + 1
    }

    // 코스별로 처리
    courses.value.forEach((course) => {
      // 현재 코스에 schedule_id가 없으면 자동 할당
      const scheduleId = course.schedule_id || getScheduleIdForCourse(course.type)
      console.log(`코스 ${course.type}에 schedule_id ${scheduleId} 할당됨`)

      course.dishes.forEach((dish) => {
        if (selectedDishes[dish.id]) {
          const dishId = parseInt(dish.id)

          console.log('선택된 음식:', {
            name: dish.name,
            dish_id: dishId,
            course_type: course.type,
            schedule_id: scheduleId,
          })

          if (!isNaN(dishId)) {
            selectedMeals.push({
              schedule_id: scheduleId,
              dish_id: dishId,
            })
          }
        }
      })
    })

    console.log('최종 선택된 음식:', selectedMeals)

    if (selectedMeals.length === 0) {
      alert('최소 하나 이상의 음식을 선택해주세요.')
      return
    }

    // 식단 저장 전에 먼저 정리 API 호출 (옵션)
    // await cleanupRecords()

    // 백엔드 API 호출 - 수정 모드일 때는 update API를 호출
    try {
      let response
      if (isEditMode.value) {
        // 수정 모드일 때 update API 사용
        console.log('수정 모드로 API 호출')
        response = await axios.post('/api/meal-records/update', {
          courseType: selectedCourse.value,
          meals: selectedMeals,
        })
      } else {
        // 신규 저장 모드일 때
        console.log('신규 저장 모드로 API 호출')
        response = await axios.post('/api/meal-records', {
          courseType: selectedCourse.value,
          meals: selectedMeals,
        })
      }

      if (response.data.success) {
        alert(
          response.data.message ||
            (isEditMode.value
              ? '식단이 성공적으로 수정되었습니다.'
              : '식단이 성공적으로 저장되었습니다.'),
        )
        showModal.value = false

        // 수정 후 모달이 다시 열릴 때 최신 데이터 표시를 위해 상태 업데이트
        isEditMode.value = true

        // 데이터 갱신
        fetchTodayCourses()
      }
    } catch (saveError) {
      // 오류 처리
      console.error('식단 저장/수정 실패:', saveError)
      console.error('오류 상세 정보:', saveError.response?.data)
      alert(saveError.response?.data?.message || '식단 저장/수정 중 오류가 발생했습니다.')
    }
  } catch (error) {
    console.error('식단 저장/수정 실패:', error)
    console.error('오류 상세 정보:', error.response?.data)
    alert(error.response?.data?.message || '식단 저장/수정 중 오류가 발생했습니다.')
  }
}

// 모달 타이틀 버튼 텍스트
const modalButtonText = computed(() => {
  return hasRecords.value ? '상세보기/수정' : '상세/코스 선택'
})

// 다른 코스에서 음식이 선택되었는지 확인하는 함수
const isOtherCourseSelected = (courseType) => {
  // 현재 보고 있는 코스가 아닌 다른 코스의 체크박스가 선택되어 있는지 확인
  if (courseType !== selectedCourse.value) {
    return false // 현재 보고 있는 코스에서는 선택 가능
  }

  // 다른 코스의 음식 중 하나라도 선택되어 있는지 확인
  let otherCourseSelected = false
  courses.value.forEach((course) => {
    if (course.type !== courseType) {
      course.dishes.forEach((dish) => {
        if (selectedDishes[dish.id]) {
          otherCourseSelected = true
        }
      })
    }
  })

  return otherCourseSelected
}

// 코스 변경 시 다른 코스의 선택 항목 초기화
const changeSelectedCourse = (newCourse) => {
  // 현재 코스의 음식 중 하나라도 선택되어 있는지 확인
  let hasCurrentCourseSelection = false

  courses.value.forEach((course) => {
    if (course.type === selectedCourse.value) {
      course.dishes.forEach((dish) => {
        if (selectedDishes[dish.id]) {
          hasCurrentCourseSelection = true
        }
      })
    }
  })

  // 현재 코스에 선택된 항목이 있고, 다른 코스로 변경하려고 할 때 확인
  if (hasCurrentCourseSelection && selectedCourse.value !== newCourse) {
    if (
      confirm(
        `코스를 ${selectedCourse.value} → ${newCourse} 변경하면 선택한 음식이 초기화됩니다. 계속하시겠습니까?`,
      )
    ) {
      // 모든 선택 초기화
      Object.keys(selectedDishes).forEach((key) => {
        selectedDishes[key] = false
      })
      selectedCourse.value = newCourse
    }
  } else {
    selectedCourse.value = newCourse
  }
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
})

watch(
  () => showModal.value,
  (val) => {
    if (val) {
      // 모달이 열릴 때마다 항상 최신 데이터 로드
      fetchTodayCourses().then(() => {
        console.log('모달 오픈 시 데이터 로드 완료')
        // 초기 코스 설정
        setInitialCourse()
        // 차트 갱신
        _chartRenderKey.value++
      })
    }
  },
)

// 선택된 코스가 변경되었을 때
watch(
  () => selectedCourse.value,
  (newValue, oldValue) => {
    // 차트만 갱신
    _chartRenderKey.value++
  },
)

// 선택된 음식들이 변경되었을 때
watch(
  selectedDishes,
  () => {
    courses.value.forEach((course) => {
      const all = course.dishes.every((dish) => selectedDishes[dish.id])
      const none = course.dishes.every((dish) => !selectedDishes[dish.id])
      selectedCourses[course.type] = all ? true : none ? false : selectedCourses[course.type]
    })
    selectAllMeals.value = courses.value.every((c) => selectedCourses[c.type])
    _chartRenderKey.value++
  },
  { deep: true },
)

// 전체 선택 체크박스가 변경되었을 때
watch(
  () => selectAllMeals.value,
  (newValue) => {
    courses.value.forEach((course) => {
      selectedCourses[course.type] = newValue
      course.dishes.forEach((dish) => {
        selectedDishes[dish.id] = newValue
      })
    })
  },
)

// computed 속성 추가 - 기록이 있는지 확인
const hasRecords = computed(() => {
  // 비동기 데이터가 로드되기 전에는 false 반환
  if (!courses.value || courses.value.length === 0) return false

  // userRecords가 있으면 true
  return userRecords.value && userRecords.value.length > 0
})
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
