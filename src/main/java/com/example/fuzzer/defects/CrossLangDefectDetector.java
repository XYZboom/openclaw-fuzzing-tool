package com.example.fuzzer.defects;

import com.example.fuzzer.model.TestCase;
import com.example.fuzzer.model.CompilationResult;
import com.example.fuzzer.model.ExecutionResult;
import com.example.fuzzer.model.DefectReport;

import java.util.logging.Logger;

/**
 * CrossLangDefectDetector analyzes compilation and execution results to detect
 * potential compiler defects and interoperability issues between JVM languages.
 */
public class CrossLangDefectDetector {
    private static final Logger logger = Logger.getLogger(CrossLangDefectDetector.class.getName());
    
    /**
     * Detects if a compilation result indicates a potential compiler defect.
     * 
     * @param compileResult The compilation result to analyze
     * @param testCase The test case that was compiled
     * @return true if a defect is detected, false otherwise
     */
    public boolean detectCompilationDefect(CompilationResult compileResult, TestCase testCase) {
        if (compileResult.isSuccess()) {
            return false; // Successful compilation is not a defect
        }
        
        String errorMessage = compileResult.getErrorMessage();
        
        // Look for patterns that indicate compiler bugs rather than syntax errors
        String[] compilerBugPatterns = {
            "Internal error",
            "Assertion failed",
            "NullPointerException",
            "ClassCastException",
            "StackOverflowError",
            "OutOfMemoryError",
            "compiler crashed",
            "kotlin-compiler.jar",
            "scalac error",
            "javac internal error"
        };
        
        for (String pattern : compilerBugPatterns) {
            if (errorMessage != null && errorMessage.contains(pattern)) {
                logger.warning("Potential compiler defect detected: " + pattern);
                return true;
            }
        }
        
        // For now, treat any unexpected compilation failure as a potential defect
        // In a real implementation, we would be more sophisticated about this
        return true;
    }
    
    /**
     * Detects if an execution result indicates a potential runtime defect.
     * 
     * @param execResult The execution result to analyze
     * @param testCase The test case that was executed
     * @return true if a defect is detected, false otherwise
     */
    public boolean detectRuntimeDefect(ExecutionResult execResult, TestCase testCase) {
        if (execResult.isSuccess()) {
            return false; // Successful execution is not a defect
        }
        
        String errorMessage = execResult.getErrorMessage();
        
        // Look for patterns that indicate runtime system bugs
        String[] runtimeBugPatterns = {
            "VerifyError",
            "IncompatibleClassChangeError",
            "LinkageError",
            "NoSuchMethodError",
            "NoSuchFieldError",
            "AbstractMethodError"
        };
        
        for (String pattern : runtimeBugPatterns) {
            if (errorMessage != null && errorMessage.contains(pattern)) {
                logger.warning("Potential runtime defect detected: " + pattern);
                return true;
            }
        }
        
        return false;
    }
}