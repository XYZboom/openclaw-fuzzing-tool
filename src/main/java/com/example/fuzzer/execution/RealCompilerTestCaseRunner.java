package com.example.fuzzer.execution;

import com.example.fuzzer.defects.CrossLangDefectDetector;
import com.example.fuzzer.model.TestCase;
import com.example.fuzzer.model.TestCaseResult;
import com.example.fuzzer.model.CompilationResult;
import com.example.fuzzer.model.ExecutionResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * RealCompilerTestCaseRunner executes generated cross-language test cases using actual compilers.
 * This class handles real compilation with javac, kotlinc, and scalac, then executes the results.
 */
public class RealCompilerTestCaseRunner {
    private static final Logger logger = Logger.getLogger(RealCompilerTestCaseRunner.class.getName());
    
    private final CrossLangDefectDetector defectDetector;
    private final ExecutorService executorService;
    private final Path workingDirectory;
    private final long timeoutSeconds;
    
    public RealCompilerTestCaseRunner() {
        this.defectDetector = new CrossLangDefectDetector();
        this.executorService = Executors.newFixedThreadPool(4);
        this.workingDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "jvm_fuzzer_tests");
        this.timeoutSeconds = 30;
        
        // Ensure working directory exists
        try {
            Files.createDirectories(workingDirectory);
        } catch (IOException e) {
            logger.severe("Failed to create working directory: " + e.getMessage());
        }
    }
    
    /**
     * Runs a single test case with real compilation and execution.
     */
    public TestCaseResult runTestCase(TestCase testCase) {
        String testCaseId = testCase.getId();
        Path testCaseDir = workingDirectory.resolve(testCaseId);
        
        try {
            // Create test case directory
            Files.createDirectories(testCaseDir);
            
            // Write source files
            writeSourceFiles(testCaseDir, testCase);
            
            // Compile the test case with real compilers
            CompilationResult compileResult = realCompileTestCase(testCaseDir, testCase);
            if (!compileResult.isSuccess()) {
                // Check if compilation failure indicates a defect
                boolean isDefect = defectDetector.detectCompilationDefect(compileResult, testCase);
                return new TestCaseResult(testCaseId, false, isDefect, compileResult.getErrorMessage());
            }
            
            // Execute the compiled test case
            ExecutionResult execResult = realExecuteTestCase(testCaseDir, testCase);
            if (!execResult.isSuccess()) {
                // Check if execution failure indicates a runtime defect
                boolean isDefect = defectDetector.detectRuntimeDefect(execResult, testCase);
                return new TestCaseResult(testCaseId, false, isDefect, execResult.getErrorMessage());
            }
            
            // Test case executed successfully - no defects found
            return new TestCaseResult(testCaseId, true, false, "Success");
            
        } catch (Exception e) {
            logger.warning("Exception running test case " + testCaseId + ": " + e.getMessage());
            return new TestCaseResult(testCaseId, false, true, "Exception: " + e.getMessage());
        } finally {
            // Clean up test case directory (optional - keep for debugging if needed)
            cleanupTestCaseDirectory(testCaseDir);
        }
    }
    
    /**
     * Compiles test case using real compilers (javac, kotlinc, scalac).
     */
    private CompilationResult realCompileTestCase(Path testCaseDir, TestCase testCase) {
        try {
            boolean hasJava = !testCase.getJavaFiles().isEmpty();
            boolean hasKotlin = !testCase.getKotlinFiles().isEmpty();
            boolean hasScala = !testCase.getScalaFiles().isEmpty();
            
            if (hasKotlin && hasJava) {
                return compileKotlinJava(testCaseDir, testCase);
            } else if (hasScala && hasJava) {
                return compileScalaJava(testCaseDir, testCase);
            } else if (hasJava) {
                return compileJavaOnly(testCaseDir, testCase);
            } else if (hasKotlin) {
                return compileKotlinOnly(testCaseDir, testCase);
            } else if (hasScala) {
                return compileScalaOnly(testCaseDir, testCase);
            } else {
                return new CompilationResult(false, "No source files to compile");
            }
            
        } catch (Exception e) {
            return new CompilationResult(false, "Compilation exception: " + e.getMessage());
        }
    }
    
    /**
     * Executes compiled test case.
     */
    private ExecutionResult realExecuteTestCase(Path testCaseDir, TestCase testCase) {
        try {
            // Find main class to execute
            String mainClass = findMainClass(testCase);
            if (mainClass == null) {
                return new ExecutionResult(true, "No main class found - compilation only");
            }
            
            // Execute the compiled class
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", testCaseDir.toString(), mainClass);
            pb.directory(testCaseDir.toFile());
            Process process = pb.start();
            
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return new ExecutionResult(false, "Execution timeout after " + timeoutSeconds + " seconds");
            }
            
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                return new ExecutionResult(true, "Success");
            } else {
                String errorOutput = readProcessOutput(process.getErrorStream());
                return new ExecutionResult(false, "Execution failed with exit code " + exitCode + ": " + errorOutput);
            }
            
        } catch (Exception e) {
            return new ExecutionResult(false, "Execution error: " + e.getMessage());
        }
    }
    
    private CompilationResult compileKotlinJava(Path testCaseDir, TestCase testCase) {
        try {
            // Use kotlinc to compile both Kotlin and Java files together
            List<String> command = new ArrayList<>();
            command.add("kotlinc");
            command.add("-include-runtime");
            command.add("-d");
            command.add("test.jar");
            
            // Add all Kotlin and Java files
            for (String fileName : testCase.getKotlinFiles().keySet()) {
                command.add(fileName);
            }
            for (String fileName : testCase.getJavaFiles().keySet()) {
                command.add(fileName);
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(testCaseDir.toFile());
            Process process = pb.start();
            
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return new CompilationResult(false, "Kotlin-Java compilation timeout");
            }
            
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                return new CompilationResult(true, "Success");
            } else {
                String errorOutput = readProcessOutput(process.getErrorStream());
                return new CompilationResult(false, "Kotlin-Java compilation failed: " + errorOutput);
            }
            
        } catch (Exception e) {
            return new CompilationResult(false, "Kotlin-Java compilation error: " + e.getMessage());
        }
    }
    
    private CompilationResult compileScalaJava(Path testCaseDir, TestCase testCase) {
        try {
            // Use scalac to compile both Scala and Java files together
            List<String> command = new ArrayList<>();
            command.add("scalac");
            
            // Add all Scala and Java files
            for (String fileName : testCase.getScalaFiles().keySet()) {
                command.add(fileName);
            }
            for (String fileName : testCase.getJavaFiles().keySet()) {
                command.add(fileName);
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(testCaseDir.toFile());
            Process process = pb.start();
            
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return new CompilationResult(false, "Scala-Java compilation timeout");
            }
            
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                return new CompilationResult(true, "Success");
            } else {
                String errorOutput = readProcessOutput(process.getErrorStream());
                return new CompilationResult(false, "Scala-Java compilation failed: " + errorOutput);
            }
            
        } catch (Exception e) {
            return new CompilationResult(false, "Scala-Java compilation error: " + e.getMessage());
        }
    }
    
    private CompilationResult compileJavaOnly(Path testCaseDir, TestCase testCase) {
        try {
            List<String> command = new ArrayList<>();
            command.add("javac");
            for (String fileName : testCase.getJavaFiles().keySet()) {
                command.add(fileName);
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(testCaseDir.toFile());
            Process process = pb.start();
            
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return new CompilationResult(false, "Java compilation timeout");
            }
            
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                return new CompilationResult(true, "Success");
            } else {
                String errorOutput = readProcessOutput(process.getErrorStream());
                return new CompilationResult(false, "Java compilation failed: " + errorOutput);
            }
            
        } catch (Exception e) {
            return new CompilationResult(false, "Java compilation error: " + e.getMessage());
        }
    }
    
    private CompilationResult compileKotlinOnly(Path testCaseDir, TestCase testCase) {
        try {
            List<String> command = new ArrayList<>();
            command.add("kotlinc");
            command.add("-include-runtime");
            command.add("-d");
            command.add("test.jar");
            for (String fileName : testCase.getKotlinFiles().keySet()) {
                command.add(fileName);
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(testCaseDir.toFile());
            Process process = pb.start();
            
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return new CompilationResult(false, "Kotlin compilation timeout");
            }
            
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                return new CompilationResult(true, "Success");
            } else {
                String errorOutput = readProcessOutput(process.getErrorStream());
                return new CompilationResult(false, "Kotlin compilation failed: " + errorOutput);
            }
            
        } catch (Exception e) {
            return new CompilationResult(false, "Kotlin compilation error: " + e.getMessage());
        }
    }
    
    private CompilationResult compileScalaOnly(Path testCaseDir, TestCase testCase) {
        try {
            List<String> command = new ArrayList<>();
            command.add("scalac");
            for (String fileName : testCase.getScalaFiles().keySet()) {
                command.add(fileName);
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(testCaseDir.toFile());
            Process process = pb.start();
            
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return new CompilationResult(false, "Scala compilation timeout");
            }
            
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                return new CompilationResult(true, "Success");
            } else {
                String errorOutput = readProcessOutput(process.getErrorStream());
                return new CompilationResult(false, "Scala compilation failed: " + errorOutput);
            }
            
        } catch (Exception e) {
            return new CompilationResult(false, "Scala compilation error: " + e.getMessage());
        }
    }
    
    private String findMainClass(TestCase testCase) {
        // Look for main classes in different languages
        if (!testCase.getJavaFiles().isEmpty()) {
            for (String fileName : testCase.getJavaFiles().keySet()) {
                if (fileName.contains("Main") || fileName.equals("Main.java")) {
                    return fileName.replace(".java", "");
                }
            }
        }
        
        if (!testCase.getKotlinFiles().isEmpty()) {
            for (String fileName : testCase.getKotlinFiles().keySet()) {
                if (fileName.contains("Main") || fileName.equals("Main.kt")) {
                    return fileName.replace(".kt", "") + "Kt";
                }
            }
        }
        
        if (!testCase.getScalaFiles().isEmpty()) {
            for (String fileName : testCase.getScalaFiles().keySet()) {
                if (fileName.contains("Main") || fileName.equals("Main.scala")) {
                    return fileName.replace(".scala", "");
                }
            }
        }
        
        return null;
    }
    
    private void writeSourceFiles(Path testCaseDir, TestCase testCase) throws IOException {
        // Write Java files
        for (String fileName : testCase.getJavaFiles().keySet()) {
            Path javaFile = testCaseDir.resolve(fileName);
            Files.write(javaFile, testCase.getJavaFiles().get(fileName).getBytes());
        }
        
        // Write Kotlin files
        for (String fileName : testCase.getKotlinFiles().keySet()) {
            Path kotlinFile = testCaseDir.resolve(fileName);
            Files.write(kotlinFile, testCase.getKotlinFiles().get(fileName).getBytes());
        }
        
        // Write Scala files
        for (String fileName : testCase.getScalaFiles().keySet()) {
            Path scalaFile = testCaseDir.resolve(fileName);
            Files.write(scalaFile, testCase.getScalaFiles().get(fileName).getBytes());
        }
    }
    
    private String readProcessOutput(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString().trim();
    }
    
    private void cleanupTestCaseDirectory(Path testCaseDir) {
        try {
            if (!Boolean.getBoolean("jvm.fuzzer.debug")) {
                Files.walk(testCaseDir)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            logger.warning("Failed to delete " + path + ": " + e.getMessage());
                        }
                    });
            }
        } catch (Exception e) {
            logger.warning("Failed to cleanup test case directory: " + e.getMessage());
        }
    }
    
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}