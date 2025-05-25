<template>
  <div class="similar-recipe-item d-flex align-items-center p-2 border-bottom" @click="goToRecipe">
    <div class="flex-grow-1">
      <h6 class="mb-1">{{ recipe.name }}</h6>
      <div class="small text-muted">
        <span class="me-2"><i class="bi bi-clock"></i> {{ formatCookTime(recipe.cookTimeMinutes) }}</span>
        <span><i class="bi bi-people"></i> {{ recipe.servings }}인분</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  recipe: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const formatCookTime = (minutes) => {
  if (minutes < 60) {
    return `${minutes}분`
  } else {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}시간 ${mins}분` : `${hours}시간`
  }
}

const goToRecipe = () => {
  router.push(`/recipe/${props.recipe.id}`)
}
</script>

<style scoped>
.similar-recipe-item {
  cursor: pointer;
  transition: background-color 0.2s;
}

.similar-recipe-item:hover {
  background-color: #f8f9fa;
}
</style> 