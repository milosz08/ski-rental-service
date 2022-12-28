-- liquibase formatted sql
-- changeset milosz08:fd3

UPDATE users SET phone_area_code = 48, phone_number = '212471463' WHERE id = 1;
UPDATE users SET phone_area_code = 49, phone_number = '458816100' WHERE id = 2;
UPDATE users SET phone_area_code = 323, phone_number = '578323886' WHERE id = 3;
