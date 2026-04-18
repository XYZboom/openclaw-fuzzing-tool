package com.example.fuzzer.model;

import java.util.List;
import java.time.LocalDateTime;

/**
 * Represents a detected compiler defect or bug found during fuzz testing.
 * This class captures all relevant information about cross-language interoperability issues.
 */
public class DefectReport {
    private String defectId;
    private LocalDateTime timestamp;
    private String languagePair;
    private String severity;
    private String description;
    private String reproductionSteps;
    private List<String> affectedCompilers;
    private String testCaseSource;
    private String expectedBehavior;
    private String actualBehavior;
    private boolean isConfirmed;
    
    /**
     * Default constructor for JSON serialization
     */
    public DefectReport() {
    }
    
    /**
     * Creates a new defect report with essential information
     */
    public DefectReport(String defectId, String languagePair, String severity, 
                       String description, String reproductionSteps) {
        this.defectId = defectId;
        this.timestamp = LocalDateTime.now();
        this.languagePair = languagePair;
        this.severity = severity;
        this.description = description;
        this.reproductionSteps = reproductionSteps;
        this.isConfirmed = false;
    }
    
    // Getters and setters
    public String getDefectId() {
        return defectId;
    }
    
    public void setDefectId(String defectId) {
        this.defectId = defectId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getLanguagePair() {
        return languagePair;
    }
    
    public void setLanguagePair(String languagePair) {
        this.languagePair = languagePair;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReproductionSteps() {
        return reproductionSteps;
    }
    
    public void setReproductionSteps(String reproductionSteps) {
        this.reproductionSteps = reproductionSteps;
    }
    
    public List<String> getAffectedCompilers() {
        return affectedCompilers;
    }
    
    public void setAffectedCompilers(List<String> affectedCompilers) {
        this.affectedCompilers = affectedCompilers;
    }
    
    public String getTestCaseSource() {
        return testCaseSource;
    }
    
    public void setTestCaseSource(String testCaseSource) {
        this.testCaseSource = testCaseSource;
    }
    
    public String getExpectedBehavior() {
        return expectedBehavior;
    }
    
    public void setExpectedBehavior(String expectedBehavior) {
        this.expectedBehavior = expectedBehavior;
    }
    
    public String getActualBehavior() {
        return actualBehavior;
    }
    
    public void setActualBehavior(String actualBehavior) {
        this.actualBehavior = actualBehavior;
    }
    
    public boolean isConfirmed() {
        return isConfirmed;
    }
    
    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
    
    @Override
    public String toString() {
        return "DefectReport{" +
                "defectId='" + defectId + '\'' +
                ", timestamp=" + timestamp +
                ", languagePair='" + languagePair + '\'' +
                ", severity='" + severity + '\'' +
                ", description='" + description + '\'' +
                ", reproductionSteps='" + reproductionSteps + '\'' +
                ", isConfirmed=" + isConfirmed +
                '}';
    }
}