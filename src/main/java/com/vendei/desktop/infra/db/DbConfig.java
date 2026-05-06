package com.vendei.desktop.infra.db;

import java.nio.file.Path;

public record DbConfig(Path dbFile) {
    public static DbConfig defaultConfig() {
        return new DbConfig(Path.of("data", "vendei.sqlite"));
    }
}

