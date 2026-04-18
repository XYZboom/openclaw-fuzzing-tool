#!/bin/bash

# JVM Cross-Language Fuzzer - Run Script
# This script runs the fuzz testing tool with appropriate parameters

set -e

echo "JVM Cross-Language Fuzzer - Run Script"
echo "======================================="

# Build the project first
./build.sh

# Check if enhanced mode is requested
if [ "$1" = "--enhanced" ]; then
    echo "Running Enhanced JVM Cross-Language Fuzzer..."
    java -cp build/jvm-cross-lang-fuzzer.jar com.example.fuzzer.EnhancedJVMCrossLangFuzzer \
        -o fuzz-output-enhanced -n 20 -v
else
    echo "Running Basic JVM Cross-Language Fuzzer..."
    java -cp build/jvm-cross-lang-fuzzer.jar com.example.fuzzer.JVMCrossLangFuzzer \
        -o fuzz-output -n 10 -v
fi