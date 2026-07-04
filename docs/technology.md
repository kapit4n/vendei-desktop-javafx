# Technology

## Runtime

| Component | Version |
|---|---|
| Java | 21 (also compatible with 17) |
| JavaFX | 21.0.4 (`javafx.controls` only) |

## Build

- **Gradle** (Kotlin DSL) with `application` and `java` plugins
- **JavaFX plugin** (`org.openjfx.javafxplugin:0.1.0`) — manages JavaFX module path
- **Wrapper** included (`gradlew` / `gradlew.bat`)
- **Main class**: `com.vendei.desktop.Main`
- **JVM args**: `-Xmx1g -Dfile.encoding=UTF-8`

## Dependencies

| Library | Purpose |
|---|---|
| `org.xerial:sqlite-jdbc:3.46.0.0` | SQLite JDBC driver |
| `org.flywaydb:flyway-core:10.17.0` | Database schema migrations |
| `org.jooq:jooq:3.19.10` | Type-safe SQL DSL (no ORM) |
| `jakarta.xml.bind:jakarta.xml.bind-api:4.0.2` | JAXB (required by jOOQ) |
| `org.slf4j:slf4j-simple:2.0.13` | Logging |
| `org.junit.jupiter:junit-jupiter` | Testing (JUnit 5 BOM 5.10.3) |

## Database

- **SQLite** — Embedded, zero-config, single-file database
- **Flyway** — Versioned SQL migrations (`V1__init.sql` through `V8`)
- **jOOQ** — DSL-based query construction (no runtime code generation, no ORM mapping)

## UI

- **Pure JavaFX** — No FXML, no CSS files, no Scene Builder
- All views built programmatically using JavaFX layout panes (`BorderPane`, `GridPane`, `TilePane`, `VBox`, `HBox`, `StackPane`)
- Inline styling via `setStyle()` strings
- Single-window app with menu-driven secondary stages

## Architecture

- No dependency injection framework (manual wiring)
- No ORM (raw jOOQ DSL + JDBC)
- No modular JavaFX (`module-info.java`)
- No external state management (JavaFX properties suffice)
