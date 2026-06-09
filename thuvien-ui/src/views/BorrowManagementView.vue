<template>
  <div>
    <h2>Quản lý mượn/trả</h2>

    <div class="tabs">
      <button :class="tab === 'create' ? 'active' : ''" @click="tab = 'create'">Tạo phiếu mượn</button>
      <button :class="tab === 'current' ? 'active' : ''" @click="tab = 'current'; fetchCurrent()">Đang mượn</button>
      <button :class="tab === 'due' ? 'active' : ''" @click="tab = 'due'; fetchDueSoon()">Sắp đến hạn</button>
      <button :class="tab === 'overdue' ? 'active' : ''" @click="tab = 'overdue'; fetchOverdue()">Quá hạn</button>
      <button :class="tab === 'return' ? 'active' : ''" @click="tab = 'return'">Trả sách</button>
    </div>

    <!-- Tab: Tạo phiếu mượn -->
    <div v-if="tab === 'create'" class="card">
      <h3>Tạo phiếu mượn sách</h3>

      <div class="lookup-section">
        <label class="lookup-label">Bạn đọc</label>
        <div class="lookup-row">
          <input v-model="userQuery" placeholder="Nhập CCCD, tên hoặc ID bạn đọc" @keyup.enter="lookupUser" />
          <button class="btn btn-sm" @click="lookupUser" :disabled="!userQuery.trim()">Tra cứu</button>
        </div>
        <div v-if="userLookupLoading" class="lookup-status">Đang tra cứu...</div>
        <div v-if="selectedUser" class="lookup-result success">
          <strong>{{ selectedUser.fullName }}</strong> ({{ selectedUser.username }}) — {{ selectedUser.userType || 'ADMIN' }}
          <br/><small>CCCD: {{ selectedUser.identityCard }} | Email: {{ selectedUser.email }}</small>
          <button class="btn btn-sm btn-danger" @click="selectedUser = null; userQuery = ''" style="margin-left:0.5rem">Bỏ chọn</button>
        </div>
        <div v-if="userResults.length > 0 && !selectedUser" class="lookup-results">
          <div v-for="u in userResults" :key="u.id" class="lookup-item" @click="selectUser(u)">
            <strong>{{ u.fullName }}</strong> ({{ u.username }}) — CCCD: {{ u.identityCard }}
          </div>
        </div>
        <p v-if="userNotFound" class="error">Không tìm thấy bạn đọc.</p>
      </div>

      <div class="lookup-section">
        <label class="lookup-label">Sách</label>
        <div class="lookup-row">
          <input v-model="bookQuery" placeholder="Nhập ID hoặc tên sách" @keyup.enter="lookupBook" />
          <button class="btn btn-sm" @click="lookupBook" :disabled="!bookQuery.trim()">Tra cứu</button>
        </div>
        <div v-if="bookLookupLoading" class="lookup-status">Đang tra cứu...</div>
        <div v-if="selectedBook" class="lookup-result success">
          <strong>{{ selectedBook.title }}</strong> — {{ selectedBook.author }}
          <br/><small>Vị trí: {{ selectedBook.location }} | Còn: {{ selectedBook.availableQuantity }}/{{ selectedBook.totalQuantity }} bản</small>
          <button class="btn btn-sm btn-danger" @click="selectedBook = null; bookQuery = ''" style="margin-left:0.5rem">Bỏ chọn</button>
        </div>
        <div v-if="bookResults.length > 0 && !selectedBook" class="lookup-results">
          <div v-for="b in bookResults" :key="b.id" class="lookup-item" @click="selectBook(b)">
            [ID {{ b.id }}] <strong>{{ b.title }}</strong> — {{ b.author }} (còn {{ b.availableQuantity }}/{{ b.totalQuantity }})
          </div>
        </div>
        <p v-if="bookNotFound" class="error">Không tìm thấy sách.</p>
      </div>

      <p v-if="borrowMsg" :class="borrowError ? 'error' : 'success'">{{ borrowMsg }}</p>
      <button class="btn" @click="createBorrow" :disabled="!selectedUser || !selectedBook || borrowing">
        {{ borrowing ? 'Đang xử lý...' : 'Xác nhận mượn' }}
      </button>
    </div>

    <!-- Tab: Đang mượn -->
    <div v-if="tab === 'current'">
      <p v-if="currentLoading">Đang tải...</p>
      <div v-else-if="currentRecords.length === 0" class="empty">Không có sách đang mượn.</div>
      <table v-else class="table">
        <thead>
          <tr><th>ID</th><th>Người mượn</th><th>Sách</th><th>Ngày mượn</th><th>Hạn trả</th><th>Trạng thái</th></tr>
        </thead>
        <tbody>
          <tr v-for="r in currentRecords" :key="r.id">
            <td>{{ r.id }}</td>
            <td><router-link :to="'/admin/users/' + r.user?.id">{{ r.user?.fullName }}</router-link></td>
            <td><router-link :to="'/books/' + r.book?.id">{{ r.book?.title }}</router-link></td>
            <td>{{ r.borrowDate }}</td>
            <td>{{ r.dueDate }}</td>
            <td><span :class="'status-' + r.status.toLowerCase()">{{ statusLabel(r.status) }}</span></td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Tab: Sắp đến hạn -->
    <div v-if="tab === 'due'">
      <p v-if="dueLoading">Đang tải...</p>
      <div v-else-if="dueRecords.length === 0" class="empty">Không có sách sắp đến hạn.</div>
      <table v-else class="table">
        <thead>
          <tr><th>ID</th><th>Người mượn</th><th>Sách</th><th>Ngày mượn</th><th>Hạn trả</th></tr>
        </thead>
        <tbody>
          <tr v-for="r in dueRecords" :key="r.id">
            <td>{{ r.id }}</td>
            <td><router-link :to="'/admin/users/' + r.user?.id">{{ r.user?.fullName }}</router-link></td>
            <td><router-link :to="'/books/' + r.book?.id">{{ r.book?.title }}</router-link></td>
            <td>{{ r.borrowDate }}</td><td>{{ r.dueDate }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Tab: Quá hạn -->
    <div v-if="tab === 'overdue'">
      <p v-if="overdueLoading">Đang tải...</p>
      <div v-else-if="overdueRecords.length === 0" class="empty">Không có sách quá hạn.</div>
      <table v-else class="table">
        <thead>
          <tr><th>ID</th><th>Người mượn</th><th>Sách</th><th>Ngày mượn</th><th>Hạn trả</th></tr>
        </thead>
        <tbody>
          <tr v-for="r in overdueRecords" :key="r.id">
            <td>{{ r.id }}</td>
            <td><router-link :to="'/admin/users/' + r.user?.id">{{ r.user?.fullName }}</router-link></td>
            <td><router-link :to="'/books/' + r.book?.id">{{ r.book?.title }}</router-link></td>
            <td>{{ r.borrowDate }}</td><td>{{ r.dueDate }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Tab: Trả sách -->
    <div v-if="tab === 'return'" class="card">
      <h3>Trả sách</h3>
      <form @submit.prevent="returnBook">
        <div class="field">
          <label>ID sách</label>
          <input v-model.number="returnBookId" type="number" placeholder="Nhập ID sách" required />
        </div>
        <p v-if="returnMsg" :class="returnError ? 'error' : 'success'">{{ returnMsg }}</p>
        <button type="submit" class="btn">Xác nhận trả</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { borrowApi } from '../api/borrow'
import { userApi } from '../api/users'
import { bookApi } from '../api/books'

const tab = ref('create')

const userQuery = ref('')
const selectedUser = ref(null)
const userResults = ref([])
const userLookupLoading = ref(false)
const userNotFound = ref(false)

const bookQuery = ref('')
const selectedBook = ref(null)
const bookResults = ref([])
const bookLookupLoading = ref(false)
const bookNotFound = ref(false)

const borrowing = ref(false)
const borrowMsg = ref('')
const borrowError = ref(false)

const currentRecords = ref([])
const currentLoading = ref(false)

const dueRecords = ref([])
const dueLoading = ref(false)

const overdueRecords = ref([])
const overdueLoading = ref(false)

const returnBookId = ref('')
const returnMsg = ref('')
const returnError = ref(false)

async function lookupUser() {
  const q = userQuery.value.trim()
  if (!q) return
  selectedUser.value = null
  userResults.value = []
  userNotFound.value = false
  userLookupLoading.value = true

  if (/^\d+$/.test(q)) {
    try {
      const res = await userApi.getById(q)
      selectedUser.value = res.data
      userResults.value = []
      userLookupLoading.value = false
      return
    } catch { /* fall through to search */ }
  }

  try {
    const res = await userApi.search(q)
    const patrons = res.data.filter(u => u.role === 'PATRON')
    if (patrons.length === 1) {
      selectedUser.value = patrons[0]
    } else if (patrons.length > 1) {
      userResults.value = patrons
    } else {
      userNotFound.value = true
    }
  } catch {
    userNotFound.value = true
  }
  userLookupLoading.value = false
}

function selectUser(u) {
  selectedUser.value = u
  userResults.value = []
  userQuery.value = u.fullName
}

async function lookupBook() {
  const q = bookQuery.value.trim()
  if (!q) return
  selectedBook.value = null
  bookResults.value = []
  bookNotFound.value = false
  bookLookupLoading.value = true

  if (/^\d+$/.test(q)) {
    try {
      const res = await bookApi.getById(q)
      if (res.data.availableQuantity > 0) {
        selectedBook.value = res.data
        bookQuery.value = res.data.title
      } else {
        selectedBook.value = res.data
        bookNotFound.value = true
      }
      bookLookupLoading.value = false
      return
    } catch { /* fall through to search */ }
  }

  try {
    const res = await bookApi.search({ title: q })
    const avail = res.data.filter(b => b.availableQuantity > 0)
    if (avail.length === 1) {
      selectedBook.value = avail[0]
      bookQuery.value = avail[0].title
    } else if (avail.length > 1) {
      bookResults.value = avail
    } else {
      bookNotFound.value = true
    }
  } catch {
    bookNotFound.value = true
  }
  bookLookupLoading.value = false
}

function selectBook(b) {
  selectedBook.value = b
  bookResults.value = []
  bookQuery.value = b.title
}

async function createBorrow() {
  if (!selectedUser.value || !selectedBook.value) return
  borrowMsg.value = ''
  borrowing.value = true
  try {
    await borrowApi.borrow(selectedUser.value.id, selectedBook.value.id)
    borrowMsg.value = 'Tạo phiếu mượn thành công!'
    borrowError.value = false
    selectedUser.value = null
    selectedBook.value = null
    userQuery.value = ''
    bookQuery.value = ''
  } catch (e) {
    borrowMsg.value = e.response?.data?.message || 'Mượn sách thất bại.'
    borrowError.value = true
  } finally {
    borrowing.value = false
  }
}

async function fetchCurrent() {
  currentLoading.value = true
  try {
    const res = await borrowApi.current()
    currentRecords.value = res.data
  } catch { currentRecords.value = [] }
  finally { currentLoading.value = false }
}

async function fetchDueSoon() {
  dueLoading.value = true
  try {
    const res = await borrowApi.dueSoon()
    dueRecords.value = res.data
  } catch { dueRecords.value = [] }
  finally { dueLoading.value = false }
}

async function fetchOverdue() {
  overdueLoading.value = true
  try {
    const res = await borrowApi.overdue()
    overdueRecords.value = res.data
  } catch { overdueRecords.value = [] }
  finally { overdueLoading.value = false }
}

async function returnBook() {
  returnMsg.value = ''
  try {
    await borrowApi.returnBook(returnBookId.value)
    returnMsg.value = 'Trả sách thành công!'
    returnError.value = false
    returnBookId.value = ''
  } catch (e) {
    returnMsg.value = e.response?.data?.message || 'Trả sách thất bại.'
    returnError.value = true
  }
}

function statusLabel(s) {
  return { BORROWING: 'Đang mượn', RETURNED: 'Đã trả', OVERDUE: 'Quá hạn' }[s] || s
}
</script>

<style scoped>
.lookup-section { margin-bottom: 1.25rem; }
.lookup-label { display: block; font-weight: 600; margin-bottom: 0.35rem; font-size: 0.9rem; }
.lookup-row { display: flex; gap: 0.5rem; }
.lookup-row input { flex: 1; padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.95rem; }
.lookup-status { color: #888; font-size: 0.85rem; margin-top: 0.3rem; }
.lookup-result { margin-top: 0.4rem; padding: 0.5rem 0.75rem; border-radius: 6px; font-size: 0.9rem; }
.lookup-results { margin-top: 0.3rem; max-height: 180px; overflow-y: auto; border: 1px solid #d1d5db; border-radius: 6px; }
.lookup-item { padding: 0.45rem 0.75rem; cursor: pointer; border-bottom: 1px solid #f0f0f0; font-size: 0.9rem; }
.lookup-item:last-child { border-bottom: none; }
.lookup-item:hover { background: #e8f4fd; }
</style>
