package com.example.crudapp.logic.core;

import com.example.crudapp.data.core.BaseEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * [LOGIC LAYER]
 * Abstract business logic service with dynamic specification support.
 */
public abstract class BaseService<T extends BaseEntity> {

    protected abstract JpaRepository<T, Long> getRepository();

    @SuppressWarnings("unchecked")
    protected JpaSpecificationExecutor<T> getSpecificationExecutor() {
        return (JpaSpecificationExecutor<T>) getRepository();
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    /**
     * ⚡ PERFORMANCE OPTIMIZATION: Dynamic Query Support with Pagination
     */
    @Transactional(readOnly = true)
    public List<T> findAll(Specification<T> spec) {
        return getSpecificationExecutor().findAll(spec);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<T> findAll(Specification<T> spec, org.springframework.data.domain.Pageable pageable) {
        return getSpecificationExecutor().findAll(spec, pageable);
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
