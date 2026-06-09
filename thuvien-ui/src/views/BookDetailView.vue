<template>
  <div>
    <router-link to="/" class="back-link">&larr; Quay lại</router-link>
    <div v-if="loading" class="loading">Đang tải...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="card">
      <h2>{{ book.title }}</h2>
      <table class="info-table">
        <tr><td>ID</td><td><strong>{{ book.id }}</strong></td></tr>
        <tr><td>Tác giả</td><td>{{ book.author }}</td></tr>
        <tr><td>ISBN</td><td>{{ book.isbn }}</td></tr>
        <tr><td>Năm XB</td><td>{{ book.publishedYear }}</td></tr>
        <tr><td>Danh mục</td><td>{{ book.category?.name || 'Chưa phân loại' }}</td></tr>
        <tr><td>Vị trí</td><td><strong>{{ book.location }}</strong></td></tr>
        <tr><td>Số bản</td><td>{{ book.availableQuantity }}/{{ book.totalQuantity }}</td></tr>
        <tr><td>Trạng thái</td>
          <td>
            <span :class="book.availableQuantity > 0 ? 'tag-avail' : 'tag-unavail'">
              {{ book.availableQuantity > 0 ? 'Có thể mượn' : 'Hết bản' }}
            </span>
          </td>
        </tr>
      </table>
      <p class="mt-1 info-note">
        Để mượn sách, vui lòng đến vị trí <strong>{{ book.location }}</strong> trên kệ và mang sách ra bàn thủ tục.
      </p>
      <div v-if="auth.isAdmin" class="admin-actions">
        <router-link :to="'/admin/books/' + book.id + '/edit'" class="btn btn-sm">Sửa</router-link>
        <button class="btn btn-sm btn-danger" @click="deleteBook">Xóa</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { bookApi } from '../api/books'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const book = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const res = await bookApi.getById(route.params.id)
    book.value = res.data
  } catch (e) {
    error.value = 'Không tìm thấy sách.'
  } finally {
    loading.value = false
  }
})

async function deleteBook() {
  if (!confirm('Xóa sách này?')) return
  try {
    await bookApi.delete(route.params.id)
    router.push('/')
  } catch (e) {
    error.value = 'Xóa thất bại.'
  }
}
</script>
