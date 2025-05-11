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
          ※ <span class="text-danger">주의</span> ) 미입력 시엔 나이·성별 평균값 기반 분석이 제공되어<br />
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

              <span class="text-danger fw-normal warning" v-show="click" v-if="!result.checkedEmail" v-text="warning.email"></span>
              <span class="text-success fw-normal warning" v-show="click" v-else>사용 가능한 이메일입니다</span>
      
            </label>

            <div class="input-group">
              <input
                type="text"
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
              <span class="text-danger fw-normal warning" v-show="click" v-text="warning.password"></span>
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
            <span class="text-info warning" >※ 8~16자의 영문, 숫자, 특수문자를 사용해 주세요.</span>
          </div>

          <!-- 비밀번호 재입력 -->
          <div class="col-md-12">
            <label for="checkPassword" class="form-label fw-bold"
              >비밀번호 확인
              <span class="text-danger fw-normal warning" v-show="click" v-text="warning.checkPassword"></span>
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
              <span class="text-danger fw-normal warning" v-show="click" v-if="!result.checkedUsername" v-text="warning.username"></span>
              <span class="text-success fw-normal warning" v-show="click" v-else>사용 가능한 닉네임입니다</span>
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
            <span class="text-info warning" >※ 4~10자의 한글, 영문, 숫자를 사용해 주세요.</span>
          </div>

          <div class="col-md-12">
            <!-- 성별 -->
            <label class="form-label fw-bold"
              >성별
              <span class="text-danger fw-normal warning" v-show="click" v-text="warning.gender"></span>
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
              <span class="text-danger fw-normal warning" v-show="click" v-text="warning.birthDate"></span>
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
          <div class="col-md-12">
            <div class="row align-items-center">
              <!-- 키 -->
              <div class="mb-3">
                <label class="form-label">키 (cm)</label>
                <input
                  type="number"
                  class="form-control"
                  v-model="optionResult.height"
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
                  v-model="optionResult.weight"
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
                  v-model="optionResult.targetWeight"
                  min="0"
                  required
                />
              </div>
            </div>
          </div>

          <div class="d-flex justify-content-between">
            <button class="btn btn-secondary" @click="prevBasic">이전</button>
            <button type="button" class="btn btn-primary" @click="handleSignup">
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
    <!-- ConfirmDialog 컴포넌트를 ref 로 선언 -->
    <ConfirmDialog ref="confirmDialog" />
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

.warning {
  font-size: small;
}
</style>

<script>
import axios from '@/plugins/axios';
import ConfirmDialog from '@/components/ConfirmDialog.vue';

