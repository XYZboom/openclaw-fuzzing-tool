package com.example.fuzzer;

import com.example.fuzzer.defects.CrossLangDefectDetector;
import com.example.fuzzer.execution.TestCaseRunner;
import com.example.fuzzer.model.TestCase;
import com.example.fuzzer.model.TestCaseResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main entry point for the JVM Cross-Language Fuzz Testing Tool.
 * This tool is designed to detect compiler defects and interoperability issues
 * between different JVM languages, specifically focusing on:
 * - Kotlin-Java interoperability
 * - Scala-Java interoperability
 */
public class JVMCrossLangFuzzer {
    private static final Logger logger = Logger.getLogger(JVMCrossLangFuzzer.class.getName());
    
    private final File outputDirectory;
    private final int maxTestCases;
    private final TestCaseRunner testCaseRunner;
    private final CrossLangDefectDetector defectDetector;
    
    public JVMCrossLangFuzzer(File outputDirectory, int maxTestCases) {
        this.outputDirectory = outputDirectory;
        this.maxTestCases = maxTestCases;
        this.testCaseRunner = new TestCaseRunner();
        this.defectDetector = new CrossLangDefectDetector();
        
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
    }
    
    public void runFuzzing() {
        logger.info("Starting JVM Cross-Language Fuzz Testing");
        logger.info("Output directory: " + outputDirectory.getAbsolutePath());
        logger.info("Max test cases: " + maxTestCases);
        
        List<TestCase> testCases = new ArrayList<>();
        
        // Generate Kotlin-Java test cases
        for (int i = 0; i < maxTestCases / 2; i++) {
            TestCase testCase = new TestCase("kotlin-java-" + i, "Main");
            // Add simple Kotlin-Java interoperability test
            testCase.addJavaFile("JavaClass.java", 
                "public class JavaClass {\n" +
                "    public static String getMessage() { return \"Hello from Java\"; }\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(KotlinClass.getMessageFromKotlin());\n" +
                "    }\n" +
                "}\n");
            testCase.addKotlinFile("KotlinClass.kt",
                "class KotlinClass {\n" +
                "    companion object {\n" +
                "        fun getMessageFromKotlin(): String {\n" +
                "            return JavaClass.getMessage() + \" and Kotlin\"\n" +
                "        }\n" +
                "    }\n" +
                "}\n");
            testCases.add(testCase);
        }
        
        // Generate Scala-Java test cases
        for (int i = 0; i < maxTestCases / 2; i++) {
            TestCase testCase = new TestCase("scala-java-" + i, "JavaClass");
            // Add simple Scala-Java interoperability test
            testCase.addJavaFile("JavaClass.java",
                "public class JavaClass {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(ScalaObject$.MODULE$.getMessageFromScala());\n" +
                "    }\n" +
                "    public static String getMessage() { return \"Hello from Java\"; }\n" +
                "}\n");
            testCase.addScalaFile("ScalaObject.scala",
                "object ScalaObject {\n" +
                "  def getMessageFromScala: String = {\n" +
                "    JavaClass.getMessage + \" and Scala\"\n" +
                "  }\n" +
                "}\n");
            testCases.add(testCase);
        }
        
        logger.info("Generated " + testCases.size() + " test cases");
        
        // Run all test cases
        List<TestCaseResult> results = testCaseRunner.runTestCases(testCases);
        
        int defectsFound = 0;
        for (TestCaseResult result : results) {
            if (result.isDefect()) {
                defectsFound++;
                logger.severe("DEFECT DETECTED in test case: " + result.getTestCaseId());
                saveDefectReport(result);
            }
        }
        
        logger.info("Fuzzing completed. Total defects found: " + defectsFound);
    }
    
    private void saveDefectReport(TestCaseResult result) {
        try {
            File defectDir = new File(outputDirectory, "defects");
            if (!defectDir.exists()) {
                defectDir.mkdirs();
            }
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            File reportFile = new File(defectDir, 
                result.getTestCaseId() + "_defect_" + timestamp + ".txt");
            
            String report = "Test Case ID: " + result.getTestCaseId() + "\n" +
                           "Defect Detected: " + result.isDefect() + "\n" +
                           "Success: " + result.isSuccess() + "\n" +
                           "Message: " + result.getMessage() + "\n";
            
            java.nio.file.Files.write(reportFile.toPath(), report.getBytes());
            logger.info("Saved defect report: " + reportFile.getAbsolutePath());
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save defect report", e);
        }
    }
    
    public static void main(String[] args) {
        File outputDir = new File("fuzz-output");
        int maxTestCases = 10;
        
        JVMCrossLangFuzzer fuzzer = new JVMCrossLangFuzzer(outputDir, maxTestCases);
        fuzzer.runFuzzing();
    }
}