-- liquibase formatted sql
-- changeset milosz08:fd2

INSERT INTO equipment_types (name) VALUES
('narty'),
('sanki'),
('showboard'),
('kije narciarskie'),
('kask narciarski'),
('buty narciarskie'),
('buty snowboardowe'),
('gogle narciarskie');


INSERT INTO equipment_brands (name) VALUES
('atomic'),
('head'),
('nordica'),
('salomon'),
('volkl');


INSERT INTO equipment_colors (name) VALUES
('czerwony'),
('niebieski'),
('czarny'),
('zielony');


INSERT INTO account_units (name) VALUES
('doba'),
('godzina');
