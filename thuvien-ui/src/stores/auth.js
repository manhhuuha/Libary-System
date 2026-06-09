import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const credentials = ref(JSON.parse(localStorage.getItem('auth') || 'null'))

  const isAuthenticated = computed(() => !!credentials.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const role = computed(() => user.value?.role || null)
  const username = computed(() => user.value?.username || '')

  async function login(username, password) {
    const auth = { username, password }
    localStorage.setItem('auth', JSON.stringify(auth))
    credentials.value = auth
    try {
      const res = await api.get('/users/me')
      user.value = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
    } catch {
      localStorage.removeItem('auth')
      credentials.value = null
      throw new Error('Đăng nhập thất bại')
    }
  }

  function logout() {
    localStorage.removeItem('auth')
    localStorage.removeItem('user')
    user.value = null
    credentials.value = null
  }

  async function fetchCurrentUser() {
    try {
      const res = await api.get('/users/me')
      user.value = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
    } catch {
      logout()
    }
  }

  return { user, isAuthenticated, isAdmin, role, username, login, logout, fetchCurrentUser }
})
