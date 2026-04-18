package com.example.fuzzer.model;

/**
 * Represents the result of executing a test case.
 */
public class ExecutionResult {
    private final boolean success;
    private final String errorMessage;
    
    public ExecutionResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}