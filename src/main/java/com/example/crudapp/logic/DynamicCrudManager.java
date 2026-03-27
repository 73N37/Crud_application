package com.example.crudapp.logic;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.infrastructure.annotations.CrudResource;
import com.example.crudapp.logic.core.BaseService;
import jakarta.persistence.EntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * [LOGIC LAYER]
 * Orchestrates the registration of dynamic CRUD resources.
 */
@Component
public class DynamicCrudManager {

    private final ApplicationContext context;
    private final EntityManager entityManager;
    private final Map<String, ResourceMetadata<?, ?>> resources = new HashMap<>();

    public DynamicCrudManager(ApplicationContext context, EntityManager entityManager) {
        this.context = context;
        this.entityManager = entityManager;
    }

    public Map<String, ResourceMetadata<?, ?>> getResources() {
        return Collections.unmodifiableMap(resources);
    }

    @SuppressWarnings("unchecked")
    public void registerResource(Class<? extends BaseEntity> entityClass) {
        if (!entityClass.isAnnotationPresent(CrudResource.class)) return;

        CrudResource annotation = entityClass.getAnnotation(CrudResource.class);
        String path = annotation.path();
        Class<?> dtoClass = annotation.dto();
        Class<? extends BaseService> serviceClass = annotation.service();

        // ⚡ PERFORMANCE OPTIMIZATION: Dynamic Repository Generation
        // Using SimpleJpaRepository to avoid the "must be an interface" requirement of JpaRepositoryFactory.
        JpaRepository repository = new SimpleJpaRepository(entityClass, entityManager);

        BaseService service;
        
        // 🏗️ ARCHITECTURE OPTIMIZATION: Service Registry
        // First try to find a specialized bean in the context.
        try {
            service = context.getBean(serviceClass);
        } catch (Exception e) {
            // Fallback to anonymous generic service if no bean exists.
            service = new BaseService() {
                @Override
                protected JpaRepository getRepository() {
                    return repository;
                }
            };
        }

        ResourceMetadata metadata = ResourceMetadata.builder()
                .entityClass(entityClass)
                .dtoClass(dtoClass)
                .basePath(path)
                .repository(repository)
                .service(service)
                .build();

        resources.put(path, metadata);
    }

    public ResourceMetadata<?, ?> getMetadata(String path) {
        return resources.get(path);
    }
}
