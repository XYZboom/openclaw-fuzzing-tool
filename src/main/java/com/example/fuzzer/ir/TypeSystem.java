package com.example.fuzzer.ir;

import java.util.*;

/**
 * Type system modeling for cross-language interoperability testing.
 * This class defines the type systems of Java, Kotlin, and Scala,
 * including their primitive types, object types, and type relationships.
 */
public class TypeSystem {
    
    public enum Language {
        JAVA, KOTLIN, SCALA
    }
    
    public enum TypeCategory {
        PRIMITIVE, STRING, OBJECT, ARRAY, GENERIC, FUNCTIONAL, ENUM, INTERFACE, CLASS
    }
    
    public enum Variance {
        INVARIANT, COVARIANT, CONTRAVARIANT
    }
    
    // Primitive types supported by all languages
    public static final Set<String> PRIMITIVE_TYPES;
    static {
        Set<String> primitives = new HashSet<>();
        primitives.add("boolean");
        primitives.add("byte");
        primitives.add("short");
        primitives.add("int");
        primitives.add("long");
        primitives.add("float");
        primitives.add("double");
        primitives.add("char");
        PRIMITIVE_TYPES = Collections.unmodifiableSet(primitives);
    }
    
    // Common object types and their language support
    public static final Map<String, Set<Language>> COMMON_TYPES;
    static {
        Map<String, Set<Language>> types = new HashMap<>();
        
        Set<Language> allLanguages = new HashSet<>();
        allLanguages.add(Language.JAVA);
        allLanguages.add(Language.KOTLIN);
        allLanguages.add(Language.SCALA);
        types.put("String", allLanguages);
        types.put("Object", allLanguages);
        
        Set<Language> kotlinOnly = new HashSet<>();
        kotlinOnly.add(Language.KOTLIN);
        types.put("Any", kotlinOnly);
        types.put("Unit", kotlinOnly);
        types.put("Nothing", kotlinOnly);
        
        Set<Language> scalaOnly = new HashSet<>();
        scalaOnly.add(Language.SCALA);
        types.put("AnyRef", scalaOnly);
        types.put("Null", scalaOnly);
        
        Set<Language> kotlinScala = new HashSet<>();
        kotlinScala.add(Language.KOTLIN);
        kotlinScala.add(Language.SCALA);
        types.put("Unit", kotlinScala);
        
        Set<Language> javaOnly = new HashSet<>();
        javaOnly.add(Language.JAVA);
        types.put("void", javaOnly);
        
        COMMON_TYPES = Collections.unmodifiableMap(types);
    }
    
    /**
     * Represents a type in the cross-language type system.
     */
    public static class Type {
        private final String name;
        private final TypeCategory category;
        private final Set<Language> supportedLanguages;
        private final List<Type> typeParameters;
        private final boolean isArray;
        private final int arrayDimensions;
        private final boolean isNullable;
        private final Variance variance;
        private final Type superType;
        private final List<Type> interfaces;
        
        public Type(String name, TypeCategory category, Set<Language> supportedLanguages) {
            this(name, category, supportedLanguages, Collections.<Type>emptyList(), 
                 false, 0, false, Variance.INVARIANT, null, Collections.<Type>emptyList());
        }
        
        public Type(String name, TypeCategory category, Set<Language> supportedLanguages,
                   List<Type> typeParameters, boolean isArray, int arrayDimensions,
                   boolean isNullable, Variance variance, Type superType, List<Type> interfaces) {
            this.name = name;
            this.category = category;
            this.supportedLanguages = Collections.unmodifiableSet(new HashSet<>(supportedLanguages));
            this.typeParameters = Collections.unmodifiableList(new ArrayList<>(typeParameters));
            this.isArray = isArray;
            this.arrayDimensions = arrayDimensions;
            this.isNullable = isNullable;
            this.variance = variance;
            this.superType = superType;
            this.interfaces = Collections.unmodifiableList(new ArrayList<>(interfaces));
        }
        
        // Getters
        public String getName() { return name; }
        public TypeCategory getCategory() { return category; }
        public Set<Language> getSupportedLanguages() { return supportedLanguages; }
        public List<Type> getTypeParameters() { return typeParameters; }
        public boolean isArray() { return isArray; }
        public int getArrayDimensions() { return arrayDimensions; }
        public boolean isNullable() { return isNullable; }
        public Variance getVariance() { return variance; }
        public Type getSuperType() { return superType; }
        public List<Type> getInterfaces() { return interfaces; }
        
