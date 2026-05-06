package com.vendei.desktop.domain;

public record InventoryLot(
        long id,
        long productId,
        double quantity,
        String expiryDate,
        String batchCode,
        String receivedAt
) {}
