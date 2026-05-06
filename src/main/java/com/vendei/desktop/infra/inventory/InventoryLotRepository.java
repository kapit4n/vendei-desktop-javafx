package com.vendei.desktop.infra.inventory;

import com.vendei.desktop.domain.InventoryLot;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.List;
import java.util.Objects;

import static org.jooq.impl.DSL.*;

public final class InventoryLotRepository {
    private final DSLContext dsl;

    public InventoryLotRepository(DSLContext dsl) {
        this.dsl = Objects.requireNonNull(dsl);
    }

    public List<InventoryLot> listByProduct(long productId) {
        var t = table(name("inventory_lots"));
        return dsl.select(
                        field(name("id")),
                        field(name("product_id")),
                        field(name("quantity")),
                        field(name("expiry_date")),
                        field(name("batch_code")),
                        field(name("received_at"))
                )
                .from(t)
                .where(field(name("product_id")).eq(inline(productId)))
                .orderBy(field(name("received_at")).desc(), field(name("id")).desc())
                .fetch(this::map);
    }

    private InventoryLot map(Record r) {
        return new InventoryLot(
                r.get(field(name("id")), Long.class),
                r.get(field(name("product_id")), Long.class),
                r.get(field(name("quantity")), Double.class),
                r.get(field(name("expiry_date")), String.class),
                r.get(field(name("batch_code")), String.class),
                r.get(field(name("received_at")), String.class)
        );
    }
}
