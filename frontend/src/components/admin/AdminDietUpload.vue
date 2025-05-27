<template>
  <LoadingSpinner v-if="isLoading" />
  <div class="container">
    <!-- 업로드 영역 -->
    <div
      class="drop-area border rounded p-4 mb-3 text-center"
      :class="{ 'bg-light': isDragging }"
      @dragover.prevent="onDragOver"
      @dragleave.prevent="onDragLeave"
      @drop.prevent="onDrop"
    >
      <p class="mb-2">이미지를 드래그 앤 드롭하거나 클릭하여 선택하세요</p>
      <input
        type="file"
        class="form-control"
        accept="image/*"
        @change="handleFile"
        ref="fileInput"
        hidden
      />
      <button class="btn btn-secondary" @click="() => fileInput.click()">파일 선택</button>
    </div>

    <h5 v-if="tableData.length > 0" class="text-danger">
      음식이 아닌 것 및 원산지 표기는 꼭 지워주세요!
    </h5>
    <!-- 이미지 + 테이블 나란히 -->
    <div class="d-flex gap-3" style="width: 100%">
      <!-- 이미지 영역 -->
      <div ref="imageScrollBox" v-if="previewUrl && tableData.length <= 0">
        <img ref="previewImage" :src="previewUrl" class="rounded w-100" />
      </div>
      <div
        ref="imageScrollBox"
        v-else-if="previewUrl && tableData.length > 0"
        style="flex: 1; max-width: 50%; overflow-x: auto; overflow-y: hidden"
        @scroll="syncScroll('image')"
      >
        <img
          ref="previewImage"
          :src="previewUrl"
          class="rounded"
          style="display: block; max-height: 700px; height: auto; width: auto; max-width: none"
          @load="syncTableWidth"
        />
      </div>

      <!-- 테이블 영역 -->
      <div
        v-if="tableData.length > 0"
        :style="{ flex: 1, maxWidth: '50%', overflowX: 'auto', width: tableWidth + 'px' }"
      >
        <div
          ref="tableScrollBox"
          class="table-responsive"
          style="overflow-x: auto"
          @scroll="syncScroll('table')"
        >
          <table
            class="table table-bordered mt-2 text-center align-middle"
            :style="{ width: tableWidth + 'px' }"
          >
            <thead class="table-primary">
              <tr>
                <th v-for="(day, colIndex) in tableData[0]" :key="'head-' + colIndex">
                  <div class="fw-bold fs-6">{{ day }}</div>
                </th>
              </tr>
              <tr>
                <th v-for="(day, colIndex) in tableData[1]" :key="'head2-' + colIndex">
                  <div class="fw-bold fs-6">{{ day }}</div>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, rowIndex) in tableData.slice(2)" :key="'row-' + rowIndex">
                <td v-for="(cell, colIndex) in row" :key="'cell-' + colIndex">
                  <input
                    type="text"
                    v-model="tableData[rowIndex + 2][colIndex]"
                    class="form-control border-0 p-0"
                    style="font-size: 0.85rem"
                  />
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 버튼 -->
    <div class="mt-3">
      <button
        v-if="imageFile && tableData.length == 0"
        class="btn btn-primary"
        @click="sendToServer"
      >
        분석 요청
      </button>
      <button
        v-if="tableData.length"
        type="button"
        class="btn btn-primary ms-2"
        @click="handleUpload"
      >
        업로드
      </button>
    </div>
  </div>
  <ConfirmDialog ref="confirmDialog" />
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axios from '@/plugins/axios'
import ImageCropper from '@/components/admin/ImageCropper.vue'
import ConfirmDialog from '@/components/dialog/ConfirmDialog.vue'
import router from '@/router'
import LoadingSpinner from '@/components/loading/LoadingSpinner.vue'

const result = ref(null)
const imageFile = ref(null)
const previewUrl = ref(null)
const isDragging = ref(false)
const fileInput = ref(null)

const previewImage = ref(null)
const imageScrollBox = ref(null)
const tableScrollBox = ref(null)
const tableWidth = ref(null)

const confirmDialog = ref(null)
const isLoading = ref(false)

let isSyncingScroll = false

function syncTableWidth() {
  if (previewImage.value) {
    tableWidth.value = previewImage.value.clientWidth
  }
}

function syncScroll(source) {
  //console.log(`syncScroll from ${source}`)
  if (isSyncingScroll) return

  isSyncingScroll = true
  const imageBox = imageScrollBox.value
  const tableBox = tableScrollBox.value

  if (!imageBox || !tableBox) return

  if (source === 'image') {
    tableBox.scrollLeft = imageBox.scrollLeft
  } else {
    imageBox.scrollLeft = tableBox.scrollLeft
  }

  // 1프레임 뒤에 동기화 잠금 해제
  requestAnimationFrame(() => {
    isSyncingScroll = false
  })
}

