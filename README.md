# JVM Cross-Language Fuzz Testing Tool

A fuzz testing tool designed to detect compiler defects and interoperability issues between different JVM languages, specifically focusing on **Kotlin-Java** and **Scala-Java** interoperability.

## Overview

This tool automatically generates random but syntactically valid cross-language code combinations, compiles them using the appropriate compilers (javac, kotlinc, scalac), executes them, and monitors for unexpected behaviors that indicate:

- **Compiler bugs**: Crashes, internal errors, or inconsistent compilation results
- **Interoperability defects**: Runtime errors when calling between languages
- **Semantic inconsistencies**: Different behavior between equivalent code in different languages
- **Type system violations**: Issues with type inference, generics, or null safety across language boundaries

## Features

- **Cross-language test case generation**: Creates realistic Kotlin-Java and Scala-Java code combinations
- **Automated compilation and execution**: Handles multi-language compilation workflows
- **Defect detection**: Identifies compiler crashes, runtime errors, and semantic inconsistencies  
- **Comprehensive reporting**: Saves defective test cases for further analysis
- **Configurable fuzzing**: Adjustable test case count, timeout settings, and verbosity

## Project Structure

```
jvm-cross-lang-fuzzer/
├── src/main/java/com/example/fuzzer/
│   ├── JVMCrossLangFuzzer.java          # Main entry point
│   ├── generators/                      # Test case generators
│   │   ├── KotlinGenerator.java         # Kotlin-Java test case generator
│   │   └── ScalaGenerator.java          # Scala-Java test case generator
│   ├── defects/                         # Defect detection logic
│   │   └── CrossLangDefectDetector.java # Compiler defect analyzer
│   ├── execution/                       # Test execution framework
│   │   └── TestCaseRunner.java          # Compiles and runs test cases
│   ├── model/                           # Data models
│   │   ├── TestCase.java                # Test case representation
│   │   ├── TestCaseResult.java          # Test execution results
│   │   ├── CompilationResult.java       # Compilation results
│   │   └── ExecutionResult.java         # Runtime execution results
│   └── examples/                        # Usage examples
│       └── BasicExample.java            # Simple usage example
├── build.sh                             # Build script
├── run.sh                               # Run script
├── USAGE.md                             # Detailed usage instructions
└── README.md                            # This file
```

## Getting Started

### Prerequisites

- Java 8+ (for running the fuzzer)
- Kotlin compiler (`kotlinc`) - for Kotlin-Java testing
- Scala compiler (`scalac`) - for Scala-Java testing
- These compilers should be available in your PATH

### Building

```bash
cd jvm-cross-lang-fuzzer
./build.sh
```

This creates `build/jvm-cross-lang-fuzzer.jar`.

### Running

```bash
# Basic usage with defaults (1000 test cases, output to fuzz-output/)
./run.sh

# Custom output directory and test count
java -jar build/jvm-cross-lang-fuzzer.jar -o my-results -n 500
```

### Command Line Options

- `-o, --output DIR`: Output directory for results (default: `fuzz-output`)
- `-n, --max-test-cases N`: Maximum number of test cases to generate (default: 1000)
- `-v, --verbose`: Enable verbose logging

## How It Works

1. **Test Case Generation**: The fuzzer creates random but valid cross-language code combinations:
   - Kotlin classes that extend Java classes or implement Java interfaces
   - Scala traits that mix with Java classes
   - Complex generic type interactions
   - Null-safety boundary scenarios
   - Method overloading and overriding across languages

2. **Compilation**: Each test case is compiled using the appropriate combination of compilers:
   - Kotlin-Java: Uses `kotlinc` which can compile both Kotlin and Java files together
   - Scala-Java: Uses `scalac` which handles mixed Scala/Java compilation
   - Pure Java/Kotlin/Scala: Uses respective single-language compilers

3. **Execution**: Successfully compiled test cases are executed with a timeout to prevent hangs.

4. **Defect Detection**: The tool monitors for:
   - Compiler crashes or internal errors
   - Unexpected compilation failures
   - Runtime exceptions during execution
   - Semantic inconsistencies between equivalent operations

5. **Reporting**: Any detected defects are saved to the output directory with full test case details for reproduction and bug reporting.

## Acceptance Criteria

The primary acceptance criterion for this project is **discovering new bugs in JVM language compilers**. Success is measured by:

- Finding previously unknown compiler defects
- Identifying interoperability issues between Kotlin/Scala and Java
- Generating reproducible test cases that can be submitted to compiler teams

## Example Defect Types

- **Kotlin-Java**: 
  - Inline class interoperability issues
  - Coroutines interacting with Java futures
  - Sealed class inheritance from Java
  - Default parameter handling across boundaries

- **Scala-Java**:
  - Implicit conversion conflicts
  - Trait linearization with Java inheritance
  - Case class serialization with Java frameworks
  - Type erasure differences causing runtime errors

## Contributing

This tool is designed to be extensible. You can:

- Add new test case generators for other JVM language combinations
- Implement additional defect detection heuristics
- Improve the quality and diversity of generated test cases
- Add support for more sophisticated semantic analysis

## License

This project is open source and available under the MIT License.

---

**Note**: This fuzz testing tool is most effective when run on systems with actual Kotlin and Scala compilers installed. In environments without these compilers, it will simulate the process but won't detect real compiler defects.