@echo off
setlocal
title Birla Blitz - Android APK Builder
echo ============================================================
echo   Birla Blitz Sound to Text - Standalone APK Compiler
echo ============================================================
echo.

set "PY_EXE="
for %%P in ("py -3.9" "py -3" "python") do (
    if not defined PY_EXE (
        %%~P -V >nul 2>&1
        if not errorlevel 1 set "PY_EXE=%%~P"
    )
)

if not defined PY_EXE (
    echo [ERROR] Python 3 was not found in PATH or via py launcher.
    echo Please install Python 3 or ensure it is added to your environment variables.
    pause
    exit /b 1
)

echo [OK] Using Python launcher: %PY_EXE%
echo.

%PY_EXE% "%~dp0build_apk.py"
set "BUILD_STATUS=%ERRORLEVEL%"

echo.
if %BUILD_STATUS% equ 0 (
    echo [SUCCESS] APK compiled and signed in: "%~dp0release\BirlaBlitzSoundToText.apk"
) else (
    echo [ERROR] Compilation failed with error code %BUILD_STATUS%.
)
echo.
pause
exit /b %BUILD_STATUS%
