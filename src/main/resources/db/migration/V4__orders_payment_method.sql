-- Optional payment channel on completed orders (CASH / QR)
ALTER TABLE orders ADD COLUMN payment_method TEXT NULL;
