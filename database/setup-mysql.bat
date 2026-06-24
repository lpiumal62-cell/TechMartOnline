@echo off
setlocal
set MYSQL="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist %MYSQL% (
  echo MySQL client not found at %MYSQL%
  exit /b 1
)

if "%~1"=="" (
  echo Enter your MySQL root password when prompted.
  %MYSQL% -u root -p < "%~dp0techmart.sql"
) else (
  %MYSQL% -u root -p"%~1" < "%~dp0techmart.sql"
)
if errorlevel 1 (
  echo Setup failed. Check your root password and try again.
  exit /b 1
)

echo Verifying techmart user...
%MYSQL% -u techmart -ptechmart_pass -e "USE techmart_db; SELECT COUNT(*) AS products FROM products;"
echo.
echo Database ready. Redeploy TechMartOnline in IntelliJ, then open:
echo   http://localhost:8080/TechMartOnline/
