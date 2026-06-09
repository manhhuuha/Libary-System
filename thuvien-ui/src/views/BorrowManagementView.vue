<template>
  <div>
    <h2>Quản lý mượn/trả</h2>

    <div class="tabs">
      <button :class="tab === 'create' ? 'active' : ''" @click="tab = 'create'">Tạo phiếu mượn</button>
      <button :class="tab === 'current' ? 'active' : ''" @click="tab = 'current'; fetchCurrent()">Đang mượn</button>
      <button :class="tab === 'due' ? 'active' : ''" @click="tab = 'due'; fetchDueSoon()">Sắp đến hạn</button>
      <button :class="tab === 'overdue' ? 'active' : ''" @click="tab = 'overdue'; fetchOverdue()">Quá hạn</button>
      <button :class="tab === 'history' ? 'active' : ''" @click="tab = 'history'">Lịch sử</button>
      <button :class="tab === 'all-history' ? 'active' : ''" @click="tab = 'all-history'; fetchAllHistory()">Tất cả lịch sử</button>
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
          <button class="btn btn-sm btn-danger" @click="resetUser" style="margin-left:0.5rem">Bỏ chọn</button>
        </div>
        <div v-if="userResults.length > 0 && !selectedUser" class="lookup-results">
          <div v-for="u in userResults" :key="u.id" class="lookup-item" @click="selectUser(u)">
            <strong>{{ u.fullName }}</strong> ({{ u.username }}) — CCCD: {{ u.identityCard }}
          </div>
        </div>
        <p v-if="userNotFound" class="error">Không tìm thấy bạn đọc.</p>
      </div>

      <div class="lookup-section">
        <label class="lookup-label">Đầu sách</label>
        <div class="lookup-row">
          <input v-model="bookQuery" placeholder="Nhập tên sách" @keyup.enter="lookupBook" />
          <button class="btn btn-sm" @click="lookupBook" :disabled="!bookQuery.trim()">Tra cứu</button>
        </div>
        <div v-if="bookLookupLoading" class="lookup-status">Đang tra cứu...</div>
        <div v-if="selectedBook" class="lookup-result success">
          <strong>{{ selectedBook.title }}</strong> — {{ selectedBook.author }}
          <button class="btn btn-sm btn-danger" @click="resetBook" style="margin-left:0.5rem">Bỏ chọn</button>
        </div>
        <div v-if="bookResults.length > 0 && !selectedBook" class="lookup-results">
          <div v-for="b in bookResults" :key="b.id" class="lookup-item" @click="selectBook(b)">
            <strong>{{ b.title }}</strong> — {{ b.author }}
          </div>
        </div>
        <p v-if="bookNotFound" class="error">Không tìm thấy sách.</p>
      </div>

      <div v-if="selectedBook" class="lookup-section">
        <label class="lookup-label">Chọn bản sách</label>
        <p v-if="copiesLoading" class="lookup-status">Đang tải danh sách bản sao...</p>
        <div v-else-if="availableCopies.length === 0" class="error">Đầu sách này hiện không còn bản nào có sẵn.</div>
        <select v-else v-model="selectedCopyId" class="input">
          <option :value="null" disabled>-- Chọn bản sách --</option>
          <option v-for="c in availableCopies" :key="c.id" :value="c.id">
            [ID {{ c.id }}] Bản {{ c.copyNumber }}
          </option>
        </select>
        <small class="hint">Có {{ availableCopies.length }} bản có sẵn.</small>
      </div>

      <div class="lookup-section">
        <label class="lookup-label">Hạn trả (tùy chọn)</label>
        <input v-model="dueDate" type="date" class="input" :min="minDate" />
        <small class="hint">Để trống sẽ mặc định 14 ngày kể từ hôm nay.</small>
      </div>

      <p v-if="borrowMsg" :class="borrowError ? 'error' : 'success'">{{ borrowMsg }}</p>
      <button class="btn" @click="createBorrow" :disabled="!selectedUser || !selectedCopyId || borrowing">
        {{ borrowing ? 'Đang xử lý...' : 'Xác nhận mượn' }}
      </button>
    </div>

    <!-- Tab: Đang mượn -->
    <div v-if="tab === 'current'">
      <p v-if="currentLoading">Đang tải...</p>
      <div v-else-if="currentRecords.length === 0" class="empty">Không có sách đang mượn.</div>
      <table v-else class="table">
        <thead>
          <tr><th>ID</th><th>Người mượn</th><th>Sách</th><th>Bản sao</th><th>Ngày mượn</th><th>Hạn trả</th><th>Trạng thái</th></tr>
        </thead>
        <tbody>
          <tr v-for="r in currentRecords" :key="r.id">
            <td>{{ r.id }}</td>
            <td><router-link :to="'/admin/users/' + r.user?.id">{{ r.user?.fullName }}</router-link></td>
            <td><router-link :to="'/books/' + r.bookCopy?.book?.id">{{ r.bookCopy?.book?.title }}</router-link></td>
            <td>{{ r.bookCopy?.copyNumber || r.bookCopy?.id }}</td>
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
          <tr><th>ID</th><th>Người mượn</th><th>Sách</th><th>Bản sao</th><th>Ngày mượn</th><th>Hạn trả</th><th>Email</th><th>Thao tác</th></tr>
        </thead>
        <tbody>
          <tr v-for="r in dueRecords" :key="r.id">
            <td>{{ r.id }}</td>
            <td><router-link :to="'/admin/users/' + r.user?.id">{{ r.user?.fullName }}</router-link></td>
            <td><router-link :to="'/books/' + r.bookCopy?.book?.id">{{ r.bookCopy?.book?.title }}</router-link></td>
            <td>{{ r.bookCopy?.copyNumber || r.bookCopy?.id }}</td>
            <td>{{ r.borrowDate }}</td>
            <td>{{ r.dueDate }}</td>
            <td>
              <span v-if="r.emailSent" class="sent-badge">Đã gửi</span>
              <span v-else class="not-sent-badge">Chưa gửi</span>
            </td>
            <td>
              <button class="btn btn-sm" @click="doSendReminder(r)" :disabled="r._sending">
                {{ r._sending ? 'Đang gửi...' : 'Gửi email' }}
              </button>
            </td>
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
          <tr><th>ID</th><th>Người mượn</th><th>Sách</th><th>Bản sao</th><th>Ngày mượn</th><th>Hạn trả</th></tr>
        </thead>
        <tbody>
          <tr v-for="r in overdueRecords" :key="r.id">
            <td>{{ r.id }}</td>
            <td><router-link :to="'/admin/users/' + r.user?.id">{{ r.user?.fullName }}</router-link></td>
            <td><router-link :to="'/books/' + r.bookCopy?.book?.id">{{ r.bookCopy?.book?.title }}</router-link></td>
            <td>{{ r.bookCopy?.copyNumber || r.bookCopy?.id }}</td>
            <td>{{ r.borrowDate }}</td><td>{{ r.dueDate }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Tab: Lịch sử -->
    <div v-if="tab === 'history'">
      <div class="card">
        <h3>Tra cứu lịch sử mượn/trả</h3>
        <div class="lookup-section">
          <label class="lookup-label">Bạn đọc</label>
          <div class="lookup-row">
            <input v-model="historyQuery" placeholder="Nhập CCCD, tên hoặc ID bạn đọc" @keyup.enter="lookupHistoryUser" />
            <button class="btn btn-sm" @click="lookupHistoryUser" :disabled="!historyQuery.trim()">Tra cứu</button>
          </div>
          <div v-if="historyUserLookupLoading" class="lookup-status">Đang tra cứu...</div>
          <div v-if="historySelectedUser" class="lookup-result success">
            <strong>{{ historySelectedUser.fullName }}</strong> ({{ historySelectedUser.username }}) — {{ historySelectedUser.userType || 'ADMIN' }}
            <button class="btn btn-sm btn-danger" @click="historySelectedUser = null; historyQuery = ''; historyRecords = []" style="margin-left:0.5rem">Bỏ chọn</button>
          </div>
          <div v-if="historyUserResults.length > 0 && !historySelectedUser" class="lookup-results">
            <div v-for="u in historyUserResults" :key="u.id" class="lookup-item" @click="selectHistoryUser(u)">
              <strong>{{ u.fullName }}</strong> ({{ u.username }}) — CCCD: {{ u.identityCard }}
            </div>
          </div>
          <p v-if="historyUserNotFound" class="error">Không tìm thấy bạn đọc.</p>
        </div>
      </div>
      <div v-if="historySelectedUser">
        <p v-if="historyLoading">Đang tải lịch sử...</p>
        <div v-else-if="historyRecords.length === 0" class="empty">Người dùng này chưa có lịch sử mượn/trả.</div>
        <table v-else class="table">
          <thead>
            <tr><th>ID</th><th>Sách</th><th>Bản sao</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Trạng thái</th></tr>
          </thead>
          <tbody>
            <tr v-for="r in historyRecords" :key="r.id">
              <td>{{ r.id }}</td>
              <td><router-link :to="'/books/' + r.bookCopy?.book?.id">{{ r.bookCopy?.book?.title }}</router-link></td>
              <td>{{ r.bookCopy?.copyNumber || r.bookCopy?.id }}</td>
              <td>{{ r.borrowDate }}</td>
              <td>{{ r.dueDate }}</td>
              <td>{{ r.returnDate || '—' }}</td>
              <td><span :class="'status-' + r.status.toLowerCase()">{{ statusLabel(r.status) }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Tab: Tất cả lịch sử -->
    <div v-if="tab === 'all-history'">
      <p v-if="allHistoryLoading">Đang tải...</p>
      <div v-else-if="allHistoryRecords.length === 0" class="empty">Chưa có lịch sử mượn/trả.</div>
      <table v-else class="table">
        <thead>
          <tr><th>ID</th><th>Người mượn</th><th>Sách</th><th>Bản sao</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Trạng thái</th></tr>
        </thead>
        <tbody>
          <tr v-for="r in allHistoryRecords" :key="r.id">
            <td>{{ r.id }}</td>
            <td><router-link :to="'/admin/users/' + r.user?.id">{{ r.user?.fullName }}</router-link></td>
            <td><router-link :to="'/books/' + r.bookCopy?.book?.id">{{ r.bookCopy?.book?.title }}</router-link></td>
            <td>{{ r.bookCopy?.copyNumber || r.bookCopy?.id }}</td>
            <td>{{ r.borrowDate }}</td>
            <td>{{ r.dueDate }}</td>
            <td>{{ r.returnDate || '—' }}</td>
            <td><span :class="'status-' + r.status.toLowerCase()">{{ statusLabel(r.status) }}</span></td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Tab: Trả sách -->
    <div v-if="tab === 'return'" class="card">
      <h3>Trả sách</h3>
      <form @submit.prevent="returnBook">
        <div class="field">
          <label>ID bản sách</label>
          <input v-model.number="returnBookCopyId" type="number" placeholder="Nhập ID bản sách" required />
        </div>
        <p v-if="returnMsg" :class="returnError ? 'error' : 'success'">{{ returnMsg }}</p>
        <button type="submit" class="btn">Xác nhận trả</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { borrowApi } from '../api/borrow'
import { userApi } from '../api/users'
import { bookApi } from '../api/books'

const tab = ref('create')

// -- Tạo phiếu mượn --
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

const copiesLoading = ref(false)
const copies = ref([])
const selectedCopyId = ref(null)

const availableCopies = computed(() => copies.value.filter(c => c.status === 'AVAILABLE'))

const dueDate = ref('')
const minDate = computed(() => new Date().toISOString().slice(0, 10))

const borrowing = ref(false)
const borrowMsg = ref('')
const borrowError = ref(false)

const currentRecords = ref([])
const currentLoading = ref(false)

const dueRecords = ref([])
const dueLoading = ref(false)

const overdueRecords = ref([])
const overdueLoading = ref(false)

const returnBookCopyId = ref('')
const returnMsg = ref('')
const returnError = ref(false)

// -- Lịch sử --
const historyQuery = ref('')
const historySelectedUser = ref(null)
const historyUserResults = ref([])
const historyUserLookupLoading = ref(false)
const historyUserNotFound = ref(false)
const historyRecords = ref([])
const historyLoading = ref(false)

// -- Tất cả lịch sử --
const allHistoryRecords = ref([])
const allHistoryLoading = ref(false)

function resetUser() {
  selectedUser.value = null
  userQuery.value = ''
  userResults.value = []
}

function resetBook() {
  selectedBook.value = null
  bookQuery.value = ''
  bookResults.value = []
  copies.value = []
  selectedCopyId.value = null
}

async function lookupUser() {
  const q = userQuery.value.trim()
  if (!q) return
  resetUser()
  userNotFound.value = false
  userLookupLoading.value = true

  if (/^\d+$/.test(q)) {
    try {
      const res = await userApi.getById(q)
      selectedUser.value = res.data
      userLookupLoading.value = false
      return
    } catch { /* fall through */ }
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
  resetBook()
  bookNotFound.value = false
  bookLookupLoading.value = true

  if (/^\d+$/.test(q)) {
    try {
      const res = await bookApi.getById(q)
      await selectBook(res.data)
      bookQuery.value = res.data.title
      bookLookupLoading.value = false
      return
    } catch { /* fall through */ }
  }

  try {
    const res = await bookApi.getPaged(0, 20, { title: q })
    const matches = res.data.content
    if (matches.length === 1) {
      await selectBook(matches[0])
      bookQuery.value = matches[0].title
    } else if (matches.length > 1) {
      bookResults.value = matches
    } else {
      bookNotFound.value = true
    }
  } catch {
    bookNotFound.value = true
  }
  bookLookupLoading.value = false
}

async function selectBook(b) {
  selectedBook.value = b
  bookResults.value = []
  bookQuery.value = b.title
  copiesLoading.value = true
  selectedCopyId.value = null
  try {
    const res = await bookApi.getCopies(b.id)
    copies.value = res.data
  } catch {
    copies.value = []
  } finally {
    copiesLoading.value = false
  }
}

async function createBorrow() {
  if (!selectedUser.value || !selectedCopyId.value) return
  borrowMsg.value = ''
  borrowing.value = true
  try {
    await borrowApi.borrow(selectedUser.value.id, selectedCopyId.value, dueDate.value || null)
    borrowMsg.value = 'Tạo phiếu mượn thành công!'
    borrowError.value = false
    resetUser()
    resetBook()
    dueDate.value = ''
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

async function doSendReminder(r) {
  r._sending = true
  try {
    const res = await borrowApi.sendReminder(r.id)
    r.emailSent = true
    alert(res.data)
  } catch (e) {
    alert(e.response?.data?.message || 'Gửi email thất bại.')
  } finally {
    r._sending = false
  }
}

async function returnBook() {
  returnMsg.value = ''
  try {
    await borrowApi.returnBook(returnBookCopyId.value)
    returnMsg.value = 'Trả sách thành công!'
    returnError.value = false
    returnBookCopyId.value = ''
  } catch (e) {
    returnMsg.value = e.response?.data?.message || 'Trả sách thất bại.'
    returnError.value = true
  }
}

// -- Lịch sử --
async function lookupHistoryUser() {
  const q = historyQuery.value.trim()
  if (!q) return
  historySelectedUser.value = null
  historyUserResults.value = []
  historyUserNotFound.value = false
  historyUserLookupLoading.value = true

  if (/^\d+$/.test(q)) {
    try {
      const res = await userApi.getById(q)
      historySelectedUser.value = res.data
      historyUserLookupLoading.value = false
      fetchHistory(res.data.id)
      return
    } catch { /* fall through */ }
  }

  try {
    const res = await userApi.search(q)
    if (res.data.length === 1) {
      historySelectedUser.value = res.data[0]
      historyQuery.value = res.data[0].fullName
      fetchHistory(res.data[0].id)
    } else if (res.data.length > 1) {
      historyUserResults.value = res.data
    } else {
      historyUserNotFound.value = true
    }
  } catch {
    historyUserNotFound.value = true
  }
  historyUserLookupLoading.value = false
}

function selectHistoryUser(u) {
  historySelectedUser.value = u
  historyUserResults.value = []
  historyQuery.value = u.fullName
  fetchHistory(u.id)
}

async function fetchHistory(userId) {
  historyLoading.value = true
  try {
    const res = await borrowApi.history(userId)
    historyRecords.value = res.data
  } catch { historyRecords.value = [] }
  finally { historyLoading.value = false }
}

async function fetchAllHistory() {
  allHistoryLoading.value = true
  try {
    const res = await borrowApi.historyAll()
    allHistoryRecords.value = res.data
  } catch { allHistoryRecords.value = [] }
  finally { allHistoryLoading.value = false }
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
.input { padding: 0.5rem 0.75rem; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.95rem; width: 100%; box-sizing: border-box; }
.hint { display: block; color: #888; font-size: 0.8rem; margin-top: 0.25rem; }
.sent-badge { color: #059669; font-weight: 600; font-size: 0.85rem; }
.not-sent-badge { color: #d97706; font-weight: 600; font-size: 0.85rem; }
</style>
