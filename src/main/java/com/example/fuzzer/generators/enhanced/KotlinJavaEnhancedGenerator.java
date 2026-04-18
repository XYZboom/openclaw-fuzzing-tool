package com.example.fuzzer.generators.enhanced;

import com.example.fuzzer.generators.BaseGenerator;
import com.example.fuzzer.model.TestCase;
import com.example.fuzzer.ir.CrossLangIR;
import com.example.fuzzer.ir.TypeSystem;
import com.example.fuzzer.generators.ir.IRGenerator;

import java.util.*;

/**
 * Enhanced generator for Kotlin-Java interoperability test cases.
 * Uses intermediate representation to generate complex cross-language scenarios.
 */
public class KotlinJavaEnhancedGenerator extends BaseGenerator {
    private final IRGenerator irGenerator;
    
    public KotlinJavaEnhancedGenerator() {
        this.irGenerator = new IRGenerator();
    }
    
    @Override
    public List<TestCase> generateTestCases(int count) {
        List<TestCase> testCases = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            TestCase testCase = generateKotlinJavaTestCase("kotlin_java_test_" + i);
            if (testCase != null) {
                testCases.add(testCase);
            }
        }
        
        return testCases;
    }
    
    private TestCase generateKotlinJavaTestCase(String testCaseId) {
        try {
            // Generate IR for Kotlin-Java scenario
            CrossLangIR ir = irGenerator.generateKotlinJavaIR();
            
            if (ir == null) {
                return null;
            }
            
            // Create test case
            TestCase testCase = new TestCase(testCaseId, "Main");
            
            // Generate source code from IR
            Map<String, String> sources = ir.generateSourceCode(testCaseId);
            
            // Add Java source
            if (sources.containsKey("java")) {
                testCase.addJavaFile("Main.java", sources.get("java"));
            }
            
            // Add Kotlin source
            if (sources.containsKey("kotlin")) {
                testCase.addKotlinFile("Main.kt", sources.get("kotlin"));
            }
            
            return testCase;
            
        } catch (Exception e) {
            System.err.println("Error generating Kotlin-Java test case: " + e.getMessage());
            return null;
        }
    }
}