-- liquibase formatted sql
-- changeset milosz08:aq4

ALTER TABLE user_details
MODIFY COLUMN phone_area_code INT DEFAULT 48;

ALTER TABLE employeers
ADD COLUMN first_access BIT NOT NULL DEFAULT 1 AFTER image_url,
ADD COLUMN is_blocked BIT NOT NULL DEFAULT 0 AFTER first_access;
