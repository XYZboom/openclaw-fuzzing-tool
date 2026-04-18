package com.example.fuzzer.ir;

import java.util.*;
import java.util.logging.Logger;

/**
 * Intermediate Representation for cross-language interoperability testing.
 * This class models the structure of cross-language programs and provides
 * methods to generate source code in different languages.
 */
public class CrossLangIR {
    private static final Logger logger = Logger.getLogger(CrossLangIR.class.getName());
    
    private final String id;
    private final TypeSystem.Language primaryLanguage;
    private final TypeSystem.Language secondaryLanguage;
    private final Map<String, IRType> types;
    private final Map<String, IRFunction> functions;
    private final List<String> javaFields;
    private final List<String> javaMethods;
    private final List<String> javaMethodCalls;
    private final List<String> kotlinProperties;
    private final List<String> kotlinFunctions;
    private final List<String> kotlinCompanionFunctions;
    private final List<String> scalaTypes;
    private final List<String> scalaCompanionObjects;
    private final List<String> scalaImplicits;
    private final List<String> scalaTraits;
    private boolean hasValueClass;
    private boolean hasInterfaces;
    private boolean hasEnums;
    private boolean hasAnnotations;
    private String mainClassName;
    private String kotlinClassName;
    private String scalaClassName;
    
    public CrossLangIR(String id, TypeSystem.Language primaryLanguage, TypeSystem.Language secondaryLanguage) {
        this.id = id;
        this.primaryLanguage = primaryLanguage;
        this.secondaryLanguage = secondaryLanguage;
        this.types = new HashMap<>();
        this.functions = new HashMap<>();
        this.javaFields = new ArrayList<>();
        this.javaMethods = new ArrayList<>();
        this.javaMethodCalls = new ArrayList<>();
        this.kotlinProperties = new ArrayList<>();
        this.kotlinFunctions = new ArrayList<>();
        this.kotlinCompanionFunctions = new ArrayList<>();
        this.scalaTypes = new ArrayList<>();
        this.scalaCompanionObjects = new ArrayList<>();
        this.scalaImplicits = new ArrayList<>();
        this.scalaTraits = new ArrayList<>();
        this.hasValueClass = false;
        this.hasInterfaces = false;
        this.hasEnums = false;
        this.hasAnnotations = false;
        this.mainClassName = "Main";
        this.kotlinClassName = "KotlinMain";
        this.scalaClassName = "ScalaMain";
    }
    
    // Getters
    public String getId() { return id; }
    public TypeSystem.Language getPrimaryLanguage() { return primaryLanguage; }
    public TypeSystem.Language getSecondaryLanguage() { return secondaryLanguage; }
    public Map<String, IRType> getTypes() { return types; }
    public Map<String, IRFunction> getFunctions() { return functions; }
    public List<String> getJavaFields() { return javaFields; }
    public List<String> getJavaMethods() { return javaMethods; }
    public List<String> getJavaMethodCalls() { return javaMethodCalls; }
    public List<String> getKotlinProperties() { return kotlinProperties; }
    public List<String> getKotlinFunctions() { return kotlinFunctions; }
    public List<String> getKotlinCompanionFunctions() { return kotlinCompanionFunctions; }
    public List<String> getScalaTypes() { return scalaTypes; }
    public List<String> getScalaCompanionObjects() { return scalaCompanionObjects; }
    public List<String> getScalaImplicits() { return scalaImplicits; }
    public List<String> getScalaTraits() { return scalaTraits; }
    public boolean hasValueClass() { return hasValueClass; }
    public boolean hasInterfaces() { return hasInterfaces; }
    public boolean hasEnums() { return hasEnums; }
    public boolean hasAnnotations() { return hasAnnotations; }
    public String getMainClassName() { return mainClassName; }
    public String getKotlinClassName() { return kotlinClassName; }
    public String getScalaClassName() { return scalaClassName; }
    
