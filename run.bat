@echo off
setlocal EnableExtensions EnableDelayedExpansion
echo Starting Doctor Channelling System...

REM Build the project before running to ensure all classes are compiled
call build.bat
if %ERRORLEVEL% NEQ 0 (
    echo Aborting run due to build failure.
    endlocal & exit /b 1
)

REM Set classpath with all required JARs
set CLASSPATH=out;lib\bson-4.11.0.jar;lib\mongodb-driver-core-4.11.0.jar;lib\mongodb-driver-sync-4.11.0.jar;lib\slf4j-api-1.7.36.jar

REM Run the application
java -cp "%CLASSPATH%" Main

endlocal
