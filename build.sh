#!/bin/bash

# Navigate to the project directory
cd "$(dirname "$0")"

# Clean the project
./gradlew clean

# Build the APK
./gradlew assembleDebug

# Optionally, install the APK to a connected device (uncomment if needed)
# ./gradlew installDebug

echo "Build completed successfully!"