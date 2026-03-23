package com.example.crudapp.infrastructure.mapping;

import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ⚡ PERFORMANCE OPTIMIZATION
 * Singleton cache for storing record constructors and field mappings.
 * Reduces the CPU overhead of reflection during entity-to-DTO conversion.
 */
public class MappingCache {

    private static final Map<Class<?>, RecordMapping<?>> cache = new ConcurrentHashMap<>();

    @Getter
    @Builder
    public static class RecordMapping<R extends Record> {
        private final Constructor<R> constructor;
        private final Class<?>[] parameterTypes;
        private final String[] parameterNames;
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

                Constructor<R> constructor = (Constructor<R>) clazz.getDeclaredConstructor(paramTypes);
                constructor.setAccessible(true);

                return RecordMapping.<R>builder()
                        .constructor(constructor)
                        .parameterTypes(paramTypes)
                        .parameterNames(paramNames)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to cache mapping for record: " + recordClass.getName(), e);
            }
        });
    }
}
