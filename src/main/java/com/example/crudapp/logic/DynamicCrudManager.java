package com.example.crudapp.logic;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.data.core.BaseRepository;
import com.example.crudapp.infrastructure.annotations.CrudResource;
import com.example.crudapp.logic.core.BaseService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * [LOGIC LAYER]
 * Orchestrates the registration of dynamic CRUD resources.
 */
@Component
@RequiredArgsConstructor
public class DynamicCrudManager {

    private final ApplicationContext context;
    private final EntityManager entityManager;
    private final Map<String, ResourceMetadata<?, ?>> resources = new HashMap<>();

    public void registerResource(Class<? extends BaseEntity> entityClass) {
        if (!entityClass.isAnnotationPresent(CrudResource.class)) return;

        CrudResource annotation = entityClass.getAnnotation(CrudResource.class);
        String path = annotation.path();
        Class<?> dtoClass = annotation.dto();

        JpaRepositoryFactory factory = new JpaRepositoryFactory(entityManager);
        BaseRepository repository = (BaseRepository) factory.getRepository(entityClass);

        BaseService service = new BaseService() {
            @Override
            protected BaseRepository getRepository() {
                return repository;
            }
        };

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
