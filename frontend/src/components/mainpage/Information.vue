<template>
  <div class="info-section-low">
    <div class="card shadow-sm info-card-low">
      <!-- 헤더 -->
      <div class="card-header bg-white d-flex justify-content-between align-items-center">
        <h6 class="mb-0">건강 정보</h6>
        <div>
          <button class="btn btn-sm btn-outline-primary" @click="openDetailModal">
            활동 로그 보기
          </button>
        </div>
      </div>

      <!-- 본문 -->
      <div class="card-body info-card-body-low p-3">
        <div class="row gx-3 gy-2">
          <!-- 운동시간 -->
          <div class="col-6 text-center">
            <div class="d-flex align-items-center justify-content-center mb-1">
              <button
                class="btn btn-sm btn-outline-danger me-2 control-btn"
                @click="updateExerciseMinutes(-10)"
              >
                -
              </button>
              <div class="text-muted info-title mx-2">운동시간</div>
              <button
                class="btn btn-sm btn-outline-success ms-2 control-btn"
                @click="updateExerciseMinutes(10)"
              >
                +
              </button>
            </div>
            <div class="d-inline-block position-relative" style="width: 90px; height: 90px">
              <DonutChart
                :value="healthData.exerciseMinutes"
                :max="targetExerciseMinutes"
                suffix="분"
                color="#ff6b6b"
              />
            </div>
          </div>

          <!-- 물 섭취 -->
          <div class="col-6 text-center">
            <div class="d-flex align-items-center justify-content-center mb-1">
              <button
                class="btn btn-sm btn-outline-danger me-2 control-btn"
                @click="updateWaterIntake(-100)"
              >
                -
              </button>
              <div class="text-muted info-title mx-2">물 섭취</div>
              <button
                class="btn btn-sm btn-outline-success ms-2 control-btn"
                @click="updateWaterIntake(100)"
              >
                +
              </button>
            </div>
            <div class="d-inline-block position-relative" style="width: 90px; height: 90px">
              <DonutChart
                :value="waterIntakeLiters"
                :max="targetWaterLiters"
                suffix="L"
                color="#4dabf7"
                :decimalPlaces="1"
              />
            </div>
          </div>

          <!-- 오늘 수면 시간 -->
          <div class="col-12 mt-3">
            <div class="d-flex align-items-center justify-content-center mb-1">
              <button
                class="btn btn-sm btn-outline-danger me-2 control-btn"
                @click="updateSleepMinutes(-30)"
              >
                -
              </button>
              <div class="text-muted info-title mx-2">
                오늘 수면 시간: {{ formatSleepTime(healthData.sleepMinutes) }}
              </div>
              <button
                class="btn btn-sm btn-outline-success ms-2 control-btn"
                @click="updateSleepMinutes(30)"
              >
                +
              </button>
            </div>
            <div class="sleep-bar-chart mt-1">
              <div class="progress" style="height: 18px">
                <div
                  class="progress-bar bg-info"
                  role="progressbar"
                  :style="{ width: `${(healthData.sleepMinutes / targetSleepMinutes) * 100}%` }"
                  :aria-valuenow="healthData.sleepMinutes"
                  aria-valuemin="0"
                  :aria-valuemax="targetSleepMinutes"
                >
                  {{ formatSleepTime(healthData.sleepMinutes) }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 상세 정보 모달 -->
    <div
      class="modal fade"
      id="healthDetailModal"
      tabindex="-1"
      aria-labelledby="healthDetailModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="healthDetailModalLabel">활동 로그 그래프</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div class="modal-body">
            <!-- 탭 네비게이션 -->
            <ul class="nav nav-tabs mb-3">
              <li class="nav-item" v-for="(tab, index) in tabs" :key="index">
                <a
                  class="nav-link"
                  :class="{ active: currentTab === tab.value }"
                  href="#"
                  @click.prevent="changeTab(tab.value)"
                >
                  {{ tab.label }}
                </a>
              </li>
            </ul>

            <!-- 차트 영역 -->
            <div class="chart-container" style="position: relative; height: 300px">
              <canvas ref="chartCanvas"></canvas>
            </div>

            <p v-if="chartError" class="text-danger mt-2">{{ chartError }}</p>
            <p v-if="chartLoading" class="text-muted mt-2">데이터를 불러오는 중...</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, onUnmounted } from 'vue'
