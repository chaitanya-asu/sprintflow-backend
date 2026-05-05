# SprintFlow Local Docker Setup - Status & Quick Start

## ✅ What's Been Done

1. **Created optimized local docker-compose file** (`docker-compose.local.yml`)
   - MySQL on port 3307 (to avoid conflicts)
   - Backend on port 8081
   - Frontend on port 8000
   - All services configured with health checks and resource limits

2. **Updated environment files**
   - Backend `.env` configured for Docker
   - Frontend `.env` configured for Docker
   - All API URLs point to localhost with correct ports

3. **Created helper script** (`docker-local.bat`)
   - Easy commands to start/stop/restart/rebuild services
   - View logs, check status, clean up

4. **Created this guide** with troubleshooting and common commands

---

## 🚀 Quick Start

### Option 1: Using the batch script (Recommended)
```bash
cd C:\Users\Lenovo\Documents\workspace-spring-tools-for-eclipse-5.0.1.RELEASE\springboot\project\sprintflow

# Start all services
docker-local.bat start

# View logs
docker-local.bat logs

# Stop services
docker-local.bat stop
```

### Option 2: Using docker-compose directly
```bash
cd C:\Users\Lenovo\Documents\workspace-spring-tools-for-eclipse-5.0.1.RELEASE\springboot\project\sprintflow

# Start services
docker compose -f docker-compose.local.yml up -d

# View status
docker compose -f docker-compose.local.yml ps

# View logs
docker compose -f docker-compose.local.yml logs -f

# Stop services
docker compose -f docker-compose.local.yml down
```

---

## 📍 Access Points

Once all services are running:

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:8000 | React application |
| Backend API | http://localhost:8081/api | REST API endpoints |
| Swagger Docs | http://localhost:8081/swagger-ui.html | API documentation |
| MySQL | localhost:3307 | Database (user: sprintflow, pass: sprintflow123) |

---

## 🔧 Service Details

### MySQL (Port 3307)
- **Container**: sprintflow-mysql-local
- **Database**: sprintflow_db
- **User**: sprintflow
- **Password**: sprintflow123
- **Data**: Persists in `mysql_data_local` volume

### Backend (Port 8081)
- **Container**: sprintflow-backend-local
- **Framework**: Spring Boot 3.2 + Java 21
- **Status**: Check with `docker logs sprintflow-backend-local`
- **Restart policy**: Automatic on failure

### Frontend (Port 8000)
- **Container**: sprintflow-frontend-local
- **Framework**: React 19 + Vite + Nginx
- **Status**: Check with `docker logs sprintflow-frontend-local`

---

## 🐛 Troubleshooting

### Backend won't start
1. Check MySQL is healthy:
   ```bash
   docker logs sprintflow-mysql-local
   ```

2. Check backend logs for errors:
   ```bash
   docker logs sprintflow-backend-local
   ```

3. If database migration fails, reset everything:
   ```bash
   docker compose -f docker-compose.local.yml down -v
   docker compose -f docker-compose.local.yml up -d
   ```

### Frontend shows blank page
1. Check Nginx logs:
   ```bash
   docker logs sprintflow-frontend-local
   ```

2. Verify backend is running:
   ```bash
   curl http://localhost:8081/api/health
   ```

3. Check browser console for errors (F12)

### Port conflicts
If ports are already in use, edit `docker-compose.local.yml`:
- Change `3307:3306` to `3308:3306` for MySQL
- Change `8081:8080` to `8082:8080` for Backend
- Change `8000:80` to `8001:80` for Frontend

Then update `.env` files accordingly.

### Database issues
To completely reset the database:
```bash
docker compose -f docker-compose.local.yml down -v
docker compose -f docker-compose.local.yml up -d
```

This removes all data and recreates fresh database from migrations.

---

## 📊 Monitoring

### View all logs
```bash
docker compose -f docker-compose.local.yml logs -f
```

### View specific service logs
```bash
docker logs -f sprintflow-backend-local
docker logs -f sprintflow-frontend-local
docker logs -f sprintflow-mysql-local
```

### Check resource usage
```bash
docker stats
```

### Inspect container
```bash
docker inspect sprintflow-backend-local
```

---

## 🔄 Common Workflows

### Rebuild after code changes
```bash
# Rebuild and restart all services
docker compose -f docker-compose.local.yml up -d --build

# Or rebuild just one service
docker compose -f docker-compose.local.yml up -d --build backend
docker compose -f docker-compose.local.yml up -d --build frontend
```

### View database
```bash
docker exec -it sprintflow-mysql-local mysql -u sprintflow -psprintflow123 sprintflow_db
```

### Execute command in container
```bash
docker exec sprintflow-backend-local ls -la /app
docker exec sprintflow-frontend-local ls -la /usr/share/nginx/html
```

---

## 📝 Environment Variables

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

## ✨ Next Steps

1. **Start services**:
   ```bash
   docker-local.bat start
   ```

2. **Wait for backend to be healthy** (check logs):
   ```bash
   docker logs -f sprintflow-backend-local
   ```

3. **Open frontend**:
   - http://localhost:8000

4. **Login with mock credentials**:
   - Trainer: Vikram Singh
   - HR: Meena Iyer
   - Manager: Surya Prakash

5. **Test API**:
   - http://localhost:8081/swagger-ui.html

---

## 📚 Files Created/Modified

- ✅ `docker-compose.local.yml` - Local dev compose file
- ✅ `docker-local.bat` - Helper script
- ✅ `.env` - Backend environment variables
- ✅ `d:\React js\demo\sprintflow-frontend-main\.env` - Frontend environment variables
- ✅ `LOCAL_DOCKER_GUIDE.md` - This guide

---

## 🆘 Need Help?

1. Check logs: `docker compose -f docker-compose.local.yml logs -f`
2. Verify all services running: `docker compose -f docker-compose.local.yml ps`
3. Reset everything: `docker compose -f docker-compose.local.yml down -v && docker compose -f docker-compose.local.yml up -d`
4. Check port conflicts: `netstat -ano | findstr :8000` (or 8081, 3307)

---

## 🎯 Current Status

**Services**: All 3 containers created and starting
- MySQL: ✅ Running (healthy)
- Backend: ⏳ Starting (may take 30-60 seconds)
- Frontend: ✅ Running

**Next**: Wait for backend to fully start, then access http://localhost:8000
