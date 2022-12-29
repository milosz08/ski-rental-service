-- liquibase formatted sql
-- changeset milosz08:fd4

UPDATE roles SET role_name = 'kierownik', alias = 'K' WHERE alias = 'W';
