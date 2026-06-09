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

    <div v-if="totalPages > 1" class="pagination">
      <button :disabled="page === 0" @click="goToPage(0)" class="page-btn">&laquo;</button>
      <button :disabled="page === 0" @click="goToPage(page - 1)" class="page-btn">&lsaquo;</button>
      <button v-for="p in visiblePages" :key="p" :class="['page-btn', { active: p === page }]" @click="goToPage(p)">
        {{ p + 1 }}
      </button>
      <button :disabled="page >= totalPages - 1" @click="goToPage(page + 1)" class="page-btn">&rsaquo;</button>
      <button :disabled="page >= totalPages - 1" @click="goToPage(totalPages - 1)" class="page-btn">&raquo;</button>
      <span class="page-info">Tổng: {{ totalElements }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { userApi } from '../api/users'

const users = ref([])
const loading = ref(true)
const error = ref('')
const page = ref(0)
const size = 10
const totalPages = ref(0)
const totalElements = ref(0)

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(0, page.value - 2)
  const end = Math.min(totalPages.value - 1, page.value + 2)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

onMounted(() => fetchUsers())

async function fetchUsers() {
  loading.value = true
  error.value = ''
  try {
    const res = await userApi.getPaged(page.value, size)
    users.value = res.data.content
    totalPages.value = res.data.totalPages
    totalElements.value = res.data.totalElements
  } catch (e) {
    error.value = 'Không thể tải danh sách.'
  } finally {
    loading.value = false
  }
}

function goToPage(p) {
  if (p < 0 || p >= totalPages.value) return
  page.value = p
  fetchUsers()
}
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  margin-top: 1.5rem;
  flex-wrap: wrap;
}
.page-btn {
  padding: 0.4rem 0.7rem;
  border: 1px solid #d1d5db;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}
.page-btn:hover:not(:disabled) { background: #e8f4fd; }
.page-btn.active { background: #1a73e8; color: #fff; border-color: #1a73e8; }
.page-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.page-info { margin-left: 0.75rem; color: #888; font-size: 0.85rem; }
</style>
