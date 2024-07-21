# Car REST Service

This project is designed to manage car-related data using a REST API. It reads car data from a `.csv` file, transforms it, and stores it in a PostgreSQL database. The project provides a comprehensive REST API for CRUD operations and includes OpenAPI and Swagger documentation. Docker is used for containerization, enabling easy setup and deployment.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Swagger Documentation](#swagger-documentation)
- [Contributors](#contributors)

## Features

- Read and parse `.csv` files containing car data.
- Store car data in a PostgreSQL database.
- Provide a REST API for CRUD operations on car data.
- Use OpenAPI and Swagger for API documentation.
- Utilize Docker for easy setup and deployment.
- Integrate Keycloak for OAuth2 and JWT-based authentication.

## Getting Started

### Prerequisites

- Docker installed on your machine.

### Setup and Run

1. Clone the repository:

    ```sh
    git clone https://github.com/your-username/car-service.git
    cd car-service
    ```
2. To configure your application to connect to a PostgreSQL database, add the following properties to your application.yml / docker-compose.yml file:
    ```yml
    # Database configuration
    datasource:
        driver-class-name: org.postgresql.Driver
        url: jdbc:postgresql://localhost:5432/your_database_name
        username: your_username
        password: your_password
    ```
   
    ```yml
    # docker-compose configuration
   services:
        api:
            environment:
                - SPRING_DATASOURCE_URL=jdbc:postgresql://db_pg:5432/your_database_name
                - SPRING_DATASOURCE_USERNAME=your_username
                - SPRING_DATASOURCE_PASSWORD=your_password
        db_pg:
            environment:
                - POSTGRES_DB=your_database_name
                - POSTGRES_USER=your_username
                - POSTGRES_PASSWORD=your_password
    ```
   Make sure to replace `your_database_name`, `your_username`, and `your_password` with your actual PostgreSQL database name, username, and password.
   
3. Build and run the services using Docker Compose:

    ```sh
    docker-compose up --build
    ```

This command will start the PostgreSQL database, the Car REST Service, and Keycloak for authentication.

### Configuration

Configuration details is provided in the `docker-compose.yml` file. Below is a brief overview of key configurations:

#### `docker-compose.yml`

```yaml
name: car-rest-service

services:
    api:
        image: car-service
        build:
            context: .
            dockerfile: Dockerfile
        restart: always
        ports:
            - "8080:8080"
        depends_on:
            - db_pg
        environment:
            - SPRING_DATASOURCE_URL=jdbc:postgresql://db_pg:5432/your_database_name
            - SPRING_DATASOURCE_USERNAME=your_username
            - SPRING_DATASOURCE_PASSWORD=your_password
        container_name: "api-car-service"

    db_pg:
        image: postgres:16-alpine
        restart: always
        environment:
            POSTGRES_DB: your_database_name
            POSTGRES_USER: your_username
            POSTGRES_PASSWORD: your_password
        ports:
            - "5433:5432"
        container_name: "db-pg"

    keycloak:
        image: quay.io/keycloak/keycloak:25.0.1
        restart: always
        environment:
            KEYCLOAK_ADMIN: admin
            KEYCLOAK_ADMIN_PASSWORD: admin
        ports:
            - "8888:8080"
        volumes:
            - './config/keycloak:/opt/keycloak/data'
        depends_on:
            - db_pg
        command: start-dev
        container_name: "keycloak"
```
### Database Schema
The following tables are defined in the PostgreSQL database:

- Producer: Stores car manufacturers.
- Model: Stores car models.
- Category: Stores car categories.
- Car: Stores car details.
- Car_Category: Junction table for car categories.

Schema creation script:
```postgresql
CREATE SCHEMA IF NOT EXISTS car;

CREATE TABLE IF NOT EXISTS car.producer
(
    producer_id BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS car.model
(
    model_id    BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    producer_id BIGINT       REFERENCES car.producer (producer_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS car.category
(
    category_id BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS car.car
(
    id        BIGSERIAL PRIMARY KEY,
    object_id VARCHAR(255),
    year      VARCHAR(4) NOT NULL CHECK (year ~ '^\d{4}$') CHECK (year BETWEEN '1900' AND '2100'),
    model_id  BIGINT     REFERENCES car.model (model_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS car.car_category
(
    car_id      BIGINT REFERENCES car.car (id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES car.category (category_id) ON DELETE CASCADE,
    PRIMARY KEY (car_id, category_id)
)
```
### API Endpoints
The Car REST Service provides the following endpoints for managing cars, models, and manufacturers:

#### Car Endpoints
- GET /api/v1/cars/{id}: Retrieve a car by ID.
- POST /api/v1/cars: Create a new car.
- PATCH /api/v1/cars: Update an existing car.
- DELETE /api/v1/cars/{id}: Delete a car by ID.
- GET /api/v1/cars: List all cars with optional filtering and pagination.
#### Model Endpoints
- GET /api/v1/models/{id}: Retrieve a model by ID.
- POST /api/v1/models: Create a new model.
- PATCH /api/v1/models: Update an existing model.
- DELETE /api/v1/models/{id}: Delete a model by ID.
- GET /api/v1/models: List all models with optional filtering and pagination.
#### Manufacturer Endpoints
- GET /api/v1/manufacturers/{id}: Retrieve a manufacturer by ID.
- POST /api/v1/manufacturers: Create a new manufacturer.
- PATCH /api/v1/manufacturers: Update an existing manufacturer.
- DELETE /api/v1/manufacturers/{id}: Delete a manufacturer by ID.
- GET /api/v1/manufacturers: List all manufacturers with pagination.

### Security
The service uses Keycloak for OAuth2 and JWT-based authentication. Security configurations are defined in the SecurityConfig class.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
            .build();
    }
}
```
### Swagger Documentation
Swagger UI is available at `/swagger-ui.html` once the service is running. It provides an interactive interface to explore and test the API endpoints.
### Contributors
- [avexcoss](https://github.com/avecoss)