# SprintFlow Local Docker Development Guide

## Quick Start

### 1. Navigate to backend directory
```bash
cd C:\Users\Lenovo\Documents\workspace-spring-tools-for-eclipse-5.0.1.RELEASE\springboot\project\sprintflow
```

### 2. Start all 3 services
```bash
docker-local.bat start
```

Or manually:
```bash
docker compose -f docker-compose.local.yml up -d
```

### 3. Access the application
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **Swagger Docs**: http://localhost:8080/swagger-ui.html
- **MySQL**: localhost:3306 (user: sprintflow, password: sprintflow123)

---

## Service Details

### MySQL (Port 3306)
- **Container**: sprintflow-mysql-local
- **Database**: sprintflow_db
- **User**: sprintflow
- **Password**: sprintflow123
- **Data persists** in `mysql_data_local` volume

### Backend (Port 8080)
- **Container**: sprintflow-backend-local
- **Framework**: Spring Boot 3.2 + Java 21
- **Endpoints**:
  - API: http://localhost:8080/api
  - Swagger: http://localhost:8080/swagger-ui.html
  - Health: http://localhost:8080/actuator/health
- **Logs**: `docker logs sprintflow-backend-local`

### Frontend (Port 80)
- **Container**: sprintflow-frontend-local
- **Framework**: React 19 + Vite + Nginx
- **URL**: http://localhost
- **Nginx proxies**:
  - `/api/*` → backend:8080/api
  - `/ws/*` → backend:8080/ws (WebSocket)
  - `/` → React SPA

---

## Common Commands

### View logs
```bash
# All services
docker compose -f docker-compose.local.yml logs -f

# Specific service
docker logs -f sprintflow-backend-local
docker logs -f sprintflow-frontend-local
docker logs -f sprintflow-mysql-local
```

### Restart a service
```bash
docker compose -f docker-compose.local.yml restart backend
docker compose -f docker-compose.local.yml restart frontend
docker compose -f docker-compose.local.yml restart mysql
```

### Rebuild images
```bash
docker compose -f docker-compose.local.yml up -d --build
```

### Stop all services
```bash
docker compose -f docker-compose.local.yml down
```

### Remove everything (including database)
```bash
docker compose -f docker-compose.local.yml down -v
```

### Check service status
```bash
docker compose -f docker-compose.local.yml ps
```

---

## Troubleshooting

### Backend won't start
1. Check MySQL is healthy: `docker logs sprintflow-mysql-local`
2. Check backend logs: `docker logs sprintflow-backend-local`
3. Verify .env has correct JWT_SECRET and MAIL_KEY
4. Rebuild: `docker compose -f docker-compose.local.yml up -d --build`

### Frontend shows blank page
1. Check Nginx logs: `docker logs sprintflow-frontend-local`
2. Verify backend is running: `curl http://localhost:8080/api/health`
3. Check browser console for errors
4. Rebuild frontend: `docker compose -f docker-compose.local.yml up -d --build frontend`

### Port already in use
```bash
# Find what's using port 80
netstat -ano | findstr :80

# Find what's using port 8080
netstat -ano | findstr :8080

# Find what's using port 3306
netstat -ano | findstr :3306

# Kill process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

### Database connection issues
1. Verify MySQL is running: `docker ps | findstr mysql`
2. Check MySQL logs: `docker logs sprintflow-mysql-local`
3. Test connection: `docker exec sprintflow-mysql-local mysqladmin ping -u sprintflow -psprintflow123`

### WebSocket connection fails
1. Verify backend is running on port 8080
2. Check Nginx config is proxying `/ws` correctly
3. Check browser console for WebSocket errors
4. Verify `VITE_WS_URL=http://localhost` in frontend .env

---

## Environment Variables

### Backend (.env)
```env
APP_JWT_SECRET=1EDA0E87B0D5C78C8DC9E4D7BD8F2D7B73629119D9B75D908D081C580829E29F1
APP_MAIL_KEY=3999C9C5BF5106AA585228E24B1A550
DB_PASSWORD=sprintflow123
MYSQL_ROOT_PASSWORD=rootpassword
DDL_AUTO=update
SWAGGER_ENABLED=true
VITE_API_BASE_URL=http://localhost/api
VITE_WS_URL=http://localhost
FRONTEND_PATH=D:/React js/demo/sprintflow-frontend-main
```

### Frontend (.env)
```env
VITE_USE_MOCK=false
VITE_API_BASE_URL=http://localhost/api
VITE_WS_URL=http://localhost
```

---

## Testing the Setup

### 1. Check all services are running
```bash
docker compose -f docker-compose.local.yml ps
```

Expected output:
```
NAME                          STATUS
sprintflow-mysql-local        Up (healthy)
sprintflow-backend-local      Up (healthy)
sprintflow-frontend-local     Up
```

### 2. Test backend API
```bash
curl http://localhost:8080/api/health
```

### 3. Test frontend
Open http://localhost in browser

### 4. Test database
```bash
docker exec sprintflow-mysql-local mysql -u sprintflow -psprintflow123 sprintflow_db -e "SELECT COUNT(*) FROM users;"
```

---

## Performance Tips

- **Memory limits**: MySQL 768M, Backend 1024M, Frontend 256M (adjust in docker-compose.local.yml if needed)
- **Rebuild only changed services**: `docker compose -f docker-compose.local.yml up -d --build frontend`
- **Use named volumes**: Database persists across restarts
- **Check Docker Desktop resources**: Ensure Docker has enough CPU/RAM allocated

---

## Next Steps

1. **Verify all services are healthy**: `docker compose -f docker-compose.local.yml ps`
2. **Access frontend**: http://localhost
3. **Login with mock credentials**:
   - Trainer: Vikram Singh
   - HR: Meena Iyer
   - Manager: Surya Prakash
4. **Check backend logs**: `docker logs -f sprintflow-backend-local`
5. **Monitor all services**: `docker compose -f docker-compose.local.yml logs -f`
