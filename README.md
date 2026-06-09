# Thư viện — Library Management System

Hệ thống quản lý thư viện trường học: quản lý sách, người dùng (học sinh/giáo viên/khách), danh mục và hoạt động mượn/trả sách. Hỗ trợ phân quyền, thống kê dashboard, và gửi email nhắc nhở hạn trả.

## Công nghệ

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Database | H2 (demo) / PostgreSQL 17 (production) |
| ORM | Spring Data JPA + Hibernate |
| Security | Spring Security (HTTP Basic, BCrypt) |
| Mapping | MapStruct 1.6.3 + Lombok 1.18.34 |
| API docs | springdoc-openapi 2.8.3 (Swagger UI) |

## Yêu cầu

- **JDK 21** (kiểm tra: `java -version`)
- **Maven Wrapper** (đi kèm project — `mvnw.cmd` / `mvnw`)

## Chạy ứng dụng

### Demo (H2) — mặc định, không cần cài đặt

```powershell
.\mvnw.cmd spring-boot:run
```

Lần chạy đầu tiên tự động seed dữ liệu mẫu (1 admin, 99 bạn đọc, 10 danh mục, 1000 sách).

Database H2 lưu ở `./data/library_db`, persist giữa các lần restart.

### Production (PostgreSQL)

Chuyển file `application.properties` sang cấu hình PostgreSQL (port 5432, database `library_db`, user `postgres`, password `123456`).

## Tài khoản mặc định

| Vai trò | Username | Password |
|---|---|---|
| Thủ thư (ADMIN) | `user1` | `123456` |
| Bạn đọc (PATRON) | `user2` … `user100` | `123456` |

## API documentation

- Swagger UI: [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Cấu trúc thư mục

```
src/main/java/org/example/thuvien/
├── config/          # Security, CORS
├── controller/      # REST controllers
├── dto/             # Request/Response DTOs
├── exception/       # Global exception handler + custom exceptions
├── mapper/          # MapStruct mappers
├── model/           # JPA entities
├── repository/      # Spring Data repositories
├── seed/            # Data seeder (CommandLineRunner)
└── service/         # Business logic layer
```

## Triển khai với Docker

### Yêu cầu

- Docker & Docker Compose

### Cấu trúc

```
Dockerfile              # Backend (Spring Boot)
thuvien-ui/Dockerfile   # Frontend (Vue + nginx)
docker-compose.yml      # Orchestrator
```

### Biến môi trường

| Biến | Mặc định | Mô tả |
|---|---|---|
| `DB_USERNAME` | `sa` | Username database (H2) |
| `DB_PASSWORD` | *(rỗng)* | Password database |
| `MAIL_USERNAME` | *(rỗng)* | Email gửi thông báo |
| `MAIL_PASSWORD` | *(rỗng)* | App password email |

### Build & chạy

```powershell
docker compose up -d --build
```

- **Frontend:** http://localhost:80
- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/
- **H2 Console:** http://localhost:8080/h2-console

### Dừng

```powershell
docker compose down
```

Xoá luôn volume database:

```powershell
docker compose down -v
```

## Build & Test

```powershell
.\mvnw.cmd clean install   # Build + test
.\mvnw.cmd test             # Chạy test
```

Tests yêu cầu PostgreSQL đang chạy (xem AGENTS.md).