function handleFile(e) {
  const file = e.target.files?.[0]
  if (file && file.type.startsWith('image/')) {
    imageFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

function onDrop(e) {
  const file = e.dataTransfer.files?.[0]
  if (file && file.type.startsWith('image/')) {
    imageFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
  isDragging.value = false
}

function onDragOver() {
  isDragging.value = true
}

function onDragLeave() {
  isDragging.value = false
}

function onCroppedFile(file) {
  imageFile.value = file
}

async function sendToServer() {
  if (!imageFile.value) {
    alert('이미지를 먼저 선택해주세요.')
    return
  }
  isLoading.value = true
  const formData = new FormData()
  formData.append('file', imageFile.value)
  formData.append(
    'message',
    JSON.stringify({
      version: 'V2',
      requestId: 'uuid-' + Date.now(),
      timestamp: Date.now(),
      images: [{ name: imageFile.value.name, format: 'png' }],
      enableTableDetection: true,
    }),
  )

  try {
    // 실제 요청 - 유료니깐 마지막에 쓸것
    const res = await axios.post('/api/clova', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    result.value = res.data
  } catch (err) {
    console.error(err)
    alert('서버 요청 실패')
  }

  isLoading.value = false
  console.log(formData.value)
}

const parsed2D = computed(() => {
  if (!result.value) return []
  const cells = result.value.images?.[0]?.tables?.[0]?.cells || []

  // 최대 행/열 구하기
  const maxRow = Math.max(...cells.map((c) => c.rowIndex), 0)
  const maxCol = Math.max(...cells.map((c) => c.columnIndex), 0)

  // 빈 그리드 생성
  const table = Array.from({ length: maxRow + 1 }, () =>
    Array.from({ length: maxCol + 1 }, () => ''),
  )

  // 각 셀 텍스트 합쳐서 배치
  cells.forEach((cell) => {
    const text = cell.cellTextLines
      .map((line) => line.cellWords.map((w) => w.inferText).join(''))
      .join('\n')
    table[cell.rowIndex][cell.columnIndex] = text
  })
  // 여기서 ( or ) 가 들어간 셀은 빈 문자열로 치환
  return removeColumnsWithKeywords(table)
})

// 편집 가능한 복사본
const tableData = ref([])

// parsed2D가 바뀔 때마다 formData에 복사
watch(
  parsed2D,
  (newTable) => {
    tableData.value = newTable.map((row) => [...row])
  },
  { immediate: true },
)

async function save() {
  const courseLength = Math.floor(tableData.value.length / 2)
  console.log('길이 : ', tableData.value.length)
  const transposed = transpose(tableData.value)

  // for (let i = 0; i < tableData.value.length; i++) {
  //   console.log(tableData.value[i])
  // }

  let post = {
    diet: [],
  }

  for (let i = 0; i < transposed.length; i++) {
    // 단일메뉴
    if (transposed[i][2] == '') {
      let data = {
        date: '',
        course: '',
        food: [],
      }

      data.date = parseKoreanDate(transposed[i][0])
      data.course = 'A'
      for (let j = 2; j < transposed[i].length; j++) {
        if (transposed[i][j] != '') {
          if (transposed[i][j].startsWith('&')) {
            let str = data.food.pop() + transposed[i][j]
            data.food.push(str)
          } else {
            data.food.push(...transposed[i][j].split('*').filter((s) => s !== ''))
          }
        }
      }
      if (data.food.length != 0) {
        post.diet.push(data)
      }
    } else {
      let data = {
        date: '',
        course: '',
        food: [],
      }
      data.date = parseKoreanDate(transposed[i][0])
      data.course = 'A'
      for (let j = 2; j < courseLength; j++) {
        if (transposed[i][j] != '') {
          if (transposed[i][j].startsWith('&')) {
            let str = data.food.pop() + transposed[i][j]
            data.food.push(str)
          } else {
            data.food.push(...transposed[i][j].split('*').filter((s) => s !== ''))
          }
        }
      }
      if (data.food.length != 0) {
        post.diet.push(data)
      }

      // B코스
      data = {
        date: '',
        course: '',
        food: [],
      }
      data.date = parseKoreanDate(transposed[i][0])
      data.course = 'B'

      for (let j = courseLength + 1; j < transposed[i].length; j++) {
        if (transposed[i][j] != '') {
          if (transposed[i][j].startsWith('&')) {
            let str = data.food.pop() + transposed[i][j]
            data.food.push(str)
          } else {
            data.food.push(...transposed[i][j].split('*').filter((s) => s !== ''))
          }
        }
      }
      if (data.food.length != 0) {
        post.diet.push(data)
      }
    }
  }

  for (let i = 0; i < post.diet.length; i++) {
    console.log(post.diet[i])
  }

  console.log(post)

  try {
    const res = await axios.post('/api/foods', post)
    router.push('/admin')
  } catch (err) {
    alert('실패')
  }
}

function transpose(matrix) {
  return matrix[0].map((_, colIndex) => matrix.map((row) => row[colIndex]))
}

function parseKoreanDate(koreanDateStr) {
  const currentYear = new Date().getFullYear() // 예: 2025
  const match = koreanDateStr.match(/(\d+)월(\d+)일/)

  if (!match) return null

  const month = match[1].padStart(2, '0')
  const day = match[2].padStart(2, '0')

  const dateStr = `${currentYear}-${month}-${day}` // 예: 2025-05-20
  return dateStr
}

const keywords = ['구분', '[20F]', '일반식', 'A.한식', 'B.일품']

function removeColumnsWithKeywords(table) {
  if (!table.length) return table

  const numCols = table[0].length
  const colsToRemove = new Set()

  for (let col = 0; col < numCols; col++) {
    for (let row = 0; row < table.length; row++) {
      const cell = table[row][col]
      if (keywords.some((keyword) => cell.includes(keyword))) {
        colsToRemove.add(col)
        break
      }
    }
  }

  // 열 제거
  const cleaned = table.map((row) => row.filter((_, colIndex) => !colsToRemove.has(colIndex)))

  return cleaned
}

async function handleUpload() {
  const ok = await confirmDialog.value.open({
    title: '식단 업로드',
    message: `식단을 업로드하시겠습니까?<br><span class="text-danger small">음식이 아니거나 원산지 표기가 있는지 확인해주세요</span>`,
  })
  if (!ok) return

  save()
}
</script>

<style scoped>
.drop-area {
  border: 2px dashed #ccc;
  transition: background-color 0.3s;
}
</style>
