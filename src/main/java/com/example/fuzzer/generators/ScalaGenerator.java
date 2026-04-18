package com.example.fuzzer.generators;

import com.example.fuzzer.JVMCrossLangFuzzer;
import com.example.fuzzer.defects.CrossLangDefectDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ScalaGenerator creates Scala-Java interoperability test cases that can expose
 * compiler defects when Scala and Java code interact.
 * 
 * This generator focuses on areas where Scala's advanced type system, implicits,
 * and functional programming features interact with Java's more traditional OOP model.
 */
public class ScalaGenerator {
    
    private static final String[] SCALA_KEYWORDS = {
        "abstract", "case", "class", "def", "do", "else", "extends", "false", 
        "final", "finally", "for", "if", "implicit", "import", "lazy", "match", 
        "new", "null", "object", "override", "package", "private", "protected", 
        "return", "sealed", "super", "this", "throw", "trait", "try", "true", 
        "type", "val", "var", "while", "with", "yield"
    };
    
    private static final String[] SCALA_TYPES = {
        "Int", "Long", "Double", "Float", "Boolean", "String", "Char", 
        "Byte", "Short", "Unit", "Any", "AnyRef", "AnyVal", "Nothing", "Null"
    };
    
    private static final String[] SCALA_COLLECTIONS = {
        "List", "Vector", "Set", "Map", "Array", "Seq", "Iterable", "Option", "Either"
    };
    
    private static final String[] JAVA_INTEROP_SCENARIOS = {
        // Method signatures with complex generic types
        "generic_interop",
        // Varargs and array handling
        "varargs_arrays", 
        // Exception handling differences
        "exception_handling",
        // SAM (Single Abstract Method) conversions
        "sam_conversions",
        // Implicit conversions to/from Java types
        "implicit_conversions",
        // Null handling and Option vs nullable types
        "null_handling",
        // Type erasure issues with generics
        "type_erasure",
        // Companion objects and static methods
        "companion_static",
        // Case classes and JavaBean patterns
        "case_classes_beans",
        // Traits vs interfaces with default methods
        "traits_interfaces"
    };
    
    private final Random random = ThreadLocalRandom.current();
    private final CrossLangDefectDetector defectDetector;
    private int testCaseCounter = 0;
    
    public ScalaGenerator() {
        this.defectDetector = new CrossLangDefectDetector();
    }
    
    public ScalaGenerator(CrossLangDefectDetector defectDetector) {
        this.defectDetector = defectDetector;
    }
    
