@echo off
echo Cleaning and rebuilding the application...
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)
echo Build successful!
echo.
echo Please restart your Spring Boot application now.
pause
