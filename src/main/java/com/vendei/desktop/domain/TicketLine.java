package com.vendei.desktop.domain;

import java.util.Objects;

public record TicketLine(
        long productId,
        String name,
        String code,
        double unitPrice,
        double quantity
) {
    public TicketLine {
        Objects.requireNonNull(name);
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        if (unitPrice < 0) throw new IllegalArgumentException("unitPrice must be >= 0");
    }

    public double lineTotal() {
        return unitPrice * quantity;
    }

    public TicketLine withQuantity(double newQuantity) {
        return new TicketLine(productId, name, code, unitPrice, newQuantity);
    }
}

