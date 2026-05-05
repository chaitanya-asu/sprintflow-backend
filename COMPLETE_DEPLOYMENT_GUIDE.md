# 🚀 SprintFlow v2.0 - Complete Deployment Guide

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Quick Start](#quick-start)
3. [Configuration](#configuration)
4. [Deployment](#deployment)
5. [Verification](#verification)
6. [Troubleshooting](#troubleshooting)
7. [Production Deployment](#production-deployment)
8. [Monitoring & Maintenance](#monitoring--maintenance)

---

## Prerequisites

### System Requirements
- **Docker**: 20.10+ 
- **Docker Compose**: 2.0+
- **Disk Space**: 10GB minimum
- **RAM**: 4GB minimum (8GB recommended)
- **CPU**: 2 cores minimum (4 cores recommended)

### Verify Installation
```bash
docker --version
docker-compose --version
```

### Required Ports
- **80**: Frontend (HTTP)
- **443**: Frontend (HTTPS) - optional
- **3306**: MySQL - internal only
- **8080**: Backend - internal only

---

## Quick Start

### 1. Clone Repositories

```bash
# Create project directory
mkdir sprintflow-deployment
cd sprintflow-deployment

# Clone backend
git clone -b main https://github.com/chaitanya-asu/sprintflow-backend.git backend

# Clone frontend
git clone -b main https://github.com/chaitanya-asu/sprintflow-frontend.git frontend
```

### 2. Configure Environment

```bash
cd backend

# Copy environment template
cp .env.example .env

# Edit configuration
nano .env  # or use your preferred editor
```

### 3. Generate Secrets

```bash
# Generate JWT Secret (64 hex characters)
openssl rand -hex 32

# Generate Mail Key (32 hex characters)
openssl rand -hex 16

# Add to .env file
```

### 4. Deploy

```bash
# Build and start all services
docker-compose up -d --build

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### 5. Access Application

- **Frontend**: http://localhost
- **API Health**: http://localhost/api/health
- **Swagger UI**: http://localhost/api/swagger-ui.html (if enabled)

---

## Configuration

### Environment Variables

#### Required Variables
```env
# JWT Secret (REQUIRED - 64 hex characters)
APP_JWT_SECRET=<generate with: openssl rand -hex 32>

# Mail Encryption Key (REQUIRED - 32 hex characters)
APP_MAIL_KEY=<generate with: openssl rand -hex 16>

# Database
DB_USERNAME=sprintflow
DB_PASSWORD=<secure password>
MYSQL_ROOT_PASSWORD=<secure password>

# Frontend URLs
APP_FRONTEND_URL=http://localhost
APP_CORS_ORIGINS=http://localhost,http://localhost:80

# Vite Build Arguments
VITE_API_BASE_URL=/api
VITE_WS_URL=http://localhost
```

#### Optional Variables
```env
# Database
DDL_AUTO=update  # update, validate, create, create-drop
MYSQL_PORT=3306

# Frontend
FRONTEND_PORT=80
FRONTEND_HTTPS_PORT=443
FRONTEND_PATH=../sprintflow-frontend

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_SPRINTFLOW=INFO

# Swagger
SWAGGER_ENABLED=false  # true for development, false for production

# MySQL Data Path
MYSQL_DATA_PATH=./data/mysql
```

### .env.example Template

```env
# ── Database ──────────────────────────────────────────────────
MYSQL_ROOT_PASSWORD=ChangeThisRootPassword123!
DB_USERNAME=sprintflow
DB_PASSWORD=ChangeThisDbPassword123!
MYSQL_PORT=3306

# ── Security ──────────────────────────────────────────────────
APP_JWT_SECRET=REPLACE_WITH_64_HEX_CHARACTERS
APP_MAIL_KEY=REPLACE_WITH_32_HEX_CHARACTERS

# ── URLs ──────────────────────────────────────────────────────
APP_FRONTEND_URL=http://localhost
APP_CORS_ORIGINS=http://localhost,http://localhost:80
VITE_API_BASE_URL=/api
VITE_WS_URL=http://localhost

# ── Deployment ────────────────────────────────────────────────
FRONTEND_PORT=80
FRONTEND_HTTPS_PORT=443
FRONTEND_PATH=../sprintflow-frontend
MYSQL_DATA_PATH=./data/mysql

# ── Database Configuration ────────────────────────────────────
DDL_AUTO=update

# ── Logging ───────────────────────────────────────────────────
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_SPRINTFLOW=INFO

# ── Features ──────────────────────────────────────────────────
SWAGGER_ENABLED=false
```

---

## Deployment

### Development Deployment

```bash
# Start services
docker-compose up -d --build

# View logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql

# Stop services
docker-compose down

# Stop and remove volumes (WARNING: deletes data)
docker-compose down -v
```

### Production Deployment

```bash
# Set production environment
export ENVIRONMENT=production

# Build images
docker-compose build --no-cache

# Start with production settings
docker-compose up -d

# Verify all services
docker-compose ps
```

### Scaling

```bash
# Scale backend service (if using multiple instances)
docker-compose up -d --scale backend=3

# Note: Frontend and MySQL should not be scaled in this setup
```

---

## Verification

### Health Checks

```bash
# Frontend health
curl http://localhost/health

# Backend health
curl http://localhost/api/health

# Database connection
docker-compose exec mysql mysqladmin ping -u sprintflow -psprintflow123
```

### Container Status

```bash
# Check all containers
docker-compose ps

# View container logs
docker-compose logs <service-name>

# Inspect container
docker inspect sprintflow-frontend
```

### Database Verification

```bash
# Connect to MySQL
docker-compose exec mysql mysql -u sprintflow -psprintflow123 sprintflow_db

# Check tables
SHOW TABLES;

# Check data
SELECT COUNT(*) FROM sprints;
SELECT COUNT(*) FROM users;
```

---

## Troubleshooting

### Common Issues

#### 1. Port Already in Use
```bash
# Find process using port 80
lsof -i :80

# Kill process
kill -9 <PID>

# Or use different port in .env
FRONTEND_PORT=8000
```

#### 2. Database Connection Failed
```bash
# Check MySQL logs
docker-compose logs mysql

# Verify MySQL is healthy
docker-compose ps mysql

# Restart MySQL
docker-compose restart mysql
```

#### 3. Frontend Not Loading
```bash
# Check Nginx logs
docker-compose logs frontend

# Verify Nginx configuration
docker-compose exec frontend nginx -t

# Restart frontend
docker-compose restart frontend
```

#### 4. Backend API Not Responding
```bash
# Check backend logs
docker-compose logs backend

# Verify backend health
curl http://localhost/api/health

# Restart backend
docker-compose restart backend
```

#### 5. Out of Memory
```bash
# Check resource usage
docker stats

# Increase Docker memory limit
# Edit Docker Desktop settings or docker daemon.json

# Reduce container limits in docker-compose.yml
```

### Debug Mode

```bash
# Enable debug logging
LOGGING_LEVEL_COM_SPRINTFLOW=DEBUG docker-compose up

# View detailed logs
docker-compose logs --tail=100 -f backend

# Inspect network
docker network inspect sprintflow-net
```

---

## Production Deployment

### Pre-Production Checklist

- [ ] Generate strong JWT secret
- [ ] Generate strong mail encryption key
- [ ] Set secure database passwords
- [ ] Configure SSL/TLS certificates
- [ ] Set SWAGGER_ENABLED=false
- [ ] Set DDL_AUTO=validate
- [ ] Configure backup strategy
- [ ] Set up monitoring
- [ ] Configure logging aggregation
- [ ] Test disaster recovery

### SSL/TLS Configuration

```bash
# Generate self-signed certificate (development only)
./generate-ssl.bat  # Windows
./generate-ssl.sh   # Linux/Mac

# For production, use Let's Encrypt
# Update nginx-simple.conf with SSL configuration
```

### Backup Strategy

```bash
# Backup database
docker-compose exec mysql mysqldump -u sprintflow -psprintflow123 sprintflow_db > backup.sql

# Backup volumes
docker run --rm -v sprintflow-mysql_data:/data -v $(pwd):/backup \
  alpine tar czf /backup/mysql-backup.tar.gz /data

# Restore database
docker-compose exec -T mysql mysql -u sprintflow -psprintflow123 sprintflow_db < backup.sql
```

---

## Monitoring & Maintenance

### Container Monitoring

```bash
# Real-time resource usage
docker stats

# Container logs
docker-compose logs -f --tail=50

# Event monitoring
docker events --filter type=container
```

### Database Maintenance

```bash
# Optimize tables
docker-compose exec mysql mysql -u sprintflow -psprintflow123 sprintflow_db \
  -e "OPTIMIZE TABLE sprints, users, employees, attendance;"

# Check table status
docker-compose exec mysql mysql -u sprintflow -psprintflow123 sprintflow_db \
  -e "SHOW TABLE STATUS;"
```

### Log Rotation

```bash
# Configure Docker log rotation
# Edit /etc/docker/daemon.json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

### Updates

```bash
# Pull latest images
docker-compose pull

# Rebuild with latest code
docker-compose build --no-cache

# Restart services
docker-compose up -d
```

---

## Support & Documentation

- **GitHub Issues**: https://github.com/chaitanya-asu/sprintflow-backend/issues
- **API Documentation**: http://localhost/api/swagger-ui.html
- **Architecture**: See DEPLOYMENT_GUIDE.md
- **Troubleshooting**: See this guide's Troubleshooting section

---

## Version Information

- **SprintFlow Version**: 2.0.0
- **React**: 19.2.4
- **Spring Boot**: 3.2.5
- **Java**: 21
- **MySQL**: 8.0
- **Nginx**: 1.27-Alpine
- **Node**: 20-Alpine

---

**Last Updated**: 2024
**Maintained By**: SprintFlow Team