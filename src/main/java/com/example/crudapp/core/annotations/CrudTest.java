package com.example.crudapp.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a test class for automated CRUD integration testing.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudTest {
    /**
     * The resource path to test (e.g., "products").
     */
    String path();

    /**
     * The entity class being tested.
     */
    Class<?> entity();
}
