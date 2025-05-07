<template>
  <div id="app" class="d-flex flex-column min-vh-100">
    <HeaderComponent />
    <main class="flex-grow-1 pt-5">
      <router-view />
    </main>
    <FooterComponent />
  </div>
</template>

<script setup>
import HeaderComponent from '@/components/HeaderComponent.vue';
import FooterComponent from '@/components/FooterComponent.vue';
</script>

<script>
  import axios from '@/plugins/axios';
  import store from '@/store'

  export default {
    created(){
      axios.get('/api/users/me') 
        .then((res) => {
          if(res.data =='anonymousUser'){
            store.commit('setAccount',{userId:null, username: '', isSurveyed:false})
          }
          else{
            store.commit('setAccount',{userId : res.data.userId , username : res.data.username, isSurveyed : res.data.surveyed})
          }
        });
    },
};

</script>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}

/* 다크 모드용 예시 */
.dark-mode {
  background-color: #121212;
  color: #eee;
}

</style>
