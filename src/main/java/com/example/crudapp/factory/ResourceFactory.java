package com.example.crudapp.factory;

import com.example.crudapp.core.BaseEntity;

/**
 * Abstract Factory interface for creating entities.
 */
public interface ResourceFactory<T extends BaseEntity, D> {
    T createEntity(D dto);
}
