<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>식사 기록 추가 - {{ selectedDate }}</h2>
        <button class="close-button" @click="closeModal">&times;</button>
      </div>
      <div class="tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.value"
          :class="['tab-button', { active: currentTab === tab.value }]"
          @click="currentTab = tab.value"
        >
          {{ tab.label }}
        </button>
      </div>
      <div class="modal-body">
        <div v-if="loading" class="text-center my-4">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-2">기록을 불러오는 중...</p>
        </div>
        <div v-else-if="error" class="alert alert-danger" role="alert">
          {{ error }}
        </div>
        <div v-else class="records-container">
          <div class="records-header">
            <div class="header-cell">음식명</div>
            <div class="header-cell">칼로리(kcal)</div>
            <div class="header-cell">탄수화물(g)</div>
            <div class="header-cell">단백질</div>
            <div class="header-cell">지방</div>
            <div class="header-cell">당</div>
            <div class="header-cell"></div>
          </div>
          <div class="records-list">
            <div v-for="(record, index) in currentRecords" :key="index" class="record-row">
              <input type="text" v-model="record.dishName" placeholder="음식명" :disabled="saving">
              <input type="number" v-model="record.calories" min="0" step="0.1" :disabled="saving">
              <input type="number" v-model="record.carbs" min="0" step="0.1" :disabled="saving">
              <input type="number" v-model="record.protein" min="0" step="0.1" :disabled="saving">
              <input type="number" v-model="record.fat" min="0" step="0.1" :disabled="saving">
              <input type="number" v-model="record.sugar" min="0" step="0.1" :disabled="saving">
              <button 
                v-if="currentRecords.length > 1 || record.dishName"
                class="btn btn-outline-danger btn-sm delete-row-button rounded-circle" 
                @click="deleteRecord(index)"
                :disabled="saving"
              >
                <i class="bi bi-dash-lg"></i>
              </button>
            </div>
          </div>
          <div class="add-button-container">
            <button class="btn btn-outline-primary btn-sm add-row-button rounded-circle" 
                   @click="addNewRecord"
                   :disabled="saving">
              <i class="bi bi-plus-lg"></i>
            </button>
          </div>
        </div>
        <div v-if="saveSuccess" class="alert alert-success mt-3" role="alert">
          식사 기록이 성공적으로 저장되었습니다.
        </div>
        <div class="button-group">
          <button type="button" class="cancel-button" @click="closeModal" :disabled="saving">취소</button>
          <button type="submit" class="submit-button" @click="submitForm" :disabled="saving">
            <span v-if="saving" class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            {{ saving ? '저장 중...' : '저장' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from '@/plugins/axios'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'
import eventBus from '@/utils/eventBus'

const props = defineProps({
  selectedDate: {
    type: String,
    required: true
  },
  apiDate: {
    type: String,
    required: true
  },
  isWeekend: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['close'])

const tabs = computed(() => {
  if (props.isWeekend) {
    return [
      { label: '점심', value: 'LUNCH' },
      { label: '저녁', value: 'DINNER' },
      { label: '아침', value: 'BREAKFAST' },
      { label: '간식', value: 'SNACK' }
    ]
  }
  return [
    { label: '저녁', value: 'DINNER' },
    { label: '아침', value: 'BREAKFAST' },
    { label: '간식', value: 'SNACK' }
  ]
})

const currentTab = ref(props.isWeekend ? 'LUNCH' : 'DINNER')
const loading = ref(false)
const error = ref(null)
const saving = ref(false)
const saveSuccess = ref(false)

// 각 탭별로 독립적인 records 관리
const recordsByTab = ref({
  DINNER: [{
    courseType: 'DINNER',
    dishName: '',
    calories: '',
    carbs: '',
    protein: '',
    fat: '',
    sugar: ''
  }],
  BREAKFAST: [{
    courseType: 'BREAKFAST',
    dishName: '',
    calories: '',
    carbs: '',
    protein: '',
    fat: '',
    sugar: ''
  }],
  SNACK: [{
    courseType: 'SNACK',
    dishName: '',
    calories: '',
    carbs: '',
    protein: '',
    fat: '',
    sugar: ''
  }],
  LUNCH: [{
    courseType: 'LUNCH',
    dishName: '',
    calories: '',
    carbs: '',
    protein: '',
    fat: '',
    sugar: ''
  }]
})

// 기존 기록 불러오기
const fetchExistingRecords = async () => {
  loading.value = true
  error.value = null
  
  try {
    const response = await axios.get(`/api/extra-meal-records/by-date/${props.apiDate}`)
    const records = response.data

    // 기존 기록을 탭별로 분류
    resetRecordsByTab()
    
    records.forEach(record => {
      const courseType = record.courseType
      // 빈 초기 레코드가 있으면 제거
      if (recordsByTab.value[courseType].length === 1 && !recordsByTab.value[courseType][0].dishName) {
        recordsByTab.value[courseType] = []
      }
      
      recordsByTab.value[courseType].push({
        id: record.id,
        courseType: record.courseType,
        dishName: record.dishName,
        calories: record.calories,
        carbs: record.carbs,
        protein: record.protein,
        fat: record.fat,
        sugar: record.sugar
      })
    })
    
    // 기록이 없는 탭에 빈 레코드 추가
    Object.keys(recordsByTab.value).forEach(tab => {
      if (recordsByTab.value[tab].length === 0) {
        recordsByTab.value[tab].push({
          courseType: tab,
          dishName: '',
          calories: '',
          carbs: '',
          protein: '',
          fat: '',
          sugar: ''
        })
      }
    })
    
  } catch (err) {
    console.error('식사 기록 불러오기 실패:', err)
    error.value = '식사 기록을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 모든 탭의 레코드 초기화
const resetRecordsByTab = () => {
  Object.keys(recordsByTab.value).forEach(tab => {
    recordsByTab.value[tab] = []
  })
}

// 현재 탭의 records를 반환하는 computed 속성
const currentRecords = computed(() => {
  return recordsByTab.value[currentTab.value] || []
})

const addNewRecord = () => {
  recordsByTab.value[currentTab.value].push({
    courseType: currentTab.value,
    dishName: '',
    calories: '',
    carbs: '',
    protein: '',
    fat: '',
    sugar: ''
  })
}

const deleteRecord = (index) => {
  const record = recordsByTab.value[currentTab.value][index]
  
  // 이미 저장된 기록이면 API를 통해 삭제
  if (record.id) {
    deleteRecordFromServer(record.id, index)
  } else {
    recordsByTab.value[currentTab.value].splice(index, 1)
    
    // 모든 레코드가 삭제되었다면 빈 레코드 추가
    if (recordsByTab.value[currentTab.value].length === 0) {
      addNewRecord()
    }
  }
}

const deleteRecordFromServer = async (id, index) => {
  try {
    await axios.delete(`/api/extra-meal-records/${id}`)
    recordsByTab.value[currentTab.value].splice(index, 1)
    
    // 모든 레코드가 삭제되었다면 빈 레코드 추가
    if (recordsByTab.value[currentTab.value].length === 0) {
      addNewRecord()
    }
  } catch (error) {
    console.error('식사 기록 삭제 실패:', error)
    alert('식사 기록 삭제에 실패했습니다.')
  }
}

const closeModal = () => {
  emit('close')
}

const submitForm = async () => {
  try {
    saving.value = true
    saveSuccess.value = false
    
    // 모든 탭의 유효한 레코드 수집
    const allValidRecords = []
    
    for (const tab in recordsByTab.value) {
      const validTabRecords = recordsByTab.value[tab].filter(record => record.dishName.trim() !== '')
      allValidRecords.push(...validTabRecords)
    }
    
    if (allValidRecords.length === 0) {
      alert('최소 하나의 음식을 입력해주세요.')
      saving.value = false
      return
    }

    // 모든 레코드에 대해 API 호출
    const promises = allValidRecords.map(record => {
      const requestData = {
        id: record.id, // 기존 기록인 경우 ID 포함
        userId: 1, // 테스트용 고정 값
        courseType: record.courseType,
        dishName: record.dishName,
        calories: record.calories || 0,
        carbs: record.carbs || 0,
        protein: record.protein || 0,
        fat: record.fat || 0,
        sugar: record.sugar || 0,
        recordedAt: props.apiDate
      }
      
      // 기존 기록이면 업데이트, 아니면 신규 추가
      if (record.id) {
        return axios.put(`/api/extra-meal-records/${record.id}`, requestData)
      } else {
        return axios.post('/api/extra-meal-records', requestData)
      }
    })

    await Promise.all(promises)
    saveSuccess.value = true
    setTimeout(() => {
      saveSuccess.value = false
    }, 3000)
    
    fetchExistingRecords() // 기록 다시 불러오기
    eventBus.emit('meal-data-updated')
  } catch (error) {
    console.error('식사 기록 저장 실패:', error)
    alert('식사 기록 저장에 실패했습니다.')
  } finally {
    saving.value = false
  }
}

// 컴포넌트 마운트 시 기존 기록 불러오기
onMounted(() => {
  fetchExistingRecords()
})

// 날짜가 변경되면 기록 다시 불러오기
watch(() => props.apiDate, () => {
  fetchExistingRecords()
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  width: 95%;
  max-width: 1000px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #666;
  cursor: pointer;
}

.tabs {
  display: flex;
  padding: 0 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.tab-button {
  padding: 12px 24px;
  border: none;
  background: none;
  font-size: 0.95rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.tab-button.active {
  color: #4a90e2;
  font-weight: 600;
}

.tab-button.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #4a90e2;
}

.modal-body {
  padding: 20px;
}

.records-container {
  margin-bottom: 20px;
}

.records-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr 40px;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 2px solid #eee;
  margin-bottom: 12px;
}

.header-cell {
  font-size: 0.9rem;
  color: #666;
  text-align: center;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr 40px;
  gap: 12px;
  align-items: center;
}

.record-row input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.record-row input:focus {
  outline: none;
  border-color: #4a90e2;
}

.add-button-container {
  display: flex;
  justify-content: center;
  margin-top: 12px;
}

.add-row-button {
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  border-radius: 50% !important;
}

.add-row-button:hover {
  transform: scale(1.1);
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.cancel-button, .submit-button {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.cancel-button {
  background-color: #f1f3f5;
  color: #666;
}

.submit-button {
  background-color: #4a90e2;
  color: white;
}

.cancel-button:hover {
  background-color: #e9ecef;
}

.submit-button:hover {
  background-color: #357abd;
}

.delete-row-button {
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  border-radius: 50% !important;
}

.delete-row-button:hover {
  transform: scale(1.1);
}

.bi {
  font-size: 1.2rem;
  line-height: 1;
  position: relative;
  top: 1px;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border-color: #c3e6cb;
  padding: 0.75rem 1.25rem;
  border-radius: 0.25rem;
  animation: fadeIn 0.3s, fadeOut 0.5s 2.5s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeOut {
  from { opacity: 1; }
  to { opacity: 0; }
}
</style> 