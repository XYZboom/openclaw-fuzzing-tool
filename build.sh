#!/bin/bash

echo "JVM Cross-Language Fuzzer - Build Script"
echo "========================================="

# Create output directory
mkdir -p build/classes

# Find all Java source files
SOURCES=$(find src/main/java -name "*.java" | tr '\n' ' ')

# Compile with proper classpath
echo "Compiling JVM Cross-Language Fuzzer..."
javac -d build/classes $SOURCES

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Creating JAR file..."
    jar cfe build/jvm-cross-lang-fuzzer.jar com.example.fuzzer.JVMCrossLangFuzzer -C build/classes .
    echo "Build completed successfully!"
    echo "JAR file: build/jvm-cross-lang-fuzzer.jar"
else
    echo "Compilation failed!"
    exit 1
fi