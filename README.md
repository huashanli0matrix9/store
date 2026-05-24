# Store API

Spring Boot backend for managing customers, orders, and products with PostgreSQL + Liquibase.

## Project Overview
This project implements a production-oriented REST API with:
- Layered architecture (`Controller -> Service -> Repository`)
- DTO-based API contracts (no direct JPA entity exposure)
- Validation and global error handling
- Product support with `Order <-> Product` many-to-many relationship
- Pagination and list/detail response separation
- Audit fields and optimistic locking
- Automated tests, Docker setup, and CI workflow

## Implemented Requirements
- Customer APIs with optional query search (`/customer?query=...`)
- Order APIs including detail lookup (`/order/{id}`)
- Product APIs (`/products`, `/products/{id}`)
- Paginated list endpoints using `page`, `size`, `sort`
- Safe error responses via `ApiErrorResponse`
- PostgreSQL schema/versioning via Liquibase

## Architecture
- **Controller**: HTTP contract, validation triggering, response status
- **Service**: business rules, transaction boundaries, exception decisions
- **Repository**: JPA data access and query definitions
- **Mapper (`@Component`)**: explicit entity-to-DTO mapping

## API Endpoints
### Customer
- `GET /customer` (paginated summary list)
- `GET /customer?query=...` (paginated search)
- `POST /customer`

### Order
- `GET /order` (paginated summary list)
- `GET /order/{id}` (detail)
- `POST /order`

### Product
- `GET /products` (paginated summary list)
- `GET /products/{id}` (detail)
- `POST /products`

## Database Design (High Level)
- `customer`
- `orders` (FK: `customer_id -> customer.id`)
- `product`
- `order_product` (join table for many-to-many)

Additional database concerns:
- Liquibase-managed schema/data migrations
- Performance indexes (including trigram search index for `customer.name`)
- Audit fields:
  - `created_at`
  - `updated_at`
  - `version` (optimistic locking)

## Security Considerations
This project intentionally **does not** include authentication/authorization in this scope, but includes defensive backend practices:
- DTO contracts (no entity exposure)
- Bean validation on request DTOs
- Centralized exception handling with safe, generic 500 responses
- Server-side logging for unexpected exceptions
- Externalized datasource configuration via env vars

## Performance Considerations
- Pagination for list endpoints
- Configured default/max page size:
  - default: `50`
  - max: `100`
- Summary DTOs for list endpoints to reduce payload size
- Detail DTOs for richer single-resource responses
- `@EntityGraph` on order detail fetch to intentionally load customer/products

## Testing
The project includes:
- Controller tests (`MockMvc`) for success + validation + not found + paginated responses
- Service tests (Mockito) for business flow and exception paths
- Repository tests (`@DataJpaTest`) for search, relationships, and persistence behavior
- Audit persistence tests for `createdAt/updatedAt/version`

Run tests:
```bash
./gradlew clean test
```

## Run Locally (Without Docker)
### Prerequisites
- JDK 17
- PostgreSQL running and reachable

Default app configuration expects:
- host: `localhost`
- port: `5433`
- db: `store`
- user: `admin`
- password: `admin`

You can override using environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Start app:
```bash
./gradlew bootRun
```

## Run With Docker Compose
Build and start:
```bash
docker compose up --build
```

Services:
- App: `http://localhost:8080`
- PostgreSQL: `localhost:5433`

Stop:
```bash
docker compose down
```

Stop and remove database volume:
```bash
docker compose down -v
```

## CI
GitHub Actions workflow:
- File: `.github/workflows/ci.yml`
- Triggers: push/PR on `main` and `develop`
- Uses JDK 17
- Runs:
  - `./gradlew clean test`
  - `./gradlew build`

## OpenAPI
OpenAPI spec is maintained in:
- `OpenAPI.yaml`

It reflects:
- Product endpoints
- Paginated list endpoints
- Request DTOs (including `CreateOrderRequest.productIds`)
- Standard error response model

## Future Improvements
- Authentication and authorization
- API rate limiting
- Structured audit/event trails
- Container hardening and vulnerability scanning
- Dedicated environment profiles and secret management
