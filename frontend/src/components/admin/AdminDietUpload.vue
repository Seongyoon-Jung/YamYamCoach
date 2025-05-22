<template>
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
      <div
        ref="imageScrollBox"
        v-if="previewUrl"
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
    // const res = await axios.post('/api/clova', formData, {
    //   headers: { 'Content-Type': 'multipart/form-data' },
    // })
    // result.value = res.data

    result.value = {
      version: 'V2',
      requestId: '487bb08b687c4186b8384f7ebff16a27',
      timestamp: 1747741229175,
      images: [
        {
          uid: '26c6f8472dd641439519835ade129398',
          name: 'demo',
          inferResult: 'SUCCESS',
          message: 'SUCCESS',
          validationResult: { result: 'NO_REQUESTED' },
          convertedImageInfo: { width: 3399, height: 1483, pageIndex: 0, longImage: false },
          tables: [
            {
              cells: [
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 42.0, y: 328.0 },
                          { x: 140.0, y: 328.0 },
                          { x: 140.0, y: 376.0 },
                          { x: 42.0, y: 376.0 },
                        ],
                      },
                      inferConfidence: 0.9509,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 42.0, y: 328.0 },
                              { x: 140.0, y: 328.0 },
                              { x: 140.0, y: 376.0 },
                              { x: 42.0, y: 376.0 },
                            ],
                          },
                          inferText: '구분',
                          inferConfidence: 0.9509,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 12.0, y: 252.0 },
                      { x: 169.0, y: 252.0 },
                      { x: 169.0, y: 452.0 },
                      { x: 12.0, y: 452.0 },
                    ],
                  },
                  inferConfidence: 0.9509,
                  rowSpan: 2,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 52.0, y: 923.0 },
                          { x: 140.0, y: 923.0 },
                          { x: 140.0, y: 965.0 },
                          { x: 52.0, y: 965.0 },
                        ],
                      },
                      inferConfidence: 0.9992,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 52.0, y: 923.0 },
                              { x: 140.0, y: 923.0 },
                              { x: 140.0, y: 965.0 },
                              { x: 52.0, y: 965.0 },
                            ],
                          },
                          inferText: '[20F]',
                          inferConfidence: 0.9992,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 12.0, y: 452.0 },
                      { x: 169.0, y: 452.0 },
                      { x: 169.0, y: 966.0 },
                      { x: 12.0, y: 966.0 },
                    ],
                  },
                  inferConfidence: 0.9992,
                  rowSpan: 8,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 42.0, y: 968.0 },
                          { x: 146.0, y: 968.0 },
                          { x: 146.0, y: 1014.0 },
                          { x: 42.0, y: 1014.0 },
                        ],
                      },
                      inferConfidence: 0.9993,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 42.0, y: 968.0 },
                              { x: 146.0, y: 968.0 },
                              { x: 146.0, y: 1014.0 },
                              { x: 42.0, y: 1014.0 },
                            ],
                          },
                          inferText: '일반식',
                          inferConfidence: 0.9993,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 12.0, y: 966.0 },
                      { x: 169.0, y: 966.0 },
                      { x: 169.0, y: 1480.0 },
                      { x: 12.0, y: 1480.0 },
                    ],
                  },
                  inferConfidence: 0.9993,
                  rowSpan: 10,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 252.0 },
                      { x: 416.0, y: 252.0 },
                      { x: 416.0, y: 452.0 },
                      { x: 169.0, y: 452.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 2,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 452.0 },
                      { x: 416.0, y: 452.0 },
                      { x: 416.0, y: 571.0 },
                      { x: 169.0, y: 571.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 571.0 },
                      { x: 416.0, y: 571.0 },
                      { x: 416.0, y: 618.0 },
                      { x: 169.0, y: 618.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 3,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 618.0 },
                      { x: 416.0, y: 618.0 },
                      { x: 416.0, y: 664.0 },
                      { x: 169.0, y: 664.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 4,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 231.0, y: 686.0 },
                          { x: 352.0, y: 686.0 },
                          { x: 352.0, y: 731.0 },
                          { x: 231.0, y: 731.0 },
                        ],
                      },
                      inferConfidence: 0.99875,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 231.0, y: 686.0 },
                              { x: 282.0, y: 686.0 },
                              { x: 282.0, y: 731.0 },
                              { x: 231.0, y: 731.0 },
                            ],
                          },
                          inferText: 'A.',
                          inferConfidence: 0.9997,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 282.0, y: 686.0 },
                              { x: 352.0, y: 686.0 },
                              { x: 352.0, y: 731.0 },
                              { x: 282.0, y: 731.0 },
                            ],
                          },
                          inferText: '한식',
                          inferConfidence: 0.9978,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 664.0 },
                      { x: 416.0, y: 664.0 },
                      { x: 416.0, y: 711.0 },
                      { x: 169.0, y: 711.0 },
                    ],
                  },
                  inferConfidence: 0.99875,
                  rowSpan: 1,
                  rowIndex: 5,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 711.0 },
                      { x: 416.0, y: 711.0 },
                      { x: 416.0, y: 758.0 },
                      { x: 169.0, y: 758.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 6,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 758.0 },
                      { x: 416.0, y: 758.0 },
                      { x: 416.0, y: 805.0 },
                      { x: 169.0, y: 805.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 7,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 805.0 },
                      { x: 416.0, y: 805.0 },
                      { x: 416.0, y: 851.0 },
                      { x: 169.0, y: 851.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 8,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 851.0 },
                      { x: 416.0, y: 851.0 },
                      { x: 416.0, y: 966.0 },
                      { x: 169.0, y: 966.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 966.0 },
                      { x: 416.0, y: 966.0 },
                      { x: 416.0, y: 1034.0 },
                      { x: 169.0, y: 1034.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1034.0 },
                      { x: 416.0, y: 1034.0 },
                      { x: 416.0, y: 1085.0 },
                      { x: 169.0, y: 1085.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 11,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1085.0 },
                      { x: 416.0, y: 1085.0 },
                      { x: 416.0, y: 1132.0 },
                      { x: 169.0, y: 1132.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 12,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1132.0 },
                      { x: 416.0, y: 1132.0 },
                      { x: 416.0, y: 1183.0 },
                      { x: 169.0, y: 1183.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 13,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1183.0 },
                      { x: 416.0, y: 1183.0 },
                      { x: 416.0, y: 1225.0 },
                      { x: 169.0, y: 1225.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 14,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 240.0, y: 1205.0 },
                          { x: 352.0, y: 1205.0 },
                          { x: 352.0, y: 1247.0 },
                          { x: 240.0, y: 1247.0 },
                        ],
                      },
                      inferConfidence: 0.9986,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 240.0, y: 1205.0 },
                              { x: 352.0, y: 1205.0 },
                              { x: 352.0, y: 1247.0 },
                              { x: 240.0, y: 1247.0 },
                            ],
                          },
                          inferText: 'B. 일품',
                          inferConfidence: 0.9986,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1225.0 },
                      { x: 416.0, y: 1225.0 },
                      { x: 416.0, y: 1272.0 },
                      { x: 169.0, y: 1272.0 },
                    ],
                  },
                  inferConfidence: 0.9986,
                  rowSpan: 1,
                  rowIndex: 15,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1272.0 },
                      { x: 416.0, y: 1272.0 },
                      { x: 416.0, y: 1323.0 },
                      { x: 169.0, y: 1323.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 16,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1323.0 },
                      { x: 416.0, y: 1323.0 },
                      { x: 416.0, y: 1370.0 },
                      { x: 169.0, y: 1370.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 17,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1370.0 },
                      { x: 416.0, y: 1370.0 },
                      { x: 416.0, y: 1416.0 },
                      { x: 169.0, y: 1416.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 169.0, y: 1416.0 },
                      { x: 416.0, y: 1416.0 },
                      { x: 416.0, y: 1480.0 },
                      { x: 169.0, y: 1480.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 628.0, y: 288.0 },
                          { x: 798.0, y: 288.0 },
                          { x: 798.0, y: 340.0 },
                          { x: 628.0, y: 340.0 },
                        ],
                      },
                      inferConfidence: 0.99945,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 628.0, y: 291.0 },
                              { x: 719.0, y: 291.0 },
                              { x: 719.0, y: 340.0 },
                              { x: 628.0, y: 340.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9992,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 713.0, y: 288.0 },
                              { x: 798.0, y: 288.0 },
                              { x: 798.0, y: 340.0 },
                              { x: 713.0, y: 340.0 },
                            ],
                          },
                          inferText: '19일',
                          inferConfidence: 0.9997,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 252.0 },
                      { x: 1011.0, y: 252.0 },
                      { x: 1011.0, y: 376.0 },
                      { x: 416.0, y: 376.0 },
                    ],
                  },
                  inferConfidence: 0.99945,
                  rowSpan: 1,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 668.0, y: 395.0 },
                          { x: 762.0, y: 395.0 },
                          { x: 762.0, y: 434.0 },
                          { x: 668.0, y: 434.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 668.0, y: 395.0 },
                              { x: 762.0, y: 395.0 },
                              { x: 762.0, y: 434.0 },
                              { x: 668.0, y: 434.0 },
                            ],
                          },
                          inferText: 'MON',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 376.0 },
                      { x: 1011.0, y: 376.0 },
                      { x: 1011.0, y: 452.0 },
                      { x: 416.0, y: 452.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 662.0, y: 525.0 },
                          { x: 765.0, y: 525.0 },
                          { x: 765.0, y: 568.0 },
                          { x: 662.0, y: 568.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 662.0, y: 525.0 },
                              { x: 765.0, y: 525.0 },
                              { x: 765.0, y: 568.0 },
                              { x: 662.0, y: 568.0 },
                            ],
                          },
                          inferText: '비빔밥',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 452.0 },
                      { x: 1011.0, y: 452.0 },
                      { x: 1011.0, y: 571.0 },
                      { x: 416.0, y: 571.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 604.0, y: 568.0 },
                          { x: 822.0, y: 568.0 },
                          { x: 822.0, y: 619.0 },
                          { x: 604.0, y: 619.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 604.0, y: 568.0 },
                              { x: 822.0, y: 568.0 },
                              { x: 822.0, y: 619.0 },
                              { x: 604.0, y: 619.0 },
                            ],
                          },
                          inferText: '(우육:호주산)',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 571.0 },
                      { x: 1011.0, y: 571.0 },
                      { x: 1011.0, y: 618.0 },
                      { x: 416.0, y: 618.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 3,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 631.0, y: 619.0 },
                          { x: 798.0, y: 619.0 },
                          { x: 798.0, y: 662.0 },
                          { x: 631.0, y: 662.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 631.0, y: 619.0 },
                              { x: 798.0, y: 619.0 },
                              { x: 798.0, y: 662.0 },
                              { x: 631.0, y: 662.0 },
                            ],
                          },
                          inferText: '가다랭이국',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 618.0 },
                      { x: 1011.0, y: 618.0 },
                      { x: 1011.0, y: 664.0 },
                      { x: 416.0, y: 664.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 4,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 607.0, y: 662.0 },
                          { x: 819.0, y: 662.0 },
                          { x: 819.0, y: 713.0 },
                          { x: 607.0, y: 713.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 607.0, y: 662.0 },
                              { x: 819.0, y: 662.0 },
                              { x: 819.0, y: 713.0 },
                              { x: 607.0, y: 713.0 },
                            ],
                          },
                          inferText: '도토리묵무침',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 664.0 },
                      { x: 1011.0, y: 664.0 },
                      { x: 1011.0, y: 711.0 },
                      { x: 416.0, y: 711.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 5,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 595.0, y: 710.0 },
                          { x: 829.0, y: 710.0 },
                          { x: 829.0, y: 756.0 },
                          { x: 595.0, y: 756.0 },
                        ],
                      },
                      inferConfidence: 0.9972,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 595.0, y: 710.0 },
                              { x: 829.0, y: 710.0 },
                              { x: 829.0, y: 756.0 },
                              { x: 595.0, y: 756.0 },
                            ],
                          },
                          inferText: '시래기된장지짐',
                          inferConfidence: 0.9972,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 711.0 },
                      { x: 1011.0, y: 711.0 },
                      { x: 1011.0, y: 758.0 },
                      { x: 416.0, y: 758.0 },
                    ],
                  },
                  inferConfidence: 0.9972,
                  rowSpan: 1,
                  rowIndex: 6,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 625.0, y: 756.0 },
                          { x: 804.0, y: 756.0 },
                          { x: 804.0, y: 807.0 },
                          { x: 625.0, y: 807.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 625.0, y: 756.0 },
                              { x: 804.0, y: 756.0 },
                              { x: 804.0, y: 807.0 },
                              { x: 625.0, y: 807.0 },
                            ],
                          },
                          inferText: '다시마튀각',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 758.0 },
                      { x: 1011.0, y: 758.0 },
                      { x: 1011.0, y: 805.0 },
                      { x: 416.0, y: 805.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 7,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 671.0, y: 798.0 },
                          { x: 753.0, y: 798.0 },
                          { x: 753.0, y: 853.0 },
                          { x: 671.0, y: 853.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 671.0, y: 798.0 },
                              { x: 753.0, y: 798.0 },
                              { x: 753.0, y: 853.0 },
                              { x: 671.0, y: 853.0 },
                            ],
                          },
                          inferText: '김치',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 805.0 },
                      { x: 1011.0, y: 805.0 },
                      { x: 1011.0, y: 851.0 },
                      { x: 416.0, y: 851.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 8,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 628.0, y: 850.0 },
                          { x: 798.0, y: 850.0 },
                          { x: 798.0, y: 895.0 },
                          { x: 628.0, y: 895.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 628.0, y: 850.0 },
                              { x: 798.0, y: 850.0 },
                              { x: 798.0, y: 895.0 },
                              { x: 628.0, y: 895.0 },
                            ],
                          },
                          inferText: '*ICE초코*',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 851.0 },
                      { x: 1011.0, y: 851.0 },
                      { x: 1011.0, y: 966.0 },
                      { x: 416.0, y: 966.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 966.0 },
                      { x: 1011.0, y: 966.0 },
                      { x: 1011.0, y: 1034.0 },
                      { x: 416.0, y: 1034.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 613.0, y: 1041.0 },
                          { x: 816.0, y: 1041.0 },
                          { x: 816.0, y: 1083.0 },
                          { x: 613.0, y: 1083.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 613.0, y: 1041.0 },
                              { x: 816.0, y: 1041.0 },
                              { x: 816.0, y: 1083.0 },
                              { x: 613.0, y: 1083.0 },
                            ],
                          },
                          inferText: '해물볶음우동',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1034.0 },
                      { x: 1011.0, y: 1034.0 },
                      { x: 1011.0, y: 1085.0 },
                      { x: 416.0, y: 1085.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 11,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 589.0, y: 1083.0 },
                          { x: 838.0, y: 1083.0 },
                          { x: 838.0, y: 1135.0 },
                          { x: 589.0, y: 1135.0 },
                        ],
                      },
                      inferConfidence: 0.9992,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 589.0, y: 1083.0 },
                              { x: 838.0, y: 1083.0 },
                              { x: 838.0, y: 1135.0 },
                              { x: 589.0, y: 1135.0 },
                            ],
                          },
                          inferText: '(오징어:원양산)',
                          inferConfidence: 0.9992,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1085.0 },
                      { x: 1011.0, y: 1085.0 },
                      { x: 1011.0, y: 1132.0 },
                      { x: 416.0, y: 1132.0 },
                    ],
                  },
                  inferConfidence: 0.9992,
                  rowSpan: 1,
                  rowIndex: 12,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 601.0, y: 1135.0 },
                          { x: 825.0, y: 1135.0 },
                          { x: 825.0, y: 1178.0 },
                          { x: 601.0, y: 1178.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 601.0, y: 1135.0 },
                              { x: 825.0, y: 1135.0 },
                              { x: 825.0, y: 1178.0 },
                              { x: 601.0, y: 1178.0 },
                            ],
                          },
                          inferText: '쌀밥&후리가케',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1132.0 },
                      { x: 1011.0, y: 1132.0 },
                      { x: 1011.0, y: 1183.0 },
                      { x: 416.0, y: 1183.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 13,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 625.0, y: 1178.0 },
                          { x: 804.0, y: 1178.0 },
                          { x: 804.0, y: 1229.0 },
                          { x: 625.0, y: 1229.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 625.0, y: 1178.0 },
                              { x: 804.0, y: 1178.0 },
                              { x: 804.0, y: 1229.0 },
                              { x: 625.0, y: 1229.0 },
                            ],
                          },
                          inferText: '가다랭이국',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1183.0 },
                      { x: 1011.0, y: 1183.0 },
                      { x: 1011.0, y: 1225.0 },
                      { x: 416.0, y: 1225.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 14,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 628.0, y: 1229.0 },
                          { x: 798.0, y: 1229.0 },
                          { x: 798.0, y: 1272.0 },
                          { x: 628.0, y: 1272.0 },
                        ],
                      },
                      inferConfidence: 0.9993,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 628.0, y: 1229.0 },
                              { x: 798.0, y: 1229.0 },
                              { x: 798.0, y: 1272.0 },
                              { x: 628.0, y: 1272.0 },
                            ],
                          },
                          inferText: '옥수수빠스',
                          inferConfidence: 0.9993,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1225.0 },
                      { x: 1011.0, y: 1225.0 },
                      { x: 1011.0, y: 1272.0 },
                      { x: 416.0, y: 1272.0 },
                    ],
                  },
                  inferConfidence: 0.9993,
                  rowSpan: 1,
                  rowIndex: 15,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 595.0, y: 1272.0 },
                          { x: 832.0, y: 1272.0 },
                          { x: 832.0, y: 1323.0 },
                          { x: 595.0, y: 1323.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 595.0, y: 1272.0 },
                              { x: 832.0, y: 1272.0 },
                              { x: 832.0, y: 1323.0 },
                              { x: 595.0, y: 1323.0 },
                            ],
                          },
                          inferText: '샐러드&드레싱',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1272.0 },
                      { x: 1011.0, y: 1272.0 },
                      { x: 1011.0, y: 1323.0 },
                      { x: 416.0, y: 1323.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 16,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 662.0, y: 1323.0 },
                          { x: 765.0, y: 1323.0 },
                          { x: 765.0, y: 1366.0 },
                          { x: 662.0, y: 1366.0 },
                        ],
                      },
                      inferConfidence: 0.9952,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 662.0, y: 1323.0 },
                              { x: 765.0, y: 1323.0 },
                              { x: 765.0, y: 1366.0 },
                              { x: 662.0, y: 1366.0 },
                            ],
                          },
                          inferText: '단무지',
                          inferConfidence: 0.9952,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1323.0 },
                      { x: 1011.0, y: 1323.0 },
                      { x: 1011.0, y: 1370.0 },
                      { x: 416.0, y: 1370.0 },
                    ],
                  },
                  inferConfidence: 0.9952,
                  rowSpan: 1,
                  rowIndex: 17,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 628.0, y: 1366.0 },
                          { x: 798.0, y: 1366.0 },
                          { x: 798.0, y: 1411.0 },
                          { x: 628.0, y: 1411.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 628.0, y: 1366.0 },
                              { x: 798.0, y: 1366.0 },
                              { x: 798.0, y: 1411.0 },
                              { x: 628.0, y: 1411.0 },
                            ],
                          },
                          inferText: '*ICE초코*',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1370.0 },
                      { x: 1011.0, y: 1370.0 },
                      { x: 1011.0, y: 1416.0 },
                      { x: 416.0, y: 1416.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 416.0, y: 1416.0 },
                      { x: 1011.0, y: 1416.0 },
                      { x: 1011.0, y: 1480.0 },
                      { x: 416.0, y: 1480.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1220.0, y: 291.0 },
                          { x: 1393.0, y: 291.0 },
                          { x: 1393.0, y: 340.0 },
                          { x: 1220.0, y: 340.0 },
                        ],
                      },
                      inferConfidence: 0.99815,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1220.0, y: 291.0 },
                              { x: 1308.0, y: 291.0 },
                              { x: 1308.0, y: 337.0 },
                              { x: 1220.0, y: 337.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9987,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1305.0, y: 291.0 },
                              { x: 1393.0, y: 291.0 },
                              { x: 1393.0, y: 340.0 },
                              { x: 1305.0, y: 340.0 },
                            ],
                          },
                          inferText: '20일',
                          inferConfidence: 0.9976,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 252.0 },
                      { x: 1606.0, y: 252.0 },
                      { x: 1606.0, y: 376.0 },
                      { x: 1011.0, y: 376.0 },
                    ],
                  },
                  inferConfidence: 0.99815,
                  rowSpan: 1,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1272.0, y: 395.0 },
                          { x: 1344.0, y: 395.0 },
                          { x: 1344.0, y: 434.0 },
                          { x: 1272.0, y: 434.0 },
                        ],
                      },
                      inferConfidence: 0.9997,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1272.0, y: 395.0 },
                              { x: 1344.0, y: 395.0 },
                              { x: 1344.0, y: 434.0 },
                              { x: 1272.0, y: 434.0 },
                            ],
                          },
                          inferText: 'TUE',
                          inferConfidence: 0.9997,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 376.0 },
                      { x: 1606.0, y: 376.0 },
                      { x: 1606.0, y: 452.0 },
                      { x: 1011.0, y: 452.0 },
                    ],
                  },
                  inferConfidence: 0.9997,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1235.0, y: 522.0 },
                          { x: 1372.0, y: 522.0 },
                          { x: 1372.0, y: 568.0 },
                          { x: 1235.0, y: 568.0 },
                        ],
                      },
                      inferConfidence: 0.9988,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1235.0, y: 522.0 },
                              { x: 1372.0, y: 522.0 },
                              { x: 1372.0, y: 568.0 },
                              { x: 1235.0, y: 568.0 },
                            ],
                          },
                          inferText: '닭매운찜',
                          inferConfidence: 0.9988,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 452.0 },
                      { x: 1606.0, y: 452.0 },
                      { x: 1606.0, y: 571.0 },
                      { x: 1011.0, y: 571.0 },
                    ],
                  },
                  inferConfidence: 0.9988,
                  rowSpan: 1,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1181.0, y: 568.0 },
                          { x: 1432.0, y: 568.0 },
                          { x: 1432.0, y: 619.0 },
                          { x: 1181.0, y: 619.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1181.0, y: 568.0 },
                              { x: 1432.0, y: 568.0 },
                              { x: 1432.0, y: 619.0 },
                              { x: 1181.0, y: 619.0 },
                            ],
                          },
                          inferText: '(계육:브라질산)',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 571.0 },
                      { x: 1606.0, y: 571.0 },
                      { x: 1606.0, y: 618.0 },
                      { x: 1011.0, y: 618.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 3,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1214.0, y: 619.0 },
                          { x: 1399.0, y: 619.0 },
                          { x: 1399.0, y: 662.0 },
                          { x: 1214.0, y: 662.0 },
                        ],
                      },
                      inferConfidence: 0.9995,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1214.0, y: 619.0 },
                              { x: 1399.0, y: 619.0 },
                              { x: 1399.0, y: 662.0 },
                              { x: 1214.0, y: 662.0 },
                            ],
                          },
                          inferText: '차조밥/쌀밥',
                          inferConfidence: 0.9995,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 618.0 },
                      { x: 1606.0, y: 618.0 },
                      { x: 1606.0, y: 664.0 },
                      { x: 1011.0, y: 664.0 },
                    ],
                  },
                  inferConfidence: 0.9995,
                  rowSpan: 1,
                  rowIndex: 4,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1235.0, y: 662.0 },
                          { x: 1381.0, y: 662.0 },
                          { x: 1381.0, y: 713.0 },
                          { x: 1235.0, y: 713.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1235.0, y: 662.0 },
                              { x: 1381.0, y: 662.0 },
                              { x: 1381.0, y: 713.0 },
                              { x: 1235.0, y: 713.0 },
                            ],
                          },
                          inferText: '멸치육수',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 664.0 },
                      { x: 1606.0, y: 664.0 },
                      { x: 1606.0, y: 711.0 },
                      { x: 1011.0, y: 711.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 5,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1254.0, y: 712.0 },
                          { x: 1358.0, y: 717.0 },
                          { x: 1356.0, y: 759.0 },
                          { x: 1252.0, y: 754.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1254.0, y: 712.0 },
                              { x: 1358.0, y: 717.0 },
                              { x: 1356.0, y: 758.0 },
                              { x: 1252.0, y: 754.0 },
                            ],
                          },
                          inferText: '부추전',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 711.0 },
                      { x: 1606.0, y: 711.0 },
                      { x: 1606.0, y: 758.0 },
                      { x: 1011.0, y: 758.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 6,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1217.0, y: 756.0 },
                          { x: 1396.0, y: 756.0 },
                          { x: 1396.0, y: 807.0 },
                          { x: 1217.0, y: 807.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1217.0, y: 756.0 },
                              { x: 1396.0, y: 756.0 },
                              { x: 1396.0, y: 807.0 },
                              { x: 1217.0, y: 807.0 },
                            ],
                          },
                          inferText: '건파래볶음',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 758.0 },
                      { x: 1606.0, y: 758.0 },
                      { x: 1606.0, y: 805.0 },
                      { x: 1011.0, y: 805.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 7,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1266.0, y: 801.0 },
                          { x: 1347.0, y: 801.0 },
                          { x: 1347.0, y: 853.0 },
                          { x: 1266.0, y: 853.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1266.0, y: 801.0 },
                              { x: 1347.0, y: 801.0 },
                              { x: 1347.0, y: 853.0 },
                              { x: 1266.0, y: 853.0 },
                            ],
                          },
                          inferText: '김치',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 805.0 },
                      { x: 1606.0, y: 805.0 },
                      { x: 1606.0, y: 851.0 },
                      { x: 1011.0, y: 851.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 8,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1165.0, y: 850.0 },
                          { x: 1448.0, y: 850.0 },
                          { x: 1448.0, y: 901.0 },
                          { x: 1165.0, y: 901.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1165.0, y: 850.0 },
                              { x: 1448.0, y: 850.0 },
                              { x: 1448.0, y: 901.0 },
                              { x: 1165.0, y: 901.0 },
                            ],
                          },
                          inferText: '*아이스티(레몬)*',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 851.0 },
                      { x: 1606.0, y: 851.0 },
                      { x: 1606.0, y: 966.0 },
                      { x: 1011.0, y: 966.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 966.0 },
                      { x: 1606.0, y: 966.0 },
                      { x: 1606.0, y: 1034.0 },
                      { x: 1011.0, y: 1034.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1220.0, y: 1041.0 },
                          { x: 1393.0, y: 1041.0 },
                          { x: 1393.0, y: 1083.0 },
                          { x: 1220.0, y: 1083.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1220.0, y: 1041.0 },
                              { x: 1393.0, y: 1041.0 },
                              { x: 1393.0, y: 1083.0 },
                              { x: 1220.0, y: 1083.0 },
                            ],
                          },
                          inferText: '양지쌀국수',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1034.0 },
                      { x: 1606.0, y: 1034.0 },
                      { x: 1606.0, y: 1085.0 },
                      { x: 1011.0, y: 1085.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 11,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1108.0, y: 1083.0 },
                          { x: 1505.0, y: 1083.0 },
                          { x: 1505.0, y: 1135.0 },
                          { x: 1108.0, y: 1135.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1108.0, y: 1083.0 },
                              { x: 1505.0, y: 1083.0 },
                              { x: 1505.0, y: 1135.0 },
                              { x: 1108.0, y: 1135.0 },
                            ],
                          },
                          inferText: '(소고기편육-우육:외국산)',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1085.0 },
                      { x: 1606.0, y: 1085.0 },
                      { x: 1606.0, y: 1132.0 },
                      { x: 1011.0, y: 1132.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 12,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1159.0, y: 1135.0 },
                          { x: 1457.0, y: 1135.0 },
                          { x: 1457.0, y: 1178.0 },
                          { x: 1159.0, y: 1178.0 },
                        ],
                      },
                      inferConfidence: 0.9979,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1159.0, y: 1135.0 },
                              { x: 1457.0, y: 1135.0 },
                              { x: 1457.0, y: 1178.0 },
                              { x: 1159.0, y: 1178.0 },
                            ],
                          },
                          inferText: '미니파인애플볶음밥',
                          inferConfidence: 0.9979,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1132.0 },
                      { x: 1606.0, y: 1132.0 },
                      { x: 1606.0, y: 1183.0 },
                      { x: 1011.0, y: 1183.0 },
                    ],
                  },
                  inferConfidence: 0.9979,
                  rowSpan: 1,
                  rowIndex: 13,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1217.0, y: 1178.0 },
                          { x: 1396.0, y: 1178.0 },
                          { x: 1396.0, y: 1229.0 },
                          { x: 1217.0, y: 1229.0 },
                        ],
                      },
                      inferConfidence: 0.9981,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1217.0, y: 1178.0 },
                              { x: 1396.0, y: 1178.0 },
                              { x: 1396.0, y: 1229.0 },
                              { x: 1217.0, y: 1229.0 },
                            ],
                          },
                          inferText: '달콤팥춘권',
                          inferConfidence: 0.9981,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1183.0 },
                      { x: 1606.0, y: 1183.0 },
                      { x: 1606.0, y: 1225.0 },
                      { x: 1011.0, y: 1225.0 },
                    ],
                  },
                  inferConfidence: 0.9981,
                  rowSpan: 1,
                  rowIndex: 14,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1238.0, y: 1229.0 },
                          { x: 1375.0, y: 1229.0 },
                          { x: 1375.0, y: 1272.0 },
                          { x: 1238.0, y: 1272.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1238.0, y: 1229.0 },
                              { x: 1375.0, y: 1229.0 },
                              { x: 1375.0, y: 1272.0 },
                              { x: 1238.0, y: 1272.0 },
                            ],
                          },
                          inferText: '알새우칩',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1225.0 },
                      { x: 1606.0, y: 1225.0 },
                      { x: 1606.0, y: 1272.0 },
                      { x: 1011.0, y: 1272.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 15,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1190.0, y: 1272.0 },
                          { x: 1423.0, y: 1272.0 },
                          { x: 1423.0, y: 1323.0 },
                          { x: 1190.0, y: 1323.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1190.0, y: 1272.0 },
                              { x: 1423.0, y: 1272.0 },
                              { x: 1423.0, y: 1323.0 },
                              { x: 1190.0, y: 1323.0 },
                            ],
                          },
                          inferText: '샐러드&드레싱',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1272.0 },
                      { x: 1606.0, y: 1272.0 },
                      { x: 1606.0, y: 1323.0 },
                      { x: 1011.0, y: 1323.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 16,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1223.0, y: 1323.0 },
                          { x: 1390.0, y: 1323.0 },
                          { x: 1390.0, y: 1366.0 },
                          { x: 1223.0, y: 1366.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1223.0, y: 1323.0 },
                              { x: 1390.0, y: 1323.0 },
                              { x: 1390.0, y: 1366.0 },
                              { x: 1223.0, y: 1366.0 },
                            ],
                          },
                          inferText: '사각단무지',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1323.0 },
                      { x: 1606.0, y: 1323.0 },
                      { x: 1606.0, y: 1370.0 },
                      { x: 1011.0, y: 1370.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 17,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1171.0, y: 1369.0 },
                          { x: 1439.0, y: 1369.0 },
                          { x: 1439.0, y: 1411.0 },
                          { x: 1171.0, y: 1411.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1171.0, y: 1369.0 },
                              { x: 1439.0, y: 1369.0 },
                              { x: 1439.0, y: 1411.0 },
                              { x: 1171.0, y: 1411.0 },
                            ],
                          },
                          inferText: '*아이스티(레몬)*',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1370.0 },
                      { x: 1606.0, y: 1370.0 },
                      { x: 1606.0, y: 1416.0 },
                      { x: 1011.0, y: 1416.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1011.0, y: 1416.0 },
                      { x: 1606.0, y: 1416.0 },
                      { x: 1606.0, y: 1480.0 },
                      { x: 1011.0, y: 1480.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1818.0, y: 288.0 },
                          { x: 1982.0, y: 288.0 },
                          { x: 1982.0, y: 340.0 },
                          { x: 1818.0, y: 340.0 },
                        ],
                      },
                      inferConfidence: 0.99895,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1818.0, y: 288.0 },
                              { x: 1909.0, y: 288.0 },
                              { x: 1909.0, y: 340.0 },
                              { x: 1818.0, y: 340.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9984,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1900.0, y: 288.0 },
                              { x: 1982.0, y: 288.0 },
                              { x: 1982.0, y: 340.0 },
                              { x: 1900.0, y: 340.0 },
                            ],
                          },
                          inferText: '21일',
                          inferConfidence: 0.9995,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 252.0 },
                      { x: 2196.0, y: 252.0 },
                      { x: 2196.0, y: 376.0 },
                      { x: 1606.0, y: 376.0 },
                    ],
                  },
                  inferConfidence: 0.99895,
                  rowSpan: 1,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1857.0, y: 395.0 },
                          { x: 1942.0, y: 395.0 },
                          { x: 1942.0, y: 434.0 },
                          { x: 1857.0, y: 434.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1857.0, y: 395.0 },
                              { x: 1942.0, y: 395.0 },
                              { x: 1942.0, y: 434.0 },
                              { x: 1857.0, y: 434.0 },
                            ],
                          },
                          inferText: 'WED',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 376.0 },
                      { x: 2196.0, y: 376.0 },
                      { x: 2196.0, y: 452.0 },
                      { x: 1606.0, y: 452.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1830.0, y: 522.0 },
                          { x: 1967.0, y: 522.0 },
                          { x: 1967.0, y: 568.0 },
                          { x: 1830.0, y: 568.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1830.0, y: 522.0 },
                              { x: 1967.0, y: 522.0 },
                              { x: 1967.0, y: 568.0 },
                              { x: 1830.0, y: 568.0 },
                            ],
                          },
                          inferText: '비지찌개',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 452.0 },
                      { x: 2196.0, y: 452.0 },
                      { x: 2196.0, y: 571.0 },
                      { x: 1606.0, y: 571.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1800.0, y: 568.0 },
                          { x: 2000.0, y: 568.0 },
                          { x: 2000.0, y: 619.0 },
                          { x: 1800.0, y: 619.0 },
                        ],
                      },
                      inferConfidence: 0.9994,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1800.0, y: 568.0 },
                              { x: 2000.0, y: 568.0 },
                              { x: 2000.0, y: 619.0 },
                              { x: 1800.0, y: 619.0 },
                            ],
                          },
                          inferText: '보리밥/쌀밥',
                          inferConfidence: 0.9994,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 571.0 },
                      { x: 2196.0, y: 571.0 },
                      { x: 2196.0, y: 618.0 },
                      { x: 1606.0, y: 618.0 },
                    ],
                  },
                  inferConfidence: 0.9994,
                  rowSpan: 1,
                  rowIndex: 3,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1754.0, y: 619.0 },
                          { x: 2045.0, y: 619.0 },
                          { x: 2045.0, y: 662.0 },
                          { x: 1754.0, y: 662.0 },
                        ],
                      },
                      inferConfidence: 0.9989,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1754.0, y: 619.0 },
                              { x: 2045.0, y: 619.0 },
                              { x: 2045.0, y: 662.0 },
                              { x: 1754.0, y: 662.0 },
                            ],
                          },
                          inferText: '치킨너겟&머스타드',
                          inferConfidence: 0.9989,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 618.0 },
                      { x: 2196.0, y: 618.0 },
                      { x: 2196.0, y: 664.0 },
                      { x: 1606.0, y: 664.0 },
                    ],
                  },
                  inferConfidence: 0.9989,
                  rowSpan: 1,
                  rowIndex: 4,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1724.0, y: 668.0 },
                          { x: 2076.0, y: 668.0 },
                          { x: 2076.0, y: 710.0 },
                          { x: 1724.0, y: 710.0 },
                        ],
                      },
                      inferConfidence: 0.9996,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1724.0, y: 668.0 },
                              { x: 2076.0, y: 668.0 },
                              { x: 2076.0, y: 710.0 },
                              { x: 1724.0, y: 710.0 },
                            ],
                          },
                          inferText: '(치킨너겟-계육:국내산)',
                          inferConfidence: 0.9996,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 664.0 },
                      { x: 2196.0, y: 664.0 },
                      { x: 2196.0, y: 711.0 },
                      { x: 1606.0, y: 711.0 },
                    ],
                  },
                  inferConfidence: 0.9996,
                  rowSpan: 1,
                  rowIndex: 5,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1754.0, y: 713.0 },
                          { x: 2049.0, y: 713.0 },
                          { x: 2049.0, y: 756.0 },
                          { x: 1754.0, y: 756.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1754.0, y: 713.0 },
                              { x: 2049.0, y: 713.0 },
                              { x: 2049.0, y: 756.0 },
                              { x: 1754.0, y: 756.0 },
                            ],
                          },
                          inferText: '분홍소시지전&케찹',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 711.0 },
                      { x: 2196.0, y: 711.0 },
                      { x: 2196.0, y: 758.0 },
                      { x: 1606.0, y: 758.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 6,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1690.0, y: 759.0 },
                          { x: 2109.0, y: 759.0 },
                          { x: 2109.0, y: 801.0 },
                          { x: 1690.0, y: 801.0 },
                        ],
                      },
                      inferConfidence: 0.9925,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1690.0, y: 759.0 },
                              { x: 2109.0, y: 759.0 },
                              { x: 2109.0, y: 801.0 },
                              { x: 1690.0, y: 801.0 },
                            ],
                          },
                          inferText: '(참피온소시지-돈육:국내산)',
                          inferConfidence: 0.9925,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 758.0 },
                      { x: 2196.0, y: 758.0 },
                      { x: 2196.0, y: 805.0 },
                      { x: 1606.0, y: 805.0 },
                    ],
                  },
                  inferConfidence: 0.9925,
                  rowSpan: 1,
                  rowIndex: 7,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1727.0, y: 807.0 },
                          { x: 2070.0, y: 807.0 },
                          { x: 2070.0, y: 850.0 },
                          { x: 1727.0, y: 850.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1727.0, y: 807.0 },
                              { x: 2070.0, y: 807.0 },
                              { x: 2070.0, y: 850.0 },
                              { x: 1727.0, y: 850.0 },
                            ],
                          },
                          inferText: '매콤콩나물잡채/깍두기',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 805.0 },
                      { x: 2196.0, y: 805.0 },
                      { x: 2196.0, y: 851.0 },
                      { x: 1606.0, y: 851.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 8,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1806.0, y: 850.0 },
                          { x: 1994.0, y: 850.0 },
                          { x: 1994.0, y: 901.0 },
                          { x: 1806.0, y: 901.0 },
                        ],
                      },
                      inferConfidence: 0.9997,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1806.0, y: 850.0 },
                              { x: 1994.0, y: 850.0 },
                              { x: 1994.0, y: 901.0 },
                              { x: 1806.0, y: 901.0 },
                            ],
                          },
                          inferText: '*복분자차*',
                          inferConfidence: 0.9997,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 851.0 },
                      { x: 2196.0, y: 851.0 },
                      { x: 2196.0, y: 966.0 },
                      { x: 1606.0, y: 966.0 },
                    ],
                  },
                  inferConfidence: 0.9997,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 966.0 },
                      { x: 2196.0, y: 966.0 },
                      { x: 2196.0, y: 1034.0 },
                      { x: 1606.0, y: 1034.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1812.0, y: 1038.0 },
                          { x: 1985.0, y: 1038.0 },
                          { x: 1985.0, y: 1083.0 },
                          { x: 1812.0, y: 1083.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1812.0, y: 1038.0 },
                              { x: 1985.0, y: 1038.0 },
                              { x: 1985.0, y: 1083.0 },
                              { x: 1812.0, y: 1083.0 },
                            ],
                          },
                          inferText: '계란떡볶이',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1034.0 },
                      { x: 2196.0, y: 1034.0 },
                      { x: 2196.0, y: 1085.0 },
                      { x: 1606.0, y: 1085.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 11,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1715.0, y: 1083.0 },
                          { x: 2085.0, y: 1083.0 },
                          { x: 2085.0, y: 1135.0 },
                          { x: 1715.0, y: 1135.0 },
                        ],
                      },
                      inferConfidence: 0.9986,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1715.0, y: 1083.0 },
                              { x: 2085.0, y: 1083.0 },
                              { x: 2085.0, y: 1135.0 },
                              { x: 1715.0, y: 1135.0 },
                            ],
                          },
                          inferText: '꼬마김밥&김가루양념밥',
                          inferConfidence: 0.9986,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1085.0 },
                      { x: 2196.0, y: 1085.0 },
                      { x: 2196.0, y: 1132.0 },
                      { x: 1606.0, y: 1132.0 },
                    ],
                  },
                  inferConfidence: 0.9986,
                  rowSpan: 1,
                  rowIndex: 12,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1848.0, y: 1135.0 },
                          { x: 1951.0, y: 1135.0 },
                          { x: 1951.0, y: 1178.0 },
                          { x: 1848.0, y: 1178.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1848.0, y: 1135.0 },
                              { x: 1951.0, y: 1135.0 },
                              { x: 1951.0, y: 1178.0 },
                              { x: 1848.0, y: 1178.0 },
                            ],
                          },
                          inferText: '어묵국',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1132.0 },
                      { x: 2196.0, y: 1132.0 },
                      { x: 2196.0, y: 1183.0 },
                      { x: 1606.0, y: 1183.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 13,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1766.0, y: 1181.0 },
                          { x: 2033.0, y: 1181.0 },
                          { x: 2033.0, y: 1223.0 },
                          { x: 1766.0, y: 1223.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1766.0, y: 1181.0 },
                              { x: 2033.0, y: 1181.0 },
                              { x: 2033.0, y: 1223.0 },
                              { x: 1766.0, y: 1223.0 },
                            ],
                          },
                          inferText: '자색고구마크로켓',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1183.0 },
                      { x: 2196.0, y: 1183.0 },
                      { x: 2196.0, y: 1225.0 },
                      { x: 1606.0, y: 1225.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 14,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1797.0, y: 1226.0 },
                          { x: 2000.0, y: 1226.0 },
                          { x: 2000.0, y: 1272.0 },
                          { x: 1797.0, y: 1272.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1797.0, y: 1226.0 },
                              { x: 2000.0, y: 1226.0 },
                              { x: 2000.0, y: 1272.0 },
                              { x: 1797.0, y: 1272.0 },
                            ],
                          },
                          inferText: '멕시칸샐러드',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1225.0 },
                      { x: 2196.0, y: 1225.0 },
                      { x: 2196.0, y: 1272.0 },
                      { x: 1606.0, y: 1272.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 15,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1745.0, y: 1272.0 },
                          { x: 2055.0, y: 1272.0 },
                          { x: 2055.0, y: 1323.0 },
                          { x: 1745.0, y: 1323.0 },
                        ],
                      },
                      inferConfidence: 0.9979,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1745.0, y: 1272.0 },
                              { x: 2055.0, y: 1272.0 },
                              { x: 2055.0, y: 1323.0 },
                              { x: 1745.0, y: 1323.0 },
                            ],
                          },
                          inferText: '(햄-계육,돈육:국산)',
                          inferConfidence: 0.9979,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1272.0 },
                      { x: 2196.0, y: 1272.0 },
                      { x: 2196.0, y: 1323.0 },
                      { x: 1606.0, y: 1323.0 },
                    ],
                  },
                  inferConfidence: 0.9979,
                  rowSpan: 1,
                  rowIndex: 16,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1815.0, y: 1323.0 },
                          { x: 1985.0, y: 1323.0 },
                          { x: 1985.0, y: 1366.0 },
                          { x: 1815.0, y: 1366.0 },
                        ],
                      },
                      inferConfidence: 0.9972,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1815.0, y: 1323.0 },
                              { x: 1985.0, y: 1323.0 },
                              { x: 1985.0, y: 1366.0 },
                              { x: 1815.0, y: 1366.0 },
                            ],
                          },
                          inferText: '단무지무침',
                          inferConfidence: 0.9972,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1323.0 },
                      { x: 2196.0, y: 1323.0 },
                      { x: 2196.0, y: 1370.0 },
                      { x: 1606.0, y: 1370.0 },
                    ],
                  },
                  inferConfidence: 0.9972,
                  rowSpan: 1,
                  rowIndex: 17,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1806.0, y: 1366.0 },
                          { x: 1994.0, y: 1366.0 },
                          { x: 1994.0, y: 1417.0 },
                          { x: 1806.0, y: 1417.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1806.0, y: 1366.0 },
                              { x: 1994.0, y: 1366.0 },
                              { x: 1994.0, y: 1417.0 },
                              { x: 1806.0, y: 1417.0 },
                            ],
                          },
                          inferText: '*복분자차*',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1370.0 },
                      { x: 2196.0, y: 1370.0 },
                      { x: 2196.0, y: 1416.0 },
                      { x: 1606.0, y: 1416.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1606.0, y: 1416.0 },
                      { x: 2196.0, y: 1416.0 },
                      { x: 2196.0, y: 1480.0 },
                      { x: 1606.0, y: 1480.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2416.0, y: 288.0 },
                          { x: 2586.0, y: 288.0 },
                          { x: 2586.0, y: 340.0 },
                          { x: 2416.0, y: 340.0 },
                        ],
                      },
                      inferConfidence: 0.99915,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2416.0, y: 291.0 },
                              { x: 2504.0, y: 291.0 },
                              { x: 2504.0, y: 337.0 },
                              { x: 2416.0, y: 337.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9985,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2498.0, y: 288.0 },
                              { x: 2586.0, y: 288.0 },
                              { x: 2586.0, y: 340.0 },
                              { x: 2498.0, y: 340.0 },
                            ],
                          },
                          inferText: '22일',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 252.0 },
                      { x: 2808.0, y: 252.0 },
                      { x: 2808.0, y: 376.0 },
                      { x: 2196.0, y: 376.0 },
                    ],
                  },
                  inferConfidence: 0.99915,
                  rowSpan: 1,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2461.0, y: 395.0 },
                          { x: 2543.0, y: 395.0 },
                          { x: 2543.0, y: 434.0 },
                          { x: 2461.0, y: 434.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2461.0, y: 395.0 },
                              { x: 2543.0, y: 395.0 },
                              { x: 2543.0, y: 434.0 },
                              { x: 2461.0, y: 434.0 },
                            ],
                          },
                          inferText: 'THU',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 376.0 },
                      { x: 2808.0, y: 376.0 },
                      { x: 2808.0, y: 452.0 },
                      { x: 2196.0, y: 452.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2446.0, y: 522.0 },
                          { x: 2552.0, y: 522.0 },
                          { x: 2552.0, y: 568.0 },
                          { x: 2446.0, y: 568.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2446.0, y: 522.0 },
                              { x: 2552.0, y: 522.0 },
                              { x: 2552.0, y: 568.0 },
                              { x: 2446.0, y: 568.0 },
                            ],
                          },
                          inferText: '설렁탕',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 452.0 },
                      { x: 2808.0, y: 452.0 },
                      { x: 2808.0, y: 571.0 },
                      { x: 2196.0, y: 571.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2279.0, y: 568.0 },
                          { x: 2722.0, y: 568.0 },
                          { x: 2722.0, y: 619.0 },
                          { x: 2279.0, y: 619.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2279.0, y: 568.0 },
                              { x: 2722.0, y: 568.0 },
                              { x: 2722.0, y: 619.0 },
                              { x: 2279.0, y: 619.0 },
                            ],
                          },
                          inferText: '(우육:호주산,진한사골농축액',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 571.0 },
                      { x: 2808.0, y: 571.0 },
                      { x: 2808.0, y: 618.0 },
                      { x: 2196.0, y: 618.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 3,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2322.0, y: 619.0 },
                          { x: 2677.0, y: 619.0 },
                          { x: 2677.0, y: 662.0 },
                          { x: 2322.0, y: 662.0 },
                        ],
                      },
                      inferConfidence: 0.9995,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2322.0, y: 619.0 },
                              { x: 2677.0, y: 619.0 },
                              { x: 2677.0, y: 662.0 },
                              { x: 2322.0, y: 662.0 },
                            ],
                          },
                          inferText: '-한우사골/모둠뼈:국산)',
                          inferConfidence: 0.9995,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 618.0 },
                      { x: 2808.0, y: 618.0 },
                      { x: 2808.0, y: 664.0 },
                      { x: 2196.0, y: 664.0 },
                    ],
                  },
                  inferConfidence: 0.9995,
                  rowSpan: 1,
                  rowIndex: 4,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2367.0, y: 662.0 },
                          { x: 2631.0, y: 662.0 },
                          { x: 2631.0, y: 713.0 },
                          { x: 2367.0, y: 713.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2367.0, y: 662.0 },
                              { x: 2631.0, y: 662.0 },
                              { x: 2631.0, y: 713.0 },
                              { x: 2367.0, y: 713.0 },
                            ],
                          },
                          inferText: '혼합잡곡밥/쌀밥',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 664.0 },
                      { x: 2808.0, y: 664.0 },
                      { x: 2808.0, y: 711.0 },
                      { x: 2196.0, y: 711.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 5,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2416.0, y: 713.0 },
                          { x: 2586.0, y: 713.0 },
                          { x: 2586.0, y: 756.0 },
                          { x: 2416.0, y: 756.0 },
                        ],
                      },
                      inferConfidence: 0.9966,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2416.0, y: 713.0 },
                              { x: 2586.0, y: 713.0 },
                              { x: 2586.0, y: 756.0 },
                              { x: 2416.0, y: 756.0 },
                            ],
                          },
                          inferText: '새우완자전',
                          inferConfidence: 0.9966,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 711.0 },
                      { x: 2808.0, y: 711.0 },
                      { x: 2808.0, y: 758.0 },
                      { x: 2196.0, y: 758.0 },
                    ],
                  },
                  inferConfidence: 0.9966,
                  rowSpan: 1,
                  rowIndex: 6,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2394.0, y: 756.0 },
                          { x: 2607.0, y: 756.0 },
                          { x: 2607.0, y: 807.0 },
                          { x: 2394.0, y: 807.0 },
                        ],
                      },
                      inferConfidence: 0.9954,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2394.0, y: 756.0 },
                              { x: 2607.0, y: 756.0 },
                              { x: 2607.0, y: 807.0 },
                              { x: 2394.0, y: 807.0 },
                            ],
                          },
                          inferText: '매운감자조림',
                          inferConfidence: 0.9954,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 758.0 },
                      { x: 2808.0, y: 758.0 },
                      { x: 2808.0, y: 805.0 },
                      { x: 2196.0, y: 805.0 },
                    ],
                  },
                  inferConfidence: 0.9954,
                  rowSpan: 1,
                  rowIndex: 7,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2401.0, y: 807.0 },
                          { x: 2601.0, y: 807.0 },
                          { x: 2601.0, y: 850.0 },
                          { x: 2401.0, y: 850.0 },
                        ],
                      },
                      inferConfidence: 0.9997,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2401.0, y: 807.0 },
                              { x: 2601.0, y: 807.0 },
                              { x: 2601.0, y: 850.0 },
                              { x: 2401.0, y: 850.0 },
                            ],
                          },
                          inferText: '열무된장나물',
                          inferConfidence: 0.9997,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 805.0 },
                      { x: 2808.0, y: 805.0 },
                      { x: 2808.0, y: 851.0 },
                      { x: 2196.0, y: 851.0 },
                    ],
                  },
                  inferConfidence: 0.9997,
                  rowSpan: 1,
                  rowIndex: 8,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2385.0, y: 850.0 },
                          { x: 2616.0, y: 850.0 },
                          { x: 2616.0, y: 901.0 },
                          { x: 2385.0, y: 901.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2385.0, y: 850.0 },
                              { x: 2501.0, y: 850.0 },
                              { x: 2501.0, y: 901.0 },
                              { x: 2385.0, y: 901.0 },
                            ],
                          },
                          inferText: '깍두기',
                          inferConfidence: 1.0,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2495.0, y: 850.0 },
                              { x: 2616.0, y: 850.0 },
                              { x: 2616.0, y: 901.0 },
                              { x: 2495.0, y: 901.0 },
                            ],
                          },
                          inferText: '*식혜*',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 851.0 },
                      { x: 2808.0, y: 851.0 },
                      { x: 2808.0, y: 966.0 },
                      { x: 2196.0, y: 966.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 966.0 },
                      { x: 2808.0, y: 966.0 },
                      { x: 2808.0, y: 1034.0 },
                      { x: 2196.0, y: 1034.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2413.0, y: 1038.0 },
                          { x: 2586.0, y: 1038.0 },
                          { x: 2586.0, y: 1083.0 },
                          { x: 2413.0, y: 1083.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2413.0, y: 1038.0 },
                              { x: 2586.0, y: 1038.0 },
                              { x: 2586.0, y: 1083.0 },
                              { x: 2413.0, y: 1083.0 },
                            ],
                          },
                          inferText: '카레라이스',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1034.0 },
                      { x: 2808.0, y: 1034.0 },
                      { x: 2808.0, y: 1085.0 },
                      { x: 2196.0, y: 1085.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 11,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2398.0, y: 1090.0 },
                          { x: 2604.0, y: 1090.0 },
                          { x: 2604.0, y: 1132.0 },
                          { x: 2398.0, y: 1132.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2398.0, y: 1090.0 },
                              { x: 2604.0, y: 1090.0 },
                              { x: 2604.0, y: 1132.0 },
                              { x: 2398.0, y: 1132.0 },
                            ],
                          },
                          inferText: '(돈육:국내산)',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1085.0 },
                      { x: 2808.0, y: 1085.0 },
                      { x: 2808.0, y: 1132.0 },
                      { x: 2196.0, y: 1132.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 12,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2450.0, y: 1132.0 },
                          { x: 2554.0, y: 1137.0 },
                          { x: 2552.0, y: 1179.0 },
                          { x: 2448.0, y: 1174.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2450.0, y: 1132.0 },
                              { x: 2554.0, y: 1137.0 },
                              { x: 2552.0, y: 1179.0 },
                              { x: 2448.0, y: 1174.0 },
                            ],
                          },
                          inferText: '김칫국',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1132.0 },
                      { x: 2808.0, y: 1132.0 },
                      { x: 2808.0, y: 1183.0 },
                      { x: 2196.0, y: 1183.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 13,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2394.0, y: 1178.0 },
                          { x: 2607.0, y: 1178.0 },
                          { x: 2607.0, y: 1229.0 },
                          { x: 2394.0, y: 1229.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2394.0, y: 1178.0 },
                              { x: 2607.0, y: 1178.0 },
                              { x: 2607.0, y: 1229.0 },
                              { x: 2394.0, y: 1229.0 },
                            ],
                          },
                          inferText: '비빔채소만두',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1183.0 },
                      { x: 2808.0, y: 1183.0 },
                      { x: 2808.0, y: 1225.0 },
                      { x: 2196.0, y: 1225.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 14,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2367.0, y: 1229.0 },
                          { x: 2634.0, y: 1229.0 },
                          { x: 2634.0, y: 1272.0 },
                          { x: 2367.0, y: 1272.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2367.0, y: 1229.0 },
                              { x: 2634.0, y: 1229.0 },
                              { x: 2634.0, y: 1272.0 },
                              { x: 2367.0, y: 1272.0 },
                            ],
                          },
                          inferText: '파프리카버섯볶음',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1225.0 },
                      { x: 2808.0, y: 1225.0 },
                      { x: 2808.0, y: 1272.0 },
                      { x: 2196.0, y: 1272.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 15,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2394.0, y: 1272.0 },
                          { x: 2607.0, y: 1272.0 },
                          { x: 2607.0, y: 1323.0 },
                          { x: 2394.0, y: 1323.0 },
                        ],
                      },
                      inferConfidence: 0.9996,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2394.0, y: 1272.0 },
                              { x: 2607.0, y: 1272.0 },
                              { x: 2607.0, y: 1323.0 },
                              { x: 2394.0, y: 1323.0 },
                            ],
                          },
                          inferText: '열무된장나물',
                          inferConfidence: 0.9996,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1272.0 },
                      { x: 2808.0, y: 1272.0 },
                      { x: 2808.0, y: 1323.0 },
                      { x: 2196.0, y: 1323.0 },
                    ],
                  },
                  inferConfidence: 0.9996,
                  rowSpan: 1,
                  rowIndex: 16,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2446.0, y: 1320.0 },
                          { x: 2549.0, y: 1320.0 },
                          { x: 2549.0, y: 1366.0 },
                          { x: 2446.0, y: 1366.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2446.0, y: 1320.0 },
                              { x: 2549.0, y: 1320.0 },
                              { x: 2549.0, y: 1366.0 },
                              { x: 2446.0, y: 1366.0 },
                            ],
                          },
                          inferText: '깍두기',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1323.0 },
                      { x: 2808.0, y: 1323.0 },
                      { x: 2808.0, y: 1370.0 },
                      { x: 2196.0, y: 1370.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 17,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2440.0, y: 1366.0 },
                          { x: 2561.0, y: 1366.0 },
                          { x: 2561.0, y: 1414.0 },
                          { x: 2440.0, y: 1414.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2440.0, y: 1366.0 },
                              { x: 2561.0, y: 1366.0 },
                              { x: 2561.0, y: 1414.0 },
                              { x: 2440.0, y: 1414.0 },
                            ],
                          },
                          inferText: '*식혜*',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1370.0 },
                      { x: 2808.0, y: 1370.0 },
                      { x: 2808.0, y: 1416.0 },
                      { x: 2196.0, y: 1416.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 2196.0, y: 1416.0 },
                      { x: 2808.0, y: 1416.0 },
                      { x: 2808.0, y: 1480.0 },
                      { x: 2196.0, y: 1480.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 5,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3017.0, y: 288.0 },
                          { x: 3187.0, y: 288.0 },
                          { x: 3187.0, y: 340.0 },
                          { x: 3017.0, y: 340.0 },
                        ],
                      },
                      inferConfidence: 0.9988,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3017.0, y: 291.0 },
                              { x: 3105.0, y: 291.0 },
                              { x: 3105.0, y: 340.0 },
                              { x: 3017.0, y: 340.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9993,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3102.0, y: 288.0 },
                              { x: 3187.0, y: 288.0 },
                              { x: 3187.0, y: 340.0 },
                              { x: 3102.0, y: 340.0 },
                            ],
                          },
                          inferText: '23일',
                          inferConfidence: 0.9983,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 252.0 },
                      { x: 3394.0, y: 252.0 },
                      { x: 3394.0, y: 376.0 },
                      { x: 2808.0, y: 376.0 },
                    ],
                  },
                  inferConfidence: 0.9988,
                  rowSpan: 1,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3074.0, y: 395.0 },
                          { x: 3135.0, y: 395.0 },
                          { x: 3135.0, y: 434.0 },
                          { x: 3074.0, y: 434.0 },
                        ],
                      },
                      inferConfidence: 0.9993,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3074.0, y: 395.0 },
                              { x: 3135.0, y: 395.0 },
                              { x: 3135.0, y: 434.0 },
                              { x: 3074.0, y: 434.0 },
                            ],
                          },
                          inferText: 'FRI',
                          inferConfidence: 0.9993,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 376.0 },
                      { x: 3394.0, y: 376.0 },
                      { x: 3394.0, y: 452.0 },
                      { x: 2808.0, y: 452.0 },
                    ],
                  },
                  inferConfidence: 0.9993,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2919.0, y: 525.0 },
                          { x: 3287.0, y: 525.0 },
                          { x: 3287.0, y: 568.0 },
                          { x: 2919.0, y: 568.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2919.0, y: 525.0 },
                              { x: 3287.0, y: 525.0 },
                              { x: 3287.0, y: 568.0 },
                              { x: 2919.0, y: 568.0 },
                            ],
                          },
                          inferText: '숯불돼지고기고추장볶음',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 452.0 },
                      { x: 3394.0, y: 452.0 },
                      { x: 3394.0, y: 571.0 },
                      { x: 2808.0, y: 571.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2901.0, y: 568.0 },
                          { x: 3308.0, y: 568.0 },
                          { x: 3308.0, y: 619.0 },
                          { x: 2901.0, y: 619.0 },
                        ],
                      },
                      inferConfidence: 0.9993333,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2901.0, y: 574.0 },
                              { x: 3105.0, y: 574.0 },
                              { x: 3105.0, y: 616.0 },
                              { x: 2901.0, y: 616.0 },
                            ],
                          },
                          inferText: '(돈육:국내산,',
                          inferConfidence: 1.0,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3114.0, y: 574.0 },
                              { x: 3217.0, y: 574.0 },
                              { x: 3217.0, y: 616.0 },
                              { x: 3114.0, y: 616.0 },
                            ],
                          },
                          inferText: '미국산',
                          inferConfidence: 1.0,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3214.0, y: 568.0 },
                              { x: 3308.0, y: 568.0 },
                              { x: 3308.0, y: 619.0 },
                              { x: 3214.0, y: 619.0 },
                            ],
                          },
                          inferText: '섞음)',
                          inferConfidence: 0.998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 571.0 },
                      { x: 3394.0, y: 571.0 },
                      { x: 3394.0, y: 618.0 },
                      { x: 2808.0, y: 618.0 },
                    ],
                  },
                  inferConfidence: 0.9993333,
                  rowSpan: 1,
                  rowIndex: 3,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3011.0, y: 619.0 },
                          { x: 3196.0, y: 619.0 },
                          { x: 3196.0, y: 662.0 },
                          { x: 3011.0, y: 662.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3011.0, y: 619.0 },
                              { x: 3196.0, y: 619.0 },
                              { x: 3196.0, y: 662.0 },
                              { x: 3011.0, y: 662.0 },
                            ],
                          },
                          inferText: '흑미밥/쌀밥',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 618.0 },
                      { x: 3394.0, y: 618.0 },
                      { x: 3394.0, y: 664.0 },
                      { x: 2808.0, y: 664.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 4,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2995.0, y: 662.0 },
                          { x: 3211.0, y: 662.0 },
                          { x: 3211.0, y: 713.0 },
                          { x: 2995.0, y: 713.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2995.0, y: 662.0 },
                              { x: 3211.0, y: 662.0 },
                              { x: 3211.0, y: 713.0 },
                              { x: 2995.0, y: 713.0 },
                            ],
                          },
                          inferText: '시금치된장국',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 664.0 },
                      { x: 3394.0, y: 664.0 },
                      { x: 3394.0, y: 711.0 },
                      { x: 2808.0, y: 711.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 5,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2953.0, y: 713.0 },
                          { x: 3253.0, y: 713.0 },
                          { x: 3253.0, y: 756.0 },
                          { x: 2953.0, y: 756.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2953.0, y: 713.0 },
                              { x: 3253.0, y: 713.0 },
                              { x: 3253.0, y: 756.0 },
                              { x: 2953.0, y: 756.0 },
                            ],
                          },
                          inferText: '토마토스크램블에그',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 711.0 },
                      { x: 3394.0, y: 711.0 },
                      { x: 3394.0, y: 758.0 },
                      { x: 2808.0, y: 758.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 6,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2998.0, y: 756.0 },
                          { x: 3208.0, y: 756.0 },
                          { x: 3208.0, y: 807.0 },
                          { x: 2998.0, y: 807.0 },
                        ],
                      },
                      inferConfidence: 0.9969,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2998.0, y: 756.0 },
                              { x: 3208.0, y: 756.0 },
                              { x: 3208.0, y: 807.0 },
                              { x: 2998.0, y: 807.0 },
                            ],
                          },
                          inferText: '쌈무&깻잎쌈',
                          inferConfidence: 0.9969,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 758.0 },
                      { x: 3394.0, y: 758.0 },
                      { x: 3394.0, y: 805.0 },
                      { x: 2808.0, y: 805.0 },
                    ],
                  },
                  inferConfidence: 0.9969,
                  rowSpan: 1,
                  rowIndex: 7,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3062.0, y: 801.0 },
                          { x: 3144.0, y: 801.0 },
                          { x: 3144.0, y: 853.0 },
                          { x: 3062.0, y: 853.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3062.0, y: 801.0 },
                              { x: 3144.0, y: 801.0 },
                              { x: 3144.0, y: 853.0 },
                              { x: 3062.0, y: 853.0 },
                            ],
                          },
                          inferText: '김치',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 805.0 },
                      { x: 3394.0, y: 805.0 },
                      { x: 3394.0, y: 851.0 },
                      { x: 2808.0, y: 851.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 8,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3026.0, y: 850.0 },
                          { x: 3180.0, y: 850.0 },
                          { x: 3180.0, y: 901.0 },
                          { x: 3026.0, y: 901.0 },
                        ],
                      },
                      inferConfidence: 0.9059,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3026.0, y: 850.0 },
                              { x: 3180.0, y: 850.0 },
                              { x: 3180.0, y: 901.0 },
                              { x: 3026.0, y: 901.0 },
                            ],
                          },
                          inferText: '*유자차*',
                          inferConfidence: 0.9059,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 851.0 },
                      { x: 3394.0, y: 851.0 },
                      { x: 3394.0, y: 966.0 },
                      { x: 2808.0, y: 966.0 },
                    ],
                  },
                  inferConfidence: 0.9059,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 966.0 },
                      { x: 3394.0, y: 966.0 },
                      { x: 3394.0, y: 1034.0 },
                      { x: 2808.0, y: 1034.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2971.0, y: 1041.0 },
                          { x: 3232.0, y: 1041.0 },
                          { x: 3232.0, y: 1083.0 },
                          { x: 2971.0, y: 1083.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2971.0, y: 1041.0 },
                              { x: 3232.0, y: 1041.0 },
                              { x: 3232.0, y: 1083.0 },
                              { x: 2971.0, y: 1083.0 },
                            ],
                          },
                          inferText: '새우볶음밥&케찹',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1034.0 },
                      { x: 3394.0, y: 1034.0 },
                      { x: 3394.0, y: 1085.0 },
                      { x: 2808.0, y: 1085.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 11,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3029.0, y: 1083.0 },
                          { x: 3177.0, y: 1083.0 },
                          { x: 3177.0, y: 1135.0 },
                          { x: 3029.0, y: 1135.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3029.0, y: 1083.0 },
                              { x: 3177.0, y: 1083.0 },
                              { x: 3177.0, y: 1135.0 },
                              { x: 3029.0, y: 1135.0 },
                            ],
                          },
                          inferText: '일식장국',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1085.0 },
                      { x: 3394.0, y: 1085.0 },
                      { x: 3394.0, y: 1132.0 },
                      { x: 2808.0, y: 1132.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 12,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2998.0, y: 1132.0 },
                          { x: 3202.0, y: 1132.0 },
                          { x: 3202.0, y: 1178.0 },
                          { x: 2998.0, y: 1178.0 },
                        ],
                      },
                      inferConfidence: 0.9995,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2998.0, y: 1132.0 },
                              { x: 3202.0, y: 1132.0 },
                              { x: 3202.0, y: 1178.0 },
                              { x: 2998.0, y: 1178.0 },
                            ],
                          },
                          inferText: '연근땅콩강정',
                          inferConfidence: 0.9995,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1132.0 },
                      { x: 3394.0, y: 1132.0 },
                      { x: 3394.0, y: 1183.0 },
                      { x: 2808.0, y: 1183.0 },
                    ],
                  },
                  inferConfidence: 0.9995,
                  rowSpan: 1,
                  rowIndex: 13,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2995.0, y: 1178.0 },
                          { x: 3208.0, y: 1178.0 },
                          { x: 3208.0, y: 1229.0 },
                          { x: 2995.0, y: 1229.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2995.0, y: 1178.0 },
                              { x: 3208.0, y: 1178.0 },
                              { x: 3208.0, y: 1229.0 },
                              { x: 2995.0, y: 1229.0 },
                            ],
                          },
                          inferText: '쫄면채소무침',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1183.0 },
                      { x: 3394.0, y: 1183.0 },
                      { x: 3394.0, y: 1225.0 },
                      { x: 2808.0, y: 1225.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 14,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3001.0, y: 1229.0 },
                          { x: 3202.0, y: 1229.0 },
                          { x: 3202.0, y: 1272.0 },
                          { x: 3001.0, y: 1272.0 },
                        ],
                      },
                      inferConfidence: 0.9963,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3001.0, y: 1229.0 },
                              { x: 3202.0, y: 1229.0 },
                              { x: 3202.0, y: 1272.0 },
                              { x: 3001.0, y: 1272.0 },
                            ],
                          },
                          inferText: '만다린샐러드',
                          inferConfidence: 0.9963,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1225.0 },
                      { x: 3394.0, y: 1225.0 },
                      { x: 3394.0, y: 1272.0 },
                      { x: 2808.0, y: 1272.0 },
                    ],
                  },
                  inferConfidence: 0.9963,
                  rowSpan: 1,
                  rowIndex: 15,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3032.0, y: 1272.0 },
                          { x: 3171.0, y: 1272.0 },
                          { x: 3171.0, y: 1323.0 },
                          { x: 3032.0, y: 1323.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3032.0, y: 1272.0 },
                              { x: 3171.0, y: 1272.0 },
                              { x: 3171.0, y: 1323.0 },
                              { x: 3032.0, y: 1323.0 },
                            ],
                          },
                          inferText: '&드레싱',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1272.0 },
                      { x: 3394.0, y: 1272.0 },
                      { x: 3394.0, y: 1323.0 },
                      { x: 2808.0, y: 1323.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 16,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3062.0, y: 1317.0 },
                          { x: 3144.0, y: 1317.0 },
                          { x: 3144.0, y: 1369.0 },
                          { x: 3062.0, y: 1369.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3062.0, y: 1317.0 },
                              { x: 3144.0, y: 1317.0 },
                              { x: 3144.0, y: 1369.0 },
                              { x: 3062.0, y: 1369.0 },
                            ],
                          },
                          inferText: '김치',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1323.0 },
                      { x: 3394.0, y: 1323.0 },
                      { x: 3394.0, y: 1370.0 },
                      { x: 2808.0, y: 1370.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 17,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 3029.0, y: 1372.0 },
                          { x: 3174.0, y: 1372.0 },
                          { x: 3174.0, y: 1414.0 },
                          { x: 3029.0, y: 1414.0 },
                        ],
                      },
                      inferConfidence: 0.989,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 3029.0, y: 1372.0 },
                              { x: 3174.0, y: 1372.0 },
                              { x: 3174.0, y: 1414.0 },
                              { x: 3029.0, y: 1414.0 },
                            ],
                          },
                          inferText: '*유자차*',
                          inferConfidence: 0.989,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1370.0 },
                      { x: 3394.0, y: 1370.0 },
                      { x: 3394.0, y: 1416.0 },
                      { x: 2808.0, y: 1416.0 },
                    ],
                  },
                  inferConfidence: 0.989,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 6,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 2808.0, y: 1416.0 },
                      { x: 3394.0, y: 1416.0 },
                      { x: 3394.0, y: 1480.0 },
                      { x: 2808.0, y: 1480.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 6,
                },
              ],
              boundingPoly: {
                vertices: [
                  { x: 12.0, y: 252.0 },
                  { x: 3394.0, y: 252.0 },
                  { x: 3394.0, y: 1480.0 },
                  { x: 12.0, y: 1480.0 },
                ],
              },
              inferConfidence: 0.9982171,
            },
          ],
          fields: [
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1311.0, y: 70.0 },
                  { x: 1560.0, y: 70.0 },
                  { x: 1560.0, y: 191.0 },
                  { x: 1311.0, y: 191.0 },
                ],
              },
              inferText: '20층',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1572.0, y: 67.0 },
                  { x: 2091.0, y: 67.0 },
                  { x: 2091.0, y: 194.0 },
                  { x: 1572.0, y: 194.0 },
                ],
              },
              inferText: '주간메뉴표',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 628.0, y: 291.0 },
                  { x: 719.0, y: 291.0 },
                  { x: 719.0, y: 340.0 },
                  { x: 628.0, y: 340.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9992,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 713.0, y: 288.0 },
                  { x: 798.0, y: 288.0 },
                  { x: 798.0, y: 340.0 },
                  { x: 713.0, y: 340.0 },
                ],
              },
              inferText: '19일',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1220.0, y: 291.0 },
                  { x: 1308.0, y: 291.0 },
                  { x: 1308.0, y: 337.0 },
                  { x: 1220.0, y: 337.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9987,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1305.0, y: 291.0 },
                  { x: 1393.0, y: 291.0 },
                  { x: 1393.0, y: 340.0 },
                  { x: 1305.0, y: 340.0 },
                ],
              },
              inferText: '20일',
              inferConfidence: 0.9976,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1818.0, y: 288.0 },
                  { x: 1909.0, y: 288.0 },
                  { x: 1909.0, y: 340.0 },
                  { x: 1818.0, y: 340.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9984,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1900.0, y: 288.0 },
                  { x: 1982.0, y: 288.0 },
                  { x: 1982.0, y: 340.0 },
                  { x: 1900.0, y: 340.0 },
                ],
              },
              inferText: '21일',
              inferConfidence: 0.9995,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2416.0, y: 291.0 },
                  { x: 2504.0, y: 291.0 },
                  { x: 2504.0, y: 337.0 },
                  { x: 2416.0, y: 337.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9985,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2498.0, y: 288.0 },
                  { x: 2586.0, y: 288.0 },
                  { x: 2586.0, y: 340.0 },
                  { x: 2498.0, y: 340.0 },
                ],
              },
              inferText: '22일',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3017.0, y: 291.0 },
                  { x: 3105.0, y: 291.0 },
                  { x: 3105.0, y: 340.0 },
                  { x: 3017.0, y: 340.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9993,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3102.0, y: 288.0 },
                  { x: 3187.0, y: 288.0 },
                  { x: 3187.0, y: 340.0 },
                  { x: 3102.0, y: 340.0 },
                ],
              },
              inferText: '23일',
              inferConfidence: 0.9983,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 42.0, y: 328.0 },
                  { x: 140.0, y: 328.0 },
                  { x: 140.0, y: 376.0 },
                  { x: 42.0, y: 376.0 },
                ],
              },
              inferText: '구분',
              inferConfidence: 0.9509,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 668.0, y: 395.0 },
                  { x: 762.0, y: 395.0 },
                  { x: 762.0, y: 434.0 },
                  { x: 668.0, y: 434.0 },
                ],
              },
              inferText: 'MON',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1272.0, y: 395.0 },
                  { x: 1344.0, y: 395.0 },
                  { x: 1344.0, y: 434.0 },
                  { x: 1272.0, y: 434.0 },
                ],
              },
              inferText: 'TUE',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1857.0, y: 395.0 },
                  { x: 1942.0, y: 395.0 },
                  { x: 1942.0, y: 434.0 },
                  { x: 1857.0, y: 434.0 },
                ],
              },
              inferText: 'WED',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2461.0, y: 395.0 },
                  { x: 2543.0, y: 395.0 },
                  { x: 2543.0, y: 434.0 },
                  { x: 2461.0, y: 434.0 },
                ],
              },
              inferText: 'THU',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3074.0, y: 395.0 },
                  { x: 3135.0, y: 395.0 },
                  { x: 3135.0, y: 434.0 },
                  { x: 3074.0, y: 434.0 },
                ],
              },
              inferText: 'FRI',
              inferConfidence: 0.9993,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 662.0, y: 525.0 },
                  { x: 765.0, y: 525.0 },
                  { x: 765.0, y: 568.0 },
                  { x: 662.0, y: 568.0 },
                ],
              },
              inferText: '비빔밥',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1235.0, y: 522.0 },
                  { x: 1372.0, y: 522.0 },
                  { x: 1372.0, y: 568.0 },
                  { x: 1235.0, y: 568.0 },
                ],
              },
              inferText: '닭매운찜',
              inferConfidence: 0.9988,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1830.0, y: 522.0 },
                  { x: 1967.0, y: 522.0 },
                  { x: 1967.0, y: 568.0 },
                  { x: 1830.0, y: 568.0 },
                ],
              },
              inferText: '비지찌개',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2446.0, y: 522.0 },
                  { x: 2552.0, y: 522.0 },
                  { x: 2552.0, y: 568.0 },
                  { x: 2446.0, y: 568.0 },
                ],
              },
              inferText: '설렁탕',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2919.0, y: 525.0 },
                  { x: 3287.0, y: 525.0 },
                  { x: 3287.0, y: 568.0 },
                  { x: 2919.0, y: 568.0 },
                ],
              },
              inferText: '숯불돼지고기고추장볶음',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 604.0, y: 568.0 },
                  { x: 822.0, y: 568.0 },
                  { x: 822.0, y: 619.0 },
                  { x: 604.0, y: 619.0 },
                ],
              },
              inferText: '(우육:호주산)',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1181.0, y: 568.0 },
                  { x: 1432.0, y: 568.0 },
                  { x: 1432.0, y: 619.0 },
                  { x: 1181.0, y: 619.0 },
                ],
              },
              inferText: '(계육:브라질산)',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1800.0, y: 568.0 },
                  { x: 2000.0, y: 568.0 },
                  { x: 2000.0, y: 619.0 },
                  { x: 1800.0, y: 619.0 },
                ],
              },
              inferText: '보리밥/쌀밥',
              inferConfidence: 0.9994,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2279.0, y: 568.0 },
                  { x: 2722.0, y: 568.0 },
                  { x: 2722.0, y: 619.0 },
                  { x: 2279.0, y: 619.0 },
                ],
              },
              inferText: '(우육:호주산,진한사골농축액',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2901.0, y: 574.0 },
                  { x: 3105.0, y: 574.0 },
                  { x: 3105.0, y: 616.0 },
                  { x: 2901.0, y: 616.0 },
                ],
              },
              inferText: '(돈육:국내산,',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3114.0, y: 574.0 },
                  { x: 3217.0, y: 574.0 },
                  { x: 3217.0, y: 616.0 },
                  { x: 3114.0, y: 616.0 },
                ],
              },
              inferText: '미국산',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3214.0, y: 568.0 },
                  { x: 3308.0, y: 568.0 },
                  { x: 3308.0, y: 619.0 },
                  { x: 3214.0, y: 619.0 },
                ],
              },
              inferText: '섞음)',
              inferConfidence: 0.998,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 631.0, y: 619.0 },
                  { x: 798.0, y: 619.0 },
                  { x: 798.0, y: 662.0 },
                  { x: 631.0, y: 662.0 },
                ],
              },
              inferText: '가다랭이국',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1214.0, y: 619.0 },
                  { x: 1399.0, y: 619.0 },
                  { x: 1399.0, y: 662.0 },
                  { x: 1214.0, y: 662.0 },
                ],
              },
              inferText: '차조밥/쌀밥',
              inferConfidence: 0.9995,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1754.0, y: 619.0 },
                  { x: 2045.0, y: 619.0 },
                  { x: 2045.0, y: 662.0 },
                  { x: 1754.0, y: 662.0 },
                ],
              },
              inferText: '치킨너겟&머스타드',
              inferConfidence: 0.9989,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2322.0, y: 619.0 },
                  { x: 2677.0, y: 619.0 },
                  { x: 2677.0, y: 662.0 },
                  { x: 2322.0, y: 662.0 },
                ],
              },
              inferText: '-한우사골/모둠뼈:국산)',
              inferConfidence: 0.9995,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3011.0, y: 619.0 },
                  { x: 3196.0, y: 619.0 },
                  { x: 3196.0, y: 662.0 },
                  { x: 3011.0, y: 662.0 },
                ],
              },
              inferText: '흑미밥/쌀밥',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 607.0, y: 662.0 },
                  { x: 819.0, y: 662.0 },
                  { x: 819.0, y: 713.0 },
                  { x: 607.0, y: 713.0 },
                ],
              },
              inferText: '도토리묵무침',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1235.0, y: 662.0 },
                  { x: 1381.0, y: 662.0 },
                  { x: 1381.0, y: 713.0 },
                  { x: 1235.0, y: 713.0 },
                ],
              },
              inferText: '멸치육수',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1724.0, y: 668.0 },
                  { x: 2076.0, y: 668.0 },
                  { x: 2076.0, y: 710.0 },
                  { x: 1724.0, y: 710.0 },
                ],
              },
              inferText: '(치킨너겟-계육:국내산)',
              inferConfidence: 0.9996,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2367.0, y: 662.0 },
                  { x: 2631.0, y: 662.0 },
                  { x: 2631.0, y: 713.0 },
                  { x: 2367.0, y: 713.0 },
                ],
              },
              inferText: '혼합잡곡밥/쌀밥',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2995.0, y: 662.0 },
                  { x: 3211.0, y: 662.0 },
                  { x: 3211.0, y: 713.0 },
                  { x: 2995.0, y: 713.0 },
                ],
              },
              inferText: '시금치된장국',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 231.0, y: 686.0 },
                  { x: 282.0, y: 686.0 },
                  { x: 282.0, y: 731.0 },
                  { x: 231.0, y: 731.0 },
                ],
              },
              inferText: 'A.',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 282.0, y: 686.0 },
                  { x: 352.0, y: 686.0 },
                  { x: 352.0, y: 731.0 },
                  { x: 282.0, y: 731.0 },
                ],
              },
              inferText: '한식',
              inferConfidence: 0.9978,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 595.0, y: 710.0 },
                  { x: 829.0, y: 710.0 },
                  { x: 829.0, y: 756.0 },
                  { x: 595.0, y: 756.0 },
                ],
              },
              inferText: '시래기된장지짐',
              inferConfidence: 0.9972,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1254.0, y: 712.0 },
                  { x: 1358.0, y: 717.0 },
                  { x: 1356.0, y: 758.0 },
                  { x: 1252.0, y: 754.0 },
                ],
              },
              inferText: '부추전',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1754.0, y: 713.0 },
                  { x: 2049.0, y: 713.0 },
                  { x: 2049.0, y: 756.0 },
                  { x: 1754.0, y: 756.0 },
                ],
              },
              inferText: '분홍소시지전&케찹',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2416.0, y: 713.0 },
                  { x: 2586.0, y: 713.0 },
                  { x: 2586.0, y: 756.0 },
                  { x: 2416.0, y: 756.0 },
                ],
              },
              inferText: '새우완자전',
              inferConfidence: 0.9966,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2953.0, y: 713.0 },
                  { x: 3253.0, y: 713.0 },
                  { x: 3253.0, y: 756.0 },
                  { x: 2953.0, y: 756.0 },
                ],
              },
              inferText: '토마토스크램블에그',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 625.0, y: 756.0 },
                  { x: 804.0, y: 756.0 },
                  { x: 804.0, y: 807.0 },
                  { x: 625.0, y: 807.0 },
                ],
              },
              inferText: '다시마튀각',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1217.0, y: 756.0 },
                  { x: 1396.0, y: 756.0 },
                  { x: 1396.0, y: 807.0 },
                  { x: 1217.0, y: 807.0 },
                ],
              },
              inferText: '건파래볶음',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1690.0, y: 759.0 },
                  { x: 2109.0, y: 759.0 },
                  { x: 2109.0, y: 801.0 },
                  { x: 1690.0, y: 801.0 },
                ],
              },
              inferText: '(참피온소시지-돈육:국내산)',
              inferConfidence: 0.9925,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2394.0, y: 756.0 },
                  { x: 2607.0, y: 756.0 },
                  { x: 2607.0, y: 807.0 },
                  { x: 2394.0, y: 807.0 },
                ],
              },
              inferText: '매운감자조림',
              inferConfidence: 0.9954,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2998.0, y: 756.0 },
                  { x: 3208.0, y: 756.0 },
                  { x: 3208.0, y: 807.0 },
                  { x: 2998.0, y: 807.0 },
                ],
              },
              inferText: '쌈무&깻잎쌈',
              inferConfidence: 0.9969,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 671.0, y: 798.0 },
                  { x: 753.0, y: 798.0 },
                  { x: 753.0, y: 853.0 },
                  { x: 671.0, y: 853.0 },
                ],
              },
              inferText: '김치',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1266.0, y: 801.0 },
                  { x: 1347.0, y: 801.0 },
                  { x: 1347.0, y: 853.0 },
                  { x: 1266.0, y: 853.0 },
                ],
              },
              inferText: '김치',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1727.0, y: 807.0 },
                  { x: 2070.0, y: 807.0 },
                  { x: 2070.0, y: 850.0 },
                  { x: 1727.0, y: 850.0 },
                ],
              },
              inferText: '매콤콩나물잡채/깍두기',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2401.0, y: 807.0 },
                  { x: 2601.0, y: 807.0 },
                  { x: 2601.0, y: 850.0 },
                  { x: 2401.0, y: 850.0 },
                ],
              },
              inferText: '열무된장나물',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3062.0, y: 801.0 },
                  { x: 3144.0, y: 801.0 },
                  { x: 3144.0, y: 853.0 },
                  { x: 3062.0, y: 853.0 },
                ],
              },
              inferText: '김치',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 628.0, y: 850.0 },
                  { x: 798.0, y: 850.0 },
                  { x: 798.0, y: 895.0 },
                  { x: 628.0, y: 895.0 },
                ],
              },
              inferText: '*ICE초코*',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1165.0, y: 850.0 },
                  { x: 1448.0, y: 850.0 },
                  { x: 1448.0, y: 901.0 },
                  { x: 1165.0, y: 901.0 },
                ],
              },
              inferText: '*아이스티(레몬)*',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1806.0, y: 850.0 },
                  { x: 1994.0, y: 850.0 },
                  { x: 1994.0, y: 901.0 },
                  { x: 1806.0, y: 901.0 },
                ],
              },
              inferText: '*복분자차*',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2385.0, y: 850.0 },
                  { x: 2501.0, y: 850.0 },
                  { x: 2501.0, y: 901.0 },
                  { x: 2385.0, y: 901.0 },
                ],
              },
              inferText: '깍두기',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2495.0, y: 850.0 },
                  { x: 2616.0, y: 850.0 },
                  { x: 2616.0, y: 901.0 },
                  { x: 2495.0, y: 901.0 },
                ],
              },
              inferText: '*식혜*',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3026.0, y: 850.0 },
                  { x: 3180.0, y: 850.0 },
                  { x: 3180.0, y: 901.0 },
                  { x: 3026.0, y: 901.0 },
                ],
              },
              inferText: '*유자차*',
              inferConfidence: 0.9059,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 52.0, y: 923.0 },
                  { x: 140.0, y: 923.0 },
                  { x: 140.0, y: 965.0 },
                  { x: 52.0, y: 965.0 },
                ],
              },
              inferText: '[20F]',
              inferConfidence: 0.9992,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 42.0, y: 968.0 },
                  { x: 146.0, y: 968.0 },
                  { x: 146.0, y: 1014.0 },
                  { x: 42.0, y: 1014.0 },
                ],
              },
              inferText: '일반식',
              inferConfidence: 0.9993,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 613.0, y: 1041.0 },
                  { x: 816.0, y: 1041.0 },
                  { x: 816.0, y: 1083.0 },
                  { x: 613.0, y: 1083.0 },
                ],
              },
              inferText: '해물볶음우동',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1220.0, y: 1041.0 },
                  { x: 1393.0, y: 1041.0 },
                  { x: 1393.0, y: 1083.0 },
                  { x: 1220.0, y: 1083.0 },
                ],
              },
              inferText: '양지쌀국수',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1812.0, y: 1038.0 },
                  { x: 1985.0, y: 1038.0 },
                  { x: 1985.0, y: 1083.0 },
                  { x: 1812.0, y: 1083.0 },
                ],
              },
              inferText: '계란떡볶이',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2413.0, y: 1038.0 },
                  { x: 2586.0, y: 1038.0 },
                  { x: 2586.0, y: 1083.0 },
                  { x: 2413.0, y: 1083.0 },
                ],
              },
              inferText: '카레라이스',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2971.0, y: 1041.0 },
                  { x: 3232.0, y: 1041.0 },
                  { x: 3232.0, y: 1083.0 },
                  { x: 2971.0, y: 1083.0 },
                ],
              },
              inferText: '새우볶음밥&케찹',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 589.0, y: 1083.0 },
                  { x: 838.0, y: 1083.0 },
                  { x: 838.0, y: 1135.0 },
                  { x: 589.0, y: 1135.0 },
                ],
              },
              inferText: '(오징어:원양산)',
              inferConfidence: 0.9992,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1108.0, y: 1083.0 },
                  { x: 1505.0, y: 1083.0 },
                  { x: 1505.0, y: 1135.0 },
                  { x: 1108.0, y: 1135.0 },
                ],
              },
              inferText: '(소고기편육-우육:외국산)',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1715.0, y: 1083.0 },
                  { x: 2085.0, y: 1083.0 },
                  { x: 2085.0, y: 1135.0 },
                  { x: 1715.0, y: 1135.0 },
                ],
              },
              inferText: '꼬마김밥&김가루양념밥',
              inferConfidence: 0.9986,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2398.0, y: 1090.0 },
                  { x: 2604.0, y: 1090.0 },
                  { x: 2604.0, y: 1132.0 },
                  { x: 2398.0, y: 1132.0 },
                ],
              },
              inferText: '(돈육:국내산)',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3029.0, y: 1083.0 },
                  { x: 3177.0, y: 1083.0 },
                  { x: 3177.0, y: 1135.0 },
                  { x: 3029.0, y: 1135.0 },
                ],
              },
              inferText: '일식장국',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 601.0, y: 1135.0 },
                  { x: 825.0, y: 1135.0 },
                  { x: 825.0, y: 1178.0 },
                  { x: 601.0, y: 1178.0 },
                ],
              },
              inferText: '쌀밥&후리가케',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1159.0, y: 1135.0 },
                  { x: 1457.0, y: 1135.0 },
                  { x: 1457.0, y: 1178.0 },
                  { x: 1159.0, y: 1178.0 },
                ],
              },
              inferText: '미니파인애플볶음밥',
              inferConfidence: 0.9979,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1848.0, y: 1135.0 },
                  { x: 1951.0, y: 1135.0 },
                  { x: 1951.0, y: 1178.0 },
                  { x: 1848.0, y: 1178.0 },
                ],
              },
              inferText: '어묵국',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2450.0, y: 1132.0 },
                  { x: 2554.0, y: 1137.0 },
                  { x: 2552.0, y: 1179.0 },
                  { x: 2448.0, y: 1174.0 },
                ],
              },
              inferText: '김칫국',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2998.0, y: 1132.0 },
                  { x: 3202.0, y: 1132.0 },
                  { x: 3202.0, y: 1178.0 },
                  { x: 2998.0, y: 1178.0 },
                ],
              },
              inferText: '연근땅콩강정',
              inferConfidence: 0.9995,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 625.0, y: 1178.0 },
                  { x: 804.0, y: 1178.0 },
                  { x: 804.0, y: 1229.0 },
                  { x: 625.0, y: 1229.0 },
                ],
              },
              inferText: '가다랭이국',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1217.0, y: 1178.0 },
                  { x: 1396.0, y: 1178.0 },
                  { x: 1396.0, y: 1229.0 },
                  { x: 1217.0, y: 1229.0 },
                ],
              },
              inferText: '달콤팥춘권',
              inferConfidence: 0.9981,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1766.0, y: 1181.0 },
                  { x: 2033.0, y: 1181.0 },
                  { x: 2033.0, y: 1223.0 },
                  { x: 1766.0, y: 1223.0 },
                ],
              },
              inferText: '자색고구마크로켓',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2394.0, y: 1178.0 },
                  { x: 2607.0, y: 1178.0 },
                  { x: 2607.0, y: 1229.0 },
                  { x: 2394.0, y: 1229.0 },
                ],
              },
              inferText: '비빔채소만두',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2995.0, y: 1178.0 },
                  { x: 3208.0, y: 1178.0 },
                  { x: 3208.0, y: 1229.0 },
                  { x: 2995.0, y: 1229.0 },
                ],
              },
              inferText: '쫄면채소무침',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 240.0, y: 1205.0 },
                  { x: 352.0, y: 1205.0 },
                  { x: 352.0, y: 1247.0 },
                  { x: 240.0, y: 1247.0 },
                ],
              },
              inferText: 'B. 일품',
              inferConfidence: 0.9986,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 628.0, y: 1229.0 },
                  { x: 798.0, y: 1229.0 },
                  { x: 798.0, y: 1272.0 },
                  { x: 628.0, y: 1272.0 },
                ],
              },
              inferText: '옥수수빠스',
              inferConfidence: 0.9993,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1238.0, y: 1229.0 },
                  { x: 1375.0, y: 1229.0 },
                  { x: 1375.0, y: 1272.0 },
                  { x: 1238.0, y: 1272.0 },
                ],
              },
              inferText: '알새우칩',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1797.0, y: 1226.0 },
                  { x: 2000.0, y: 1226.0 },
                  { x: 2000.0, y: 1272.0 },
                  { x: 1797.0, y: 1272.0 },
                ],
              },
              inferText: '멕시칸샐러드',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2367.0, y: 1229.0 },
                  { x: 2634.0, y: 1229.0 },
                  { x: 2634.0, y: 1272.0 },
                  { x: 2367.0, y: 1272.0 },
                ],
              },
              inferText: '파프리카버섯볶음',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3001.0, y: 1229.0 },
                  { x: 3202.0, y: 1229.0 },
                  { x: 3202.0, y: 1272.0 },
                  { x: 3001.0, y: 1272.0 },
                ],
              },
              inferText: '만다린샐러드',
              inferConfidence: 0.9963,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 595.0, y: 1272.0 },
                  { x: 832.0, y: 1272.0 },
                  { x: 832.0, y: 1323.0 },
                  { x: 595.0, y: 1323.0 },
                ],
              },
              inferText: '샐러드&드레싱',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1190.0, y: 1272.0 },
                  { x: 1423.0, y: 1272.0 },
                  { x: 1423.0, y: 1323.0 },
                  { x: 1190.0, y: 1323.0 },
                ],
              },
              inferText: '샐러드&드레싱',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1745.0, y: 1272.0 },
                  { x: 2055.0, y: 1272.0 },
                  { x: 2055.0, y: 1323.0 },
                  { x: 1745.0, y: 1323.0 },
                ],
              },
              inferText: '(햄-계육,돈육:국산)',
              inferConfidence: 0.9979,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2394.0, y: 1272.0 },
                  { x: 2607.0, y: 1272.0 },
                  { x: 2607.0, y: 1323.0 },
                  { x: 2394.0, y: 1323.0 },
                ],
              },
              inferText: '열무된장나물',
              inferConfidence: 0.9996,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3032.0, y: 1272.0 },
                  { x: 3171.0, y: 1272.0 },
                  { x: 3171.0, y: 1323.0 },
                  { x: 3032.0, y: 1323.0 },
                ],
              },
              inferText: '&드레싱',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 662.0, y: 1323.0 },
                  { x: 765.0, y: 1323.0 },
                  { x: 765.0, y: 1366.0 },
                  { x: 662.0, y: 1366.0 },
                ],
              },
              inferText: '단무지',
              inferConfidence: 0.9952,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1223.0, y: 1323.0 },
                  { x: 1390.0, y: 1323.0 },
                  { x: 1390.0, y: 1366.0 },
                  { x: 1223.0, y: 1366.0 },
                ],
              },
              inferText: '사각단무지',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1815.0, y: 1323.0 },
                  { x: 1985.0, y: 1323.0 },
                  { x: 1985.0, y: 1366.0 },
                  { x: 1815.0, y: 1366.0 },
                ],
              },
              inferText: '단무지무침',
              inferConfidence: 0.9972,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2446.0, y: 1320.0 },
                  { x: 2549.0, y: 1320.0 },
                  { x: 2549.0, y: 1366.0 },
                  { x: 2446.0, y: 1366.0 },
                ],
              },
              inferText: '깍두기',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3062.0, y: 1317.0 },
                  { x: 3144.0, y: 1317.0 },
                  { x: 3144.0, y: 1369.0 },
                  { x: 3062.0, y: 1369.0 },
                ],
              },
              inferText: '김치',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 628.0, y: 1366.0 },
                  { x: 798.0, y: 1366.0 },
                  { x: 798.0, y: 1411.0 },
                  { x: 628.0, y: 1411.0 },
                ],
              },
              inferText: '*ICE초코*',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1171.0, y: 1369.0 },
                  { x: 1439.0, y: 1369.0 },
                  { x: 1439.0, y: 1411.0 },
                  { x: 1171.0, y: 1411.0 },
                ],
              },
              inferText: '*아이스티(레몬)*',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1806.0, y: 1366.0 },
                  { x: 1994.0, y: 1366.0 },
                  { x: 1994.0, y: 1417.0 },
                  { x: 1806.0, y: 1417.0 },
                ],
              },
              inferText: '*복분자차*',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2440.0, y: 1366.0 },
                  { x: 2561.0, y: 1366.0 },
                  { x: 2561.0, y: 1414.0 },
                  { x: 2440.0, y: 1414.0 },
                ],
              },
              inferText: '*식혜*',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 3029.0, y: 1372.0 },
                  { x: 3174.0, y: 1372.0 },
                  { x: 3174.0, y: 1414.0 },
                  { x: 3029.0, y: 1414.0 },
                ],
              },
              inferText: '*유자차*',
              inferConfidence: 0.989,
              type: 'NORMAL',
              lineBreak: true,
            },
          ],
        },
      ],
      originalFileName: 'image (2) (2)',
      imageType: 'png',
    }
  } catch (err) {
    console.error(err)
    alert('서버 요청 실패')
  }

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
