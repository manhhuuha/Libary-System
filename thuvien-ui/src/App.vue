<template>
  <div class="app">
    <nav class="navbar">
      <div class="nav-inner">
        <router-link to="/" class="nav-brand">📚 Thư viện</router-link>
        <div class="nav-links">
          <template v-if="auth.isAuthenticated">
            <router-link to="/profile">{{ auth.user?.fullName || auth.username }}</router-link>
            <router-link v-if="!auth.isAdmin" to="/profile/borrow-history">Lịch sử mượn</router-link>
            <router-link v-if="!auth.isAdmin" to="/profile/current-borrows">Đang mượn</router-link>
            <template v-if="auth.isAdmin">
              <router-link to="/admin/dashboard">Thống kê</router-link>
              <router-link to="/admin/books/new">+ Thêm sách</router-link>
              <router-link to="/admin/users">Người dùng</router-link>
              <router-link to="/admin/categories">Danh mục</router-link>
              <router-link to="/admin/borrow">Mượn/Trả</router-link>
            </template>
            <button class="btn-link" @click="handleLogout">Thoát</button>
          </template>
          <template v-else>
            <router-link to="/login">Đăng nhập</router-link>
            <router-link to="/register">Đăng ký</router-link>
          </template>
        </div>
      </div>
    </nav>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from './stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

function handleLogout() {
  auth.logout()
  router.push('/')
}
</script>
