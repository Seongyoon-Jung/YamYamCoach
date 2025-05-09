<template>
  <div class="d-flex align-items-center justify-content-center min-vh-100">
    <div
      class="card p-5 shadow-lg"
      style="max-width: 600px; width: 100%; border-radius: 1rem"
    >
      <h3 class="text-center fw-bold mb-4 text-dark" v-if="isbasic">
        회원 정보 입력 (필수)
      </h3>
      <div v-else>
        <h3 class="text-center fw-bold mb-4 text-dark">
          회원 정보 입력 (선택)
        </h3>
        <p>
          ※주의 ) <u>건너뛰기</u> 시엔 나이·성별 평균값 기반 분석이 제공되어<br />
          정확도가 떨어질 수 있으니 주의하세요!<br />
        </p>
      </div>
      <hr />

      <form>
        <div class="row g-3 text-start" v-if="isbasic">
          <!-- 이메일 -->
          <div class="col-md-12">
            <!-- 레이블을 input-group 위에 두고 form-label 클래스로 왼쪽 정렬 -->
            <label for="email" class="form-label fw-bold"
              >이메일
              <span class="text-danger fw-normal warning" v-if="warning.email"
                >이메일을 입력해주세요.</span
              >
              <span
                class="text-danger fw-normal warning"
                v-if="warning.checkEmail === null"
                >중복 확인을 해주세요.</span
              >
              <span
                class="text-danger fw-normal warning"
                v-else-if="warning.checkEmail === true"
                >사용할 수 없는 이메일입니다.</span
              >
              <span
                class="text-success fw-normal warning"
                v-if="warning.checkEmail === false"
                >사용할 수 있는 이메일입니다.</span
              >
            </label>

            <div class="input-group">
              <input
                type="email"
                id="email"
                class="form-control"
                placeholder="example@domain.com"
                required
                name="email"
                v-model="result.email"
              />
              <button
                type="button"
                class="btn btn-outline-secondary"
                @click="checkEmail"
              >
                중복 확인
              </button>
            </div>
          </div>

          <!-- 비밀번호 -->
          <div class="col-md-12">
            <label for="password" class="form-label fw-bold"
              >비밀번호
              <span
                class="text-danger fw-normal warning"
                v-if="warning.password"
                >비밀번호를 입력해주세요.</span
              >
            </label>
            <input
              type="password"
              id="password"
              class="form-control"
              placeholder="비밀번호를 입력하세요"
              required
              name="password"
              v-model="result.password"
            />
          </div>

          <!-- 비밀번호 재입력 -->
          <div class="col-md-12">
            <label for="checkPassword" class="form-label fw-bold"
              >비밀번호 확인
              <span
                class="text-danger fw-normal warning"
                v-if="warning.checkPassword === null"
                >비밀번호 확인해주세요.</span
              >
              <span
                class="text-danger fw-normal warning"
                v-if="warning.checkPassword === true"
                >비밀번호가 일치하지 않습니다</span
              >
            </label>

            <input
              type="password"
              id="checkPassword"
              class="form-control"
              placeholder="비밀번호를 재입력해주세요"
              required
              v-model="result.checkPassword"
            />
          </div>

          <!-- 닉네임 -->
          <div class="col-md-12">
            <label for="username" class="form-label fw-bold"
              >닉네임
              <span
                class="text-danger fw-normal warning"
                v-if="warning.username"
                >닉네임을 입력해주세요.</span
              >
              <span
                class="text-danger fw-normal warning"
                v-if="warning.checkUsername === null"
                >중복 확인을 해주세요.</span
              >
              <span
                class="text-danger fw-normal warning"
                v-else-if="warning.checkUsername === true"
                >사용할 수 없는 닉네임입니다.</span
              >
              <span
                class="text-success fw-normal warning"
                v-if="warning.checkUsername === false"
                >사용할 수 있는 닉네임입니다.</span
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
              <button
                type="button"
                class="btn btn-outline-secondary"
                @click="checkUsername"
              >
                중복 확인
              </button>
            </div>
          </div>

          <div class="col-md-12">
            <!-- 성별 -->
            <label class="form-label fw-bold"
              >성별
              <span
                class="text-danger fw-normal warning"
                v-if="warning.gender === null"
                >성별을 선택해주세요.</span
              >
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
              <label class="btn btn-outline-secondary flex-fill" for="male">
                남성
              </label>

              <input
                type="radio"
                class="btn-check"
                name="gender"
                id="female"
                :value="true"
                v-model="result.gender"
                autocomplete="off"
              />
              <label class="btn btn-outline-secondary flex-fill" for="female">
                여성
              </label>
            </div>
          </div>

          <!-- 생년월일 -->
          <div class="col-md-12">
            <label for="birthdate" class="form-label fw-bold">
              생년월일
              <span
                class="text-danger fw-normal warning"
                v-if="warning.birthDate === null"
                >생년월일을 입력해주세요.</span
              >
              <span
                class="text-danger fw-normal warning"
                v-if="warning.birthDate === true"
                >생년월일을 정확히 입력해주세요.</span
              >
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

          <!-- 추가 정보 버튼 -->
          <div class="d-flex justify-content-between mt-4">
            <router-link class="btn btn-secondary" to="/login"
              >이전</router-link
            >
            <button type="button" class="btn btn-primary" @click="nextBasic">
              다음
            </button>
          </div>
        </div>

        <div class="row g-3" v-else>
          <!-- 이메일 -->
          <div class="col-md-12">
            <div class="row align-items-center">
              <!-- 키 -->
              <div class="mb-3">
                <label class="form-label">키 (cm)</label>
                <input
                  type="number"
                  class="form-control"
                  v-model.number="result.height"
                  min="0"
                  required
                />
              </div>

              <!-- 현재 체중 -->
              <div class="mb-3">
                <label class="form-label">현재 체중 (kg)</label>
                <input
                  type="number"
                  class="form-control"
                  v-model.number="result.weight"
                  min="0"
                  required
                />
              </div>

              <!-- 목표 체중 -->
              <div class="mb-0">
                <label class="form-label">목표 체중 (kg)</label>
                <input
                  type="number"
                  class="form-control"
                  v-model.number="result.targetWeight"
                  min="0"
                  required
                />
              </div>
            </div>
          </div>
          <!-- 가입 버튼 -->
          <div class="text-end">
            <a class="btn text-black" @click="save">건너뛰기</a>
          </div>

          <div class="d-flex justify-content-between">
            <button class="btn btn-secondary" @click="prevBasic">이전</button>
            <button type="button" class="btn btn-primary" @click="save">
              회원가입
            </button>
          </div>
        </div>
      </form>
      <div class="text-center mt-4">
        <router-link
          to="/login"
          class="text-decoration-none text-black-50 small"
        >
          이미 계정이 있으신가요?
          <span class="fw-semibold text-black">로그인</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card {
  background-color: white;
  border-radius: 1rem;
}

