@echo off
echo ========================================
echo Task Management Setup Script
echo ========================================
echo.

echo Step 1: Checking MySQL connection...
mysql -u root -p -e "SELECT 1;" 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Cannot connect to MySQL. Please ensure MySQL is running.
    pause
    exit /b 1
)
echo MySQL connection OK!
echo.

echo Step 2: Running database migration...
echo Please enter your MySQL root password when prompted.
mysql -u root -p sprintflow < docker\init\06-tasks-schema.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to run database migration.
    pause
    exit /b 1
)
echo Database migration completed!
echo.

echo Step 3: Rebuilding Spring Boot application...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Maven build failed.
    pause
    exit /b 1
)
echo Build completed!
echo.

echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Start the backend: mvn spring-boot:run
echo 2. Start the frontend: npm run dev (in frontend directory)
echo 3. Navigate to http://localhost:5173/tasks
echo.
echo The Task Management feature is now ready to use!
echo.
pause