    // Add methods
    public void addJavaField(String field) {
        javaFields.add(field);
    }
    
    public void addJavaMethod(String method) {
        javaMethods.add(method);
    }
    
    public void addJavaMethodCall(String methodCall) {
        javaMethodCalls.add(methodCall);
    }
    
    public void addKotlinProperty(String property) {
        kotlinProperties.add(property);
    }
    
    public void addKotlinFunction(String function) {
        kotlinFunctions.add(function);
    }
    
    public void addKotlinCompanionFunction(String function) {
        kotlinCompanionFunctions.add(function);
    }
    
    public void addScalaType(String type) {
        scalaTypes.add(type);
    }
    
    public void addScalaCompanionObject(String companion) {
        scalaCompanionObjects.add(companion);
    }
    
    public void addScalaImplicit(String implicit) {
        scalaImplicits.add(implicit);
    }
    
    public void addScalaTrait(String trait) {
        scalaTraits.add(trait);
    }
    
    public void setHasValueClass(boolean hasValueClass) {
        this.hasValueClass = hasValueClass;
    }
    
    public void setHasInterfaces(boolean hasInterfaces) {
        this.hasInterfaces = hasInterfaces;
    }
    
    public void setHasEnums(boolean hasEnums) {
        this.hasEnums = hasEnums;
    }
    
