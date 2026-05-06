package com.vendei.desktop.app;

import com.vendei.desktop.domain.InventoryLot;
import com.vendei.desktop.infra.inventory.InventoryLotRepository;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.jooq.impl.DSL.*;

public final class InventoryService {
    private final DSLContext dsl;
    private final InventoryLotRepository lots;

    public InventoryService(DSLContext dsl) {
        this.dsl = Objects.requireNonNull(dsl);
        this.lots = new InventoryLotRepository(dsl);
    }

    public List<InventoryLot> listLots(long productId) {
        return lots.listByProduct(productId);
    }

    /**
     * Inserts an {@code inventory_lots} row and increments {@code products.stock} in one transaction.
     *
     * @param expiry optional; stored as ISO-8601 date ({@code yyyy-MM-dd}) or {@code null}
     */
    public void receiveStock(long productId, double quantity, String lotCode, LocalDate expiry) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        Objects.requireNonNull(lotCode, "lotCode");
        var lot = lotCode.trim();
        if (lot.isEmpty()) {
            throw new IllegalArgumentException("Lot / batch code is required");
        }
        var expiryStr = expiry == null ? null : expiry.toString();

        dsl.transaction(cfg -> {
            var d = DSL.using(cfg);
            d.insertInto(table(name("inventory_lots")))
                    .columns(
                            field(name("product_id")),
                            field(name("quantity")),
                            field(name("expiry_date")),
                            field(name("batch_code"))
                    )
                    .values(inline(productId), inline(quantity), val(expiryStr), val(lot))
                    .execute();

            d.update(table(name("products")))
                    .set(
                            field(name("stock")),
                            field(name("stock"), Double.class).add(inline(quantity))
                    )
                    .where(field(name("id")).eq(inline(productId)))
                    .execute();
        });
    }
}
