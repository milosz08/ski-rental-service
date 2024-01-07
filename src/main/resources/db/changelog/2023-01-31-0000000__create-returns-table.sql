-- liquibase formatted sql
-- changeset milosz08:aq10

CREATE TABLE IF NOT EXISTS rent_returns
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    issued_identifier VARCHAR(18) NOT NULL UNIQUE,
    issued_datetime DATETIME NOT NULL,
    description VARCHAR(200),
    total_price DECIMAL(10,2) NOT NULL,
    total_deposit_price DECIMAL(10,2) NOT NULL,

    rent_id BIGINT UNSIGNED,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (rent_id) REFERENCES rents(id) ON UPDATE NO ACTION ON DELETE SET NULL,

    INDEX (id, issued_identifier)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE TABLE IF NOT EXISTS rent_returns_equipments
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    total_price DECIMAL(10,2) NOT NULL,
    description VARCHAR(200),
    deposit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    rent_return_id BIGINT UNSIGNED,
    rent_equipment_id BIGINT UNSIGNED,
    equipment_id BIGINT UNSIGNED,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (rent_return_id) REFERENCES rent_returns(id) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY (rent_equipment_id) REFERENCES rent_equipments(id) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipments(id) ON UPDATE NO ACTION ON DELETE SET NULL,

    INDEX (id)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;
