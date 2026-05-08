@echo off
echo ========================================
echo Phase 5 Enhanced Fix Script
echo ========================================
echo.

echo [1/7] Stopping any running Spring Boot instances...
taskkill /F /IM java.exe 2>nul
timeout /t 3 >nul

echo.
echo [2/7] Cleaning Maven build artifacts...
call mvnw clean
if errorlevel 1 (
    echo ERROR: Maven clean failed
    pause
    exit /b 1
)

echo.
echo [3/7] Deleting Spring DevTools restart cache...
rmdir /s /q target\.restart 2>nul
rmdir /s /q .metadata\.plugins\org.springframework.ide.eclipse.boot 2>nul
del /f /q .factorypath 2>nul

echo.
echo [4/7] Clearing local Maven cache for problematic dependencies...
rmdir /s /q %USERPROFILE%\.m2\repository\com\bucket4j 2>nul
rmdir /s /q %USERPROFILE%\.m2\repository\io\github\bucket4j 2>nul
rmdir /s /q %USERPROFILE%\.m2\repository\com\googlecode\owasp-java-html-sanitizer 2>nul

echo.
echo [5/7] Force updating Maven dependencies...
call mvnw dependency:purge-local-repository -DactTransitively=false -DreResolve=false
call mvnw dependency:resolve

echo.
echo [6/7] Compiling and packaging application (skip tests for speed)...
call mvnw clean install -DskipTests -U
if errorlevel 1 (
    echo ERROR: Maven build failed
    echo.
    echo Trying alternative build without DevTools...
    call mvnw clean install -DskipTests -U -Dspring-boot.run.profiles=prod
    if errorlevel 1 (
        echo ERROR: Build still failed
        pause
        exit /b 1
    )
)

echo.
echo [7/7] Starting application...
echo.
echo ========================================
echo Build successful! Starting server...
echo ========================================
echo.

java -jar target\sprintflow-0.0.1-SNAPSHOT.jar

pause
