package com.example.crudapp.infrastructure.mapping;

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

    public static class RecordMapping<R extends Record> {
        private final Constructor<R> constructor;
        private final Class<?>[] parameterTypes;
        private final String[] parameterNames;

        public RecordMapping(Constructor<R> constructor, Class<?>[] parameterTypes, String[] parameterNames) {
            this.constructor = constructor;
            this.parameterTypes = parameterTypes;
            this.parameterNames = parameterNames;
        }

        public Constructor<R> getConstructor() { return constructor; }
        public String[] getParameterNames() { return parameterNames; }

        public static <R extends Record> RecordMappingBuilder<R> builder() {
            return new RecordMappingBuilder<>();
        }

        public static class RecordMappingBuilder<R extends Record> {
            private Constructor<R> constructor;
            private Class<?>[] parameterTypes;
            private String[] parameterNames;

            public RecordMappingBuilder<R> constructor(Constructor<R> constructor) {
                this.constructor = constructor;
                return this;
            }

            public RecordMappingBuilder<R> parameterTypes(Class<?>[] parameterTypes) {
                this.parameterTypes = parameterTypes;
                return this;
            }

            public RecordMappingBuilder<R> parameterNames(String[] parameterNames) {
                this.parameterNames = parameterNames;
                return this;
            }

            public RecordMapping<R> build() {
                return new RecordMapping<>(constructor, parameterTypes, parameterNames);
            }
        }
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
