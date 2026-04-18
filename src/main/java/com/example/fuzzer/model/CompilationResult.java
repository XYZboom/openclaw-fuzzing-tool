package com.example.fuzzer.model;

/**
 * Represents the result of compiling a test case.
 */
public class CompilationResult {
    private final boolean success;
    private final String errorMessage;
    
    public CompilationResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}