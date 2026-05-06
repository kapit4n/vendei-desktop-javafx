# Vendei Desktop (JavaFX)

Java desktop POS app inspired by `ng-vendei-full`.

Tech:
- **Java 21** (or 17) + **JavaFX**
- **SQLite** database (single file)
- **Flyway** migrations
- **jOOQ** for SQL access (no runtime ORM)

## Quick start

```bash
cd vendei-desktop-javafx
./run.sh
```

The app creates the database at `./data/vendei.sqlite`.

## Architecture

- `app/` — application services / use-cases
- `domain/` — domain model (pure Java)
- `infra/` — database (Flyway + jOOQ repositories)
- `ui/` — JavaFX views/controllers

