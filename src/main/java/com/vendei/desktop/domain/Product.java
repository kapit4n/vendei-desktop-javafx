package com.vendei.desktop.domain;

public record Product(
        long id,
        String name,
        String code,
        String imageUrl,
        boolean visible,
        double price,
        double stock
) {}

