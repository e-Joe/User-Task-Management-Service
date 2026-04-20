# Java Backend — Task Management API

Spring Boot REST API for managing users and tasks with validation, error handling, and request logging.

## Quick Start

```bash
cd java-backend
mvn spring-boot:run
```

Server starts on `http://localhost:8080`

## What Was Added

### New Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/users` | Create user (validates name, email format, role) |
| `POST` | `/api/tasks` | Create task (validates status enum, userId exists) |
| `PUT` | `/api/tasks/{id}` | Partial task update (404 if not found) |

### Centralized Error Handling

`GlobalExceptionHandler` (`@RestControllerAdvice`) catches all exceptions and returns consistent JSON errors:

```json
{ "error": "Name is required", "status": 400 }
```

- `ValidationException` → 400
- `ResourceNotFoundException` → 404
- `MethodArgumentNotValidException` (Bean Validation) → 400
- Unhandled exceptions → 500 (internal details not exposed)

### Request Validation

Separate request DTOs with Bean Validation annotations:

- `CreateUserRequest` — `@NotBlank` on name/role, `@Email` on email
- `CreateTaskRequest` — `@NotBlank` on title/status, `@NotNull` on userId
- `UpdateTaskRequest` — all fields optional (`Integer` for userId to distinguish null from 0)

Status must be one of: `pending`, `in-progress`, `completed`.

### Request Logging

`RequestLoggingFilter` (extends `OncePerRequestFilter`) logs every request:

```
INFO  POST /api/users 201 - 12ms
INFO  GET /api/tasks 200 - 3ms
INFO  PUT /api/tasks/999 404 - 1ms
```

### Tests (25 total)

```bash
mvn test
```

- **DataStoreTest** (14 tests) — user/task creation, updates, validation errors, edge cases
- **UserControllerTest** (5 tests) — POST success/failure, GET, validation
- **TaskControllerTest** (6 tests) — POST/PUT success/failure, GET, 404 handling

## Project Structure

```
src/main/java/com/developer/test/
├── controller/          # REST endpoints
├── dto/                 # Request/response objects
├── exception/           # Custom exceptions + global handler
├── filter/              # Request logging
├── model/               # User, Task entities
└── service/             # DataStore (in-memory, thread-safe)
```

## Existing Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/health` | Health check |
| `GET` | `/api/users` | All users |
| `GET` | `/api/users/{id}` | User by ID |
| `GET` | `/api/tasks` | All tasks (supports `?status=` and `?userId=` filters) |
| `GET` | `/api/stats` | User/task statistics |

## Requirements

- Java 11+
- Maven 3.6+
