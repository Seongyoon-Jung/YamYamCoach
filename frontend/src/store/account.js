// src/stores/account.js
import { defineStore } from 'pinia'

export const userAccountStore = defineStore('account', {
  state: () => ({
    userId: null,
    username: '',
    personaId: false,
    role: '',
  }),
  actions: {
    setAccount({ userId, username, personaId, role }) {
      this.userId = userId
      this.username = username
      this.personaId = personaId
      this.role = role
    },
    clearAccount() {
      this.userId = null
      this.username = ''
      this.personaId = false
      this.role = ''
    },
  },
})
