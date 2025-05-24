<template>
  <div class="card calendar-modern-outer calendar-rect">
    <div class="calendar-modern-container calendar-rect-container">
      <div class="calendar-modern-header">
        <button class="calendar-nav-btn" @click="prevMonth">&#60;</button>
        <span class="calendar-month-label">{{ displayMonth }}</span>
        <button class="calendar-nav-btn" @click="nextMonth">&#62;</button>
      </div>
      <div class="calendar-modern-grid">
        <div class="calendar-modern-day" v-for="day in weekDays" :key="day">{{ day }}</div>
        <div
          v-for="day in allDays"
          :key="day.date + (day.isPrevMonth ? 'prev' : day.isNextMonth ? 'next' : 'current')"
          class="calendar-modern-cell"
          :class="{
            today: isToday(day.fullDate, day.isCurrentMonth),
            selected: day.isLastClicked && lastClickedDate.visible,
            'other-month': day.isPrevMonth || day.isNextMonth
          }"
          @click="selectDate(day)"
        >
          {{ day.date }}
        </div>
      </div>
      <div class="calendar-modern-schedule-list">
        <div v-if="loading" class="text-center">
          <span class="loading-text">불러오는 중...</span>
        </div>
        <div v-else-if="error" class="text-center text-danger">
          {{ error }}
        </div>
        <div v-else>
          <div v-if="!hasAnyMeals" class="text-center text-muted meal-section">
            기록된 식사가 없습니다.
          </div>
          <template v-else>
            <!-- 아침 섹션 -->
            <div v-if="extraMeals.BREAKFAST.length > 0" class="meal-section">
              <div class="meal-section-title">[아침]</div>
              <div class="meal-list">
                {{ extraMeals.BREAKFAST.map(meal => meal.dishName).join(' · ') }}
              </div>
            </div>

            <!-- 점심 섹션 (기본 식단) -->
            <div v-if="meals.length > 0 || extraMeals.LUNCH.length > 0" class="meal-section">
              <div class="meal-section-title">[점심]</div>
              <div class="meal-list">
                <template v-if="meals.length > 0">
                  {{ meals.map(meal => meal.dishName).join(' · ') }}
                </template>
                <template v-if="extraMeals.LUNCH.length > 0">
                  <template v-if="meals.length > 0"> · </template>
                  {{ extraMeals.LUNCH.map(meal => meal.dishName).join(' · ') }}
                </template>
              </div>
            </div>

            <!-- 저녁 섹션 -->
            <div v-if="extraMeals.DINNER.length > 0" class="meal-section">
              <div class="meal-section-title">[저녁]</div>
              <div class="meal-list">
                {{ extraMeals.DINNER.map(meal => meal.dishName).join(' · ') }}
              </div>
            </div>

            <!-- 간식 섹션 -->
            <div v-if="extraMeals.SNACK.length > 0" class="meal-section">
              <div class="meal-section-title">[간식]</div>
              <div class="meal-list">
                {{ extraMeals.SNACK.map(meal => meal.dishName).join(' · ') }}
              </div>
            </div>
          </template>
        </div>
      </div>
      <div class="add-record-button-container">
        <button class="add-record-button" @click="openAddModal">
          추가 기록 +
        </button>
      </div>
    </div>
    <!-- 식사 기록 모달 -->
    <MealRecordModal
      v-if="showModal"
      :selectedDate="selectedDateFormatted"
      :apiDate="apiDateFormatted"
      :isWeekend="isWeekend"
      @close="closeModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import axios from '@/plugins/axios'
import eventBus from '@/utils/eventBus'
import MealRecordModal from './MealRecordModal.vue'

const today = new Date()
const year = ref(today.getFullYear())
const month = ref(today.getMonth())
const selectedDate = ref(today.getDate())
const isInitialLoad = ref(true)

const weekDays = ['월', '화', '수', '목', '금', '토', '일']

const monthNames = [
  '1월', '2월', '3월', '4월', '5월', '6월',
  '7월', '8월', '9월', '10월', '11월', '12월'
]

const lastClickedDate = ref({
  year: today.getFullYear(),
  month: today.getMonth(),
  date: today.getDate(),
  visible: true
})

// 날짜 계산 헬퍼 함수
const getWeekStartDate = (date) => {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  return new Date(d.setDate(diff))
}

