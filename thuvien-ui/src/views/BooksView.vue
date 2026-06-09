<template>
  <div>
    <div class="flex-between">
      <h2>Danh sách sách</h2>
      <div class="search-bar">
        <input v-model="search.title" placeholder="Tìm theo tên..." @input="doSearch" />
        <input v-model="search.author" placeholder="Tác giả..." @input="doSearch" />
        <input v-model="search.categoryName" placeholder="Danh mục..." @input="doSearch" />
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
        <p class="meta" :class="book.availableQuantity > 0 ? 'avail' : 'unavail'">
          {{ book.availableQuantity }}/{{ book.totalQuantity }} bản
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { bookApi } from '../api/books'

const books = ref([])
const loading = ref(true)
const error = ref('')
const search = reactive({ title: '', author: '', categoryName: '' })
let searchTimer

onMounted(() => fetchBooks())

async function fetchBooks(params) {
  loading.value = true
  error.value = ''
  try {
    const res = params ? await bookApi.search(params) : await bookApi.getAll()
    books.value = res.data
  } catch (e) {
    error.value = 'Không thể tải danh sách sách.'
  } finally {
    loading.value = false
  }
}

function doSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    const params = {}
    if (search.title) params.title = search.title
    if (search.author) params.author = search.author
    if (search.categoryName) params.categoryName = search.categoryName
    fetchBooks(Object.keys(params).length ? params : undefined)
  }, 300)
}
</script>
