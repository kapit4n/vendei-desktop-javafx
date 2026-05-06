package com.vendei.desktop.app;

import com.vendei.desktop.domain.Product;
import com.vendei.desktop.infra.catalog.ProductRepository;

import java.util.List;
import java.util.Objects;

public final class CatalogService {
    private final ProductRepository products;

    public CatalogService(ProductRepository products) {
        this.products = Objects.requireNonNull(products);
    }

    public List<Product> listProducts(String query) {
        return products.listVisible(query);
    }
}

