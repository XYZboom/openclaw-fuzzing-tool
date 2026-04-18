# JVM Cross-Language Fuzzer Usage Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Building the Tool](#building-the-tool)
3. [Running the Fuzzer](#running-the-fuzzer)
4. [Command Line Options](#command-line-options)
5. [Understanding Output](#understanding-output)
6. [Customizing Test Generation](#customizing-test-generation)
7. [Defect Detection](#defect-detection)
8. [Troubleshooting](#troubleshooting)

## Prerequisites

Before using the JVM Cross-Language Fuzzer, ensure you have the following installed:

- **Java 8 or higher** - Required for running the fuzzer
- **Kotlin compiler (kotlinc)** - Required for Kotlin-Java interoperability testing
- **Scala compiler (scalac)** - Required for Scala-Java interoperability testing
- **Sufficient disk space** - Test cases and output can consume significant space during extended fuzzing sessions

### Installing Compilers

**Kotlin:**
```bash
# Using SDKMAN! (recommended)
sdk install kotlin

# Or download from https://github.com/JetBrains/kotlin/releases
```

**Scala:**
```bash
# Using SDKMAN!
sdk install scala

# Or download from https://www.scala-lang.org/download/
```

## Building the Tool

The fuzzer includes a simple build script that compiles all source files and creates a JAR:

```bash
cd jvm-cross-lang-fuzzer
./build.sh
```

This will:
- Compile all Java source files
- Create a `build/` directory
- Generate `jvm-cross-lang-fuzzer.jar`

The build script automatically handles classpath and dependency management.

## Running the Fuzzer

### Basic Usage

```bash
./run.sh
```

This runs the fuzzer with default settings:
- Output directory: `fuzz-output/`
- Maximum test cases: 1000
- Verbose logging: disabled

### Custom Parameters

```bash
java -jar build/jvm-cross-lang-fuzzer.jar \
  --output /path/to/output \
  --max-test-cases 5000 \
  --verbose
```

## Command Line Options

| Option | Short | Description | Default |
|--------|-------|-------------|---------|
| `--output` | `-o` | Output directory for test cases and reports | `fuzz-output` |
| `--max-test-cases` | `-n` | Maximum number of test cases to generate | `1000` |
| `--verbose` | `-v` | Enable verbose logging | `false` |
| `--help` | `-h` | Show help message | N/A |

## Understanding Output

### Directory Structure

After running, the output directory contains:

```
fuzz-output/
├── defects/           # Defective test cases (if any found)
├── test-cases/        # All generated test cases
├── logs/              # Execution logs
└── summary.json       # Summary report
```

### Log Messages

- **INFO**: Normal operation messages
- **WARNING**: Non-critical issues (e.g., compilation warnings)
- **SEVERE**: Potential defects detected

### Defect Reports

When a defect is detected, a report file is created in `defects/` containing:
- Language pair (kotlin-java or scala-java)
- Test case ID
- Compilation/execution results
- Full source code of the defective test case

## Customizing Test Generation

The fuzzer generates random but syntactically valid cross-language code. You can influence generation by:

### Modifying Generators

The test case generators are located in:
- `src/main/java/com/example/fuzzer/generators/KotlinGenerator.java`
- `src/main/java/com/example/fuzzer/generators/ScalaGenerator.java`

You can modify these to:
- Add new language features to test
- Focus on specific interoperability scenarios
- Increase/decrease complexity of generated code

### Adding New Language Pairs

To add support for additional JVM language combinations:

1. Create a new generator class extending the base generator pattern
2. Update `JVMCrossLangFuzzer.java` to include your generator
3. Update the build script if necessary

## Defect Detection

The fuzzer detects defects through several mechanisms:

### Compilation Defects
- Unexpected compilation failures
- Compiler crashes
- Inconsistent error messages between languages

### Runtime Defects
- Unexpected runtime exceptions
- Different behavior between equivalent implementations
- Memory leaks or performance issues

### Semantic Inconsistencies
- Same code producing different results in different language contexts
- Type system inconsistencies
- Method resolution differences

## Troubleshooting

### Common Issues

**"Command not found: kotlinc"**
- Install Kotlin compiler as described in prerequisites

**"Command not found: scalac"**
- Install Scala compiler as described in prerequisites

**"Permission denied" on build/run scripts**
- Make scripts executable: `chmod +x build.sh run.sh`

**"Out of memory" errors**
- Increase JVM heap size: `export JAVA_OPTS="-Xmx4g"`

### Debugging Defective Test Cases

When a defect is found:
1. Examine the defect report in `fuzz-output/defects/`
2. Reproduce manually using the provided source code
3. Verify the defect is consistent and not environment-specific
4. Report to the appropriate compiler team with the minimal reproducer

### Performance Optimization

For large-scale fuzzing:
- Use SSD storage for faster I/O
- Increase available RAM
- Consider running multiple instances with different seed values
- Monitor disk space usage during extended sessions

## Advanced Usage

### Integration with CI/CD

You can integrate the fuzzer into your continuous integration pipeline:

```yaml
# GitHub Actions example
- name: Run JVM Cross-Language Fuzzer
  run: |
    cd jvm-cross-lang-fuzzer
    ./build.sh
    ./run.sh --max-test-cases 100
    # Fail build if defects found
    if [ -d "fuzz-output/defects" ] && [ "$(ls -A fuzz-output/defects)" ]; then
      echo "Defects found!" >&2
      exit 1
    fi
```

### Automated Bug Reporting

The fuzzer can be extended to automatically report bugs to issue trackers by:
1. Parsing defect reports
2. Creating standardized bug reports
3. Submitting via API to compiler project issue trackers

### Fuzzing Campaigns

For systematic testing:
- Run multiple sessions with different parameters
- Track defect discovery over time
- Correlate findings with compiler versions
- Build a database of known interoperability issues

## Contributing

Contributions are welcome! Consider:
- Adding support for additional JVM languages
- Improving test case generation algorithms
- Enhancing defect detection heuristics
- Adding new output formats or integrations

## License

This tool is provided under the MIT License. See LICENSE file for details.

---

**Note**: This fuzzer is designed to find compiler and runtime defects. Always verify findings before reporting bugs to compiler teams.