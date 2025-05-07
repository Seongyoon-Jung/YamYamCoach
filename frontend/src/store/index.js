import { createStore } from 'vuex'
// import createPersistedState from "vuex-persistedstate";

//https://www.youtube.com/watch?v=htYYSszfzv0&t=5501s
// 1:20:39

const store = createStore({
    state(){
        return {
            account:{
                userId:null,
                username:'',
                isSurveyed:false
            }
        }
    },
    //metations에서 mutations로 바뀜!!!!
    mutations: {
        setAccount(state, {userId, username, isSurveyed}){
            state.account.userId = userId
            state.account.username = username
            state.account.isSurveyed = isSurveyed
        },
        clearAccount(state){
            state.account.userId = null
            state.account.username = ''
            state.account.isSurveyed = false
        }
    },
    // plugins : [ createPersistedState({
    //     paths: ["account"]
    // }) ],
})

export default store;