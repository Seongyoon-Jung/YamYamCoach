<template>
  <div class="container py-5">
    <!-- <input type="file" @change="handleFile" /> -->

    <ImageCropper @cropped="onCroppedFile"></ImageCropper>
    <button v-if="imageFile" class="btn btn-primary" @click="sendToServer">분석 요청</button>

    <div class="table-responsive" style="max-width: 100%">
      <table
        class="table table-bordered mt-4 text-center align-middle"
      >
        <thead class="table-primary">
          <tr>
            <th
              v-for="(day, colIndex) in tableData[0]"
              :key="'head-' + colIndex"
              style="white-space: nowrap; padding: 0.5rem"
            >
              <div class="fw-bold fs-6">{{ day }}</div>
            </th>
          </tr>
          <tr>
            <th
              v-for="(day, colIndex) in tableData[1]"
              :key="'head-' + colIndex"
              style="white-space: nowrap; padding: 0.5rem"
            >
              <div class="fw-bold fs-6">{{ day }}</div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, rowIndex) in tableData.slice(2)" :key="'row-' + rowIndex">
            <td
              v-for="(cell, colIndex) in row"
              :key="'cell-' + colIndex"
              style="padding: 0.25rem; max-width: 140px"
            >
              <input
                type="text"
                v-model="tableData[rowIndex + 2][colIndex]"
                class="form-control border-0 p-0"
                style="
                  width: 100%;
                  min-width: 0;
                  height: auto;
                  resize: none;
                  overflow-wrap: break-word;
                  white-space: pre-line;
                  font-size: 0.85rem;
                  background-color: transparent;
                "
              ></input>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <button v-if="tableData.length" type="button" class="btn btn-primary" @click="save">
      수정완료
    </button>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axios from '@/plugins/axios'
import ImageCropper from '@/components/admin/ImageCropper.vue'

