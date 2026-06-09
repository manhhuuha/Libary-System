<template>
  <div>
    <div class="flex-between">
      <h2>Danh sách sách</h2>
      <div class="search-bar">
        <input v-model="search.title" placeholder="Tìm theo tên..." @input="doSearch" />
        <input v-model="search.author" placeholder="Tác giả..." @input="doSearch" />
        <select v-model="search.categoryName" @change="doSearch" class="filter-select">
          <option value="">Tất cả danh mục</option>
          <option v-for="c in categories" :key="c.id" :value="c.name">{{ c.name }}</option>
        </select>
      </div>
    </div>
    <p v-if="loading">Đang tải...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <div v-else-if="books.length === 0" class="empty">Không tìm thấy sách nào.</div>
    <div v-else class="book-grid">
      <div v-for="book in books" :key="book.id" class="card book-card" @click="$router.push('/books/' + book.id)">
        <h3>{{ book.title }}</h3>
        <p class="meta">{{ book.author }}</p>
        <p class="meta">{{ book.categoryName }}</p>
      </div>
    </div>

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
import { ref, reactive, onMounted, computed } from 'vue'
import { bookApi } from '../api/books'
import { categoryApi } from '../api/categories'

const books = ref([])
const loading = ref(true)
const error = ref('')
const page = ref(0)
const size = 12
const totalPages = ref(0)
const totalElements = ref(0)
const search = reactive({ title: '', author: '', categoryName: '' })
const categories = ref([])
let searchTimer

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(0, page.value - 2)
  const end = Math.min(totalPages.value - 1, page.value + 2)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

onMounted(async () => {
  try {
    const res = await categoryApi.getAll()
    categories.value = res.data
  } catch { /* ignore */ }
  fetchBooks()
})

async function fetchBooks() {
  loading.value = true
  error.value = ''
  try {
    const params = {}
    if (search.title) params.title = search.title
    if (search.author) params.author = search.author
    if (search.categoryName) params.categoryName = search.categoryName
    const res = await bookApi.getPaged(page.value, size, params)
    books.value = res.data.content
    totalPages.value = res.data.totalPages
    totalElements.value = res.data.totalElements
  } catch (e) {
    error.value = 'Không thể tải danh sách sách.'
  } finally {
    loading.value = false
  }
}

function doSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    page.value = 0
    fetchBooks()
  }, 300)
}

function goToPage(p) {
  if (p < 0 || p >= totalPages.value) return
  page.value = p
  fetchBooks()
}
</script>

<style scoped>
.filter-select {
  padding: 0.4rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9rem;
  background: #fff;
  min-width: 140px;
}
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
