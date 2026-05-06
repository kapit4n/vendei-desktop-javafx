package com.vendei.desktop.domain;

public record ProductSalesRow(
        long productId,
        String productName,
        double quantitySold,
        double revenue
) {}
