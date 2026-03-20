# Generic Spring Boot CRUD Application

This project implements a highly reusable and testable backend architecture for a Java-based web application.

## Key Features

### 1. Generic Base Classes
To reduce code duplication and centralize logic, the application uses generic base classes:
- **`BaseEntity`**: Abstract class for all JPA entities, managing common fields like `id`.
- **`BaseRepository<T>`**: Generic repository interface extending `JpaRepository`.
- **`BaseService<T>`**: Abstract service providing default implementation for CRUD operations (`findAll`, `findById`, `save`, `update`, `delete`).
- **`BaseController<T>`**: Abstract REST controller mapping standard CRUD endpoints.

### 2. Streamlined Architecture
- **Java Records**: DTOs are now implemented as `records` (e.g., `ProductRecord`), providing a concise and immutable data structure.
- **`@CrudResource` Annotation**: A unified annotation that handles all the heavy lifting. By adding `@CrudResource(path = "products", dto = ProductRecord.class)` to an entity, you automatically register:
    - A dynamic **Repository**.
    - A generic **Service**.
    - A unified **Controller** route at `/api/v2/products`.

### 3. Usage Example
To create a new CRUD resource, you only need:
1. A JPA Entity (extending `BaseEntity`).
2. A DTO Record.
3. The `@CrudResource` annotation.

No more manual creation of Controllers, Services, or Repositories is required for standard CRUD operations.

### 4. Testability
- **Unit Tests**: The `BaseServiceTest` demonstrates how core logic can be tested independently of concrete entities.
- **Integration Tests**: `ProductControllerIntegrationTest` verifies the full API stack using H2 and `MockMvc`.

## How to Run
1. Ensure Java 17+ is installed.
2. Run the application using your IDE or Maven: `mvn spring-boot:run`.
3. The API will be available at `http://localhost:8080/api/products`.
4. H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`).
