package com.example.crudapp.data.core.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiSchemaRepository extends JpaRepository<ApiSchema, Long> {
    Optional<ApiSchema> findByPath(String path);
}
