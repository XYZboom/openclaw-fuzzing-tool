package com.example.fuzzer;

import com.example.fuzzer.generators.KotlinGenerator;
import com.example.fuzzer.generators.ScalaGenerator;
import com.example.fuzzer.defects.CrossLangDefectDetector;
import com.example.fuzzer.execution.TestCaseRunner;
import com.example.fuzzer.model.TestCase;
import com.example.fuzzer.model.TestCaseResult;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Enhanced version of the JVM Cross-Language Fuzz Testing Tool.
 * This version generates more complex test cases with advanced language features
 * and better defect detection capabilities.
 */
public class EnhancedJVMCrossLangFuzzer {
    private static final Logger logger = Logger.getLogger(EnhancedJVMCrossLangFuzzer.class.getName());
    
    private final KotlinGenerator kotlinGenerator;
    private final ScalaGenerator scalaGenerator;
    private final CrossLangDefectDetector defectDetector;
    private final TestCaseRunner testCaseRunner;
    private final File outputDirectory;
    private final int maxTestCases;
    
    public EnhancedJVMCrossLangFuzzer(File outputDirectory, int maxTestCases) {
        this.outputDirectory = outputDirectory;
        this.maxTestCases = maxTestCases;
        
        // Initialize with enhanced complexity settings
        this.kotlinGenerator = new KotlinGenerator();
        this.scalaGenerator = new ScalaGenerator();
        this.defectDetector = new CrossLangDefectDetector();
        this.testCaseRunner = new TestCaseRunner();
        
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
    }
    
