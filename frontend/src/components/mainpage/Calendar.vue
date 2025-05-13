<template>
    <div class="col-12">
        <div class="card shadow-sm">
            <div class="card-header bg-success text-white">
                오늘은 {{ currentDay }}요일 입니다
            </div>
            <div class="card-body p-3">
                <small>
                    <i class="bi bi-calendar3"></i> {{ currentDate }}  
                    <i class="bi bi-clock ms-3"></i> {{ currentTime }}
                </small>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const currentDay = ref('')
const currentDate = ref('')
const currentTime = ref('')
let timer = null

const updateDateTime = () => {
    const now = new Date()
    
    // 요일 설정
    const days = ['일', '월', '화', '수', '목', '금', '토']
    currentDay.value = days[now.getDay()]
    
    // 날짜 설정 (YYYY-MM-DD 형식)
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    currentDate.value = `${year}-${month}-${day}`
    
    // 시간 설정 (HH:MM:SS 형식)
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    const seconds = String(now.getSeconds()).padStart(2, '0')
    currentTime.value = `${hours}:${minutes}:${seconds}`
}

onMounted(() => {
    // 초기 실행
    updateDateTime()
    
    // 1초마다 업데이트
    timer = setInterval(updateDateTime, 1000)
})

onUnmounted(() => {
    // 컴포넌트가 제거될 때 타이머 정리
    if (timer) {
        clearInterval(timer)
    }
})
</script>