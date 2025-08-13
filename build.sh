#!/bin/bash

echo "Building Smart Items Desktop Application..."

# Create target directories
mkdir -p target/classes target/lib

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# Check if Maven is available
if command -v mvn &> /dev/null; then
    echo "Using Maven to build..."
    mvn clean package
    if [ $? -eq 0 ]; then
        echo "Build completed successfully!"
        echo "Run with: java -jar target/smartitems-desktop-0.1.0-jar-with-dependencies.jar"
        exit 0
    else
        echo "Maven build failed, attempting manual compilation..."
    fi
else
    echo "Warning: Maven not found, attempting manual compilation..."
fi

# Manual compilation
echo "Manual compilation mode..."
echo "Please ensure all required JAR files are in target/lib directory:"
echo "- flatlaf-3.4.jar"
echo "- mysql-connector-j-8.0.33.jar"
echo "- bcrypt-0.10.2.jar"
echo "- slf4j-api-2.0.13.jar"
echo "- slf4j-simple-2.0.13.jar"
echo "- annotations-24.1.0.jar"

# Compile all Java files
javac -cp "target/lib/*" -d target/classes src/main/java/com/smartitems/*.java src/main/java/com/smartitems/config/*.java src/main/java/com/smartitems/dao/*.java src/main/java/com/smartitems/model/*.java src/main/java/com/smartitems/service/*.java src/main/java/com/smartitems/ui/*.java
if [ $? -eq 0 ]; then
    echo "Manual compilation completed!"
    echo "Run with: java -cp 'target/classes:target/lib/*' com.smartitems.App"
else
    echo "Compilation failed!"
    exit 1
fi
