package com.example.crudapp.domain.product;

import com.example.crudapp.core.BaseCrudIntegrationTest;
import com.example.crudapp.core.annotations.CrudTest;

/**
 * Automated CRUD tests for Product resource.
 * Minimal boilerplate: just the annotation and extending the base class.
 */
@CrudTest(path = "products", entity = Product.class)
class ProductCrudTest extends BaseCrudIntegrationTest {
}
