<template>
  <div class="col-md-6 mb-4">
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>오늘의 식단</span>
        <button class="btn btn-sm btn-outline-success"><i class="bi bi-plus"></i></button>
      </div>
      <div class="card-body d-flex gap-3 flex-wrap">
        <div v-for="dish in todayDishes" :key="dish.name" class="text-center" style="width: 120px">
          <img
            :src="dish.img"
            class="rounded mb-2"
            style="width: 100%; height: 80px; object-fit: cover"
            alt=""
          />
          <div class="small">{{ dish.name }}</div>
          <i class="bi bi-heart ms-1"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const todayDishes = ref([
  { name: '된장찌개 + 순대튀김강정', img: 'https://via.placeholder.com/120x80' },
  { name: '차이니즈파게티 + 마늘토스트', img: 'https://via.placeholder.com/120x80' },
])
</script>
