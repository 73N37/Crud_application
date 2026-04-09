package com.example.crudapp.logic;

import com.example.crudapp.data.core.meta.ApiSchema;
import com.example.crudapp.data.core.meta.ApiSchemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ⚡ PERFORMANCE OPTIMIZATION
 * In-memory cache for API Schemas to avoid double-hitting the DB on every request.
 */
@Service
public class SchemaCacheService {

    private static final Logger log = LoggerFactory.getLogger(SchemaCacheService.class);
    private final ApiSchemaRepository schemaRepository;
    private final Map<String, ApiSchema> cache = new ConcurrentHashMap<>();

    public SchemaCacheService(ApiSchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    public Optional<ApiSchema> getSchema(String path) {
        if (cache.containsKey(path)) {
            log.debug("⚡ Schema cache hit for: [{}]", path);
            return Optional.of(cache.get(path));
        }

        return schemaRepository.findByPath(path).map(schema -> {
            log.info("📥 Schema cache miss. Loading from DB: [{}]", path);
            cache.put(path, schema);
            return schema;
        });
    }

    public void evict(String path) {
        log.info("🗑️ Evicting schema from cache: [{}]", path);
        cache.remove(path);
    }
}
