<template>
  <div class="d-flex align-items-center justify-content-center min-vh-100">
    <div
      class="card p-5 shadow-lg"
      style="max-width: 450px; width: 100%; border-radius: 1rem"
    >
      <h3 class="text-center fw-bold text-dark">로그인</h3>
      <hr />

      <form>
        <div class="row g-3">
          <span class="text-danger" v-show="click" v-text="warning"></span>
          <!-- 이메일 -->
          <div class="col-md-12">
            <div class="row align-items-center">
              <label for="email" class="col-md-3 col-form-label fw-bold"
                >이메일</label
              >
              <div class="col-md-9">
                <input
                  type="email"
                  id="email"
                  class="form-control"
                  placeholder="example@domain.com"
                  name="email"
                  v-model="result.email"
                />
              </div>
            </div>
          </div>

          <!-- 비밀번호 -->
          <div class="col-md-12">
            <div class="row align-items-center">
              <label for="password" class="col-md-3 col-form-label fw-bold"
                >비밀번호</label
              >
              <div class="col-md-9">
                <input
                  type="password"
                  id="password"
                  class="form-control"
                  placeholder="비밀번호를 입력하세요"
                  name="password"
                  v-model="result.password"
                />
              </div>
            </div>
          </div>

          <!-- 로그인 버튼 -->
          <div class="col-md-12 text-center mt-4">
            <button
              type="button"
              class="btn btn-success w-100 mb-3"
              @click="login"
            >
              로그인
            </button>
            <router-link
              class="btn btn-outline-primary w-100 mb-3"
              to="/signup"
            >
              회원가입
            </router-link>
          </div>

          <!-- 아이디 / 비밀번호 찾기 -->
          <div class="col-md-12 d-flex justify-content-between text-sm">
            <a href="#">아이디 찾기</a>
            <a href="#">비밀번호 찾기</a>
          </div>
        </div>
      </form>
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
import store from '@/store';

export default {
  name: 'SignupView', // name은 index.js에 선언해놓은 이름과 똑같아야함
  data() {
    return {
      result: {
        email: '',
        password: '',
      },
      click: false,
      warning:""
    };
  },
  created() {},
  methods: {
    login() {
      this.click = true
      if (this.result.email == '') {
        this.warning = "이메일을 입력해주세요"
        return;
      }

      if (this.result.password == '') {
        this.warning = "비밀번호를 입력해주세요"
        return;
      }

      //  is머시기 boolean 타입으로 넘어오면 json이 강제로 surveyed로 바꿔버린다
      axios
        .post('/api/auth/login', this.result)
        .then((res) => {
          store.commit('setAccount', {
            userId: res.data.userId,
            username: res.data.username,
            isSurveyed: res.data.surveyed,
          });

          this.$router.push({ name: 'HomeView' });
          
        })
        .catch((err) => {
          this.warning = "이메일 또는 비밀번호가 일치하지 않습니다"
        });
    },
  },
};
</script>
