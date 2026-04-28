# 📚 SprintFlow Backend Master Reference

This document serves as the single source of truth for the SprintFlow backend infrastructure, API, and deployment.

---

## 🚀 Quick Start
1. **Start Backend**: `mvn spring-boot:run`
2. **Health Check**: `http://localhost:8080/api/health`
3. **Database**: MySQL 8.0+ (Configuration in `src/main/resources/application.properties`)

---

## 🔐 Security & Auth
- **JWT**: Token-based authentication (24h expiry).
- **Roles**: `ADMIN`, `HR`, `MANAGER`, `TRAINER`, `EMPLOYEE`.
- **Encryption**: BCrypt for passwords, AES for sensitive mail data.

---

## 📋 API Endpoints Summary

### Authentication (`/api/auth`)
- `POST /login`: Get JWT token.
- `POST /register`: Create new user.
- `GET /me`: Get current user profile.

### Messaging (`/api/messages`)
- `GET /history?with={email}`: Direct message history.
- `GET /contacts`: Recent chat contacts.
- `GET /search?q={query}`: Search users for new chats.
- `POST /groups`: Create a new chat group.
- `GET /groups`: List my groups.
- `GET /groups/{id}/history`: Group chat history.

### Sprints (`/api/sprints`)
- `GET /all`: List all sprints.
- `POST /create`: Create a new sprint (HR only).
- `GET /rooms/availability`: Get room utilization data (Calendar view).

### Attendance (`/api/attendance`)
- `POST /mark`: Mark daily attendance.
- `GET /history/{userId}`: View user attendance history.

---

## 🛠️ Deployment Guide
### Environment Variables
- `APP_JWT_SECRET`: 64-char hex string.
- `APP_MAIL_KEY`: 32-char hex string.
- `DB_URL`, `DB_USER`, `DB_PASS`.

### Docker
- `docker-compose up -d` to spin up MySQL and Spring Boot app.

---

## 📝 Change Log & Notes
- **Group Messaging**: Implemented with `ChatGroup` entity and STOMP topics `/topic/group.{id}`.
- **Room Calendar**: Mapped via `SprintService.getRoomAvailability`.

---
*Note: All legacy documentation files (.md) have been consolidated into this master reference for clarity.*
