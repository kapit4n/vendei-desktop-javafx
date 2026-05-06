package com.vendei.desktop.infra.catalog;

import com.vendei.desktop.app.NewProductDraft;
import com.vendei.desktop.domain.Product;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Field;
import org.jooq.SelectOnConditionStep;

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
        var r = productRowsBase()
                .where(field(name("p", "id")).eq(inline(id)))
                .fetchOne();
        return r == null ? Optional.empty() : Optional.of(mapProduct(r));
    }

    public List<Product> listVisible(String query) {
        var cond = field(name("p", "visible")).eq(inline(1));
        if (query != null && !query.isBlank()) {
            var like = "%" + query.trim() + "%";
            cond = cond.and(
                    field(name("p", "name")).likeIgnoreCase(like)
                            .or(field(name("p", "code")).likeIgnoreCase(like))
                            .or(field(name("p", "brand")).likeIgnoreCase(like))
            );
        }
        return productRowsBase()
                .where(cond)
                .orderBy(field(name("p", "name")).asc())
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
                            .or(field(name("brand")).likeIgnoreCase(like))
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
        Condition cond = trueCondition();
        if (query != null && !query.isBlank()) {
            var like = "%" + query.trim() + "%";
            cond = cond.and(
                    field(name("p", "name")).likeIgnoreCase(like)
                            .or(field(name("p", "code")).likeIgnoreCase(like))
                            .or(field(name("p", "brand")).likeIgnoreCase(like))
            );
        }
        return productRowsBase()
                .where(cond)
                .orderBy(field(name("p", "id")).asc())
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
                            .or(field(name("brand")).likeIgnoreCase(like))
            );
        }
        Record1<Integer> row = dsl.selectCount()
                .from(p)
                .where(cond)
                .fetchOne();
        return row == null ? 0 : row.value1();
    }

    public boolean existsCodeIgnoreCase(String code) {
        if (code == null || code.isBlank()) return false;
        var p = table(name("products"));
        Field<String> codeCol = field(name("code"), String.class);
        Record1<Integer> row = dsl.selectCount()
                .from(p)
                .where(lower(trim(codeCol)).eq(lower(inline(code.trim()))))
                .fetchOne();
        return row != null && row.value1() > 0;
    }

    public long insert(NewProductDraft d) {
        var p = table(name("products"));
        return dsl.insertInto(p)
                .columns(
                        field(name("name")),
                        field(name("code")),
                        field(name("image_url")),
                        field(name("visible")),
                        field(name("price")),
                        field(name("stock")),
                        field(name("track_expiry")),
                        field(name("default_shelf_life_days")),
                        field(name("category_id")),
                        field(name("brand")),
                        field(name("cost")),
                        field(name("unit_id"))
                )
                .values(
                        val(d.name().trim()),
                        val(d.code().trim()),
                        val(blankToNull(d.imageUrl())),
                        inline(d.visible() ? 1 : 0),
                        inline(d.price()),
                        inline(d.initialStock()),
                        inline(0),
                        val((Integer) null),
                        val((Long) null),
                        val(blankToNull(d.brand())),
                        inline(d.cost()),
                        inline(d.unitId())
                )
                .returning(field(name("id")))
                .fetchOne()
                .get(field(name("id")), Long.class);
    }

    private SelectOnConditionStep<?> productRowsBase() {
        var p = table(name("products")).as("p");
        var u = table(name("units_of_measure")).as("u");
        return dsl.select(
                        field(name("p", "id")).as("id"),
                        field(name("p", "name")).as("name"),
                        field(name("p", "code")).as("code"),
                        field(name("p", "image_url")).as("image_url"),
                        field(name("p", "visible")).as("visible"),
                        field(name("p", "price")).as("price"),
                        field(name("p", "stock")).as("stock"),
                        field(name("p", "brand")).as("brand"),
                        field(name("p", "unit_id")).as("unit_id"),
                        field(name("p", "cost")).as("cost"),
                        field(name("u", "code")).as("unit_code"),
                        field(name("u", "label")).as("unit_label")
                )
                .from(p)
                .leftJoin(u).on(field(name("p", "unit_id")).eq(field(name("u", "id"))));
    }

    private Product mapProduct(Record r) {
        Double cost = r.get(field(name("cost")), Double.class);
        Long unitId = r.get(field(name("unit_id")), Long.class);
        String unitCode = r.get(field(name("unit_code")), String.class);
        String unitLabel = r.get(field(name("unit_label")), String.class);
        return new Product(
                r.get(field(name("id")), Long.class),
                r.get(field(name("name")), String.class),
                r.get(field(name("code")), String.class),
                r.get(field(name("image_url")), String.class),
                asBoolean(r.get(field(name("visible")), Integer.class)),
                r.get(field(name("price")), Double.class),
                r.get(field(name("stock")), Double.class),
                r.get(field(name("brand")), String.class),
                unitId == null ? 0L : unitId,
                unitCode == null ? "" : unitCode,
                unitLabel == null ? "" : unitLabel,
                cost == null ? 0.0 : cost
        );
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) return null;
        return s.trim();
    }

    private static boolean asBoolean(Integer i) {
        return i != null && i != 0;
    }
}
