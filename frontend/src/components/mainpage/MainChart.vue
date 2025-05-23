<template>
  <div class="main-chart-container mb-4">
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex flex-column align-items-start">
        <span>최근 5일 영양소 섭취 추이</span>
      </div>
      <ul class="nav nav-tabs card-header-tabs px-4 mt-2">
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
      <div class="card-body">
        <div class="chart-container" style="height: 300px">
          <canvas ref="chartCanvas"></canvas>
        </div>
        <p v-if="error" class="text-danger mt-2">{{ error }}</p>
        <p v-if="loading" class="text-muted mt-2">데이터를 불러오는 중...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick, onUnmounted } from 'vue'
import { Chart, registerables } from 'chart.js'
import axios from '@/plugins/axios'
import eventBus from '@/utils/eventBus' // 이벤트 버스 가져오기

Chart.register(...registerables)

// 상태 변수
const chartCanvas = ref(null)
const chartInstance = ref(null)
const currentTab = ref('calories')
const loading = ref(false)
const error = ref(null)
const nutrientsData = ref([])
const renderingInProgress = ref(false) // 렌더링 진행 중 상태 추적
const pendingTab = ref(null) // 대기 중인 탭 변경 요청

// 색상과 단위 설정
const colors = {
  calories: 'rgb(255, 99, 132)',
  carbohydrate: 'rgb(54, 162, 235)',
  protein: 'rgb(255, 206, 86)',
  fat: 'rgb(75, 192, 192)',
  sugar: 'rgb(153, 102, 255)',
}

const pastelBackgroundColors = {
  calories: 'rgba(255, 99, 132, 0.3)',
  carbohydrate: 'rgba(54, 162, 235, 0.3)',
  protein: 'rgba(255, 206, 86, 0.3)',
  fat: 'rgba(75, 192, 192, 0.3)',
  sugar: 'rgba(153, 102, 255, 0.3)',
}

const units = {
  calories: 'kcal',
  carbohydrate: 'g',
  protein: 'g',
  fat: 'g',
  sugar: 'g',
}

// 탭 메뉴 옵션
const tabs = [
  { label: '칼로리', value: 'calories' },
  { label: '탄수화물', value: 'carbohydrate' },
  { label: '단백질', value: 'protein' },
  { label: '지방', value: 'fat' },
  { label: '당', value: 'sugar' },
]

// 탭 변경 핸들러
const changeTab = (tabValue) => {
  if (currentTab.value === tabValue) return

  // 탭 즉시 변경
  currentTab.value = tabValue

  // 진행 중인 렌더링에 상관없이 즉시 차트 업데이트
  updateChart()
}

// 5일간의 영양소 데이터 가져오기
const fetchNutrientsData = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await axios.get('/api/meal-records/last-5-days')
    nutrientsData.value = response.data
    await nextTick() // DOM 업데이트 보장
    updateChart()
  } catch (err) {
    console.error('영양소 데이터 가져오기 실패:', err)
    error.value = '데이터를 불러오는 데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 프리로드된 차트 데이터 캐시
const chartDataCache = reactive({})

// 차트 데이터 준비 (탭별로 한 번만 계산하도록 최적화)
const prepareChartData = (tabValue) => {
  // 캐시에 이미 있으면 재사용
  if (chartDataCache[tabValue]) {
    return chartDataCache[tabValue]
  }

  // 최근 5일간의 라벨(날짜) 준비
  const labels = nutrientsData.value.map((item) => {
    const date = new Date(item.date)
    return `${date.getMonth() + 1}.${date.getDate()}`
  })

  // 선택된 영양소 데이터 추출
  const data = nutrientsData.value.map((item) => {
    const nutrients = item.nutrients || {}
    return nutrients[tabValue] || 0
  })

  // 캐시에 저장
  chartDataCache[tabValue] = {
    labels,
    data,
  }

  return chartDataCache[tabValue]
}

// 차트 업데이트
const updateChart = async () => {
  // 데이터가 없는 경우 차트를 그리지 않음
  if (!nutrientsData.value || nutrientsData.value.length === 0) {
    console.error('영양소 데이터가 없습니다')
    return
  }

  // 차트 캔버스 요소 확인
  if (!chartCanvas.value) {
    console.error('차트 캔버스 요소를 찾을 수 없습니다')
    return
  }

  // 이전 차트 인스턴스가 있으면 즉시 제거
  if (chartInstance.value) {
    chartInstance.value.destroy()
    chartInstance.value = null
  }

  // 현재 탭 데이터 준비
  const tabValue = currentTab.value
  const { labels, data } = prepareChartData(tabValue)

  // 안정적인 렌더링을 위해 try-catch 추가
  try {
    // 새 차트 생성 전에 DOM 업데이트 보장
    await nextTick()

    const ctx = chartCanvas.value.getContext('2d')
    chartInstance.value = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: `${tabs.find((t) => t.value === tabValue)?.label || ''} (${units[tabValue]})`,
            data: data,
            borderColor: colors[tabValue],
            backgroundColor: pastelBackgroundColors[tabValue],
            borderWidth: 2,
            fill: true,
            tension: 0.4,
            pointBackgroundColor: colors[tabValue],
            pointRadius: 4,
          },
        ],
      },
      options: {
        animation: {
          duration: 800, // 애니메이션 시간 증가 (300ms → 800ms)
          easing: 'easeOutQuart', // 부드러운 이징 효과 추가
        },
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'top',
            align: 'end',
            labels: {
              padding: 2, // 범례와 그래프 사이의 간격을 2px로 설정
            },
          },
          tooltip: {
            callbacks: {
              label: function (context) {
                return `${context.dataset.label}: ${context.raw} ${units[tabValue]}`
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
                return `${value} ${units[tabValue]}`
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

// 영양소 데이터 변경 시 캐시 초기화
watch(nutrientsData, () => {
  Object.keys(chartDataCache).forEach((key) => {
    delete chartDataCache[key]
  })
})

// 이벤트 리스너 등록 - 식단 데이터가 업데이트되었을 때 차트 데이터도 업데이트
const handleMealDataUpdated = () => {
  console.log('식단 데이터 업데이트 이벤트 수신 - 차트 데이터 새로 불러옴')
  fetchNutrientsData()
}

onMounted(async () => {
  // 이벤트 리스너 등록
  eventBus.on('meal-data-updated', handleMealDataUpdated)

  // 컴포넌트가 완전히 마운트된 후 데이터 로드
  await nextTick()
  fetchNutrientsData()
})

onUnmounted(() => {
  // 컴포넌트 언마운트 시 이벤트 리스너 제거
  eventBus.off('meal-data-updated', handleMealDataUpdated)
})
</script>

<style scoped>
.chart-container {
  position: relative;
}

.nav-link {
  cursor: pointer;
  color: #495057;
}

.nav-link.active {
  color: #007bff;
  font-weight: 500;
}

.main-chart-container {
  width: 100%;
  min-height: 400px;
  height: 100%;
}
</style>
