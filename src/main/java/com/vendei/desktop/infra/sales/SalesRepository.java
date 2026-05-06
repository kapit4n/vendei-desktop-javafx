package com.vendei.desktop.infra.sales;

import com.vendei.desktop.domain.PaymentMethod;
import com.vendei.desktop.domain.ProductSalesRow;
import com.vendei.desktop.domain.TicketLine;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.jooq.impl.DSL.*;

public final class SalesRepository {
    private static final DateTimeFormatter SQLITE_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DSLContext dsl;

    public SalesRepository(DSLContext dsl) {
        this.dsl = Objects.requireNonNull(dsl);
    }

    public void insertCompletedOrder(
            Long customerId,
            double orderTotal,
            PaymentMethod paymentMethod,
            List<TicketLine> lines,
            LocalDateTime createdAt
    ) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Cannot save an order with no lines");
        }
        var createdStr = createdAt.format(SQLITE_DT);
        var pay = paymentMethod.name();
        dsl.transaction(cfg -> {
            var d = DSL.using(cfg);
            long orderId = d.insertInto(table(name("orders")))
                    .columns(
                            field(name("customer_id")),
                            field(name("total")),
                            field(name("paid")),
                            field(name("delivered")),
                            field(name("payment_method")),
                            field(name("created_at"))
                    )
                    .values(
                            val(customerId, Long.class),
                            inline(orderTotal),
                            inline(1),
                            inline(1),
                            val(pay),
                            val(createdStr)
                    )
                    .returning(field(name("id")))
                    .fetchOne()
                    .get(field(name("id")), Long.class);

            for (var line : lines) {
                d.insertInto(table(name("order_lines")))
                        .columns(
                                field(name("order_id")),
                                field(name("product_id")),
                                field(name("quantity")),
                                field(name("unit_price")),
                                field(name("line_total"))
                        )
                        .values(
                                inline(orderId),
                                inline(line.productId()),
                                inline(line.quantity()),
                                inline(line.unitPrice()),
                                inline(line.lineTotal())
                        )
                        .execute();
            }
        });
    }

    public List<ProductSalesRow> productSalesBetween(String startInclusive, String endExclusive) {
        return dsl.select(
                        field(DSL.name("ol", "product_id"), Long.class).as("product_id"),
                        field(DSL.name("p", "name"), String.class).as("product_name"),
                        DSL.sum(field(DSL.name("ol", "quantity"), Double.class)).as("qty"),
                        DSL.sum(field(DSL.name("ol", "line_total"), Double.class)).as("rev")
                )
                .from(table(name("order_lines")).as("ol"))
                .join(table(name("orders")).as("o"))
                .on(field(DSL.name("ol", "order_id"), Long.class).eq(field(DSL.name("o", "id"), Long.class)))
                .join(table(name("products")).as("p"))
                .on(field(DSL.name("ol", "product_id"), Long.class).eq(field(DSL.name("p", "id"), Long.class)))
                .where(field(DSL.name("o", "created_at"), String.class).ge(inline(startInclusive))
                        .and(field(DSL.name("o", "created_at"), String.class).lt(inline(endExclusive))))
                .groupBy(field(DSL.name("ol", "product_id"), Long.class), field(DSL.name("p", "name"), String.class))
                .orderBy(DSL.sum(field(DSL.name("ol", "line_total"), Double.class)).desc())
                .fetch(r -> new ProductSalesRow(
                        r.get("product_id", Long.class),
                        r.get("product_name", String.class),
                        toDouble(r.get("qty")),
                        toDouble(r.get("rev"))
                ));
    }

    private static double toDouble(Object v) {
        if (v == null) return 0.0;
        if (v instanceof Number n) return n.doubleValue();
        return Double.parseDouble(v.toString());
    }

    public long countOrdersBetween(String startInclusive, String endExclusive) {
        var o = table(name("orders"));
        Record1<Integer> row = dsl.selectCount()
                .from(o)
                .where(field(name("created_at"), String.class).ge(inline(startInclusive))
                        .and(field(name("created_at"), String.class).lt(inline(endExclusive))))
                .fetchOne();
        return row == null ? 0 : row.value1();
    }

    public double sumRevenueBetween(String startInclusive, String endExclusive) {
        var o = table(name("orders"));
        Record1<BigDecimal> row = dsl.select(DSL.sum(field(name("total"), Double.class)))
                .from(o)
                .where(field(name("created_at"), String.class).ge(inline(startInclusive))
                        .and(field(name("created_at"), String.class).lt(inline(endExclusive))))
                .fetchOne();
        BigDecimal v = row == null ? null : row.value1();
        return v == null ? 0.0 : v.doubleValue();
    }
}