        // Factory methods for common types
        public static Type createPrimitive(String name) {
            Set<Language> allLangs = new HashSet<>();
            allLangs.add(Language.JAVA);
            allLangs.add(Language.KOTLIN);
            allLangs.add(Language.SCALA);
            return new Type(name, TypeCategory.PRIMITIVE, allLangs);
        }
        
        public static Type createString() {
            Set<Language> allLangs = new HashSet<>();
            allLangs.add(Language.JAVA);
            allLangs.add(Language.KOTLIN);
            allLangs.add(Language.SCALA);
            return new Type("String", TypeCategory.STRING, allLangs);
        }
        
        public static Type createObject() {
            Set<Language> allLangs = new HashSet<>();
            allLangs.add(Language.JAVA);
            allLangs.add(Language.KOTLIN);
            allLangs.add(Language.SCALA);
            return new Type("Object", TypeCategory.OBJECT, allLangs);
        }
        
        public static Type createArray(Type elementType, int dimensions) {
            Set<Language> langs = new HashSet<>(elementType.getSupportedLanguages());
            return new Type(elementType.getName() + "[]", TypeCategory.ARRAY, langs,
                           Collections.singletonList(elementType), true, dimensions, 
                           elementType.isNullable(), Variance.INVARIANT, null, 
                           Collections.<Type>emptyList());
        }
        
        public static Type createGeneric(String name, List<Type> parameters, Set<Language> langs) {
            return new Type(name, TypeCategory.GENERIC, langs, parameters,
                           false, 0, false, Variance.INVARIANT, null, 
                           Collections.<Type>emptyList());
        }
        
        public Type withNullable(boolean nullable) {
            return new Type(this.name, this.category, this.supportedLanguages,
                           this.typeParameters, this.isArray, this.arrayDimensions,
                           nullable, this.variance, this.superType, this.interfaces);
        }
        
        public Type withArray(int dimensions) {
            Set<Language> langs = new HashSet<>(this.supportedLanguages);
            return new Type(this.name + "[]", TypeCategory.ARRAY, langs,
                           Collections.singletonList(this), true, dimensions,
                           this.isNullable(), Variance.INVARIANT, null, 
                           Collections.<Type>emptyList());
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(name);
            if (isArray) {
                for (int i = 0; i < arrayDimensions; i++) {
                    sb.append("[]");
                }
            }
            if (!typeParameters.isEmpty()) {
                sb.append("<");
                for (int i = 0; i < typeParameters.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(typeParameters.get(i).toString());
                }
                sb.append(">");
            }
            return sb.toString();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Type type = (Type) obj;
            return isArray == type.isArray &&
                   arrayDimensions == type.arrayDimensions &&
                   isNullable == type.isNullable &&
                   Objects.equals(name, type.name) &&
                   category == type.category &&
                   Objects.equals(supportedLanguages, type.supportedLanguages) &&
                   Objects.equals(typeParameters, type.typeParameters) &&
                   variance == type.variance &&
                   Objects.equals(superType, type.superType) &&
                   Objects.equals(interfaces, type.interfaces);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, category, supportedLanguages, typeParameters, 
                              isArray, arrayDimensions, isNullable, variance, superType, interfaces);
        }
    }
    
    /**
     * Checks if a type is compatible between two languages.
     */
    public static boolean isCompatible(Type type, Language lang1, Language lang2) {
        return type.getSupportedLanguages().contains(lang1) && 
               type.getSupportedLanguages().contains(lang2);
    }
    
    /**
     * Gets the equivalent type name in the target language.
     */
    public static String getEquivalentTypeName(String typeName, Language sourceLang, Language targetLang) {
        if (sourceLang == targetLang) {
            return typeName;
        }
        
        // Handle language-specific type mappings
        switch (typeName) {
            case "Any":
                return targetLang == Language.JAVA ? "Object" : 
                       targetLang == Language.SCALA ? "AnyRef" : "Any";
            case "Object":
                return targetLang == Language.KOTLIN ? "Any" : 
                       targetLang == Language.SCALA ? "AnyRef" : "Object";
            case "AnyRef":
                return targetLang == Language.JAVA ? "Object" : 
                       targetLang == Language.KOTLIN ? "Any" : "AnyRef";
            case "Unit":
                return targetLang == Language.JAVA ? "void" : "Unit";
            case "void":
                return targetLang == Language.KOTLIN || targetLang == Language.SCALA ? "Unit" : "void";
            default:
                return typeName;
        }
    }
}