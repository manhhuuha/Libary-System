<template>
  <div>
    <h2>Tài khoản của tôi</h2>
    <div class="card">
      <table class="info-table">
        <tr><td>Họ tên</td><td>{{ auth.user?.fullName }}</td></tr>
        <tr><td>Email</td><td>{{ auth.user?.email }}</td></tr>
        <tr><td>Số điện thoại</td><td>{{ auth.user?.phoneNumber }}</td></tr>
        <tr><td>CCCD/CMND</td><td>{{ auth.user?.identityCard }}</td></tr>
        <tr><td>Tên đăng nhập</td><td>{{ auth.user?.username }}</td></tr>
        <tr><td>Vai trò</td><td>{{ roleLabel }}</td></tr>
        <tr><td>Loại</td><td>{{ auth.user?.userType || '—' }}</td></tr>
        <tr><td>Ngày tạo</td><td>{{ auth.user?.createdAt?.slice(0, 10) }}</td></tr>
      </table>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { computed } from 'vue'

const auth = useAuthStore()
const roleLabel = computed(() =>
  auth.user?.role === 'ADMIN' ? 'Thủ thư' : 'Bạn đọc'
)
</script>
