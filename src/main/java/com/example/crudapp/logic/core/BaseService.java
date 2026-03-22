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
public abstract class BaseService<Entity extends BaseEntity> {
    
    protected abstract BaseRepository<Entity> getRepository();

    @Transactional(readOnly = true)
    public List<Entity> findAll() {
        return getRepository().findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Entity> findById(Long id) {
        return getRepository().findById(id);
    }

    @Transactional
    public Entity save(Entity entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        getRepository().deleteById(id);
    }

    @Transactional
    public Entity update(Long id, Entity entity) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Entity not found");
        }
        entity.setId(id);
        return getRepository().save(entity);
    }
}
