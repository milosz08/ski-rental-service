-- liquibase formatted sql
-- changeset milosz08:aq5

DROP TABLE customers_addresses_binding;

ALTER TABLE customers
ADD COLUMN location_address_id BIGINT after user_details_id,
ADD FOREIGN KEY (location_address_id) REFERENCES location_addresses(id) ON UPDATE CASCADE ON DELETE CASCADE;

