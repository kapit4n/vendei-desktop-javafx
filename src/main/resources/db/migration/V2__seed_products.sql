-- Flyway migration V2: seed demo data (SQLite)

-- Categories
INSERT INTO categories(name)
SELECT 'Grocery'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Grocery');

INSERT INTO categories(name)
SELECT 'Electronics'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Electronics');

INSERT INTO categories(name)
SELECT 'Apparel'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Apparel');

INSERT INTO categories(name)
SELECT 'Home'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Home');

INSERT INTO categories(name)
SELECT 'Sports'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Sports');

-- Helper: insert product only if code doesn't already exist
-- (products.code is not unique, but we treat it as one for demo seeding)

-- Grocery
INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Red Apple', 'APL-RED-001', NULL, 1, 9.99, 120, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'APL-RED-001');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Bananas (1 lb)', 'BAN-001', NULL, 1, 11.49, 80, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'BAN-001');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Baguette Bread', 'BRD-BAG-001', NULL, 1, 12.99, 40, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'BRD-BAG-001');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Milk (1 L)', 'MLK-001', NULL, 1, 14.49, 30, 1, 10, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'MLK-001');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Eggs (12 pack)', 'EGG-012', NULL, 1, 15.99, 25, 1, 21, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'EGG-012');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Pasta (500 g)', 'PST-500', NULL, 1, 17.49, 60, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'PST-500');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'White Rice (1 kg)', 'RCE-1K', NULL, 1, 18.99, 55, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'RCE-1K');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Coffee (ground) 250 g', 'COF-250', NULL, 1, 20.49, 35, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'COF-250');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Black Tea (20 bags)', 'TEA-BLK-020', NULL, 1, 21.99, 45, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'TEA-BLK-020');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Olive Oil (500 ml)', 'OIL-OLV-500', NULL, 1, 23.49, 28, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'OIL-OLV-500');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Cheddar Cheese (200 g)', 'CHS-CHED-200', NULL, 1, 24.99, 18, 1, 30, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'CHS-CHED-200');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Chicken Breast (1 lb)', 'CHK-BRST-1LB', NULL, 1, 26.49, 22, 1, 7, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'CHK-BRST-1LB');

-- Home
INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Laundry Detergent (1.5 L)', 'HOM-LAUN-15', NULL, 1, 27.99, 20, 0, NULL, (SELECT id FROM categories WHERE name='Home')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'HOM-LAUN-15');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Paper Towels (2 rolls)', 'HOM-PT-02', NULL, 1, 29.49, 35, 0, NULL, (SELECT id FROM categories WHERE name='Home')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'HOM-PT-02');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Hand Soap (500 ml)', 'HOM-SOAP-500', NULL, 1, 30.99, 40, 0, NULL, (SELECT id FROM categories WHERE name='Home')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'HOM-SOAP-500');

-- Personal care / Grocery
INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Toothpaste (120 g)', 'PC-TOOTH-120', NULL, 1, 32.49, 45, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'PC-TOOTH-120');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Shampoo (400 ml)', 'PC-SHAM-400', NULL, 1, 33.99, 30, 0, NULL, (SELECT id FROM categories WHERE name='Grocery')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'PC-SHAM-400');

-- Electronics
INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Smartphone (demo)', 'EL-PHONE-DEMO', NULL, 1, 35.49, 5, 0, NULL, (SELECT id FROM categories WHERE name='Electronics')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'EL-PHONE-DEMO');

INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Wireless Headphones (demo)', 'EL-HP-DEMO', NULL, 1, 36.99, 8, 0, NULL, (SELECT id FROM categories WHERE name='Electronics')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'EL-HP-DEMO');

-- Sports
INSERT INTO products(name, code, image_url, visible, price, stock, track_expiry, default_shelf_life_days, category_id)
SELECT 'Water Bottle (1.5 L)', 'SP-BOT-15', NULL, 1, 38.49, 60, 0, NULL, (SELECT id FROM categories WHERE name='Sports')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE code = 'SP-BOT-15');

