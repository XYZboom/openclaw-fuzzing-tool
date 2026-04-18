package com.example.fuzzer.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a generated test case for cross-language interoperability testing.
 */
public class TestCase {
    private final String id;
    private final String mainClass;
    private final Map<String, String> javaFiles;
    private final Map<String, String> kotlinFiles;
    private final Map<String, String> scalaFiles;
    
    public TestCase(String id, String mainClass) {
        this.id = id;
        this.mainClass = mainClass;
        this.javaFiles = new HashMap<>();
        this.kotlinFiles = new HashMap<>();
        this.scalaFiles = new HashMap<>();
    }
    
    public String getId() { return id; }
    public String getMainClass() { return mainClass; }
    public Map<String, String> getJavaFiles() { return javaFiles; }
    public Map<String, String> getKotlinFiles() { return kotlinFiles; }
    public Map<String, String> getScalaFiles() { return scalaFiles; }
    
    public void addJavaFile(String fileName, String content) {
        javaFiles.put(fileName, content);
    }
    
    public void addKotlinFile(String fileName, String content) {
        kotlinFiles.put(fileName, content);
    }
    
    public void addScalaFile(String fileName, String content) {
        scalaFiles.put(fileName, content);
    }
}