package com.vendei.desktop.app;

import com.vendei.desktop.domain.TicketLine;
import com.vendei.desktop.infra.sales.SalesRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public final class SalesService {
    private static final DateTimeFormatter SQLITE_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SalesRepository sales;

    public SalesService(SalesRepository sales) {
        this.sales = Objects.requireNonNull(sales);
    }

    public void recordCompletedSale(TicketService ticket) {
        Objects.requireNonNull(ticket);
        List<TicketLine> lines = List.copyOf(ticket.lines());
        if (lines.isEmpty()) {
            throw new IllegalStateException("No lines to record");
        }
        Long customerId = ticket.customerIdProperty().get();
        double total = ticket.subtotalProperty().get();
        var method = ticket.paymentMethodProperty().get();
        sales.insertCompletedOrder(customerId, total, method, lines, LocalDateTime.now());
    }

    public ProductSalesReport productSales(SalesReportPeriod period, ZoneId zone) {
        Objects.requireNonNull(period);
        Objects.requireNonNull(zone);
        String start = period.startInclusive(zone).format(SQLITE_DT);
        String end = period.endExclusive(zone).format(SQLITE_DT);
        var rows = sales.productSalesBetween(start, end);
        long orders = sales.countOrdersBetween(start, end);
        double revenue = sales.sumRevenueBetween(start, end);
        return new ProductSalesReport(period, period.describeRange(zone), rows, revenue, orders);
    }
}
