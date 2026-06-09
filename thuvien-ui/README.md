# thuvien-ui — Frontend cho Hệ thống Quản lý Thư viện

Giao diện người dùng (UI) cho backend **thuvien** — quản lý sách, người dùng, danh mục và mượn/trả sách.

## Công nghệ

- **Vue 3** (Composition API, `<script setup>`)
- **Vite 6** (build tool)
- **Vue Router 4** (SPA routing)
- **Pinia 3** (state management)
- **Axios** (HTTP client)
- **CSS** thuần (không dùng UI framework)

## Yêu cầu

- Node.js >= 18
- Backend `thuvien` đang chạy tại `http://localhost:8080`

## Cài đặt & chạy

```powershell
cd thuvien-ui
npm install
npm run dev
```

Mở trình duyệt tại `http://localhost:5173`.

## Tính năng

### Không cần đăng nhập
- Xem danh sách sách
- Tìm kiếm sách (theo tên, tác giả, danh mục)
- Xem chi tiết sách
- Đăng ký tài khoản
- Đăng nhập

### Bạn đọc (PATRON)
- Xem thông tin tài khoản
- Lịch sử mượn sách
- Sách đang mượn
- Mượn sách

### Thủ thư (ADMIN)
- Tất cả quyền của bạn đọc
- CRUD sách
- CRUD danh mục
- Quản lý người dùng
- Quản lý mượn/trả (trả sách, xem sắp hạn/quá hạn, đếm chưa trả)
- Dashboard thống kê

## Triển khai với Docker

```powershell
docker compose up -d --build
```

Frontend được build static, phục vụ qua nginx tại port 80. API proxy qua `/api/` → backend container.

Xem thêm `docker-compose.yml` và `Dockerfile` ở thư mục gốc project.

## Cấu trúc thư mục

```
thuvien-ui/
├── src/
│   ├── api/          # Axios instance + API modules
│   ├── components/   # (dành cho component dùng chung)
│   ├── router/       # Vue Router config
│   ├── stores/       # Pinia stores (auth)
│   ├── styles/       # Global CSS
│   └── views/        # Trang (views)
├── index.html
├── package.json
├── vite.config.js
├── Dockerfile
├── nginx.conf
└── README.md
```
