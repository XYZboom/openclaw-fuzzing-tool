package com.example.fuzzer.generators;

import com.example.fuzzer.model.TestCase;
import java.util.List;

/**
 * Base class for all test case generators.
 */
public abstract class BaseGenerator {
    /**
     * Generates a list of test cases.
     * 
     * @param count Number of test cases to generate
     * @return List of generated test cases
     */
    public abstract List<TestCase> generateTestCases(int count);
    
    /**
     * Generates a single test case with the given ID.
     * 
     * @param testCaseId Unique identifier for the test case
     * @return Generated test case
     */
    public abstract TestCase generateTestCase(String testCaseId);
}