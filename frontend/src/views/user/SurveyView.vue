<template>
  <main class="survey-page">
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <!-- 인트로 화면 -->
          <div v-if="showIntro" class="card p-4 shadow-sm text-center">
            <h3 class="text-primary mb-3">🍽️ 싸피밥피 식습관 설문조사 🍽️</h3>
            <p>
              당신의 식습관을 흥미지시가게 파헤쳐 줄<br />
              <strong>‘싸피밥피 식습관 분석’</strong>에 조대합니다!<br />
              질문에 소심히 답하면 나만의 맞춤 리포트를 드립니다.<br /><br />
              ※주의 ) <u>건너뛰기</u> 시에는 나이·성별 평균값 기반 분석이 제공되어<br />
              정확도가 떨어지더라는 것을 주의해주세요!<br /><br />
            </p>
            <div class="mt-4">
              <button class="btn btn-success me-2" @click="startSurvey">설문 시작하기</button>
              <router-link to="/" class="btn btn-outline-secondary"> 건너뛰기 </router-link>
            </div>
          </div>

          <!-- 설문 화면 -->
          <div v-else-if="showSurvey" class="card p-4 shadow-sm">
            <h3 class="text-center text-primary mb-4">식습관 분석</h3>

            <!-- 진행 상태 -->
            <div class="d-flex justify-content-center mb-4">
              <span
                v-for="(q, i) in questions"
                :key="i"
                class="step mx-1"
                :class="{ active: currentStep === i, finish: i < currentStep }"
                >{{ i + 1 }}</span
              >
            </div>

            <!-- 질문 -->
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

            <!-- 이동 버튼 -->
            <div class="d-flex justify-content-between mt-4">
              <button class="btn btn-secondary" @click="prevStep">이전</button>
              <button class="btn btn-primary" @click="isLast ? submitSurvey() : nextStep()">
                {{ isLast ? '제출' : '다음' }}
              </button>
            </div>
          </div>

          <!-- 결과 화면 -->
          <div v-else class="card p-4 shadow-sm text-center result-card">
            <h3 class="text-primary mb-2">{{ results[dietType - 1].name }}</h3>
            <p class="mb-4 text-muted">{{ results[dietType - 1].description }}</p>
            <img
              :src="imgUrl"
              alt="결과 사진"
              class="result-img mb-3 mx-auto d-block"
              width="50%"
            />
            <router-link to="/" class="btn btn-outline-secondary">메인으로 돌아가기</router-link>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
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

const imgUrl = ref('')

onMounted(async () => {
  try {
    const res = await axios.get('/api/survey/questions', { params: { stepLevel: 1 } })
    questions.value = res.data
  } catch (e) {
    console.error(e)
  }

  try {
    const res = await axios.get('/api/survey/results')
    results.value = res.data
    results.value.forEach((r) => (descriptions.value[r.name] = r.description))
  } catch (e) {
    console.error(e)
  }

  window.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown)
})

function startSurvey() {
  showIntro.value = false
  showSurvey.value = true
}
function prevStep() {
  if (currentStep.value === 0) showIntro.value = true
  else currentStep.value--
}
function nextStep() {
  if (!validate()) return
  currentStep.value++
}
function validate() {
  if (answers.value[currentStep.value] === null) {
    alert('단호를 선택해주세요.')
    return false
  }
  return true
}
async function submitSurvey() {
  dietType.value = result()
  await axios.post('/api/survey/', {
    userId: accountStore.userId,
    personaId: dietType.value,
    stepLevel: 1,
    answerValues: answers.value.toString(),
  })

  const res = await axios.get('/api/s3/get-url', {
    params: { filename: `uploads/user/${dietType.value}.png` },
  })
  imgUrl.value = res.data

  const me = await axios.get('/api/users/me')
  accountStore.setAccount({
    userId: me.data.userId,
    username: me.data.username,
    personaId: me.data.personaId,
    role: me.data.role,
    profileUrl: me.data.profileUrl,
  })
  showSurvey.value = false
}

function handleKeydown(e) {
  const key = e.key
  if (key === 'Enter') {
    if (showIntro.value) startSurvey()
    else if (showSurvey.value) {
      if (!validate()) return
      isLast.value ? submitSurvey() : nextStep()
    }
  } else if (/^[1-5]$/.test(key) && showSurvey.value) {
    answers.value[currentStep.value] = parseInt(key) - 1
  }
}

function result() {
  const scores = answers.value.map((idx) => idx + 1)
  const tagQuestions = {
    obesity: [2, 4, 5, 12, 13],
    diabetes: [5, 9, 12, 13],
    hypertension: [12, 17, 19, 13],
    hyperlipidemia: [8, 9, 17, 19],
  }
  const tagScores = {}
  for (const [tag, qs] of Object.entries(tagQuestions)) {
    tagScores[tag] = qs.reduce((sum, q) => sum + scores[q - 1], 0) / qs.length
  }
  let best = { personaId: null, score: -Infinity }
  results.value.forEach((p) => {
    const tags = p.diseaseTags?.split(',') || []
    const score = tags.reduce((acc, t) => acc + (tagScores[t] || 0), 0)
    if (score > best.score) best = { personaId: p.personaId, score }
  })
  return best.personaId || results.value[0].personaId
}
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
.result-img {
  max-width: 300px;
}
</style>
