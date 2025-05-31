import { createApp } from 'vue'
import App from './App.vue'

// 라우터
import router from './router'

// Pinia
import { createPinia } from 'pinia'
import { userAccountStore } from '@/store/account' // ✅ store 직접 사용

// axios
import axios from '@/plugins/axios'

// Bootstrap
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

async function bootstrap() {
  const pinia = createPinia()
  const app = createApp(App)

  app.use(pinia)

  // Pinia 스토어 인스턴스 생성 (useXXX는 앱 등록 후에 호출)
  const accountStore = userAccountStore()

  try {
    const res = await axios.get('/api/users/me')

    if (res.data === 'anonymousUser') {
      accountStore.setAccount({
        userId: null,
        username: '',
        personaId: -1,
        role: '',
        profileUrl: '',
      })
    } else {
      let profileUrl = res.data.profileUrl

      // presigned URL 요청
      if (profileUrl) {
        try {
          const presigned = await axios.get('/api/s3/get-url', {
            params: { filename: profileUrl },
          })
          profileUrl = presigned.data
        } catch (err) {
          console.warn('Presigned URL 요청 실패:', err)
        }
      }
      accountStore.setAccount({
        userId: res.data.userId,
        username: res.data.username,
        personaId: res.data.personaId ?? -1,
        role: res.data.role,
        profileUrl: profileUrl,
      })
    }
  } catch (e) {}

  app.use(router).mount('#app')
}

bootstrap()
