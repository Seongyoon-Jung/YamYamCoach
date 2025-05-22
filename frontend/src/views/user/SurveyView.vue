<template>
  <main class="survey-page">
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <!-- 1) 인트로 화면 -->
          <div v-if="showIntro" class="card p-4 shadow-sm text-center">
            <h3 class="text-primary mb-3">🍽️ 냠냠코치 식습관 설문조사 🍽️</h3>
            <p>
              당신의 식습관을 흥미진진하게 파헤쳐 줄<br />
              <strong>‘냠냠코치 식습관 분석’</strong>에 초대합니다!<br />
              질문에 솔직하게 답하면 나만의 맞춤 리포트를 드려요.<br /><br />
              ※주의 ) <u>건너뛰기</u> 시엔 나이·성별 평균값 기반 분석이 제공되어<br />
              정확도가 떨어질 수 있으니 주의하세요!<br /><br />
              🎉 특별 이벤트: 참여만 해도 <strong>냠냠코인 10,000개</strong>를 드립니다!
            </p>
            <div class="mt-4">
              <button class="btn btn-success me-2" @click="startSurvey">설문 시작하기</button>
              <router-link to="/" class="btn btn-outline-secondary"> 건너뛰기 </router-link>
            </div>
          </div>

          <!-- 3) 실제 설문 화면 -->
          <div v-else-if="showSurvey" class="card p-4 shadow-sm">
            <h3 class="text-center text-primary mb-4">식습관 분석</h3>

            <!-- 스텝 인디케이터 -->
            <div class="d-flex justify-content-center mb-4">
              <span
                v-for="(q, i) in questions"
                :key="i"
                class="step mx-1"
                :class="{ active: currentStep === i, finish: i < currentStep }"
                >{{ i + 1 }}</span
              >
            </div>

            <!-- 질문 탭 -->
            <div v-for="(q, i) in questions" :key="i" v-show="currentStep === i" class="tab">
              <h5>{{ q.questionText }}</h5>
              <div class="mt-3 d-flex justify-content-between">
                <div class="form-check form-check-inline" v-for="(opt, idx) in options" :key="idx">
                  <input
                    class="form-check-input"
                    type="radio"
                    :name="'q' + i"
                    :id="'q' + i + '-opt' + idx"
                    :value="idx"
                    v-model="answers[i]"
                  />
                  <label class="form-check-label" :for="'q' + i + '-opt' + idx">{{ opt }}</label>
                </div>
              </div>
            </div>

            <!-- 네비게이션 버튼 -->
            <div class="d-flex justify-content-between mt-4">
              <button class="btn btn-secondary" @click="prevStep">이전</button>
              <button class="btn btn-primary" @click="isLast ? submitSurvey() : nextStep()">
                {{ isLast ? '제출' : '다음' }}
              </button>
            </div>

            <!-- 완료 메시지
              <div v-if="finished" class="text-center mt-4">
                <h4 class="text-success">설문이 완료되었습니다!</h4>
                <p>소중한 응답 감사합니다.</p>
              </div> -->
          </div>

          <!-- 4) 결과 화면 -->
          <div v-else class="card p-4 shadow-sm text-center result-card">
            <!-- 결과 타입 -->
            <h3 class="text-primary mb-2">{{ results[dietType - 1].name }}</h3>

            <!-- 간단 설명 (dietType별로 다른 설명을 data나 computed로 관리해도 좋아요) -->
            <p class="mb-4 text-muted">
              {{ results[dietType - 1].description }}
            </p>

            <!-- 이미지: 캐릭터 일러스트 -->
            <img
              :src="`https://yamyamcoach.s3.ap-northeast-2.amazonaws.com/uploads/user/${dietType}.png`"
              alt="결과 캐릭터"
              class="result-img mb-3 mx-auto d-block"
              width="50%;"
            />

            <!-- 메인으로 돌아가기 버튼 -->
            <router-link to="/" class="btn btn-outline-secondary"> 메인으로 돌아가기 </router-link>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'

const router = useRouter()
const accountStore = userAccountStore()

const showIntro = ref(true)
const showSurvey = ref(false)
const currentStep = ref(0)
const questions = ref([])
const answers = ref(Array(20).fill(null))

const options = ['전혀 그렇지 않다', '그렇지 않다', '보통이다', '그렇다', '매우 그렇다']

const dietType = ref(null)
const results = ref([])

const descriptions = ref({})

