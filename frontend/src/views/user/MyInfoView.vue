<template>
  <main class="mt-5 pt-4 pb-5 position-relative">
    <div class="container" style="max-width: 600px">
      <!-- 뒤로가기 + 제목 -->
      <h2 class="fw-bold mb-4">개인정보 수정</h2>

      <form>
        <!-- 기본 정보 섹션 -->
        <h6 class="mb-3">기본 정보</h6>
        <div class="card mb-4 p-3">
          <div class="row g-3 text-start col-md-12">
            <!-- 이메일 -->
            <div class="col-md-12 mb-3">
              <!-- 레이블을 input-group 위에 두고 form-label 클래스로 왼쪽 정렬 -->
              <label for="email" class="form-label fw-bold">이메일 </label>

              <div class="input-group">
                <input
                  type="text"
                  id="email"
                  class="form-control"
                  placeholder="example@domain.com"
                  required
                  name="email"
                  v-model="result.email"
                  readonly
                  disabled
                />
              </div>
            </div>

            <!-- 닉네임 -->
            <div class="col-md-12 mb-3">
              <label for="username" class="form-label fw-bold"
                >닉네임
                <span
                  class="text-danger fw-normal warning"
                  v-show="click"
                  v-if="!result.checkedUsername"
                  v-text="warning.username"
                ></span>
                <span class="text-success fw-normal warning" v-show="click" v-else
                  >사용 가능한 닉네임입니다</span
                >
              </label>

              <div class="input-group">
                <input
                  type="text"
                  id="username"
                  class="form-control"
                  placeholder="사용할 닉네임"
                  required
                  name="username"
                  v-model="result.username"
                />
                <button type="button" class="btn btn-outline-secondary" @click="checkUsername">
                  중복 확인
                </button>
              </div>
              <span class="text-info warning">※ 4~10자의 한글, 영문, 숫자를 사용해 주세요.</span>
            </div>

            <div class="col-md-12 mb-3">
              <!-- 성별 -->
              <label class="form-label fw-bold"
                >성별
                <span
                  class="text-danger fw-normal warning"
                  v-show="click"
                  v-text="warning.gender"
                ></span>
              </label>
              <div class="btn-group w-100" role="group">
                <input
                  type="radio"
                  class="btn-check"
                  name="gender"
                  id="male"
                  :value="false"
                  v-model="result.gender"
                  autocomplete="off"
                />
                <label class="btn btn-outline-secondary flex-fill" for="male"> 남성 </label>

                <input
                  type="radio"
                  class="btn-check"
                  name="gender"
                  id="female"
                  :value="true"
                  v-model="result.gender"
                  autocomplete="off"
                />
                <label class="btn btn-outline-secondary flex-fill" for="female"> 여성 </label>
              </div>
            </div>

            <!-- 생년월일 -->
            <div class="col-md-12 mb-0">
              <label for="birthdate" class="form-label fw-bold">
                생년월일
                <span
                  class="text-danger fw-normal warning"
                  v-show="click"
                  v-text="warning.birthDate"
                ></span>
              </label>
              <input
                type="text"
                id="birthdate"
                class="form-control"
                required
                name="birthDate"
                placeholder="예) 20000101"
                v-model="result.birthDate"
              />
            </div>
          </div>
        </div>

        <!-- 추가 정보 섹션 -->
        <h6 class="mb-3">추가 정보</h6>
        <div class="card mb-4 p-3">
          <div class="row g-3 text-start col-md-12">
            <!-- 키 -->
            <div class="col-md-12 mb-3">
              <label class="form-label fw-bold">키 (cm)</label>
              <input
                type="number"
                class="form-control"
                v-model.number="optionResult.height"
                min="0"
                required
              />
            </div>

            <!-- 현재 체중 -->
            <div class="col-md-12 mb-3">
              <label class="form-label fw-bold">현재 체중 (kg)</label>
              <input
                type="number"
                class="form-control"
                v-model.number="optionResult.weight"
                min="0"
                required
              />
            </div>

            <!-- 목표 체중 -->
            <div class="col-md-12 mb-0">
              <label class="form-label fw-bold">목표 체중 (kg)</label>
              <input
                type="number"
                class="form-control"
                v-model.number="optionResult.targetWeight"
                min="0"
                required
              />
            </div>
          </div>
        </div>

        <!-- 저장/취소 버튼 -->
        <div class="d-flex justify-content-center gap-3">
          <button type="button" class="btn btn-primary px-5" @click="handleUpdate">저장</button>
          <button type="button" class="btn btn-outline-secondary px-5" @click="goBack">취소</button>
        </div>
      </form>

      <div class="text-end">
        <a class="btn text-decoration-underline" @click="handleDelete">회원 탈퇴</a>
      </div>
    </div>
    <!-- ConfirmDialog 컴포넌트를 ref 로 선언 -->
    <ConfirmDialog ref="confirmDialog" />
  </main>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plugins/axios'
import { userAccountStore } from '@/store/account'
import ConfirmDialog from '@/components/dialog/ConfirmDialog.vue'

