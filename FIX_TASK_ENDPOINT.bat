@echo off
echo ========================================
echo   FIXING TASK ENDPOINT ISSUE
echo ========================================
echo.

echo Step 1: Stopping any running Spring Boot instances...
echo Please manually stop your Spring Boot application if it's running in Eclipse/STS
echo Press any key once you've stopped the application...
pause > nul

echo.
echo Step 2: Cleaning build artifacts...
call mvnw.cmd clean
if %ERRORLEVEL% NEQ 0 (
    echo Clean failed!
    pause
    exit /b 1
)

echo.
echo Step 3: Compiling and packaging...
call mvnw.cmd package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo   BUILD SUCCESSFUL!
echo ========================================
echo.
echo Next steps:
echo 1. Start your Spring Boot application in Eclipse/STS
echo 2. Wait for it to fully start (check console for "Started" message)
echo 3. Test the endpoint: GET http://localhost:8080/api/tasks/my-tasks
echo.
echo The error should now be resolved!
echo.
pause
