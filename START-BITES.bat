@echo off
cd /d "%~dp0"
where docker >nul 2>nul
if errorlevel 1 (echo Docker is not installed or not in PATH.& pause & exit /b 1)
echo Building and starting BITES...
docker compose up -d --build
if errorlevel 1 (echo Build/start failed. See errors above.& pause & exit /b 1)
echo.
echo BITES URL: http://localhost:8081
echo Checking container status...
docker compose ps
echo.
echo If the application is still starting, inspect VIEW-LOGS.bat.
start "" "http://localhost:8081"
pause
