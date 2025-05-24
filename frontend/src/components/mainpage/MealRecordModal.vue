<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>식사 기록 추가</h2>
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
        <div class="records-container">
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
              <input type="text" v-model="record.dishName" placeholder="음식명">
              <input type="number" v-model="record.calories" min="0" step="0.1">
              <input type="number" v-model="record.carbs" min="0" step="0.1">
              <input type="number" v-model="record.protein" min="0" step="0.1">
              <input type="number" v-model="record.fat" min="0" step="0.1">
              <input type="number" v-model="record.sugar" min="0" step="0.1">
              <button 
                v-if="currentRecords.length > 1"
                class="btn btn-outline-danger btn-sm delete-row-button rounded-circle" 
                @click="deleteRecord(index)"
              >
                <i class="bi bi-dash-lg"></i>
              </button>
            </div>
          </div>
          <div class="add-button-container">
            <button class="btn btn-outline-primary btn-sm add-row-button rounded-circle" @click="addNewRecord">
              <i class="bi bi-plus-lg"></i>
            </button>
          </div>
        </div>
        <div class="button-group">
          <button type="button" class="cancel-button" @click="closeModal">취소</button>
          <button type="submit" class="submit-button" @click="submitForm">저장</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from '@/plugins/axios'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

const props = defineProps({
  selectedDate: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['close'])

const tabs = [
  { label: '저녁', value: 'dinner' },
  { label: '아침', value: 'breakfast' },
  { label: '간식', value: 'snack' }
]

const currentTab = ref('dinner')

const recordsByTab = ref({
  dinner: [{
    courseType: 'dinner',
    dishName: '',
    calories: '',
    carbs: '',
    protein: '',
    fat: '',
    sugar: ''
  }],
  breakfast: [{
    courseType: 'breakfast',
    dishName: '',
    calories: '',
    carbs: '',
    protein: '',
    fat: '',
    sugar: ''
  }],
  snack: [{
    courseType: 'snack',
    dishName: '',
    calories: '',
    carbs: '',
    protein: '',
    fat: '',
    sugar: ''
  }]
})

const currentRecords = computed(() => {
  return recordsByTab.value[currentTab.value]
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
  recordsByTab.value[currentTab.value].splice(index, 1)
}

const closeModal = () => {
  emit('close')
}

const submitForm = async () => {
  try {
    const allValidRecords = Object.values(recordsByTab.value).flatMap(records => 
      records.filter(record => record.dishName.trim() !== '')
    )
    
    if (allValidRecords.length === 0) {
      alert('최소 하나의 음식을 입력해주세요.')
      return
    }

    const promises = allValidRecords.map(record => 
      axios.post('/api/meal-records', {
        ...record,
        recordedAt: props.selectedDate
      })
    )

    await Promise.all(promises)
    closeModal()
    window.eventBus.emit('meal-data-updated')
  } catch (error) {
    console.error('식사 기록 저장 실패:', error)
    alert('식사 기록 저장에 실패했습니다.')
  }
}
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
</style> 