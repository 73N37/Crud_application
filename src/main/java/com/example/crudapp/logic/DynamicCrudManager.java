package com.example.crudapp.logic;

import com.example.crudapp.api.UniversalCrudController;
import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.infrastructure.annotations.CrudResource;
import com.example.crudapp.logic.core.BaseService;
import com.example.crudapp.logic.core.CrudInterceptor;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class DynamicCrudManager {

    private static final Logger log = LoggerFactory.getLogger(DynamicCrudManager.class);
    private final ApplicationContext context;
    private final EntityManager entityManager;
    private final RequestMappingHandlerMapping handlerMapping;
    private final Map<String, ResourceMetadata<?, ?>> resources = new HashMap<>();

    public DynamicCrudManager(ApplicationContext context, 
                              EntityManager entityManager, 
                              RequestMappingHandlerMapping handlerMapping) {
        this.context = context;
        this.entityManager = entityManager;
        this.handlerMapping = handlerMapping;
    }

    public void registerResource(Class<? extends BaseEntity> entityClass) {
        if (!entityClass.isAnnotationPresent(CrudResource.class)) return;

        CrudResource annotation = entityClass.getAnnotation(CrudResource.class);
        String path = annotation.path();
        log.info("🚀 Registering dynamic resource: [{}] at path [/api/v2/{}]", entityClass.getSimpleName(), path);

        Class<?> dtoClass = annotation.dto();
        Class<? extends BaseService> serviceClass = annotation.service();

        JpaRepository repository = new SimpleJpaRepository(entityClass, entityManager);

        BaseService service;
        try {
            service = context.getBean(serviceClass);
        } catch (Exception e) {
            service = new BaseService() {
                @Override
                protected JpaRepository getRepository() {
                    return repository;
                }
            };
        }

        // 🧠 META-PROGRAMMING: Discover generic interceptor for this entity
        CrudInterceptor interceptor = findInterceptor(entityClass);
        log.debug("🧠 Interceptor for [{}]: {}", entityClass.getSimpleName(), interceptor.getClass().getSimpleName());

        // 🔍 META-PROGRAMMING: Deep metadata inspection via Reflection
        List<ResourceMetadata.FieldInfo> fieldMetadata = inspectFields(dtoClass);

        ResourceMetadata metadata = ResourceMetadata.builder()
                .entityClass(entityClass)
                .dtoClass(dtoClass)
                .basePath(path)
                .repository(repository)
                .service(service)
                .interceptor(interceptor)
                .fields(fieldMetadata)
                .build();

        resources.put(path, metadata);
        registerExplicitRoutes(path);
    }

    private CrudInterceptor<?> findInterceptor(Class<? extends BaseEntity> entityClass) {
        Map<String, CrudInterceptor> beans = context.getBeansOfType(CrudInterceptor.class);
        for (CrudInterceptor bean : beans.values()) {
            ResolvableType type = ResolvableType.forClass(bean.getClass()).as(CrudInterceptor.class);
            Class<?> genericType = type.getGeneric(0).resolve();
            if (genericType != null && genericType.isAssignableFrom(entityClass)) {
                return bean;
            }
        }
        return new CrudInterceptor<>() {}; // Default no-op
    }

    private List<ResourceMetadata.FieldInfo> inspectFields(Class<?> clazz) {
        List<ResourceMetadata.FieldInfo> infos = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            Map<String, Object> constraints = new HashMap<>();
            boolean required = field.isAnnotationPresent(NotNull.class) || field.isAnnotationPresent(NotBlank.class);
            
            if (field.isAnnotationPresent(Size.class)) {
                Size size = field.getAnnotation(Size.class);
                constraints.put("min", size.min());
                constraints.put("max", size.max());
            }
            if (field.isAnnotationPresent(Positive.class)) {
                constraints.put("positive", true);
            }

            infos.add(new ResourceMetadata.FieldInfo(
                    field.getName(),
                    field.getType().getSimpleName(),
                    required,
                    constraints
            ));
        }
        return infos;
    }

    private void registerExplicitRoutes(String path) {
        try {
            UniversalCrudController controller = context.getBean(UniversalCrudController.class);
            String basePath = "/api/v2/" + path;
            log.info("📍 Mapping explicit routes for resource: [{}]", path);
            registerMapping(basePath, "getAll", RequestMethod.GET, controller);
            registerMapping(basePath, "create", RequestMethod.POST, controller);
            registerMapping(basePath + "/{id}", "getById", RequestMethod.GET, controller);
            registerMapping(basePath + "/{id}", "update", RequestMethod.PUT, controller);
            registerMapping(basePath + "/{id}", "delete", RequestMethod.DELETE, controller);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register explicit routes for: " + path, e);
        }
    }

    private void registerMapping(String path, String methodName, RequestMethod method, Object controller) throws NoSuchMethodException {
        Method handlerMethod = Arrays.stream(UniversalCrudController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Method not found: " + methodName));
        
        RequestMappingInfo mappingInfo = RequestMappingInfo.paths(path).methods(method).build();
        handlerMapping.registerMapping(mappingInfo, controller, handlerMethod);
    }

    public Map<String, ResourceMetadata<?, ?>> getResources() {
        return Collections.unmodifiableMap(resources);
    }

    public ResourceMetadata<?, ?> getMetadata(String path) {
        return resources.get(path);
    }
}
