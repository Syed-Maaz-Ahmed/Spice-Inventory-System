@echo off
setlocal
title Inventory Management System

echo ========================================
echo  Initializing System...
echo ========================================

:: Clean and create bin directory
if exist bin rmdir /s /q bin
mkdir bin

:: Compile all Java files
echo Compiling source code...
javac -encoding UTF-8 -d bin -cp "lib/*" -sourcepath src src/models/*.java src/data/*.java src/ui/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation Failed! Please check the errors above.
    pause
    exit /b %errorlevel%
)

echo.
echo [SUCCESS] Compilation complete.
echo Starting Application...
echo.

:: Run the application
java -cp "bin;lib/*" ui.LoginFrame

endlocal
pause

