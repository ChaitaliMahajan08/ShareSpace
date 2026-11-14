@echo off
echo ======================================
echo        Compiling ShareSpace...
echo ======================================

if not exist bin mkdir bin

dir /s /B src\main\java\*.java > sources.txt
javac -cp "lib/mysql-connector-j-9.1.0.jar" -d bin @sources.txt
del sources.txt

if %ERRORLEVEL% equ 0 (
    echo.
    echo Compilation successful! Launching ShareSpace...
    echo.
    java -cp "bin;lib/mysql-connector-j-9.1.0.jar" Main
) else (
    echo.
    echo Compilation failed.
    pause
)
