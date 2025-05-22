<template>
  <div class="col-md-6 mb-4 position-relative">
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>오늘의 뉴스</span>
        <button class="btn btn-sm btn-outline-success"><i class="bi bi-plus"></i></button>
      </div>

      <!-- 슬라이드 영역 -->
      <div class="overflow-hidden position-relative">
        <div
          class="d-flex transition"
          :style="{ transform: `translateX(-${currentIndex * 100}%)` }"
        >
          <div
            v-for="(news, index) in newsList"
            :key="index"
            class="flex-shrink-0 p-5"
            style="width: 100%"
          >
            <NewsCard
              :imageUrl="news.imageUrl"
              :title="news.title"
              :description="news.description"
              :newsUrl="news.newsUrl"
            />
          </div>
        </div>
      </div>

      <!-- 좌우 버튼 -->
      <button
        class="btn btn-outline-secondary position-absolute top-50 start-0 translate-middle-y z-2"
        @click="prev"
      >
        <i class="bi bi-chevron-left"></i>
      </button>
      <button
        class="btn btn-outline-secondary position-absolute top-50 end-0 translate-middle-y z-2"
        @click="next"
      >
        <i class="bi bi-chevron-right"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, onBeforeUnmount } from 'vue'
import { userAccountStore } from '@/store/account'
import axios from '@/plugins/axios'
import NewsCard from '@/components/news/NewsCard.vue'

const accountStore = userAccountStore()
const newsList = ref([])
const currentIndex = ref(0)

const next = () => {
  currentIndex.value = (currentIndex.value + 1) % newsList.value.length
}

const prev = () => {
  currentIndex.value = (currentIndex.value - 1 + newsList.value.length) % newsList.value.length
}

onMounted(async () => {
  const personaId = accountStore.personaId ?? -1
  try {
    const res = await axios.get(`/api/news/${personaId}`)
    newsList.value = res.data
  } catch (err) {
    console.error(err)
  }
})

// 자동 슬라이딩
let interval = null
onMounted(() => {
  interval = setInterval(next, 5000)
})
onBeforeUnmount(() => {
  clearInterval(interval)
})
</script>

<style scoped>
.transition {
  transition: transform 0.5s ease;
}
</style>
