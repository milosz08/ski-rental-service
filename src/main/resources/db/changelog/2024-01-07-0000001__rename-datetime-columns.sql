-- liquibase formatted sql
-- changeset milosz08:aq12

ALTER TABLE rent_returns
    RENAME COLUMN issued_datetime TO issued_date_time;

ALTER TABLE rents
    RENAME COLUMN issued_datetime TO issued_date_time;

ALTER TABLE rents
    RENAME COLUMN rent_datetime TO rent_date_time;

ALTER TABLE rents
    RENAME COLUMN return_datetime TO return_date_time;


