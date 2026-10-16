@echo off
setlocal
cd /d "%~dp0"

where keytool >nul 2>&1
if errorlevel 1 (
    echo ERROR: JDK keytool not found. Install JDK 21.
    exit /b 1
)

if not exist "tls" mkdir "tls"

if exist "tls\drs-server.p12" (
    echo ERROR: Server keystore already exists.
    echo Back it up before generating a new one.
    exit /b 1
)

if exist "tls\drs-client-trust.p12" (
    echo ERROR: Client truststore already exists.
    exit /b 1
)

echo Enter a TLS password of at least 6 characters.
set /p "TLS_PASS=TLS demo password: "

if "%TLS_PASS%"=="" (
    echo ERROR: Password is required.
    exit /b 1
)

keytool -genkeypair ^
  -alias drs-server ^
  -keyalg RSA ^
  -keysize 3072 ^
  -validity 365 ^
  -storetype PKCS12 ^
  -keystore "tls\drs-server.p12" ^
  -storepass "%TLS_PASS%" ^
  -keypass "%TLS_PASS%" ^
  -dname "CN=localhost, OU=DRS, O=University Demo, C=AU" ^
  -ext "SAN=dns:localhost,ip:127.0.0.1" ^
  -noprompt

if errorlevel 1 exit /b 1

keytool -exportcert ^
  -alias drs-server ^
  -keystore "tls\drs-server.p12" ^
  -storetype PKCS12 ^
  -storepass "%TLS_PASS%" ^
  -file "tls\drs-server.cer"

if errorlevel 1 exit /b 1

keytool -importcert ^
  -alias drs-server ^
  -file "tls\drs-server.cer" ^
  -keystore "tls\drs-client-trust.p12" ^
  -storetype PKCS12 ^
  -storepass "%TLS_PASS%" ^
  -noprompt

if errorlevel 1 exit /b 1

echo.
echo TLS setup completed successfully.
echo Keep your TLS password for server and client startup.
pause