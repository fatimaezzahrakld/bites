@echo off
cd /d "%~dp0"
docker compose logs --tail=100 app
pause
