@echo off
echo ========================================
echo   ECLIPSE IDE REFRESH SCRIPT
echo ========================================
echo.

cd /d "%~dp0"

echo [1/4] Cleaning Maven build...
call mvn clean

echo.
echo [2/4] Deleting Eclipse metadata...
if exist .classpath del /f /q .classpath
if exist .project del /f /q .project
if exist .settings rmdir /s /q .settings

echo.
echo [3/4] Regenerating Eclipse project files...
call mvn eclipse:clean eclipse:eclipse

echo.
echo [4/4] Compiling project...
call mvn compile

echo.
echo ========================================
echo   REFRESH COMPLETE!
echo ========================================
echo.
echo NEXT STEPS IN ECLIPSE:
echo 1. Close Eclipse/STS completely
echo 2. Reopen Eclipse/STS
echo 3. Right-click project ^> Refresh (F5)
echo 4. Right-click project ^> Maven ^> Update Project
echo 5. Project ^> Clean... ^> Clean all projects
echo.
pause
