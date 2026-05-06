package com.vendei.desktop.domain;

/**
 * @param brand     manufacturer / label (different brand ⇒ can have different {@code cost} and {@code price})
 * @param unitId    FK to {@code units_of_measure}; stock qty is in this unit
 * @param unitCode  short code from joined row (e.g. {@code kg}, {@code ea})
 * @param unitLabel human-readable label from joined row
 * @param cost      acquisition / unit cost in Bs (margin = price − cost per unit)
 */
public record Product(
        long id,
        String name,
        String code,
        String imageUrl,
        boolean visible,
        double price,
        double stock,
        String brand,
        long unitId,
        String unitCode,
        String unitLabel,
        double cost
) {
    /** Label for POS ticket lines and cards; falls back to code or "unit". */
    public String saleUnitDisplay() {
        if (unitLabel != null && !unitLabel.isBlank()) return unitLabel;
        if (unitCode != null && !unitCode.isBlank()) return unitCode;
        return "unit";
    }

    /** Table / detail text: label with code when both differ. */
    public String unitSummary() {
        boolean hasLabel = unitLabel != null && !unitLabel.isBlank();
        boolean hasCode = unitCode != null && !unitCode.isBlank();
        if (hasLabel && hasCode && !unitLabel.equals(unitCode)) {
            return unitLabel + " (" + unitCode + ")";
        }
        if (hasLabel) return unitLabel;
        if (hasCode) return unitCode;
        return "";
    }
}
