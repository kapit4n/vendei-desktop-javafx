-- Reference list of sale / stock units (codes are stable; see SaleUnitCodes.java)
CREATE TABLE IF NOT EXISTS units_of_measure (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  code TEXT NOT NULL UNIQUE,
  label TEXT NOT NULL,
  sort_order INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_units_of_measure_code ON units_of_measure(code);

INSERT INTO units_of_measure (code, label, sort_order) VALUES
  ('ea', 'Each / unit', 10),
  ('kg', 'Kilogram', 20),
  ('g', 'Gram', 30),
  ('lb', 'Pound', 40),
  ('L', 'Liter', 50),
  ('ml', 'Milliliter', 60),
  ('pack', 'Pack', 70),
  ('box', 'Box', 80),
  ('bottle', 'Bottle', 90),
  ('tube', 'Tube', 100),
  ('roll', 'Roll', 110),
  ('pair', 'Pair', 120),
  ('carton', 'Carton', 130);
