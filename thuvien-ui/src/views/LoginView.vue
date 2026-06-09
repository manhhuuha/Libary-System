<template>
  <div class="form-page">
    <div class="card form-card">
      <h2>Đăng nhập</h2>
      <form @submit.prevent="handleLogin">
        <div class="field">
          <label>Tên đăng nhập</label>
          <input v-model="form.username" required />
        </div>
        <div class="field">
          <label>Mật khẩu</label>
          <input v-model="form.password" type="password" required />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" class="btn" :disabled="loading">{{ loading ? 'Đang xử lý...' : 'Đăng nhập' }}</button>
      </form>
      <p class="mt-1">Chưa có tài khoản? <router-link to="/register">Đăng ký</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const form = reactive({ username: '', password: '' })
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(form.username, form.password)
    router.push(route.query.redirect || '/')
  } catch (e) {
    error.value = e.response?.data?.message || e.message || 'Đăng nhập thất bại'
  } finally {
    loading.value = false
  }
}
</script>
