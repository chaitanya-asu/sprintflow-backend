# SprintFlow - Quick Reference Guide

## 🚀 Quick Start

### Clone & Setup (5 minutes)

**Frontend:**
```bash
git clone https://github.com/chaitanya-asu/sprintflow-frontend.git
cd sprintflow-frontend
git checkout main
git pull origin main
npm install
npm run dev
# Open http://localhost:5173
```

**Backend:**
```bash
git clone https://github.com/chaitanya-asu/sprintflow-backend.git
cd sprintflow-backend
git checkout main
git pull origin main
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

**Database:**
```bash
mysql -u root -p
CREATE DATABASE sprintflow_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sprintflow_db;
SOURCE schema.sql;
SOURCE MASTER_SEED.sql;
```

---

## 📊 Data Summary

| Entity | Count | Details |
|--------|-------|---------|
| Users | 5 | 2 Managers, 2 HR, 1 Trainer |
| Rooms | 9 | Training rooms A-D + 5 additional |
| Employees | 73 | 5 technologies, 6 cohorts |
| Sprints | 5 | One per technology |
| Attendance | 73 | Sample records for all employees |

---

## 🔐 Login Credentials

**Default Password:** Admin@123

| Role | Email | Name |
|------|-------|------|
| Manager | surya@sprintflow.com | Surya Prakash |
| Manager | a.pasam@ajacs.in | Aswini Pasam |
| HR | s.lakkampally@ajacs.in | Satwika |
| HR | nikitha@ajacs.in | Nikitha |
| Trainer | s.posanapally@ajacs.in | Surya Posanapally |

---

## 📁 Project Structure

### Frontend (`src/`)
```
├── components/          # Shared UI components
├── context/             # State management (Auth, AppData, etc.)
├── features/
│   ├── trainer/         # Trainer module
│   ├── hr/              # HR module
│   ├── manager/         # Manager module
│   └── sprint/          # Sprint management
├── pages/               # Shared pages (Login, Chat, Profile)
├── routes/              # Route configuration
├── services/            # API services
├── theme/               # Role-specific themes
└── utils/               # Helper functions
```

### Backend (`src/main/java/com/sprintflow/`)
```
├── config/              # Spring configuration
├── controller/          # REST endpoints
├── dto/                 # Data transfer objects
├── entity/              # JPA entities
├── repository/          # Data access
├── service/             # Business logic
├── security/            # JWT & security
└── validation/          # Custom validators
```

---

## 🎨 Themes

| Role | Colors | Sidebar |
|------|--------|---------|
| Trainer | Teal gradient | #0d4f4a → #14b8a6 |
| HR | Rose/Red gradient | #7a1d2e → #D45769 |
| Manager | Dark navy + Orange | #1a1a2e + #f97316 |

---

## 📚 Key Files

### Frontend
- `src/routes/AppRoutes.jsx` - All routes
- `src/context/AppDataContext.jsx` - Global data
- `src/services/api.js` - API base + JWT
- `src/theme/trainer.js` - Trainer theme

### Backend
- `src/main/resources/db/migration/` - Flyway migrations
- `src/main/java/com/sprintflow/config/SecurityConfig.java` - JWT config
- `src/main/java/com/sprintflow/service/AttendanceService.java` - Attendance logic
- `MASTER_SEED.sql` - Complete seed data

---

## 🔗 API Endpoints

### Authentication
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/forgot-password` - Forgot password
- `POST /api/auth/reset-password` - Reset password

### Sprints
- `GET /api/sprints` - List all sprints
- `GET /api/sprints/{id}` - Get sprint details
- `POST /api/sprints` - Create sprint
- `PUT /api/sprints/{id}` - Update sprint
- `DELETE /api/sprints/{id}` - Delete sprint

### Attendance
- `GET /api/attendance/sprint/{sprintId}` - Get sprint attendance
- `POST /api/attendance` - Mark attendance
- `PUT /api/attendance/{id}` - Update attendance

### Employees
- `GET /api/employees` - List employees
- `GET /api/employees/{id}` - Get employee
- `POST /api/employees` - Create employee
- `PUT /api/employees/{id}` - Update employee

### Rooms
- `GET /api/rooms` - List rooms
- `GET /api/rooms/availability` - Check availability

---

## 🐛 Common Issues & Fixes

### Frontend

**Issue:** Old UI showing
```bash
git pull origin main
npm install
npm run dev
# Clear browser cache: Ctrl+Shift+Delete
# Hard refresh: Ctrl+Shift+R
```

**Issue:** Port 5173 in use
```bash
npm run dev -- --port 5174
```

**Issue:** Module not found
```bash
rm -rf node_modules package-lock.json
npm install
```

### Backend

**Issue:** Database connection fails
```bash
# Check MySQL is running
# Check .env has correct credentials
# Check database exists: CREATE DATABASE sprintflow_db;
```

**Issue:** Migrations fail
```bash
# Check schema.sql was run first
# Check MASTER_SEED.sql syntax
# Check MySQL version (5.7+)
```

**Issue:** Port 8080 in use
```bash
# Change in application.properties
server.port=8081
```

### Database

**Issue:** Cohort constraint violation
```sql
-- Ensure cohort format is C1, C2, C3, etc.
SELECT DISTINCT cohort FROM employees;
```

**Issue:** Foreign key constraint fails
```sql
-- Check users exist before creating sprints
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM sprints;
```

---

## 📝 Environment Variables

### Frontend (`.env`)
```env
VITE_USE_MOCK=false
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=http://localhost:8080
```

### Backend (`application.properties`)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sprintflow_db
spring.datasource.username=root
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

---

## 🧪 Testing

### Frontend
```bash
npm run test              # Unit tests (Vitest)
npm run test:watch       # Watch mode
npm run e2e              # E2E tests (Playwright)
```

### Backend
```bash
mvn test                 # Run tests
mvn test -Dtest=ClassName  # Run specific test
```

---

## 📦 Build & Deploy

### Frontend
```bash
npm run build            # Production build
npm run preview          # Preview build locally
docker build -t sprintflow-frontend .
```

### Backend
```bash
mvn clean package        # Build JAR
mvn spring-boot:run      # Run locally
docker build -t sprintflow-backend .
```

---

## 📖 Documentation

| Document | Purpose |
|----------|---------|
| README.md | Project overview |
| DATABASE_SETUP_GUIDE.md | Database setup |
| COMPLETE_CHANGES_SUMMARY.md | All changes made |
| TROUBLESHOOTING_GUIDE.md | Common issues |
| FINAL_SETUP_GUIDE.md | Complete setup |
| README_FOR_TEAMMATES.md | Quick start |

---

## 🔄 Git Workflow

### Pull Latest
```bash
git checkout main
git pull origin main
```

### Create Feature Branch
```bash
git checkout -b feature/your-feature
# Make changes
git add .
git commit -m "feat: description"
git push origin feature/your-feature
# Create PR on GitHub
```

### Merge to Main
```bash
git checkout main
git pull origin main
git merge feature/your-feature
git push origin main
```

---

## 📞 Support

1. Check documentation files
2. Review git commit history
3. Check browser console (F12)
4. Check backend logs
5. Run verification queries

---

## ✅ Verification Checklist

- [ ] Frontend running on http://localhost:5173
- [ ] Backend running on http://localhost:8080
- [ ] Database has 73 employees
- [ ] Can login with credentials
- [ ] Trainer dashboard shows charts
- [ ] Attendance page loads data
- [ ] No console errors
- [ ] No database errors

---

**Last Updated:** 2026-05-05
**Version:** 1.0
**Status:** ✅ Production Ready
