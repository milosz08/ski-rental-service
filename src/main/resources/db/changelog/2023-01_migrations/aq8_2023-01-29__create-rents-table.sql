-- liquibase formatted sql
-- changeset milosz08:aq8

CREATE TABLE IF NOT EXISTS rents
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    issued_identifier VARCHAR(18) NOT NULL UNIQUE,
    issued_datetime DATETIME NOT NULL,
    rent_datetime DATETIME NOT NULL,
    return_datetime DATETIME NOT NULL,
    status ENUM('otwarty', 'wypożyczony', 'zwrócony') NOT NULL,
    tax INT UNSIGNED NOT NULL DEFAULT 0,
    description VARCHAR(200),
    total_price DECIMAL(10,2) NOT NULL,
    total_deposit_price DECIMAL(10,2) NOT NULL,

    customer_id BIGINT UNSIGNED,
    employer_id BIGINT UNSIGNED,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON UPDATE NO ACTION ON DELETE SET NULL,
    FOREIGN KEY (employer_id) REFERENCES employeers(id) ON UPDATE NO ACTION ON DELETE SET NULL,

    INDEX (id, issued_identifier)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE TABLE IF NOT EXISTS rent_equipments
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    count INT UNSIGNED NOT NULL DEFAULT 1,
    total_price DECIMAL(10,2) NOT NULL,
    description VARCHAR(200),
    deposit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    rent_id BIGINT UNSIGNED,
    equipment_id BIGINT UNSIGNED,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (rent_id) REFERENCES rents(id) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipments(id) ON UPDATE NO ACTION ON DELETE SET NULL,

    INDEX (id)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;
