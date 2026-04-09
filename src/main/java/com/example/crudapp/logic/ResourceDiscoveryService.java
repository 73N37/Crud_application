package com.example.crudapp.logic;

import com.example.crudapp.data.core.BaseEntity;
import com.example.crudapp.infrastructure.annotations.CrudResource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * [LOGIC LAYER]
 * Automatically discovers classes annotated with @CrudResource on the classpath.
 * Eliminates manual registration in CrudApplication.
 */
@Service
public class ResourceDiscoveryService {

    @SuppressWarnings("unchecked")
    public List<Class<? extends BaseEntity>> discoverResources(String basePackage) {
        List<Class<? extends BaseEntity>> resources = new ArrayList<>();
        
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CrudResource.class));

        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
            try {
                resources.add((Class<? extends BaseEntity>) Class.forName(bd.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                // Log error
            }
        }
        return resources;
    }
}