    /**
     * Generates a complete Scala-Java interoperability test case.
     * 
     * @param outputDir Directory to write generated files
     * @param testCaseId Unique identifier for this test case
     * @return true if generation successful, false otherwise
     */
    public boolean generateTestCase(String outputDir, String testCaseId) {
        try {
            Path testDir = Paths.get(outputDir, testCaseId);
            Files.createDirectories(testDir);
            
            // Generate Java side first
            String javaCode = generateJavaSide(testCaseId);
            String javaFilePath = testDir.resolve(testCaseId + "Java.java").toString();
            writeToFile(javaFilePath, javaCode);
            
            // Generate Scala side
            String scalaCode = generateScalaSide(testCaseId);
            String scalaFilePath = testDir.resolve(testCaseId + "Scala.scala").toString();
            writeToFile(scalaFilePath, scalaCode);
            
            // Generate build script (sbt or Maven)
            generateBuildScript(testDir, testCaseId);
            
            // Generate test runner
            generateTestRunner(testDir, testCaseId);
            
            testCaseCounter++;
            return true;
            
        } catch (IOException e) {
            System.err.println("Error generating Scala-Java test case: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates the Java side of the interoperability test.
     */
    private String generateJavaSide(String testCaseId) {
        StringBuilder javaCode = new StringBuilder();
        
        // Package declaration
        javaCode.append("package ").append(testCaseId.toLowerCase()).append(";\n\n");
        
        // Imports
        javaCode.append("import java.util.*;\n");
        javaCode.append("import java.util.function.*;\n");
        javaCode.append("import java.util.stream.*;\n\n");
        
        // Class declaration
        javaCode.append("public class ").append(testCaseId).append("Java {\n\n");
        
        // Generate fields based on scenario
        String scenario = JAVA_INTEROP_SCENARIOS[random.nextInt(JAVA_INTEROP_SCENARIOS.length)];
        generateJavaFields(javaCode, scenario);
        javaCode.append("\n");
        
        // Generate constructors
        generateJavaConstructors(javaCode, testCaseId, scenario);
        javaCode.append("\n");
        
        // Generate methods
        generateJavaMethods(javaCode, testCaseId, scenario);
        javaCode.append("\n");
        
        // Close class
        javaCode.append("}\n");
        
        return javaCode.toString();
    }
    
    /**
     * Generates the Scala side of the interoperability test.
     */
    private String generateScalaSide(String testCaseId) {
        StringBuilder scalaCode = new StringBuilder();
        
        // Package declaration
        scalaCode.append("package ").append(testCaseId.toLowerCase()).append("\n\n");
        
        // Imports
        scalaCode.append("import java.util.{List => JList, Map => JMap, Set => JSet}\n");
        scalaCode.append("import scala.collection.JavaConverters._\n");
        scalaCode.append("import scala.util.{Try, Success, Failure}\n\n");
        
        // Object or class declaration
        String scenario = JAVA_INTEROP_SCENARIOS[random.nextInt(JAVA_INTEROP_SCENARIOS.length)];
        generateScalaDeclaration(scalaCode, testCaseId, scenario);
        scalaCode.append("\n");
        
        // Generate Scala methods that interact with Java side
        generateScalaMethods(scalaCode, testCaseId, scenario);
        scalaCode.append("\n");
        
        // Close declaration
        scalaCode.append("}\n");
        
        return scalaCode.toString();
    }
    
    /**
     * Generates Java fields based on the selected scenario.
     */
    private void generateJavaFields(StringBuilder code, String scenario) {
        switch (scenario) {
            case "generic_interop":
                code.append("    private List<? extends Number> numbers;\n");
                code.append("    private Map<String, ? super Object> metadata;\n");
                break;
            case "varargs_arrays":
                code.append("    private String[] stringArray;\n");
                code.append("    private int[] intArray;\n");
                break;
            case "exception_handling":
                code.append("    private RuntimeException lastException;\n");
                break;
            case "sam_conversions":
                code.append("    private Function<String, Integer> stringToInt;\n");
                code.append("    private Consumer<String> stringConsumer;\n");
                break;
            case "implicit_conversions":
                code.append("    private Object convertibleValue;\n");
                break;
            case "null_handling":
                code.append("    private String nullableString;\n");
                code.append("    private List<String> nullableList;\n");
                break;
            case "type_erasure":
                code.append("    private List rawList;\n");
                code.append("    private Map rawMap;\n");
                break;
            case "companion_static":
                code.append("    public static final String STATIC_FIELD = \"static_value\";\n");
                break;
            case "case_classes_beans":
                code.append("    private String name;\n");
                code.append("    private int age;\n");
                break;
            case "traits_interfaces":
                code.append("    private MyInterface myInterface;\n");
                break;
            default:
                code.append("    private Object genericField;\n");
        }
    }
    
    /**
     * Generates Java constructors.
     */
    private void generateJavaConstructors(StringBuilder code, String className, String scenario) {
        code.append("    public ").append(className).append("Java() {\n");
        code.append("        // Default constructor\n");
        code.append("    }\n\n");
        
        code.append("    public ").append(className).append("Java(");
        
        switch (scenario) {
            case "generic_interop":
                code.append("List<? extends Number> numbers, Map<String, ? super Object> metadata");
                break;
            case "varargs_arrays":
                code.append("String... strings");
                break;
            case "sam_conversions":
                code.append("Function<String, Integer> func, Consumer<String> consumer");
                break;
            case "case_classes_beans":
                code.append("String name, int age");
                break;
            default:
                code.append("Object param");
        }
        
        code.append(") {\n");
        code.append("        // Parameterized constructor\n");
        code.append("    }\n");
    }
    
    /**
     * Generates Java methods based on scenario.
     */
    private void generateJavaMethods(StringBuilder code, String className, String scenario) {
        // Always include a basic method
        code.append("    public String processInput(String input) {\n");
        code.append("        if (input == null) return \"null\";\n");
        code.append("        return input.toUpperCase();\n");
        code.append("    }\n\n");
        
        // Scenario-specific methods
        switch (scenario) {
            case "generic_interop":
                code.append("    public <T extends Number> T getNumber(Class<T> clazz, double value) {\n");
                code.append("        try {\n");
                code.append("            if (clazz == Integer.class) {\n");
                code.append("                return clazz.cast(Integer.valueOf((int)value));\n");
                code.append("            } else if (clazz == Double.class) {\n");
                code.append("                return clazz.cast(Double.valueOf(value));\n");
                code.append("            }\n");
                code.append("        } catch (Exception e) {\n");
                code.append("            // Intentional empty catch to trigger potential compiler issues\n");
                code.append("        }\n");
                code.append("        return null;\n");
                code.append("    }\n\n");
                break;
                
            case "varargs_arrays":
                code.append("    public String[] processVarargs(String... args) {\n");
                code.append("        return Arrays.stream(args)\n");
                code.append("            .map(String::toUpperCase)\n");
                code.append("            .toArray(String[]::new);\n");
                code.append("    }\n\n");
                break;
                
            case "exception_handling":
                code.append("    public void riskyOperation() throws Exception {\n");
                code.append("        throw new Exception(\"Intentional exception for interop testing\");\n");
                code.append("    }\n\n");
                break;
                
            case "sam_conversions":
                code.append("    public int applyFunction(String input) {\n");
                code.append("        if (stringToInt != null) {\n");
                code.append("            return stringToInt.apply(input);\n");
                code.append("        }\n");
                code.append("        return input.length();\n");
                code.append("    }\n\n");
                break;
                
            case "null_handling":
                code.append("    public boolean isNullOrEmpty(String str) {\n");
                code.append("        return str == null || str.isEmpty();\n");
                code.append("    }\n\n");
                break;
                
            case "type_erasure":
                code.append("    @SuppressWarnings(\"unchecked\")\n");
                code.append("    public <T> T unsafeCast(Object obj) {\n");
                code.append("        return (T) obj; // Raw cast - potential compiler issue\n");
                code.append("    }\n\n");
                break;
                
            case "companion_static":
                code.append("    public static String getStaticValue() {\n");
                code.append("        return STATIC_FIELD;\n");
                code.append("    }\n\n");
                break;
                
            case "case_classes_beans":
                code.append("    public String getName() { return name; }\n");
                code.append("    public void setName(String name) { this.name = name; }\n");
                code.append("    public int getAge() { return age; }\n");
                code.append("    public void setAge(int age) { this.age = age; }\n\n");
                break;
                
            case "traits_interfaces":
                code.append("    public interface MyInterface {\n");
                code.append("        default String defaultMethod() {\n");
                code.append("            return \"default implementation\";\n");
                code.append("        }\n");
                code.append("        String abstractMethod();\n");
                code.append("    }\n\n");
                code.append("    public void setMyInterface(MyInterface iface) {\n");
                code.append("        this.myInterface = iface;\n");
                code.append("    }\n\n");
                break;
        }
        
        // Add a method that returns complex generic types
        code.append("    public Map<String, List<Integer>> getComplexStructure() {\n");
        code.append("        Map<String, List<Integer>> result = new HashMap<>();\n");
        code.append("        result.put(\"numbers\", Arrays.asList(1, 2, 3, 4, 5));\n");
        code.append("        return result;\n");
        code.append("    }\n");
    }
    
    /**
     * Generates Scala class/object declaration.
     */
    private void generateScalaDeclaration(StringBuilder code, String className, String scenario) {
        boolean useObject = random.nextBoolean();
        
        if (useObject) {
            code.append("object ").append(className).append("Scala {\n");
        } else {
            code.append("class ").append(className).append("Scala {\n");
        }
    }
    
    /**
     * Generates Scala methods that interact with Java side.
     */
    private void generateScalaMethods(StringBuilder code, String className, String scenario) {
        code.append("  def callJavaMethod(input: String): String = {\n");
        code.append("    val javaInstance = new ").append(className).append("Java()\n");
        code.append("    javaInstance.processInput(input)\n");
        code.append("  }\n\n");
        
        switch (scenario) {
            case "generic_interop":
                code.append("  def testGenericInterop(): Unit = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java()\n");
                code.append("    val result = javaInstance.getNumber(classOf[Integer], 42.0)\n");
                code.append("    println(s\"Generic result: $$result\")\n");
                code.append("  }\n\n");
                break;
                
            case "varargs_arrays":
                code.append("  def testVarargs(): Array[String] = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java()\n");
                code.append("    javaInstance.processVarargs(\"hello\", \"world\", \"scala\")\n");
                code.append("  }\n\n");
                break;
                
            case "exception_handling":
                code.append("  def testExceptionHandling(): Try[Unit] = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java()\n");
                code.append("    Try(javaInstance.riskyOperation())\n");
                code.append("  }\n\n");
                break;
                
            case "sam_conversions":
                code.append("  def testSAM(): Int = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java(\n");
                code.append("      (s: String) => s.length,\n");
                code.append("      (s: String) => println(s)\n");
                code.append("    )\n");
                code.append("    javaInstance.applyFunction(\"test\")\n");
                code.append("  }\n\n");
                break;
                
            case "implicit_conversions":
                code.append("  implicit def stringToInt(s: String): Int = s.length\n");
                code.append("  \n");
                code.append("  def testImplicitConversion(): Unit = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java()\n");
                code.append("    // This should trigger implicit conversion\n");
                code.append("    val result: Int = \"hello world\"\n");
                code.append("    println(s\"Implicit result: $$result\")\n");
                code.append("  }\n\n");
                break;
                
            case "null_handling":
                code.append("  def testNullHandling(): Option[String] = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java()\n");
                code.append("    val nullable = javaInstance.isNullOrEmpty(null)\n");
                code.append("    if (nullable) None else Some(\"not null\")\n");
                code.append("  }\n\n");
                break;
                
            case "type_erasure":
                code.append("  def testTypeErasure(): Unit = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java()\n");
                code.append("    val rawList = javaInstance.unsafeCast[List[String]](List(\"a\", \"b\"))\n");
                code.append("    println(s\"Raw list: $$rawList\")\n");
                code.append("  }\n\n");
                break;
                
            case "companion_static":
                code.append("  def testStaticAccess(): String = {\n");
                code.append("    ").append(className).append("Java.getStaticValue()\n");
                code.append("  }\n\n");
                break;
                
            case "case_classes_beans":
                code.append("  case class Person(name: String, age: Int)\n");
                code.append("  \n");
                code.append("  def testCaseClassInterop(person: Person): Unit = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java(person.name, person.age)\n");
                code.append("    println(s\"Java bean: $${javaInstance.getName()}, $${javaInstance.getAge()}\")\n");
                code.append("  }\n\n");
                break;
                
            case "traits_interfaces":
                code.append("  trait MyScalaTrait {\n");
                code.append("    def defaultMethod(): String = \"scala default\"\n");
                code.append("    def abstractMethod(): String\n");
                code.append("  }\n");
                code.append("  \n");
                code.append("  def testTraitInterface(): Unit = {\n");
                code.append("    val javaInstance = new ").append(className).append("Java()\n");
                code.append("    val iface = new ").append(className).append("Java.MyInterface {\n");
                code.append("      override def abstractMethod(): String = \"implemented\"\n");
                code.append("    }\n");
                code.append("    javaInstance.setMyInterface(iface)\n");
                code.append("    println(s\"Interface result: $${iface.defaultMethod()}\")\n");
                code.append("  }\n\n");
                break;
        }
        
        // Add method to test complex structure interaction
        code.append("  def testComplexStructure(): Map[String, List[Int]] = {\n");
        code.append("    val javaInstance = new ").append(className).append("Java()\n");
        code.append("    val javaMap = javaInstance.getComplexStructure()\n");
        code.append("    // Convert Java collections to Scala\n");
        code.append("    import scala.jdk.CollectionConverters._\n");
        code.append("    javaMap.asScala.map { case (k, v) => (k, v.asScala.toList) }.toMap\n");
        code.append("  }\n");
    }
    
    /**
     * Generates a build script (sbt) for the test case.
     */
    private void generateBuildScript(Path testDir, String testCaseId) throws IOException {
        String sbtContent = "name := \"" + testCaseId + "\"\n" +
                           "version := \"0.1\"\n" +
                           "scalaVersion := \"2.13.12\"\n" +
                           "\n" +
                           "libraryDependencies ++= Seq(\n" +
                           "  \"org.scalatest\" %% \"scalatest\" % \"3.2.17\" % Test\n" +
                           ")\n" +
                           "\n" +
                           "javacOptions ++= Seq(\"-Xlint:unchecked\", \"-Xlint:deprecation\")\n" +
                           "scalacOptions ++= Seq(\"-feature\", \"-deprecation\", \"-unchecked\")\n";
        
        writeToFile(testDir.resolve("build.sbt").toString(), sbtContent);
    }
    
    /**
     * Generates a test runner script.
     */
    private void generateTestRunner(Path testDir, String testCaseId) throws IOException {
        String runnerContent = "#!/bin/bash\n" +
                              "# Test runner for " + testCaseId + "\n" +
                              "\n" +
                              "set -e\n" +
                              "\n" +
                              "# Compile Java first\n" +
                              "javac " + testCaseId + "Java.java\n" +
                              "\n" +
                              "# Compile Scala with Java classes on classpath\n" +
                              "scalac -cp . " + testCaseId + "Scala.scala\n" +
                              "\n" +
                              "# Run the test\n" +
                              "scala " + testCaseId + "Scala\n" +
                              "\n" +
                              "echo \"Test completed successfully\"\n";
        
        String runnerPath = testDir.resolve("run_test.sh").toString();
        writeToFile(runnerPath, runnerContent);
        
        // Make executable
        new File(runnerPath).setExecutable(true);
    }
    
    /**
     * Writes content to a file.
     */
    private void writeToFile(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }
    
    /**
     * Gets the current test case counter.
     */
    public int getTestCaseCounter() {
        return testCaseCounter;
    }
    
    /**
     * Resets the test case counter.
     */
    public void resetCounter() {
        testCaseCounter = 0;
    }
    
    /**
     * Generates multiple test cases in a batch.
     */
    public int generateBatch(String outputDir, int count) {
        int successCount = 0;
        for (int i = 0; i < count; i++) {
            String testCaseId = "TestCase" + String.format("%04d", i + 1);
            if (generateTestCase(outputDir, testCaseId)) {
                successCount++;
            }
        }
        return successCount;
    }
    
    /**
     * Gets all available interop scenarios.
     */
    public String[] getAvailableScenarios() {
        return JAVA_INTEROP_SCENARIOS.clone();
    }
}