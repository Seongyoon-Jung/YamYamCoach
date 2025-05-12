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
              <button class="btn btn-secondary" @click="prevStep" :disabled="isFirst">이전</button>
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
            <h3 class="text-primary mb-2">{{ dietType }}</h3>

            <!-- 간단 설명 (dietType별로 다른 설명을 data나 computed로 관리해도 좋아요) -->
            <p class="mb-4 text-muted">
              {{ descriptions[dietType] }}
            </p>

            <!-- 이미지: 캐릭터 일러스트 -->
            <img
              :src="`/survey/${dietType}.png`"
              alt="결과 캐릭터"
              class="result-img mb-3 mx-auto d-block"
              width="50%;"
            />

            <!-- 메인으로 돌아가기 버튼 -->
            <router-link to="/home" class="btn btn-outline-secondary">
              메인으로 돌아가기
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/stores/account'

const router = useRouter()
const accountStore = userAccountStore()

const showIntro = ref(true)
const showSurvey = ref(false)
const currentStep = ref(0)
const questions = ref([])
const answers = ref(Array(10).fill(null))

const options = ['전혀 그렇지 않다', '그렇지 않다', '보통이다', '그렇다', '매우 그렇다']

const dietType = ref(null)

const descriptions = reactive({
  '균형의 마에스트로': '식사·영양·수분·운동을 고루 챙기는 ‘완벽 균형파’입니다.',
  '심야의 스낵 헌터': '밤마다 야식을 즐겨 수면·소화 리듬이 불규칙한 ‘야행성 스낵러’입니다.',
  '무계획의 방랑자': '식사 시간이 들쑥날쑥, TV 보며 불규칙하게 먹는 ‘방랑형 식사러’입니다.',
  '단짠의 시럽 러버': '단 음식·인스턴트에 빠져 단짠 중독에 가까운 ‘시럽 러버’입니다.',
  '액티브 에이스': '매일 운동·충분 수분 섭취·규칙 식단으로 건강 루틴이 탄탄한 ‘운동광’입니다.',
  '디저트 덕후': '하루종일 달달한 디저트 한 입을 찾는 ‘디저트 덕후’입니다.',
  '소식의 달인': '끼니마다 아주 조금씩 자주 먹으며 과식은 피하는 ‘소식 마스터’입니다.',
  '하이드레이션 히어로': '물만 보면 달려들어 2L 이상 꾸준히 마시는 ‘수분 챔피언’입니다.',
  '집중 먹방 챌린저': '식사할 땐 무조건 집중!TV·휴대폰 NO, ‘집중형 식사러’입니다.',
  '헬시 뉴비': '가끔은 규칙·건강을 시도하지만 아직은 초보 단계인 ‘건강 초보’입니다.',
})

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
})

function backToIntro() {
  showIntro.value = true
}

function startSurvey() {
  showIntro.value = false
  showSurvey.value = true
}

function skipSurvey() {
  router.push({ name: 'HomeLoginView' })
}

function nextStep() {
  if (!validate()) return
  currentStep.value++
}

function prevStep() {
  if (currentStep.value > 0) currentStep.value--
}

function validate() {
  if (answers[currentStep] === null) {
    alert('답변을 선택해주세요.')
    return false
  }
  return true
}

function submitSurvey() {
  dietType.value = result()

  //console.log(basic);
  let data = {
    userId: accountStore.userId,
    dietType: dietType,
    stepLevel: 1,
    answerValues: answers.value.toString(),
  }

  axios
    .post('/api/survey/submit', data)
    .then((res) => {
      showSurvey.value = false
    })
    .catch((err) => {})

  // const me = axios.get('/api/users/me');
  // store.commit('setAccount', {
  //   userId: me.data.userId,
  //   username: me.data.username,
  //   isSurveyed: me.data.isSurveyed,
  // });
}

