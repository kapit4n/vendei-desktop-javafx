package com.vendei.desktop.app;

import com.vendei.desktop.domain.ProductSalesRow;

import java.util.List;

public record ProductSalesReport(
        SalesReportPeriod period,
        String rangeDescription,
        List<ProductSalesRow> rows,
        double totalRevenue,
        long orderCount
) {}