// 날짜 배열 생성
const allDays = computed(() => {
  // 오늘이 속한 주의 월요일 찾기
  const baseDate = new Date(year.value, month.value, selectedDate.value)
  const currentWeekMonday = getWeekStartDate(baseDate)
  
  // 2주 전 월요일로 이동
  const startDate = new Date(currentWeekMonday)
  startDate.setDate(startDate.getDate() - 14)
  
  const days = []
  const currentDate = new Date(startDate)

  // 21일(3주) 동안의 날짜 생성
  for (let i = 0; i < 21; i++) {
    const isLastClicked = currentDate.getDate() === lastClickedDate.value.date &&
                         currentDate.getMonth() === lastClickedDate.value.month &&
                         currentDate.getFullYear() === lastClickedDate.value.year
    
    days.push({
      date: currentDate.getDate(),
      isCurrentMonth: currentDate.getMonth() === month.value,
      isPrevMonth: currentDate.getMonth() < month.value || 
                  (currentDate.getMonth() === 11 && month.value === 0),
      isNextMonth: currentDate.getMonth() > month.value || 
                  (currentDate.getMonth() === 0 && month.value === 11),
      fullDate: new Date(currentDate),
      isLastClicked: isLastClicked
    })
    currentDate.setDate(currentDate.getDate() + 1)
  }

  // 마지막으로 클릭한 날짜가 현재 보이는 범위 안에 있는지 확인
  const hasLastClickedDate = days.some(day => day.isLastClicked)
  lastClickedDate.value.visible = hasLastClickedDate

  return days
})

// 현재 표시되는 마지막 주의 달 계산
const displayMonth = computed(() => {
  const days = allDays.value
  if (days.length > 0) {
    const lastWeek = days.slice(-7)[0] // 마지막 주의 첫 날
    return monthNames[lastWeek.fullDate.getMonth()]
  }
  return monthNames[month.value]
})

const isToday = (date, isCurrentMonth) => {
  return (
    isCurrentMonth &&
    date.getFullYear() === today.getFullYear() &&
    date.getMonth() === today.getMonth() &&
    date.getDate() === today.getDate()
  )
}

const selectDate = (day) => {
  // 마지막으로 클릭한 날짜 업데이트
  lastClickedDate.value = {
    year: day.fullDate.getFullYear(),
    month: day.fullDate.getMonth(),
    date: day.fullDate.getDate(),
    visible: true
  }
  
  // 현재 표시되는 날짜 업데이트
  year.value = day.fullDate.getFullYear()
  month.value = day.fullDate.getMonth()
  selectedDate.value = day.date

  // 식사 기록 조회
  fetchMeals(day.date)
}

const prevMonth = () => {
  if (month.value === 0) {
    year.value--
    month.value = 11
  } else {
    month.value--
  }
  selectedDate.value = 1
}

const nextMonth = () => {
  if (month.value === 11) {
    year.value++
    month.value = 0
  } else {
    month.value++
  }
  selectedDate.value = 1
}

// 예시 일정 데이터 (실제 데이터 연동 필요)
const schedule = [
  { date: '2020-10-08', time: '8 AM', title: 'Breakfast' },
  { date: '2020-10-08', time: '10 AM', title: 'Travel to venue' },
  { date: '2020-10-08', time: '10:30 AM', title: 'Ceremony' },
]

const filteredSchedule = computed(() => {
  const m = String(month.value + 1).padStart(2, '0')
  const d = String(selectedDate.value).padStart(2, '0')
  const y = year.value
  const dateStr = `${y}-${m}-${d}`
  return schedule.filter((item) => item.date === dateStr)
})

const loading = ref(false)
const error = ref(null)
const meals = ref([])
const extraMeals = ref({
  BREAKFAST: [],
  LUNCH: [],
  DINNER: [],
  SNACK: []
})

// 오늘 날짜로 캘린더를 초기화하고 식사 기록을 다시 불러오는 함수
const resetToTodayAndFetchMeals = () => {
  const newToday = new Date()
  year.value = newToday.getFullYear()
  month.value = newToday.getMonth()
  selectedDate.value = newToday.getDate()
  // fetchMeals는 selectedDate, month, year의 watch에 의해 자동으로 호출됩니다.
  // 만약 watch가 selectedDate만 감지한다면 month, year 변경 후 fetchMeals를 명시적으로 호출해야 할 수 있습니다.
  // 현재 watch는 [selectedDate, month, year]를 모두 감지하므로 추가 호출은 필요 없어 보입니다.
  // 다만, watch 콜백이 즉시 실행되지 않을 수 있으므로, 안전하게 직접 호출하는 것도 고려할 수 있습니다.
  fetchMeals(selectedDate.value)
}

