package com.example.fuzzer.model;

/**
 * Represents the result of executing a test case.
 */
public class TestCaseResult {
    private final String testCaseId;
    private final boolean success;
    private final boolean isDefect;
    private final String message;
    
    public TestCaseResult(String testCaseId, boolean success, boolean isDefect, String message) {
        this.testCaseId = testCaseId;
        this.success = success;
        this.isDefect = isDefect;
        this.message = message;
    }
    
    public String getTestCaseId() { return testCaseId; }
    public boolean isSuccess() { return success; }
    public boolean isDefect() { return isDefect; }
    public String getMessage() { return message; }
}