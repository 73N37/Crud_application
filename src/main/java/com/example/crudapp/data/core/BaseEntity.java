package com.example.crudapp.data.core;

import com.example.crudapp.infrastructure.annotations.Children;
import com.example.crudapp.infrastructure.annotations.CrudResource;
import com.example.crudapp.infrastructure.annotations.Parent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * [DATA LAYER]
 * Base entity providing JPA identity, hierarchical relationships, and automatic record mapping.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BaseEntity parent;

    @Children
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BaseEntity> children = new ArrayList<>();

    /**
     * Helper to get the grandparent of this entity.
     */
    public Optional<BaseEntity> getGrandparent() {
        return Optional.ofNullable(parent).map(BaseEntity::getParent);
    }

    /**
     * Helper to get all grandchildren of this entity.
     */
    public List<BaseEntity> getGrandchildren() {
        return children.stream()
                .flatMap(child -> child.getChildren().stream())
                .collect(Collectors.toList());
    }

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
            values[i] = resolveValue(name, components[i].getType());
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

    private Object resolveValue(String componentName, Class<?> componentType) {
        // Special case: parentId
        if ("parentId".equals(componentName)) {
            return findAnnotatedField(Parent.class)
                    .map(field -> {
                        try {
                            field.setAccessible(true);
                            BaseEntity p = (BaseEntity) field.get(this);
                            return p != null ? p.getId() : null;
                        } catch (IllegalAccessException e) {
                            return null;
                        }
                    }).orElse(null);
        }

        // Special case: grandparentId
        if ("grandparentId".equals(componentName)) {
            return getGrandparent().map(BaseEntity::getId).orElse(null);
        }

        // Regular fields
        try {
            Field field = findField(this.getClass(), componentName);
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception e) {
            // If field not found or other error, return null or handle accordingly
            return null;
        }
    }

    private Optional<Field> findAnnotatedField(Class<? extends java.lang.annotation.Annotation> annotation) {
        Class<?> current = this.getClass();
        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    return Optional.of(field);
                }
            }
            current = current.getSuperclass();
        }
        return Optional.empty();
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
