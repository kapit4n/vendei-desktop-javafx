package com.vendei.desktop.app;

import com.vendei.desktop.infra.catalog.ProductRepository;
import com.vendei.desktop.infra.db.Db;
import com.vendei.desktop.infra.db.DbConfig;
import org.jooq.DSLContext;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

public final class AppWiring implements AutoCloseable {
    public final Connection conn;
    public final DSLContext dsl;
    public final CatalogService catalogService;
    public final TicketService ticketService;

    private AppWiring(Connection conn, DSLContext dsl, CatalogService catalogService, TicketService ticketService) {
        this.conn = conn;
        this.dsl = dsl;
        this.catalogService = catalogService;
        this.ticketService = ticketService;
    }

    public static AppWiring buildDefault() throws SQLException, IOException {
        var cfg = DbConfig.defaultConfig();
        Files.createDirectories(cfg.dbFile().getParent());
        Db.migrate(cfg);
        var conn = Db.connect(cfg);
        var dsl = Db.jooq(conn);
        var products = new ProductRepository(dsl);
        var catalog = new CatalogService(products);
        var ticket = new TicketService();
        return new AppWiring(conn, dsl, catalog, ticket);
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}

