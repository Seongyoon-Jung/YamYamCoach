<template>
  <div class="d-flex align-items-center justify-content-center min-vh-100">
    <div
      class="card p-5 shadow-lg"
      style="max-width: 700px; width: 100%; border-radius: 1rem"
    >
      <h3 class="text-center fw-bold mb-4 text-dark">회원 정보 입력 (필수)</h3>
      <hr />

      <form>
        <div class="row g-3">
          <!-- 이메일 -->
          <div class="col-md-12">
            <div class="row align-items-center">
              <label for="email" class="col-md-2 col-form-label fw-bold"
                >이메일</label
              >
              <div class="col-md-10">
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
            </div>
          </div>

          <!-- 비밀번호 -->
          <div class="col-md-12">
            <div class="row align-items-center">
              <label for="password" class="col-md-2 col-form-label fw-bold"
                >비밀번호</label
              >
              <div class="col-md-10">
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
            </div>
          </div>

          <!-- 닉네임 -->
          <div class="col-md-12">
            <div class="row align-items-center">
              <label for="username" class="col-md-2 col-form-label fw-bold"
                >닉네임</label
              >
              <div class="col-md-10">
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
                  <button type="button" class="btn btn-outline-secondary"  @click="checkUsername">
                    중복 확인
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 성별 -->
          <div class="col-md-12">
            <div class="row align-items-center">
              <label class="col-md-2 col-form-label fw-bold">성별</label>
              <div class="col-md-10">
                <div
                  class="d-flex align-items-center gap-3 border rounded px-3 py-2"
                  style="height: 38px"
                >
                  <div class="form-check form-check-inline m-0">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="gender"
                      id="male"
                      v-bind:value="false"
                      v-model="result.gender"
                    />
                    <label class="form-check-label mb-0" for="male">남성</label>
                  </div>
                  <div class="form-check form-check-inline m-0">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="gender"
                      id="female"
                      v-bind:value="true"
                      v-model="result.gender"
                    />
                    <label class="form-check-label mb-0" for="female"
                      >여성</label
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 생년월일 -->
          <div class="col-md-12">
            <div class="row align-items-center">
              <label for="birthdate" class="col-md-2 col-form-label fw-bold"
                >생년월일</label
              >
              <div class="col-md-10">
                <input
                  type="text"
                  id="birthdate"
                  class="form-control"
                  required
                  name="birthDate"
                  placeholder="예) 20000101 (8자리)"
                  v-model="result.birthDate"
                />
              </div>
            </div>
          </div>

          <!-- 가입 버튼 -->
          <div class="col-12 text-center mt-4">
            <!-- 일단 submit하지말고 console 출력확인을 위해 버튼으로 임시설정 -->
            <button type="button" class="btn btn-primary px-5" @click="save">
              회원가입
            </button>
          </div>
        </div>
      </form>

      <div class="text-center mt-4">
        <router-link
          to="/login"
          class="text-decoration-none text-white-50 small"
        >
          이미 계정이 있으신가요?
          <span class="fw-semibold text-white">로그인</span>
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
      result: {
        email: '',
        password: '',
        username: '',
        gender: null,
        birthDate: '',
        checkedEmail:false,
        checkedUsername:false,
      },
    };
  },
  created() {},
  methods: {
    save() {
      if(!this.result.checkedEmail){
        alert('이메일 중복 검사 필요')
        return
      }

      if(!this.result.checkedUsername){
        alert('닉네임 중복 검사 필요')
        return
      }

      if (this.result.email == '') {
        alert('이메일 입력필요');
        return;
      }

      if (this.result.password == '') {
        alert('비밀번호 입력필요');
        return;
      }

      if (this.result.username == '') {
        alert('닉네임 입력필요');
        return;
      }

      if (this.result.gender == null) {
        alert('성별 입력필요');
        return;
      }

      //사용자 편의를 위해서 날짜입력을 텍스트 값으로 했기 때문에 맞는 생년월일이 들어와야하는 지 함수로 판단
      if(this.isValidCompactDate(this.result.birthDate)){
        var str = this.result.birthDate;
        str = str.substring(0,4) + "-" +str.substring(4,6)+"-"+str.substring(6,8)
        this.result.birthDate = str
      }
      //아무것도 입력하지 않았을 경우
      else if(this.result.birthDate ==''){
        alert('생년월일을 입력필요.');
        return;
      }
      else {
        console.log(this.result.birthDate)
        alert('생년월일을 제대로 입력해주세요.');
        return;
      }

      console.log(this.result);

      axios
        .post('/api/users', this.result)
        .then((res) => {
          console.log(res);
          alert("회원가입이 완료되었습니다.")
          this.$router.push({name:'LoginView'})
        })
        .catch((err) => {
          console.log(err);
        });
    },
    isValidCompactDate(str) {
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
    checkEmail(){
      if(this.result.email == ''){
        alert('이메일 입력필요')
        return 
      }
      axios
        .get('/api/users/check-email',{params:{email:this.result.email}})
        .then((res) => {
          console.log(res);
          alert("사용 가능한 이메일 입니다")
          this.result.checkedEmail = true;
        })
        .catch((err) => {
          alert("사용 불가능한 이메일 입니다")
          console.log(err);
          this.result.email='';
          this.result.checkedEmail = false;
        });
    },
    checkUsername(){
      if(this.result.username == ''){
        alert('닉네임 입력필요')
        return 
      }
      axios
        .get('/api/users/check-username',{params:{username:this.result.username}})
        .then((res) => {
          console.log(res);
          alert("사용 가능한 닉네임 입니다")
          this.result.checkedUsername = true;
        })
        .catch((err) => {
          alert("사용 불가능한 닉네임 입니다")
          console.log(err);
          this.result.username='';
          this.result.checkedUsername = false;
        });
    }
  },
};
</script>
