import { createApp } from 'vue';
import App from './App.vue';
// 라우터 기능을 사용하기 위한 임포트
import router from './router';
// 스토어 기능을 사용하기 위한 임포트
import store from './store';
import axios from '@/plugins/axios';
//@/ 는 src부터 찾겠다라는 뜻

// bootstrap을 쓰기위한 import
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

async function bootstrap() {
  try {
    const res = await axios.get('/api/users/me');
    if (res.data === 'anonymousUser') {
      store.commit('setAccount', {
        userId: null,
        username: '',
        isSurveyed: false,
      });
    } else {
      store.commit('setAccount', {
        userId: res.data.userId,
        username: res.data.username,
        isSurveyed: res.data.surveyed,
      });
    }
  } catch (e) {
    console.error('유저 정보 로딩 실패:', e);
    // 필요하면 익셉션 처리
  }

  createApp(App).use(store).use(router).mount('#app');
}

// 진입 전에 부트스트랩 실행
bootstrap();
