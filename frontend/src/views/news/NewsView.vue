<template>
  <div class="mb-4 position-relative p-5 news-header" :class="{ 'dark-header': isDarkMode }">
    <div class="overflow-hidden position-relative">
      <h3 class="text-start fw-bold">{{ accountStore.username }} 님을 위한 뉴스를 준비했어요!</h3>
      <p class="text-start text-muted">{{ persona.description }}</p>

      <div
        class="d-flex transition"
        :class="{ 'disabled-overlay': !personaAvailable }"
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

      <div
        v-if="!personaAvailable"
        class="position-absolute top-50 start-50 translate-middle text-center z-3 survey-notice"
      >
        <p class="mb-3 fw-bold">맞춤 뉴스를 보려면 설문조사를 완료해 주세요</p>
        <router-link to="/survey" class="btn btn-success btn-lg">설문조사 시작하기</router-link>
      </div>

     
    </div>
     <button
        class="btn btn-outline-secondary position-absolute top-50 start-0 translate-middle-y z-2"
        :disabled="!personaAvailable"
        :class="{ 'disabled-button': !personaAvailable }"
        @click="prev"
      >
        <i class="bi bi-chevron-left"></i>
      </button>

      <button
        class="btn btn-outline-secondary position-absolute top-50 end-0 translate-middle-y z-2"
        :disabled="!personaAvailable"
        :class="{ 'disabled-button': !personaAvailable }"
        @click="next"
      >
        <i class="bi bi-chevron-right"></i>
      </button>
  </div>

  <!-- 무한스크롤 뉴스 영역 -->
  <div class="row mt-5">
    <div v-for="(news, index) in loadedNews" :key="news.id || news.title + index" class="col-md-6">
      <SectionNewsTabs
        :imageUrl="news.imageUrl"
        :title="news.title"
        :description="news.description"
        :newsUrl="news.newsUrl"
        :date="news.date"
      />
    </div>

    <!-- 감지 지점 -->
    <div ref="scrollObserver" class="w-100 py-3 text-center">
      <LoadingSpinner v-if="isLoading" />
      <span v-else-if="isEnd">마지막 뉴스입니다.</span>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, onBeforeUnmount, computed } from 'vue'
import { userAccountStore } from '@/store/account'
import axios from '@/plugins/axios'
import NewsCard from '@/components/news/NewsCard.vue'
import SectionNewsTabs from '@/components/news/SectionNewsTab.vue'
import LoadingSpinner from '@/components/loading/LoadingSpinner.vue'

const accountStore = userAccountStore()
const persona = ref({})
const newsList = ref([])
const loadedNews = ref([])

const page = ref(0)
const pageSize = 6
const isLoading = ref(false)
const isEnd = ref(false)
const scrollObserver = ref(null)

// 다크모드 상태 감지
const isDarkMode = computed(() => document.body.classList.contains('dark-mode'))

const personaAvailable = computed(() => accountStore.personaId > 0 && persona.value.description)

const currentIndex = ref(0)
const visibleCount = 4
const slideWidth = 100 / visibleCount
const maxIndex = computed(() => Math.max(0, newsList.value.length - visibleCount))

const next = () => {
  currentIndex.value = currentIndex.value >= maxIndex.value ? 0 : currentIndex.value + 1
}
const prev = () => {
  currentIndex.value = currentIndex.value <= 0 ? maxIndex.value : currentIndex.value - 1
}

let observer

const loadNextPage = () => {
  if (isLoading.value || isEnd.value) return
  isLoading.value = true

  const start = page.value * pageSize
  const end = start + pageSize
  const nextChunk = newsList.value.slice(start, end)

  if (nextChunk.length === 0) {
    setTimeout(() => {
      isEnd.value = true
      isLoading.value = false
    }, 2000)
  } else {
    loadedNews.value.push(...nextChunk)
    page.value++

    setTimeout(() => {
      isLoading.value = false

      if (scrollObserver.value && observer) {
        observer.unobserve(scrollObserver.value)
        observer.observe(scrollObserver.value)
      }
    }, 1000)
  }
}

onMounted(async () => {
  if (accountStore.personaId > 0) {
    try {
      const res = await axios.get(`/api/personas/${accountStore.personaId}`)
      persona.value = res.data.persona
    } catch (err) {
      console.error(err)
    }
  }

  try {
    const res = await axios.get(`/api/news/${accountStore.personaId ?? -1}`)
    newsList.value = res.data
    loadNextPage()
  } catch (err) {
    console.error(err)
  }

  const interval = setInterval(() => {
    if (personaAvailable.value) {
      currentIndex.value = (currentIndex.value + 1) % newsList.value.length
    }
  }, 5000)
  onBeforeUnmount(() => clearInterval(interval))

  observer = new IntersectionObserver(
    (entries) => {
      if (entries[0].isIntersecting && !isLoading.value && !isEnd.value) {
        loadNextPage()
      }
    },
    { threshold: 1.0 },
  )

  if (scrollObserver.value) {
    observer.observe(scrollObserver.value)
  }
})
</script>

<style scoped>
.transition {
  transition: transform 0.5s ease;
}

.disabled-overlay {
  filter: blur(4px);
  pointer-events: none;
  user-select: none;
}

.disabled-button {
  pointer-events: none;
  opacity: 0.4;
}

.news-header {
  background-color: #eaeef3;
  transition: background-color 0.3s ease;
}

.dark-header {
  background-color: #333 !important;
}

.survey-notice {
  background: #FFFFFFD9;
  backdrop-filter: blur(8px);
  padding: 2rem;
  border-radius: 1rem;
  transition: all 0.3s ease;
}

:global(body.dark-mode) .survey-notice {
  background: rgba(42, 42, 42, 0.9);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

/* 다크모드 추가 스타일 */
:global(body.dark-mode) .text-muted {
  color: #9ca3af !important;
}

:global(body.dark-mode) .btn-outline-secondary {
  color: #e0e0e0;
  border-color: #666;
}

:global(body.dark-mode) .btn-outline-secondary:hover:not(:disabled) {
  background-color: #404040;
  border-color: #888;
  color: #fff;
}

:global(body.dark-mode) .btn-outline-secondary:disabled {
  color: #666;
  border-color: #444;
}
</style>
