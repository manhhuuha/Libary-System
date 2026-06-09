# AGENTS.md — thuvien (Library Management System)

## Project

Java 21, Spring Boot 4.0.6, single-module Maven project.  
`mvnw.cmd` (Windows) / `./mvnw` (Unix). H2 in-memory (demo) / PostgreSQL 17 (production).

## Before building

**Demo (H2, default):** No setup needed — database stored in `./data/library_db` file (persists across restarts).  
H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/library_db`).  
Seed data (1 admin, 99 users, 10 categories, 1000 books) tự động tạo khi chạy lần đầu.

**Production (PostgreSQL):** Switch `application.properties` — must have PostgreSQL on `localhost:5432`, database `library_db`, user `postgres`, password `123456`.  

Hibernate `ddl-auto=update` — schema auto-managed; no manual migration scripts.

## Commands

| Action | Command |
|---|---|
| Build (with tests) | `mvnw.cmd clean install` |
| Test only | `mvnw.cmd test` |
| Run dev server | `mvnw.cmd spring-boot:run` |
| Run JAR | `java -jar target/thuvien-0.0.1-SNAPSHOT.jar` |

All commands from project root.

## Tests

Single context-load test in `ThuvienApplicationTests`. No other tests exist.  
Tests require the PostgreSQL database to be running (same as dev).

## Code generation

**MapStruct 1.6.3** + **Lombok 1.18.34** — annotation processors configured in `pom.xml` with the Lombok-MapStruct binding bridge.  
Generated sources go to `target/generated-sources/annotations/`.  
After editing a mapper or entity, run `mvnw.cmd compile` to regenerate.

## Lint / Format / Static analysis

**None configured.** No checkstyle, spotbugs, PMD, or formatter setup.

## CI / Pre-commit

**None.** No GitHub Actions, no hooks. No AGENTS.md or other instruction files existed before this one.

## Security

- HTTP Basic auth, BCrypt password encoder.
- Roles: `ADMIN` (thủ thư), `PATRON` (bạn đọc — STUDENT/TEACHER/GUEST).
- Default role on registration: `PATRON` (set in `UserMapper`).
- UserType: `STUDENT`, `TEACHER`, `GUEST` (chỉ áp dụng cho PATRON).
- Public: `POST /api/users/register`, `GET /api/books/**`, `GET /api/categories`, `/swagger-ui/**`, `/v3/api-docs/**`.
- PATRON: `/api/users/me/**`, `POST /api/borrow`.
- Admin-only: users CRUD, books write, categories write, borrow return, due-soon, overdue, dashboard.
- CORS allows `http://localhost:5173` (Vue frontend dev server).

## API docs

Swagger UI at `http://localhost:8080/swagger-ui/` (via `springdoc-openapi` 2.8.3).

## Architecture

Standard layered: `controller → service → repository (JPA) → model`.  
No monorepo modules, no multi-package boundaries.  
Entrypoint: `ThuvienApplication.java`.
