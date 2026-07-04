# Architecture

## Layered Architecture

The application follows a clean layered architecture with strict dependency flow:

```
ui  →  app  →  domain
          ↓
       infra
```

### Layers

**`domain`** — Pure Java records and enums. Zero framework dependencies. Contains the business model: `Product`, `Customer`, `InventoryLot`, `TicketLine`, `UnitOfMeasure`, `SaleUnitCodes`, `PaymentMethod`, `ProductSalesRow`.

**`infra`** — Infrastructure layer. All database access lives here using the Repository pattern over jOOQ DSL queries. Sub-packages: `catalog`, `customers`, `inventory`, `sales`, `db`. Flyway migrations are in `src/main/resources/db/migration/`.

**`app`** — Application services / use-cases. Orchestrates domain objects and infra repositories. Classes: `CatalogService`, `InventoryService`, `CustomerService`, `SalesService`, `TicketService`. Also holds DTOs like `NewProductDraft` and `ProductSalesReport`.

**`ui`** — JavaFX views and controllers built programmatically (no FXML, no CSS files). Each screen is a self-contained class. Views include: `MainView` (POS), `RegisteredProductsView`, `RegisterProductView`, `ProductDetailsView`, `UnitsManagementView`, `ClientsView`, `ClientPickerDialog`, `ProductSalesReportView`, `AddStockDialog`, `CreateClientDialog`.

### Dependency Injection

Manual wiring via `AppWiring` (no DI framework). `AppWiring.buildDefault()` creates all services and repositories, connects to the database, runs Flyway migrations, and returns a closable container.

### Database

SQLite via JDBC with jOOQ for compile-time-safe SQL. Flyway handles schema migrations. Foreign keys enabled via JDBC URL parameter (`?foreign_keys=on`).

### UI State

The `TicketService` uses JavaFX observable properties (`ObservableList`, `SimpleDoubleProperty`, etc.) for automatic UI binding. No separate state management library.

## Key Patterns

| Pattern | Usage |
|---|---|
| Repository | All DB access via repository classes |
| Service Layer | Business logic in `app/*Service` |
| Record (Java 14+) | Immutable domain models |
| Observer (JavaFX) | Properties for automatic UI updates |
| Transaction Script | jOOQ `transaction()` for atomic operations |
| Enum Strategy | `PaymentMethod`, `SalesReportPeriod` |
| Sealed Interface | `ClientPickerDialog.Pick` (Anonymous / Selected) |
