package com.loglite.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread-bound Mapped Diagnostic Context storage.
 * Each thread has its own independent map of key-value metadata.
 */
public final class MDC {

    private static final ThreadLocal<Map<String, String>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    private MDC() {
    }

    /**
     * Stores a value for the given key in the current thread's context.
     */
    public static void put(String key, String value) {
        Map<String, String> copy = new HashMap<>(CONTEXT.get());
        copy.put(key, value);
        CONTEXT.set(copy);
    }

    /**
     * @return the value bound to the key in the current thread's context, or null if absent
     */
    public static String get(String key) {
        return CONTEXT.get().get(key);
    }

    /**
     * Removes the key from the current thread's context.
     */
    public static void remove(String key) {
        Map<String, String> copy = new HashMap<>(CONTEXT.get());
        copy.remove(key);
        CONTEXT.set(copy);
    }

    /**
     * Clears all entries from the current thread's context.
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * @return an unmodifiable snapshot of the current thread's context
     */
    public static Map<String, String> getCopyOfContextMap() {
        return Collections.unmodifiableMap(new HashMap<>(CONTEXT.get()));
    }
}
