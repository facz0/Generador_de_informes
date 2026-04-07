@echo off
echo ========================================
echo  Generando instalador - Generador GGE
echo ========================================

REM Paso 1: Compilar y generar el fat JAR
echo.
echo [1/3] Compilando y empaquetando...
call mvn clean package -q
if %errorlevel% neq 0 (
    echo ERROR: Fallo la compilacion
    pause
    exit /b 1
)
echo     OK - JAR generado

REM Paso 2: Preparar carpeta de recursos para incluir en el instalador
echo.
echo [2/3] Preparando recursos...
if exist installer-input rmdir /s /q installer-input
mkdir installer-input
copy target\Genereador_Informes-1.0-SNAPSHOT.jar installer-input\app.jar
xcopy /s /e /q plantillas installer-input\plantillas\
echo     OK - Recursos listos

REM Paso 3: Generar la imagen de la app con jpackage
echo.
echo [3/3] Generando aplicacion...
if exist installer-output rmdir /s /q installer-output
jpackage ^
  --input installer-input ^
  --dest installer-output ^
  --name "Generador de Informes GGE" ^
  --main-jar app.jar ^
  --main-class com.onpe.genereador_informes.vista.AppStarter ^
  --type app-image ^
  --app-version 1.0 ^
  --vendor "ONPE" ^
  --description "Generador de Informes de Actividades GGE"

if %errorlevel% neq 0 (
    echo ERROR: Fallo la generacion del instalador
    pause
    exit /b 1
)

echo.
echo ========================================
echo  Instalador generado en: installer-output\
echo ========================================
pause