const router = useRouter()
const accountStore = userAccountStore()
const confirmDialog = ref(null)

const result = reactive({
  email: '',
  username: '',
  gender: '',
  birthDate: '',
  checkedUsername: '',
  checkedUsernameRule: '',
})

const optionResult = reactive({
  height: '',
  weight: '',
  targetWeight: '',
})

const click = ref(false)
const warning = reactive({
  email: '',
  username: '',
  gender: '',
  birthDate: '',
})
const success = reactive({ username: '' })

onMounted(() => {
  axios.get('/api/users').then((res) => {
    result.email = res.data.email
    result.username = res.data.username
    result.gender = res.data.gender
    result.birthDate = res.data.birthDate.replaceAll('-', '')
    optionResult.height = res.data.height
    optionResult.weight = res.data.weight
    optionResult.targetWeight = res.data.targetWeight
    success.username = res.data.username
    result.checkedUsername = true
    result.checkedUsernameRule = true
  })
})

const checkUsernameRule = () => {
  const pattern = /^[A-Za-z0-9\u1100-\u11FF\u3130-\u318F\uAC00-\uD7A3]{4,10}$/
  if (pattern.test(result.username)) {
    result.checkedUsernameRule = true
    return true
  }
  result.checkedUsernameRule = ''
  return false
}

const isValidCompactDate = () => {
  const pattern =
    /^(19|20)\d{2}(?:(?:0[13578]|1[02])(?:0[1-9]|[12]\d|3[01])|(?:0[469]|11)(?:0[1-9]|[12]\d|30)|02(?:0[1-9]|1\d|2\d))$/
  if (!pattern.test(result.birthDate)) return false

  const year = parseInt(result.birthDate.slice(0, 4))
  const month = result.birthDate.slice(4, 6)
  const day = result.birthDate.slice(6, 8)

  if (month === '02' && day === '29') {
    return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0
  }
  return true
}

const warningMessage = () => {
  if (!result.username) {
    warning.username = '닉네임을 입력하세요'
    result.checkedUsername = ''
  } else if ((result.username && !result.checkedUsername) || success.username !== result.username) {
    result.checkedUsername = ''
    warning.username = '닉네임 중복을 확인하세요'
  } else if (result.username && checkUsernameRule()) {
    warning.username = ''
  } else {
    result.checkedUsername = ''
    warning.username = '닉네임이 규칙에 맞지않습니다'
  }

  warning.gender = result.gender === '' ? '성별을 선택하세요' : ''

  if (!result.birthDate) {
    warning.birthDate = '생년월일을 입력하세요'
  } else if (!isValidCompactDate()) {
    warning.birthDate = '생년월일을 정확히 입력하세요'
  } else {
    warning.birthDate = ''
  }
}

const checkUsername = () => {
  click.value = true
  if (!result.username) {
    result.checkedUsername = ''
    warning.username = '닉네임을 입력하세요'
    return
  }
  if (!checkUsernameRule()) {
    result.checkedUsername = ''
    warning.username = '닉네임이 규칙에 맞지 않습니다'
    return
  }
  if (result.username === success.username) {
    click.value = false
    return
  }
  axios
    .get('/api/users/check-username', { params: { username: result.username } })
    .then(() => {
      result.checkedUsername = true
      success.username = result.username
    })
    .catch(() => {
      warning.username = '사용 불가능한 닉네임 입니다'
      result.checkedUsername = ''
    })
}

const save = () => {
  const birth = result.birthDate
  const stringToDate = `${birth.substring(0, 4)}-${birth.substring(4, 6)}-${birth.substring(6, 8)}`

  const post = {
    userId: accountStore.userId,
    username: result.username,
    gender: result.gender,
    birthDate: stringToDate,
    height: optionResult.height,
    weight: optionResult.weight,
    targetWeight: optionResult.targetWeight,
  }

  axios
    .put('/api/users', post)
    .then(() => {
      accountStore.username = result.username
      router.push({ name: 'MyView' })
    })
    .catch((err) => {
      console.error(err)
    })
}

const handleUpdate = async () => {
  warningMessage()
  if (Object.values(result).includes('')) {
    click.value = true
    return
  }
  if (Object.values(optionResult).includes('')) {
    const ok = await confirmDialog.value.open({
      title: '선택정보 확인',
      message: `선택정보 미입력 시 정확한 식단분석이 불가능합니다.<br/>그래도 진행하시겠습니까?`,
    })
    if (!ok) return
  }
  save()
}

const handleDelete = async () => {
  const ok = await confirmDialog.value.open({
    title: '회원 탈퇴 확인',
    message: `회원 탈퇴 시 모든 정보가 삭제되고 복구가 불가능합니다.<br/>그래도 진행하시겠습니까?`,
  })
  if (!ok) return

  axios.delete('/api/users', { params: { userId: accountStore.userId } }).then(() => {
    accountStore.clearAccount()
    router.push({ name: 'HomeView' })
  })
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.hero {
  min-height: 0; /* 기존 Hero 가 있다면 제거 */
}
</style>
