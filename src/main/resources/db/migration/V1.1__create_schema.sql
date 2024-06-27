CREATE SCHEMA IF NOT EXISTS car;

CREATE TABLE IF NOT EXISTS car.producer
(
    producer_id BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL
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
    id       VARCHAR(255) PRIMARY KEY,
    year     VARCHAR(4) NOT NULL CHECK (year ~ '^\d{4}$') CHECK (year BETWEEN '1900' AND '2100'),
    model_id BIGINT REFERENCES car.model (model_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS car.car_category
(
    car_id      VARCHAR(255) REFERENCES car.car (id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES car.category (category_id) ON DELETE CASCADE,
    PRIMARY KEY (car_id, category_id)
)