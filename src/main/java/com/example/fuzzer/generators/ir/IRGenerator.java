package com.example.fuzzer.generators.ir;

import com.example.fuzzer.ir.TypeSystem;
import com.example.fuzzer.ir.CrossLangIR;
import com.example.fuzzer.model.TestCase;

import java.util.*;
import java.util.logging.Logger;

/**
 * IRGenerator creates intermediate representations for cross-language test cases.
 * This generator uses the type system model to create complex interoperability scenarios
 * that can expose compiler defects in JVM language implementations.
 */
public class IRGenerator {
    private static final Logger logger = Logger.getLogger(IRGenerator.class.getName());
    
    private final TypeSystem typeSystem;
    
    public IRGenerator(TypeSystem typeSystem) {
        this.typeSystem = typeSystem;
    }
    
    /**
     * Generates a Kotlin-Java interoperability test case IR.
     */
    public TestCase generateKotlinJavaTestCase(String testCaseId, int complexityLevel) {
        CrossLangIR ir = new CrossLangIR("kotlin-java-test-" + testCaseId, 
                                        TypeSystem.Language.KOTLIN, TypeSystem.Language.JAVA);
        
        // Add basic types
        addBasicTypes(ir);
        
        // Add inheritance scenarios
        addBasicInheritance(ir, TypeSystem.Language.KOTLIN, TypeSystem.Language.JAVA);
        
        // Add generic scenarios based on complexity
        if (complexityLevel >= 2) {
            addGenericScenarios(ir, TypeSystem.Language.KOTLIN, TypeSystem.Language.JAVA);
        }
        
        // Add advanced features based on complexity
        if (complexityLevel >= 3) {
            addAdvancedFeatures(ir, TypeSystem.Language.KOTLIN, TypeSystem.Language.JAVA);
        }
        
        // Add complex interoperability scenarios based on complexity
        if (complexityLevel >= 4) {
            addComplexInteroperability(ir, TypeSystem.Language.KOTLIN, TypeSystem.Language.JAVA);
        }
        
        // Add main method
        addMainMethod(ir, TypeSystem.Language.KOTLIN, TypeSystem.Language.JAVA);
        
        // Generate source code
        Map<String, String> sources = ir.generateSourceCode(testCaseId);
        
        // Create test case
        TestCase testCase = new TestCase(testCaseId, "Main");
        testCase.addJavaFile("Main.java", sources.get("java"));
        testCase.addKotlinFile("Main.kt", sources.get("kotlin"));
        
        return testCase;
    }
    
    /**
     * Generates a Scala-Java interoperability test case IR.
     */
    public TestCase generateScalaJavaTestCase(String testCaseId, int complexityLevel) {
        CrossLangIR ir = new CrossLangIR("scala-java-test-" + testCaseId,
                                        TypeSystem.Language.SCALA, TypeSystem.Language.JAVA);
        
        // Add basic types
        addBasicTypes(ir);
        
        // Add inheritance scenarios
        addBasicInheritance(ir, TypeSystem.Language.SCALA, TypeSystem.Language.JAVA);
        
        // Add generic scenarios based on complexity
        if (complexityLevel >= 2) {
            addGenericScenarios(ir, TypeSystem.Language.SCALA, TypeSystem.Language.JAVA);
        }
        
        // Add advanced features based on complexity
        if (complexityLevel >= 3) {
            addAdvancedFeatures(ir, TypeSystem.Language.SCALA, TypeSystem.Language.JAVA);
        }
        
        // Add complex interoperability scenarios based on complexity
        if (complexityLevel >= 4) {
            addComplexInteroperability(ir, TypeSystem.Language.SCALA, TypeSystem.Language.JAVA);
        }
        
        // Add main method
        addMainMethod(ir, TypeSystem.Language.SCALA, TypeSystem.Language.JAVA);
        
        // Generate source code
        Map<String, String> sources = ir.generateSourceCode(testCaseId);
        
        // Create test case
        TestCase testCase = new TestCase(testCaseId, "Main");
        testCase.addJavaFile("Main.java", sources.get("java"));
        testCase.addScalaFile("Main.scala", sources.get("scala"));
        
        return testCase;
    }
    
    private void addBasicTypes(CrossLangIR ir) {
        // Add common types that work across languages
        ir.addTypeDefinition("String", "String");
        ir.addTypeDefinition("Int", "int");
        ir.addTypeDefinition("Boolean", "boolean");
        ir.addTypeDefinition("Double", "double");
        ir.addTypeDefinition("Long", "long");
    }
    
