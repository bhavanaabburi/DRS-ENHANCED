@echo off
setlocal
cd /d "%~dp0"

set "MAVEN_CMD=C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"

if not exist "%MAVEN_CMD%" (
    echo ERROR: Maven executable not found.
    pause
    exit /b 1
)

if not exist "tls\drs-client-trust.p12" (
    echo ERROR: Client truststore not found.
    pause
    exit /b 1
)

set /p "DRS_TRUSTSTORE_PASSWORD=Enter client truststore password: "

call "%MAVEN_CMD%" javafx:run

if errorlevel 1 (
    echo ERROR: JavaFX client failed to start.
)

pause