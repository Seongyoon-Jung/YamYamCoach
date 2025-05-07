import { createStore } from 'vuex'
// import createPersistedState from "vuex-persistedstate";

//https://www.youtube.com/watch?v=htYYSszfzv0&t=5501s
// 1:20:39

const store = createStore({
    state(){
        return {
            account:{
                username:'',
                isSurveyed:false
            }
        }
    },
    //metations에서 mutations로 바뀜!!!!
    mutations: {
        setAccount(state, {username, isSurveyed}){
            state.account.username = username
            state.account.isSurveyed = isSurveyed
        },
        clearAccount(state){
            state.account.username = '';
            state.account.isSurveyed = false;
        }
    },
    // plugins : [ createPersistedState({
    //     paths: ["account"]
    // }) ],
})

export default store;