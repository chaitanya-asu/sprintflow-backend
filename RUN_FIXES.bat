@echo off
REM ============================================================================
REM SprintFlow - Automated Fix Execution Script
REM ============================================================================
REM This script will:
REM 1. Backup current database
REM 2. Load clean seed data
REM 3. Verify data
REM 4. Rebuild project
REM 5. Start application
REM ============================================================================

setlocal enabledelayedexpansion
set TIMESTAMP=%date:~-4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set BACKUP_FILE=backup_%TIMESTAMP%.sql
set PROJECT_DIR=%cd%

echo.
echo ============================================================================
echo                    SprintFlow - Fix Execution Script
echo ============================================================================
echo.
echo Project Directory: %PROJECT_DIR%
echo Backup File: %BACKUP_FILE%
echo.

REM ============================================================================
REM STEP 1: Backup Current Database
REM ============================================================================
echo [STEP 1] Backing up current database...
echo.

set /p MYSQL_USER="Enter MySQL username (default: root): "
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p MYSQL_PASS="Enter MySQL password: "

if "%MYSQL_PASS%"=="" (
    echo Backing up without password...
    mysqldump -u %MYSQL_USER% sprintflow_db > %BACKUP_FILE%
) else (
    echo Backing up with password...
    mysqldump -u %MYSQL_USER% -p%MYSQL_PASS% sprintflow_db > %BACKUP_FILE%
)

if %ERRORLEVEL% EQU 0 (
    echo ✓ Backup created: %BACKUP_FILE%
) else (
    echo ✗ Backup failed!
    pause
    exit /b 1
)

echo.

REM ============================================================================
REM STEP 2: Load Clean Seed Data
REM ============================================================================
echo [STEP 2] Loading clean seed data...
echo.

if "%MYSQL_PASS%"=="" (
    mysql -u %MYSQL_USER% sprintflow_db < CLEAN_SEED.sql
) else (
    mysql -u %MYSQL_USER% -p%MYSQL_PASS% sprintflow_db < CLEAN_SEED.sql
)

if %ERRORLEVEL% EQU 0 (
    echo ✓ Seed data loaded successfully
) else (
    echo ✗ Seed data loading failed!
    pause
    exit /b 1
)

echo.

REM ============================================================================
REM STEP 3: Verify Data
REM ============================================================================
echo [STEP 3] Verifying data...
echo.

if "%MYSQL_PASS%"=="" (
    mysql -u %MYSQL_USER% sprintflow_db -e "SELECT 'Users' AS table_name, COUNT(*) AS count FROM users UNION ALL SELECT 'Employees', COUNT(*) FROM employees UNION ALL SELECT 'Sprints', COUNT(*) FROM sprints UNION ALL SELECT 'Rooms', COUNT(*) FROM rooms;"
) else (
    mysql -u %MYSQL_USER% -p%MYSQL_PASS% sprintflow_db -e "SELECT 'Users' AS table_name, COUNT(*) AS count FROM users UNION ALL SELECT 'Employees', COUNT(*) FROM employees UNION ALL SELECT 'Sprints', COUNT(*) FROM sprints UNION ALL SELECT 'Rooms', COUNT(*) FROM rooms;"
)

echo.
echo ✓ Data verification complete
echo.

REM ============================================================================
REM STEP 4: Rebuild Project
REM ============================================================================
echo [STEP 4] Rebuilding project...
echo.

call mvn clean install -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo ✓ Project rebuilt successfully
) else (
    echo ✗ Project rebuild failed!
    pause
    exit /b 1
)

echo.

REM ============================================================================
REM STEP 5: Start Application
REM ============================================================================
echo [STEP 5] Starting application...
echo.
echo Application will start in a new window...
echo.

start cmd /k "mvn spring-boot:run"

echo.
echo ============================================================================
echo                         ✓ EXECUTION COMPLETE
echo ============================================================================
echo.
echo Application is starting. Please wait for the message:
echo   "Started SprintFlowApplication"
echo.
echo Then test with:
echo   curl http://localhost:8080/api/employees
echo.
echo Default credentials:
echo   Email: s.lakkampally@ajacs.in
echo   Password: Admin@123
echo.
echo ============================================================================
echo.

pause
