@echo off
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Requesting Administrator permission to update Windows Firewall...
    powershell -Command "Start-Process -FilePath '%~f0' -Verb RunAs"
    exit /b
)

powershell -ExecutionPolicy Bypass -File "%~dp0allow-firewall.ps1"
pause
