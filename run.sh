#!/bin/bash

echo "Starting Doctor Channelling System..."

# Set classpath with all required JARs
CLASSPATH="out:lib/bson-4.11.0.jar:lib/mongodb-driver-core-4.11.0.jar:lib/mongodb-driver-sync-4.11.0.jar:lib/slf4j-api-1.7.36.jar"

# Run the application
java -cp "$CLASSPATH" Main