const imageFile = ref(null)
const result = ref(null)

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
      requestId: '608c0788f52b4e1aa72b0a78b958a78c',
      timestamp: 1747703197888,
      images: [
        {
          uid: '7b60309a921246018c5cdaf0611f248c',
          name: 'demo',
          inferResult: 'SUCCESS',
          message: 'SUCCESS',
          validationResult: { result: 'NO_REQUESTED' },
          convertedImageInfo: { width: 2987, height: 1237, pageIndex: 0, longImage: false },
          tables: [
            {
              cells: [
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 224.0, y: 51.0 },
                          { x: 376.0, y: 51.0 },
                          { x: 376.0, y: 88.0 },
                          { x: 224.0, y: 88.0 },
                        ],
                      },
                      inferConfidence: 0.99975,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 224.0, y: 51.0 },
                              { x: 299.0, y: 51.0 },
                              { x: 299.0, y: 88.0 },
                              { x: 224.0, y: 88.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9997,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 309.0, y: 51.0 },
                              { x: 376.0, y: 51.0 },
                              { x: 376.0, y: 88.0 },
                              { x: 309.0, y: 88.0 },
                            ],
                          },
                          inferText: '19일',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 5.0 },
                      { x: 597.0, y: 5.0 },
                      { x: 597.0, y: 128.0 },
                      { x: 7.0, y: 128.0 },
                    ],
                  },
                  inferConfidence: 0.99975,
                  rowSpan: 1,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 253.0, y: 147.0 },
                          { x: 349.0, y: 147.0 },
                          { x: 349.0, y: 189.0 },
                          { x: 253.0, y: 189.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 253.0, y: 147.0 },
                              { x: 349.0, y: 147.0 },
                              { x: 349.0, y: 189.0 },
                              { x: 253.0, y: 189.0 },
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
                      { x: 7.0, y: 128.0 },
                      { x: 597.0, y: 128.0 },
                      { x: 597.0, y: 207.0 },
                      { x: 7.0, y: 207.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 207.0 },
                      { x: 597.0, y: 207.0 },
                      { x: 597.0, y: 274.0 },
                      { x: 7.0, y: 274.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 248.0, y: 277.0 },
                          { x: 352.0, y: 277.0 },
                          { x: 352.0, y: 320.0 },
                          { x: 248.0, y: 320.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 248.0, y: 277.0 },
                              { x: 352.0, y: 277.0 },
                              { x: 352.0, y: 320.0 },
                              { x: 248.0, y: 320.0 },
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
                      { x: 7.0, y: 274.0 },
                      { x: 597.0, y: 274.0 },
                      { x: 597.0, y: 322.0 },
                      { x: 7.0, y: 322.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 3,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 195.0, y: 325.0 },
                          { x: 403.0, y: 325.0 },
                          { x: 403.0, y: 368.0 },
                          { x: 195.0, y: 368.0 },
                        ],
                      },
                      inferConfidence: 0.9985,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 195.0, y: 325.0 },
                              { x: 403.0, y: 325.0 },
                              { x: 403.0, y: 368.0 },
                              { x: 195.0, y: 368.0 },
                            ],
                          },
                          inferText: '(우육:호주산)',
                          inferConfidence: 0.9985,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 322.0 },
                      { x: 597.0, y: 322.0 },
                      { x: 597.0, y: 371.0 },
                      { x: 7.0, y: 371.0 },
                    ],
                  },
                  inferConfidence: 0.9985,
                  rowSpan: 1,
                  rowIndex: 4,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 216.0, y: 371.0 },
                          { x: 387.0, y: 371.0 },
                          { x: 387.0, y: 413.0 },
                          { x: 216.0, y: 413.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 216.0, y: 371.0 },
                              { x: 387.0, y: 371.0 },
                              { x: 387.0, y: 413.0 },
                              { x: 216.0, y: 413.0 },
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
                      { x: 7.0, y: 371.0 },
                      { x: 597.0, y: 371.0 },
                      { x: 597.0, y: 416.0 },
                      { x: 7.0, y: 416.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 5,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 197.0, y: 419.0 },
                          { x: 400.0, y: 419.0 },
                          { x: 400.0, y: 461.0 },
                          { x: 197.0, y: 461.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 197.0, y: 419.0 },
                              { x: 400.0, y: 419.0 },
                              { x: 400.0, y: 461.0 },
                              { x: 197.0, y: 461.0 },
                            ],
                          },
                          inferText: '도토리묵무침',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 416.0 },
                      { x: 597.0, y: 416.0 },
                      { x: 597.0, y: 464.0 },
                      { x: 7.0, y: 464.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 6,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 184.0, y: 464.0 },
                          { x: 416.0, y: 464.0 },
                          { x: 416.0, y: 509.0 },
                          { x: 184.0, y: 509.0 },
                        ],
                      },
                      inferConfidence: 0.9938,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 184.0, y: 464.0 },
                              { x: 416.0, y: 464.0 },
                              { x: 416.0, y: 509.0 },
                              { x: 184.0, y: 509.0 },
                            ],
                          },
                          inferText: '시래기된장지짐',
                          inferConfidence: 0.9938,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 464.0 },
                      { x: 597.0, y: 464.0 },
                      { x: 597.0, y: 513.0 },
                      { x: 7.0, y: 513.0 },
                    ],
                  },
                  inferConfidence: 0.9938,
                  rowSpan: 1,
                  rowIndex: 7,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 216.0, y: 512.0 },
                          { x: 387.0, y: 512.0 },
                          { x: 387.0, y: 555.0 },
                          { x: 216.0, y: 555.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 216.0, y: 512.0 },
                              { x: 387.0, y: 512.0 },
                              { x: 387.0, y: 555.0 },
                              { x: 216.0, y: 555.0 },
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
                      { x: 7.0, y: 513.0 },
                      { x: 597.0, y: 513.0 },
                      { x: 597.0, y: 558.0 },
                      { x: 7.0, y: 558.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 8,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 264.0, y: 560.0 },
                          { x: 336.0, y: 560.0 },
                          { x: 336.0, y: 603.0 },
                          { x: 264.0, y: 603.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 264.0, y: 560.0 },
                              { x: 336.0, y: 560.0 },
                              { x: 336.0, y: 603.0 },
                              { x: 264.0, y: 603.0 },
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
                      { x: 7.0, y: 558.0 },
                      { x: 597.0, y: 558.0 },
                      { x: 597.0, y: 606.0 },
                      { x: 7.0, y: 606.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 219.0, y: 605.0 },
                          { x: 384.0, y: 605.0 },
                          { x: 384.0, y: 648.0 },
                          { x: 219.0, y: 648.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 219.0, y: 605.0 },
                              { x: 384.0, y: 605.0 },
                              { x: 384.0, y: 648.0 },
                              { x: 219.0, y: 648.0 },
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
                      { x: 7.0, y: 606.0 },
                      { x: 597.0, y: 606.0 },
                      { x: 597.0, y: 722.0 },
                      { x: 7.0, y: 722.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 722.0 },
                      { x: 597.0, y: 722.0 },
                      { x: 597.0, y: 785.0 },
                      { x: 7.0, y: 785.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 11,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 200.0, y: 795.0 },
                          { x: 403.0, y: 795.0 },
                          { x: 403.0, y: 837.0 },
                          { x: 200.0, y: 837.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 200.0, y: 795.0 },
                              { x: 403.0, y: 795.0 },
                              { x: 403.0, y: 837.0 },
                              { x: 200.0, y: 837.0 },
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
                      { x: 7.0, y: 785.0 },
                      { x: 597.0, y: 785.0 },
                      { x: 597.0, y: 838.0 },
                      { x: 7.0, y: 838.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 12,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 181.0, y: 840.0 },
                          { x: 419.0, y: 840.0 },
                          { x: 419.0, y: 883.0 },
                          { x: 181.0, y: 883.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 181.0, y: 840.0 },
                              { x: 419.0, y: 840.0 },
                              { x: 419.0, y: 883.0 },
                              { x: 181.0, y: 883.0 },
                            ],
                          },
                          inferText: '(오징어:원양산)',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 838.0 },
                      { x: 597.0, y: 838.0 },
                      { x: 597.0, y: 886.0 },
                      { x: 7.0, y: 886.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 13,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 187.0, y: 888.0 },
                          { x: 413.0, y: 888.0 },
                          { x: 413.0, y: 931.0 },
                          { x: 187.0, y: 931.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 187.0, y: 888.0 },
                              { x: 413.0, y: 888.0 },
                              { x: 413.0, y: 931.0 },
                              { x: 187.0, y: 931.0 },
                            ],
                          },
                          inferText: '쌀밥&후리가케',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 886.0 },
                      { x: 597.0, y: 886.0 },
                      { x: 597.0, y: 935.0 },
                      { x: 7.0, y: 935.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 14,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 216.0, y: 933.0 },
                          { x: 387.0, y: 933.0 },
                          { x: 387.0, y: 976.0 },
                          { x: 216.0, y: 976.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 216.0, y: 933.0 },
                              { x: 387.0, y: 933.0 },
                              { x: 387.0, y: 976.0 },
                              { x: 216.0, y: 976.0 },
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
                      { x: 7.0, y: 935.0 },
                      { x: 597.0, y: 935.0 },
                      { x: 597.0, y: 980.0 },
                      { x: 7.0, y: 980.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 15,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 213.0, y: 981.0 },
                          { x: 387.0, y: 981.0 },
                          { x: 387.0, y: 1024.0 },
                          { x: 213.0, y: 1024.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 213.0, y: 981.0 },
                              { x: 387.0, y: 981.0 },
                              { x: 387.0, y: 1024.0 },
                              { x: 213.0, y: 1024.0 },
                            ],
                          },
                          inferText: '옥수수빠스',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 980.0 },
                      { x: 597.0, y: 980.0 },
                      { x: 597.0, y: 1028.0 },
                      { x: 7.0, y: 1028.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 16,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 187.0, y: 1029.0 },
                          { x: 413.0, y: 1029.0 },
                          { x: 413.0, y: 1072.0 },
                          { x: 187.0, y: 1072.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 187.0, y: 1029.0 },
                              { x: 413.0, y: 1029.0 },
                              { x: 413.0, y: 1072.0 },
                              { x: 187.0, y: 1072.0 },
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
                      { x: 7.0, y: 1028.0 },
                      { x: 597.0, y: 1028.0 },
                      { x: 597.0, y: 1077.0 },
                      { x: 7.0, y: 1077.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 17,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 245.0, y: 1075.0 },
                          { x: 352.0, y: 1075.0 },
                          { x: 352.0, y: 1117.0 },
                          { x: 245.0, y: 1117.0 },
                        ],
                      },
                      inferConfidence: 0.9843,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 245.0, y: 1075.0 },
                              { x: 352.0, y: 1075.0 },
                              { x: 352.0, y: 1117.0 },
                              { x: 245.0, y: 1117.0 },
                            ],
                          },
                          inferText: '단무지',
                          inferConfidence: 0.9843,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 1077.0 },
                      { x: 597.0, y: 1077.0 },
                      { x: 597.0, y: 1121.0 },
                      { x: 7.0, y: 1121.0 },
                    ],
                  },
                  inferConfidence: 0.9843,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 216.0, y: 1120.0 },
                          { x: 384.0, y: 1120.0 },
                          { x: 384.0, y: 1163.0 },
                          { x: 216.0, y: 1163.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 216.0, y: 1120.0 },
                              { x: 384.0, y: 1120.0 },
                              { x: 384.0, y: 1163.0 },
                              { x: 216.0, y: 1163.0 },
                            ],
                          },
                          inferText: '*ICE초코*',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 1121.0 },
                      { x: 597.0, y: 1121.0 },
                      { x: 597.0, y: 1174.0 },
                      { x: 7.0, y: 1174.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 7.0, y: 1174.0 },
                      { x: 597.0, y: 1174.0 },
                      { x: 597.0, y: 1234.0 },
                      { x: 7.0, y: 1234.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 20,
                  columnSpan: 1,
                  columnIndex: 0,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 816.0, y: 51.0 },
                          { x: 973.0, y: 51.0 },
                          { x: 973.0, y: 88.0 },
                          { x: 816.0, y: 88.0 },
                        ],
                      },
                      inferConfidence: 0.99965,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 816.0, y: 51.0 },
                              { x: 891.0, y: 51.0 },
                              { x: 891.0, y: 88.0 },
                              { x: 816.0, y: 88.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9997,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 899.0, y: 51.0 },
                              { x: 973.0, y: 51.0 },
                              { x: 973.0, y: 88.0 },
                              { x: 899.0, y: 88.0 },
                            ],
                          },
                          inferText: '20일',
                          inferConfidence: 0.9996,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 5.0 },
                      { x: 1191.0, y: 5.0 },
                      { x: 1191.0, y: 128.0 },
                      { x: 597.0, y: 128.0 },
                    ],
                  },
                  inferConfidence: 0.99965,
                  rowSpan: 1,
                  rowIndex: 0,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 856.0, y: 147.0 },
                          { x: 933.0, y: 147.0 },
                          { x: 933.0, y: 189.0 },
                          { x: 856.0, y: 189.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 856.0, y: 147.0 },
                              { x: 933.0, y: 147.0 },
                              { x: 933.0, y: 189.0 },
                              { x: 856.0, y: 189.0 },
                            ],
                          },
                          inferText: 'TUE',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 128.0 },
                      { x: 1191.0, y: 128.0 },
                      { x: 1191.0, y: 207.0 },
                      { x: 597.0, y: 207.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 207.0 },
                      { x: 1191.0, y: 207.0 },
                      { x: 1191.0, y: 274.0 },
                      { x: 597.0, y: 274.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 2,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 824.0, y: 277.0 },
                          { x: 960.0, y: 277.0 },
                          { x: 960.0, y: 320.0 },
                          { x: 824.0, y: 320.0 },
                        ],
                      },
                      inferConfidence: 0.9994,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 824.0, y: 277.0 },
                              { x: 960.0, y: 277.0 },
                              { x: 960.0, y: 320.0 },
                              { x: 824.0, y: 320.0 },
                            ],
                          },
                          inferText: '닭매운찜',
                          inferConfidence: 0.9994,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 274.0 },
                      { x: 1191.0, y: 274.0 },
                      { x: 1191.0, y: 322.0 },
                      { x: 597.0, y: 322.0 },
                    ],
                  },
                  inferConfidence: 0.9994,
                  rowSpan: 1,
                  rowIndex: 3,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 773.0, y: 325.0 },
                          { x: 1013.0, y: 325.0 },
                          { x: 1013.0, y: 368.0 },
                          { x: 773.0, y: 368.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 773.0, y: 325.0 },
                              { x: 1013.0, y: 325.0 },
                              { x: 1013.0, y: 368.0 },
                              { x: 773.0, y: 368.0 },
                            ],
                          },
                          inferText: '(계육:브라질산)',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 322.0 },
                      { x: 1191.0, y: 322.0 },
                      { x: 1191.0, y: 371.0 },
                      { x: 597.0, y: 371.0 },
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
                          { x: 800.0, y: 373.0 },
                          { x: 987.0, y: 373.0 },
                          { x: 987.0, y: 413.0 },
                          { x: 800.0, y: 413.0 },
                        ],
                      },
                      inferConfidence: 0.9997,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 800.0, y: 373.0 },
                              { x: 987.0, y: 373.0 },
                              { x: 987.0, y: 413.0 },
                              { x: 800.0, y: 413.0 },
                            ],
                          },
                          inferText: '차조밥/쌀밥',
                          inferConfidence: 0.9997,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 371.0 },
                      { x: 1191.0, y: 371.0 },
                      { x: 1191.0, y: 416.0 },
                      { x: 597.0, y: 416.0 },
                    ],
                  },
                  inferConfidence: 0.9997,
                  rowSpan: 1,
                  rowIndex: 5,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 827.0, y: 419.0 },
                          { x: 963.0, y: 419.0 },
                          { x: 963.0, y: 461.0 },
                          { x: 827.0, y: 461.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 827.0, y: 419.0 },
                              { x: 963.0, y: 419.0 },
                              { x: 963.0, y: 461.0 },
                              { x: 827.0, y: 461.0 },
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
                      { x: 597.0, y: 416.0 },
                      { x: 1191.0, y: 416.0 },
                      { x: 1191.0, y: 464.0 },
                      { x: 597.0, y: 464.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 6,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 840.0, y: 467.0 },
                          { x: 947.0, y: 467.0 },
                          { x: 947.0, y: 509.0 },
                          { x: 840.0, y: 509.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 840.0, y: 467.0 },
                              { x: 947.0, y: 467.0 },
                              { x: 947.0, y: 509.0 },
                              { x: 840.0, y: 509.0 },
                            ],
                          },
                          inferText: '부추전',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 464.0 },
                      { x: 1191.0, y: 464.0 },
                      { x: 1191.0, y: 513.0 },
                      { x: 597.0, y: 513.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 7,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 808.0, y: 512.0 },
                          { x: 979.0, y: 512.0 },
                          { x: 979.0, y: 555.0 },
                          { x: 808.0, y: 555.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 808.0, y: 512.0 },
                              { x: 979.0, y: 512.0 },
                              { x: 979.0, y: 555.0 },
                              { x: 808.0, y: 555.0 },
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
                      { x: 597.0, y: 513.0 },
                      { x: 1191.0, y: 513.0 },
                      { x: 1191.0, y: 558.0 },
                      { x: 597.0, y: 558.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 8,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 859.0, y: 560.0 },
                          { x: 928.0, y: 560.0 },
                          { x: 928.0, y: 603.0 },
                          { x: 859.0, y: 603.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 859.0, y: 560.0 },
                              { x: 928.0, y: 560.0 },
                              { x: 928.0, y: 603.0 },
                              { x: 859.0, y: 603.0 },
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
                      { x: 597.0, y: 558.0 },
                      { x: 1191.0, y: 558.0 },
                      { x: 1191.0, y: 606.0 },
                      { x: 597.0, y: 606.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 763.0, y: 608.0 },
                          { x: 1027.0, y: 608.0 },
                          { x: 1027.0, y: 648.0 },
                          { x: 763.0, y: 648.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 763.0, y: 608.0 },
                              { x: 1027.0, y: 608.0 },
                              { x: 1027.0, y: 648.0 },
                              { x: 763.0, y: 648.0 },
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
                      { x: 597.0, y: 606.0 },
                      { x: 1191.0, y: 606.0 },
                      { x: 1191.0, y: 722.0 },
                      { x: 597.0, y: 722.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 722.0 },
                      { x: 1191.0, y: 722.0 },
                      { x: 1191.0, y: 785.0 },
                      { x: 597.0, y: 785.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 11,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 808.0, y: 792.0 },
                          { x: 979.0, y: 792.0 },
                          { x: 979.0, y: 837.0 },
                          { x: 808.0, y: 837.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 808.0, y: 792.0 },
                              { x: 979.0, y: 792.0 },
                              { x: 979.0, y: 837.0 },
                              { x: 808.0, y: 837.0 },
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
                      { x: 597.0, y: 785.0 },
                      { x: 1191.0, y: 785.0 },
                      { x: 1191.0, y: 838.0 },
                      { x: 597.0, y: 838.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 12,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 701.0, y: 840.0 },
                          { x: 1085.0, y: 840.0 },
                          { x: 1085.0, y: 883.0 },
                          { x: 701.0, y: 883.0 },
                        ],
                      },
                      inferConfidence: 0.9988,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 701.0, y: 840.0 },
                              { x: 1085.0, y: 840.0 },
                              { x: 1085.0, y: 883.0 },
                              { x: 701.0, y: 883.0 },
                            ],
                          },
                          inferText: '(소고기편육-우육:외국산)',
                          inferConfidence: 0.9988,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 838.0 },
                      { x: 1191.0, y: 838.0 },
                      { x: 1191.0, y: 886.0 },
                      { x: 597.0, y: 886.0 },
                    ],
                  },
                  inferConfidence: 0.9988,
                  rowSpan: 1,
                  rowIndex: 13,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 744.0, y: 888.0 },
                          { x: 1043.0, y: 888.0 },
                          { x: 1043.0, y: 931.0 },
                          { x: 744.0, y: 931.0 },
                        ],
                      },
                      inferConfidence: 0.9985,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 744.0, y: 888.0 },
                              { x: 1043.0, y: 888.0 },
                              { x: 1043.0, y: 931.0 },
                              { x: 744.0, y: 931.0 },
                            ],
                          },
                          inferText: '미니파인애플볶음밥',
                          inferConfidence: 0.9985,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 886.0 },
                      { x: 1191.0, y: 886.0 },
                      { x: 1191.0, y: 935.0 },
                      { x: 597.0, y: 935.0 },
                    ],
                  },
                  inferConfidence: 0.9985,
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
                          { x: 808.0, y: 933.0 },
                          { x: 976.0, y: 933.0 },
                          { x: 976.0, y: 976.0 },
                          { x: 808.0, y: 976.0 },
                        ],
                      },
                      inferConfidence: 0.9997,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 808.0, y: 933.0 },
                              { x: 976.0, y: 933.0 },
                              { x: 976.0, y: 976.0 },
                              { x: 808.0, y: 976.0 },
                            ],
                          },
                          inferText: '달콤팥춘권',
                          inferConfidence: 0.9997,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 935.0 },
                      { x: 1191.0, y: 935.0 },
                      { x: 1191.0, y: 980.0 },
                      { x: 597.0, y: 980.0 },
                    ],
                  },
                  inferConfidence: 0.9997,
                  rowSpan: 1,
                  rowIndex: 15,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 824.0, y: 981.0 },
                          { x: 960.0, y: 981.0 },
                          { x: 960.0, y: 1024.0 },
                          { x: 824.0, y: 1024.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 824.0, y: 981.0 },
                              { x: 960.0, y: 981.0 },
                              { x: 960.0, y: 1024.0 },
                              { x: 824.0, y: 1024.0 },
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
                      { x: 597.0, y: 980.0 },
                      { x: 1191.0, y: 980.0 },
                      { x: 1191.0, y: 1028.0 },
                      { x: 597.0, y: 1028.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 16,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 779.0, y: 1029.0 },
                          { x: 1005.0, y: 1029.0 },
                          { x: 1005.0, y: 1072.0 },
                          { x: 779.0, y: 1072.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 779.0, y: 1029.0 },
                              { x: 1005.0, y: 1029.0 },
                              { x: 1005.0, y: 1072.0 },
                              { x: 779.0, y: 1072.0 },
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
                      { x: 597.0, y: 1028.0 },
                      { x: 1191.0, y: 1028.0 },
                      { x: 1191.0, y: 1077.0 },
                      { x: 597.0, y: 1077.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 17,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 808.0, y: 1075.0 },
                          { x: 976.0, y: 1075.0 },
                          { x: 976.0, y: 1117.0 },
                          { x: 808.0, y: 1117.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 808.0, y: 1075.0 },
                              { x: 976.0, y: 1075.0 },
                              { x: 976.0, y: 1117.0 },
                              { x: 808.0, y: 1117.0 },
                            ],
                          },
                          inferText: '사각단무지',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 1077.0 },
                      { x: 1191.0, y: 1077.0 },
                      { x: 1191.0, y: 1121.0 },
                      { x: 597.0, y: 1121.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 763.0, y: 1125.0 },
                          { x: 1027.0, y: 1125.0 },
                          { x: 1027.0, y: 1165.0 },
                          { x: 763.0, y: 1165.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 763.0, y: 1125.0 },
                              { x: 1027.0, y: 1125.0 },
                              { x: 1027.0, y: 1165.0 },
                              { x: 763.0, y: 1165.0 },
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
                      { x: 597.0, y: 1121.0 },
                      { x: 1191.0, y: 1121.0 },
                      { x: 1191.0, y: 1174.0 },
                      { x: 597.0, y: 1174.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 597.0, y: 1174.0 },
                      { x: 1191.0, y: 1174.0 },
                      { x: 1191.0, y: 1234.0 },
                      { x: 597.0, y: 1234.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 20,
                  columnSpan: 1,
                  columnIndex: 1,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1411.0, y: 48.0 },
                          { x: 1563.0, y: 48.0 },
                          { x: 1563.0, y: 88.0 },
                          { x: 1411.0, y: 88.0 },
                        ],
                      },
                      inferConfidence: 0.99975,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1411.0, y: 51.0 },
                              { x: 1488.0, y: 51.0 },
                              { x: 1488.0, y: 88.0 },
                              { x: 1411.0, y: 88.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9997,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1491.0, y: 48.0 },
                              { x: 1563.0, y: 48.0 },
                              { x: 1563.0, y: 88.0 },
                              { x: 1491.0, y: 88.0 },
                            ],
                          },
                          inferText: '21일',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 5.0 },
                      { x: 1784.0, y: 5.0 },
                      { x: 1784.0, y: 128.0 },
                      { x: 1191.0, y: 128.0 },
                    ],
                  },
                  inferConfidence: 0.99975,
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
                          { x: 1445.0, y: 147.0 },
                          { x: 1534.0, y: 147.0 },
                          { x: 1534.0, y: 189.0 },
                          { x: 1445.0, y: 189.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1445.0, y: 147.0 },
                              { x: 1534.0, y: 147.0 },
                              { x: 1534.0, y: 189.0 },
                              { x: 1445.0, y: 189.0 },
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
                      { x: 1191.0, y: 128.0 },
                      { x: 1784.0, y: 128.0 },
                      { x: 1784.0, y: 207.0 },
                      { x: 1191.0, y: 207.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 207.0 },
                      { x: 1784.0, y: 207.0 },
                      { x: 1784.0, y: 274.0 },
                      { x: 1191.0, y: 274.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 1419.0, y: 277.0 },
                          { x: 1552.0, y: 277.0 },
                          { x: 1552.0, y: 320.0 },
                          { x: 1419.0, y: 320.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1419.0, y: 277.0 },
                              { x: 1552.0, y: 277.0 },
                              { x: 1552.0, y: 320.0 },
                              { x: 1419.0, y: 320.0 },
                            ],
                          },
                          inferText: '비지찌개',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 274.0 },
                      { x: 1784.0, y: 274.0 },
                      { x: 1784.0, y: 322.0 },
                      { x: 1191.0, y: 322.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 1395.0, y: 325.0 },
                          { x: 1579.0, y: 325.0 },
                          { x: 1579.0, y: 365.0 },
                          { x: 1395.0, y: 365.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1395.0, y: 325.0 },
                              { x: 1579.0, y: 325.0 },
                              { x: 1579.0, y: 365.0 },
                              { x: 1395.0, y: 365.0 },
                            ],
                          },
                          inferText: '보리밥/쌀밥',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 322.0 },
                      { x: 1784.0, y: 322.0 },
                      { x: 1784.0, y: 371.0 },
                      { x: 1191.0, y: 371.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
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
                          { x: 1339.0, y: 371.0 },
                          { x: 1632.0, y: 371.0 },
                          { x: 1632.0, y: 413.0 },
                          { x: 1339.0, y: 413.0 },
                        ],
                      },
                      inferConfidence: 0.9996,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1339.0, y: 371.0 },
                              { x: 1632.0, y: 371.0 },
                              { x: 1632.0, y: 413.0 },
                              { x: 1339.0, y: 413.0 },
                            ],
                          },
                          inferText: '치킨너겟&머스타드',
                          inferConfidence: 0.9996,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 371.0 },
                      { x: 1784.0, y: 371.0 },
                      { x: 1784.0, y: 416.0 },
                      { x: 1191.0, y: 416.0 },
                    ],
                  },
                  inferConfidence: 0.9996,
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
                          { x: 1309.0, y: 419.0 },
                          { x: 1662.0, y: 419.0 },
                          { x: 1662.0, y: 461.0 },
                          { x: 1309.0, y: 461.0 },
                        ],
                      },
                      inferConfidence: 0.9994,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1309.0, y: 419.0 },
                              { x: 1662.0, y: 419.0 },
                              { x: 1662.0, y: 461.0 },
                              { x: 1309.0, y: 461.0 },
                            ],
                          },
                          inferText: '(치킨너겟-계육:국내산)',
                          inferConfidence: 0.9994,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 416.0 },
                      { x: 1784.0, y: 416.0 },
                      { x: 1784.0, y: 464.0 },
                      { x: 1191.0, y: 464.0 },
                    ],
                  },
                  inferConfidence: 0.9994,
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
                          { x: 1339.0, y: 467.0 },
                          { x: 1632.0, y: 467.0 },
                          { x: 1632.0, y: 507.0 },
                          { x: 1339.0, y: 507.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1339.0, y: 467.0 },
                              { x: 1632.0, y: 467.0 },
                              { x: 1632.0, y: 507.0 },
                              { x: 1339.0, y: 507.0 },
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
                      { x: 1191.0, y: 464.0 },
                      { x: 1784.0, y: 464.0 },
                      { x: 1784.0, y: 513.0 },
                      { x: 1191.0, y: 513.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
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
                          { x: 1277.0, y: 512.0 },
                          { x: 1696.0, y: 512.0 },
                          { x: 1696.0, y: 555.0 },
                          { x: 1277.0, y: 555.0 },
                        ],
                      },
                      inferConfidence: 0.9898,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1277.0, y: 512.0 },
                              { x: 1696.0, y: 512.0 },
                              { x: 1696.0, y: 555.0 },
                              { x: 1277.0, y: 555.0 },
                            ],
                          },
                          inferText: '(참피온소시지-돈육:국내산)',
                          inferConfidence: 0.9898,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 513.0 },
                      { x: 1784.0, y: 513.0 },
                      { x: 1784.0, y: 558.0 },
                      { x: 1191.0, y: 558.0 },
                    ],
                  },
                  inferConfidence: 0.9898,
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
                          { x: 1315.0, y: 560.0 },
                          { x: 1656.0, y: 560.0 },
                          { x: 1656.0, y: 603.0 },
                          { x: 1315.0, y: 603.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1315.0, y: 560.0 },
                              { x: 1656.0, y: 560.0 },
                              { x: 1656.0, y: 603.0 },
                              { x: 1315.0, y: 603.0 },
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
                      { x: 1191.0, y: 558.0 },
                      { x: 1784.0, y: 558.0 },
                      { x: 1784.0, y: 606.0 },
                      { x: 1191.0, y: 606.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1400.0, y: 611.0 },
                          { x: 1574.0, y: 611.0 },
                          { x: 1574.0, y: 651.0 },
                          { x: 1400.0, y: 651.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1400.0, y: 611.0 },
                              { x: 1574.0, y: 611.0 },
                              { x: 1574.0, y: 651.0 },
                              { x: 1400.0, y: 651.0 },
                            ],
                          },
                          inferText: '*복분자차*',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 606.0 },
                      { x: 1784.0, y: 606.0 },
                      { x: 1784.0, y: 722.0 },
                      { x: 1191.0, y: 722.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 722.0 },
                      { x: 1784.0, y: 722.0 },
                      { x: 1784.0, y: 785.0 },
                      { x: 1191.0, y: 785.0 },
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
                          { x: 1403.0, y: 791.0 },
                          { x: 1572.0, y: 794.0 },
                          { x: 1571.0, y: 838.0 },
                          { x: 1402.0, y: 835.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1403.0, y: 791.0 },
                              { x: 1571.0, y: 794.0 },
                              { x: 1571.0, y: 838.0 },
                              { x: 1402.0, y: 835.0 },
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
                      { x: 1191.0, y: 785.0 },
                      { x: 1784.0, y: 785.0 },
                      { x: 1784.0, y: 838.0 },
                      { x: 1191.0, y: 838.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 1304.0, y: 840.0 },
                          { x: 1667.0, y: 840.0 },
                          { x: 1667.0, y: 883.0 },
                          { x: 1304.0, y: 883.0 },
                        ],
                      },
                      inferConfidence: 0.9994,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1304.0, y: 840.0 },
                              { x: 1667.0, y: 840.0 },
                              { x: 1667.0, y: 883.0 },
                              { x: 1304.0, y: 883.0 },
                            ],
                          },
                          inferText: '꼬마김밥&김가루양념밥',
                          inferConfidence: 0.9994,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 838.0 },
                      { x: 1784.0, y: 838.0 },
                      { x: 1784.0, y: 886.0 },
                      { x: 1191.0, y: 886.0 },
                    ],
                  },
                  inferConfidence: 0.9994,
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
                          { x: 1435.0, y: 888.0 },
                          { x: 1539.0, y: 888.0 },
                          { x: 1539.0, y: 931.0 },
                          { x: 1435.0, y: 931.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1435.0, y: 888.0 },
                              { x: 1539.0, y: 888.0 },
                              { x: 1539.0, y: 931.0 },
                              { x: 1435.0, y: 931.0 },
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
                      { x: 1191.0, y: 886.0 },
                      { x: 1784.0, y: 886.0 },
                      { x: 1784.0, y: 935.0 },
                      { x: 1191.0, y: 935.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
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
                          { x: 1352.0, y: 933.0 },
                          { x: 1619.0, y: 933.0 },
                          { x: 1619.0, y: 976.0 },
                          { x: 1352.0, y: 976.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1352.0, y: 933.0 },
                              { x: 1619.0, y: 933.0 },
                              { x: 1619.0, y: 976.0 },
                              { x: 1352.0, y: 976.0 },
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
                      { x: 1191.0, y: 935.0 },
                      { x: 1784.0, y: 935.0 },
                      { x: 1784.0, y: 980.0 },
                      { x: 1191.0, y: 980.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 1387.0, y: 981.0 },
                          { x: 1587.0, y: 981.0 },
                          { x: 1587.0, y: 1024.0 },
                          { x: 1387.0, y: 1024.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1387.0, y: 981.0 },
                              { x: 1587.0, y: 981.0 },
                              { x: 1587.0, y: 1024.0 },
                              { x: 1387.0, y: 1024.0 },
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
                      { x: 1191.0, y: 980.0 },
                      { x: 1784.0, y: 980.0 },
                      { x: 1784.0, y: 1028.0 },
                      { x: 1191.0, y: 1028.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
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
                          { x: 1339.0, y: 1027.0 },
                          { x: 1635.0, y: 1027.0 },
                          { x: 1635.0, y: 1072.0 },
                          { x: 1339.0, y: 1072.0 },
                        ],
                      },
                      inferConfidence: 0.9986,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1339.0, y: 1027.0 },
                              { x: 1635.0, y: 1027.0 },
                              { x: 1635.0, y: 1072.0 },
                              { x: 1339.0, y: 1072.0 },
                            ],
                          },
                          inferText: '(햄-계육,돈육:국산)',
                          inferConfidence: 0.9986,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 1028.0 },
                      { x: 1784.0, y: 1028.0 },
                      { x: 1784.0, y: 1077.0 },
                      { x: 1191.0, y: 1077.0 },
                    ],
                  },
                  inferConfidence: 0.9986,
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
                          { x: 1400.0, y: 1075.0 },
                          { x: 1571.0, y: 1075.0 },
                          { x: 1571.0, y: 1117.0 },
                          { x: 1400.0, y: 1117.0 },
                        ],
                      },
                      inferConfidence: 0.9963,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1400.0, y: 1075.0 },
                              { x: 1571.0, y: 1075.0 },
                              { x: 1571.0, y: 1117.0 },
                              { x: 1400.0, y: 1117.0 },
                            ],
                          },
                          inferText: '단무지무침',
                          inferConfidence: 0.9963,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 1077.0 },
                      { x: 1784.0, y: 1077.0 },
                      { x: 1784.0, y: 1121.0 },
                      { x: 1191.0, y: 1121.0 },
                    ],
                  },
                  inferConfidence: 0.9963,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1400.0, y: 1125.0 },
                          { x: 1576.0, y: 1125.0 },
                          { x: 1576.0, y: 1165.0 },
                          { x: 1400.0, y: 1165.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1400.0, y: 1125.0 },
                              { x: 1576.0, y: 1125.0 },
                              { x: 1576.0, y: 1165.0 },
                              { x: 1400.0, y: 1165.0 },
                            ],
                          },
                          inferText: '*복분자차*',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 1121.0 },
                      { x: 1784.0, y: 1121.0 },
                      { x: 1784.0, y: 1174.0 },
                      { x: 1191.0, y: 1174.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1191.0, y: 1174.0 },
                      { x: 1784.0, y: 1174.0 },
                      { x: 1784.0, y: 1234.0 },
                      { x: 1191.0, y: 1234.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 20,
                  columnSpan: 1,
                  columnIndex: 2,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2011.0, y: 48.0 },
                          { x: 2166.0, y: 48.0 },
                          { x: 2166.0, y: 88.0 },
                          { x: 2011.0, y: 88.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2011.0, y: 51.0 },
                              { x: 2086.0, y: 51.0 },
                              { x: 2086.0, y: 88.0 },
                              { x: 2011.0, y: 88.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9998,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2091.0, y: 48.0 },
                              { x: 2166.0, y: 48.0 },
                              { x: 2166.0, y: 88.0 },
                              { x: 2091.0, y: 88.0 },
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
                      { x: 1784.0, y: 5.0 },
                      { x: 2397.0, y: 5.0 },
                      { x: 2397.0, y: 128.0 },
                      { x: 1784.0, y: 128.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
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
                          { x: 2048.0, y: 147.0 },
                          { x: 2131.0, y: 147.0 },
                          { x: 2131.0, y: 189.0 },
                          { x: 2048.0, y: 189.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2048.0, y: 147.0 },
                              { x: 2131.0, y: 147.0 },
                              { x: 2131.0, y: 189.0 },
                              { x: 2048.0, y: 189.0 },
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
                      { x: 1784.0, y: 128.0 },
                      { x: 2397.0, y: 128.0 },
                      { x: 2397.0, y: 207.0 },
                      { x: 1784.0, y: 207.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 207.0 },
                      { x: 2397.0, y: 207.0 },
                      { x: 2397.0, y: 274.0 },
                      { x: 1784.0, y: 274.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 2038.0, y: 277.0 },
                          { x: 2139.0, y: 277.0 },
                          { x: 2139.0, y: 320.0 },
                          { x: 2038.0, y: 320.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2038.0, y: 277.0 },
                              { x: 2139.0, y: 277.0 },
                              { x: 2139.0, y: 320.0 },
                              { x: 2038.0, y: 320.0 },
                            ],
                          },
                          inferText: '설렁탕',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 274.0 },
                      { x: 2397.0, y: 274.0 },
                      { x: 2397.0, y: 322.0 },
                      { x: 1784.0, y: 322.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 1870.0, y: 325.0 },
                          { x: 2302.0, y: 325.0 },
                          { x: 2302.0, y: 368.0 },
                          { x: 1870.0, y: 368.0 },
                        ],
                      },
                      inferConfidence: 0.999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1870.0, y: 325.0 },
                              { x: 2302.0, y: 325.0 },
                              { x: 2302.0, y: 368.0 },
                              { x: 1870.0, y: 368.0 },
                            ],
                          },
                          inferText: '(우육:호주산,진한사골농축액',
                          inferConfidence: 0.999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 322.0 },
                      { x: 2397.0, y: 322.0 },
                      { x: 2397.0, y: 371.0 },
                      { x: 1784.0, y: 371.0 },
                    ],
                  },
                  inferConfidence: 0.999,
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
                          { x: 1910.0, y: 371.0 },
                          { x: 2264.0, y: 371.0 },
                          { x: 2264.0, y: 413.0 },
                          { x: 1910.0, y: 413.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1910.0, y: 371.0 },
                              { x: 2264.0, y: 371.0 },
                              { x: 2264.0, y: 413.0 },
                              { x: 1910.0, y: 413.0 },
                            ],
                          },
                          inferText: '-한우사골/모둠뼈:국산)',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 371.0 },
                      { x: 2397.0, y: 371.0 },
                      { x: 2397.0, y: 416.0 },
                      { x: 1784.0, y: 416.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
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
                          { x: 1960.0, y: 419.0 },
                          { x: 2214.0, y: 419.0 },
                          { x: 2214.0, y: 461.0 },
                          { x: 1960.0, y: 461.0 },
                        ],
                      },
                      inferConfidence: 0.9996,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1960.0, y: 419.0 },
                              { x: 2214.0, y: 419.0 },
                              { x: 2214.0, y: 461.0 },
                              { x: 1960.0, y: 461.0 },
                            ],
                          },
                          inferText: '혼합잡곡밥/쌀밥',
                          inferConfidence: 0.9996,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 416.0 },
                      { x: 2397.0, y: 416.0 },
                      { x: 2397.0, y: 464.0 },
                      { x: 1784.0, y: 464.0 },
                    ],
                  },
                  inferConfidence: 0.9996,
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
                          { x: 2003.0, y: 467.0 },
                          { x: 2171.0, y: 467.0 },
                          { x: 2171.0, y: 509.0 },
                          { x: 2003.0, y: 509.0 },
                        ],
                      },
                      inferConfidence: 0.9997,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2003.0, y: 467.0 },
                              { x: 2171.0, y: 467.0 },
                              { x: 2171.0, y: 509.0 },
                              { x: 2003.0, y: 509.0 },
                            ],
                          },
                          inferText: '새우완자전',
                          inferConfidence: 0.9997,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 464.0 },
                      { x: 2397.0, y: 464.0 },
                      { x: 2397.0, y: 513.0 },
                      { x: 1784.0, y: 513.0 },
                    ],
                  },
                  inferConfidence: 0.9997,
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
                          { x: 1987.0, y: 512.0 },
                          { x: 2187.0, y: 512.0 },
                          { x: 2187.0, y: 555.0 },
                          { x: 1987.0, y: 555.0 },
                        ],
                      },
                      inferConfidence: 0.9992,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1987.0, y: 512.0 },
                              { x: 2187.0, y: 512.0 },
                              { x: 2187.0, y: 555.0 },
                              { x: 1987.0, y: 555.0 },
                            ],
                          },
                          inferText: '매운감자조림',
                          inferConfidence: 0.9992,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 513.0 },
                      { x: 2397.0, y: 513.0 },
                      { x: 2397.0, y: 558.0 },
                      { x: 1784.0, y: 558.0 },
                    ],
                  },
                  inferConfidence: 0.9992,
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
                          { x: 1987.0, y: 560.0 },
                          { x: 2190.0, y: 560.0 },
                          { x: 2190.0, y: 603.0 },
                          { x: 1987.0, y: 603.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1987.0, y: 560.0 },
                              { x: 2190.0, y: 560.0 },
                              { x: 2190.0, y: 603.0 },
                              { x: 1987.0, y: 603.0 },
                            ],
                          },
                          inferText: '열무된장나물',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 558.0 },
                      { x: 2397.0, y: 558.0 },
                      { x: 2397.0, y: 606.0 },
                      { x: 1784.0, y: 606.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 1979.0, y: 603.0 },
                          { x: 2199.0, y: 611.0 },
                          { x: 2198.0, y: 656.0 },
                          { x: 1977.0, y: 648.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1979.0, y: 603.0 },
                              { x: 2084.0, y: 607.0 },
                              { x: 2082.0, y: 652.0 },
                              { x: 1978.0, y: 648.0 },
                            ],
                          },
                          inferText: '깍두기',
                          inferConfidence: 1.0,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2091.0, y: 611.0 },
                              { x: 2198.0, y: 611.0 },
                              { x: 2198.0, y: 648.0 },
                              { x: 2091.0, y: 648.0 },
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
                      { x: 1784.0, y: 606.0 },
                      { x: 2397.0, y: 606.0 },
                      { x: 2397.0, y: 722.0 },
                      { x: 1784.0, y: 722.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 722.0 },
                      { x: 2397.0, y: 722.0 },
                      { x: 2397.0, y: 785.0 },
                      { x: 1784.0, y: 785.0 },
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
                          { x: 2003.0, y: 792.0 },
                          { x: 2171.0, y: 792.0 },
                          { x: 2171.0, y: 837.0 },
                          { x: 2003.0, y: 837.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2003.0, y: 792.0 },
                              { x: 2171.0, y: 792.0 },
                              { x: 2171.0, y: 837.0 },
                              { x: 2003.0, y: 837.0 },
                            ],
                          },
                          inferText: '카레라이스',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 785.0 },
                      { x: 2397.0, y: 785.0 },
                      { x: 2397.0, y: 838.0 },
                      { x: 1784.0, y: 838.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
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
                          { x: 1984.0, y: 840.0 },
                          { x: 2192.0, y: 840.0 },
                          { x: 2192.0, y: 883.0 },
                          { x: 1984.0, y: 883.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1984.0, y: 840.0 },
                              { x: 2192.0, y: 840.0 },
                              { x: 2192.0, y: 883.0 },
                              { x: 1984.0, y: 883.0 },
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
                      { x: 1784.0, y: 838.0 },
                      { x: 2397.0, y: 838.0 },
                      { x: 2397.0, y: 886.0 },
                      { x: 1784.0, y: 886.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 2035.0, y: 888.0 },
                          { x: 2142.0, y: 888.0 },
                          { x: 2142.0, y: 931.0 },
                          { x: 2035.0, y: 931.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2035.0, y: 888.0 },
                              { x: 2142.0, y: 888.0 },
                              { x: 2142.0, y: 931.0 },
                              { x: 2035.0, y: 931.0 },
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
                      { x: 1784.0, y: 886.0 },
                      { x: 2397.0, y: 886.0 },
                      { x: 2397.0, y: 935.0 },
                      { x: 1784.0, y: 935.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
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
                          { x: 1987.0, y: 933.0 },
                          { x: 2190.0, y: 933.0 },
                          { x: 2190.0, y: 976.0 },
                          { x: 1987.0, y: 976.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1987.0, y: 933.0 },
                              { x: 2190.0, y: 933.0 },
                              { x: 2190.0, y: 976.0 },
                              { x: 1987.0, y: 976.0 },
                            ],
                          },
                          inferText: '비빔채소만두',
                          inferConfidence: 0.9999,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 935.0 },
                      { x: 2397.0, y: 935.0 },
                      { x: 2397.0, y: 980.0 },
                      { x: 1784.0, y: 980.0 },
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
                          { x: 1952.0, y: 981.0 },
                          { x: 2219.0, y: 981.0 },
                          { x: 2219.0, y: 1024.0 },
                          { x: 1952.0, y: 1024.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1952.0, y: 981.0 },
                              { x: 2219.0, y: 981.0 },
                              { x: 2219.0, y: 1024.0 },
                              { x: 1952.0, y: 1024.0 },
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
                      { x: 1784.0, y: 980.0 },
                      { x: 2397.0, y: 980.0 },
                      { x: 2397.0, y: 1028.0 },
                      { x: 1784.0, y: 1028.0 },
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
                          { x: 1987.0, y: 1029.0 },
                          { x: 2190.0, y: 1029.0 },
                          { x: 2190.0, y: 1072.0 },
                          { x: 1987.0, y: 1072.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 1987.0, y: 1029.0 },
                              { x: 2190.0, y: 1029.0 },
                              { x: 2190.0, y: 1072.0 },
                              { x: 1987.0, y: 1072.0 },
                            ],
                          },
                          inferText: '열무된장나물',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 1028.0 },
                      { x: 2397.0, y: 1028.0 },
                      { x: 2397.0, y: 1077.0 },
                      { x: 1784.0, y: 1077.0 },
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
                          { x: 2035.0, y: 1075.0 },
                          { x: 2139.0, y: 1075.0 },
                          { x: 2139.0, y: 1117.0 },
                          { x: 2035.0, y: 1117.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2035.0, y: 1075.0 },
                              { x: 2139.0, y: 1075.0 },
                              { x: 2139.0, y: 1117.0 },
                              { x: 2035.0, y: 1117.0 },
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
                      { x: 1784.0, y: 1077.0 },
                      { x: 2397.0, y: 1077.0 },
                      { x: 2397.0, y: 1121.0 },
                      { x: 1784.0, y: 1121.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2035.0, y: 1125.0 },
                          { x: 2144.0, y: 1125.0 },
                          { x: 2144.0, y: 1163.0 },
                          { x: 2035.0, y: 1163.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2035.0, y: 1125.0 },
                              { x: 2144.0, y: 1125.0 },
                              { x: 2144.0, y: 1163.0 },
                              { x: 2035.0, y: 1163.0 },
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
                      { x: 1784.0, y: 1121.0 },
                      { x: 2397.0, y: 1121.0 },
                      { x: 2397.0, y: 1174.0 },
                      { x: 1784.0, y: 1174.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 1784.0, y: 1174.0 },
                      { x: 2397.0, y: 1174.0 },
                      { x: 2397.0, y: 1234.0 },
                      { x: 1784.0, y: 1234.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 20,
                  columnSpan: 1,
                  columnIndex: 3,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2606.0, y: 45.0 },
                          { x: 2768.0, y: 45.0 },
                          { x: 2768.0, y: 91.0 },
                          { x: 2606.0, y: 91.0 },
                        ],
                      },
                      inferConfidence: 0.99945,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2606.0, y: 45.0 },
                              { x: 2694.0, y: 45.0 },
                              { x: 2694.0, y: 91.0 },
                              { x: 2606.0, y: 91.0 },
                            ],
                          },
                          inferText: '05월',
                          inferConfidence: 0.9991,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2696.0, y: 51.0 },
                              { x: 2768.0, y: 51.0 },
                              { x: 2768.0, y: 88.0 },
                              { x: 2696.0, y: 88.0 },
                            ],
                          },
                          inferText: '23일',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 5.0 },
                      { x: 2983.0, y: 5.0 },
                      { x: 2983.0, y: 128.0 },
                      { x: 2397.0, y: 128.0 },
                    ],
                  },
                  inferConfidence: 0.99945,
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
                          { x: 2662.0, y: 147.0 },
                          { x: 2720.0, y: 147.0 },
                          { x: 2720.0, y: 184.0 },
                          { x: 2662.0, y: 184.0 },
                        ],
                      },
                      inferConfidence: 0.9994,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2662.0, y: 147.0 },
                              { x: 2720.0, y: 147.0 },
                              { x: 2720.0, y: 184.0 },
                              { x: 2662.0, y: 184.0 },
                            ],
                          },
                          inferText: 'FRI',
                          inferConfidence: 0.9994,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 128.0 },
                      { x: 2983.0, y: 128.0 },
                      { x: 2983.0, y: 207.0 },
                      { x: 2397.0, y: 207.0 },
                    ],
                  },
                  inferConfidence: 0.9994,
                  rowSpan: 1,
                  rowIndex: 1,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 207.0 },
                      { x: 2983.0, y: 207.0 },
                      { x: 2983.0, y: 274.0 },
                      { x: 2397.0, y: 274.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 2504.0, y: 277.0 },
                          { x: 2872.0, y: 277.0 },
                          { x: 2872.0, y: 320.0 },
                          { x: 2504.0, y: 320.0 },
                        ],
                      },
                      inferConfidence: 0.9999,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2504.0, y: 277.0 },
                              { x: 2872.0, y: 277.0 },
                              { x: 2872.0, y: 320.0 },
                              { x: 2504.0, y: 320.0 },
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
                      { x: 2397.0, y: 274.0 },
                      { x: 2983.0, y: 274.0 },
                      { x: 2983.0, y: 322.0 },
                      { x: 2397.0, y: 322.0 },
                    ],
                  },
                  inferConfidence: 0.9999,
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
                          { x: 2488.0, y: 325.0 },
                          { x: 2888.0, y: 325.0 },
                          { x: 2888.0, y: 368.0 },
                          { x: 2488.0, y: 368.0 },
                        ],
                      },
                      inferConfidence: 0.99983335,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2488.0, y: 325.0 },
                              { x: 2694.0, y: 325.0 },
                              { x: 2694.0, y: 368.0 },
                              { x: 2488.0, y: 368.0 },
                            ],
                          },
                          inferText: '(돈육:국내산,',
                          inferConfidence: 1.0,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2696.0, y: 325.0 },
                              { x: 2803.0, y: 325.0 },
                              { x: 2803.0, y: 368.0 },
                              { x: 2696.0, y: 368.0 },
                            ],
                          },
                          inferText: '미국산',
                          inferConfidence: 1.0,
                        },
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2806.0, y: 325.0 },
                              { x: 2888.0, y: 325.0 },
                              { x: 2888.0, y: 368.0 },
                              { x: 2806.0, y: 368.0 },
                            ],
                          },
                          inferText: '섞음)',
                          inferConfidence: 0.9995,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 322.0 },
                      { x: 2983.0, y: 322.0 },
                      { x: 2983.0, y: 371.0 },
                      { x: 2397.0, y: 371.0 },
                    ],
                  },
                  inferConfidence: 0.99983335,
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
                          { x: 2595.0, y: 371.0 },
                          { x: 2784.0, y: 371.0 },
                          { x: 2784.0, y: 413.0 },
                          { x: 2595.0, y: 413.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2595.0, y: 371.0 },
                              { x: 2784.0, y: 371.0 },
                              { x: 2784.0, y: 413.0 },
                              { x: 2595.0, y: 413.0 },
                            ],
                          },
                          inferText: '흑미밥/쌀밥',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 371.0 },
                      { x: 2983.0, y: 371.0 },
                      { x: 2983.0, y: 416.0 },
                      { x: 2397.0, y: 416.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
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
                          { x: 2587.0, y: 419.0 },
                          { x: 2792.0, y: 419.0 },
                          { x: 2792.0, y: 461.0 },
                          { x: 2587.0, y: 461.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2587.0, y: 419.0 },
                              { x: 2792.0, y: 419.0 },
                              { x: 2792.0, y: 461.0 },
                              { x: 2587.0, y: 461.0 },
                            ],
                          },
                          inferText: '시금치된장국',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 416.0 },
                      { x: 2983.0, y: 416.0 },
                      { x: 2983.0, y: 464.0 },
                      { x: 2397.0, y: 464.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 2539.0, y: 467.0 },
                          { x: 2840.0, y: 467.0 },
                          { x: 2840.0, y: 509.0 },
                          { x: 2539.0, y: 509.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2539.0, y: 467.0 },
                              { x: 2840.0, y: 467.0 },
                              { x: 2840.0, y: 509.0 },
                              { x: 2539.0, y: 509.0 },
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
                      { x: 2397.0, y: 464.0 },
                      { x: 2983.0, y: 464.0 },
                      { x: 2983.0, y: 513.0 },
                      { x: 2397.0, y: 513.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 2590.0, y: 512.0 },
                          { x: 2787.0, y: 512.0 },
                          { x: 2787.0, y: 555.0 },
                          { x: 2590.0, y: 555.0 },
                        ],
                      },
                      inferConfidence: 0.9981,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2590.0, y: 512.0 },
                              { x: 2787.0, y: 512.0 },
                              { x: 2787.0, y: 555.0 },
                              { x: 2590.0, y: 555.0 },
                            ],
                          },
                          inferText: '쌈무&깻잎쌈',
                          inferConfidence: 0.9981,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 513.0 },
                      { x: 2983.0, y: 513.0 },
                      { x: 2983.0, y: 558.0 },
                      { x: 2397.0, y: 558.0 },
                    ],
                  },
                  inferConfidence: 0.9981,
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
                          { x: 2654.0, y: 560.0 },
                          { x: 2723.0, y: 560.0 },
                          { x: 2723.0, y: 603.0 },
                          { x: 2654.0, y: 603.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2654.0, y: 560.0 },
                              { x: 2723.0, y: 560.0 },
                              { x: 2723.0, y: 603.0 },
                              { x: 2654.0, y: 603.0 },
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
                      { x: 2397.0, y: 558.0 },
                      { x: 2983.0, y: 558.0 },
                      { x: 2983.0, y: 606.0 },
                      { x: 2397.0, y: 606.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 9,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2619.0, y: 611.0 },
                          { x: 2763.0, y: 611.0 },
                          { x: 2763.0, y: 651.0 },
                          { x: 2619.0, y: 651.0 },
                        ],
                      },
                      inferConfidence: 0.9686,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2619.0, y: 611.0 },
                              { x: 2763.0, y: 611.0 },
                              { x: 2763.0, y: 651.0 },
                              { x: 2619.0, y: 651.0 },
                            ],
                          },
                          inferText: '*유자차*',
                          inferConfidence: 0.9686,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 606.0 },
                      { x: 2983.0, y: 606.0 },
                      { x: 2983.0, y: 722.0 },
                      { x: 2397.0, y: 722.0 },
                    ],
                  },
                  inferConfidence: 0.9686,
                  rowSpan: 1,
                  rowIndex: 10,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 722.0 },
                      { x: 2983.0, y: 722.0 },
                      { x: 2983.0, y: 785.0 },
                      { x: 2397.0, y: 785.0 },
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
                          { x: 2560.0, y: 792.0 },
                          { x: 2822.0, y: 792.0 },
                          { x: 2822.0, y: 837.0 },
                          { x: 2560.0, y: 837.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2560.0, y: 792.0 },
                              { x: 2822.0, y: 792.0 },
                              { x: 2822.0, y: 837.0 },
                              { x: 2560.0, y: 837.0 },
                            ],
                          },
                          inferText: '새우볶음밥&케찹',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 785.0 },
                      { x: 2983.0, y: 785.0 },
                      { x: 2983.0, y: 838.0 },
                      { x: 2397.0, y: 838.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 2622.0, y: 840.0 },
                          { x: 2760.0, y: 840.0 },
                          { x: 2760.0, y: 883.0 },
                          { x: 2622.0, y: 883.0 },
                        ],
                      },
                      inferConfidence: 0.9994,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2622.0, y: 840.0 },
                              { x: 2760.0, y: 840.0 },
                              { x: 2760.0, y: 883.0 },
                              { x: 2622.0, y: 883.0 },
                            ],
                          },
                          inferText: '일식장국',
                          inferConfidence: 0.9994,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 838.0 },
                      { x: 2983.0, y: 838.0 },
                      { x: 2983.0, y: 886.0 },
                      { x: 2397.0, y: 886.0 },
                    ],
                  },
                  inferConfidence: 0.9994,
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
                          { x: 2587.0, y: 888.0 },
                          { x: 2790.0, y: 888.0 },
                          { x: 2790.0, y: 931.0 },
                          { x: 2587.0, y: 931.0 },
                        ],
                      },
                      inferConfidence: 0.9998,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2587.0, y: 888.0 },
                              { x: 2790.0, y: 888.0 },
                              { x: 2790.0, y: 931.0 },
                              { x: 2587.0, y: 931.0 },
                            ],
                          },
                          inferText: '연근땅콩강정',
                          inferConfidence: 0.9998,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 886.0 },
                      { x: 2983.0, y: 886.0 },
                      { x: 2983.0, y: 935.0 },
                      { x: 2397.0, y: 935.0 },
                    ],
                  },
                  inferConfidence: 0.9998,
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
                          { x: 2587.0, y: 933.0 },
                          { x: 2790.0, y: 933.0 },
                          { x: 2790.0, y: 976.0 },
                          { x: 2587.0, y: 976.0 },
                        ],
                      },
                      inferConfidence: 0.9997,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2587.0, y: 933.0 },
                              { x: 2790.0, y: 933.0 },
                              { x: 2790.0, y: 976.0 },
                              { x: 2587.0, y: 976.0 },
                            ],
                          },
                          inferText: '쫄면채소무침',
                          inferConfidence: 0.9997,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 935.0 },
                      { x: 2983.0, y: 935.0 },
                      { x: 2983.0, y: 980.0 },
                      { x: 2397.0, y: 980.0 },
                    ],
                  },
                  inferConfidence: 0.9997,
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
                          { x: 2587.0, y: 981.0 },
                          { x: 2790.0, y: 981.0 },
                          { x: 2790.0, y: 1024.0 },
                          { x: 2587.0, y: 1024.0 },
                        ],
                      },
                      inferConfidence: 0.9972,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2587.0, y: 981.0 },
                              { x: 2790.0, y: 981.0 },
                              { x: 2790.0, y: 1024.0 },
                              { x: 2587.0, y: 1024.0 },
                            ],
                          },
                          inferText: '만다린샐러드',
                          inferConfidence: 0.9972,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 980.0 },
                      { x: 2983.0, y: 980.0 },
                      { x: 2983.0, y: 1028.0 },
                      { x: 2397.0, y: 1028.0 },
                    ],
                  },
                  inferConfidence: 0.9972,
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
                          { x: 2624.0, y: 1029.0 },
                          { x: 2752.0, y: 1029.0 },
                          { x: 2752.0, y: 1069.0 },
                          { x: 2624.0, y: 1069.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2624.0, y: 1029.0 },
                              { x: 2752.0, y: 1029.0 },
                              { x: 2752.0, y: 1069.0 },
                              { x: 2624.0, y: 1069.0 },
                            ],
                          },
                          inferText: '&드레싱',
                          inferConfidence: 1.0,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 1028.0 },
                      { x: 2983.0, y: 1028.0 },
                      { x: 2983.0, y: 1077.0 },
                      { x: 2397.0, y: 1077.0 },
                    ],
                  },
                  inferConfidence: 1.0,
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
                          { x: 2654.0, y: 1075.0 },
                          { x: 2726.0, y: 1075.0 },
                          { x: 2726.0, y: 1117.0 },
                          { x: 2654.0, y: 1117.0 },
                        ],
                      },
                      inferConfidence: 1.0,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2654.0, y: 1075.0 },
                              { x: 2726.0, y: 1075.0 },
                              { x: 2726.0, y: 1117.0 },
                              { x: 2654.0, y: 1117.0 },
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
                      { x: 2397.0, y: 1077.0 },
                      { x: 2983.0, y: 1077.0 },
                      { x: 2983.0, y: 1121.0 },
                      { x: 2397.0, y: 1121.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 18,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [
                    {
                      boundingPoly: {
                        vertices: [
                          { x: 2619.0, y: 1125.0 },
                          { x: 2763.0, y: 1125.0 },
                          { x: 2763.0, y: 1165.0 },
                          { x: 2619.0, y: 1165.0 },
                        ],
                      },
                      inferConfidence: 0.9796,
                      cellWords: [
                        {
                          boundingPoly: {
                            vertices: [
                              { x: 2619.0, y: 1125.0 },
                              { x: 2763.0, y: 1125.0 },
                              { x: 2763.0, y: 1165.0 },
                              { x: 2619.0, y: 1165.0 },
                            ],
                          },
                          inferText: '*유자차*',
                          inferConfidence: 0.9796,
                        },
                      ],
                    },
                  ],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 1121.0 },
                      { x: 2983.0, y: 1121.0 },
                      { x: 2983.0, y: 1174.0 },
                      { x: 2397.0, y: 1174.0 },
                    ],
                  },
                  inferConfidence: 0.9796,
                  rowSpan: 1,
                  rowIndex: 19,
                  columnSpan: 1,
                  columnIndex: 4,
                },
                {
                  cellTextLines: [],
                  boundingPoly: {
                    vertices: [
                      { x: 2397.0, y: 1174.0 },
                      { x: 2983.0, y: 1174.0 },
                      { x: 2983.0, y: 1234.0 },
                      { x: 2397.0, y: 1234.0 },
                    ],
                  },
                  inferConfidence: 1.0,
                  rowSpan: 1,
                  rowIndex: 20,
                  columnSpan: 1,
                  columnIndex: 4,
                },
              ],
              boundingPoly: {
                vertices: [
                  { x: 7.0, y: 5.0 },
                  { x: 2983.0, y: 5.0 },
                  { x: 2983.0, y: 1234.0 },
                  { x: 7.0, y: 1234.0 },
                ],
              },
              inferConfidence: 0.99894696,
            },
          ],
          fields: [
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 224.0, y: 51.0 },
                  { x: 299.0, y: 51.0 },
                  { x: 299.0, y: 88.0 },
                  { x: 224.0, y: 88.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 309.0, y: 51.0 },
                  { x: 376.0, y: 51.0 },
                  { x: 376.0, y: 88.0 },
                  { x: 309.0, y: 88.0 },
                ],
              },
              inferText: '19일',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 816.0, y: 51.0 },
                  { x: 891.0, y: 51.0 },
                  { x: 891.0, y: 88.0 },
                  { x: 816.0, y: 88.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 899.0, y: 51.0 },
                  { x: 973.0, y: 51.0 },
                  { x: 973.0, y: 88.0 },
                  { x: 899.0, y: 88.0 },
                ],
              },
              inferText: '20일',
              inferConfidence: 0.9996,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1411.0, y: 51.0 },
                  { x: 1488.0, y: 51.0 },
                  { x: 1488.0, y: 88.0 },
                  { x: 1411.0, y: 88.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1491.0, y: 48.0 },
                  { x: 1563.0, y: 48.0 },
                  { x: 1563.0, y: 88.0 },
                  { x: 1491.0, y: 88.0 },
                ],
              },
              inferText: '21일',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2011.0, y: 51.0 },
                  { x: 2086.0, y: 51.0 },
                  { x: 2086.0, y: 88.0 },
                  { x: 2011.0, y: 88.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2091.0, y: 48.0 },
                  { x: 2166.0, y: 48.0 },
                  { x: 2166.0, y: 88.0 },
                  { x: 2091.0, y: 88.0 },
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
                  { x: 2606.0, y: 45.0 },
                  { x: 2694.0, y: 45.0 },
                  { x: 2694.0, y: 91.0 },
                  { x: 2606.0, y: 91.0 },
                ],
              },
              inferText: '05월',
              inferConfidence: 0.9991,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2696.0, y: 51.0 },
                  { x: 2768.0, y: 51.0 },
                  { x: 2768.0, y: 88.0 },
                  { x: 2696.0, y: 88.0 },
                ],
              },
              inferText: '23일',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 253.0, y: 147.0 },
                  { x: 349.0, y: 147.0 },
                  { x: 349.0, y: 189.0 },
                  { x: 253.0, y: 189.0 },
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
                  { x: 856.0, y: 147.0 },
                  { x: 933.0, y: 147.0 },
                  { x: 933.0, y: 189.0 },
                  { x: 856.0, y: 189.0 },
                ],
              },
              inferText: 'TUE',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1445.0, y: 147.0 },
                  { x: 1534.0, y: 147.0 },
                  { x: 1534.0, y: 189.0 },
                  { x: 1445.0, y: 189.0 },
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
                  { x: 2048.0, y: 147.0 },
                  { x: 2131.0, y: 147.0 },
                  { x: 2131.0, y: 189.0 },
                  { x: 2048.0, y: 189.0 },
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
                  { x: 2662.0, y: 147.0 },
                  { x: 2720.0, y: 147.0 },
                  { x: 2720.0, y: 184.0 },
                  { x: 2662.0, y: 184.0 },
                ],
              },
              inferText: 'FRI',
              inferConfidence: 0.9994,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 248.0, y: 277.0 },
                  { x: 352.0, y: 277.0 },
                  { x: 352.0, y: 320.0 },
                  { x: 248.0, y: 320.0 },
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
                  { x: 824.0, y: 277.0 },
                  { x: 960.0, y: 277.0 },
                  { x: 960.0, y: 320.0 },
                  { x: 824.0, y: 320.0 },
                ],
              },
              inferText: '닭매운찜',
              inferConfidence: 0.9994,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1419.0, y: 277.0 },
                  { x: 1552.0, y: 277.0 },
                  { x: 1552.0, y: 320.0 },
                  { x: 1419.0, y: 320.0 },
                ],
              },
              inferText: '비지찌개',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2038.0, y: 277.0 },
                  { x: 2139.0, y: 277.0 },
                  { x: 2139.0, y: 320.0 },
                  { x: 2038.0, y: 320.0 },
                ],
              },
              inferText: '설렁탕',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2504.0, y: 277.0 },
                  { x: 2872.0, y: 277.0 },
                  { x: 2872.0, y: 320.0 },
                  { x: 2504.0, y: 320.0 },
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
                  { x: 195.0, y: 325.0 },
                  { x: 403.0, y: 325.0 },
                  { x: 403.0, y: 368.0 },
                  { x: 195.0, y: 368.0 },
                ],
              },
              inferText: '(우육:호주산)',
              inferConfidence: 0.9985,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 773.0, y: 325.0 },
                  { x: 1013.0, y: 325.0 },
                  { x: 1013.0, y: 368.0 },
                  { x: 773.0, y: 368.0 },
                ],
              },
              inferText: '(계육:브라질산)',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1395.0, y: 325.0 },
                  { x: 1579.0, y: 325.0 },
                  { x: 1579.0, y: 365.0 },
                  { x: 1395.0, y: 365.0 },
                ],
              },
              inferText: '보리밥/쌀밥',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1870.0, y: 325.0 },
                  { x: 2302.0, y: 325.0 },
                  { x: 2302.0, y: 368.0 },
                  { x: 1870.0, y: 368.0 },
                ],
              },
              inferText: '(우육:호주산,진한사골농축액',
              inferConfidence: 0.999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2488.0, y: 325.0 },
                  { x: 2694.0, y: 325.0 },
                  { x: 2694.0, y: 368.0 },
                  { x: 2488.0, y: 368.0 },
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
                  { x: 2696.0, y: 325.0 },
                  { x: 2803.0, y: 325.0 },
                  { x: 2803.0, y: 368.0 },
                  { x: 2696.0, y: 368.0 },
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
                  { x: 2806.0, y: 325.0 },
                  { x: 2888.0, y: 325.0 },
                  { x: 2888.0, y: 368.0 },
                  { x: 2806.0, y: 368.0 },
                ],
              },
              inferText: '섞음)',
              inferConfidence: 0.9995,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 216.0, y: 371.0 },
                  { x: 387.0, y: 371.0 },
                  { x: 387.0, y: 413.0 },
                  { x: 216.0, y: 413.0 },
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
                  { x: 800.0, y: 373.0 },
                  { x: 987.0, y: 373.0 },
                  { x: 987.0, y: 413.0 },
                  { x: 800.0, y: 413.0 },
                ],
              },
              inferText: '차조밥/쌀밥',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1339.0, y: 371.0 },
                  { x: 1632.0, y: 371.0 },
                  { x: 1632.0, y: 413.0 },
                  { x: 1339.0, y: 413.0 },
                ],
              },
              inferText: '치킨너겟&머스타드',
              inferConfidence: 0.9996,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1910.0, y: 371.0 },
                  { x: 2264.0, y: 371.0 },
                  { x: 2264.0, y: 413.0 },
                  { x: 1910.0, y: 413.0 },
                ],
              },
              inferText: '-한우사골/모둠뼈:국산)',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2595.0, y: 371.0 },
                  { x: 2784.0, y: 371.0 },
                  { x: 2784.0, y: 413.0 },
                  { x: 2595.0, y: 413.0 },
                ],
              },
              inferText: '흑미밥/쌀밥',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 197.0, y: 419.0 },
                  { x: 400.0, y: 419.0 },
                  { x: 400.0, y: 461.0 },
                  { x: 197.0, y: 461.0 },
                ],
              },
              inferText: '도토리묵무침',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 827.0, y: 419.0 },
                  { x: 963.0, y: 419.0 },
                  { x: 963.0, y: 461.0 },
                  { x: 827.0, y: 461.0 },
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
                  { x: 1309.0, y: 419.0 },
                  { x: 1662.0, y: 419.0 },
                  { x: 1662.0, y: 461.0 },
                  { x: 1309.0, y: 461.0 },
                ],
              },
              inferText: '(치킨너겟-계육:국내산)',
              inferConfidence: 0.9994,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1960.0, y: 419.0 },
                  { x: 2214.0, y: 419.0 },
                  { x: 2214.0, y: 461.0 },
                  { x: 1960.0, y: 461.0 },
                ],
              },
              inferText: '혼합잡곡밥/쌀밥',
              inferConfidence: 0.9996,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2587.0, y: 419.0 },
                  { x: 2792.0, y: 419.0 },
                  { x: 2792.0, y: 461.0 },
                  { x: 2587.0, y: 461.0 },
                ],
              },
              inferText: '시금치된장국',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 184.0, y: 464.0 },
                  { x: 416.0, y: 464.0 },
                  { x: 416.0, y: 509.0 },
                  { x: 184.0, y: 509.0 },
                ],
              },
              inferText: '시래기된장지짐',
              inferConfidence: 0.9938,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 840.0, y: 467.0 },
                  { x: 947.0, y: 467.0 },
                  { x: 947.0, y: 509.0 },
                  { x: 840.0, y: 509.0 },
                ],
              },
              inferText: '부추전',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1339.0, y: 467.0 },
                  { x: 1632.0, y: 467.0 },
                  { x: 1632.0, y: 507.0 },
                  { x: 1339.0, y: 507.0 },
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
                  { x: 2003.0, y: 467.0 },
                  { x: 2171.0, y: 467.0 },
                  { x: 2171.0, y: 509.0 },
                  { x: 2003.0, y: 509.0 },
                ],
              },
              inferText: '새우완자전',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2539.0, y: 467.0 },
                  { x: 2840.0, y: 467.0 },
                  { x: 2840.0, y: 509.0 },
                  { x: 2539.0, y: 509.0 },
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
                  { x: 216.0, y: 512.0 },
                  { x: 387.0, y: 512.0 },
                  { x: 387.0, y: 555.0 },
                  { x: 216.0, y: 555.0 },
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
                  { x: 808.0, y: 512.0 },
                  { x: 979.0, y: 512.0 },
                  { x: 979.0, y: 555.0 },
                  { x: 808.0, y: 555.0 },
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
                  { x: 1277.0, y: 512.0 },
                  { x: 1696.0, y: 512.0 },
                  { x: 1696.0, y: 555.0 },
                  { x: 1277.0, y: 555.0 },
                ],
              },
              inferText: '(참피온소시지-돈육:국내산)',
              inferConfidence: 0.9898,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1987.0, y: 512.0 },
                  { x: 2187.0, y: 512.0 },
                  { x: 2187.0, y: 555.0 },
                  { x: 1987.0, y: 555.0 },
                ],
              },
              inferText: '매운감자조림',
              inferConfidence: 0.9992,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2590.0, y: 512.0 },
                  { x: 2787.0, y: 512.0 },
                  { x: 2787.0, y: 555.0 },
                  { x: 2590.0, y: 555.0 },
                ],
              },
              inferText: '쌈무&깻잎쌈',
              inferConfidence: 0.9981,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 264.0, y: 560.0 },
                  { x: 336.0, y: 560.0 },
                  { x: 336.0, y: 603.0 },
                  { x: 264.0, y: 603.0 },
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
                  { x: 859.0, y: 560.0 },
                  { x: 928.0, y: 560.0 },
                  { x: 928.0, y: 603.0 },
                  { x: 859.0, y: 603.0 },
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
                  { x: 1315.0, y: 560.0 },
                  { x: 1656.0, y: 560.0 },
                  { x: 1656.0, y: 603.0 },
                  { x: 1315.0, y: 603.0 },
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
                  { x: 1987.0, y: 560.0 },
                  { x: 2190.0, y: 560.0 },
                  { x: 2190.0, y: 603.0 },
                  { x: 1987.0, y: 603.0 },
                ],
              },
              inferText: '열무된장나물',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2654.0, y: 560.0 },
                  { x: 2723.0, y: 560.0 },
                  { x: 2723.0, y: 603.0 },
                  { x: 2654.0, y: 603.0 },
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
                  { x: 219.0, y: 605.0 },
                  { x: 384.0, y: 605.0 },
                  { x: 384.0, y: 648.0 },
                  { x: 219.0, y: 648.0 },
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
                  { x: 763.0, y: 608.0 },
                  { x: 1027.0, y: 608.0 },
                  { x: 1027.0, y: 648.0 },
                  { x: 763.0, y: 648.0 },
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
                  { x: 1400.0, y: 611.0 },
                  { x: 1574.0, y: 611.0 },
                  { x: 1574.0, y: 651.0 },
                  { x: 1400.0, y: 651.0 },
                ],
              },
              inferText: '*복분자차*',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1979.0, y: 603.0 },
                  { x: 2084.0, y: 607.0 },
                  { x: 2082.0, y: 652.0 },
                  { x: 1978.0, y: 648.0 },
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
                  { x: 2091.0, y: 611.0 },
                  { x: 2198.0, y: 611.0 },
                  { x: 2198.0, y: 648.0 },
                  { x: 2091.0, y: 648.0 },
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
                  { x: 2619.0, y: 611.0 },
                  { x: 2763.0, y: 611.0 },
                  { x: 2763.0, y: 651.0 },
                  { x: 2619.0, y: 651.0 },
                ],
              },
              inferText: '*유자차*',
              inferConfidence: 0.9686,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 200.0, y: 795.0 },
                  { x: 403.0, y: 795.0 },
                  { x: 403.0, y: 837.0 },
                  { x: 200.0, y: 837.0 },
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
                  { x: 808.0, y: 792.0 },
                  { x: 979.0, y: 792.0 },
                  { x: 979.0, y: 837.0 },
                  { x: 808.0, y: 837.0 },
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
                  { x: 1403.0, y: 791.0 },
                  { x: 1571.0, y: 794.0 },
                  { x: 1571.0, y: 838.0 },
                  { x: 1402.0, y: 835.0 },
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
                  { x: 2003.0, y: 792.0 },
                  { x: 2171.0, y: 792.0 },
                  { x: 2171.0, y: 837.0 },
                  { x: 2003.0, y: 837.0 },
                ],
              },
              inferText: '카레라이스',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2560.0, y: 792.0 },
                  { x: 2822.0, y: 792.0 },
                  { x: 2822.0, y: 837.0 },
                  { x: 2560.0, y: 837.0 },
                ],
              },
              inferText: '새우볶음밥&케찹',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 181.0, y: 840.0 },
                  { x: 419.0, y: 840.0 },
                  { x: 419.0, y: 883.0 },
                  { x: 181.0, y: 883.0 },
                ],
              },
              inferText: '(오징어:원양산)',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 701.0, y: 840.0 },
                  { x: 1085.0, y: 840.0 },
                  { x: 1085.0, y: 883.0 },
                  { x: 701.0, y: 883.0 },
                ],
              },
              inferText: '(소고기편육-우육:외국산)',
              inferConfidence: 0.9988,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1304.0, y: 840.0 },
                  { x: 1667.0, y: 840.0 },
                  { x: 1667.0, y: 883.0 },
                  { x: 1304.0, y: 883.0 },
                ],
              },
              inferText: '꼬마김밥&김가루양념밥',
              inferConfidence: 0.9994,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1984.0, y: 840.0 },
                  { x: 2192.0, y: 840.0 },
                  { x: 2192.0, y: 883.0 },
                  { x: 1984.0, y: 883.0 },
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
                  { x: 2622.0, y: 840.0 },
                  { x: 2760.0, y: 840.0 },
                  { x: 2760.0, y: 883.0 },
                  { x: 2622.0, y: 883.0 },
                ],
              },
              inferText: '일식장국',
              inferConfidence: 0.9994,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 187.0, y: 888.0 },
                  { x: 413.0, y: 888.0 },
                  { x: 413.0, y: 931.0 },
                  { x: 187.0, y: 931.0 },
                ],
              },
              inferText: '쌀밥&후리가케',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 744.0, y: 888.0 },
                  { x: 1043.0, y: 888.0 },
                  { x: 1043.0, y: 931.0 },
                  { x: 744.0, y: 931.0 },
                ],
              },
              inferText: '미니파인애플볶음밥',
              inferConfidence: 0.9985,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1435.0, y: 888.0 },
                  { x: 1539.0, y: 888.0 },
                  { x: 1539.0, y: 931.0 },
                  { x: 1435.0, y: 931.0 },
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
                  { x: 2035.0, y: 888.0 },
                  { x: 2142.0, y: 888.0 },
                  { x: 2142.0, y: 931.0 },
                  { x: 2035.0, y: 931.0 },
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
                  { x: 2587.0, y: 888.0 },
                  { x: 2790.0, y: 888.0 },
                  { x: 2790.0, y: 931.0 },
                  { x: 2587.0, y: 931.0 },
                ],
              },
              inferText: '연근땅콩강정',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 216.0, y: 933.0 },
                  { x: 387.0, y: 933.0 },
                  { x: 387.0, y: 976.0 },
                  { x: 216.0, y: 976.0 },
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
                  { x: 808.0, y: 933.0 },
                  { x: 976.0, y: 933.0 },
                  { x: 976.0, y: 976.0 },
                  { x: 808.0, y: 976.0 },
                ],
              },
              inferText: '달콤팥춘권',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1352.0, y: 933.0 },
                  { x: 1619.0, y: 933.0 },
                  { x: 1619.0, y: 976.0 },
                  { x: 1352.0, y: 976.0 },
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
                  { x: 1987.0, y: 933.0 },
                  { x: 2190.0, y: 933.0 },
                  { x: 2190.0, y: 976.0 },
                  { x: 1987.0, y: 976.0 },
                ],
              },
              inferText: '비빔채소만두',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2587.0, y: 933.0 },
                  { x: 2790.0, y: 933.0 },
                  { x: 2790.0, y: 976.0 },
                  { x: 2587.0, y: 976.0 },
                ],
              },
              inferText: '쫄면채소무침',
              inferConfidence: 0.9997,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 213.0, y: 981.0 },
                  { x: 387.0, y: 981.0 },
                  { x: 387.0, y: 1024.0 },
                  { x: 213.0, y: 1024.0 },
                ],
              },
              inferText: '옥수수빠스',
              inferConfidence: 0.9999,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 824.0, y: 981.0 },
                  { x: 960.0, y: 981.0 },
                  { x: 960.0, y: 1024.0 },
                  { x: 824.0, y: 1024.0 },
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
                  { x: 1387.0, y: 981.0 },
                  { x: 1587.0, y: 981.0 },
                  { x: 1587.0, y: 1024.0 },
                  { x: 1387.0, y: 1024.0 },
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
                  { x: 1952.0, y: 981.0 },
                  { x: 2219.0, y: 981.0 },
                  { x: 2219.0, y: 1024.0 },
                  { x: 1952.0, y: 1024.0 },
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
                  { x: 2587.0, y: 981.0 },
                  { x: 2790.0, y: 981.0 },
                  { x: 2790.0, y: 1024.0 },
                  { x: 2587.0, y: 1024.0 },
                ],
              },
              inferText: '만다린샐러드',
              inferConfidence: 0.9972,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 187.0, y: 1029.0 },
                  { x: 413.0, y: 1029.0 },
                  { x: 413.0, y: 1072.0 },
                  { x: 187.0, y: 1072.0 },
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
                  { x: 779.0, y: 1029.0 },
                  { x: 1005.0, y: 1029.0 },
                  { x: 1005.0, y: 1072.0 },
                  { x: 779.0, y: 1072.0 },
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
                  { x: 1339.0, y: 1027.0 },
                  { x: 1635.0, y: 1027.0 },
                  { x: 1635.0, y: 1072.0 },
                  { x: 1339.0, y: 1072.0 },
                ],
              },
              inferText: '(햄-계육,돈육:국산)',
              inferConfidence: 0.9986,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1987.0, y: 1029.0 },
                  { x: 2190.0, y: 1029.0 },
                  { x: 2190.0, y: 1072.0 },
                  { x: 1987.0, y: 1072.0 },
                ],
              },
              inferText: '열무된장나물',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2624.0, y: 1029.0 },
                  { x: 2752.0, y: 1029.0 },
                  { x: 2752.0, y: 1069.0 },
                  { x: 2624.0, y: 1069.0 },
                ],
              },
              inferText: '&드레싱',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: true,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 245.0, y: 1075.0 },
                  { x: 352.0, y: 1075.0 },
                  { x: 352.0, y: 1117.0 },
                  { x: 245.0, y: 1117.0 },
                ],
              },
              inferText: '단무지',
              inferConfidence: 0.9843,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 808.0, y: 1075.0 },
                  { x: 976.0, y: 1075.0 },
                  { x: 976.0, y: 1117.0 },
                  { x: 808.0, y: 1117.0 },
                ],
              },
              inferText: '사각단무지',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 1400.0, y: 1075.0 },
                  { x: 1571.0, y: 1075.0 },
                  { x: 1571.0, y: 1117.0 },
                  { x: 1400.0, y: 1117.0 },
                ],
              },
              inferText: '단무지무침',
              inferConfidence: 0.9963,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2035.0, y: 1075.0 },
                  { x: 2139.0, y: 1075.0 },
                  { x: 2139.0, y: 1117.0 },
                  { x: 2035.0, y: 1117.0 },
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
                  { x: 2654.0, y: 1075.0 },
                  { x: 2726.0, y: 1075.0 },
                  { x: 2726.0, y: 1117.0 },
                  { x: 2654.0, y: 1117.0 },
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
                  { x: 216.0, y: 1120.0 },
                  { x: 384.0, y: 1120.0 },
                  { x: 384.0, y: 1163.0 },
                  { x: 216.0, y: 1163.0 },
                ],
              },
              inferText: '*ICE초코*',
              inferConfidence: 0.9998,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 763.0, y: 1125.0 },
                  { x: 1027.0, y: 1125.0 },
                  { x: 1027.0, y: 1165.0 },
                  { x: 763.0, y: 1165.0 },
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
                  { x: 1400.0, y: 1125.0 },
                  { x: 1576.0, y: 1125.0 },
                  { x: 1576.0, y: 1165.0 },
                  { x: 1400.0, y: 1165.0 },
                ],
              },
              inferText: '*복분자차*',
              inferConfidence: 1.0,
              type: 'NORMAL',
              lineBreak: false,
            },
            {
              valueType: 'ALL',
              boundingPoly: {
                vertices: [
                  { x: 2035.0, y: 1125.0 },
                  { x: 2144.0, y: 1125.0 },
                  { x: 2144.0, y: 1163.0 },
                  { x: 2035.0, y: 1163.0 },
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
                  { x: 2619.0, y: 1125.0 },
                  { x: 2763.0, y: 1125.0 },
                  { x: 2763.0, y: 1165.0 },
                  { x: 2619.0, y: 1165.0 },
                ],
              },
              inferText: '*유자차*',
              inferConfidence: 0.9796,
              type: 'NORMAL',
              lineBreak: true,
            },
          ],
        },
      ],
      originalFileName: 'crop',
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
  return table
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

