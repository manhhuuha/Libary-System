<template>
  <div>
    <h2>Quản lý danh mục</h2>
    <form class="inline-form" @submit.prevent="addCategory">
      <input v-model="newName" placeholder="Tên danh mục mới" required />
      <button type="submit" class="btn btn-sm">Thêm</button>
    </form>
    <p v-if="msg" :class="msgError ? 'error' : 'success'">{{ msg }}</p>
    <p v-if="loading">Đang tải...</p>
    <table v-else class="table">
      <thead><tr><th>ID</th><th>Tên danh mục</th><th></th></tr></thead>
      <tbody>
        <tr v-for="c in categories" :key="c.id">
          <td>{{ c.id }}</td>
          <td>{{ c.name }}</td>
          <td><button class="btn btn-sm btn-danger" @click="deleteCategory(c.id)">Xóa</button></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { categoryApi } from '../api/categories'

const categories = ref([])
const loading = ref(true)
const newName = ref('')
const msg = ref('')
const msgError = ref(false)

onMounted(() => fetchCategories())

async function fetchCategories() {
  loading.value = true
  try {
    const res = await categoryApi.getAll()
    categories.value = res.data
  } catch (e) {
    msg.value = 'Không thể tải danh mục.'
    msgError.value = true
  } finally {
    loading.value = false
  }
}

async function addCategory() {
  msg.value = ''
  try {
    await categoryApi.create({ name: newName.value })
    newName.value = ''
    msg.value = 'Thêm danh mục thành công.'
    msgError.value = false
    fetchCategories()
  } catch (e) {
    msg.value = e.response?.data?.message || 'Thêm thất bại.'
    msgError.value = true
  }
}

async function deleteCategory(id) {
  if (!confirm('Xóa danh mục này?')) return
  try {
    await categoryApi.delete(id)
    msg.value = 'Đã xóa.'
    msgError.value = false
    fetchCategories()
  } catch (e) {
    msg.value = 'Xóa thất bại.'
    msgError.value = true
  }
}
</script>
