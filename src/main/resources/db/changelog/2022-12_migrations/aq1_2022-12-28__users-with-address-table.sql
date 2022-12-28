-- liquibase formatted sql
-- changeset milosz08:aq1

CREATE TABLE IF NOT EXISTS roles
(
    id INT NOT NULL AUTO_INCREMENT,

    role_name VARCHAR(30) NOT NULL,
    alias CHAR(1) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    INDEX (role_name)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE TABLE IF NOT EXISTS users
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    login VARCHAR(20) NOT NULL,
    password VARCHAR(72) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    pesel VARCHAR(11) NOT NULL,
    phone_number VARCHAR(12) NOT NULL,
    email_address VARCHAR(80) NOT NULL,
    role_id INT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX (first_name, last_name)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE TABLE IF NOT EXISTS user_addresses
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    street VARCHAR(50) NOT NULL,
    building_no VARCHAR(5) NOT NULL,
    apartment_no VARCHAR(5),
    city VARCHAR(70) NOT NULL,
    postal_code VARCHAR(6) NOT NULL,
    user_id BIGINT,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    INDEX (street, city, postal_code)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;
