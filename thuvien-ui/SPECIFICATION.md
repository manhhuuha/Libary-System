# Đặc tả Frontend — thuvien-ui

> **Phiên bản:** 1.0  
> **Công nghệ:** Vue 3 + Vite 6 + Vue Router 4 + Pinia 3 + Axios  
> **Kết nối:** REST API backend `thuvien` (Spring Boot) qua HTTP Basic Auth

---

## 1. Routing

| Path | View | Auth | Role | Mô tả |
|---|---|---|---|---|
| `/login` | LoginView | Public | — | Đăng nhập |
| `/register` | RegisterView | Public | — | Đăng ký tài khoản |
| `/` | BooksView | Public | — | Danh sách sách + tìm kiếm |
| `/books/:id` | BookDetailView | Public | — | Chi tiết sách + vị trí + trạng thái |
| `/profile` | ProfileView | Authenticated | — | Thông tin tài khoản |
| `/profile/borrow-history` | BorrowHistoryView | Authenticated | — | Lịch sử mượn |
| `/profile/current-borrows` | CurrentBorrowsView | Authenticated | — | Sách đang mượn |
| `/admin/books/new` | BookFormView | Authenticated | ADMIN | Thêm sách mới |
| `/admin/books/:id/edit` | BookFormView | Authenticated | ADMIN | Sửa sách |
| `/admin/users` | UsersView | Authenticated | ADMIN | Danh sách người dùng |
| `/admin/users/:id` | UserDetailView | Authenticated | ADMIN | Chi tiết người dùng |
| `/admin/categories` | CategoriesView | Authenticated | ADMIN | Quản lý danh mục |
| `/admin/borrow` | BorrowManagementView | Authenticated | ADMIN | Mượn/trả, sắp hạn, quá hạn |
| `/admin/dashboard` | DashboardView | Authenticated | ADMIN | Thống kê |

Điều hướng: `router.beforeEach` kiểm tra `meta.auth` (cần đăng nhập) và `meta.admin` (cần role ADMIN).

---

## 2. Luồng xác thực (Auth)

1. Người dùng nhập username + password tại `/login`
2. `AuthStore.login()` lưu `{ username, password }` vào `localStorage('auth')`
3. Axios interceptor tự động thêm `Authorization: Basic base64(...)` vào mọi request
4. Gọi `GET /api/users/me` để lấy thông tin user, lưu vào `localStorage('user')`
5. Nếu server trả về 401 → xóa auth, redirect `/login`
6. `AuthStore.isAdmin` = `user.role === 'ADMIN'`

---

## 3. Kết nối API

Tất cả request đi qua **Vite proxy** (`/api` → `http://localhost:8080`), tránh CORS trên development.

| API Module | File | Base path |
|---|---|---|
| users | `src/api/users.js` | `/api/users` |
| books | `src/api/books.js` | `/api/books` |
| categories | `src/api/categories.js` | `/api/categories` |
| borrow | `src/api/borrow.js` | `/api/borrow` |
| dashboard | `src/api/dashboard.js` | `/api/dashboard` |

---

## 4. Views

### 4.1. LoginView (`/login`)
- Form: username + password
- Gọi `auth.login()`, redirect về trang trước đó (query param `redirect`)

### 4.2. RegisterView (`/register`)
- Form đầy đủ: họ tên, email, phone, CCCD, username, password, loại bạn đọc
- Gọi `POST /api/users/register`
- Username/password để trống → backend tự tạo

### 4.3. BooksView (`/`)
- Grid hiển thị sách (card: tên, tác giả, danh mục, số bản)
- Thanh tìm kiếm (debounce 300ms) gọi `GET /api/books/search`
- Click card → `/books/:id`

### 4.4. BookDetailView (`/books/:id`)
- Thông tin chi tiết sách dạng bảng, hiển thị vị trí (location) nổi bật
- Hướng dẫn mang sách ra bàn thủ tục để mượn
- ADMIN: nút Sửa, Xóa

### 4.5. ProfileView (`/profile`)
- Thông tin user từ `AuthStore.user`
- Nav bar hiển thị trực tiếp link "Lịch sử mượn" và "Đang mượn" cho mọi user đã đăng nhập

### 4.6. BorrowHistoryView (`/profile/borrow-history`)
- Bảng lịch sử mượn (sách, ngày mượn, hạn trả, ngày trả, trạng thái)
- Gọi `GET /api/users/me/borrow-history`

### 4.7. CurrentBorrowsView (`/profile/current-borrows`)
- Bảng sách đang mượn
- Gọi `GET /api/users/me/current-borrows`

### 4.8. BookFormView (`/admin/books/new`, `/admin/books/:id/edit`)
- Form CRUD sách (tiêu đề, tác giả, ISBN, năm XB, vị trí, số bản, danh mục)
- Chế độ thêm hoặc sửa dựa trên route params
- Gọi `POST /api/books` hoặc `PUT /api/books/:id`

### 4.9. UsersView (`/admin/users`)
- Bảng tất cả người dùng
- Gọi `GET /api/users`

### 4.10. UserDetailView (`/admin/users/:id`)
- Chi tiết người dùng
- Nút Xóa người dùng

### 4.11. CategoriesView (`/admin/categories`)
- Form thêm danh mục + bảng danh sách
- Gọi `GET /api/categories`, `POST /api/categories`, `DELETE /api/categories/:id`

### 4.12. BorrowManagementView (`/admin/borrow`)
- Tab "Tạo phiếu mượn": nhập CCCD/tên/ID → tra cứu bạn đọc; nhập ID/tên sách → tra cứu sách; xác nhận → `POST /api/borrow`
- Tab "Đang mượn": danh sách chi tiết các bản ghi đang mượn → `GET /api/borrow/current`
- Tab "Sắp đến hạn": danh sách sách sắp đến hạn → `GET /api/borrow/due-soon`
- Tab "Quá hạn": danh sách sách quá hạn → `GET /api/borrow/overdue`
- Tab "Trả sách": nhập ID sách → `PUT /api/borrow/return`

### 4.13. DashboardView (`/admin/dashboard`)
- Grid thống kê: tổng sách, tổng bản, đang mượn, quá hạn, người dùng
- Gọi `GET /api/dashboard/summary`

---

## 5. CSS

- CSS thuần, không UI framework
- File chính: `src/styles/main.css`
- Biến màu: chủ đạo xanh `#1a73e8`, đỏ lỗi `#dc3545`, xanh thành công `#198754`
- Responsive: grid `auto-fill`, form `max-width: 480px`

---

## 6. Scripts

| Script | Mô tả |
|---|---|
| `npm run dev` | Chạy dev server (Vite) |
| `npm run build` | Build production |
| `npm run preview` | Preview build |
