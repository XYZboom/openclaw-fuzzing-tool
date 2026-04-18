#!/bin/bash

# JVM Cross-Language Fuzzer - Run Script
echo "JVM Cross-Language Fuzzer - Run Script"
echo "======================================="

# Check if JAR file exists
if [ ! -f "build/jvm-cross-lang-fuzzer.jar" ]; then
    echo "Error: JAR file not found. Please run ./build.sh first."
    exit 1
fi

# Set default output directory and test count
OUTPUT_DIR="fuzz-output"
MAX_TESTS=10

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -o|--output)
            OUTPUT_DIR="$2"
            shift 2
            ;;
        -n|--max-tests)
            MAX_TESTS="$2"
            shift 2
            ;;
        -h|--help)
            echo "Usage: ./run.sh [options]"
            echo "Options:"
            echo "  -o, --output DIR    Output directory (default: fuzz-output)"
            echo "  -n, --max-tests N   Maximum number of test cases (default: 10)"
            echo "  -h, --help          Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Run the fuzzer
echo "Running JVM Cross-Language Fuzzer..."
echo "Output directory: $OUTPUT_DIR"
echo "Max test cases: $MAX_TESTS"
echo ""

java -jar build/jvm-cross-lang-fuzzer.jar -o "$OUTPUT_DIR" -n "$MAX_TESTS"

echo ""
echo "Fuzzing completed!"
echo "Check $OUTPUT_DIR for results and any detected defects."