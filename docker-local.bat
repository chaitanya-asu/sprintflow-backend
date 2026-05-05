@echo off
REM SprintFlow Local Docker Development Script
REM Usage: docker-local.bat [start|stop|restart|rebuild|logs|clean]

setlocal enabledelayedexpansion

if "%1"=="" (
    echo.
    echo SprintFlow Local Docker Development
    echo ====================================
    echo.
    echo Usage: docker-local.bat [command]
    echo.
    echo Commands:
    echo   start      - Start all 3 services (MySQL, Backend, Frontend^)
    echo   stop       - Stop all services
    echo   restart    - Restart all services
    echo   rebuild    - Rebuild images and start services
    echo   logs       - Show live logs from all services
    echo   clean      - Stop services and remove volumes (WARNING: deletes DB data^)
    echo   status     - Show container status
    echo.
    goto :eof
)

if "%1"=="start" (
    echo Starting SprintFlow local services...
    docker compose -f docker-compose.local.yml up -d
    echo.
    echo Services starting:
    echo   MySQL:    http://localhost:3306
    echo   Backend:  http://localhost:8080
    echo   Frontend: http://localhost
    echo.
    echo Waiting for services to be healthy...
    timeout /t 5 /nobreak
    docker compose -f docker-compose.local.yml ps
    goto :eof
)

if "%1"=="stop" (
    echo Stopping SprintFlow services...
    docker compose -f docker-compose.local.yml down
    echo Services stopped.
    goto :eof
)

if "%1"=="restart" (
    echo Restarting SprintFlow services...
    docker compose -f docker-compose.local.yml restart
    echo Services restarted.
    goto :eof
)

if "%1"=="rebuild" (
    echo Rebuilding SprintFlow images and starting services...
    docker compose -f docker-compose.local.yml down
    docker compose -f docker-compose.local.yml up -d --build
    echo.
    echo Services rebuilt and starting:
    echo   MySQL:    http://localhost:3306
    echo   Backend:  http://localhost:8080
    echo   Frontend: http://localhost
    echo.
    echo Waiting for services to be healthy...
    timeout /t 10 /nobreak
    docker compose -f docker-compose.local.yml ps
    goto :eof
)

if "%1"=="logs" (
    echo Showing live logs (Ctrl+C to exit^)...
    docker compose -f docker-compose.local.yml logs -f
    goto :eof
)

if "%1"=="clean" (
    echo WARNING: This will delete all containers and database data!
    set /p confirm="Are you sure? (yes/no): "
    if /i "!confirm!"=="yes" (
        echo Cleaning up...
        docker compose -f docker-compose.local.yml down -v
        echo Cleanup complete.
    ) else (
        echo Cleanup cancelled.
    )
    goto :eof
)

if "%1"=="status" (
    docker compose -f docker-compose.local.yml ps
    goto :eof
)

echo Unknown command: %1
echo Run "docker-local.bat" with no arguments for help.
