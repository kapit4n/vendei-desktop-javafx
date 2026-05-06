package com.vendei.desktop.app;

public record NewProductDraft(
        String name,
        String code,
        String brand,
        long unitId,
        double price,
        double cost,
        double initialStock,
        boolean visible,
        String imageUrl
) {}