import DonutChart from '@/components/common/DonutChart.vue'
import axios from '@/plugins/axios'
import { Chart, registerables } from 'chart.js'

// Chart.js 등록
Chart.register(...registerables)

// 타겟 값 설정
const targetExerciseMinutes = 180 // 일일 목표 운동 시간 (분) - 3시간으로 변경
const targetWaterLiters = 2 // 일일 목표 물 섭취량 (리터)
const targetSleepMinutes = 600 // 일일 목표 수면 시간 (분) - 10시간으로 변경

// 건강 데이터 상태
const healthData = ref({
  exerciseMinutes: 0,
  waterIntakeMl: 0,
  sleepMinutes: 0,
})
const loading = ref(false)
const error = ref(null)

// 차트 관련 상태
const chartCanvas = ref(null)
const chartInstance = ref(null)
const chartLoading = ref(false)
const chartError = ref(null)
const healthStatsData = ref([])
const currentTab = ref('exercise') // 기본 탭 - 운동 시간

// 탭 정의
const tabs = [
  { label: '운동 시간', value: 'exercise' },
  { label: '물 섭취량', value: 'water' },
  { label: '수면 시간', value: 'sleep' },
]

// 색상 및 단위 설정
const colors = {
  exercise: 'rgb(255, 99, 132)',
  water: 'rgb(54, 162, 235)',
  sleep: 'rgb(75, 192, 192)',
}

const units = {
  exercise: '분',
  water: 'L',
  sleep: '시간',
}

// ML를 L로 변환 (소수점 1자리)
const waterIntakeLiters = computed(() => {
  return healthData.value.waterIntakeMl / 1000
})

