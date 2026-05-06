-- Flyway migration V1: initial schema (SQLite); FKs via JDBC ?foreign_keys=on (not PRAGMA).

CREATE TABLE IF NOT EXISTS categories (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  code TEXT NULL,
  image_url TEXT NULL,
  visible INTEGER NOT NULL DEFAULT 1,
  price REAL NOT NULL DEFAULT 0.0,
  stock REAL NOT NULL DEFAULT 0.0,
  track_expiry INTEGER NOT NULL DEFAULT 0,
  default_shelf_life_days INTEGER NULL,
  category_id INTEGER NULL,
  FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE INDEX IF NOT EXISTS idx_products_code ON products(code);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);

CREATE TABLE IF NOT EXISTS inventory_lots (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  product_id INTEGER NOT NULL,
  quantity REAL NOT NULL DEFAULT 0.0,
  expiry_date TEXT NULL, -- ISO date
  batch_code TEXT NULL,
  received_at TEXT NOT NULL DEFAULT (datetime('now')),
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_inventory_lots_product ON inventory_lots(product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_lots_expiry ON inventory_lots(expiry_date);

CREATE TABLE IF NOT EXISTS customers (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  document TEXT NULL
);
CREATE INDEX IF NOT EXISTS idx_customers_document ON customers(document);

CREATE TABLE IF NOT EXISTS orders (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  created_at TEXT NOT NULL DEFAULT (datetime('now')),
  customer_id INTEGER NULL,
  total REAL NOT NULL DEFAULT 0.0,
  paid INTEGER NOT NULL DEFAULT 1,
  delivered INTEGER NOT NULL DEFAULT 1,
  FOREIGN KEY (customer_id) REFERENCES customers(id)
);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);

CREATE TABLE IF NOT EXISTS order_lines (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  quantity REAL NOT NULL DEFAULT 1.0,
  unit_price REAL NOT NULL DEFAULT 0.0,
  line_total REAL NOT NULL DEFAULT 0.0,
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id),
  UNIQUE(order_id, product_id)
);
CREATE INDEX IF NOT EXISTS idx_order_lines_order ON order_lines(order_id);

CREATE TABLE IF NOT EXISTS purchase_requests (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  created_at TEXT NOT NULL DEFAULT (datetime('now')),
  status TEXT NOT NULL DEFAULT 'open',
  notes TEXT NULL
);

CREATE TABLE IF NOT EXISTS purchase_request_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  request_id INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  quantity REAL NOT NULL DEFAULT 1.0,
  FOREIGN KEY (request_id) REFERENCES purchase_requests(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id),
  UNIQUE(request_id, product_id)
);

