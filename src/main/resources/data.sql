
-- Kullanıcılar (Customer tablosu)
INSERT INTO customers (username, password, is_admin)
VALUES ('admin', '$2a$10$y95Q/EkNmXKwwGmord7mVOjQh6jvFdt4frkC1LN3Ve1KJ12M.NSni', true);
INSERT INTO customers (username, password, is_admin)
VALUES ('alice', '$2a$10$y95Q/EkNmXKwwGmord7mVOjQh6jvFdt4frkC1LN3Ve1KJ12M.NSni', false);
INSERT INTO customers (username, password, is_admin)
VALUES ('bob', '$2a$10$y95Q/EkNmXKwwGmord7mVOjQh6jvFdt4frkC1LN3Ve1KJ12M.NSni', false);

-- Varlıklar (Asset tablosu)
INSERT INTO assets (customer_id, asset_name, size, usable_size) VALUES ('2', 'TRY', 10000, 8000);
INSERT INTO assets (customer_id, asset_name, size, usable_size) VALUES ('2', 'ASELS', 50, 40);
INSERT INTO assets (customer_id, asset_name, size, usable_size) VALUES ('3', 'TRY', 5000, 5000);
INSERT INTO assets (customer_id, asset_name, size, usable_size) VALUES ('3', 'THYAO', 20, 15);
