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

## School Project Mastery: System Design Features

This framework is an excellent candidate for a high-grade system design project because it demonstrates several advanced architectural patterns:

### 1. Layered Architecture & Generics
- The project clearly separates concerns into **Controller**, **Service**, and **Repository** layers.
- Use of **Java Generics** shows a high level of abstraction, allowing one code path to handle many different data types.

### 2. Reflection & Metadata-Driven Design
- By using custom annotations (`@CrudResource`), the system uses **Reflection** to build itself at runtime. This is how many industry-leading frameworks (like Spring itself) operate.

### 3. Design Patterns
- **Factory Method**: Used in the `DynamicCrudManager` to instantiate repositories and services.
- **Proxy/Registry Pattern**: The `DynamicCrudManager` acts as a registry for all application resources.
- **Template Method**: `BaseService` and `BaseController` provide a template for CRUD operations that sub-classes can optionally override.

### 4. Automated Testing
- The custom `@CrudTest` annotation and `BaseCrudIntegrationTest` demonstrate how to build a **Testing Framework** inside your application, ensuring that any new entity is automatically verified against a standard suite of tests.

## Future Roadmap (For Your Project)
To take this to the next level for your submission, consider adding:
1. **Global Exception Handling**: Use `@RestControllerAdvice` to handle 404s and 400s consistently.
2. **DTO Validation**: Add `@Valid` to the `UniversalCrudController` and use JSR-303 annotations on your records.
3. **OpenAPI (Swagger)**: Add the `springdoc-openapi` dependency to auto-generate a UI for your dynamic routes.

## How to Run
1. Ensure Java 17+ is installed.
2. Run the application using your IDE or Maven: `mvn spring-boot:run`.
3. The API will be available at `http://localhost:8080/api/products`.
4. H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`).