//설문 결과를 바탕으로 타입을 특정하기 위한 함수
function result() {
  const type = [
    '균형의 마에스트로',
    '심야의 스낵 헌터',
    '무계획의 방랑자',
    '단짠의 시럽 러버',
    '액티브 에이스',
    '디저트 덕후',
    '소식의 달인',
    '하이드레이션 히어로',
    '집중 먹방 챌린저',
    '헬시 뉴비',
  ]
  // 가중치 2차원 배열
  const score = [
    [10, 0, 2, 0, 5, 0, 8, 3, 7, 4], // Q1
    [0, 10, 1, 3, 0, 7, 0, 0, 1, 2], // Q2
    [9, 1, 2, 0, 6, 0, 5, 4, 6, 3], // Q3
    [10, 0, 2, 0, 5, 0, 4, 3, 7, 4], // Q4
    [9, 0, 1, 2, 7, 0, 3, 5, 6, 4], // Q5
    [0, 0, 0, 10, 0, 8, 0, 0, 1, 2], // Q6
    [5, 0, 0, 0, 10, 0, 1, 3, 1, 2], // Q7
    [4, 0, 1, 0, 9, 0, 2, 10, 3, 2], // Q8
    [0, 10, 3, 0, 0, 4, 0, 1, 1, 2], // Q9
    [8, 0, 2, 0, 7, 0, 4, 3, 10, 3], // Q10
  ]

  let modify_answer = [0, 2, 5, 8, 10] // 5가자의 선택지를 0,1,2,3,4로 곱하는 것이아니라 0,2,5,8,10으로 최대한 같은 값이 나오는 것을 막기

  let total = Array(10).fill(0)
  let max = -1
  let maxInd = -1

  for (let i = 0; i < 10; i++) {
    for (let j = 0; j < 10; j++) {
      total[i] += modify_answer[answers[j]] * score[i][j]
    }
    if (max < total[i]) {
      max = total[i]
      maxInd = i
    }
  }

  // console.log(total, type[maxInd])
  return type[maxInd]
}
</script>

<script>
export default {
  methods: {
    backToIntro() {
      showIntro.value = true
    },
    startSurvey() {
      showIntro.value = false
      showSurvey.value = true
    },
    skipSurvey() {
      router.push({ name: 'HomeView' })
    },
    nextStep() {
      if (!validate()) return
      currentStep.value++
    },
    prevStep() {
      if (currentStep.value > 0) currentStep.value--
    },
    validate() {
      if (answers[currentStep] === null) {
        alert('답변을 선택해주세요.')
        return false
      }
      return true
    },
    submitSurvey() {
      dietType.value = result()

      //console.log(basic);
      let data = {
        userId: accountStore.userId,
        dietType: dietType,
        stepLevel: 1,
        answerValues: answers.value.toString(),
      }

      axios
        .post('/api/survey/submit', data)
        .then((res) => {
          showSurvey.value = false
        })
        .catch((err) => {})

      // const me = axios.get('/api/users/me');
      // store.commit('setAccount', {
      //   userId: me.data.userId,
      //   username: me.data.username,
      //   isSurveyed: me.data.isSurveyed,
      // });
    },

    //설문 결과를 바탕으로 타입을 특정하기 위한 함수
    result() {
      const type = [
        '균형의 마에스트로',
        '심야의 스낵 헌터',
        '무계획의 방랑자',
        '단짠의 시럽 러버',
        '액티브 에이스',
        '디저트 덕후',
        '소식의 달인',
        '하이드레이션 히어로',
        '집중 먹방 챌린저',
        '헬시 뉴비',
      ]
      // 가중치 2차원 배열
      const score = [
        [10, 0, 2, 0, 5, 0, 8, 3, 7, 4], // Q1
        [0, 10, 1, 3, 0, 7, 0, 0, 1, 2], // Q2
        [9, 1, 2, 0, 6, 0, 5, 4, 6, 3], // Q3
        [10, 0, 2, 0, 5, 0, 4, 3, 7, 4], // Q4
        [9, 0, 1, 2, 7, 0, 3, 5, 6, 4], // Q5
        [0, 0, 0, 10, 0, 8, 0, 0, 1, 2], // Q6
        [5, 0, 0, 0, 10, 0, 1, 3, 1, 2], // Q7
        [4, 0, 1, 0, 9, 0, 2, 10, 3, 2], // Q8
        [0, 10, 3, 0, 0, 4, 0, 1, 1, 2], // Q9
        [8, 0, 2, 0, 7, 0, 4, 3, 10, 3], // Q10
      ]

      let modify_answer = [0, 2, 5, 8, 10] // 5가자의 선택지를 0,1,2,3,4로 곱하는 것이아니라 0,2,5,8,10으로 최대한 같은 값이 나오는 것을 막기

      let total = Array(10).fill(0)
      let max = -1
      let maxInd = -1

      for (let i = 0; i < 10; i++) {
        for (let j = 0; j < 10; j++) {
          total[i] += modify_answer[answers[j]] * score[i][j]
        }
        if (max < total[i]) {
          max = total[i]
          maxInd = i
        }
      }

      // console.log(total, type[maxInd])
      return type[maxInd]
    },
  },
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
/* 인트로/기본정보 카드 */
.card.text-center p,
.card h4 {
  line-height: 1.6;
}
</style>
