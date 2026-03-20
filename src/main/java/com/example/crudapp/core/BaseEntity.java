package com.example.crudapp.core;

import com.example.crudapp.core.annotations.CrudResource;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Dynamically converts this entity to its associated Record DTO.
     */
    @SuppressWarnings("unchecked")
    public <R extends Record> R toRecord() {
        if (!this.getClass().isAnnotationPresent(CrudResource.class)) {
            throw new RuntimeException("No @CrudResource annotation present on " + this.getClass().getName());
        }

        Class<R> recordClass = (Class<R>) this.getClass().getAnnotation(CrudResource.class).dto();
        RecordComponent[] components = recordClass.getRecordComponents();
        Object[] values = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            String name = components[i].getName();
            try {
                Field field = findField(this.getClass(), name);
                field.setAccessible(true);
                values[i] = field.get(this);
            } catch (Exception e) {
                throw new RuntimeException("Failed to map field " + name + " to record " + recordClass.getName(), e);
            }
        }

        try {
            Class<?>[] paramTypes = Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class<?>[]::new);
            Constructor<R> constructor = recordClass.getDeclaredConstructor(paramTypes);
            return constructor.newInstance(values);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate record " + recordClass.getName(), e);
        }
    }

    private Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), name);
            }
            throw e;
        }
    }
}
