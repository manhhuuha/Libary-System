<template>
  <div>
    <router-link to="/admin/users" class="back-link">&larr; Người dùng</router-link>
    <div v-if="loading" class="loading">Đang tải...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="card">
      <h2>Người dùng: {{ user.fullName }}</h2>
      <table class="info-table">
        <tr><td>ID</td><td>{{ user.id }}</td></tr>
        <tr><td>Họ tên</td><td>{{ user.fullName }}</td></tr>
        <tr><td>Email</td><td>{{ user.email }}</td></tr>
        <tr><td>Số điện thoại</td><td>{{ user.phoneNumber }}</td></tr>
        <tr><td>CCCD/CMND</td><td>{{ user.identityCard }}</td></tr>
        <tr><td>Tên đăng nhập</td><td>{{ user.username }}</td></tr>
        <tr><td>Vai trò</td><td>{{ user.role }}</td></tr>
        <tr><td>Loại</td><td>{{ user.userType || '—' }}</td></tr>
        <tr><td>Kích hoạt</td><td>{{ user.active ? 'Có' : 'Không' }}</td></tr>
        <tr><td>Ngày tạo</td><td>{{ user.createdAt?.slice(0, 10) }}</td></tr>
      </table>
      <div class="mt-1 flex gap">
        <button class="btn btn-sm" @click="openEdit">Sửa thông tin</button>
        <button class="btn btn-sm btn-danger" @click="deleteUser">Xóa người dùng</button>
      </div>
    </div>

    <div v-if="showEdit" class="modal-overlay" @click.self="showEdit = false">
      <div class="modal card">
        <h3>Sửa thông tin người dùng</h3>
        <form @submit.prevent="updateUser">
          <div class="field">
            <label>Họ tên</label>
            <input v-model="editForm.fullName" />
          </div>
          <div class="field">
            <label>Email</label>
            <input v-model="editForm.email" type="email" />
          </div>
          <div class="field">
            <label>Số điện thoại</label>
            <input v-model="editForm.phoneNumber" />
          </div>
          <div class="field">
            <label>CCCD/CMND</label>
            <input v-model="editForm.identityCard" />
          </div>
          <div class="field">
            <label>Loại bạn đọc</label>
            <select v-model="editForm.userType">
              <option value="">— Không đổi —</option>
              <option value="STUDENT">Học sinh</option>
              <option value="TEACHER">Giáo viên</option>
              <option value="GUEST">Khách</option>
            </select>
          </div>
          <div class="field">
            <label>Vai trò</label>
            <select v-model="editForm.role">
              <option value="PATRON">Bạn đọc</option>
              <option value="ADMIN">Thủ thư</option>
            </select>
          </div>
          <div class="field checkbox-field">
            <label>
              <input type="checkbox" v-model="editForm.active" />
              Kích hoạt
            </label>
          </div>
          <p v-if="editMsg" :class="editError ? 'error' : 'success'">{{ editMsg }}</p>
          <div class="flex gap">
            <button type="submit" class="btn">Lưu</button>
            <button type="button" class="btn btn-secondary" @click="showEdit = false">Hủy</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '../api/users'

const route = useRoute()
const router = useRouter()
const user = ref(null)
const loading = ref(true)
const error = ref('')
const showEdit = ref(false)
const editForm = ref({})
const editMsg = ref('')
const editError = ref(false)

onMounted(async () => {
  try {
    const res = await userApi.getById(route.params.id)
    user.value = res.data
  } catch (e) {
    error.value = 'Không tìm thấy người dùng.'
  } finally {
    loading.value = false
  }
})

function openEdit() {
  editForm.value = {
    fullName: user.value.fullName,
    email: user.value.email,
    phoneNumber: user.value.phoneNumber,
    identityCard: user.value.identityCard,
    userType: user.value.userType || '',
    role: user.value.role,
    active: user.value.active
  }
  showEdit.value = true
}

async function updateUser() {
  editMsg.value = ''
  try {
    const body = {}
    if (editForm.value.fullName) body.fullName = editForm.value.fullName
    if (editForm.value.email) body.email = editForm.value.email
    if (editForm.value.phoneNumber) body.phoneNumber = editForm.value.phoneNumber
    if (editForm.value.identityCard) body.identityCard = editForm.value.identityCard
    if (editForm.value.userType) body.userType = editForm.value.userType
    body.role = editForm.value.role
    body.active = editForm.value.active
    const res = await userApi.update(route.params.id, body)
    user.value = res.data
    editMsg.value = 'Cập nhật thành công!'
    editError.value = false
    setTimeout(() => { showEdit.value = false }, 1000)
  } catch (e) {
    editMsg.value = e.response?.data?.message || 'Cập nhật thất bại.'
    editError.value = true
  }
}

async function deleteUser() {
  if (!confirm('Xóa người dùng này?')) return
  try {
    await userApi.delete(route.params.id)
    router.push('/admin/users')
  } catch (e) {
    error.value = 'Xóa thất bại.'
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center; z-index: 100;
}
.modal {
  width: 480px; max-height: 90vh; overflow-y: auto;
}
.checkbox-field label { display: flex; align-items: center; gap: 0.5rem; cursor: pointer; }
.btn-secondary { background: #6b7280; }
.btn-secondary:hover { background: #4b5563; }
</style>
