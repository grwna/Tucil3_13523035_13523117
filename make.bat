@echo off
REM filepath: /home/grwcha/kuliah/stima/tucil3/Tucil3_13523035_13523117/build.bat

if not exist bin mkdir bin

if "%1"=="compile" goto :compile
if "%1"=="run" goto :run
if "%1"=="clean" goto :clean
if "%1"=="help" goto :help
goto :compile

:compile
echo Compiling...
javac -d bin src\*.java
goto :end

:run
call :compile
echo Running...
java -cp bin MainApp
goto :end

:clean
echo Cleaning...
if exist bin rmdir /s /q bin
goto :end

:help
echo Available commands:
echo   build.bat        - Compile the project (default)
echo   build.bat compile - Compile the project
echo   build.bat run     - Run the application
echo   build.bat clean   - Remove compiled files
echo   build.bat help    - Display this help

:end