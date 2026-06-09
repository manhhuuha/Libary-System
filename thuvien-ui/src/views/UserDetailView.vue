<template>
  <div>
    <router-link to="/admin/users" class="back-link">&larr; Người dùng</router-link>
    <div v-if="loading" class="loading">Đang tải...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="card">
      <h2>Người dùng: {{ user.fullName }}</h2>
      <table class="info-table">
        <tr><td>ID</td><td>{{ user.id }}</td></tr>
        <tr><td>Họ tên</td><td>{{ user.fullName }}</td></tr>
        <tr><td>Email</td><td>{{ user.email }}</td></tr>
        <tr><td>Số điện thoại</td><td>{{ user.phoneNumber }}</td></tr>
        <tr><td>CCCD/CMND</td><td>{{ user.identityCard }}</td></tr>
        <tr><td>Tên đăng nhập</td><td>{{ user.username }}</td></tr>
        <tr><td>Vai trò</td><td>{{ user.role }}</td></tr>
        <tr><td>Loại</td><td>{{ user.userType || '—' }}</td></tr>
        <tr><td>Kích hoạt</td><td>{{ user.active ? 'Có' : 'Không' }}</td></tr>
        <tr><td>Ngày tạo</td><td>{{ user.createdAt?.slice(0, 10) }}</td></tr>
      </table>
      <div class="mt-1 flex gap">
        <button class="btn btn-sm btn-danger" @click="deleteUser">Xóa người dùng</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '../api/users'

const route = useRoute()
const router = useRouter()
const user = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const res = await userApi.getById(route.params.id)
    user.value = res.data
  } catch (e) {
    error.value = 'Không tìm thấy người dùng.'
  } finally {
    loading.value = false
  }
})

async function deleteUser() {
  if (!confirm('Xóa người dùng này?')) return
  try {
    await userApi.delete(route.params.id)
    router.push('/admin/users')
  } catch (e) {
    error.value = 'Xóa thất bại.'
  }
}
</script>