    private void addBasicInheritance(CrossLangIR ir, TypeSystem.Language lang1, TypeSystem.Language lang2) {
        // Create base classes in both languages
        String baseType1 = "Base" + capitalize(lang1.name());
        String baseType2 = "Base" + capitalize(lang2.name());
        
        ir.addTypeDefinition(baseType1, "class " + baseType1 + " { public String name; }");
        ir.addTypeDefinition(baseType2, "class " + baseType2 + " { public String name; }");
        
        // Create derived class that inherits from both
        String derivedType = "Derived" + capitalize(lang1.name());
        ir.addTypeDefinition(derivedType, "class " + derivedType + " extends " + baseType1 + " { }");
        
        // Add cross-language field access
        ir.addFieldAccess("derivedInstance." + baseType1 + ".name = \"test\"");
    }
    
    private void addGenericScenarios(CrossLangIR ir, TypeSystem.Language lang1, TypeSystem.Language lang2) {
        // Create generic container
        String genericType = "GenericContainer";
        ir.addTypeDefinition(genericType, "class " + genericType + "<T, U> { public T item1; public U item2; }");
        
        // Create generic method calls
        ir.addMethodCall("new " + genericType + "<String, Integer>()");
    }
    
    private void addAdvancedFeatures(CrossLangIR ir, TypeSystem.Language lang1, TypeSystem.Language lang2) {
        // Add nested classes
        String outerType = "OuterClass";
        String innerType = "InnerClass";
        ir.addTypeDefinition(outerType, "class " + outerType + " { public class " + innerType + " { } }");
        
        // Add functional interfaces (for Java 8+ compatibility)
        if (lang2 == TypeSystem.Language.JAVA) {
            ir.addTypeDefinition("Callback", "interface Callback { void onComplete(String result); }");
            ir.addMethodCall("processWithCallback(new Callback() { public void onComplete(String result) { } })");
        }
    }
    
    private void addComplexInteroperability(CrossLangIR ir, TypeSystem.Language lang1, TypeSystem.Language lang2) {
        // Add intersection types (Kotlin-specific)
        if (lang1 == TypeSystem.Language.KOTLIN) {
            ir.addTypeDefinition("IntersectionType", "interface A { fun methodA() }; interface B { fun methodB() }");
            ir.addMethodCall("fun processIntersection(obj: A & B) { obj.methodA(); obj.methodB() }");
        }
        
        // Add sealed classes and pattern matching (Kotlin/Scala specific)
        if (lang1 == TypeSystem.Language.KOTLIN) {
            ir.addTypeDefinition("sealed class Result { data class Success(val value: String) : Result(); data class Error(val message: String) : Result() }");
            ir.addMethodCall("when (result) { is Result.Success -> println(result.value); is Result.Error -> println(result.message) }");
        } else if (lang1 == TypeSystem.Language.SCALA) {
            ir.addTypeDefinition("sealed trait Result; case class Success(value: String) extends Result; case class Error(message: String) extends Result");
            ir.addMethodCall("result match { case Success(value) => println(value); case Error(message) => println(message) }");
        }
        
        // Add extension functions (Kotlin-specific)
        if (lang1 == TypeSystem.Language.KOTLIN) {
            ir.addMethodCall("fun String.reverse(): String = this.reversed()");
            ir.addMethodCall("\"hello\".reverse()");
        }
        
        // Add implicit conversions (Scala-specific)
        if (lang1 == TypeSystem.Language.SCALA) {
            ir.addMethodCall("implicit def stringToInt(s: String): Int = s.toInt");
            ir.addMethodCall("val x: Int = \"123\"");
        }
    }
    
    private void addMainMethod(CrossLangIR ir, TypeSystem.Language lang1, TypeSystem.Language lang2) {
        // Add main method appropriate for each language combination
        if (lang2 == TypeSystem.Language.JAVA) {
            ir.addMainMethod("public static void main(String[] args) { System.out.println(\"Test started\"); }");
        }
        
        if (lang1 == TypeSystem.Language.KOTLIN) {
            ir.addMainMethod("fun main(args: Array<String>) { println(\"Test started\") }");
        } else if (lang1 == TypeSystem.Language.SCALA) {
            ir.addMainMethod("def main(args: Array[String]): Unit = { println(\"Test started\") }");
        }
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}