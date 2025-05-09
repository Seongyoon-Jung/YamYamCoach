<template>
  <main class="pt-4 pb-5">
    <div class="container" style="max-width: 600px">
      <!-- 뒤로가기 + 제목 -->
      <div class="d-flex align-items-center mb-4">
        <router-link to="/" class="me-3 text-decoration-none text-dark">
          <i class="bi bi-arrow-left fs-4"></i>
        </router-link>
        <h4 class="mb-0">개인정보 수정</h4>
      </div>

      <form @submit.prevent="updateProfile">
        <!-- 기본 정보 섹션 -->
        <h6 class="mb-3">기본 정보</h6>
        <div class="card mb-4 p-3">
          <!-- 이메일 (읽기 전용) -->
          <div class="mb-3">
            <label class="form-label">이메일 주소</label>
            <input
              type="email"
              class="form-control"
              v-model="form.email"
              readonly
            />
          </div>

          <!-- 닉네임 -->
          <div class="mb-3">
            <label class="form-label">닉네임</label>
            <div class="input-group">
              <input
                type="text"
                class="form-control"
                v-model="form.username"
                required
              />
              <button
                class="btn btn-outline-secondary"
                type="button"
                @click="checkUsername"
              >
                중복 확인
              </button>
            </div>
          </div>

          <!-- 성별 -->
          <div class="mb-3">
            <label class="form-label d-block">성별</label>
            <div class="btn-group" role="group">
              <input
                type="radio"
                class="btn-check"
                name="gender"
                id="male"
                :value="false"
                v-model="form.gender"
                autocomplete="off"
              />
              <label class="btn btn-outline-secondary" for="male">남성</label>

              <input
                type="radio"
                class="btn-check"
                name="gender"
                id="female"
                :value="true"
                v-model="form.gender"
                autocomplete="off"
              />
              <label class="btn btn-outline-secondary" for="female">여성</label>
            </div>
          </div>

          <!-- 생년월일 -->
          <div class="mb-0">
            <label class="form-label">생년월일</label>
            <input
              type="date"
              class="form-control"
              v-model="form.birthDate"
              required
            />
          </div>
        </div>

        <!-- 추가 정보 섹션 -->
        <h6 class="mb-3">추가 정보</h6>
        <div class="card mb-4 p-3">
          <!-- 키 -->
          <div class="mb-3">
            <label class="form-label">키 (cm)</label>
            <input
              type="number"
              class="form-control"
              v-model.number="form.height"
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
              v-model.number="form.weight"
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
              v-model.number="form.targetWeight"
              min="0"
              required
            />
          </div>
        </div>

        <!-- 저장/취소 버튼 -->
        <div class="d-flex justify-content-center gap-3">
          <button type="submit" class="btn btn-primary px-5">저장</button>
          <router-link to="/" class="btn btn-outline-secondary px-5"
            >취소</router-link
          >
        </div>
      </form>
    </div>
  </main>
</template>

<script>
import axios from '@/plugins/axios';
import { mapState } from 'vuex';

export default {
  name: 'MyInfoView',
  data() {
    return {
      form: {
        email: '',
        username: '',
        gender: null,
        birthDate: '',
        height: null,
        weight: null,
        targetWeight: null,
        checkedUsername: false,
      },
    };
  },
  created() {
    axios
      .get('/api/users')
      .then((res) => {
        this.form.email = res.data.email;
        this.form.username = res.data.username;
        this.form.gender = res.data.gender;
        this.form.birthDate = res.data.birthDate;
        this.form.height = res.data.height;
        this.form.weight = res.data.weight;
        this.form.targetWeight = res.data.targetWeight;
      })
      .catch((err) => {});
  },
  methods: {},
};
</script>

<style scoped>
.hero {
  min-height: 0; /* 기존 Hero 가 있다면 제거 */
}
</style>