    public void runFuzzing() {
        logger.info("Starting Enhanced JVM Cross-Language Fuzz Testing");
        logger.info("Output directory: " + outputDirectory.getAbsolutePath());
        logger.info("Max test cases: " + maxTestCases);
        
        int totalDefectsFound = 0;
        
        try {
            // Generate more complex Kotlin-Java test cases
            logger.info("Generating enhanced Kotlin-Java test cases...");
            List<TestCase> kotlinJavaTests = generateEnhancedKotlinJavaTestCases(maxTestCases / 2);
            
            for (TestCase testCase : kotlinJavaTests) {
                TestCaseResult result = testCaseRunner.runTestCase(testCase);
                if (result.isDefect()) {
                    logger.severe("DEFECT DETECTED in Kotlin-Java interoperability! Test case: " + result.getTestCaseId());
                    saveDefectiveTestCase(testCase, result, "kotlin-java");
                    totalDefectsFound++;
                }
            }
            
            // Generate more complex Scala-Java test cases
            logger.info("Generating enhanced Scala-Java test cases...");
            List<TestCase> scalaJavaTests = generateEnhancedScalaJavaTestCases(maxTestCases / 2);
            
            for (TestCase testCase : scalaJavaTests) {
                TestCaseResult result = testCaseRunner.runTestCase(testCase);
                if (result.isDefect()) {
                    logger.severe("DEFECT DETECTED in Scala-Java interoperability! Test case: " + result.getTestCaseId());
                    saveDefectiveTestCase(testCase, result, "scala-java");
                    totalDefectsFound++;
                }
            }
            
            logger.info("Enhanced fuzzing completed. Total defects found: " + totalDefectsFound);
            
        } catch (Exception e) {
            logger.severe("Fatal error during enhanced fuzzing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private List<TestCase> generateEnhancedKotlinJavaTestCases(int count) {
        List<TestCase> testCases = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            String testCaseId = "enhanced_kt_java_" + System.currentTimeMillis() + "_" + i;
            TestCase testCase = new TestCase(testCaseId, "Main");
            
            // Add more complex Kotlin-Java interoperability scenarios
            if (i % 4 == 0) {
                // Nullable types and platform types
                testCase.addJavaFile("JavaClass.java", 
                    "public class JavaClass {\n" +
                    "    public String nullableString;\n" +
                    "    public static String staticMethod(String input) { return input != null ? input.toUpperCase() : \"NULL\"; }\n" +
                    "}\n");
                
                testCase.addKotlinFile("KotlinClass.kt",
                    "class KotlinClass {\n" +
                    "    fun processJava(jc: JavaClass?) {\n" +
                    "        val result = jc?.nullableString?.let { JavaClass.staticMethod(it) } ?: \"default\"\n" +
                    "        println(result)\n" +
                    "    }\n" +
                    "}\n");
                
                testCase.addKotlinFile("Main.kt",
                    "fun main() {\n" +
                    "    val jc = JavaClass()\n" +
                    "    jc.nullableString = \"hello\"\n" +
                    "    KotlinClass().processJava(jc)\n" +
                    "}\n");
                    
            } else if (i % 4 == 1) {
                // Generics and variance
                testCase.addJavaFile("GenericContainer.java",
                    "public class GenericContainer<T> {\n" +
                    "    private T value;\n" +
                    "    public GenericContainer(T value) { this.value = value; }\n" +
                    "    public T getValue() { return value; }\n" +
                    "}\n");
                
                testCase.addKotlinFile("VarianceTest.kt",
                    "fun <T> processContainer(container: GenericContainer<T>): T {\n" +
                    "    return container.value\n" +
                    "}\n" +
                    "\n" +
                    "fun main() {\n" +
                    "    val container = GenericContainer(\"test\")\n" +
                    "    println(processContainer(container))\n" +
                    "}\n");
                    
            } else if (i % 4 == 2) {
                // SAM conversions and functional interfaces
                testCase.addJavaFile("Callback.java",
                    "public interface Callback {\n" +
                    "    void onComplete(String result);\n" +
                    "}\n" +
                    "\n" +
                    "public class AsyncProcessor {\n" +
                    "    public static void process(Callback callback) {\n" +
                    "        callback.onComplete(\"done\");\n" +
                    "    }\n" +
                    "}\n");
                
                testCase.addKotlinFile("SAMTest.kt",
                    "fun main() {\n" +
                    "    AsyncProcessor.process { result ->\n" +
                    "        println(\"Result: $result\")\n" +
                    "    }\n" +
                    "}\n");
                    
            } else {
                // Extension functions and operator overloading
                testCase.addJavaFile("DataPoint.java",
                    "public class DataPoint {\n" +
                    "    public final int x, y;\n" +
                    "    public DataPoint(int x, int y) { this.x = x; this.y = y; }\n" +
                    "    @Override public String toString() { return \"($x, $y)\"; }\n" +
                    "}\n");
                
                testCase.addKotlinFile("Extensions.kt",
                    "operator fun DataPoint.plus(other: DataPoint): DataPoint {\n" +
                    "    return DataPoint(this.x + other.x, this.y + other.y)\n" +
                    "}\n" +
                    "\n" +
                    "fun main() {\n" +
                    "    val p1 = DataPoint(1, 2)\n" +
                    "    val p2 = DataPoint(3, 4)\n" +
                    "    println(p1 + p2)\n" +
                    "}\n");
            }
            
            testCases.add(testCase);
        }
        
        return testCases;
    }
    
    private List<TestCase> generateEnhancedScalaJavaTestCases(int count) {
        List<TestCase> testCases = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            String testCaseId = "enhanced_sc_java_" + System.currentTimeMillis() + "_" + i;
            TestCase testCase = new TestCase(testCaseId, "Main");
            
            // Add more complex Scala-Java interoperability scenarios
            if (i % 3 == 0) {
                // Case classes and pattern matching with Java
                testCase.addJavaFile("Person.java",
                    "public class Person {\n" +
                    "    public final String name;\n" +
                    "    public final int age;\n" +
                    "    public Person(String name, int age) { this.name = name; this.age = age; }\n" +
                    "}\n");
                
                testCase.addScalaFile("PatternMatch.scala",
                    "object PatternMatch {\n" +
                    "  def processPerson(p: Person): String = p match {\n" +
                    "    case Person(name, age) if age >= 18 => s\"$name is an adult\"\n" +
                    "    case Person(name, _) => s\"$name is a minor\"\n" +
                    "  }\n" +
                    "}\n" +
                    "\n" +
                    "object Main extends App {\n" +
                    "  val person = new Person(\"Alice\", 25)\n" +
                    "  println(PatternMatch.processPerson(person))\n" +
                    "}\n");
                    
            } else if (i % 3 == 1) {
                // Implicit conversions and type classes
                testCase.addJavaFile("StringUtils.java",
                    "public class StringUtils {\n" +
                    "    public static String reverse(String s) {\n" +
                    "        return new StringBuilder(s).reverse().toString();\n" +
                    "    }\n" +
                    "}\n");
                
                testCase.addScalaFile("Implicits.scala",
                    "object Implicits {\n" +
                    "  implicit class StringOps(s: String) {\n" +
                    "    def reversed: String = StringUtils.reverse(s)\n" +
                    "  }\n" +
                    "}\n" +
                    "\n" +
                    "object Main extends App {\n" +
                    "  import Implicits._\n" +
                    "  println(\"hello\".reversed)\n" +
                    "}\n");
                    
            } else {
                // Traits and mixin composition
                testCase.addJavaFile("Logger.java",
                    "public interface Logger {\n" +
                    "    default void log(String message) {\n" +
                    "        System.out.println(\"[LOG] \" + message);\n" +
                    "    }\n" +
                    "}\n");
                
                testCase.addScalaFile("Traits.scala",
                    "trait Timestamped {\n" +
                    "  def timestamp: Long = System.currentTimeMillis()\n" +
                    "}\n" +
                    "\n" +
                    "class ScalaLogger extends Logger with Timestamped {\n" +
                    "  override def log(message: String): Unit = {\n" +
                    "    super.log(s\"[$timestamp] $message\")\n" +
                    "  }\n" +
                    "}\n" +
                    "\n" +
                    "object Main extends App {\n" +
                    "  val logger = new ScalaLogger()\n" +
                    "  logger.log(\"Test message\")\n" +
                    "}\n");
            }
            
            testCases.add(testCase);
        }
        
        return testCases;
    }
    
