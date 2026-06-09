<template>
  <div>
    <router-link to="/profile" class="back-link">&larr; Tài khoản</router-link>
    <h2>Lịch sử mượn sách</h2>
    <p v-if="loading">Đang tải...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <div v-else-if="records.length === 0" class="empty">Chưa có lịch sử mượn.</div>
    <table v-else class="table">
      <thead>
        <tr><th>Sách</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Trạng thái</th></tr>
      </thead>
      <tbody>
        <tr v-for="r in records" :key="r.id">
          <td><router-link :to="'/books/' + r.bookCopy?.book?.id">{{ r.bookCopy?.book?.title }}</router-link></td>
          <td>{{ r.borrowDate }}</td>
          <td>{{ r.dueDate }}</td>
          <td>{{ r.returnDate || '—' }}</td>
          <td><span :class="'status-' + r.status.toLowerCase()">{{ statusLabel(r.status) }}</span></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '../api/users'

const records = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const res = await userApi.getBorrowHistory()
    records.value = res.data
  } catch (e) {
    error.value = 'Không thể tải lịch sử mượn.'
  } finally {
    loading.value = false
  }
})

function statusLabel(s) {
  return { BORROWING: 'Đang mượn', RETURNED: 'Đã trả', OVERDUE: 'Quá hạn' }[s] || s
}
</script>
