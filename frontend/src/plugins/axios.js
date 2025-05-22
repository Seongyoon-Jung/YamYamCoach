// plugins/axios.js
import axios from 'axios'
import { useLoadingStore } from '@/store/loading'
import { storeToRefs } from 'pinia'

// Axios 인스턴스 생성
const instance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
})

// ✅ 인터셉터 등록
instance.interceptors.request.use(
  (config) => {
    const loadingStore = useLoadingStore()
    loadingStore.startLoading()
    return config
  },
  (error) => {
    const loadingStore = useLoadingStore()
    loadingStore.stopLoading()
    return Promise.reject(error)
  },
)

instance.interceptors.response.use(
  (response) => {
    const loadingStore = useLoadingStore()
    loadingStore.stopLoading()
    return response
  },
  (error) => {
    const loadingStore = useLoadingStore()
    loadingStore.stopLoading()
    return Promise.reject(error)
  },
)

export default instance
