<template>
  <div class="col-md-6 mb-4">
    <div class="card shadow-sm h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>오늘의 뉴스</span>
        <button class="btn btn-sm btn-outline-success"><i class="bi bi-plus"></i></button>
      </div>
      <img src="https://via.placeholder.com/600x200" class="card-img-top" alt="" />
      <div class="card-body">
        <h5 class="card-title">현미가 암을 더 생기게 한다?</h5>
        <p class="card-text text-muted">[송무호의 비건뉴스] 77. 현미와 비소, 왜 문제가 됐나?</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { userAccountStore } from '@/store/account'
import axios from '@/plugins/axios'

const accountStore = userAccountStore()
onMounted(async () => {
  let personaId = -1
  if (accountStore.personaId !== null) {
    personaId = accountStore.personaId
  }

  console.log(personaId)

  try {
    const res = await axios.get(`/api/news/${personaId}`)
    console.log(res.data)
  } catch (err) {
    console.log(err)
  }
})
</script>
