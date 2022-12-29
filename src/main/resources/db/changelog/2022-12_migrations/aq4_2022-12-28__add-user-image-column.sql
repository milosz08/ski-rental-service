-- liquibase formatted sql
-- changeset milosz08:aq4

ALTER TABLE users
ADD COLUMN image_url VARCHAR(100) DEFAULT NULL AFTER email_address;
