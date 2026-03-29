# 🚀 Release Tracker

This project is a Spring Boot application designed with a focus on clean architecture, containerization, and production-ready best practices such as observability, security, and automated CI/CD workflows.

---

## 🛠️ Getting Started

To ensure a consistent environment across different machines, the project is fully containerized using Docker.

### Prerequisites
* **Docker & Docker Compose**
* **Maven** (Optional, for local development outside of Docker)

---

### 📦 Option 1: Full Docker Launch (Recommended)
This is the fastest way to run the entire stack (Application + PostgreSQL) without manually installing Java or Maven.

1. **Prepare the environment file:**
   ```bash
   cp .env.example .env
   ```

*(Note: .env.example contains pre-configured defaults for an immediate "out-of-the-box" experience.)*

2. **Launch the services:**
   ```bash
   docker compose up --build -d
   ```

3. **Run without security:**
    ```bash
   SECURITY_ENABLED=false docker compose up --build -d
   ```

**The application will be accessible at:** http://localhost:8080

---

### 💻 Option 2: Local Development
If you prefer running the application locally using your installed Maven while using the Dockerized database:

1. **Start the database only:**
   ```bash
   docker compose up postgres -d
   ```
2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
3. **Run without security:**
   ```bash
   SECURITY_ENABLED=false mvn spring-boot:run
   ```
---

## 🗝️ Security Notes
* By default, the application runs with Spring Security’s in-memory authentication. User credentials are defined in the .env file for convenience. In a real project, secrets and passwords would typically be stored in a secure secret manager instead of environment files to avoid exposing sensitive data.
* You can disable security with `SECURITY_ENABLED=false`.

---

## 📝 API Documentation

All endpoints are under the base path `/api/v1/releases`.

| Method | Endpoint         | Summary | Responses |
|--------|------------------|--------|-----------|
| POST | /api/v1/releases | Create a new release | 201 Created |
| PUT | /api/v1/releases/{id}   | Update an existing release | 200 OK, 404 Not Found |
| GET | /api/v1/releases/{id}   | Get a release by ID | 200 OK, 404 Not Found |
| DELETE | /api/v1/releases/{id}   | Delete a release by ID | 204 No Content, 404 Not Found |
| GET | /api/v1/releases        | Search releases with filters and pagination | 200 OK |


The project includes **Swagger/OpenAPI documentation**. You can explore and test all API endpoints using Swagger UI:

- Open in browser: [http://localhost:8080/api/swagger-ui/index.html](http://localhost:8080/api/swagger-ui/index.html)
- OpenAPI JSON spec: [http://localhost:8080/api/v3/api-docs](http://localhost:8080/api/v3/api-docs)

This allows developers to quickly understand the available endpoints, their request/response models, and test them interactively.

### How to authenticate using Swagger UI

1. Open Swagger UI:  
   http://localhost:8080/api/swagger-ui/index.html

2. Click on the **"Authorize 🔒"** button

3. Enter your JWT token (without the `Bearer` prefix):  
   example:  
   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

4. Click **Authorize**

Swagger will automatically include the following header in all secured requests:

Authorization: Bearer <your_token>

---

### Public vs Secured Endpoints

- The `/auth/**` endpoints are **public** and do not require authentication
- All other endpoints require a valid JWT token

---

### ℹ️ Note

Authentication is required **only when security is enabled** (`SECURITY_ENABLED=true`, which is the default).

If the application is started with security disabled:

`SECURITY_ENABLED=false`

➡️ All endpoints become publicly accessible, and no JWT token is required when using Swagger or calling the API.

---

## 🗄️ Database Migrations

The database schema is automatically managed with **Flyway**. Migration scripts are located under:

`src/main/resources/db/migration`

When the application starts, Flyway will automatically apply any pending migrations to ensure the database is up-to-date.

For this project, **PostgreSQL** is used as the external database, which is configured via `docker-compose` for easy setup.

---

## 🧪 Testing

The project includes both **integration** and **unit tests**:

- **Integration tests** use Spring Boot Test, MockMvc, Testcontainers, and a clean database state for each test.
- **Unit tests** use Mockito for mocking service layers and verifying interactions.

`ReleaseControllerIntegrationTest` runs with security disabled to focus on API and database behavior; `ReleaseTrackerSecurityIntegrationTest` covers JWT authentication and public routes (for example Swagger) with security enabled.

Example commands to run tests:

- Run all tests locally with Maven:
    ```
    mvn clean verify
    ```
    
- Run specific test classes:
    ```
    mvn test -Dtest=ReleaseControllerIntegrationTest
    mvn test -Dtest=ReleaseServiceUnitTest
    ```
---

## 📝 .env.example

```dotenv
# Security
SECURITY_ENABLED=true
SECURITY_USER_USERNAME=user
SECURITY_USER_PASSWORD=user123
SECURITY_JWT_SECRET=release-tracker-super-super-secret

# Database
POSTGRES_USER=release_tracker
POSTGRES_PASSWORD=release_tracker
POSTGRES_DB=release_tracker_db
```

## 📊 Monitoring & Logging

The application includes basic production-ready observability:

### 🔹 Logging

* Structured logging with clear log levels
* Service-level logging for business operations
* Centralized exception handling with error logging
* Request Tracing: Every request is tagged with a unique `X-Trace-ID` (MDC), returned in the response headers.

### 🔹 Actuator Endpoints

The following endpoints are exposed:

* `/api/actuator/health` – application health status
* `/api/actuator/metrics` – internal metrics
* `/api/actuator/prometheus` – Prometheus-compatible metrics

These endpoints provide insight into application state and performance.

> **Note:** Metrics are primarily intended for integration with monitoring tools (e.g. Prometheus, Grafana), but can also be accessed directly.

---

## 🔁 CI/CD

The project includes a CI/CD pipeline using GitHub Actions.

### Pipeline steps:

* Checkout source code
* Setup JDK
* Run unit and integration tests
* Build application
* Build Docker image

This ensures every change is automatically validated.
