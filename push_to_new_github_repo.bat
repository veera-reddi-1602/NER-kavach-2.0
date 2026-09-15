@echo off
setlocal enabledelayedexpansion
title NER KAVACH 3.0 - Push to New GitHub Repository
color 0A

echo ==============================================================================
echo   NER KAVACH 3.0 — AI Landslide Early Warning System
echo   GitHub Repository Upload Utility
echo ==============================================================================
echo.
echo  This script will package, stage, and push the entire codebase (Android Native APK,
echo  Web Frontend, Python AI/ML Backend, IoT Firmware, and Documentation)
echo  to your new GitHub repository.
echo.
echo ==============================================================================
echo.

set /p REPO_URL="Enter your new GitHub Repository URL (e.g., https://github.com/username/new-repo.git): "

if "%REPO_URL%"=="" (
    echo [ERROR] No Repository URL provided. Exiting.
    pause
    exit /b 1
)

echo.
echo [1/4] Checking and staging all required files...
cd /d "%~dp0"
git init
git add .

echo.
echo [2/4] Committing project files...
git commit -m "Initial Release: NER KAVACH 3.0 with Production APK, AI Engine, IoT, and Offline Mesh"

echo.
echo [3/4] Setting main branch and origin...
git branch -M main
git remote remove origin >nul 2>&1
git remote add origin %REPO_URL%

echo.
echo [4/4] Pushing to GitHub (%REPO_URL%)...
git push -u origin main --force

if %ERRORLEVEL% equ 0 (
    echo.
    echo ==============================================================================
    echo  SUCCESS! All APK, Android, Web, and Backend files uploaded to:
    echo  %REPO_URL%
    echo ==============================================================================
) else (
    echo.
    echo [WARNING] Git push encountered an issue. Please verify your repository URL and credentials.
)

echo.
pause
