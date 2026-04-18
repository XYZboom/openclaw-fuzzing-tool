package com.example.fuzzer.generators;

import java.util.*;

/**
 * Utility methods for code generators.
 */
public class GeneratorUtils {
    
    /**
     * Creates an unmodifiable set from the given elements (Java 8 compatible).
     */
    @SafeVarargs
    public static <T> Set<T> asSet(T... elements) {
        Set<T> set = new HashSet<>();
        for (T element : elements) {
            set.add(element);
        }
        return Collections.unmodifiableSet(set);
    }
    
    /**
     * Creates an unmodifiable map from key-value pairs (Java 8 compatible).
     */
    @SafeVarargs
    public static <K, V> Map<K, V> asMap(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value pairs must be even number of arguments");
        }
        
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            map.put((K) keyValuePairs[i], (V) keyValuePairs[i + 1]);
        }
        return Collections.unmodifiableMap(map);
    }
    
    /**
     * Capitalizes the first letter of a string.
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * Generates a random identifier.
     */
    public static String generateRandomId() {
        return "Test" + System.currentTimeMillis() + "_" + Math.abs(new Random().nextInt());
    }
    
    /**
     * Escapes a string for use in source code.
     */
    public static String escapeString(String str) {
        if (str == null) {
            return "null";
        }
        return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
}