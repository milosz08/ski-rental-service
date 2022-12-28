-- liquibase formatted sql
-- changeset milosz08:fd2

INSERT
INTO users (login, password, first_name, last_name, pesel, phone_number, email_address, role_id)
VALUES ('jankow123', '$2a$10$Aw9Zr09KmE22wgcmUPqeY.3/80burYO2BZkAqeLmxHiFc6bHNE5p2', 'Jan', 'Kowalski',
        '87121512913', '+48212471463', 'jankow@ski.miloszgilga.pl', 1);

INSERT
INTO users (login, password, first_name, last_name, pesel, phone_number, email_address, role_id)
VALUES ('annnow321', '$2a$10$Aw9Zr09KmE22wgcmUPqeY.3/80burYO2BZkAqeLmxHiFc6bHNE5p2', 'Anna', 'Nowak',
        '06232629853', '+48458816100', 'annnow@ski.miloszgilga.pl', 2);

INSERT
INTO users (login, password, first_name, last_name, pesel, phone_number, email_address, role_id)
VALUES ('andand456', '$2a$10$Aw9Zr09KmE22wgcmUPqeY.3/80burYO2BZkAqeLmxHiFc6bHNE5p2', 'Andrzej', 'Andrzejewski',
        '74071643394', '+48578323886', 'andand@ski.miloszgilga.pl', 3);

INSERT
INTO user_addresses (street, building_no, apartment_no, city, postal_code, user_id)
VALUES ('Długa', '54', '23c', 'Gliwice', '43-100', 1);

INSERT
INTO user_addresses (street, building_no, apartment_no, city, postal_code, user_id)
VALUES ('Szeroka', '32', NULL, 'Katowice', '45-400', 2);

INSERT
INTO user_addresses (street, building_no, apartment_no, city, postal_code, user_id)
VALUES ('Arnolda Szarego', '112', '45b', 'Zabrze', '41-400', 3);