    private void saveDefectiveTestCase(TestCase testCase, TestCaseResult result, String languagePair) {
        try {
            File defectDir = new File(outputDirectory, "defects");
            if (!defectDir.exists()) {
                defectDir.mkdirs();
            }
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            File defectFile = new File(defectDir, 
                languagePair + "_defect_" + result.getTestCaseId() + "_" + timestamp + ".txt");
            
            StringBuilder report = new StringBuilder();
            report.append("Language Pair: ").append(languagePair).append("\n");
            report.append("Test Case ID: ").append(result.getTestCaseId()).append("\n");
            report.append("Defect Detected: ").append(result.isDefect()).append("\n");
            report.append("Success: ").append(result.isSuccess()).append("\n");
            report.append("Message: ").append(result.getMessage()).append("\n");
            report.append("\n=== SOURCE CODE ===\n\n");
            
            for (String fileName : testCase.getJavaFiles().keySet()) {
                report.append("=== ").append(fileName).append(" ===\n");
                report.append(testCase.getJavaFiles().get(fileName)).append("\n\n");
            }
            
            for (String fileName : testCase.getKotlinFiles().keySet()) {
                report.append("=== ").append(fileName).append(" ===\n");
                report.append(testCase.getKotlinFiles().get(fileName)).append("\n\n");
            }
            
            for (String fileName : testCase.getScalaFiles().keySet()) {
                report.append("=== ").append(fileName).append(" ===\n");
                report.append(testCase.getScalaFiles().get(fileName)).append("\n\n");
            }
            
            java.nio.file.Files.write(defectFile.toPath(), report.toString().getBytes());
            logger.info("Saved defective test case to: " + defectFile.getAbsolutePath());
            
        } catch (Exception e) {
            logger.severe("Failed to save defective test case: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        File outputDir = new File("fuzz-output-enhanced");
        int maxTestCases = 20;
        
        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if ("-o".equals(args[i]) || "--output".equals(args[i])) {
                if (i + 1 < args.length) {
                    outputDir = new File(args[++i]);
                }
            } else if ("-n".equals(args[i]) || "--max-test-cases".equals(args[i])) {
                if (i + 1 < args.length) {
                    try {
                        maxTestCases = Integer.parseInt(args[++i]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number: " + args[i]);
                        System.exit(1);
                    }
                }
            }
        }
        
        EnhancedJVMCrossLangFuzzer fuzzer = new EnhancedJVMCrossLangFuzzer(outputDir, maxTestCases);
        fuzzer.runFuzzing();
    }
}