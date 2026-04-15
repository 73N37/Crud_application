-- ==========================================================
-- TEST DATA FOR GENERIC CRUD (VANILLA JAVA - POSTGRES)
-- ==========================================================

-- 1. Clean up existing data
TRUNCATE TABLE product CASCADE;
TRUNCATE TABLE baseentity CASCADE;

-- 2. Insert Base Entities (Identity & Hierarchy)
INSERT INTO baseentity (id, parent_id) VALUES (1, NULL);
INSERT INTO baseentity (id, parent_id) VALUES (2, 1);
INSERT INTO baseentity (id, parent_id) VALUES (3, NULL);

-- 3. Insert Products
INSERT INTO product (id, name, description, price) VALUES (1, 'GAMING LAPTOP', 'RTX 4080, 32GB RAM, 1TB SSD', 2499.99);
INSERT INTO product (id, name, description, price) VALUES (2, 'COOLING PAD', 'Triple fan setup for laptops', 35.50);
INSERT INTO product (id, name, description, price) VALUES (3, 'MECHANICAL KEYBOARD', 'Blue switches, RGB lighting', 89.00);

-- 4. Reset sequences (PostgreSQL identity)
SELECT setval(pg_get_serial_sequence('baseentity', 'id'), 4, false);
