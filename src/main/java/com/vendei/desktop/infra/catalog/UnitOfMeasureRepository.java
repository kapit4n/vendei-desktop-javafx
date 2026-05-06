package com.vendei.desktop.infra.catalog;

import com.vendei.desktop.domain.UnitOfMeasure;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

public final class UnitOfMeasureRepository {
    private final DSLContext dsl;

    public UnitOfMeasureRepository(DSLContext dsl) {
        this.dsl = Objects.requireNonNull(dsl);
    }

    public List<UnitOfMeasure> listAllOrdered() {
        var t = table(name("units_of_measure"));
        return dsl.select(field(name("id")), field(name("code")), field(name("label")), field(name("sort_order")))
                .from(t)
                .orderBy(field(name("sort_order")).asc(), field(name("code")).asc())
                .fetch(this::map);
    }

    public Optional<UnitOfMeasure> findById(long id) {
        var t = table(name("units_of_measure"));
        var r = dsl.select(field(name("id")), field(name("code")), field(name("label")), field(name("sort_order")))
                .from(t)
                .where(field(name("id")).eq(inline(id)))
                .fetchOne();
        return r == null ? Optional.empty() : Optional.of(map(r));
    }

    public Optional<UnitOfMeasure> findByCode(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        var t = table(name("units_of_measure"));
        var r = dsl.select(field(name("id")), field(name("code")), field(name("label")), field(name("sort_order")))
                .from(t)
                .where(field(name("code")).eq(inline(code.trim())))
                .fetchOne();
        return r == null ? Optional.empty() : Optional.of(map(r));
    }

    public long insert(String code, String label, int sortOrder) {
        var t = table(name("units_of_measure"));
        return dsl.insertInto(t)
                .columns(field(name("code")), field(name("label")), field(name("sort_order")))
                .values(val(code.trim()), val(label.trim()), inline(sortOrder))
                .returning(field(name("id")))
                .fetchOne()
                .get(field(name("id")), Long.class);
    }

    public void update(long id, String code, String label, int sortOrder) {
        var t = table(name("units_of_measure"));
        dsl.update(t)
                .set(field(name("code")), val(code.trim()))
                .set(field(name("label")), val(label.trim()))
                .set(field(name("sort_order")), inline(sortOrder))
                .where(field(name("id")).eq(inline(id)))
                .execute();
    }

    public int countProductsUsing(long unitId) {
        var p = table(name("products"));
        Record1<Integer> row = dsl.selectCount()
                .from(p)
                .where(field(name("unit_id")).eq(inline(unitId)))
                .fetchOne();
        return row == null ? 0 : row.value1();
    }

    public void delete(long id) {
        var t = table(name("units_of_measure"));
        dsl.deleteFrom(t).where(field(name("id")).eq(inline(id))).execute();
    }

    private UnitOfMeasure map(Record r) {
        return new UnitOfMeasure(
                r.get(field(name("id")), Long.class),
                r.get(field(name("code")), String.class),
                r.get(field(name("label")), String.class),
                r.get(field(name("sort_order")), Integer.class)
        );
    }
}