// 수면 시간 포맷 (분 -> 시간:분)
const formatSleepTime = (minutes) => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}시간 ${mins}분`
}

// 모달 인스턴스
let modal = null

// 모달 열기
const openDetailModal = async () => {
  // 모달 열기 전에 데이터 로드
  await fetchHealthStats()

  if (!modal) {
    // Bootstrap 모달 초기화 (첫 실행 시에만)
    const { Modal } = await import('bootstrap')
    const modalElement = document.getElementById('healthDetailModal')
    modal = new Modal(modalElement)
  }

  modal.show()

  // 모달이 완전히 표시된 후 차트 업데이트
  setTimeout(() => {
    updateChart()
  }, 300)
}

// 최근 일주일 건강 통계 데이터 가져오기
const fetchHealthStats = async () => {
  chartLoading.value = true
  chartError.value = null

  try {
    const response = await axios.get('/api/health-log/stats', {
      params: { days: 7 }, // 일주일치 데이터 요청
    })

    console.log('건강 통계 응답:', response.data)

    if (response.data && response.data.success && response.data.data) {
      // logs 배열을 가져와서 저장
      healthStatsData.value = response.data.data.logs || []
    } else {
      chartError.value = '건강 통계 데이터를 가져오는 데 실패했습니다.'
    }
  } catch (err) {
    console.error('건강 통계 데이터 로드 실패:', err)
    chartError.value = '건강 통계를 가져오는 중 오류가 발생했습니다.'
  } finally {
    chartLoading.value = false
  }
}

// 오늘의 건강 로그 데이터 가져오기
const fetchTodayHealthLog = async () => {
  loading.value = true
  error.value = null

  try {
    const response = await axios.get('/api/health-log/today')
    console.log('건강 로그 응답:', response.data)

    if (response.data && response.data.success && response.data.data) {
      const data = response.data.data
      healthData.value = {
        exerciseMinutes: data.exerciseMinutes || 0,
        waterIntakeMl: data.waterIntakeMl || 0,
        sleepMinutes: data.sleepMinutes || 0,
      }
    } else {
      error.value =
        response.data && response.data.message
          ? response.data.message
          : '데이터를 가져오는 데 실패했습니다.'
      console.error('건강 로그 데이터 형식 오류:', response.data)
    }
  } catch (err) {
    console.error('건강 로그 데이터 로드 실패:', err)
    error.value = '건강 로그를 가져오는 중 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}

// 운동 시간 업데이트
const updateExerciseMinutes = async (minutes) => {
  try {
    const response = await axios.post('/api/health-log/exercise', null, {
      params: { minutes },
    })

    console.log('운동 시간 업데이트 응답:', response.data)

    if (response.data && response.data.success && response.data.data) {
      const data = response.data.data
      healthData.value = {
        exerciseMinutes: data.exerciseMinutes || healthData.value.exerciseMinutes || 0,
        waterIntakeMl: data.waterIntakeMl || healthData.value.waterIntakeMl || 0,
        sleepMinutes: data.sleepMinutes || healthData.value.sleepMinutes || 0,
      }
    } else {
      console.error(
        '운동 시간 업데이트 실패:',
        response.data ? response.data.message : '응답 형식 오류',
      )
    }
  } catch (err) {
    console.error('운동 시간 업데이트 API 호출 실패:', err)
  }
}

// 물 섭취량 업데이트
const updateWaterIntake = async (milliliters) => {
  try {
    const response = await axios.post('/api/health-log/water', null, {
      params: { milliliters },
    })

    console.log('물 섭취량 업데이트 응답:', response.data)

    if (response.data && response.data.success && response.data.data) {
      const data = response.data.data
      healthData.value = {
        exerciseMinutes: data.exerciseMinutes || healthData.value.exerciseMinutes || 0,
        waterIntakeMl: data.waterIntakeMl || healthData.value.waterIntakeMl || 0,
        sleepMinutes: data.sleepMinutes || healthData.value.sleepMinutes || 0,
      }
    } else {
      console.error(
        '물 섭취량 업데이트 실패:',
        response.data ? response.data.message : '응답 형식 오류',
      )
    }
  } catch (err) {
    console.error('물 섭취량 업데이트 API 호출 실패:', err)
  }
}

// 수면 시간 업데이트
const updateSleepMinutes = async (minutes) => {
  try {
    const response = await axios.post('/api/health-log/sleep', null, {
      params: { minutes },
    })

    console.log('수면 시간 업데이트 응답:', response.data)

    if (response.data && response.data.success && response.data.data) {
      const data = response.data.data
      healthData.value = {
        exerciseMinutes: data.exerciseMinutes || healthData.value.exerciseMinutes || 0,
        waterIntakeMl: data.waterIntakeMl || healthData.value.waterIntakeMl || 0,
        sleepMinutes: data.sleepMinutes || healthData.value.sleepMinutes || 0,
      }
    } else {
      console.error(
        '수면 시간 업데이트 실패:',
        response.data ? response.data.message : '응답 형식 오류',
      )
    }
  } catch (err) {
    console.error('수면 시간 업데이트 API 호출 실패:', err)
  }
}

// 데이터 새로고침
const refreshData = () => {
  fetchTodayHealthLog()
}

// 탭 변경 핸들러
const changeTab = (tabValue) => {
  if (currentTab.value === tabValue) return

  currentTab.value = tabValue
  updateChart()
}

// 차트 데이터 준비
const prepareChartData = () => {
  if (!healthStatsData.value || healthStatsData.value.length === 0) {
    return { labels: [], data: [] }
  }

  // 최근 일주일간의 날짜 라벨 준비 (오름차순으로 정렬)
  let logs = [...healthStatsData.value] // 복사본 생성
  logs.sort((a, b) => new Date(a.logDate) - new Date(b.logDate)) // 날짜순 정렬

  const labels = logs.map((item) => {
    const date = new Date(item.logDate)
    return `${date.getMonth() + 1}.${date.getDate()}`
  })

  // 선택된 탭에 따라 데이터 추출
  let data = []

  if (currentTab.value === 'exercise') {
    data = logs.map((item) => item.exerciseMinutes || 0)
  } else if (currentTab.value === 'water') {
    data = logs.map((item) => (item.waterIntakeMl || 0) / 1000) // ml -> L 변환
  } else if (currentTab.value === 'sleep') {
    data = logs.map((item) => {
      const minutes = item.sleepMinutes || 0
      return +(minutes / 60).toFixed(1) // 분 -> 시간 변환 (소수점 1자리)
    })
  }

  return { labels, data }
}

// 차트 업데이트
const updateChart = async () => {
  // 데이터가 없는 경우
  if (!healthStatsData.value || healthStatsData.value.length === 0) {
    console.log('건강 통계 데이터가 없습니다')
    return
  }

  // 차트 캔버스 확인
  if (!chartCanvas.value) {
    console.error('차트 캔버스 요소를 찾을 수 없습니다')
    return
  }

  // 이전 차트 인스턴스 제거
  if (chartInstance.value) {
    chartInstance.value.destroy()
    chartInstance.value = null
  }

  // 차트 데이터 준비
  const { labels, data } = prepareChartData()

  // 현재 탭에 따른 차트 타이틀과 단위
  let chartTitle = ''
  if (currentTab.value === 'exercise') {
    chartTitle = '일일 운동 시간'
  } else if (currentTab.value === 'water') {
    chartTitle = '일일 물 섭취량'
  } else if (currentTab.value === 'sleep') {
    chartTitle = '일일 수면 시간'
  }

  // DOM 업데이트 보장 후 차트 생성
  await nextTick()

  try {
    const ctx = chartCanvas.value.getContext('2d')
    chartInstance.value = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: `${chartTitle} (${units[currentTab.value]})`,
            data: data,
            borderColor: colors[currentTab.value],
            backgroundColor: `${colors[currentTab.value].slice(0, -1)}, 0.2)`,
            borderWidth: 2,
            fill: true,
            tension: 0.4,
            pointBackgroundColor: colors[currentTab.value],
            pointRadius: 4,
          },
        ],
      },
      options: {
        animation: {
          duration: 800,
          easing: 'easeOutQuart',
        },
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'top',
          },
          tooltip: {
            callbacks: {
              label: function (context) {
                return `${chartTitle}: ${context.raw} ${units[currentTab.value]}`
              },
            },
          },
        },
        scales: {
          x: {
            grid: {
              display: false,
            },
          },
          y: {
            beginAtZero: true,
            ticks: {
              callback: function (value) {
                return `${value} ${units[currentTab.value]}`
              },
            },
          },
        },
      },
    })
  } catch (error) {
    console.error('차트 렌더링 실패:', error)
  }
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchTodayHealthLog()
})

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {})
</script>

<style scoped>
.info-section-low {
  max-height: 340px;
  min-height: 0;
  height: 340px;
  padding-bottom: 0;
}
.info-card-low {
  max-height: 320px;
  min-height: 0;
  height: 100%;
  overflow: hidden;
}
.info-card-body-low {
  max-height: 240px;
  min-height: 0;
  height: 240px;
  overflow: hidden;
  padding-bottom: 0 !important;
}
.sleep-bar-chart {
  height: 18px;
}
.chart-placeholder {
  background-color: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  color: #6c757d;
  font-size: 0.8rem;
}
.info-title {
  font-weight: 700;
  font-size: 0.95rem;
}
.control-btn {
  width: 30px;
  height: 30px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1rem;
}
.nav-link {
  cursor: pointer;
  color: #495057;
}
.nav-link.active {
  color: #007bff;
  font-weight: 500;
}
</style>