// 날짜별 식사 기록 조회
const fetchMeals = async (selectedDate) => {
  if (!selectedDate) return

  loading.value = true
  error.value = null

  try {
    const formattedDate = `${year.value}-${String(month.value + 1).padStart(2, '0')}-${String(selectedDate).padStart(2, '0')}`
    
    // 기본 식단 조회 (점심)
    const mealResponse = await axios.get(`/api/meal-records/date/${formattedDate}`)
    meals.value = mealResponse.data

    // 추가 식사 기록 조회 (아침, 저녁, 간식)
    const extraResponse = await axios.get(`/api/extra-meal-records/by-date/${formattedDate}`)
    
    // 초기화
    extraMeals.value = {
      BREAKFAST: [],
      LUNCH: [],
      DINNER: [],
      SNACK: []
    }
    
    // 추가 식사 기록을 종류별로 분류
    extraResponse.data.forEach(meal => {
      if (extraMeals.value[meal.courseType]) {
        extraMeals.value[meal.courseType].push(meal)
      }
    })
  } catch (err) {
    console.error('식사 기록 조회 실패:', err)
    error.value = '식사 기록을 불러오는데 실패했습니다.'
    meals.value = []
    extraMeals.value = {
      BREAKFAST: [],
      LUNCH: [],
      DINNER: [],
      SNACK: []
    }
  } finally {
    loading.value = false
  }
}

// 시간 포맷 함수
const formatTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
}

// 식사 종류 포맷 함수
const formatCourseType = (type) => {
  const types = {
    breakfast: '아침',
    lunch: '점심',
    dinner: '저녁',
    snack: '간식',
  }
  return types[type] || type
}

// 날짜가 변경될 때마다 식사 기록 조회
watch([selectedDate, month, year], () => {
  if (selectedDate.value) {
    fetchMeals(selectedDate.value)
  }
})

// 스크롤 상태 관리
const isScrolling = ref(false)
const scrollTimeout = ref(null)

// 스크롤 이벤트 핸들러
const handleScroll = (event) => {
  if (isScrolling.value) return
  
  event.preventDefault()
  isScrolling.value = true

  const direction = event.deltaY > 0 ? 1 : -1
  const calendarGrid = document.querySelector('.calendar-modern-grid')
  
  if (calendarGrid) {
    calendarGrid.style.transition = 'transform 0.3s ease-out'
    calendarGrid.style.transform = `translateY(${direction * -20}px)`

    // 실제 날짜 변경 및 데이터 업데이트
    setTimeout(() => {
      calendarGrid.style.transition = 'transform 0s'
      calendarGrid.style.transform = 'translateY(0)'
      
      // 7일씩 이동
      const newDate = new Date(year.value, month.value, selectedDate.value + (direction * 7))
      year.value = newDate.getFullYear()
      month.value = newDate.getMonth()
      selectedDate.value = newDate.getDate()

      // 스크롤 잠금 해제
      if (scrollTimeout.value) clearTimeout(scrollTimeout.value)
      scrollTimeout.value = setTimeout(() => {
        isScrolling.value = false
      }, 300)
    }, 300)
  }
}

// 컴포넌트 마운트/언마운트 시 스크롤 이벤트 리스너 등록/해제
onMounted(() => {
  const calendarGrid = document.querySelector('.calendar-modern-grid')
  if (calendarGrid) {
    calendarGrid.addEventListener('wheel', handleScroll)
  }
  eventBus.on('meal-data-updated', resetToTodayAndFetchMeals)
  fetchMeals(selectedDate.value)
})

onUnmounted(() => {
  const calendarGrid = document.querySelector('.calendar-modern-grid')
  if (calendarGrid) {
    calendarGrid.removeEventListener('wheel', handleScroll)
  }
  eventBus.off('meal-data-updated', resetToTodayAndFetchMeals)
})

const showModal = ref(false)
const formatDateToKorean = (year, month, date) => {
  return `${month + 1}월 ${date}일`
}

const selectedDateFormatted = computed(() => {
  return formatDateToKorean(lastClickedDate.value.year, lastClickedDate.value.month, lastClickedDate.value.date)
})

