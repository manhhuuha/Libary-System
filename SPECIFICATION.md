# Đặc tả tính năng — Hệ thống Quản lý Thư viện (thuvien)

> **Phiên bản:** 2.1  
> **Công nghệ:** Java 21, Spring Boot 4.x, Spring Security, Spring Data JPA, PostgreSQL 17 / H2, MapStruct, Swagger/OpenAPI  
> **Kiến trúc:** Controller → Service → Repository → Database (H2 file / PostgreSQL)  
> **Triển khai:** Docker (multi-stage build + docker-compose)

---

## Mục lục

1. [Tổng quan](#1-tổng-quan)
2. [Đối tượng sử dụng](#2-đối-tượng-sử-dụng)
3. [Phân quyền (Security)](#3-phân-quyền-security)
4. [Module Người dùng (User)](#4-module-người-dùng-user)
5. [Module Danh mục (Category)](#5-module-danh-mục-category)
6. [Module Sách (Book)](#6-module-sách-book)
7. [Module Mượn/Trả (Borrow)](#7-module-mượntrả-borrow)
8. [Module Thống kê (Dashboard)](#8-module-thống-kê-dashboard)
9. [Module Thông báo (Notification)](#9-module-thông-báo-notification)
10. [Xử lý ngoại lệ (Global Exception Handler)](#10-xử-lý-ngoại-lệ-global-exception-handler)
11. [Soft Delete](#11-soft-delete)
12. [API Documentation](#12-api-documentation)

---

## 1. Tổng quan

Hệ thống quản lý thư viện trường học, cho phép quản lý sách, người dùng (học sinh, giáo viên, khách), danh mục và hoạt động mượn/trả sách. Hỗ trợ phân quyền, thống kê, và gửi email nhắc nhở hạn trả.

### Mô hình dữ liệu

```
Book (đầu sách)           BookCopy (bản sách)        BorrowRecord
┌──────────────────┐     ┌──────────────────┐     ┌───────────────────┐
│ id               │◄────│ book_id (FK)      │     │ user_id           │
│ title            │     │ id                │◄────│ book_copy_id (FK) │
│ author           │     │ copyNumber        │     │ borrowDate        │
│ isbn (unique)    │     │ status (AVAILABLE │     │ dueDate           │
│ publishedYear    │     │        │BORROWED  │     │ returnDate        │
│ location         │     │        │DAMAGED   │     │ status            │
│ category_id (FK) │     │        │LOST)     │     │ emailSent         │
└──────────────────┘     └──────────────────┘     └───────────────────┘
```

Mỗi Book có 1–nhiều BookCopy. Mỗi lần mượn là mượn 1 BookCopy cụ thể.

---

## 2. Đối tượng sử dụng

| Đối tượng | Vai trò (Role) | Mô tả |
|---|---|---|
| Thủ thư | `ADMIN` | Quản lý toàn bộ hệ thống |
| Học sinh | `PATRON` + `userType=STUDENT` | Tra cứu, mượn/trả sách |
| Giáo viên | `PATRON` + `userType=TEACHER` | Tra cứu, mượn/trả sách |
| Khách | `PATRON` + `userType=GUEST` | Tra cứu, mượn/trả sách |

---

## 3. Phân quyền (Security)

- **Xác thực:** HTTP Basic với mã hóa mật khẩu BCrypt.
- **CORS:** Cho phép frontend tại `http://localhost:5173` (Vue).
- **Role:** `ADMIN` (thủ thư), `PATRON` (bạn đọc — học sinh, giáo viên, khách).
- **Mặc định đăng ký:** `role = PATRON`.

### Bảng phân quyền API

| Endpoint | ADMIN | PATRON | Public |
|---|---|---|---|
| `POST /api/users/register` | | | ✓ |
| `GET /api/users/me` | ✓ | ✓ | |
| `GET /api/users/me/borrow-history` | ✓ | ✓ | |
| `GET /api/users/me/current-borrows` | ✓ | ✓ | |
| `GET /api/users` | ✓ | | |
| `GET /api/users/{id}` | ✓ | | |
| `PUT /api/users/{id}` | ✓ | | |
| `DELETE /api/users/{id}` | ✓ | | |
| `GET /api/books` | ✓ | ✓ | ✓ |
| `GET /api/books/{id}` | ✓ | ✓ | ✓ |
| `GET /api/books/{id}/copies` | ✓ | ✓ | ✓ |
| `POST /api/books` | ✓ | | |
| `PUT /api/books/{id}` | ✓ | | |
| `DELETE /api/books/{id}` | ✓ | | |
| `GET /api/categories` | ✓ | ✓ | ✓ |
| `POST /api/categories` | ✓ | | |
| `DELETE /api/categories/{id}` | ✓ | | |
| `POST /api/borrow?userId=&bookCopyId=&dueDate=` | ✓ | | |
| `PUT /api/borrow/return?bookCopyId=` | ✓ | | |
| `GET /api/borrow/due-soon` | ✓ | | |
| `GET /api/borrow/overdue` | ✓ | | |
| `GET /api/borrow/count-book-not-return` | ✓ | | |
| `GET /api/dashboard/summary` | ✓ | | |
| `/swagger-ui/**`, `/v3/api-docs/**` | | | ✓ |

---

## 4. Module Người dùng (User)

### 4.1. Entity — `User`

| Trường | Kiểu | Ràng buộc | Ghi chú |
|---|---|---|---|
| `id` | Long (PK, auto-increment) | | |
| `fullName` | String | `@NotBlank` | Họ và tên |
| `email` | String | `@Email` | |
| `phoneNumber` | String | `@Pattern(regexp="^[0-9]{10}$")` | 10 chữ số |
| `identityCard` | String | `@NotBlank`, `@Column(unique=true)` | CCCD/CMND không trùng |
| `username` | String | `@Column(unique=true)` | Tên đăng nhập |
| `password` | String | | Mã hóa BCrypt khi lưu |
| `role` | String | | `ADMIN` hoặc `PATRON` |
| `userType` | String | | `STUDENT`, `TEACHER`, hoặc `GUEST` |
| `active` | Boolean | | `true` nếu tài khoản đang hoạt động |
| `createdAt` | LocalDateTime | | Thời điểm tạo tài khoản |
| `borrowRecords` | List\<BorrowRecord\> | `@OneToMany`, `@JsonIgnore` | |

**Ràng buộc:** Nếu `role = ADMIN` thì `userType` có thể để null (không áp dụng phân loại).

### 4.2. API

| Phương thức | Endpoint | Quyền | Mô tả | Request | Response |
|---|---|---|---|---|---|
| `POST` | `/api/users/register` | Public | Đăng ký tài khoản mới | `UserRequestDTO` | `UserResponseDTO` |
| `GET` | `/api/users/me` | Authenticated | Lấy thông tin người dùng hiện tại | — | `UserResponseDTO` |
| `GET` | `/api/users/me/borrow-history` | Authenticated | Lịch sử mượn của bản thân | — | `List<BorrowRecord>` |
| `GET` | `/api/users/me/current-borrows` | Authenticated | Các sách đang mượn | — | `List<BorrowRecord>` |
| `GET` | `/api/users/search?keyword=` | ADMIN | Tìm kiếm người dùng theo tên/CCCD | Query param | `List<UserResponseDTO>` |
| `GET` | `/api/users` | ADMIN | Danh sách tất cả người dùng | — | `List<UserResponseDTO>` |
| `GET` | `/api/users/{id}` | ADMIN | Chi tiết người dùng | — | `UserResponseDTO` |
| `PUT` | `/api/users/{id}` | ADMIN | Cập nhật thông tin | `UserRequestDTO` | `UserResponseDTO` |
| `DELETE` | `/api/users/{id}` | ADMIN | Xóa người dùng | — | `String` (thông báo) |

### 4.3. DTO

**`UserRequestDTO`** (Java record):

| Trường | Kiểu | Validation | Ghi chú |
|---|---|---|---|
| `fullName` | String | `@NotBlank` | |
| `email` | String | `@Email` | |
| `phoneNumber` | String | `@NotBlank`, `@Pattern("^[0-9]{10}$")` | |
| `identityCard` | String | `@NotBlank` | |
| `username` | String | | Có thể để trống, hệ thống tự sinh |
| `password` | String | | Có thể để trống |
| `userType` | String | | `STUDENT`, `TEACHER`, `GUEST` |

> **Lưu ý:** `role` không nằm trong DTO — mặc định `PATRON` khi đăng ký qua MapStruct.

**`UserResponseDTO`** (Java record — không chứa password):

```java
public record UserResponseDTO(
    Long id, String fullName, String email, String phoneNumber,
    String identityCard, String username, String role,
    String userType, Boolean active, LocalDateTime createdAt
) {}
```

### 4.4. MapStruct Mapping (`UserMapper`)

- `UserRequestDTO → User`: bỏ qua `id`, `borrowRecords`; mặc định `role = "PATRON"`; set `active = true`; set `createdAt = LocalDateTime.now()`.
- `User → UserResponseDTO`: ánh xạ trực tiếp.

---

## 5. Module Danh mục (Category)

### 5.1. Entity — `Category`

| Trường | Kiểu | Ràng buộc | Ghi chú |
|---|---|---|---|
| `id` | Long (PK, auto-increment) | | |
| `name` | String | `@Column(nullable=false, unique=true)` | Tên danh mục (CNTT, Văn học, Kinh tế...) |
| `books` | List\<Book\> | `@OneToMany(mappedBy="category")`, `@JsonIgnore` | |

### 5.2. API

| Phương thức | Endpoint | Quyền | Mô tả | Response |
|---|---|---|---|---|
| `GET` | `/api/categories` | Public | Lấy tất cả danh mục | `List<Category>` |
| `POST` | `/api/categories` | ADMIN | Tạo danh mục mới | `Category` |
| `DELETE` | `/api/categories/{id}` | ADMIN | Xóa danh mục | `String` (thông báo) |

### 5.3. Soft Delete

Category được soft delete qua `@SoftDelete(columnName = "is_deleted")`.

---

## 6. Module Sách (Book)

### 6.1. Entity — `Book`

| Trường | Kiểu | Ràng buộc | Ghi chú |
|---|---|---|---|
| `id` | Long (PK, auto-increment) | | |
| `title` | String | `@NotBlank`, `@Size(min=2, max=100)` | Tên sách |
| `author` | String | `@NotBlank` | Tác giả |
| `isbn` | String | `@Pattern(regexp="^[0-9]{10,13}$")`, `@Column(unique=true)` | Mã ISBN 10–13 chữ số |
| `publishedYear` | int | `@Min(1000)`, `@Max(value=2025)` | Năm xuất bản |
| `location` | String | | Vị trí trên kệ sách |
| `category` | Category | `@ManyToOne(fetch=LAZY)` | Danh mục |

### 6.2. Entity — `BookCopy`

| Trường | Kiểu | Ràng buộc | Ghi chú |
|---|---|---|---|
| `id` | Long (PK, auto-increment) | | |
| `book` | Book | `@ManyToOne(fetch=LAZY)` | Đầu sách |
| `copyNumber` | int | | Số thứ tự bản sao (1, 2, 3...) |
| `status` | BookCopyStatus enum | | `AVAILABLE` / `BORROWED` / `DAMAGED` / `LOST` |

### 6.3. Quy tắc trạng thái BookCopy

- **`AVAILABLE`:** Bản sách có thể mượn.
- **`BORROWED`:** Đang được mượn.
- **`DAMAGED`:** Hư hỏng, không thể mượn.
- **`LOST`:** Mất, không thể mượn.

> Khi mượn: BookCopy được chọn cụ thể, không chỉ định đầu sách chung.

### 6.4. API

| Phương thức | Endpoint | Quyền | Mô tả | Request | Response |
|---|---|---|---|---|---|
| `GET` | `/api/books?page=&size=&title=&author=&categoryName=` | Public | Danh sách sách (phân trang) | Query params | `Page<BookResponseDTO>` |
| `GET` | `/api/books/{id}` | Public | Chi tiết 1 sách | — | `Book` |
| `GET` | `/api/books/{id}/copies` | Public | Danh sách BookCopy của sách | — | `List<BookCopy>` |
| `POST` | `/api/books` | ADMIN | Thêm sách mới | `Book` | `Book` |
| `PUT` | `/api/books/{id}` | ADMIN | Cập nhật sách | `Book` | `Book` |
| `DELETE` | `/api/books/{id}` | ADMIN | Xóa sách | — | `String` (thông báo) |

### 6.5. Tìm kiếm sách (`GET /api/books`)

Query params (tất cả optional, không phân biệt hoa/thường, dùng logic AND):

| Param | Mô tả |
|---|---|
| `page` | Số trang (mặc định 0) |
| `size` | Kích thước trang (mặc định 12) |
| `title` | Tìm theo tiêu đề |
| `author` | Tìm theo tác giả |
| `categoryName` | Tìm theo tên danh mục |

### 6.6. DTO — `BookResponseDTO`

```java
public record BookResponseDTO(
    Long id, String title, String author, String isbn,
    String location, String categoryName
) {}
```

Trong đó `categoryName` được map từ `category.name`. Mặc định `"Chưa phân loại"` nếu category là null.

### 6.6. Soft Delete

Book được soft delete qua `@SoftDelete(columnName = "is_deleted")`.

---

## 7. Module Mượn/Trả (Borrow)

### 7.1. Entity — `BorrowRecord`

| Trường | Kiểu | Ràng buộc | Ghi chú |
|---|---|---|---|
| `id` | Long (PK, auto-increment) | | |
| `borrowDate` | LocalDate | `@NotNull` | Ngày mượn |
| `dueDate` | LocalDate | `@NotNull` | Hạn trả = borrowDate + 14 ngày |
| `returnDate` | LocalDate | nullable | Ngày trả thực tế (null nếu chưa trả) |
| `status` | BorrowStatus enum | `@NotNull` | `BORROWING` / `RETURNED` / `OVERDUE` |
| `user` | User | `@ManyToOne` | Người mượn |
| `bookCopy` | BookCopy | `@ManyToOne` | Bản sách cụ thể được mượn |

### 7.2. BorrowStatus Enum

```java
public enum BorrowStatus {
    BORROWING,  // Đang mượn
    RETURNED,   // Đã trả
    OVERDUE     // Quá hạn
}
```

### 7.3. API

| Phương thức | Endpoint | Quyền | Mô tả | Request | Response |
|---|---|---|---|---|---|
| `POST` | `/api/borrow?userId=&bookCopyId=&dueDate=` | ADMIN | Mượn sách (thủ thư tạo phiếu) | Query params | `BorrowRecord` |
| `PUT` | `/api/borrow/return?bookCopyId=` | ADMIN | Đánh dấu đã trả sách | Query param | `BorrowRecord` |
| `GET` | `/api/borrow/due-soon` | ADMIN | Danh sách sắp đến hạn (≤ 3 ngày) | — | `List<BorrowRecord>` |
| `GET` | `/api/borrow/overdue` | ADMIN | Danh sách quá hạn | — | `List<BorrowRecord>` |
| `GET` | `/api/borrow/count-book-not-return` | ADMIN | Đếm số sách chưa trả | — | `long` |
| `GET` | `/api/borrow/current` | ADMIN | Danh sách chi tiết các sách đang mượn | — | `List<BorrowRecord>` |
| `GET` | `/api/borrow/history/all?page=&size=` | ADMIN | Lịch sử mượn/trả (phân trang) | Query params | `Page<BorrowRecord>` |
| `POST` | `/api/borrow/send-reminder/{id}` | ADMIN | Gửi email nhắc hạn trả | Path param | `String` |

### 7.4. Quy tắc nghiệp vụ khi mượn sách

```
Khi thủ thư tạo phiếu mượn cho bạn đọc:

1. Kiểm tra User tồn tại
2. Kiểm tra BookCopy tồn tại và status = AVAILABLE
3. Kiểm tra số sách chưa trả của User < 3 (returnDate IS NULL)
4. Kiểm tra User không có sách OVERDUE
5. Nếu tất cả hợp lệ:
   - Tạo BorrowRecord:
     - borrowDate = LocalDate.now()
     - dueDate = borrowDate + 14 ngày (hoặc do ADMIN chỉ định)
     - status = BORROWING
     - user, bookCopy
   - Đổi status của BookCopy thành BORROWED
   - Lưu BorrowRecord
   - Lưu BookCopy
```

### 7.5. Quy tắc nghiệp vụ khi trả sách

```
Khi ADMIN gửi yêu cầu trả sách:

1. Tìm BorrowRecord theo bookCopyId (với returnDate IS NULL)
2. Nếu không tìm thấy → báo lỗi
3. Nếu tìm thấy:
   - returnDate = LocalDate.now()
   - status = RETURNED
   - Đổi status của BookCopy thành AVAILABLE
   - Lưu BorrowRecord
   - Lưu BookCopy
```

### 7.6. Quy tắc OVERDUE

```
Hệ thống kiểm tra tự động (qua Scheduled job) hoặc kiểm tra khi người dùng mượn sách:

NẾU dueDate < LocalDate.now()
VÀ returnDate IS NULL
THÌ status = OVERDUE
```

### 7.7. Giới hạn mượn

| Quy tắc | Mô tả |
|---|---|
| Số lượng tối đa | Mỗi User chỉ được mượn tối đa **3 cuốn** chưa trả |
| OVERDUE | Nếu User đang có sách `OVERDUE` → **không được mượn thêm** |

---

## 8. Module Thống kê (Dashboard)

### 8.1. API

| Phương thức | Endpoint | Quyền | Mô tả | Response |
|---|---|---|---|---|
| `GET` | `/api/dashboard/summary` | ADMIN | Thống kê tổng quan | `DashboardSummaryDTO` |

### 8.2. DTO — `DashboardSummaryDTO`

```json
{
  "totalBooks": 150,
  "totalBookCopies": 350,
  "borrowedBooks": 42,
  "overdueBooks": 5,
  "totalUsers": 80
}
```

| Trường | Kiểu | Nguồn dữ liệu |
|---|---|---|
| `totalBooks` | long | Số lượng Book (count, không tính soft delete) |
| `totalBookCopies` | long | Số lượng BookCopy (`bookCopyRepository.count()`) |
| `borrowedBooks` | long | Số BorrowRecord có `status = BORROWING` |
| `overdueBooks` | long | Số BorrowRecord có `status = OVERDUE` |
| `totalUsers` | long | Số lượng User (count, không tính soft delete) |

---

## 9. Module Thông báo (Notification)

### 9.1. Công nghệ

- **Spring Mail** (`spring-boot-starter-mail`)
- **Spring Scheduler** (`@Scheduled` / `@EnableScheduling`)

### 9.2. Scheduled Job

| Thuộc tính | Giá trị |
|---|---|
| Tần suất | Mỗi ngày một lần (ví dụ: 08:00 AM) |
| Cron expression | `0 0 8 * * *` |

### 9.3. Logic gửi email

```
Mỗi ngày vào 08:00:

1. Truy vấn tất cả BorrowRecord có:
   - status = BORROWING
   - dueDate - LocalDate.now() <= 3 ngày
   - returnDate IS NULL

2. Với mỗi bản ghi:
   - Lấy email từ User
   - Soạn email với nội dung:
     - Tên người dùng
     - Tên sách
     - Ngày đến hạn (dueDate)
   - Gửi email
```

### 9.4. Cấu hình yêu cầu

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<email>
spring.mail.password=<app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 10. Xử lý ngoại lệ (Global Exception Handler)

### 10.1. Bảng xử lý lỗi

| Loại lỗi | HTTP Status | Message |
|---|---|---|
| `RuntimeException` (business logic) | `404 Not Found` hoặc `400 Bad Request` | `ex.getMessage()` |
| `MethodArgumentNotValidException` | `400 Bad Request` | Field error message đầu tiên |
| `Exception` (các lỗi khác chưa định nghĩa) | `500 Internal Server Error` | `"Có lỗi hệ thống xảy ra, vui lòng thử lại sau!"` |

### 10.2. Response mẫu

```json
{
  "status": 404,
  "message": "Xin lỗi, chúng tôi không tìm thấy cuốn sách này! ID: 99",
  "timestamp": "2025-06-10T14:30:00"
}
```

```json
{
  "status": 400,
  "message": "Bạn đã mượn đủ giới hạn 3 cuốn sách!",
  "timestamp": "2025-06-10T14:30:00"
}
```

```json
{
  "status": 500,
  "message": "Có lỗi hệ thống xảy ra, vui lòng thử lại sau!",
  "timestamp": "2025-06-10T14:30:00"
}
```

### 10.3. ErrorResponse DTO

```java
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
```

---

## 11. Soft Delete

### 11.1. Danh sách entity áp dụng soft delete

| Entity | Column name | Ghi chú |
|---|---|---|
| `User` | `is_deleted` | `@SoftDelete` |
| `Book` | `is_deleted` | `@SoftDelete` |
| `BookCopy` | — | **KHÔNG** soft delete |
| `Category` | `is_deleted` | `@SoftDelete` |

`BorrowRecord`: **KHÔNG** soft delete (xóa cứng).

---

## 12. API Documentation

- **Swagger UI:** `http://localhost:8080/swagger-ui/`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`
- **Thư viện:** `springdoc-openapi-starter-webmvc-ui` 2.8.3

---

## Phụ lục: Ma trận API tổng hợp

### User

| Endpoint | Method | Auth |
|---|---|---|
| `/api/users/register` | POST | Public |
| `/api/users/me` | GET | Authenticated |
| `/api/users/me/borrow-history` | GET | Authenticated |
| `/api/users/me/current-borrows` | GET | Authenticated |
| `/api/users` | GET | ADMIN |
| `/api/users/search` | GET | ADMIN |
| `/api/users/{id}` | GET | ADMIN |
| `/api/users/{id}` | PUT | ADMIN |
| `/api/users/{id}` | DELETE | ADMIN |

### Book

| Endpoint | Method | Auth |
|---|---|---|
| `/api/books` | GET | Public |
| `/api/books/{id}` | GET | Public |
| `/api/books/{id}/copies` | GET | Public |
| `/api/books` | POST | ADMIN |
| `/api/books/{id}` | PUT | ADMIN |
| `/api/books/{id}` | DELETE | ADMIN |

### Category

| Endpoint | Method | Auth |
|---|---|---|
| `/api/categories` | GET | Public |
| `/api/categories` | POST | ADMIN |
| `/api/categories/{id}` | DELETE | ADMIN |

### Borrow

| Endpoint | Method | Auth |
|---|---|---|
| `/api/borrow` | POST | ADMIN |
| `/api/borrow/return` | PUT | ADMIN |
| `/api/borrow/due-soon` | GET | ADMIN |
| `/api/borrow/overdue` | GET | ADMIN |
| `/api/borrow/count-book-not-return` | GET | ADMIN |
| `/api/borrow/current` | GET | ADMIN |
| `/api/borrow/history/all` | GET | ADMIN |
| `/api/borrow/send-reminder/{id}` | POST | ADMIN |

### Dashboard

| Endpoint | Method | Auth |
|---|---|---|
| `/api/dashboard/summary` | GET | ADMIN |

### Swagger

| Endpoint | Method | Auth |
|---|---|---|
| `/swagger-ui/**` | GET | Public |
| `/v3/api-docs/**` | GET | Public |