const isFirst = computed(() => currentStep.value === 0)
const isLast = computed(() => currentStep.value === questions.value.length - 1)

onMounted(async () => {
  try {
    const res = await axios.get('/api/survey/questions', {
      params: { stepLevel: 1 },
    })
    questions.value = res.data
  } catch (e) {
    console.error(e)
  }

  try {
    const res = await axios.get('/api/survey/results')
    results.value = res.data

    for (let i = 0; i < results.value.length; i++) {
      descriptions.value[res.data[i].name] = res.data[i].description
    }
  } catch (e) {
    console.error(e)
  }
})

function backToIntro() {
  showIntro.value = true
}

function startSurvey() {
  showIntro.value = false
  showSurvey.value = true
}

function skipSurvey() {
  router.push({ name: 'HomeView' })
}

function nextStep() {
  if (!validate()) return
  currentStep.value++
}

function prevStep() {
  if (currentStep.value === 0) {
    showSurvey.value = false
    showIntro.value = true
  } else {
    currentStep.value--
  }
}

function validate() {
  if (answers.value[currentStep.value] === null) {
    alert('답변을 선택해주세요.')
    return false
  }
  return true
}

async function submitSurvey() {
  dietType.value = result()
  console.log(dietType.value, 'dietType')
  const data = {
    userId: accountStore.userId,
    personaId: dietType.value,
    stepLevel: 1,
    answerValues: answers.value.toString(),
  }

  await axios
    .post('/api/survey/', data)
    .then((res) => {
      showSurvey.value = false
    })
    .catch((err) => {})

  const me = await axios.get('/api/users/me')

  accountStore.setAccount({
    userId: me.data.userId,
    username: me.data.username,
    personaId: me.data.personaId,
    role: me.data.role,
    profileUrl: me.data.profileUrl,
  })
}

//설문 결과를 바탕으로 타입을 특정하기 위한 함수
function result() {
  // 1) 응답 인덱스(0~4) → 실제 점수(1~5) 매핑
  const scores = answers.value.map((idx) => idx + 1)

  // 2) 각 질병 태그별 문항 매핑 (1-based index)
  const tagQuestions = {
    obesity: [2, 4, 5, 12, 13],
    diabetes: [5, 9, 12, 13],
    hypertension: [12, 17, 19, 13],
    hyperlipidemia: [8, 9, 17, 19],
  }

  // 3) 태그별 평균 점수 계산
  const tagScores = {}
  for (const [tag, qs] of Object.entries(tagQuestions)) {
    const sum = qs.reduce((acc, q) => acc + scores[q - 1], 0)
    tagScores[tag] = sum / qs.length
  }

  // 4) 각 페르소나 점수 계산: 페르소나가 가진 태그들의 평균 점수 합
  let best = { personaId: null, score: -Infinity }
  results.value.forEach((p) => {
    const tags = p.diseaseTags ? p.diseaseTags.split(',').filter((t) => t.trim()) : []
    // 태그가 없으면(헬시 히어로) score = 0
    const personaScore = tags.length ? tags.reduce((acc, t) => acc + (tagScores[t] || 0), 0) : 0

    if (personaScore > best.score) {
      best = { personaId: p.personaId, score: personaScore }
    }
  })

  // 5) 최고 점수 페르소나 반환 (없으면 기본 1번)
  return best.personaId || results.value[0].personaId
}
function handleEnterKey(e) {
  if (e.key !== 'Enter') return

  // 인트로 화면이면 설문 시작
  if (showIntro.value) {
    startSurvey()
    return
  }

  // 설문 진행 중이면 다음 또는 제출
  if (showSurvey.value) {
    if (!validate()) return
    if (isLast.value) {
      submitSurvey()
    } else {
      nextStep()
    }
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleEnterKey)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleEnterKey)
})
</script>

<style scoped>
.survey-page {
  background: #f8f9fa;
  min-height: 100vh;
  padding-bottom: 2rem;
}
.step {
  display: inline-block;
  width: 2rem;
  height: 2rem;
  background: #ddd;
  border-radius: 50%;
  line-height: 2rem;
  text-align: center;
  color: #6c757d;
  opacity: 0.5;
  transition: all 0.3s;
}
.step.active {
  opacity: 1;
  background: #6f42c1;
  color: #fff;
}
.step.finish {
  background: #6f42c1;
  color: #fff;
  opacity: 1;
}
/* 인트로/기본정보 카드 */
.card.text-center p,
.card h4 {
  line-height: 1.6;
}
</style>
