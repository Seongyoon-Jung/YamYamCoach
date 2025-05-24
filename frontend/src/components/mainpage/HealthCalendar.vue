<template>
  <div class="health-calendar">
    <div class="card shadow-sm h-100">
      <!-- 헤더 -->
      <div class="card-header bg-white d-flex justify-content-between align-items-center">
        <h6 class="mb-0">월별 건강 활동</h6>
        <div class="d-flex align-items-center">
          <button class="btn btn-sm btn-link text-muted" @click="prevMonth">
            <i class="bi bi-chevron-left"></i>
          </button>
          <span class="mx-2">{{ currentYear }}년 {{ currentMonth + 1 }}월</span>
          <button class="btn btn-sm btn-link text-muted" @click="nextMonth">
            <i class="bi bi-chevron-right"></i>
          </button>
        </div>
      </div>

      <!-- 본문 -->
      <div class="card-body p-3">
        <!-- 요일 헤더 -->
        <div class="calendar-grid mb-2">
          <div v-for="day in weekDays" :key="day" class="text-center fw-bold">
            {{ day }}
          </div>
        </div>
        
        <!-- 날짜 그리드 -->
        <div class="calendar-grid">
          <div 
            v-for="(dateInfo, index) in calendarDays" 
            :key="index"
            class="calendar-day text-center"
            :class="{ 'different-month': dateInfo.differentMonth, 'today': dateInfo.isToday }"
          >
            <div class="position-relative d-flex flex-column align-items-center">
              <div class="date-number mb-1" :class="{ 'today-number': dateInfo.isToday }">
                {{ dateInfo.day }}
              </div>
              
              <!-- 활동 링 (SVG) -->
              <div class="activity-rings" v-if="!dateInfo.differentMonth && dateInfo.hasData">
                <svg width="36" height="36" viewBox="0 0 36 36">
                  <!-- 운동 링 (빨간색) -->
                  <circle cx="18" cy="18" r="15" fill="none" stroke="#e6e6e6" stroke-width="3" />
                  <circle 
                    cx="18" cy="18" r="15" fill="none" 
                    stroke="#ff5b5b" stroke-width="3" 
                    stroke-dasharray="94.2" 
                    :stroke-dashoffset="94.2 - (dateInfo.exercisePercentage * 0.942)" 
                    transform="rotate(-90, 18, 18)"
                  />
                  
                  <!-- 물 섭취 링 (파란색) -->
                  <circle cx="18" cy="18" r="11" fill="none" stroke="#e6e6e6" stroke-width="3" />
                  <circle 
                    cx="18" cy="18" r="11" fill="none" 
                    stroke="#4dabf7" stroke-width="3" 
                    stroke-dasharray="69.1" 
                    :stroke-dashoffset="69.1 - (dateInfo.waterPercentage * 0.691)" 
                    transform="rotate(-90, 18, 18)"
                  />
                  
                  <!-- 수면 링 (녹색) -->
                  <circle cx="18" cy="18" r="7" fill="none" stroke="#e6e6e6" stroke-width="3" />
                  <circle 
                    cx="18" cy="18" r="7" fill="none" 
                    stroke="#69db7c" stroke-width="3" 
                    stroke-dasharray="44" 
                    :stroke-dashoffset="44 - (dateInfo.sleepPercentage * 0.44)" 
                    transform="rotate(-90, 18, 18)"
                  />

                  <!-- 활동 목표 달성 표시 (작은 점) -->
                  <circle 
                    v-if="dateInfo.exercisePercentage >= 100" 
                    cx="18" cy="3" r="1.5" 
                    fill="#ff5b5b"
                  />
                  <circle 
                    v-if="dateInfo.waterPercentage >= 100" 
                    cx="29" cy="18" r="1.5" 
                    fill="#4dabf7"
                  />
                  <circle 
                    v-if="dateInfo.sleepPercentage >= 100" 
                    cx="18" cy="33" r="1.5" 
                    fill="#69db7c"
                  />
                </svg>
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
import axios from '@/plugins/axios'

// 기본 상태
const currentDate = ref(new Date())
const currentYear = computed(() => currentDate.value.getFullYear())
const currentMonth = computed(() => currentDate.value.getMonth())
const weekDays = ['일', '월', '화', '수', '목', '금', '토']
const monthlyHealthData = ref([])
const isLoading = ref(false)

// 목표 설정 (하드코딩)
const targetExerciseMinutes = 180 // 3시간
const targetWaterIntakeMl = 2000 // 2L
const targetSleepMinutes = 480 // 8시간

