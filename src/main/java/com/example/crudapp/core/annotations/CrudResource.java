package com.example.crudapp.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark an entity as a CRUD resource.
 * This is the only annotation a developer needs to use to create a complete CRUD stack.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudResource {
    /**
     * The base path for the API (e.g., "products").
     */
    String path();

    /**
     * The DTO record class associated with this resource.
     */
    Class<?> dto();
}
