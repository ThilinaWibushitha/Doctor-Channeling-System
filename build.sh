#!/bin/bash

echo "Building Doctor Channelling System..."

# Create output directory if it doesn't exist
mkdir -p out

# Set classpath with all required JARs
CLASSPATH="lib/bson-4.11.0.jar:lib/mongodb-driver-core-4.11.0.jar:lib/mongodb-driver-sync-4.11.0.jar:lib/slf4j-api-1.7.36.jar"

# Compile all Java files
javac -cp "$CLASSPATH" -d out \
    src/com/xyz/model/*.java \
    src/com/xyz/Utility/*.java \
    src/com/xyz/Data/*.java \
    src/com/xyz/Service/*.java \
    src/com/xyz/Ui/*.java \
    src/Main.java

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "To run the application, use: java -cp \"out:$CLASSPATH\" Main"
else
    echo "Build failed!"
    exit 1
fi