// 이전 달로 이동
const prevMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value - 1, 1)
  fetchMonthlyHealthData()
}

// 다음 달로 이동
const nextMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value + 1, 1)
  fetchMonthlyHealthData()
}

// 월별 건강 데이터 가져오기
const fetchMonthlyHealthData = async () => {
  isLoading.value = true
  
  try {
    const year = currentYear.value
    const month = currentMonth.value + 1
    
    const response = await axios.get('/api/health-log/monthly', {
      params: { year, month }
    })
    
    console.log('월별 건강 데이터 응답:', response.data)
    
    if (response.data && response.data.success && response.data.data) {
      monthlyHealthData.value = response.data.data.logs || []
    } else {
      // API가 아직 구현되지 않았거나 오류가 있는 경우 더미 데이터 생성
      generateDummyData()
    }
  } catch (err) {
    console.error('월별 건강 데이터 로드 실패:', err)
    // API 오류 시 더미 데이터 생성
    generateDummyData()
  } finally {
    isLoading.value = false
  }
}

// 테스트용 더미 데이터 생성 (실제 API 연동 전까지 사용)
const generateDummyData = () => {
  const year = currentYear.value
  const month = currentMonth.value
  
  // 현재 달의 일수 계산
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  
  // 더미 데이터 배열
  const dummyData = []
  
  // 각 날짜별 더미 데이터 생성
  for (let day = 1; day <= daysInMonth; day++) {
    // 50% 확률로 데이터 생성 (일부 날짜는 데이터 없음)
    if (Math.random() > 0.3) {
      dummyData.push({
        logDate: `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`,
        exerciseMinutes: Math.floor(Math.random() * 240), // 0~240분
        waterIntakeMl: Math.floor(Math.random() * 3000), // 0~3000ml
        sleepMinutes: Math.floor(Math.random() * 600), // 0~600분
      })
    }
  }
  
  monthlyHealthData.value = dummyData
}

// 날짜에 해당하는 건강 데이터 찾기
const getHealthDataForDate = (date) => {
  const dateStr = date.toISOString().split('T')[0]
  
  const found = monthlyHealthData.value.find(item => {
    const itemDate = new Date(item.logDate)
    return itemDate.toISOString().split('T')[0] === dateStr
  })
  
  if (found) {
    return {
      hasData: true,
      exercisePercentage: Math.min(100, (found.exerciseMinutes / targetExerciseMinutes) * 100),
      waterPercentage: Math.min(100, (found.waterIntakeMl / targetWaterIntakeMl) * 100),
      sleepPercentage: Math.min(100, (found.sleepMinutes / targetSleepMinutes) * 100)
    }
  }
  
  return {
    hasData: false,
    exercisePercentage: 0,
    waterPercentage: 0,
    sleepPercentage: 0
  }
}

// 캘린더 날짜 계산
const calendarDays = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  
  // 이번 달의 첫날과 마지막 날
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  
  // 첫째 주 시작일 (이전 달의 날짜 포함)
  const firstDayOfWeek = firstDay.getDay()
  const startDate = new Date(firstDay)
  startDate.setDate(1 - firstDayOfWeek)
  
  // 마지막 주의 마지막 날 (다음 달의 날짜 포함)
  const lastDayOfWeek = lastDay.getDay()
  const endDate = new Date(lastDay)
  endDate.setDate(lastDay.getDate() + (6 - lastDayOfWeek))
  
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  const days = []
  const currentDate = new Date(startDate)
  
  while (currentDate <= endDate) {
    const isCurrentMonth = currentDate.getMonth() === month
    const isToday = currentDate.getTime() === today.getTime()
    
    // 건강 데이터 가져오기
    const healthData = getHealthDataForDate(currentDate)
    
    days.push({
      day: currentDate.getDate(),
      date: new Date(currentDate),
      differentMonth: !isCurrentMonth,
      isToday,
      ...healthData
    })
    
    currentDate.setDate(currentDate.getDate() + 1)
  }
  
  return days
})

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchMonthlyHealthData()
})
</script>

<style scoped>
.health-calendar {
  width: 100%;
}

/* 캘린더 그리드 스타일 */
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.calendar-day {
  padding: 8px 0;
  position: relative;
  min-height: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.different-month {
  opacity: 0.4;
}

.date-number {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
}

.today-number {
  color: white;
  background-color: #007bff;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-rings {
  margin-top: 2px;
}
</style> 