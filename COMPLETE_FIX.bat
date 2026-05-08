@echo off
echo ========================================
echo SprintFlow Complete Fix Script
echo ========================================
echo.
echo This script will:
echo 1. Stop all Java processes
echo 2. Clean all build artifacts
echo 3. Delete all caches
echo 4. Rebuild from scratch
echo 5. Start the application
echo.
pause

echo.
echo [1/8] Stopping Java processes...
taskkill /F /IM java.exe 2>nul
taskkill /F /IM javaw.exe 2>nul
timeout /t 3 >nul

echo.
echo [2/8] Cleaning Maven target directory...
if exist target rmdir /s /q target
if exist .metadata\.plugins\org.springframework.ide.eclipse.boot rmdir /s /q .metadata\.plugins\org.springframework.ide.eclipse.boot

echo.
echo [3/8] Deleting Eclipse/STS caches...
if exist .settings rmdir /s /q .settings
if exist .classpath del /f /q .classpath
if exist .factorypath del /f /q .factorypath
if exist .project del /f /q .project

echo.
echo [4/8] Recreating Eclipse project files...
call mvnw eclipse:clean
call mvnw eclipse:eclipse

echo.
echo [5/8] Cleaning Maven cache for problematic dependencies...
if exist "%USERPROFILE%\.m2\repository\com\bucket4j" rmdir /s /q "%USERPROFILE%\.m2\repository\com\bucket4j"
if exist "%USERPROFILE%\.m2\repository\io\github\bucket4j" rmdir /s /q "%USERPROFILE%\.m2\repository\io\github\bucket4j"

echo.
echo [6/8] Running Maven clean...
call mvnw clean
if errorlevel 1 (
    echo ERROR: Maven clean failed
    pause
    exit /b 1
)

echo.
echo [7/8] Compiling and packaging (this may take a few minutes)...
call mvnw clean package -DskipTests -U
if errorlevel 1 (
    echo ERROR: Build failed
    echo.
    echo Checking for compilation errors...
    call mvnw compile
    pause
    exit /b 1
)

echo.
echo [8/8] Starting application...
echo.
echo ========================================
echo Build successful!
echo Starting server on http://localhost:8080
echo ========================================
echo.
echo Press Ctrl+C to stop the server
echo.

java -jar target\sprintflow-0.0.1-SNAPSHOT.jar

pause
