# Zomiggy Backend

Spring Boot 3.4.5 REST API using Java 17, Spring Web, Spring Data JPA, Spring Security JWT, Bean Validation, and MySQL.

## Requirements

- Java 17+
- Maven 3.9+
- MySQL 8+

## Configuration

Copy `.env.example` to your environment and provide `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `FRONTEND_URL`. `JWT_SECRET` must be at least 32 bytes long. The default development setting exposes the generated OTP in the response; set `EXPOSE_OTP=false` when an SMS provider is connected. `DDL_AUTO=update` is intended for local development; set `DDL_AUTO=validate` in production so startup cannot alter the existing schema.

## Run

```text
cd backend
mvn spring-boot:run
```

The API runs at `http://localhost:8080`. The React app uses `VITE_API_BASE_URL=http://localhost:8080/api` by default.

## Build and test

```text
mvn clean test
mvn clean package
```

Hibernate updates the configured MySQL schema during development. The catalog and coupons are seeded on first startup.