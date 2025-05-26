<template>
  <div class="cli-error d-flex justify-content-center align-items-center min-vh-100">
    <pre class="cli-text text-start">
$ remember --crew "SSAFY 13기 12반"
✅ 메모리에 저장되었습니다. SSAFY_13기_12반.기억

$ run ./forever-13기-12반

<span class="text-danger">❗ Error 404:</span>  SSAFY 13기 12반의 흔적은 이 경로에 없지만...
<img src="/public/pxArt.png" class="pixel-photo" alt="SSAFY 픽셀단체사진" />
💡 우리는 영원히 메모리에 남아있습니다.

[ Tip ] 홈으로 가려면 아래 명령어를 입력하세요:
$ <router-link to="/" class="go-home">go-home 🚀</router-link><span class="cursor">▊</span>
    </pre>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// keydown 이벤트 핸들러
const handleKeyDown = (event) => {
  if (event.key === 'Enter') {
    router.push('/')
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeyDown)
})
</script>

<style scoped>
.cli-error {
  background-color: #111;
  color: #00ff00;
  font-family: 'Courier New', monospace;
  padding: 2rem;
}

.cli-text {
  white-space: pre-wrap;
  font-size: 1.2rem;
  line-height: 1.7;
  max-width: 90vw;
}

.go-home {
  color: #00ffff;
  text-decoration: none;
  font-weight: bold;
}

.go-home:hover {
  color: #00ccff;
  text-decoration: underline;
}

.cursor {
  animation: blink 1s steps(2, start) infinite;
}

@keyframes blink {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

.pixel-photo {
  display: block;
  margin: 1.5rem auto;
  max-width: 320px;
  width: 90%;
  border: 2px dashed #00ff00;
  padding: 0.5rem;
  image-rendering: pixelated;
  background-color: #222;
}
</style>
