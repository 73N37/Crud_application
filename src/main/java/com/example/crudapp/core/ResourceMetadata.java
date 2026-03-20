package com.example.crudapp.core;

import com.example.crudapp.core.annotations.CrudResource;
import lombok.*;

/**
 * Metadata derived from the @CrudResource annotation.
 */
@Getter
@Builder
@AllArgsConstructor
public class ResourceMetadata<T extends BaseEntity, R extends Record> {
    private final Class<T> entityClass;
    private final Class<R> dtoClass;
    private final String basePath;
    private final BaseRepository<T> repository;
    private final BaseService<T> service;
}
