-- liquibase formatted sql
-- changeset milosz08:fd2

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE roles;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO roles (role_name, alias) VALUES
('pracownik', 'P'),
('kierownik', 'K');


INSERT INTO user_details(first_name, last_name, pesel, phone_area_code, phone_number, email_address, born_date, gender) VALUES
('Anna', 'Nowak', '65052859767', 48, '454304821', 'annnow@ski.miloszgilga.pl', '1980-12-25', 1),
('Andrzej', 'Andrzejewski', '68102023253', 49, '212063998', 'andand@ski.miloszgilga.pl', '1972-05-12', 0),
('Jan', 'Kowalski', '66051043845', 323, '450370403', 'jankow@ski.miloszgilga.pl', '1965-01-18', 0);


INSERT INTO location_addresses (street, building_no, apartment_no, city, postal_code) VALUES
('Długa', '54', '23c', 'Gliwice', '43-100'),
('Szeroka', '32', NULL, 'Katowice', '45-400'),
('Arnolda Szarego', '112', '45b', 'Zabrze', '41-400');


INSERT INTO employeers (login, password, hired_date, role_id, user_details_id, location_address_id) VALUES
('annnow321', '$2a$10$gSSrolxiKaXtpTCYz260AOyMH2.dUVUBMvNzgzLNoIHD5o4ZFFTku', '2005-04-25', 1, 1, 1),
('andand456', '$2a$10$Aw9Zr09KmE22wgcmUPqeY.3/80burYO2BZkAqeLmxHiFc6bHNE5p2', '2001-10-12', 2, 2, 2);


INSERT INTO customers (user_details_id) VALUES
(3);


INSERT INTO customers_addresses_binding (customer_id, location_address_id) VALUES
(1, 3)
