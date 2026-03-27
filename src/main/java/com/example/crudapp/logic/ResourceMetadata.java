package com.example.crudapp.logic;

import com.example.crudapp.data.core.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.crudapp.logic.core.BaseService;

/**
 * [LOGIC LAYER]
 * Metadata bridge between Data and Interface.
 */
public class ResourceMetadata<T extends BaseEntity, R extends Record> {
    private final Class<T> entityClass;
    private final Class<R> dtoClass;
    private final String basePath;
    private final JpaRepository<T, Long> repository;
    private final BaseService<T> service;

    public ResourceMetadata(Class<T> entityClass, Class<R> dtoClass, String basePath, JpaRepository<T, Long> repository, BaseService<T> service) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
        this.basePath = basePath;
        this.repository = repository;
        this.service = service;
    }

    public Class<T> getEntityClass() { return entityClass; }
    public Class<R> getDtoClass() { return dtoClass; }
    public String getBasePath() { return basePath; }
    public JpaRepository<T, Long> getRepository() { return repository; }
    public BaseService<T> getService() { return service; }

    public static ResourceMetadataBuilder builder() {
        return new ResourceMetadataBuilder();
    }

    public static class ResourceMetadataBuilder<T extends BaseEntity, R extends Record> {
        private Class<T> entityClass;
        private Class<R> dtoClass;
        private String basePath;
        private JpaRepository<T, Long> repository;
        private BaseService<T> service;

        public ResourceMetadataBuilder<T, R> entityClass(Class<T> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public ResourceMetadataBuilder<T, R> dtoClass(Class<R> dtoClass) {
            this.dtoClass = dtoClass;
            return this;
        }

        public ResourceMetadataBuilder<T, R> basePath(String basePath) {
            this.basePath = basePath;
            return this;
        }

        public ResourceMetadataBuilder<T, R> repository(JpaRepository<T, Long> repository) {
            this.repository = repository;
            return this;
        }

        public ResourceMetadataBuilder<T, R> service(BaseService<T> service) {
            this.service = service;
            return this;
        }

        public ResourceMetadata<T, R> build() {
            return new ResourceMetadata<>(entityClass, dtoClass, basePath, repository, service);
        }
    }
}
