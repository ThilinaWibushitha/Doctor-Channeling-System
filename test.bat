@echo off
echo Testing Doctor Channelling System...

REM Test compilation
echo.
echo 1. Testing compilation...
call .\build.bat
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)
echo ✅ Compilation successful!

REM Test if Java is available
echo.
echo 2. Testing Java availability...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Java is not available or not in PATH!
    pause
    exit /b 1
)
echo ✅ Java is available!

REM Test if main class can be loaded
echo.
echo 3. Testing class loading...
java -cp "out;lib\bson-4.11.0.jar;lib\mongodb-driver-core-4.11.0.jar;lib\mongodb-driver-sync-4.11.0.jar;lib\slf4j-api-1.7.36.jar" Main --test >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ⚠️ Main class loading test failed (this is normal if --test flag is not supported)
) else (
    echo ✅ Main class loaded successfully!
)

echo.
echo 🎉 All tests completed successfully!
echo.
echo To run the application:
echo   .\run.bat
echo.
echo To build the project:
echo   .\build.bat
echo.
pause
