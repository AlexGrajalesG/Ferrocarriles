@echo off
echo ========================================
echo   INICIANDO SERVIDOR FERROVIARIO
echo ========================================
echo.

REM Verificar que esté compilado
if not exist bin\com\ferrocarriles\servidor\ServidorHTTP.class (
    echo ERROR: Proyecto no compilado
    echo Ejecuta primero: compilar.bat
    echo.
    pause
    exit /b 1
)

echo Iniciando servidor en http://localhost:8080
echo.
echo Presiona CTRL+C para detener el servidor
echo ========================================
echo.

REM Ejecutar servidor
java -cp "bin;lib\gson-2.10.1.jar;lib\jackson-core-2.15.3.jar;lib\jackson-annotations-2.15.3.jar;lib\jackson-databind-2.15.3.jar" com.ferrocarriles.servidor.ServidorHTTP

pause
