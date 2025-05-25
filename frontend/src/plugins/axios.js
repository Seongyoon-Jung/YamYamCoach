import axios from 'axios'
import { userAccountStore } from '@/store/account'

const instance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
})

// 요청 인터셉터 추가
instance.interceptors.request.use(
  config => {
    // 스토어에서 사용자 정보 가져오기
    const store = userAccountStore()
    if (store.userId) {
      // 사용자 ID가 있으면 헤더에 추가
      config.headers['X-USER-ID'] = store.userId
    }
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 응답 인터셉터 추가
instance.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    // 401 Unauthorized 에러 처리
    if (error.response && error.response.status === 401) {
      console.log('인증이 필요합니다. 로그인 페이지로 이동합니다.');
      // 인증이 필요한 경우 로그인 페이지로 리다이렉트
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default instance
