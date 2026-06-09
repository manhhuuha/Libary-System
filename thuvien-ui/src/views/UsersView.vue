<template>
  <div>
    <h2>Quản lý người dùng</h2>
    <p v-if="loading">Đang tải...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <table v-else class="table">
      <thead>
        <tr><th>ID</th><th>Họ tên</th><th>Email</th><th>Username</th><th>Vai trò</th><th>Loại</th><th>Kích hoạt</th><th></th></tr>
      </thead>
      <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{ u.id }}</td>
          <td><router-link :to="'/admin/users/' + u.id">{{ u.fullName }}</router-link></td>
          <td>{{ u.email }}</td>
          <td>{{ u.username }}</td>
          <td>{{ u.role }}</td>
          <td>{{ u.userType || '—' }}</td>
          <td>{{ u.active ? '✓' : '✗' }}</td>
          <td><router-link :to="'/admin/users/' + u.id" class="btn btn-sm">Chi tiết</router-link></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '../api/users'

const users = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const res = await userApi.getAll()
    users.value = res.data
  } catch (e) {
    error.value = 'Không thể tải danh sách.'
  } finally {
    loading.value = false
  }
})
</script>
