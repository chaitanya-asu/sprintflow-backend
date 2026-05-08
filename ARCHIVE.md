# SprintFlow Backend — Technical Archive

Consolidated documentation from resolved bug fixes, implementation notes, and system architecture details.

---

## 1. Bug Fixes

### Sprint Update — Column 'end_date' cannot be null
**Root cause:** `mapDtoToEntity` only updates fields present in DTO; partial update without `endDate` sets it to null, violating NOT NULL constraint.
**Fix:** Added null validation in `SprintService.updateSprint()` to check `startDate`/`endDate` are not null before update; added try-catch and field validation in `SprintController` (blank title check, date ordering validation).
**Files:** `SprintService.java`, `SprintController.java`
**Response:** 400 Bad Request with messages "End date is required", "Start date is required", "Start date must be before end date".

### Task Endpoint 405 Method Not Supported
**Error:** `Request method 'GET' is not supported at uri=/api/tasks/my-tasks`
**Root cause:** Spring Boot not properly registering the GET method mapping (compilation/build cache issues, app not restarted).
**Fix:** Changed `@GetMapping("/my-tasks")` to `@RequestMapping(value = "/my-tasks", method = RequestMethod.GET, produces = "application/json")`.
**Action:** Clean rebuild via `mvnw clean package -DskipTests`.

### 429 Rate Limiting in Development
**Root cause:** Backend `RateLimitFilter` enforced 100 req/min/user even in dev.
**Fix:** Made rate limiting configurable via `app.rate-limit.enabled=false` (default for dev). Enable in production via env var. Increased default limit 100→500 req/min.

### Mail Encryption Key Length
**Root cause:** `APP_MAIL_KEY` was 31 hex chars instead of 32 (128 bits = 16 bytes).
**Fix:** Padded to 32 hex chars.

---

## 2. Task Management Implementation

### Entity (Task.java)
- Email-based assignment (not Employee ID)
- Fields: `assignedTo` (String email), `createdBy` (String email)
- `TaskStatus` enum: TO_DO, IN_PROGRESS, COMPLETED, BLOCKED
- `TaskPriority` enum: LOW, MEDIUM, HIGH, CRITICAL
- `sprint_id` nullable (FK → sprints)

### DTO (TaskDTO.java)
Fields: id, title, description, status, priority, sprintId, sprintTitle, assignedTo, createdBy, dueDate, createdAt, updatedAt

### Repository (TaskRepository.java)
- `findByAssignedTo(email)` — tasks assigned to a user
- `findByCreatedBy(email)` — tasks created by a user
- `findBySprintId(id)` — tasks for a sprint
- `findByStatus(status)` — tasks by status

### Service (TaskService.java)
- 11 methods covering CRUD, status update, reassign, role-based queries
- Supports display names ("To Do" vs "TO_DO")
- Auto-sets creator from SecurityContext

### Status Display Name Mapping
| Display Name | Enum |
|-------------|------|
| To Do | TO_DO |
| In Progress | IN_PROGRESS |
| Completed | COMPLETED |
| Blocked | BLOCKED |
| Low | LOW |
| Medium | MEDIUM |
| High | HIGH |
| Critical | CRITICAL |

### Database Schema (tasks)
```sql
CREATE TABLE tasks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  status ENUM('TO_DO','IN_PROGRESS','COMPLETED','BLOCKED') NOT NULL DEFAULT 'TO_DO',
  priority ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'MEDIUM',
  sprint_id BIGINT NULL,
  assigned_to VARCHAR(255),
  created_by VARCHAR(255),
  due_date DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_tasks_sprint FOREIGN KEY (sprint_id) REFERENCES sprints(id) ON DELETE SET NULL
);
```

---

## 3. Phase 5 Implementation Status

### Complete
- ✅ Task Management (backend: entities, DTOs, repository, service, controller, security)
- ✅ Chat Groups (backend: group endpoints, WebSocket)
- ✅ Audit Log Viewer (8 endpoints, search/filter, CSV export)

### In Progress
- ⏳ Password Reset Flow
- ⏳ Email Notifications (EmailService exists, Spring Mail added, templates pending)
- ⏳ Bulk Operations (CSV import, bulk attendance/sprint/user creation)
- ⏳ Advanced Reporting (attendance, sprint performance, PDF/Excel export)
- ⏳ Calendar Views (sprint, attendance, task deadline)

### Phase 5 Blocking Issues Resolved
1. `NoResourceFoundException` at `/api/messages/mark-read` — endpoint existed but not registered
2. `PropertyReferenceException` — JPA looks for `read` field instead of `isRead`
3. `ClassNotFoundException: com.sprintflow.entity.Role` — stale compiled classes / DevTools cache
4. `PasswordEncoder` bean missing — SecurityConfig not loaded due to prior errors
5. `Bucket4j ClassNotFoundException` — Maven dependency version not downloaded

**Phase 5 Overall:** Backend 95% complete, Frontend 10% complete, Overall ~50%

### Database Migration Alignment
Current Flyway version: V9. Schema version detected: V13. Run pending migrations sequentially.

---

## 4. Nginx & Docker Issues

### SockJS WebSocket 403
**Root cause:** Nginx root `/` location matched before regex `~ ^/[0-9]+/` for SockJS session routes.
**Fix (nginx-simple.conf):**
- Added `map $uri $is_sockjs` directive for SockJS path detection
- Added `/info` endpoint for SockJS handshake
- Changed regex from `~ ^/[0-9]+/` to `~ "^/[0-9]{3}/"` (3-digit session IDs)
- Simplified root location to avoid prefix-catch-all

### Cohort Data Structure
Sprint list receives cohort data that may be JSON string, array, or single value. Frontend handles all three formats.

### Employee Cohort Values
Cohorts like "DC1" or "PC1" need to be normalized to "C1":
```sql
UPDATE employees SET cohort = 'C1' WHERE cohort IN ('DC1', 'PC1');
```

### Docker Build Args
Environment variables in `.env` override build args. To use build args, ensure they're not duplicated in `.env`.

---

## 5. Local Docker Setup

**Backend (8081), MySQL (3307), Frontend (80)**

Login credentials:
- Trainer: `vikram.singh@sprintflow.com` / `password123`
- HR: `meena.iyer@sprintflow.com` / `password123`
- Manager: `surya.prakash@sprintflow.com` / `password123`

Known limitations:
- SockJS may still show connection attempts (falls back to polling)
- Only 3 technologies seeded (Java, Python, Devops)
- Docker volumes for data persistence

---

## 6. Repository Cleanup

- **Frontend:** 35+ files removed (27 documentation .md files, temporary/config files)
- **Backend:** 38+ files removed (26 documentation .md files, 10 SQL files consolidated, temporary files)
- **Retained:** `src/`, `docker/`, `pom.xml`, `MASTER_SEED.sql`, `schema.sql`, `db/migration/` (V1-V13), `docker-compose.yml`, `docker-compose.local.yml`, `mvnw`
- **Environment:** Frontend `.env` — `VITE_USE_MOCK=false`, `VITE_API_BASE_URL=http://localhost:8080/api`

---

*Generated from consolidated bug fix documentation. Last updated: 2026-05-08.*
