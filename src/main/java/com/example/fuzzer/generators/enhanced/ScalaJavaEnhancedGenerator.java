package com.example.fuzzer.generators.enhanced;

import com.example.fuzzer.generators.BaseGenerator;
import com.example.fuzzer.model.TestCase;
import com.example.fuzzer.ir.CrossLangIR;
import com.example.fuzzer.ir.TypeSystem;
import com.example.fuzzer.generators.GeneratorUtils;

import java.util.*;

/**
 * Enhanced generator for Scala-Java interoperability test cases.
 * Uses intermediate representation to generate complex cross-language scenarios.
 */
public class ScalaJavaEnhancedGenerator extends BaseGenerator {
    
    public ScalaJavaEnhancedGenerator() {
        super();
    }
    
    /**
     * Generates enhanced Scala-Java test cases with increased complexity.
     */
    public List<TestCase> generateTestCases(int count) {
        List<TestCase> testCases = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            String testCaseId = "scala_java_enhanced_" + System.currentTimeMillis() + "_" + i;
            TestCase testCase = generateTestCase(testCaseId, i % 3 + 1); // Complexity levels 1-3
            testCases.add(testCase);
        }
        
        return testCases;
    }
    
    /**
     * Generates a single enhanced Scala-Java test case.
     */
    private TestCase generateTestCase(String testCaseId, int complexityLevel) {
        // Create IR model
        CrossLangIR ir = new CrossLangIR("scala_java_test", TypeSystem.Language.SCALA, TypeSystem.Language.JAVA);
        
        // Add basic types
        ir.addType("String");
        ir.addType("Int");
        ir.addType("Boolean");
        ir.addType("Double");
        
        // Add generic types based on complexity
        if (complexityLevel >= 2) {
            ir.addGenericType("List", Arrays.asList("T"));
            ir.addGenericType("Map", Arrays.asList("K", "V"));
        }
        
        // Add custom types and cross-language constructs
        addCrossLanguageConstructs(ir, complexityLevel);
        
        // Generate source code
        Map<String, String> sources = ir.generateSourceCode(testCaseId);
        
        // Create test case
        TestCase testCase = new TestCase(testCaseId, "Main");
        testCase.addJavaFile("Main.java", sources.get("java"));
        testCase.addScalaFile("Main.scala", sources.get("scala"));
        
        return testCase;
    }
    
    /**
     * Adds cross-language constructs based on complexity level.
     */
    private void addCrossLanguageConstructs(CrossLangIR ir, int complexityLevel) {
        // Add basic inheritance
        ir.createType(TypeSystem.Language.JAVA, "BaseClass", null, new HashMap<String, Object>());
        ir.createType(TypeSystem.Language.SCALA, "DerivedClass", "BaseClass", new HashMap<String, Object>());
        
        // Add interfaces/traits
        ir.createType(TypeSystem.Language.JAVA, "JavaInterface", null, new HashMap<String, Object>());
        ir.createType(TypeSystem.Language.SCALA, "ScalaTrait", null, new HashMap<String, Object>());
        
        // Add methods
        ir.createFunction(TypeSystem.Language.JAVA, "Main", "javaMethod", "String", 
                         Arrays.asList("String param"), "return param;");
        ir.createFunction(TypeSystem.Language.SCALA, "Main", "scalaMethod", "String", 
                         Arrays.asList("param: String"), "param");
        
        // Add complex features based on complexity level
        if (complexityLevel >= 2) {
            // Add generics
            Map<String, Object> genericProps = new HashMap<>();
            genericProps.put("generic", true);
            genericProps.put("typeParams", Arrays.asList("T", "U"));
            ir.createType(TypeSystem.Language.JAVA, "GenericContainer", null, genericProps);
            
            // Add companion objects
            ir.createCompanionObject("ScalaUtil");
        }
        
        if (complexityLevel >= 3) {
            // Add implicits
            ir.createImplicitConversion("String", "RichString");
            
            // Add case classes
            Map<String, Object> caseProps = new HashMap<>();
            caseProps.put("caseClass", true);
            caseProps.put("fields", Arrays.asList("name: String", "age: Int"));
            ir.createType(TypeSystem.Language.SCALA, "Person", null, caseProps);
            
            // Add varargs
            ir.createFunction(TypeSystem.Language.JAVA, "Main", "varargsMethod", "void", 
                             Arrays.asList("String... args"), "// Process varargs");
        }
        
        // Add main method
        ir.createFunction(TypeSystem.Language.JAVA, "Main", "main", "void", 
                         Arrays.asList("String[] args"), "System.out.println(\"Running enhanced test\");");
    }
}