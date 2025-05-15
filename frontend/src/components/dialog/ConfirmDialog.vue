<!-- src/components/ConfirmDialog.vue -->
<template>
  <teleport to="body">
    <div
      class="modal fade show custom-modal"
      tabindex="-1"
      role="dialog"
      aria-modal="true"
      style="display: block"
      v-if="visible"
    >
      <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ title }}</h5>
          </div>
          <div class="modal-body">
            <p v-html="message"></p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="cancel">취소</button>
            <button type="button" class="btn btn-primary" @click="confirm">확인</button>
          </div>
        </div>
      </div>
      <!-- backdrop -->
      <div class="modal-backdrop fade show custom-backdrop"></div>
    </div>
  </teleport>
</template>

<script setup>
import { ref, defineExpose } from 'vue'

// 상태 변수 정의
const visible = ref(false)
const title = ref('')
const message = ref('')
const resolveFn = ref(null)

// open 메서드는 Promise를 반환
function open({ title: t = '확인', message: m = '' }) {
  title.value = t
  message.value = m
  visible.value = true

  return new Promise((resolve) => {
    resolveFn.value = resolve
  })
}

function confirm() {
  visible.value = false
  if (resolveFn.value) {
    resolveFn.value(true)
    resolveFn.value = null
  }
}

function cancel() {
  visible.value = false
  if (resolveFn.value) {
    resolveFn.value(false)
    resolveFn.value = null
  }
}

// 외부에서 open()을 호출할 수 있도록 노출
defineExpose({
  open,
})
</script>

<!-- <script>
export default {
  name: 'ConfirmDialog',
  data() {
    return {
      visible: false,
      title: '',
      message: '',
      resolveFn: null,
    };
  },
  methods: {
    open({ title = '확인', message = '' }) {
      this.title = title;
      this.message = message;
      this.visible = true;
      return new Promise((resolve) => {
        this.resolveFn = resolve;
      });
    },
    confirm() {
      this.visible = false;
      this.resolveFn && this.resolveFn(true);
      this.resolveFn = null;
    },
    cancel() {
      this.visible = false;
      this.resolveFn && this.resolveFn(false);
      this.resolveFn = null;
    },
  },
};
</script> -->

<style scoped>
/* 뷰포트 최상단에 뜨도록 fixed + z-index 최상위 계층 */
.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 2050; /* Bootstrap 모달 기본(1050)보다 높게 */
  overflow: auto;
}
/* backdrop 은 모달 바로 아래 */
.custom-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 2040;
}

.modal-content {
  z-index: 2060;
}
</style>
