@echo off
echo ========================================
echo IMMEDIATE FIX - Stopping and Rebuilding
echo ========================================
echo.

echo [1/5] Killing all Java processes...
taskkill /F /IM java.exe 2>nul
taskkill /F /IM javaw.exe 2>nul
timeout /t 2 >nul

echo.
echo [2/5] Deleting target directory...
if exist target (
    rmdir /s /q target
    echo Target deleted
) else (
    echo Target already clean
)

echo.
echo [3/5] Deleting DevTools cache...
if exist .metadata\.plugins\org.springframework.ide.eclipse.boot (
    rmdir /s /q .metadata\.plugins\org.springframework.ide.eclipse.boot
    echo DevTools cache deleted
)

echo.
echo [4/5] Building application (this will take 1-2 minutes)...
call mvnw clean compile
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    echo Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo [5/5] Packaging application...
call mvnw package -DskipTests
if errorlevel 1 (
    echo.
    echo ERROR: Packaging failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! Application built successfully
echo ========================================
echo.
echo Starting server now...
echo Press Ctrl+C to stop
echo.

java -jar target\sprintflow-0.0.1-SNAPSHOT.jar

pause
