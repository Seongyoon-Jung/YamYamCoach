// src/stores/account.js
import { defineStore } from 'pinia'

export const userAccountStore = defineStore('account', {
  state: () => ({
    userId: null,
    username: '',
    isSurveyed: false,
  }),
  actions: {
    setAccount({ userId, username, isSurveyed }) {
      this.userId = userId
      this.username = username
      this.isSurveyed = isSurveyed
    },
    clearAccount() {
      this.userId = null
      this.username = ''
      this.isSurveyed = false
    },
  },
})
