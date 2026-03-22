package com.example.crudapp.logic;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.data.core.BaseRepository;
import com.example.crudapp.logic.core.BaseService;
import lombok.*;

/**
 * [LOGIC LAYER]
 * Metadata bridge between Data and Interface.
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
