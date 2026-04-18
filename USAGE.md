# JVM Cross-Language Fuzz Testing Tool - Usage Guide

## Overview

This tool is designed to detect compiler defects and interoperability issues between JVM languages, specifically focusing on Kotlin-Java and Scala-Java combinations. It generates random but valid cross-language code combinations, compiles them, executes them, and monitors for unexpected behaviors that indicate compiler bugs.

## Installation Requirements

- **Java 8 or higher** (required)
- **Kotlin compiler** (`kotlinc`) - for Kotlin-Java testing
- **Scala compiler** (`scalac`) - for Scala-Java testing  
- **Maven or Gradle** (optional, for building from source)

> **Note**: The tool will work in basic mode with just Java, but to test actual Kotlin-Java and Scala-Java interoperability, you need the respective compilers installed.

## Basic Usage

### Building the Tool

```bash
# Clone the repository
git clone <repository-url>
cd jvm-cross-lang-fuzzer

# Build the tool
./build.sh

# This creates build/jvm-cross-lang-fuzzer.jar
```

### Running Basic Fuzz Testing

```bash
# Run with default settings (1000 test cases, output to fuzz-output/)
./run.sh

# Run with custom parameters
./run.sh --output /path/to/output --test-cases 5000 --verbose
```

### Command Line Options

| Option | Description | Default |
|--------|-------------|---------|
| `-o, --output DIR` | Output directory for test cases and defect reports | `fuzz-output/` |
| `-n, --test-cases N` | Maximum number of test cases to generate | `1000` |
| `-v, --verbose` | Enable verbose logging | `false` |
| `-h, --help` | Show help message | |

## Enhanced Usage

The enhanced version generates more complex test cases that exercise advanced language features:

### Running Enhanced Fuzz Testing

```bash
# Build enhanced version
./build.sh --enhanced

# Run enhanced fuzzing
java -cp build/jvm-cross-lang-fuzzer.jar com.example.fuzzer.EnhancedJVMCrossLangFuzzer \
    --output fuzz-output-enhanced \
    --test-cases 100
```

### Enhanced Features Tested

#### Kotlin-Java Interoperability
- **Nullable types and platform types**: Tests Kotlin's null safety with Java's nullable references
- **Generics and variance**: Exercises type variance and generic interoperability
- **SAM conversions**: Tests functional interface compatibility
- **Extension functions**: Validates extension function calls from Java contexts
- **Operator overloading**: Checks operator resolution across language boundaries

#### Scala-Java Interoperability  
- **Case classes and pattern matching**: Tests Scala case classes used from Java
- **Implicit conversions**: Exercises Scala implicits with Java types
- **Traits and mixin composition**: Validates trait inheritance with Java interfaces
- **Type classes**: Tests ad-hoc polymorphism across language boundaries
- **Varargs and default parameters**: Checks parameter handling compatibility

## Understanding Output

### Directory Structure

```
fuzz-output/
├── defects/                    # Defective test cases (if any found)
│   ├── kotlin-java_defect_*.txt
│   └── scala-java_defect_*.txt
├── test-cases/                 # Generated test cases (debug mode)
└── logs/                       # Execution logs
```

### Defect Reports

When a defect is detected, a detailed report is saved containing:
- Language pair being tested
- Test case ID and timestamp
- Defect classification (compilation error, runtime crash, semantic inconsistency)
- Complete source code of the defective test case
- Compiler/runtime error messages

## Defect Detection Capabilities

The tool can detect several types of compiler defects:

### 1. Compilation Crashes
- Compiler segfaults or internal errors
- Unexpected compilation failures on valid code
- Stack overflow during compilation

### 2. Semantic Inconsistencies  
- Different behavior between languages for equivalent code
- Type system violations that should be caught at compile time
- Incorrect code generation leading to runtime errors

### 3. Runtime Defects
- Unexpected exceptions during execution
- Memory leaks or resource exhaustion
- Incorrect results from cross-language method calls

### 4. Interoperability Issues
- Missing or incorrect bridge methods
- Incorrect null handling
- Generic type erasure problems
- Visibility and access control issues

## Integration with CI/CD

You can integrate this tool into your continuous integration pipeline:

```yaml
# GitHub Actions example
- name: Run JVM Cross-Language Fuzz Testing
  run: |
    cd jvm-cross-lang-fuzzer
    ./build.sh
    ./run.sh --test-cases 1000
    
    # Fail if any defects were found
    if [ -d "fuzz-output/defects" ] && [ "$(ls -A fuzz-output/defects)" ]; then
      echo "Defects found! Check fuzz-output/defects/"
      exit 1
    fi
```

## Performance Considerations

- **Memory Usage**: Each test case runs in isolation, so memory usage scales with parallelism
- **Execution Time**: Complex test cases take longer to compile and execute
- **Disk Space**: Test cases and defect reports are saved to disk (can be disabled)

### Optimization Tips

1. **Reduce test case count** for quick validation: `--test-cases 100`
2. **Disable verbose logging** in production runs: omit `--verbose`
3. **Use parallel execution** by running multiple instances with different output directories
4. **Clean up old outputs** regularly to save disk space

## Troubleshooting

### Common Issues

#### "Command not found: kotlinc/scalac"
- **Solution**: Install Kotlin and Scala compilers
  ```bash
  # Ubuntu/Debian
  sudo apt-get install kotlin scala
  
  # macOS (with Homebrew)  
  brew install kotlin scala
  
  # Manual installation
  # Download from https://kotlinlang.org/ and https://www.scala-lang.org/
  ```

#### "Permission denied" on build/run scripts
- **Solution**: Make scripts executable
  ```bash
  chmod +x build.sh run.sh
  ```

#### Java version compatibility issues
- **Solution**: Ensure Java 8+ is installed and `JAVA_HOME` is set correctly

### Debugging Defects

When a defect is found:

1. **Examine the defect report** in `fuzz-output/defects/`
2. **Reproduce manually** by compiling the source code directly
3. **Simplify the test case** to create a minimal reproducer
4. **Report to compiler maintainers** with the minimal reproducer

## Contributing

To extend the tool with new language features or defect detection patterns:

1. **Add new generators** in `src/main/java/com/example/fuzzer/generators/`
2. **Extend defect detectors** in `src/main/java/com/example/fuzzer/defects/`
3. **Update the IR model** in `src/main/java/com/example/fuzzer/ir/` for complex scenarios
4. **Add test cases** in `src/test/java/` to validate new functionality

## License

This tool is provided under the MIT License. See `LICENSE` file for details.

## Support

For issues, feature requests, or questions:
- Open an issue on the GitHub repository
- Contact the maintainers via email
- Join the discussion forum/community

---

**Remember**: The effectiveness of fuzz testing increases with the complexity and diversity of generated test cases. Regular updates to include new language features and edge cases will improve defect detection capabilities over time.