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

    private AppWiring(Connection conn, DSLContext dsl, CatalogService catalogService) {
        this.conn = conn;
        this.dsl = dsl;
        this.catalogService = catalogService;
    }

    public static AppWiring buildDefault() throws SQLException, IOException {
        var cfg = DbConfig.defaultConfig();
        Files.createDirectories(cfg.dbFile().getParent());
        Db.migrate(cfg);
        var conn = Db.connect(cfg);
        var dsl = Db.jooq(conn);
        var products = new ProductRepository(dsl);
        var catalog = new CatalogService(products);
        return new AppWiring(conn, dsl, catalog);
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}

