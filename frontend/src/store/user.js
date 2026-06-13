import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const setUser = (user) => {
    userInfo.value = user
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  const logout = () => {
    userInfo.value = null
    localStorage.removeItem('userInfo')
  }

  const isLoggedIn = () => {
    return userInfo.value !== null
  }

  return { userInfo, setUser, logout, isLoggedIn }
})
