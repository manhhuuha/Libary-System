<template>
  <div>
    <h2>Thống kê thư viện</h2>
    <p v-if="loading">Đang tải...</p>
    <div v-else class="stats-grid">
      <div class="card stat-card"><h3>Tổng sách</h3><p class="num">{{ summary.totalBooks }}</p></div>
      <div class="card stat-card"><h3>Tổng bản sao</h3><p class="num">{{ summary.totalBookCopies }}</p></div>
      <div class="card stat-card"><h3>Đang mượn</h3><p class="num">{{ summary.borrowedBooks }}</p></div>
      <div class="card stat-card warn"><h3>Quá hạn</h3><p class="num">{{ summary.overdueBooks }}</p></div>
      <div class="card stat-card"><h3>Người dùng</h3><p class="num">{{ summary.totalUsers }}</p></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { dashboardApi } from '../api/dashboard'

const summary = ref({ totalBooks: 0, totalBookCopies: 0, borrowedBooks: 0, overdueBooks: 0, totalUsers: 0 })
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await dashboardApi.getSummary()
    summary.value = res.data
  } catch { /* ignore */ }
  finally { loading.value = false }
})
</script>
