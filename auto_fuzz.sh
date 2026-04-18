#!/bin/bash

# Automated fuzz testing script for JVM cross-language compiler defect discovery
# This script runs continuously until a real compiler defect is found

set -e

echo "=== JVM Cross-Language Fuzz Testing - Automated Defect Discovery ==="
echo "Starting automated fuzz testing..."

# Check if required compilers are available
check_compilers() {
    echo "Checking compiler availability..."
    
    if ! command -v javac &> /dev/null; then
        echo "ERROR: Java compiler (javac) not found!"
        exit 1
    fi
    
    if ! command -v kotlinc &> /dev/null; then
        echo "WARNING: Kotlin compiler (kotlinc) not found - Kotlin-Java testing disabled"
        KOTLIN_AVAILABLE=false
    else
        KOTLIN_AVAILABLE=true
        echo "Kotlin compiler available"
    fi
    
    if ! command -v scalac &> /dev/null; then
        echo "WARNING: Scala compiler (scalac) not found - Scala-Java testing disabled"  
        SCALA_AVAILABLE=false
    else
        SCALA_AVAILABLE=true
        echo "Scala compiler available"
    fi
    
    if [[ "$KOTLIN_AVAILABLE" == false && "$SCALA_AVAILABLE" == false ]]; then
        echo "ERROR: Neither Kotlin nor Scala compilers available. Cannot test cross-language interoperability."
        exit 1
    fi
}

# Run enhanced fuzz testing
run_fuzz_test() {
    local iteration=$1
    local output_dir="fuzz-output-auto-$iteration"
    
    echo "Running fuzz test iteration $iteration..."
    
    # Build the tool
    ./build.sh
    
    # Run enhanced fuzzing
    if [[ "$KOTLIN_AVAILABLE" == true && "$SCALA_AVAILABLE" == true ]]; then
        java -cp build/jvm-cross-lang-fuzzer.jar com.example.fuzzer.EnhancedJVMCrossLangFuzzer \
            --output "$output_dir" --test-cases 500
    elif [[ "$KOTLIN_AVAILABLE" == true ]]; then
        # Only Kotlin-Java testing
        java -cp build/jvm-cross-lang-fuzzer.jar com.example.fuzzer.EnhancedJVMCrossLangFuzzer \
            --output "$output_dir" --test-cases 250
    elif [[ "$SCALA_AVAILABLE" == true ]]; then
        # Only Scala-Java testing  
        java -cp build/jvm-cross-lang-fuzzer.jar com.example.fuzzer.EnhancedJVMCrossLangFuzzer \
            --output "$output_dir" --test-cases 250
    fi
    
    # Check for defects
    if [ -d "$output_dir/defects" ] && [ "$(ls -A "$output_dir/defects")" ]; then
        echo "🎉 DEFECT FOUND in iteration $iteration!"
        echo "Defect reports saved to: $output_dir/defects/"
        
        # Create summary report
        create_defect_summary "$output_dir"
        
        return 0
    else
        echo "No defects found in iteration $iteration"
        return 1
    fi
}

# Create defect summary report
create_defect_summary() {
    local output_dir=$1
    local summary_file="$output_dir/DEFECT_SUMMARY.md"
    
    echo "# JVM Cross-Language Compiler Defect Summary" > "$summary_file"
    echo "" >> "$summary_file"
    echo "## Discovery Details" >> "$summary_file"
    echo "- **Date**: $(date)" >> "$summary_file"
    echo "- **Fuzz Tool Version**: $(git rev-parse HEAD)" >> "$summary_file"
    echo "- **Test Cases Generated**: $(find "$output_dir" -name "*.java" | wc -l)" >> "$summary_file"
    echo "" >> "$summary_file"
    
    echo "## Defects Found" >> "$summary_file"
    for defect_file in "$output_dir/defects/"*; do
        if [ -f "$defect_file" ]; then
            echo "### $(basename "$defect_file")" >> "$summary_file"
            echo '```' >> "$summary_file"
            head -20 "$defect_file" >> "$summary_file"
            echo '```' >> "$summary_file"
            echo "" >> "$summary_file"
        fi
    done
    
    echo "## Next Steps" >> "$summary_file"
    echo "1. Verify the defect independently" >> "$summary_file"
    echo "2. Create minimal reproducer" >> "$summary_file" 
    echo "3. Report to appropriate compiler issue tracker" >> "$summary_file"
    echo "4. Update fuzz testing tool based on findings" >> "$summary_file"
    
    echo "Defect summary created: $summary_file"
}

# Main execution loop
main() {
    check_compilers
    
    local iteration=1
    local max_iterations=100  # Prevent infinite loops
    
    while [ $iteration -le $max_iterations ]; do
        if run_fuzz_test $iteration; then
            echo "✅ Automated defect discovery completed successfully!"
            echo "Check $PWD/fuzz-output-auto-$iteration/ for detailed results."
            exit 0
        fi
        
        iteration=$((iteration + 1))
        
        # Wait before next iteration to avoid overwhelming system
        sleep 60
    done
    
    echo "❌ No defects found after $max_iterations iterations."
    echo "Consider enhancing the fuzz testing tool with more complex test cases."
    exit 1
}

# Run main function
main "$@"