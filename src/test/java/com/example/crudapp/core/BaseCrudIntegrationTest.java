package com.example.crudapp.core;

import com.example.crudapp.infrastructure.annotations.CrudTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Base class for all automated CRUD integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseCrudIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String getResourcePath() {
        return "/api/v2/" + this.getClass().getAnnotation(CrudTest.class).path();
    }

    @Test
    void testListAll() throws Exception {
        mockMvc.perform(get(getResourcePath()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(getResourcePath() + "/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNonExistent() throws Exception {
        mockMvc.perform(delete(getResourcePath() + "/999999"))
                .andExpect(status().isNoContent());
    }
}
