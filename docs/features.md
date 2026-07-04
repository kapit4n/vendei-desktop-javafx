# Features

## Point of Sale

- **Product grid** — TilePane of product cards with image, name, brand, unit, price, and cost
- **Search** — Filter products by name, code, or brand
- **Quick add by code** — Type a product code and add it directly to the ticket
- **Category pills** — Visual filter tabs for browsing (cosmetic)

## Ticket Management

- Add products by clicking a product card
- Adjust quantity with +/- buttons per line
- Remove items from the ticket
- Real-time subtotal calculation
- Select customer (Walk-in, existing, or create new inline)
- Toggle payment method: Cash or QR

### Cash Payment

- Cash tender entry with quick-amount buttons (Bs 5/10/20/50/100/200)
- Automatic change calculation
- "Exact total" one-tap button
- Confirmation dialog before completing (shows total, tender, change, method)

## Sale Recording

- Persists completed orders with line items, timestamps, and payment method
- Updates product stock on sale completion

## Product Catalog Management

- **Register product** — Form with name, code, brand, unit, price, cost, initial stock, image URL, visibility toggle
- **All registered products** — Table view showing ID, name, code, brand, unit, cost, price, stock, visibility, image
- **Product details** — Full summary (code, brand, unit, cost, price, margin, stock, visibility) plus inventory lot history table
- **Units of measure** — CRUD management with safety check preventing deletion of units in use

## Inventory

- **Receive stock** — Add inventory lots with quantity, batch code (required), and optional expiry date
- **Lot history** — View all stock receipts per product, sorted by received date (newest first)
- **Automatic stock update** — Stock receipt increments product stock atomically in a transaction

## Customer Management

- **Client list** — Searchable table of customers (ID, name, document)
- **Create client** — Register with name (required) and document (optional)
- **Client picker** — Dialog integrated with the POS ticket flow

## Reporting

- **Product sales report** — Period selector (Today / This Week / This Month)
- Per-product breakdown: quantity sold and revenue
- Summary: total orders, total revenue, number of product lines sold
- Timezone-aware date range calculation

## Demo Data

- 20 seeded products across 6 categories
- 13 standard units of measure (ea, kg, g, lb, L, ml, pack, box, bottle, tube, roll, pair, carton)
- Bundled product images as classpath resources
