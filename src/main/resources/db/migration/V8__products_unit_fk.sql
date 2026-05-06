-- Link products to units_of_measure; remove free-text unit column
ALTER TABLE products ADD COLUMN unit_id INTEGER NULL REFERENCES units_of_measure(id);

UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'kg') WHERE unit_of_measure = 'kg';
UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'lb') WHERE unit_of_measure = 'lb';
UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'ea') WHERE unit_of_measure = 'unit';
UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'L') WHERE unit_of_measure = 'L';
UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'pack') WHERE unit_of_measure IN ('pack', '500 g pack', '250 g pack', '200 g pack', '2-roll pack');
UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'box') WHERE unit_of_measure = 'box';
UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'bottle') WHERE unit_of_measure IN ('500 ml bottle', '1.5 L bottle', '400 ml bottle');
UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'tube') WHERE unit_of_measure = 'tube';

UPDATE products SET unit_id = (SELECT id FROM units_of_measure WHERE code = 'ea') WHERE unit_id IS NULL;

ALTER TABLE products DROP COLUMN unit_of_measure;
