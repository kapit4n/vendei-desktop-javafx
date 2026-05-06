package com.vendei.desktop.infra.customers;

import com.vendei.desktop.domain.Customer;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

public final class CustomerRepository {
    private final DSLContext dsl;

    public CustomerRepository(DSLContext dsl) {
        this.dsl = Objects.requireNonNull(dsl);
    }

    public List<Customer> listAll(String query) {
        var t = table(name("customers"));
        Condition cond = trueCondition();
        if (query != null && !query.isBlank()) {
            var like = "%" + query.trim() + "%";
            cond = cond.and(
                    field(name("name")).likeIgnoreCase(like)
                            .or(field(name("document")).likeIgnoreCase(like))
            );
        }
        return dsl.select(field(name("id")), field(name("name")), field(name("document")))
                .from(t)
                .where(cond)
                .orderBy(field(name("name")).asc(), field(name("id")).asc())
                .fetch(this::map);
    }

    public int countAll(String query) {
        var t = table(name("customers"));
        Condition cond = trueCondition();
        if (query != null && !query.isBlank()) {
            var like = "%" + query.trim() + "%";
            cond = cond.and(
                    field(name("name")).likeIgnoreCase(like)
                            .or(field(name("document")).likeIgnoreCase(like))
            );
        }
        Record1<Integer> row = dsl.selectCount().from(t).where(cond).fetchOne();
        return row == null ? 0 : row.value1();
    }

    public Optional<Customer> findById(long id) {
        var t = table(name("customers"));
        var r = dsl.select(field(name("id")), field(name("name")), field(name("document")))
                .from(t)
                .where(field(name("id")).eq(inline(id)))
                .fetchOne();
        return r == null ? Optional.empty() : Optional.of(map(r));
    }

    public Customer insert(String name, String document) {
        Objects.requireNonNull(name);
        var trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        var doc = document == null || document.isBlank() ? null : document.trim();
        var t = table(name("customers"));
        long id = dsl.insertInto(t)
                .columns(field(name("name")), field(name("document")))
                .values(val(trimmed), val(doc))
                .returning(field(name("id")))
                .fetchOne()
                .get(field(name("id")), Long.class);
        return new Customer(id, trimmed, doc);
    }

    private Customer map(Record r) {
        return new Customer(
                r.get(field(name("id")), Long.class),
                r.get(field(name("name")), String.class),
                r.get(field(name("document")), String.class)
        );
    }
}
