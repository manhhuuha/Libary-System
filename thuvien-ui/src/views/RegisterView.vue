<template>
  <div class="form-page">
    <div class="card form-card">
      <h2>Đăng ký tài khoản</h2>
      <form @submit.prevent="handleRegister">
        <div class="field">
          <label>Họ và tên</label>
          <input v-model="form.fullName" required />
        </div>
        <div class="field">
          <label>Email</label>
          <input v-model="form.email" type="email" required />
        </div>
        <div class="field">
          <label>Số điện thoại (10 số)</label>
          <input v-model="form.phoneNumber" pattern="[0-9]{10}" required />
        </div>
        <div class="field">
          <label>CCCD/CMND</label>
          <input v-model="form.identityCard" required />
        </div>
        <div class="field">
          <label>Tên đăng nhập</label>
          <input v-model="form.username" />
          <small>Để trống để tự động tạo</small>
        </div>
        <div class="field">
          <label>Mật khẩu</label>
          <input v-model="form.password" type="password" />
          <small>Để trống để tạo mật khẩu mặc định</small>
        </div>
        <div class="field">
          <label>Loại bạn đọc</label>
          <select v-model="form.userType">
            <option value="STUDENT">Học sinh</option>
            <option value="TEACHER">Giáo viên</option>
            <option value="GUEST">Khách</option>
          </select>
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="success" class="success">{{ success }}</p>
        <button type="submit" class="btn" :disabled="loading">{{ loading ? 'Đang xử lý...' : 'Đăng ký' }}</button>
      </form>
      <p class="mt-1">Đã có tài khoản? <router-link to="/login">Đăng nhập</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { userApi } from '../api/users'

const form = reactive({
  fullName: '', email: '', phoneNumber: '', identityCard: '',
  username: '', password: '', userType: 'STUDENT'
})
const error = ref('')
const success = ref('')
const loading = ref(false)

async function handleRegister() {
  error.value = ''
  success.value = ''
  loading.value = true
  try {
    const payload = { ...form }
    if (!payload.username) delete payload.username
    if (!payload.password) delete payload.password
    await userApi.register(payload)
    success.value = 'Đăng ký thành công! Vui lòng đăng nhập.'
    form.fullName = ''; form.email = ''; form.phoneNumber = ''
    form.identityCard = ''; form.username = ''; form.password = ''
  } catch (e) {
    error.value = e.response?.data?.message || 'Đăng ký thất bại'
  } finally {
    loading.value = false
  }
}
</script>
