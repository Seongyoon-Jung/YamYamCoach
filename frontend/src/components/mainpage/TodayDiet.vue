<template>
  <div class="col-md-6 mb-4">
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>오늘의 식단</span>
        <button class="btn btn-sm btn-outline-primary" @click="showModal = true">
          상세/코스 선택
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
            <h5 class="modal-title">식사기록하기 - {{ getCurrentCourseName }}</h5>
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
                        @click="selectedCourse = 'A'"
                      >
                        A 코스
                      </button>
                    </li>
                    <li class="nav-item" role="presentation">
                      <button
                        class="nav-link"
                        :class="{ active: selectedCourse === 'B' }"
                        @click="selectedCourse = 'B'"
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
                      <div class="svg-chart" v-if="selectedDishesNutrients.length > 0">
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

                      <!-- 데이터가 없을 때 -->
                      <div
                        v-else
                        class="no-data-message d-flex justify-content-center align-items-center h-100"
                      >
                        <p class="text-muted">음식을 선택하면 영양소 정보가 표시됩니다</p>
                      </div>
                    </div>
                  </div>

                  <!-- 영양소 합계 정보 -->
                  <div v-if="selectedDishesNutrients.length > 0" class="nutrition-summary mt-4 p-3">
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
            <button type="button" class="btn btn-primary" @click="saveMealRecord">저장</button>
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
  const max = { calories: 0, protein: 0, carbohydrate: 0, fat: 0, sugar: 0 }
  selectedDishesNutrients.value.forEach((dish) => {
    max.calories = Math.max(max.calories, dish.calories)
    max.protein = Math.max(max.protein, dish.protein)
    max.carbohydrate = Math.max(max.carbohydrate, dish.carbohydrate)
    max.fat = Math.max(max.fat, dish.fat)
    max.sugar = Math.max(max.sugar, dish.sugar)
  })
  return {
    calories: max.calories || 100,
    protein: max.protein || 20,
    carbohydrate: max.carbohydrate || 20,
    fat: max.fat || 20,
    sugar: max.sugar || 20,
  }
})

const fetchTodayCourses = async () => {
  try {
    const response = await axios.get('/api/courses/today')
    courses.value = response.data.courses
    courses.value.forEach((course) => {
      selectedCourses[course.type] = false
      course.dishes.forEach((dish) => {
        selectedDishes[dish.id] = false
      })
    })
  } catch (error) {
    console.error('오늘의 코스 조회 실패:', error)
  }
}

const getDishImage = (dishName) =>
  `https://via.placeholder.com/120x80?text=${encodeURIComponent(dishName)}`

const calculateTotalCalories = (course) => course.dishes.reduce((t, d) => t + d.calorie, 0)

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

    // 백엔드 API 호출
    const response = await axios.post('/api/meal-records', {
      courseType: selectedCourse.value,
      meals: selectedMeals,
    })

    if (response.data.success) {
      alert('식단이 저장되었습니다.')
      showModal.value = false
      // 선택 초기화
      Object.keys(selectedDishes).forEach((key) => {
        selectedDishes[key] = false
      })
      // 데이터 새로고침
      await fetchTodayCourses()
    }
  } catch (error) {
    console.error('식단 저장 실패:', error)
    console.error('오류 상세 정보:', error.response?.data)
    alert(error.response?.data?.message || '식단 저장 중 오류가 발생했습니다.')
  }
}

onMounted(fetchTodayCourses)

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

watch(
  () => selectedCourse.value,
  () => {
    courses.value.forEach((course) => {
      course.dishes.forEach((dish) => (selectedDishes[dish.id] = false))
      selectedCourses[course.type] = false
    })
    selectAllMeals.value = false
    _chartRenderKey.value++
  },
)

watch(
  () => showModal.value,
  (val) => {
    if (val) _chartRenderKey.value++
  },
)
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
