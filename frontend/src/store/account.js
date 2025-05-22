// src/stores/account.js
import { defineStore } from 'pinia'

export const userAccountStore = defineStore('account', {
  state: () => ({
    userId: null,
    username: '',
    personaId: false,
    role: '',
    profileUrl: '',
  }),
  actions: {
    setAccount({ userId, username, personaId, role, profileUrl }) {
      this.userId = userId
      this.username = username
      this.personaId = personaId
      this.role = role
      this.profileUrl = profileUrl
    },
    clearAccount() {
      this.userId = null
      this.username = ''
      this.personaId = false
      this.role = ''
      this.profileUrl = ''
    },
  },
})
