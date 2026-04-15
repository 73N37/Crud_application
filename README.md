# Generic Spring Boot CRUD Application

This project implements a highly reusable and testable backend architecture for a Java-based web application.

## ⚡ Implemented Optimizations

The framework has been enhanced with several production-grade optimizations:

### 1. High-Performance Reflection Caching
- **Implementation**: The `MappingCache` stores record constructors and component metadata.
- **Benefit**: Drastically reduces the overhead of the `toRecord()` method, making entity-to-DTO conversion nearly as fast as manual mapping.

### 2. Service Layer Registry (Strategy Pattern)
- **Implementation**: The `@CrudResource` annotation now supports a `service()` parameter.
- **Benefit**: Allows you to override the generic `BaseService` with specialized business logic for specific entities without breaking the dynamic architecture.

### 3. Dynamic Query Specification
- **Implementation**: Integrated JPA Specifications with a `GenericSpecification` builder.
- **Benefit**: Enables instant filtering via URL parameters (e.g., `/api/v2/products?name=Pro`) for all registered resources.

### 4. Visual Metadata Explorer
- **Implementation**: New endpoint at `/api/v2/metadata`.
- **Benefit**: Provides a real-time inventory of all registered resources, facilitating front-end automation and API discovery.

## 🏗️ Architectural Separation (DLI)

The project follows a strict **Data-Logic-Interface** separation to ensure maximum maintainability and clean system design:

### 1. Data Layer (`com.example.crudapp.data`)
- **Responsibility**: Database schema, JPA mappings, and raw data access.
- **Key Components**: `BaseEntity`, `BaseRepository`, and concrete `@Entity` classes (e.g., `Product`).
- **Constraint**: This layer has no knowledge of API contracts or business workflows.

### 2. Logic Layer (`com.example.crudapp.logic`)
- **Responsibility**: Business rules, resource orchestration, and dynamic registration.
- **Key Components**: `BaseService`, `DynamicCrudManager`, and `ResourceMetadata`.
- **Constraint**: This is the "Engine" of the application. It acts as a bridge, transforming Data (Entities) into Interface (Records) through logical operations.

### 3. Interface Layer (`com.example.crudapp.api`)
- **Responsibility**: External API surface, HTTP request handling, and immutable data contracts.
- **Key Components**: `UniversalCrudController` and Java `records` (e.g., `ProductRecord`).
- **Constraint**: This layer is strictly immutable. It never interacts with the database directly; it only talks to the Logic layer.

### 2. Streamlined Architecture
- **Java Records**: DTOs are now implemented as `records` (e.g., `ProductRecord`), providing a concise and immutable data structure.
- **`@CrudResource` Annotation**: A unified annotation that handles all the heavy lifting. By adding `@CrudResource(path = "products", dto = ProductRecord.class)` to an entity, you automatically register:
    - A dynamic **Repository**.
    - A generic **Service**.
    - A unified **Controller** route at `/api/v2/products`.

### 🌳 Hierarchical Relationships
The framework natively supports tree-like structures (Parent-Child, Grandparent-Child) through custom annotations:
- **`@Parent`**: Marks a field (in `BaseEntity`) that points to a parent entity of the same type.
- **`@Children`**: Marks a collection that holds child entities.
- **Automatic Mapping**: If your Record DTO includes components named `parentId` or `grandparentId`, the `toRecord()` engine will automatically resolve and populate these from the hierarchy using reflection.
- **Helper Methods**: `getGrandparent()` and `getGrandchildren()` are available on all entities for easy traversal.

## School Project Mastery: System Design Features

This framework is an excellent candidate for several advanced architectural patterns:

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

## 🚀 Future Roadmap: Framework Evolution

To evolve this prototype into a production-ready Enterprise CRUD Engine, the following modules are planned:

### 🛡️ Phase 1: Security & Validation (COMPLETED)
- **JSR-303 Validation Engine**: Added validation annotations (`@NotBlank`, `@Positive`, etc.) to Records and integrated with a global exception handler.
- **Global Exception Mapping**: Implemented a `@RestControllerAdvice` to translate `RuntimeException` and `MethodArgumentNotValidException` into standardized JSON error responses.

### 🌐 Phase 2: Observability & Documentation (COMPLETED)
- **Auto-Generated OpenAPI (Swagger)**: Integrated `springdoc-openapi` to dynamically document the virtual routes.
- **MySQL Database Integration**: Switched from H2 to MySQL with Docker Compose for production-like testing.

### ⚙️ Phase 3: Advanced Data Patterns (COMPLETED)
- **Generic Specification API**: Implemented a dynamic filtering, pagination, and sorting system (e.g., `/api/v2/products?page=0&size=5&sortBy=name&direction=desc`).

---

## 📖 Project Documentation

This project provides three layers of documentation:

### 1. Interactive API Documentation (Swagger/UI)
The most important documentation for testing and grading. It is automatically generated based on the registered resources.
- **URL**: `http://localhost:8080/swagger-ui.html`
- **Features**: Live testing of GET, POST, PUT, and DELETE operations, including validation checks.

### 2. Technical Metadata Explorer
A custom endpoint that describes the system's dynamic state.
- **URL**: `http://localhost:8080/api/v2/metadata`
- **Output**: A JSON map of all registered resource paths and their corresponding DTO classes.

### 3. Code-Level Documentation (Javadoc)
All core classes (Logic, Data, API) include detailed Javadoc comments explaining the architectural intent and "DLI" (Data-Logic-Interface) constraints.

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven

### Step 1: Start the Database
Run the following command to start the MySQL 8.0 container:
```bash
docker-compose up -d
```

### Step 2: Run the Application
```bash
mvn spring-boot:run
```

### Step 3: Access the System
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Base**: `http://localhost:8080/api/v2/products`
