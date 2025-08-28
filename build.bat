@echo off
setlocal EnableExtensions EnableDelayedExpansion
echo Building Doctor Channelling System...

REM Clean output directory and stray class files to ensure a fresh build
if exist "out" (
    rmdir /s /q out
)
for /r "src" %%F in (*.class) do del /q "%%F" >nul 2>&1

REM Recreate output directory
mkdir out >nul 2>&1

REM Set classpath with all required JARs
set CLASSPATH=lib\bson-4.11.0.jar;lib\mongodb-driver-core-4.11.0.jar;lib\mongodb-driver-sync-4.11.0.jar;lib\slf4j-api-1.7.36.jar

REM Compile all Java files
javac -Xlint:deprecation -Xlint:unchecked -cp "%CLASSPATH%" -d out ^
    src\com\xyz\model\*.java ^
    src\com\xyz\Utility\*.java ^
    src\com\xyz\Data\*.java ^
    src\com\xyz\Service\*.java ^
    src\com\xyz\Ui\*.java ^
    src\Main.java

if %ERRORLEVEL% EQU 0 (
    echo Build successful!
    endlocal & exit /b 0
) else (
    echo Build failed!
    endlocal & exit /b 1
)
