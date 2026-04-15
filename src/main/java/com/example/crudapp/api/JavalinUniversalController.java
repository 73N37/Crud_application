package com.example.crudapp.api;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.logic.DynamicCrudManager;
import com.example.crudapp.logic.ResourceMetadata;
import com.example.crudapp.logic.core.BaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JavalinUniversalController {
    private static final Logger log = LoggerFactory.getLogger(JavalinUniversalController.class);
    private final DynamicCrudManager crudManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator;

    public JavalinUniversalController(DynamicCrudManager crudManager) {
        this.crudManager = crudManager;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void getMetadata(Context ctx) {
        log.debug("🔍 Fetching global metadata");
        Map<String, List<ResourceMetadata.FieldInfo>> metadata = crudManager.getResources().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getFields()
                ));
        ctx.json(metadata);
    }

    public void getAll(Context ctx) {
        String resource = ctx.pathParam("resource");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(10);

        ResourceMetadata metadata = getMetadataOrThrow(resource);
        BaseService.Page<? extends BaseEntity> entityPage = metadata.getService().findAll(page, size);

        ctx.header("X-Total-Count", String.valueOf(entityPage.getTotalElements()));
        ctx.json(entityPage.getContent());
    }

    public void getById(Context ctx) {
        String resource = ctx.pathParam("resource");
        Long id = Long.parseLong(ctx.pathParam("id"));

        ResourceMetadata metadata = getMetadataOrThrow(resource);
        metadata.getService().findById(id)
                .ifPresentOrElse(ctx::json, () -> ctx.status(404));
    }

    public void create(Context ctx) {
        String resource = ctx.pathParam("resource");
        ResourceMetadata metadata = getMetadataOrThrow(resource);

        Object dto = ctx.bodyAsClass(metadata.getDtoClass());
        validate(dto);

        BaseEntity entity = (BaseEntity) objectMapper.convertValue(dto, metadata.getEntityClass());
        metadata.getInterceptor().beforeCreate(entity);
        BaseEntity saved = (BaseEntity) metadata.getService().save(entity);
        metadata.getInterceptor().afterCreate(saved);

        ctx.status(201).json(saved);
    }

    public void update(Context ctx) {
        String resource = ctx.pathParam("resource");
        Long id = Long.parseLong(ctx.pathParam("id"));
        ResourceMetadata metadata = getMetadataOrThrow(resource);

        Object dto = ctx.bodyAsClass(metadata.getDtoClass());
        validate(dto);

        BaseEntity entity = (BaseEntity) objectMapper.convertValue(dto, metadata.getEntityClass());
        metadata.getInterceptor().beforeUpdate(entity);
        BaseEntity updated = (BaseEntity) metadata.getService().update(id, entity);
        metadata.getInterceptor().afterUpdate(updated);

        ctx.json(updated);
    }

    public void delete(Context ctx) {
        String resource = ctx.pathParam("resource");
        Long id = Long.parseLong(ctx.pathParam("id"));
        ResourceMetadata metadata = getMetadataOrThrow(resource);

        metadata.getInterceptor().beforeDelete(id);
        metadata.getService().deleteById(id);
        metadata.getInterceptor().afterDelete(id);

        ctx.status(204);
    }

    private ResourceMetadata getMetadataOrThrow(String resource) {
        ResourceMetadata metadata = crudManager.getMetadata(resource);
        if (metadata == null) {
            throw new RuntimeException("Resource not found: " + resource);
        }
        return metadata;
    }

    private void validate(Object dto) {
        Set<ConstraintViolation<Object>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new RuntimeException("Validation failed: " + violations);
        }
    }
}
