-- liquibase formatted sql
-- changeset milosz08:fd1

INSERT INTO roles (role_name, alias) VALUES ('użytkownik', 'U');
INSERT INTO roles (role_name, alias) VALUES ('pracownik', 'P');
INSERT INTO roles (role_name, alias) VALUES ('właściciel', 'W');