package com.example.crudapp.infrastructure.annotations;

import com.example.crudapp.logic.core.BaseService;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark an entity as a CRUD resource.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudResource {
    String path();
    Class<?> dto();
    
    /**
     * 🏗️ ARCHITECTURE OPTIMIZATION: Custom Service
     * Allows developers to provide a specific Service implementation.
     */
    Class<? extends BaseService> service() default BaseService.class;
}
