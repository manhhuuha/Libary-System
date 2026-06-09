<template>
  <div>
    <router-link to="/" class="back-link">&larr; Quay lại</router-link>
    <div v-if="loading" class="loading">Đang tải...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <div class="card">
        <h2>{{ book.title }}</h2>
        <table class="info-table">
          <tr><td>ID</td><td><strong>{{ book.id }}</strong></td></tr>
          <tr><td>Tác giả</td><td>{{ book.author }}</td></tr>
          <tr><td>Năm XB</td><td>{{ book.publishedYear }}</td></tr>
          <tr><td>Danh mục</td><td>{{ book.category?.name || 'Chưa phân loại' }}</td></tr>
          <tr><td>Vị trí</td><td><strong>{{ book.location }}</strong></td></tr>
          <tr><td>Số bản sao</td>
            <td>{{ copies.length }}</td>
          </tr>
        </table>
        <p class="mt-1 info-note">
          Sách có <strong>{{ copies.length }}</strong> bản sao,
          <strong>{{ availableCount }}</strong> bản có sẵn.
        </p>
        <div v-if="auth.isAdmin" class="admin-actions">
          <router-link :to="'/admin/books/' + book.id + '/edit'" class="btn btn-sm">Sửa</router-link>
          <button class="btn btn-sm btn-danger" @click="deleteBook">Xóa</button>
        </div>
      </div>

      <h3 class="mt-1">Danh sách bản sao</h3>
      <table class="table">
        <thead>
          <tr><th>ID</th><th>ISBN</th><th>Số hiệu</th><th>Trạng thái</th></tr>
        </thead>
        <tbody>
          <tr v-for="c in copies" :key="c.id">
            <td>{{ c.id }}</td>
            <td>{{ c.isbn }}</td>
            <td>{{ c.copyNumber }}</td>
            <td><span :style="{ color: copyColor(c.status), fontWeight: 600 }">{{ copyStatusLabel(c.status) }}</span></td>
          </tr>
        </tbody>
      </table>
      <div v-if="auth.isAdmin" class="mt-1">
        <button class="btn btn-sm" @click="showAddCopies = !showAddCopies">+ Thêm bản sao</button>
        <div v-if="showAddCopies" class="card mt-1" style="padding:1rem">
          <div class="field">
            <label>Số lượng</label>
            <input v-model.number="addCount" type="number" min="1" max="50" @input="syncAddIsbns" />
          </div>
          <div v-for="(_, i) in addCount" :key="i" class="field" style="margin-top:6px">
            <label>ISBN bản sao mới {{ i + 1 }}</label>
            <input v-model="addIsbns[i]" pattern="[0-9]{10,13}" required />
          </div>
          <div style="margin-top:8px">
            <button class="btn btn-sm" @click="addCopies" :disabled="adding">{{ adding ? 'Đang thêm...' : 'Thêm' }}</button>
            <button class="btn btn-sm btn-danger" @click="showAddCopies = false" style="margin-left:8px">Hủy</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { bookApi } from '../api/books'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const book = ref(null)
const copies = ref([])
const loading = ref(true)
const error = ref('')
const showAddCopies = ref(false)
const addCount = ref(1)
const addIsbns = ref([''])
const adding = ref(false)

const availableCount = computed(() => copies.value.filter(c => c.status === 'AVAILABLE').length)

onMounted(async () => {
  try {
    const [bookRes, copiesRes] = await Promise.all([
      bookApi.getById(route.params.id),
      bookApi.getCopies(route.params.id)
    ])
    book.value = bookRes.data
    copies.value = copiesRes.data
  } catch (e) {
    error.value = 'Không tìm thấy sách.'
  } finally {
    loading.value = false
  }
})

function copyStatusLabel(s) {
  return { AVAILABLE: 'Có sẵn', BORROWED: 'Đang mượn', DAMAGED: 'Hư hỏng', LOST: 'Mất' }[s] || s
}
function copyColor(s) {
  return { AVAILABLE: '#198754', BORROWED: '#0d6efd', DAMAGED: '#d97706', LOST: '#dc3545' }[s] || '#333'
}

function syncAddIsbns() {
  const n = addCount.value
  while (addIsbns.value.length < n) addIsbns.value.push('')
  if (addIsbns.value.length > n) addIsbns.value.splice(n)
}

async function addCopies() {
  adding.value = true
  try {
    const res = await bookApi.addCopies(route.params.id, addIsbns.value)
    copies.value = res.data
    showAddCopies.value = false
    addCount.value = 1
    addIsbns.value = ['']
  } catch (e) {
    error.value = e.response?.data?.message || 'Thêm bản sao thất bại.'
  } finally {
    adding.value = false
  }
}

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
