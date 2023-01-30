-- liquibase formatted sql
-- changeset milosz08:aq7

CREATE TABLE IF NOT EXISTS equipments
(
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL UNIQUE,
    gender ENUM('MALE', 'FEMALE', 'UNISEX') NOT NULL,
    description VARCHAR(200),
    barcode VARCHAR(13),
    count_in_store INT NOT NULL,
    size DECIMAL(10,2),
    price_per_hour DECIMAL(10,2) NOT NULL,
    price_for_next_hour DECIMAL(10,2) NOT NULL,
    price_per_day DECIMAL(10,2) NOT NULL,
    value_cost DECIMAL(10,2) NOT NULL,

    type_id BIGINT UNSIGNED,
    brand_id BIGINT UNSIGNED,
    color_id BIGINT UNSIGNED,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (type_id) REFERENCES equipment_types(id) ON UPDATE NO ACTION ON DELETE RESTRICT,
    FOREIGN KEY (brand_id) REFERENCES equipment_brands(id) ON UPDATE NO ACTION ON DELETE RESTRICT,
    FOREIGN KEY (color_id) REFERENCES equipment_colors(id) ON UPDATE NO ACTION ON DELETE RESTRICT,

    INDEX (id, name, model, type_id, count_in_store)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;


CREATE EVENT IF NOT EXISTS remove_not_activated_employer_account
    ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL 72 HOUR
    DO
    DELETE FROM employeers WHERE first_access = 1
