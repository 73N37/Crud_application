package com.example.crudapp.core;

import com.example.crudapp.core.annotations.CrudResource;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DynamicCrudManager {

    private final ApplicationContext context;
    private final EntityManager entityManager;
    private final RequestMappingHandlerMapping handlerMapping;
    private final Map<String, ResourceMetadata<?, ?>> resources = new HashMap<>();

    /**
     * Scans for @CrudResource annotations and sets up the CRUD stack.
     * In a real app, this would be a post-construct or event listener.
     */
    public void registerResource(Class<? extends BaseEntity> entityClass) {
        if (!entityClass.isAnnotationPresent(CrudResource.class)) return;

        CrudResource annotation = entityClass.getAnnotation(CrudResource.class);
        String path = annotation.path();
        Class<?> dtoClass = annotation.dto();

        // Create Repository
        JpaRepositoryFactory factory = new JpaRepositoryFactory(entityManager);
        BaseRepository repository = (BaseRepository) factory.getRepository(entityClass);

        // Create generic service
        BaseService service = new BaseService() {
            @Override
            protected BaseRepository getRepository() {
                return repository;
            }
        };

        // Register Metadata
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
