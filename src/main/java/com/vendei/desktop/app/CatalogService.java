package com.vendei.desktop.app;

import com.vendei.desktop.domain.Product;
import com.vendei.desktop.domain.UnitOfMeasure;
import com.vendei.desktop.infra.catalog.ProductRepository;
import com.vendei.desktop.infra.catalog.UnitOfMeasureRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CatalogService {
    private final ProductRepository products;
    private final UnitOfMeasureRepository units;

    public CatalogService(ProductRepository products, UnitOfMeasureRepository units) {
        this.products = Objects.requireNonNull(products);
        this.units = Objects.requireNonNull(units);
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

    public List<UnitOfMeasure> listUnits() {
        return units.listAllOrdered();
    }

    public long registerProduct(NewProductDraft d) {
        Objects.requireNonNull(d);
        if (d.name() == null || d.name().isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (d.code() == null || d.code().isBlank()) {
            throw new IllegalArgumentException("Product code is required.");
        }
        units.findById(d.unitId()).orElseThrow(() -> new IllegalArgumentException("Select a valid unit of measure."));
        if (products.existsCodeIgnoreCase(d.code())) {
            throw new IllegalArgumentException("A product with this code already exists.");
        }
        if (d.price() < 0 || d.cost() < 0 || d.initialStock() < 0) {
            throw new IllegalArgumentException("Price, cost, and stock cannot be negative.");
        }
        return products.insert(d);
    }

    public long createUnit(String code, String label, int sortOrder) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Unit code is required.");
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Unit label is required.");
        if (units.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("A unit with this code already exists.");
        }
        return units.insert(code, label, sortOrder);
    }

    public void updateUnit(long id, String code, String label, int sortOrder) {
        units.findById(id).orElseThrow(() -> new IllegalArgumentException("Unit not found."));
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Unit code is required.");
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Unit label is required.");
        units.findByCode(code).ifPresent(u -> {
            if (u.id() != id) throw new IllegalArgumentException("Another unit already uses this code.");
        });
        units.update(id, code, label, sortOrder);
    }

    public void deleteUnit(long id) {
        units.findById(id).orElseThrow(() -> new IllegalArgumentException("Unit not found."));
        if (units.countProductsUsing(id) > 0) {
            throw new IllegalArgumentException("Cannot delete a unit that is assigned to products.");
        }
        units.delete(id);
    }
}