// (옵션) 수정된 값을 백으로 보내려면 formData를 순회해서 POST
async function saveEdited() {
  // 예시: formData 전체를 서버로 전송
  try {
    // await axios.post('/api/course_schedule/bulk', { data: formData.value })
    alert('저장 완료!')
  } catch {
    alert('저장 실패')
  }
}

async function save() {
  const transposed = transpose(tableData.value)

  // for (let i = 0; i < tableData.value.length; i++) {
  //   console.log(tableData.value[i])
  // }

  let post = {
    diet: [],
  }

  for (let i = 0; i < transposed.length; i++) {
    if (transposed[i][3] == '') {
      let data = {
        date: '',
        course: '',
        food: [],
      }

      data.date = parseKoreanDate(transposed[i][0])
      data.course = 'A'
      for (let j = 3; j < transposed[i].length; j++) {
        if (transposed[i][j] != '' || !/[()]/.test(transposed[i][j])) {
          if (transposed[i][j].startsWith('&')) {
            let str = data.food.pop() + transposed[i][j]
            data.food.push(str)
          } else {
            data.food.push(...transposed[i][j].split('*').filter((s) => s !== ''))
          }
        }
      }

      post.diet.push(data)
    } else {
      let data = {
        date: '',
        course: '',
        food: [],
      }
      data.date = parseKoreanDate(transposed[i][0])
      data.course = 'A'
      let j = 3
      while (transposed[i][j] != '') {
        if (!/[()]/.test(transposed[i][j])) {
          if (transposed[i][j].startsWith('&')) {
            let str = data.food.pop() + transposed[i][j]
            data.food.push(str)
          } else {
            data.food.push(...transposed[i][j].split('*').filter((s) => s !== ''))
          }
        }
        j++
      }
      post.diet.push(data)

      // B코스
      data = {
        date: '',
        course: '',
        food: [],
      }
      j++
      data.date = parseKoreanDate(transposed[i][0])
      data.course = 'B'
      while (transposed[i][j] != '') {
        if (!/[()]/.test(transposed[i][j])) {
          if (transposed[i][j].startsWith('&')) {
            let str = data.food.pop() + transposed[i][j]
            data.food.push(str)
          } else {
            data.food.push(...transposed[i][j].split('*').filter((s) => s !== ''))
          }
        }
        j++
      }
      post.diet.push(data)
    }
  }

  for (let i = 0; i < post.diet.length; i++) {
    console.log(post.diet[i])
  }

  console.log(post)

  try {
    const res = await axios.post('/api/foods', post)
  } catch (err) {}
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

</script>
