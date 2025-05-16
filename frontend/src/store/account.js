// src/stores/account.js
import { defineStore } from 'pinia'

export const userAccountStore = defineStore('account', {
  state: () => ({
    userId: null,
    username: '',
    isSurveyed: false,
    role: '',
  }),
  actions: {
    setAccount({ userId, username, isSurveyed, role }) {
      this.userId = userId
      this.username = username
      this.isSurveyed = isSurveyed
      this.role = role
    },
    clearAccount() {
      this.userId = null
      this.username = ''
      this.isSurveyed = false
      this.role = ''
    },
  },
})
