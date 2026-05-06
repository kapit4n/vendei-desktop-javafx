package com.vendei.desktop.app;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public enum SalesReportPeriod {
    TODAY,
    THIS_WEEK,
    THIS_MONTH;

    public LocalDateTime startInclusive(ZoneId zone) {
        Objects.requireNonNull(zone);
        LocalDate today = LocalDate.now(zone);
        return switch (this) {
            case TODAY -> LocalDateTime.of(today, LocalTime.MIDNIGHT);
            case THIS_WEEK -> {
                LocalDate monday = today.with(DayOfWeek.MONDAY);
                yield LocalDateTime.of(monday, LocalTime.MIDNIGHT);
            }
            case THIS_MONTH -> {
                LocalDate first = today.withDayOfMonth(1);
                yield LocalDateTime.of(first, LocalTime.MIDNIGHT);
            }
        };
    }

    public LocalDateTime endExclusive(ZoneId zone) {
        Objects.requireNonNull(zone);
        return switch (this) {
            case TODAY -> startInclusive(zone).plusDays(1);
            case THIS_WEEK -> startInclusive(zone).plusWeeks(1);
            case THIS_MONTH -> startInclusive(zone).plusMonths(1);
        };
    }

    public String describeRange(ZoneId zone) {
        var fmt = DateTimeFormatter.ofPattern("d MMM yyyy");
        LocalDateTime start = startInclusive(zone);
        LocalDateTime endEx = endExclusive(zone);
        LocalDate endDay = endEx.toLocalDate().minusDays(1);
        return switch (this) {
            case TODAY -> start.toLocalDate().format(fmt);
            case THIS_WEEK -> start.toLocalDate().format(fmt) + " – " + endDay.format(fmt);
            case THIS_MONTH -> start.toLocalDate().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        };
    }

    public String shortLabel() {
        return switch (this) {
            case TODAY -> "Today";
            case THIS_WEEK -> "This week";
            case THIS_MONTH -> "This month";
        };
    }
}
