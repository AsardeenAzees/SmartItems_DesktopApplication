@echo off
echo Building Smart Items Desktop Application...

REM Create target directories
if not exist "target\classes" mkdir target\classes
if not exist "target\lib" mkdir target\lib

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    exit /b 1
)

REM Check if Maven is available
mvn -version >nul 2>&1
if errorlevel 1 (
    echo Warning: Maven not found, attempting manual compilation...
    goto manual_build
)

echo Using Maven to build...
mvn clean package
if errorlevel 1 (
    echo Maven build failed, attempting manual compilation...
    goto manual_build
)

echo Build completed successfully!
echo Run with: java -jar target\smartitems-desktop-0.1.0-jar-with-dependencies.jar
goto end

:manual_build
echo Manual compilation mode...
echo Please ensure all required JAR files are in target\lib directory:
echo - flatlaf-3.4.jar
echo - mysql-connector-j-8.0.33.jar
echo - bcrypt-0.10.2.jar
echo - slf4j-api-2.0.13.jar
echo - slf4j-simple-2.0.13.jar
echo - annotations-24.1.0.jar

REM Compile all Java files
javac -cp "target\lib\*" -d target\classes src\main\java\com\smartitems\*.java src\main\java\com\smartitems\config\*.java src\main\java\com\smartitems\dao\*.java src\main\java\com\smartitems\model\*.java src\main\java\com\smartitems\service\*.java src\main\java\com\smartitems\ui\*.java
if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)

echo Manual compilation completed!
echo Run with: java -cp "target\classes;target\lib\*" com.smartitems.App

:end
pause
