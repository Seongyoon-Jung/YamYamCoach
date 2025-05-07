import { createApp } from 'vue';
import App from './App.vue';
// 라우터 기능을 사용하기 위한 임포트
import router from './router';
// 스토어 기능을 사용하기 위한 임포트
import store from './store';

//@/ 는 src부터 찾겠다라는 뜻

// bootstrap을 쓰기위한 import
import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css'

createApp(App).use(store).use(router).mount('#app');
