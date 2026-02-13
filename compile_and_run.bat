@echo off
echo ========================================
echo Compiling Voyage et Affaires...
echo ========================================
call mvn clean install
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Launching application...
echo ========================================
call mvn javafx:run
