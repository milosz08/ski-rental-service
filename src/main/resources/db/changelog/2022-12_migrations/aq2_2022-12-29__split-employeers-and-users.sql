-- liquibase formatted sql
-- changeset milosz08:aq2


CREATE TABLE IF NOT EXISTS user_details
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    pesel VARCHAR(11) NOT NULL UNIQUE,
    phone_area_code INT NOT NULL DEFAULT 48,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    email_address VARCHAR(80) NOT NULL UNIQUE,
    born_date DATE NOT NULL,
    gender ENUM('mężczyzna', 'kobieta') NOT NULL DEFAULT 'mężczyzna',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    INDEX (id, first_name, last_name, pesel, email_address, gender)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE TABLE IF NOT EXISTS location_addresses
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    street VARCHAR(50) NOT NULL,
    building_no VARCHAR(5) NOT NULL,
    apartment_no VARCHAR(5),
    city VARCHAR(70) NOT NULL,
    postal_code VARCHAR(6) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    INDEX (id, street, city, postal_code)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE TABLE IF NOT EXISTS employeers
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    login VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(72) UNIQUE NOT NULL,
    hired_date DATE NOT NULL,
    image_url VARCHAR(100) DEFAULT NULL,
    user_details_id BIGINT UNSIGNED UNIQUE,
    location_address_id BIGINT UNSIGNED,
    role_id BIGINT UNSIGNED,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    FOREIGN KEY (user_details_id) REFERENCES user_details(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (location_address_id) REFERENCES location_addresses(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON UPDATE CASCADE ON DELETE RESTRICT,

    INDEX (id, login)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE TABLE IF NOT EXISTS customers
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    user_details_id BIGINT UNSIGNED UNIQUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (user_details_id) REFERENCES user_details(id) ON UPDATE CASCADE ON DELETE CASCADE,

    INDEX (id)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE TABLE IF NOT EXISTS customers_addresses_binding
(
    customer_id BIGINT UNSIGNED UNIQUE,
    location_address_id BIGINT UNSIGNED,

    FOREIGN KEY (customer_id) REFERENCES customers(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (location_address_id) REFERENCES location_addresses(id) ON UPDATE CASCADE ON DELETE CASCADE,

    INDEX (customer_id, location_address_id)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;
