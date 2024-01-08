-- liquibase formatted sql
-- changeset milosz08:aq12

ALTER TABLE rents
    MODIFY COLUMN status ENUM ('otwarty', 'wypożyczony', 'zwrócony', 'zarezerwowany') NOT NULL;
