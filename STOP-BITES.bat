@echo off
cd /d "%~dp0"
docker compose down
echo Database volume preserved.
pause
