package com.example.crudapp.infrastructure.query;

import com.example.crudapp.data.core.BaseEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ⚡ PERFORMANCE OPTIMIZATION: Dynamic Query Mapping
 * Automatically converts URL query parameters into JPA Predicates.
 * Supports basic 'equals' filtering for any entity field.
 */
public class GenericSpecification<T extends BaseEntity> implements Specification<T> {

    private final Map<String, String> params;

    public GenericSpecification(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        params.forEach((key, value) -> {
            // Simple logic: if field exists on entity, add equality predicate.
            // In a production app, we would use reflection to validate field existence.
            try {
                predicates.add(cb.equal(root.get(key), value));
            } catch (Exception ignored) {
                // Ignore parameters that don't match entity fields
            }
        });

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
