-- liquibase formatted sql
-- changeset milosz08:fd3

INSERT INTO equipments (name, model, gender, description, barcode, count_in_store, size, price_per_hour,
                        price_for_next_hour, price_per_day, value_cost, type_id, brand_id, color_id) VALUES
('Narty VOLKL FLAIR 76 ELITE', 'RC76 ELITE', 'FEMALE', 'To są przykładowe narty.', '4724855647635', 7, 123.00, 24.50, 5.00, 85.00, 2670.00, 1, 5, 3),
('Deska splitboardowa Salomon Pillow Talk', 'SPT536', 'UNISEX', NULL, '4756071050371', 4, 151.00, 32.50, 7.40, 110.00, 2399.39, 3, 4, 1);
