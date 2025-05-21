<template>
  <div class="mb-4 position-relative p-5" style="background-color: #eaeef3">
    <!-- <div class="card-header d-flex justify-content-between align-items-center">
        <span>맞춤 뉴스</span>
        <button class="btn btn-sm btn-outline-success"><i class="bi bi-plus"></i></button>
      </div> -->

    <!-- 슬라이드 영역 -->
    <div class="overflow-hidden position-relative">
      <h3 class="text-start fw-bold">{{ accountStore.username }} 님을 위한 뉴스를 준비했어요!</h3>
      <!-- wrapper -->
      <div
        class="d-flex transition"
        :style="{ transform: `translateX(-${currentIndex * slideWidth}%)` }"
      >
        <div
          v-for="(news, index) in newsList"
          :key="index"
          class="flex-shrink-0 p-2"
          style="width: 25%"
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

  <!-- 섹션별 뉴스 탭: 좌/우 두 컬럼 -->
  <div class="row mt-5">
    <div class="col-md-6" v-for="news in leftList" :key="news.id || news.title">
      <!-- 왼쪽 절반 -->
      <SectionNewsTabs
        :imageUrl="news.imageUrl"
        :title="news.title"
        :description="news.description"
        :newsUrl="news.newsUrl"
        :date="news.date"
        class="mb-4"
      />
    </div>
    <div class="col-md-6" v-for="news in rightList" :key="news.id || news.title">
      <!-- 오른쪽 절반 -->
      <SectionNewsTabs
        :imageUrl="news.imageUrl"
        :title="news.title"
        :description="news.description"
        :newsUrl="news.newsUrl"
        :date="news.date"
        class="mb-4"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, onBeforeUnmount, computed } from 'vue'
import { userAccountStore } from '@/store/account'
import axios from '@/plugins/axios'
import NewsCard from '@/components/news/NewsCard.vue'
import SectionNewsTabs from '@/components/news/SectionNewsTab.vue'

const accountStore = userAccountStore()
const newsList = ref([])
const currentIndex = ref(0)

// 리스트 절반 나누기
const mid = computed(() => Math.ceil(newsList.value.length / 2))
const leftList = computed(() => newsList.value.slice(0, mid.value))
const rightList = computed(() => newsList.value.slice(mid.value))

// 한번에 보여줄 카드 개수
const visibleCount = 4
// 슬라이드 한 칸당 이동할 퍼센트
const slideWidth = 100 / visibleCount

// 최대 인덱스 (맨 끝에서 더 이상 오른쪽으로 넘어가지 않게)
const maxIndex = computed(() => Math.max(0, newsList.value.length - visibleCount))

const next = () => {
  currentIndex.value = currentIndex.value >= maxIndex.value ? 0 : currentIndex.value + 1
}

const prev = () => {
  currentIndex.value = currentIndex.value <= 0 ? maxIndex.value : currentIndex.value - 1
}

onMounted(async () => {
  const personaId = accountStore.personaId ?? -1
  try {
    const res = await axios.get(`/api/news/${personaId}`)
    newsList.value = res.data
  } catch (err) {
    console.error(err)
  }

  // 자동 슬라이드 5초마다
  const interval = setInterval(() => {
    currentIndex.value = (currentIndex.value + 1) % newsList.value.length
  }, 5000)
  onBeforeUnmount(() => clearInterval(interval))
})
</script>

<style scoped>
.transition {
  transition: transform 0.5s ease;
}
</style>
