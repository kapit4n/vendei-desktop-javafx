package com.vendei.desktop.infra.db;

import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public final class Db {
    private Db() {}

    /** SQLite JDBC URL with FK enforcement (avoids PRAGMA in Flyway migrations). */
    private static String sqliteUrl(String dbPath) {
        return "jdbc:sqlite:" + dbPath + "?foreign_keys=on";
    }

    public static Connection connect(DbConfig cfg) throws SQLException {
        Objects.requireNonNull(cfg);
        var dbPath = cfg.dbFile().toAbsolutePath().toString();
        return DriverManager.getConnection(sqliteUrl(dbPath));
    }

    public static void migrate(DbConfig cfg) {
        var dbPath = cfg.dbFile().toAbsolutePath().toString();
        Flyway.configure()
                .dataSource(sqliteUrl(dbPath), null, null)
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    public static DSLContext jooq(Connection conn) {
        return DSL.using(conn, SQLDialect.SQLITE);
    }
}

