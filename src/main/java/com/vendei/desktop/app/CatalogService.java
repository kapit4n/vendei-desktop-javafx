package com.vendei.desktop.app;

import com.vendei.desktop.domain.Product;
import com.vendei.desktop.infra.catalog.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CatalogService {
    private final ProductRepository products;

    public CatalogService(ProductRepository products) {
        this.products = Objects.requireNonNull(products);
    }

    public Optional<Product> findById(long id) {
        return products.findById(id);
    }

    public List<Product> listProducts(String query) {
        return products.listVisible(query);
    }

    public int countProducts(String query) {
        return products.countVisible(query);
    }

    public List<Product> listAllRegistered(String query) {
        return products.listAll(query);
    }

    public int countAllRegistered(String query) {
        return products.countAll(query);
    }
}

