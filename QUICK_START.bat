@echo off
echo ========================================
echo SprintFlow Quick Start (Bucket4j Fixed)
echo ========================================
echo.

echo [1/4] Stopping any running instances...
taskkill /F /IM java.exe 2>nul
timeout /t 2 >nul

echo.
echo [2/4] Cleaning build...
call mvnw clean

echo.
echo [3/4] Building without tests...
call mvnw package -DskipTests
if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo [4/4] Starting application...
echo.
echo ========================================
echo Server starting on http://localhost:8080
echo ========================================
echo.

java -jar target\sprintflow-0.0.1-SNAPSHOT.jar

pause
