import { ref } from 'vue'

// 이벤트 버스 구현
const listeners = {}

export default {
  // 이벤트 리스너 등록
  on(event, callback) {
    if (!listeners[event]) {
      listeners[event] = []
    }
    listeners[event].push(callback)
  },
  
  // 이벤트 발생
  emit(event, ...args) {
    if (listeners[event]) {
      listeners[event].forEach(callback => callback(...args))
    }
  },
  
  // 이벤트 리스너 제거
  off(event, callback) {
    if (listeners[event]) {
      if (callback) {
        listeners[event] = listeners[event].filter(cb => cb !== callback)
      } else {
        listeners[event] = []
      }
    }
  }
} 