    public void setHasAnnotations(boolean hasAnnotations) {
        this.hasAnnotations = hasAnnotations;
    }
    
    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }
    
    public void setKotlinClassName(String kotlinClassName) {
        this.kotlinClassName = kotlinClassName;
    }
    
    public void setScalaClassName(String scalaClassName) {
        this.scalaClassName = scalaClassName;
    }
    
    /**
     * Generates source code for the IR model.
     */
    public Map<String, String> generateSourceCode(String testCaseId) {
        Map<String, String> sources = new HashMap<>();
        
        if (primaryLanguage == TypeSystem.Language.JAVA || secondaryLanguage == TypeSystem.Language.JAVA) {
            sources.put("java", generateJavaCode(testCaseId));
        }
        
        if (primaryLanguage == TypeSystem.Language.KOTLIN || secondaryLanguage == TypeSystem.Language.KOTLIN) {
            sources.put("kotlin", generateKotlinCode(testCaseId));
        }
        
        if (primaryLanguage == TypeSystem.Language.SCALA || secondaryLanguage == TypeSystem.Language.SCALA) {
            sources.put("scala", generateScalaCode(testCaseId));
        }
        
        return sources;
    }
    
    private String generateJavaCode(String testCaseId) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.test;\n\n");
        sb.append("public class ").append(mainClassName).append(" {\n\n");
        
        // Add fields
        for (String field : javaFields) {
            sb.append("    ").append(field).append("\n");
        }
        
        // Add constructor
        sb.append("    public ").append(mainClassName).append("() {\n");
        sb.append("        // Constructor\n");
        sb.append("    }\n\n");
        
        // Add methods
        for (String method : javaMethods) {
            sb.append("    ").append(method).append("\n");
        }
        
        // Add main method
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        System.out.println(\"Running test case: ").append(testCaseId).append("\");\n");
        sb.append("        ").append(mainClassName).append(" instance = new ").append(mainClassName).append("();\n");
        
        for (String methodCall : javaMethodCalls) {
            sb.append("        ").append(methodCall).append("\n");
        }
        
        sb.append("        System.out.println(\"Test completed successfully\");\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        return sb.toString();
    }
    
    private String generateKotlinCode(String testCaseId) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.test\n\n");
        
        if (hasValueClass) {
            sb.append("value class ").append(kotlinClassName).append("(val value: Int) {\n\n");
            
            for (String property : kotlinProperties) {
                sb.append("    ").append(property).append("\n");
            }
            
            for (String function : kotlinFunctions) {
                sb.append("    ").append(function).append("\n");
            }
            
            sb.append("}\n\n");
        } else {
            sb.append("class ").append(kotlinClassName).append(" {\n\n");
            
            for (String property : kotlinProperties) {
                sb.append("    ").append(property).append("\n");
            }
            
            for (String function : kotlinFunctions) {
                sb.append("    ").append(function).append("\n");
            }
            
            sb.append("}\n\n");
        }
        
        // Companion object
        if (!kotlinCompanionFunctions.isEmpty()) {
            sb.append("fun main() {\n");
            sb.append("    println(\"Running Kotlin test case: ").append(testCaseId).append("\")\n");
            
            for (String function : kotlinCompanionFunctions) {
                sb.append("    ").append(function).append("\n");
            }
            
            sb.append("    println(\"Kotlin test completed successfully\")\n");
            sb.append("}\n");
        }
        
        return sb.toString();
    }
    
    private String generateScalaCode(String testCaseId) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.test\n\n");
        
        // Types
        for (String type : scalaTypes) {
            sb.append(type).append("\n");
        }
        
        // Companion objects
        for (String companion : scalaCompanionObjects) {
            sb.append(companion).append("\n");
        }
        
        // Implicits
        for (String implicit : scalaImplicits) {
            sb.append(implicit).append("\n");
        }
        
        // Traits
        for (String trait : scalaTraits) {
            sb.append(trait).append("\n");
        }
        
        sb.append("object Main extends App {\n");
        sb.append("  println(\"Running Scala test case: ").append(testCaseId).append("\")\n");
        sb.append("  println(\"Scala test completed successfully\")\n");
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Represents a type in the IR.
     */
    public static class IRType {
        private final String name;
        private final TypeSystem.TypeCategory category;
        private final List<String> typeParameters;
        private final String superType;
        private final List<String> interfaces;
        private final Map<String, String> fields;
        private final Map<String, String> methods;
        private final boolean isAbstract;
        private final boolean isFinal;
        private final boolean isInterface;
        private final boolean isEnum;
        
        public IRType(String name, TypeSystem.TypeCategory category) {
            this.name = name;
            this.category = category;
            this.typeParameters = new ArrayList<>();
            this.superType = null;
            this.interfaces = new ArrayList<>();
            this.fields = new HashMap<>();
            this.methods = new HashMap<>();
            this.isAbstract = false;
            this.isFinal = false;
            this.isInterface = false;
            this.isEnum = false;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public TypeSystem.TypeCategory getCategory() { return category; }
        public List<String> getTypeParameters() { return typeParameters; }
        public String getSuperType() { return superType; }
        public List<String> getInterfaces() { return interfaces; }
        public Map<String, String> getFields() { return fields; }
        public Map<String, String> getMethods() { return methods; }
        public boolean isAbstract() { return isAbstract; }
        public boolean isFinal() { return isFinal; }
        public boolean isInterface() { return isInterface; }
        public boolean isEnum() { return isEnum; }
    }
    
    /**
     * Represents a function in the IR.
     */
    public static class IRFunction {
        private final String name;
        private final String returnType;
        private final List<String> parameters;
        private final String body;
        private final boolean isStatic;
        private final boolean isAbstract;
        private final boolean isFinal;
        private final String visibility;
        
        public IRFunction(String name, String returnType, List<String> parameters, String body) {
            this.name = name;
            this.returnType = returnType;
            this.parameters = new ArrayList<>(parameters);
            this.body = body;
            this.isStatic = false;
            this.isAbstract = false;
            this.isFinal = false;
            this.visibility = "public";
        }
        
        // Getters
        public String getName() { return name; }
        public String getReturnType() { return returnType; }
        public List<String> getParameters() { return parameters; }
        public String getBody() { return body; }
        public boolean isStatic() { return isStatic; }
        public boolean isAbstract() { return isAbstract; }
        public boolean isFinal() { return isFinal; }
        public String getVisibility() { return visibility; }
    }
}