input::placeholder {
  font-size: 0.9rem;
  color: #999;
}
</style>

<script>
import axios from '@/plugins/axios';

export default {
  name: 'SignupView', // name은 index.js에 선언해놓은 이름과 똑같아야함
  data() {
    return {
      isbasic: true,
      result: {
        email: null,
        password: null,
        checkPassword: '',
        username: null,
        gender: null,
        birthDate: null,
        height: null,
        weight: null,
        targetWeight: null,
        checkedEmail: null,
        checkedUsername: null,
      },
      warning: {},
    };
  },
  created() {},
  methods: {
    prevBasic() {
      this.isbasic = true;
    },

    nextBasic() {
      if (!this.result.email) {
        this.warning['email'] = true;
        delete this.warning.checkEmail;
        return;
      }

      if (!this.result.checkedEmail) {
        this.warning['checkEmail'] = null;
        return;
      }

      if (!this.result.password) {
        this.warning['password'] = true;
        return;
      }
      delete this.warning.password;

      if (!this.result.checkPassword) {
        this.warning['checkPassword'] = null;
        return;
      }

      if (this.result.password != this.result.checkPassword) {
        this.warning['checkPassword'] = true;
        return;
      }

      delete this.warning.checkPassword;

      if (!this.result.username) {
        this.warning['username'] = true;
        delete this.warning.checkUsername;
        return;
      }
      this.warning['username'] = false;

      if (!this.result.checkedUsername) {
        this.warning['checkUsername'] = null;
        return;
      }

      if (this.result.gender === null) {
        this.warning['gender'] = null;
        return;
      }
      delete this.warning.gender;

      //아무것도 입력하지 않았을 경우
      if (!this.result.birthDate) {
        this.warning['birthDate'] = null;
        return;
      } else if (!this.isValidCompactDate(this.result.birthDate)) {
        this.warning['birthDate'] = true;
        return;
      }
      delete this.warning.birthDate;

      this.isbasic = false;
    },
    save() {
      var stringToDate = this.result.birthDate;
      stringToDate =
        stringToDate.substring(0, 4) +
        '-' +
        stringToDate.substring(4, 6) +
        '-' +
        stringToDate.substring(6, 8);
      // 입력 했을때 20000101 에서 보내기직전에 2000-01-01로 바꾸지 않기 위해서 새로만들어서 보내기기
      const post = {
        email: this.result.email,
        password: this.result.password,
        username: this.result.username,
        gender: this.result.gender,
        birthDate: stringToDate,
        height: this.result.height,
        weight: this.result.weight,
        targetWeight: this.result.targetWeight,
      };

      console.log(post);

      axios
        .post('/api/users', post)
        .then((res) => {
          console.log(res);
          alert('회원가입이 완료되었습니다.');
          this.$router.push({ name: 'LoginView' });
        })
        .catch((err) => {
          console.log(err);
        });
    },
    isValidCompactDate(str) {
      // 1. 길이검사
      if (str.length != 8) {
        return false;
      }

      // 2. 연월일 추출
      const year = parseInt(str.substring(0, 4), 10);
      const month = parseInt(str.substring(4, 6), 10);
      const day = parseInt(str.substring(6, 8), 10);

      // 3. Date 객체로 변환
      const date = new Date(year, month - 1, day);

      // 4. 날짜 유효성 검사
      return (
        date.getFullYear() === year &&
        date.getMonth() + 1 === month &&
        date.getDate() === day
      );
    },
    checkEmail() {
      if (!this.result.email) {
        this.warning['email'] = true;
        delete this.warning.checkEmail;
        return;
      }
      this.warning['email'] = false;
      axios
        .get('/api/users/check-email', { params: { email: this.result.email } })
        .then((res) => {
          this.warning['checkEmail'] = false;
          this.result.checkedEmail = true;
        })
        .catch((err) => {
          this.warning['checkEmail'] = true;
          this.result.email = '';
          this.result.checkedEmail = false;
        });
    },
    checkUsername() {
      if (!this.result.username) {
        this.warning['username'] = true;
        delete this.warning.checkUsername;
        return;
      }
      this.warning['username'] = false;
      axios
        .get('/api/users/check-username', {
          params: { username: this.result.username },
        })
        .then((res) => {
          this.warning['checkUsername'] = false;
          this.result.checkedUsername = true;
        })
        .catch((err) => {
          this.warning['checkUsername'] = true;
          this.result.username = '';
          this.result.checkedUsername = false;
        });
    },
  },
};
</script>
