@echo off
echo ========================================
echo Phase 5 Issue Resolution Script
echo ========================================
echo.

echo [1/5] Stopping any running Spring Boot instances...
taskkill /F /IM java.exe 2>nul
timeout /t 2 >nul

echo.
echo [2/5] Cleaning Maven build artifacts...
call mvnw clean
if errorlevel 1 (
    echo ERROR: Maven clean failed
    pause
    exit /b 1
)

echo.
echo [3/5] Deleting Spring DevTools restart cache...
rmdir /s /q target\.restart 2>nul
rmdir /s /q .metadata\.plugins\org.springframework.ide.eclipse.boot 2>nul

echo.
echo [4/5] Compiling and packaging application...
call mvnw clean package -DskipTests
if errorlevel 1 (
    echo ERROR: Maven build failed
    pause
    exit /b 1
)

echo.
echo [5/5] Starting application...
echo.
echo ========================================
echo Build successful! Starting server...
echo ========================================
echo.

java -jar target\sprintflow-0.0.1-SNAPSHOT.jar

pause
