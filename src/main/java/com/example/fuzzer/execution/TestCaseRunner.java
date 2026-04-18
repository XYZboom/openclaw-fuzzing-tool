package com.example.fuzzer.execution;

import com.example.fuzzer.defects.CrossLangDefectDetector;
import com.example.fuzzer.model.TestCase;
import com.example.fuzzer.model.TestCaseResult;
import com.example.fuzzer.model.CompilationResult;
import com.example.fuzzer.model.ExecutionResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * TestCaseRunner executes generated cross-language test cases and monitors for compiler/runtime defects.
 * This class handles the compilation, execution, and defect detection pipeline.
 */
public class TestCaseRunner {
    private static final Logger logger = Logger.getLogger(TestCaseRunner.class.getName());
    
    private final CrossLangDefectDetector defectDetector;
    private final ExecutorService executorService;
    private final Path workingDirectory;
    private final long timeoutSeconds;
    
    public TestCaseRunner() {
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
     * Runs a single test case and returns the result.
     */
    public TestCaseResult runTestCase(TestCase testCase) {
        String testCaseId = testCase.getId();
        Path testCaseDir = workingDirectory.resolve(testCaseId);
        
        try {
            // Create test case directory
            Files.createDirectories(testCaseDir);
            
            // Write source files
            writeSourceFiles(testCaseDir, testCase);
            
            // Compile the test case
            CompilationResult compileResult = compileTestCase(testCaseDir, testCase);
            if (!compileResult.isSuccess()) {
                // Check if compilation failure is expected or indicates a defect
                boolean isDefect = defectDetector.detectCompilationDefect(compileResult, testCase);
                return new TestCaseResult(testCaseId, false, isDefect, compileResult.getErrorMessage());
            }
            
            // Execute the test case
            ExecutionResult execResult = executeTestCase(testCaseDir, testCase);
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
     * Runs multiple test cases concurrently.
     */
    public List<TestCaseResult> runTestCases(List<TestCase> testCases) {
        List<Future<TestCaseResult>> futures = new ArrayList<>();
        List<TestCaseResult> results = new ArrayList<>();
        
        // Submit all test cases for execution
        for (TestCase testCase : testCases) {
            Future<TestCaseResult> future = executorService.submit(() -> runTestCase(testCase));
            futures.add(future);
        }
        
        // Collect results with timeout
        for (Future<TestCaseResult> future : futures) {
            try {
                TestCaseResult result = future.get(timeoutSeconds, TimeUnit.SECONDS);
                results.add(result);
            } catch (Exception e) {
                logger.warning("Test case execution timed out or failed: " + e.getMessage());
                // Create a timeout result
                results.add(new TestCaseResult("unknown", false, true, "Timeout or execution error"));
            }
        }
        
        return results;
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
    
    private CompilationResult compileTestCase(Path testCaseDir, TestCase testCase) {
        try {
            // For now, just return success since we don't have actual compilers
            // In a real implementation, this would call javac, kotlinc, scalac, etc.
            return new CompilationResult(true, "Success");
            
        } catch (Exception e) {
            return new CompilationResult(false, "Compilation exception: " + e.getMessage());
        }
    }
    
    private ExecutionResult executeTestCase(Path testCaseDir, TestCase testCase) {
        try {
            // For now, just return success since we don't have actual execution
            // In a real implementation, this would execute the compiled code
            return new ExecutionResult(true, "Success");
            
        } catch (Exception e) {
            return new ExecutionResult(false, "Execution error: " + e.getMessage());
        }
    }
    
    private void cleanupTestCaseDirectory(Path testCaseDir) {
        try {
            // Only clean up if not in debug mode
            if (!Boolean.getBoolean("jvm.fuzzer.debug")) {
                Files.walk(testCaseDir)
                    .sorted((a, b) -> -a.compareTo(b)) // Delete files before directories
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