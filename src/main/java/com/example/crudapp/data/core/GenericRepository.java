package com.example.crudapp.data.core;

import com.example.crudapp.infrastructure.persistence.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GenericRepository<T extends BaseEntity> {
    private final Class<T> entityClass;

    public GenericRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList();
        }
    }

    public List<T> findAll(int offset, int limit) {
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            TypedQuery<T> query = em.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.getResultList();
        }
    }

    public Optional<T> findById(Long id) {
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(entityClass, id));
        }
    }

    public T save(T entity) {
        executeInTransaction(em -> {
            if (entity.getId() == null) {
                em.persist(entity);
            } else {
                em.merge(entity);
            }
        });
        return entity;
    }

    public void deleteById(Long id) {
        executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public long count() {
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            cq.select(cb.count(cq.from(entityClass)));
            return em.createQuery(cq).getSingleResult();
        }
    }

    private void executeInTransaction(Consumer<EntityManager> action) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            action.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
