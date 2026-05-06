package com.vendei.desktop.infra.catalog;

import com.vendei.desktop.domain.Product;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Condition;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

public final class ProductRepository {
    private final DSLContext dsl;

    public ProductRepository(DSLContext dsl) {
        this.dsl = Objects.requireNonNull(dsl);
    }

    public Optional<Product> findById(long id) {
        var p = table(name("products"));
        var r = dsl.select(
                        field(name("id")),
                        field(name("name")),
                        field(name("code")),
                        field(name("image_url")),
                        field(name("visible")),
                        field(name("price")),
                        field(name("stock"))
                )
                .from(p)
                .where(field(name("id")).eq(inline(id)))
                .fetchOne();
        return r == null ? Optional.empty() : Optional.of(mapProduct(r));
    }

    public List<Product> listVisible(String query) {
        var p = table(name("products"));
        var cond = field(name("visible")).eq(inline(1));
        if (query != null && !query.isBlank()) {
            var like = "%" + query.trim() + "%";
            cond = cond.and(
                    field(name("name")).likeIgnoreCase(like)
                            .or(field(name("code")).likeIgnoreCase(like))
            );
        }

        return dsl.select(
                        field(name("id")),
                        field(name("name")),
                        field(name("code")),
                        field(name("image_url")),
                        field(name("visible")),
                        field(name("price")),
                        field(name("stock"))
                )
                .from(p)
                .where(cond)
                .orderBy(field(name("name")).asc())
                .limit(500)
                .fetch(this::mapProduct);
    }

    public int countVisible(String query) {
        var p = table(name("products"));
        var cond = field(name("visible")).eq(inline(1));
        if (query != null && !query.isBlank()) {
            var like = "%" + query.trim() + "%";
            cond = cond.and(
                    field(name("name")).likeIgnoreCase(like)
                            .or(field(name("code")).likeIgnoreCase(like))
            );
        }
        Record1<Integer> row = dsl.selectCount()
                .from(p)
                .where(cond)
                .fetchOne();
        return row == null ? 0 : row.value1();
    }

    /** All rows in {@code products} (including not visible), optional name/code filter. */
    public List<Product> listAll(String query) {
        var p = table(name("products"));
        Condition cond = trueCondition();
        if (query != null && !query.isBlank()) {
            var like = "%" + query.trim() + "%";
            cond = cond.and(
                    field(name("name")).likeIgnoreCase(like)
                            .or(field(name("code")).likeIgnoreCase(like))
            );
        }
        return dsl.select(
                        field(name("id")),
                        field(name("name")),
                        field(name("code")),
                        field(name("image_url")),
                        field(name("visible")),
                        field(name("price")),
                        field(name("stock"))
                )
                .from(p)
                .where(cond)
                .orderBy(field(name("id")).asc())
                .fetch(this::mapProduct);
    }

    public int countAll(String query) {
        var p = table(name("products"));
        Condition cond = trueCondition();
        if (query != null && !query.isBlank()) {
            var like = "%" + query.trim() + "%";
            cond = cond.and(
                    field(name("name")).likeIgnoreCase(like)
                            .or(field(name("code")).likeIgnoreCase(like))
            );
        }
        Record1<Integer> row = dsl.selectCount()
                .from(p)
                .where(cond)
                .fetchOne();
        return row == null ? 0 : row.value1();
    }

    private Product mapProduct(Record r) {
        return new Product(
                r.get(field(name("id")), Long.class),
                r.get(field(name("name")), String.class),
                r.get(field(name("code")), String.class),
                r.get(field(name("image_url")), String.class),
                asBoolean(r.get(field(name("visible")), Integer.class)),
                r.get(field(name("price")), Double.class),
                r.get(field(name("stock")), Double.class)
        );
    }

    private static boolean asBoolean(Integer i) {
        return i != null && i != 0;
    }
}

