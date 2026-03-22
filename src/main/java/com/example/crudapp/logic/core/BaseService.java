package com.example.crudapp.logic.core;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.data.core.BaseRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * [LOGIC LAYER]
 * Abstract business logic service.
 */
public abstract class BaseService<T extends BaseEntity> {
    
    protected abstract BaseRepository<T> getRepository();

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Transactional(readOnly = true)
    public Optional<T> findById(Long id) {
        return getRepository().findById(id);
    }

    @Transactional
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        getRepository().deleteById(id);
    }

    @Transactional
    public T update(Long id, T entity) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Entity not found");
        }
        entity.setId(id);
        return getRepository().save(entity);
    }
}
