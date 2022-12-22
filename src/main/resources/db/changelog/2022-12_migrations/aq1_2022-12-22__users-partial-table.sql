-- liquibase formatted sql
-- changeset milosz08:aq1

CREATE TABLE IF NOT EXISTS users
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    INDEX (first_name, last_name)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci
