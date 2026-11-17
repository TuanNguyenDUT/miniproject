# Employee Management System - Spring Boot Mini Project

Spring Boot Mini Project covering Modules 1–10 (Employee & Department Management, Validation, Pagination, Caching & Scheduling, Spring Security + JWT, Reporting & Analytics).

## Modules Implemented

- **Modules 1–4**: Spring Boot setup, JPA entities (`Employee`, `Department`, `User`), H2 database, `DataInitializer`.
- **Modules 5–7**: REST API & Thymeleaf CRUD (`/employees`, `/api/employees`), Bean Validation, `@RestControllerAdvice` global exception handling, Pagination & keyword search.
- **Module 8 (Caching & Scheduling)**:
  - `@EnableCaching` with Caffeine 1-minute TTL on `GET /api/employees/count`.
  - `@EnableScheduling` heartbeat task (`SystemHeartbeatScheduler`) logging `"System running"` every 30 seconds.
- **Module 9 (Spring Security & JWT)**:
  - `User` entity (`username`, `password`, `role`: `USER` / `ADMIN`).
  - Register (`POST /api/auth/register`, `/register`) & Login (`POST /api/auth/login`, `/login`) with HTTP Basic + JWT.
  - `USER` can view employee list and statistics; `ADMIN` can perform full CRUD.
- **Module 10 (Reporting & Analytics)**:
  - Custom `@Query` methods in `EmployeeRepository` for total employees and count by department.
  - `GET /api/employees/statistics` and Thymeleaf view `/employees/statistics`.

## Default Accounts

- **Admin**: `admin` / `admin123`
- **User**: `user` / `user123`

## Run & Test

```bash
mvn test
mvn spring-boot:run
```
