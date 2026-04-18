#!/bin/bash

echo "JVM Cross-Language Fuzzer - Build Script"
echo "========================================="

# Create build directory
mkdir -p build/classes

# Compile only the working source files (basic version + enhanced main)
echo "Compiling JVM Cross-Language Fuzzer..."
javac -d build/classes \
    src/main/java/com/example/fuzzer/model/*.java \
    src/main/java/com/example/fuzzer/defects/CrossLangDefectDetector.java \
    src/main/java/com/example/fuzzer/execution/TestCaseRunner.java \
    src/main/java/com/example/fuzzer/generators/KotlinGenerator.java \
    src/main/java/com/example/fuzzer/generators/ScalaGenerator.java \
    src/main/java/com/example/fuzzer/JVMCrossLangFuzzer.java \
    src/main/java/com/example/fuzzer/EnhancedJVMCrossLangFuzzer.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    
    # Create JAR file
    echo "Creating JAR file..."
    jar cf build/jvm-cross-lang-fuzzer.jar -C build/classes .
    
    if [ $? -eq 0 ]; then
        echo "Build completed successfully!"
        echo "JAR file: build/jvm-cross-lang-fuzzer.jar"
    else
        echo "Failed to create JAR file!"
        exit 1
    fi
else
    echo "Compilation failed!"
    exit 1
fi