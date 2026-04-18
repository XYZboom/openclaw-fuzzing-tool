package com.example.fuzzer.examples;

import com.example.fuzzer.JVMCrossLangFuzzer;
import java.io.File;

/**
 * Basic example showing how to use the JVM Cross-Language Fuzz Testing Tool.
 */
public class BasicExample {
    public static void main(String[] args) {
        // Create output directory
        File outputDir = new File("fuzz-output");
        
        // Create fuzzer instance
        JVMCrossLangFuzzer fuzzer = new JVMCrossLangFuzzer(outputDir, 10);
        
        // Run fuzzing with 10 test cases
        fuzzer.runFuzzing();
    }
}