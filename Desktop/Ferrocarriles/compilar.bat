@echo off
REM ========================================
REM   COMPILANDO PROYECTO JAVA
REM ========================================

REM Limpiar carpeta bin
if exist bin rmdir /s /q bin
mkdir bin


REM Compilar todos los archivos .java de src en una sola llamada
echo Compilando todo el proyecto...
dir /b /s src\*.java > sources.txt
javac -encoding UTF-8 -source 8 -target 8 -d bin -cp "lib\gson-2.10.1.jar;lib\jackson-core-2.15.3.jar;lib\jackson-annotations-2.15.3.jar;lib\jackson-databind-2.15.3.jar;bin" @sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: No se pudo compilar el proyecto
    pause
    exit /b 1
)
del sources.txt

echo.
echo ========================================
echo   COMPILACION EXITOSA
echo ========================================
echo.
echo Archivos compilados en carpeta: bin\
echo.
echo Para ejecutar:
echo    ejecutar.bat
echo.
pause
