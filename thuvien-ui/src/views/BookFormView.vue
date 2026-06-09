<template>
  <div>
    <router-link to="/" class="back-link">&larr; Danh sách sách</router-link>
    <h2>{{ isEdit ? 'Sửa sách' : 'Thêm sách mới' }}</h2>
    <div class="card">
      <form @submit.prevent="save">
        <div class="field">
          <label>Tiêu đề</label>
          <input v-model="form.title" required minlength="2" maxlength="100" />
        </div>
        <div class="field">
          <label>Tác giả</label>
          <input v-model="form.author" required />
        </div>
        <div class="field">
          <label>Năm xuất bản</label>
          <input v-model.number="form.publishedYear" type="number" min="1000" :max="currentYear" />
        </div>
        <div class="field">
          <label>Vị trí</label>
          <input v-model="form.location" />
        </div>
        <div v-if="!isEdit" class="field">
          <label>Số bản sao</label>
          <input v-model.number="form.numberOfCopies" type="number" min="1" max="20" @input="syncIsbns" />
        </div>
        <div v-if="!isEdit" class="field">
          <label>ISBN từng bản sao</label>
          <div v-for="(_, i) in form.numberOfCopies" :key="i" style="display:flex;gap:4px;align-items:center;margin-bottom:4px">
            <span style="min-width:80px">Bản {{ i + 1 }}:</span>
            <input v-model="form.copyIsbns[i]" style="flex:1" pattern="[0-9]{10,13}" required />
          </div>
        </div>
        <div class="field">
          <label>Danh mục</label>
          <select v-model.number="form.categoryId">
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" class="btn" :disabled="saving">{{ saving ? 'Đang lưu...' : 'Lưu' }}</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { bookApi } from '../api/books'
import { categoryApi } from '../api/categories'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const currentYear = new Date().getFullYear()

const categories = ref([])
const form = reactive({
  title: '', author: '', publishedYear: currentYear,
  location: '', numberOfCopies: 1, copyIsbns: [''], categoryId: null
})
const error = ref('')
const saving = ref(false)

onMounted(async () => {
  try {
    const res = await categoryApi.getAll()
    categories.value = res.data
    if (res.data.length) form.categoryId = res.data[0].id
  } catch { /* ignore */ }
  if (isEdit.value) {
    try {
      const res = await bookApi.getById(route.params.id)
      const b = res.data
      form.title = b.title
      form.author = b.author
      form.publishedYear = b.publishedYear
      form.location = b.location || ''
      form.categoryId = b.category?.id || null
    } catch {
      error.value = 'Không tìm thấy sách.'
    }
  }
})

function syncIsbns() {
  const n = form.numberOfCopies
  while (form.copyIsbns.length < n) form.copyIsbns.push('')
  if (form.copyIsbns.length > n) form.copyIsbns.splice(n)
}

async function save() {
  error.value = ''
  saving.value = true
  try {
    const payload = {
      title: form.title,
      author: form.author,
      publishedYear: form.publishedYear,
      location: form.location || null,
      numberOfCopies: form.numberOfCopies,
      copyIsbns: form.copyIsbns,
      category: form.categoryId ? { id: form.categoryId } : null
    }
    if (isEdit.value) {
      await bookApi.update(route.params.id, payload)
    } else {
      await bookApi.create(payload)
    }
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || 'Lưu thất bại.'
  } finally {
    saving.value = false
  }
}
</script>
