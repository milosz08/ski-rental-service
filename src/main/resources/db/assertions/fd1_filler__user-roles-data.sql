-- liquibase formatted sql
-- changeset milosz08:fd1

INSERT INTO roles (role_name, alias) VALUES
('użytkownik', 'U'),
('pracownik', 'P'),
('kierownik', 'K');
