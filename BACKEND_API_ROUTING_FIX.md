# Backend API Routing Fix — SprintFlow

## Problem
The frontend was receiving `NoResourceFoundException` errors when calling `/api/cohorts` and `/api/rooms` endpoints:
```
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/cohorts.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/rooms.
```

**Root Cause:** The backend did not have `CohortController` and `RoomController` to handle these API endpoints. Spring was treating the requests as static resource requests instead of routing them to REST controllers.

---

## Solution

### 1. Created CohortController
**File:** `src/main/java/com/sprintflow/controller/CohortController.java`

- Provides `/api/cohorts` endpoints
- Returns static cohort data matching frontend configuration:
  - Java cohort 1 & 2
  - Python cohort 1 & 2
  - DevOps cohort 1
  - .NET cohort 1
  - Salesforce cohort 1
- Endpoints:
  - `GET /api/cohorts` — Get all cohorts
  - `GET /api/cohorts/{id}` — Get cohort by ID
  - `GET /api/cohorts/technology/{technology}` — Filter by technology

### 2. Created RoomController
**File:** `src/main/java/com/sprintflow/controller/RoomController.java`

- Provides `/api/rooms` endpoints
- Returns static room data:
  - Room A (Sandeepa)
  - Room B (Dhrona)
  - Room C (Brahma)
  - Room D (Maheshwara)
- Endpoints:
  - `GET /api/rooms` — Get all rooms
  - `GET /api/rooms/{id}` — Get room by ID
  - `GET /api/rooms/name/{name}` — Get room by name

### 3. Updated SecurityConfig
**File:** `src/main/java/com/sprintflow/config/SecurityConfig.java`

Added public access to the new endpoints:
```java
// Static reference data (cohorts, rooms)
.requestMatchers("/api/cohorts/**").permitAll()
.requestMatchers("/api/rooms/**").permitAll()
```

These endpoints don't require authentication since they return static reference data.

---

## Testing

### Before Fix
```
GET http://localhost:8080/api/cohorts
→ 500 ERROR: NoResourceFoundException

GET http://localhost:8080/api/rooms
→ 500 ERROR: NoResourceFoundException
```

### After Fix
```
GET http://localhost:8080/api/cohorts
→ 200 OK
{
  "success": true,
  "message": "Cohorts retrieved successfully",
  "data": [
    { "id": 1, "name": "Java cohort 1", "technology": "Java", "code": "JC1" },
    ...
  ],
  "statusCode": 200
}

GET http://localhost:8080/api/rooms
→ 200 OK
{
  "success": true,
  "message": "Rooms retrieved successfully",
  "data": [
    { "id": 1, "name": "Room A", "mentor": "Sandeepa", "code": "A" },
    ...
  ],
  "statusCode": 200
}
```

---

## Frontend Integration

The frontend services now work correctly:

**cohortService.js:**
```javascript
getAll() { return api.get("/cohorts"); }  // ✅ Now works
```

**roomService.js:**
```javascript
getAll() { return api.get("/rooms"); }  // ✅ Now works
```

---

## Deployment Notes

1. **Rebuild the backend:**
   ```bash
   mvn clean package
   ```

2. **Restart the Spring Boot application:**
   ```bash
   java -jar target/sprintflow-0.0.1-SNAPSHOT.jar
   ```

3. **Verify endpoints in Swagger UI:**
   - Navigate to `http://localhost:8080/swagger-ui.html`
   - Look for "Cohorts" and "Rooms" sections
   - Test endpoints directly from Swagger

4. **Frontend will now load cohorts and rooms without errors** when `VITE_USE_MOCK=false`

---

## Files Modified/Created

| File | Action | Purpose |
|------|--------|---------|
| `CohortController.java` | Created | Handle `/api/cohorts` endpoints |
| `RoomController.java` | Created | Handle `/api/rooms` endpoints |
| `SecurityConfig.java` | Updated | Allow public access to cohorts/rooms |

---

## Future Enhancements

If you need to persist cohorts and rooms in the database:

1. Create `Cohort` and `Room` JPA entities
2. Create corresponding repositories
3. Create services to manage CRUD operations
4. Update controllers to use services instead of static data
5. Add database migration scripts

For now, static data is sufficient since cohorts and rooms are reference data that rarely changes.
