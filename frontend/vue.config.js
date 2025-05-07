const { defineConfig } = require('@vue/cli-service');
module.exports = defineConfig({
  transpileDependencies: true,
});

//cors 설정해주기
// const target = 'http://localhost:8080';
// 현업에서는 프론트는 3000대 백은 8000대로 정해놓고 하는 경우가 많다
module.exports = {
  devServer: {
    port: 3000, // 여기서 포트 설정
    open: true, // 브라우저 자동 열기 (선택사항)
  },
};
