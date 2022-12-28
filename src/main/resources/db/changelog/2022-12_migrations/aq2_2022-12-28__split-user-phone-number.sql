-- liquibase formatted sql
-- changeset milosz08:aq2

ALTER TABLE users
DROP COLUMN phone_number,
ADD COLUMN phone_area_code INT NOT NULL DEFAULT 48 AFTER pesel,
ADD COLUMN phone_number VARCHAR(15) NOT NULL AFTER phone_area_code;
