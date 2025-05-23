<template>
  <div class="calendar-modern-outer calendar-rect">
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
            today: isToday(day.date, day.isCurrentMonth),
            selected: day.date === selectedDate && day.isCurrentMonth,
            'other-month': day.isPrevMonth || day.isNextMonth,
          }"
          @click="selectDate(day)"
        >
          {{ day.date }}
        </div>
      </div>
      <div class="calendar-modern-schedule-list">
        <div
          v-for="item in filteredSchedule"
          :key="item.time"
          class="calendar-modern-schedule-item"
        >
          <span class="calendar-modern-schedule-time">{{ item.time }}</span>
          <span class="calendar-modern-schedule-title">{{ item.title }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const today = new Date()
const year = ref(today.getFullYear())
const month = ref(today.getMonth()) // 0-indexed
const selectedDate = ref(today.getDate())

const weekDays = ['월', '화', '수', '목', '금', '토', '일']

const monthNames = [
  '1월',
  '2월',
  '3월',
  '4월',
  '5월',
  '6월',
  '7월',
  '8월',
  '9월',
  '10월',
  '11월',
  '12월',
]

const displayMonth = computed(() => {
  return `${monthNames[month.value]}`
})

// 이전 달의 마지막 날짜들을 계산
const prevMonthDays = computed(() => {
  const prevMonth = month.value === 0 ? 11 : month.value - 1
  const prevYear = month.value === 0 ? year.value - 1 : year.value
  const prevMonthLastDay = new Date(prevYear, month.value, 0).getDate()
  const firstDayOfMonth = new Date(year.value, month.value, 1).getDay()
  const prevDays = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1

  return Array.from({ length: prevDays }, (_, i) => ({
    date: prevMonthLastDay - prevDays + i + 1,
    isPrevMonth: true,
  }))
})

// 현재 달의 날짜들을 계산
const currentMonthDays = computed(() => {
  const daysInCurrentMonth = new Date(year.value, month.value + 1, 0).getDate()
  return Array.from({ length: daysInCurrentMonth }, (_, i) => ({
    date: i + 1,
    isCurrentMonth: true,
  }))
})

// 다음 달의 첫 날짜들을 계산
const nextMonthDays = computed(() => {
  const totalDays = prevMonthDays.value.length + currentMonthDays.value.length
  const remainingDays = 35 - totalDays // 5주 x 7일 = 35로 변경

  return remainingDays > 0
    ? Array.from({ length: remainingDays }, (_, i) => ({
        date: i + 1,
        isNextMonth: true,
      }))
    : []
})

// 전체 날짜 배열 생성 (5주로 제한)
const allDays = computed(() => {
  const days = [...prevMonthDays.value, ...currentMonthDays.value, ...nextMonthDays.value]
  return days.slice(0, 35) // 5주 x 7일 = 35일로 제한
})

const isToday = (date, isCurrentMonth) => {
  return (
    isCurrentMonth &&
    year.value === today.getFullYear() &&
    month.value === today.getMonth() &&
    date === today.getDate()
  )
}

const selectDate = (day) => {
  if (day.isPrevMonth) {
    if (month.value === 0) {
      year.value--
      month.value = 11
    } else {
      month.value--
    }
    selectedDate.value = day.date
  } else if (day.isNextMonth) {
    if (month.value === 11) {
      year.value++
      month.value = 0
    } else {
      month.value++
    }
    selectedDate.value = day.date
  } else {
    selectedDate.value = day.date
  }
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
</script>

<style scoped>
.calendar-modern-outer.calendar-rect {
  width: 100%;
  min-width: 320px;
  max-width: 420px;
  min-height: 400px;
  max-height: 520px;
  margin-top: 0 !important;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
}

.calendar-modern-container.calendar-rect-container {
  width: 100%;
  min-width: 320px;
  max-width: 420px;
  min-height: 400px;
  max-height: 520px;
  height: 100%;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.09);
  padding: 28px 20px 32px 20px;
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
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.08rem;
  transition: all 0.2s ease;
  color: #222;
}

.calendar-modern-cell::before {
  content: '';
  display: block;
  padding-top: 100%;
}

.calendar-modern-cell.today {
  background: #4a90e2;
  color: white;
  border: none;
}

.calendar-modern-cell.selected:not(.today) {
  border: 2px solid #ff5a5f;
  color: #ff5a5f;
}

.calendar-modern-cell.blank {
  background: none;
  cursor: default;
}

.calendar-modern-schedule-list {
  margin-top: 14px;
  border-top: 1px solid #eee;
  padding-top: 12px;
}

.calendar-modern-schedule-item {
  margin-bottom: 8px;
}

.calendar-modern-schedule-time {
  font-size: 0.95rem;
  color: #888;
  margin-right: 8px;
}

.calendar-modern-schedule-title {
  font-size: 1.08rem;
  font-weight: 500;
}

.calendar-modern-cell.other-month {
  color: #ccc;
  cursor: pointer;
}

.calendar-modern-cell.other-month:hover {
  background: rgba(204, 204, 204, 0.1);
  color: #999;
}

.calendar-modern-month-label {
  font-size: 1.4rem;
  font-weight: 700;
}
</style>
