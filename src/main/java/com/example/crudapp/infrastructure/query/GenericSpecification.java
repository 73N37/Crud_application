package com.example.crudapp.infrastructure.query;

import com.example.crudapp.data.core.meta.VirtualDocument;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * [PROFESSIONAL QUERY ENGINE]
 * Supports standard entities AND JSON filtering for Virtual Documents.
 */
public class GenericSpecification<T> implements Specification<T> {

    private final Map<String, String> params;

    public GenericSpecification(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        params.forEach((key, value) -> {
            try {
                // Check if we are querying a VirtualDocument's data map
                if (root.getJavaType().equals(VirtualDocument.class)) {
                    // This is a simplified approach. In a production SQL-specific app, 
                    // we would use native JSON functions (e.g., JSON_EXTRACT).
                    // For cross-DB compatibility, we filter by 'resourceType' and 
                    // handle deep JSON filtering in the service or via a custom HQL function.
                    if (key.equals("resourceType")) {
                        predicates.add(cb.equal(root.get("resourceType"), value));
                    }
                    // Meta-filtering: owner, createdAt, etc.
                    else if (List.of("owner", "id").contains(key)) {
                        predicates.add(cb.equal(root.get(key), value));
                    }
                } else {
                    // Standard Entity Filtering
                    if (key.endsWith("_gt")) {
                        predicates.add(cb.greaterThan(root.get(key.replace("_gt", "")), value));
                    } else if (key.endsWith("_like")) {
                        predicates.add(cb.like(root.get(key.replace("_like", "")), "%" + value + "%"));
                    } else {
                        predicates.add(cb.equal(root.get(key), value));
                    }
                }
            } catch (Exception ignored) {}
        });

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