const apiDateFormatted = computed(() => {
  return `${lastClickedDate.value.year}-${String(lastClickedDate.value.month + 1).padStart(2, '0')}-${String(lastClickedDate.value.date).padStart(2, '0')}`
})

const openAddModal = () => {
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const isWeekend = computed(() => {
  const date = new Date(lastClickedDate.value.year, lastClickedDate.value.month, lastClickedDate.value.date)
  const day = date.getDay()
  return day === 0 || day === 6 // 0은 일요일, 6은 토요일
})

// 식사 기록 여부 확인
const hasAnyMeals = computed(() => {
  return meals.value.length > 0 || 
         extraMeals.value.BREAKFAST.length > 0 || 
         extraMeals.value.LUNCH.length > 0 || 
         extraMeals.value.DINNER.length > 0 || 
         extraMeals.value.SNACK.length > 0
})
</script>

<style scoped>
.calendar-modern-outer.calendar-rect {
  width: 100%;
  min-width: 320px;
  max-width: 420px;
  height: 100%;
  margin-top: 0 !important;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
}

.calendar-modern-container.calendar-rect-container {
  position: relative;
  width: 100%;
  min-width: 320px;
  max-width: 420px;
  height: 100%;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.09);
  padding: 28px 20px;
  font-family: 'Inter', sans-serif;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.calendar-modern-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  font-weight: 600;
  font-size: 1.25rem;
  margin-bottom: 12px;
  gap: 10px;
}

.calendar-month-label {
  flex: 1;
  text-align: left;
  font-size: 1.15rem;
  font-weight: 700;
}

.calendar-nav-btn {
  background: none;
  border: none;
  font-size: 1.3rem;
  cursor: pointer;
  color: #333;
  padding: 0 8px;
}

.calendar-modern-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  margin-bottom: 24px;
  cursor: ns-resize;
  will-change: transform;
  position: relative;
  z-index: 0;
}

.calendar-modern-day {
  text-align: center;
  color: #888;
  font-size: 1.05rem;
  font-weight: 500;
  padding-bottom: 4px;
  margin-bottom: 2px;
}

.calendar-modern-cell {
  position: relative;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  cursor: pointer;
  font-size: 1.08rem;
  transition: all 0.2s ease;
  color: #222;
  user-select: none;
  -webkit-user-select: none;
  background: transparent;
  z-index: 2;
}

.calendar-modern-cell::before {
  content: '';
  display: block;
  padding-top: 100%;
}

.calendar-modern-cell.selected {
  position: relative;
  color: #ff5a5f;
  font-weight: 600;
}

.calendar-modern-cell.selected::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 42px;
  height: 42px;
  border: 2px solid #ff5a5f;
  border-radius: 50%;
  pointer-events: none;
  z-index: 1;
  background: rgba(255, 90, 95, 0.1);
  transition: opacity 0.3s ease;
}

.calendar-modern-cell.other-month.selected::after {
  border-color: #ff8a8f;
  background: rgba(255, 90, 95, 0.05);
}

.calendar-modern-cell.today {
  color: #ff5a5f;
  font-weight: 700;
  font-size: 1.15rem;
}

.calendar-modern-cell.other-month {
  color: #ccc;
}

.calendar-modern-cell.blank {
  background: none;
  cursor: default;
}

/* 스크롤 중일 때의 스타일 */
.calendar-modern-grid.scrolling {
  pointer-events: none;
}

.calendar-modern-grid.scrolling .calendar-modern-cell.selected::after {
  transition: none;
}

.calendar-modern-schedule-list {
  margin-top: 8px;
}

.meal-section {
  padding: 12px;
  border-top: 1px solid #eee;
}

.meal-section:first-child {
  border-top: none;
}

.meal-section-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  font-size: 0.95rem;
}

.meal-list {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
  white-space: normal;
  word-break: keep-all;
}

.text-center {
  text-align: center;
}

.text-muted {
  color: #666;
  font-size: 0.9rem;
}

.text-danger {
  color: #dc3545;
  font-size: 0.9rem;
}

.loading-text {
  font-size: 0.9rem;
  color: #666;
}

.add-record-button-container {
  position: absolute;
  right: 20px;
  bottom: 20px;
}

.add-record-button {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  background-color: transparent;
  color: #4a90e2;
  border: 1px solid #4a90e2;
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.add-record-button:hover {
  background-color: #f0f7ff;
  transform: scale(1.05);
}

.calendar-modern-container {
  position: relative;
}
</style>
