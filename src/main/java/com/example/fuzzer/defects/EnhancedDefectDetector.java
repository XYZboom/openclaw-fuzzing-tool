package com.example.fuzzer.defects;

import com.example.fuzzer.model.TestCaseResult;
import com.example.fuzzer.model.CompilationResult;
import com.example.fuzzer.model.ExecutionResult;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Enhanced defect detector that analyzes complex cross-language test cases
 * for subtle compiler and runtime defects.
 */
public class EnhancedDefectDetector {
    private static final Logger logger = Logger.getLogger(EnhancedDefectDetector.class.getName());
    
    // Patterns for detecting known compiler error messages vs unexpected crashes
    private static final Pattern KNOWN_COMPILATION_ERRORS = Pattern.compile(
        ".*cannot find symbol.*|" +
        ".*incompatible types.*|" +
        ".*missing return statement.*|" +
        ".*unreachable statement.*|" +
        ".*variable .* might not have been initialized.*"
    );
    
    private static final Pattern COMPILER_CRASH_PATTERNS = Pattern.compile(
        ".*Internal compiler error.*|" +
        ".*java.lang.NullPointerException.*|" +
        ".*java.lang.AssertionError.*|" +
        ".*StackOverflowError.*|" +
        ".*OutOfMemoryError.*|" +
        ".*Exception in thread.*compiler.*"
    );
    
    public EnhancedDefectDetector() {
        // Default constructor
    }
    
    /**
     * Detects if a compilation result indicates a compiler defect.
     */
    public boolean detectCompilationDefect(CompilationResult result, String testCaseId) {
        if (result == null || result.getErrorMessage() == null) {
            return false;
        }
        
        String error = result.getErrorMessage();
        
        // If it's a known compilation error, it's probably not a defect
        if (KNOWN_COMPILATION_ERRORS.matcher(error).matches()) {
            return false;
        }
        
        // If it looks like a compiler crash, it's likely a defect
        if (COMPILER_CRASH_PATTERNS.matcher(error).matches()) {
            logger.severe("Potential compiler defect detected in test case " + testCaseId + 
                         ": " + error);
            return true;
        }
        
        // If compilation failed but doesn't match known patterns, could be a defect
        if (!result.isSuccess()) {
            logger.warning("Unusual compilation failure in test case " + testCaseId + 
                          ": " + error);
            return true;
        }
        
        return false;
    }
    
    /**
     * Detects if an execution result indicates a runtime defect.
     */
    public boolean detectRuntimeDefect(ExecutionResult result, String testCaseId) {
        if (result == null || result.getErrorMessage() == null) {
            return false;
        }
        
        String error = result.getErrorMessage();
        
        // Look for JVM crashes or unexpected runtime errors
        if (error.contains("SIGSEGV") || 
            error.contains("Fatal error") ||
            error.contains("JVM crash") ||
            error.contains("Internal Error")) {
            logger.severe("Potential JVM/runtime defect detected in test case " + testCaseId + 
                         ": " + error);
            return true;
        }
        
        // Unexpected exceptions during normal operation could indicate defects
        if (!result.isSuccess() && 
            (error.contains("NullPointerException") || 
             error.contains("ClassCastException") || 
             error.contains("NoSuchMethodError"))) {
            logger.warning("Potential runtime defect detected in test case " + testCaseId + 
                          ": " + error);
            return true;
        }
        
        return false;
    }
    
    /**
     * Analyzes a test case result for defects.
     */
    public boolean detectDefect(TestCaseResult result) {
        if (result == null) {
            return false;
        }
        
        // If the test case is marked as a defect, return true
        if (result.isDefect()) {
            return true;
        }
        
        // Additional analysis can be added here
        return false;
    }
}