export default {
  name: 'SignupView', // name은 index.js에 선언해놓은 이름과 똑같아야함
  components: { ConfirmDialog },
  data() {
    return {
      isbasic: true,
      result: {
        email: '',
        password: '',
        checkPassword: '',
        checkedPasswordSame: '',
        username: '',
        gender: '',
        birthDate: '',
        checkedEmail: '',
        checkedUsername: '',
        checkedEmailRule:'',
        checkedPasswordRule:'',
        checkedUsernameRule:'',
        checkedBirthDateRule:''
      },
      success:{
        email : '',
        username : ''
      },
      optionResult :{
        height: '',
        weight: '',
        targetWeight: '',
      },
      click: false,
      warning: {
        email: '',
        password: '',
        checkPassword: '',
        username: '',
        gender: '',
        birthDate: '',
      },
    };
  },
  created() {},
  methods: {
    prevBasic() {
      this.isbasic = true;
    },

    nextBasic() {
      this.warningMessage()
      if(Object.values(this.result).includes('')){
        this.click = true
        return
      }

      this.isbasic = false;
    },
    warningMessage(){
      if(!this.result.email){
        this.warning.email = "이메일을 입력하세요"
        this.result.checkedEmail = ''
      }
      else if(this.result.email && !this.result.checkedEmail || this.success.email !== this.result.email){
        this.result.checkedEmail = '' 
        this.warning.email = "이메일 중복을 확인하세요"
      }
      else if(this.result.email){
        if(this.checkEmailRule()){
          this.warning.email = ''
        }
        else{
          this.warning.email = '이메일을 정확히 입력하세요'
        }
      }

      if(!this.result.password){
        this.warning.password = "비밀번호를 입력하세요"
      }
      else{
        if(this.checkPasswordRule()){
          this.warning.password = ''
        }
        else{
          this.warning.password = '비밀번호가 규칙에 맞지 않습니다'
        }
      }

      if(!this.result.checkPassword){
        this.warning.checkPassword = "비밀번호를 입력하세요"
        this.result.checkedPasswordSame = '';
      }
      else if(this.result.checkPassword !== this.result.password){
        this.warning.checkPassword = "비밀번호가 일치하지 않습니다"
        this.result.checkedPasswordSame = '';
      }
      else{
        this.warning.checkPassword = ''
        this.result.checkedPasswordSame = true;
      }

      if(!this.result.username){
        this.warning.username = '닉네임을 입력하세요'
        this.result.checkedUsername = ''
      }
      else if((this.result.username && !this.result.checkedUsername) || this.success.username !==this.result.username){
        this.result.checkedUsername = ''
        this.warning.username = "닉네임 중복을 확인하세요"
      }
      else if(this.result.username){
        if(this.checkUsernameRule()){
          this.warning.username = ''
        }
        else{
          this.warning.username = '닉네임이 규칙에 맞지않습니다'
        }
      }

      if(this.result.gender === ''){
        this.warning.gender = '성별을 선택하세요'
      }
      else{
        this.warning.gender = ''
      }

      if (!this.result.birthDate) {
        this.warning.birthDate = '생년월일을 입력하세요';
        this.result.checkedBirthDateRule = '';
      } else if (!this.isValidCompactDate()) {
        this.warning.birthDate = '생년월일을 정확히 입력하세요'
        this.result.checkedBirthDateRule = '';
      }
      else{
        this.result.checkedBirthDateRule = true;
        this.warning.birthDate = ''
      }
    },
    async handleSignup() {
      // 빈 값이 있으면
      if (Object.values(this.optionResult).includes('')) {
        const ok = await this.$refs.confirmDialog.open({
          title: '선택정보 확인',
          message: `
            선택정보 미입력 시 정확한 식단분석이 불가능합니다.<br/>
            그래도 진행하시겠습니까?
          `
        });
        if (!ok) {
          // 유저가 취소 눌렀을 때
          return;
        }
      }
      // 확인 눌렀거나, 애초에 빈 값이 없을 때
      this.save();
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
        height: this.optionResult.height,
        weight: this.optionResult.weight,
        targetWeight: this.optionResult.targetWeight,
      };

      axios
        .post('/api/users', post)
        .then((res) => {
          this.$router.push({ name: 'LoginView' });
        })
        .catch((err) => {
          console.log(err);
        });
    },
    isValidCompactDate() {
      const pattern = /^(19|20)\d{2}(?:(?:0[13578]|1[02])(?:0[1-9]|[12]\d|3[01])|(?:0[469]|11)(?:0[1-9]|[12]\d|30)|02(?:0[1-9]|1\d|2\d))$/;
      if (!pattern.test(this.result.birthDate)) {
        this.result.checkedBirthDateRule = '';
        return false;
      }
      // 윤년이면서 2월 29일인 경우만 별도 허용
      const year = parseInt(this.result.birthDate.slice(0,4), 10);
      const month = this.result.birthDate.slice(4,6), day = this.result.birthDate.slice(6,8);
      if (month === "02" && day === "29") {
        if((year % 4 === 0 && year % 100 !== 0) || (year % 400 === 0)){
          return true
        }
        this.result.checkedBirthDateRule = '';
        return false;
      }
      return true;
    },
    checkEmail() {
      this.click = true
      if (!this.result.email) {
        this.result.checkedEmail = '';
        this.warning.email = '이메일을 입력하세요'
        return;
      }
      
      if(!this.checkEmailRule()){
        this.result.checkedEmail = '';
        this.warning.email = '이메일을 정확히 입력하세요'
        return
      }

      axios
        .get('/api/users/check-email', { params: { email: this.result.email } })
        .then((res) => {
          this.result.checkedEmail = true;
          this.success.email = this.result.email
        })
        .catch((err) => {
          this.warning.email = '사용 불가능한 이메일 입니다'
          this.result.checkedEmail = '';
        });
    },
    checkUsername() {
      this.click = true
      if (!this.result.username) {
        this.result.checkedUsername = '';
        this.warning.username = '닉네임을 입력하세요'
        return
      }

      if(!this.checkUsernameRule()){
        this.result.checkedUsername = '';
        this.warning.username = '닉네임이 규칙에 맞지 않습니다'
        return
      }

      axios
        .get('/api/users/check-username', {
          params: { username: this.result.username },
        })
        .then((res) => {
          this.result.checkedUsername = true;
          this.success.username = this.result.username
        })
        .catch((err) => {
          this.warning.username = '사용 불가능한 닉네임 입니다'
          this.result.checkedUsername = '';
        });
    },
    checkEmailRule(){
      // 이메일 유효성 검사를 위한 정규식 표현 
      const pattern = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+/;
      if(pattern.test(this.result.email)){
        this.result.checkedEmailRule = true
        return true
      }

      this.result.checkedEmailRule = ''
      return false;
    },
    checkPasswordRule(){
      const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{}|;':",.<>\/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{}|;':",.<>\/?]{8,16}$/;
      if(pattern.test(this.result.password)){
        this.result.checkedPasswordRule = true
        return true
      }

      this.result.checkedPasswordRule = ''
      return false
    },
    checkUsernameRule(){
      const pattern = /^[A-Za-z0-9\u1100-\u11FF\u3130-\u318F\uAC00-\uD7A3]{4,10}$/;
      if(pattern.test(this.result.username)){
        this.result.checkedUsernameRule = true
        return true
      }
      
      this.result.checkedUsernameRule = ''
      return false
    }
  },
};
</script>
