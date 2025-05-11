<template>
  <main class="mt-5 pt-4 pb-5 position-relative">
    <div class="container" style="max-width: 600px">
      <!-- 뒤로가기 + 제목 -->
      <h2 class="fw-bold mb-4">개인정보 수정</h2>

      <form>
        <!-- 기본 정보 섹션 -->
        <h6 class="mb-3">기본 정보</h6>
        <div class="card mb-4 p-3">
          <div  class="row g-3 text-start col-md-12">
            <!-- 이메일 -->
            <div class="col-md-12 mb-3">
              <!-- 레이블을 input-group 위에 두고 form-label 클래스로 왼쪽 정렬 -->
              <label for="email" class="form-label fw-bold"
                >이메일
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
                  readonly
                  disabled
                />
              </div>
            </div>

            <!-- 닉네임 -->
            <div class="col-md-12 mb-3">
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

            <div class="col-md-12 mb-3">
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
            <div class="col-md-12 mb-0">
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
          </div>
        </div>

       

        <!-- 추가 정보 섹션 -->
        <h6 class="mb-3">추가 정보</h6>
        <div class="card mb-4 p-3">
          <div  class="row g-3 text-start col-md-12">
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
          <button class="btn btn-outline-secondary px-5" @click="goBack"
            >취소</button
          >
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

<script>
import axios from '@/plugins/axios';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import store from '@/store';

export default {
  name: 'MyInfoView',
  components: { ConfirmDialog },
  data() {
    return {
      result : {
        email: '',
        username: '',
        gender: '',
        birthDate: '',
        checkedUsername: '',
        checkedUsernameRule: '',
      },
      optionResult : {
        height: '',
        weight: '',
        targetWeight: '',
      },
      click: false,
      warning: {
        email: '',
        username: '',
        gender: '',
        birthDate: '',
      },
      success: {
        username:'',
      }
    };
  },
  created() {
    axios
      .get('/api/users')
      .then((res) => {
        this.result.email = res.data.email;
        this.result.username = res.data.username;
        this.result.gender = res.data.gender;
        this.result.birthDate = res.data.birthDate.replaceAll("-","");
        this.optionResult.height = res.data.height;
        this.optionResult.weight = res.data.weight;
        this.optionResult.targetWeight = res.data.targetWeight;
        this.success.username = res.data.username;
        this.result.checkedUsername = true,
        this.result.checkedUsernameRule = true
      })
      .catch((err) => {});
  },
  methods: {
    async handleUpdate() {
      this.warningMessage()
      if(Object.values(this.result).includes('')){
        this.click = true
        return
      }


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
        userId : this.$store.state.account.userId,
        username: this.result.username,
        gender: this.result.gender,
        birthDate: stringToDate,
        height: this.optionResult.height,
        weight: this.optionResult.weight,
        targetWeight: this.optionResult.targetWeight,
      };

      axios
        .put('/api/users', post)
        .then((res) => {
          this.$store.state.account.username = this.result.username;
          this.$router.push({ name: 'MyView' });
        })
        .catch((err) => {
          console.log(err);
        });
    },
    warningMessage(){
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
          his.result.checkedUsername = ''
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
      } else if (!this.isValidCompactDate()) {
        this.warning.birthDate = '생년월일을 정확히 입력하세요'
      }
      else{
        this.warning.birthDate = ''
      }
    },
    isValidCompactDate() {
      const pattern = /^(19|20)\d{2}(?:(?:0[13578]|1[02])(?:0[1-9]|[12]\d|3[01])|(?:0[469]|11)(?:0[1-9]|[12]\d|30)|02(?:0[1-9]|1\d|2\d))$/;
      if (!pattern.test(this.result.birthDate)) {
        return false;
      }
      // 윤년이면서 2월 29일인 경우만 별도 허용
      const year = parseInt(this.result.birthDate.slice(0,4), 10);
      const month = this.result.birthDate.slice(4,6), day = this.result.birthDate.slice(6,8);
      if (month === "02" && day === "29") {
        return (year % 4 === 0 && year % 100 !== 0) || (year % 400 === 0);
      }
      return true;
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

      if(this.result.username == this.success.username){
        this.click = false
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
    checkUsernameRule(){
      const pattern = /^[A-Za-z0-9\u1100-\u11FF\u3130-\u318F\uAC00-\uD7A3]{4,10}$/;
      if(pattern.test(this.result.username)){
        this.result.checkedUsernameRule = true
        return true
      }
      
      this.result.checkedUsernameRule = ''
      return false
    },
    async handleDelete() {
  
      const ok = await this.$refs.confirmDialog.open({
        title: '회원 탈퇴 확인',
        message: `
          회원 탈퇴 시 모든 정보가 삭제되고 복구가 불가능합니다.<br/>
          그래도 진행하시겠습니까?
        `
      });
      if (!ok) {
        return;
      }
      
      // 확인 눌렀거나
      this.delete()
      
    },
    delete(){
      axios
        .delete('/api/users', {
          params: { userId: this.$store.state.account.userId },
        })
        .then((res) => {
          store.commit('clearAccount');
          this.$router.push({ name: 'HomeView' });
        })
        .catch((err) => {

        });
    },
    goBack(){
      this.$router.go(-1)
    }
  },
};
</script>

<style scoped>
.hero {
  min-height: 0; /* 기존 Hero 가 있다면 제거 */
}
</style>
