# Review Service

A RESTful microservice for managing user reviews of restaurant menu items and calculating their average ratings and Wilson scores.

## Tech Stack

- **Language & Framework:** Java 25, Spring Boot 4
- **Persistence:** Spring Data JPA, Hibernate, Hypersistence Utils
- **Database & Migrations:** PostgreSQL 16, Flyway
- **Mapping & Utilities:** MapStruct, Lombok
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Testing:** JUnit 5, Testcontainers (PostgreSQL via JDBC), AssertJ, WebTestClient
- **Containerization:** Cloud Native Buildpacks, Docker Compose

## API Endpoints

- `POST /v1/reviews` - Create a review with a rating from 1 to 5. Each user can submit only one review per menu item.
- `GET /v1/reviews/{id}` - Retrieve a review by ID.
- `GET /v1/reviews/my?sortBy={sortBy}&from={from}&size={size}` - Retrieve a paginated list of the current user's reviews, sorted by creation date (`date_asc`, `date_desc`).
- `GET /v1/reviews/menu-item/{menuId}?sortBy={sortBy}&from={from}&size={size}` - Retrieve a paginated list of reviews for a menu item, sorted by creation date (`date_asc`, `date_desc`), along with its average rating and Wilson score.
- `POST /v1/reviews/ratings` - Retrieve average ratings and Wilson scores for menu item IDs supplied in the request body.

The `POST /v1/reviews` and `GET /v1/reviews/my` endpoints require the `X-User-Name` header to identify the user.

## Build and Run

Tests require Docker to run the PostgreSQL test container. Running the application locally requires PostgreSQL configured in `src/main/resources/application.yml`.

```bash
# Run tests
./gradlew test

# Run application
./gradlew bootRun

# Build and run containerized application
./gradlew bootBuildImage
cd ./docker
docker compose up -d
```
