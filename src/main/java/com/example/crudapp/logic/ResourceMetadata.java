package com.example.crudapp.logic;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.logic.core.BaseService;
import com.example.crudapp.logic.core.CrudInterceptor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

/**
 * [LOGIC LAYER]
 * Metadata bridge between Data and Interface.
 * Enhanced with Interceptors and Rich Field Metadata.
 */
public class ResourceMetadata<T extends BaseEntity, R extends Record> {
    private final Class<T> entityClass;
    private final Class<R> dtoClass;
    private final String basePath;
    private final JpaRepository<T, Long> repository;
    private final BaseService<T> service;
    private final CrudInterceptor<T> interceptor;
    private final List<FieldInfo> fields;

    public ResourceMetadata(Class<T> entityClass, Class<R> dtoClass, String basePath, 
                            JpaRepository<T, Long> repository, BaseService<T> service,
                            CrudInterceptor<T> interceptor, List<FieldInfo> fields) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
        this.basePath = basePath;
        this.repository = repository;
        this.service = service;
        this.interceptor = interceptor;
        this.fields = fields;
    }

    public Class<T> getEntityClass() { return entityClass; }
    public Class<R> getDtoClass() { return dtoClass; }
    public String getBasePath() { return basePath; }
    public JpaRepository<T, Long> getRepository() { return repository; }
    public BaseService<T> getService() { return service; }
    public CrudInterceptor<T> getInterceptor() { return interceptor; }
    public List<FieldInfo> getFields() { return fields; }

    public record FieldInfo(String name, String type, boolean required, Map<String, Object> constraints) {}

    public static ResourceMetadataBuilder builder() {
        return new ResourceMetadataBuilder();
    }

    public static class ResourceMetadataBuilder<T extends BaseEntity, R extends Record> {
        private Class<T> entityClass;
        private Class<R> dtoClass;
        private String basePath;
        private JpaRepository<T, Long> repository;
        private BaseService<T> service;
        private CrudInterceptor<T> interceptor;
        private List<FieldInfo> fields;

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

        public ResourceMetadataBuilder<T, R> interceptor(CrudInterceptor<T> interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public ResourceMetadataBuilder<T, R> fields(List<FieldInfo> fields) {
            this.fields = fields;
            return this;
        }

        public ResourceMetadata<T, R> build() {
            return new ResourceMetadata<>(entityClass, dtoClass, basePath, repository, service, interceptor, fields);
        }
    }
}
