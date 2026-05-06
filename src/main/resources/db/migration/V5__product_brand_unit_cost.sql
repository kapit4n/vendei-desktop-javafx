-- Brand, selling unit, and acquisition cost per SKU (same product line can differ by brand → different cost/price)
ALTER TABLE products ADD COLUMN unit_of_measure TEXT NULL;
ALTER TABLE products ADD COLUMN brand TEXT NULL;
ALTER TABLE products ADD COLUMN cost REAL NOT NULL DEFAULT 0;
