package com.example.crudapp.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseServiceTest {

    @Mock
    private BaseRepository<TestEntity> repository;

    private TestService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TestService(repository);
    }

    @Test
    void findById_ShouldReturnEntity_WhenExists() {
        TestEntity entity = new TestEntity();
        entity.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<TestEntity> found = service.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void update_ShouldThrowException_WhenNotExists() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.update(1L, new TestEntity()));
    }

    // Concrete test classes
    static class TestEntity extends BaseEntity {}
    
    static class TestService extends BaseService<TestEntity> {
        private final BaseRepository<TestEntity> repository;

        TestService(BaseRepository<TestEntity> repository) {
            this.repository = repository;
        }

        @Override
        protected BaseRepository<TestEntity> getRepository() {
            return repository;
        }
    }
}
