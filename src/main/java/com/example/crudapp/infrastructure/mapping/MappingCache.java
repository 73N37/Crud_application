package com.example.crudapp.infrastructure.mapping;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ⚡ PERFORMANCE OPTIMIZATION
 * Singleton cache for storing record constructors using MethodHandles.
 * MethodHandles are closer to direct JVM instructions than standard Reflection.
 */
public class MappingCache {

    private static final Map<Class<?>, RecordMapping<?>> cache = new ConcurrentHashMap<>();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public static class RecordMapping<R extends Record> {
        private final MethodHandle constructorHandle;
        private final String[] parameterNames;

        public RecordMapping(MethodHandle constructorHandle, String[] parameterNames) {
            this.constructorHandle = constructorHandle;
            this.parameterNames = parameterNames;
        }

        public MethodHandle getConstructorHandle() { return constructorHandle; }
        public String[] getParameterNames() { return parameterNames; }
    }

    @SuppressWarnings("unchecked")
    public static <R extends Record> RecordMapping<R> get(Class<R> recordClass) {
        return (RecordMapping<R>) cache.computeIfAbsent(recordClass, clazz -> {
            try {
                var components = clazz.getRecordComponents();
                Class<?>[] paramTypes = new Class<?>[components.length];
                String[] paramNames = new String[components.length];

                for (int i = 0; i < components.length; i++) {
                    paramTypes[i] = components[i].getType();
                    paramNames[i] = components[i].getName();
                }

                // Create a MethodHandle for the record constructor
                MethodType methodType = MethodType.methodType(void.class, paramTypes);
                MethodHandle handle = LOOKUP.findConstructor(clazz, methodType);

                return new RecordMapping<>(handle, paramNames);
            } catch (Exception e) {
                throw new RuntimeException("Failed to cache mapping for record: " + recordClass.getName(), e);
            }
        });
    }
}
