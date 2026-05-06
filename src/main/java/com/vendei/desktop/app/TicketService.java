package com.vendei.desktop.app;

import com.vendei.desktop.domain.Customer;
import com.vendei.desktop.domain.PaymentMethod;
import com.vendei.desktop.domain.Product;
import com.vendei.desktop.domain.TicketLine;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Objects;

public final class TicketService {
    private final ObservableList<TicketLine> lines = FXCollections.observableArrayList();

    private final ReadOnlyDoubleWrapper subtotal = new ReadOnlyDoubleWrapper(0.0);
    private final ObjectProperty<PaymentMethod> paymentMethod = new SimpleObjectProperty<>(PaymentMethod.CASH);
    private final ObjectProperty<Long> customerId = new SimpleObjectProperty<>(null);
    private final StringProperty customerName = new SimpleStringProperty("Anonymous");
    private final StringProperty customerCi = new SimpleStringProperty("");
    private final DoubleProperty amountReceived = new SimpleDoubleProperty(0.0);

    public TicketService() {
        lines.addListener((ListChangeListener<TicketLine>) change -> recalc());
    }

    public ObservableList<TicketLine> lines() {
        return lines;
    }

    public ReadOnlyDoubleProperty subtotalProperty() {
        return subtotal.getReadOnlyProperty();
    }

    public ObjectProperty<PaymentMethod> paymentMethodProperty() {
        return paymentMethod;
    }

    public ObjectProperty<Long> customerIdProperty() {
        return customerId;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public StringProperty customerCiProperty() {
        return customerCi;
    }

    public void setCustomer(Customer c) {
        Objects.requireNonNull(c);
        customerId.set(c.id());
        customerName.set(c.name());
        if (c.document() != null && !c.document().isBlank()) {
            customerCi.set("CI: " + c.document());
        } else {
            customerCi.set("");
        }
    }

    public void clearCustomer() {
        customerId.set(null);
        customerName.set("Anonymous");
        customerCi.set("");
    }

    public DoubleProperty amountReceivedProperty() {
        return amountReceived;
    }

    /** Clears ticket lines and payment amount; does not change the selected client. */
    public void clear() {
        lines.clear();
        amountReceived.set(0.0);
    }

    /** Full reset for a new sale (e.g. after payment). */
    public void resetForNextSale() {
        clear();
        clearCustomer();
    }

    public void addProduct(Product p) {
        addProduct(p, 1.0);
    }

    public void addProduct(Product p, double qty) {
        Objects.requireNonNull(p);
        if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");

        for (var i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (line.productId() == p.id()) {
                lines.set(i, line.withQuantity(line.quantity() + qty));
                return;
            }
        }

        lines.add(new TicketLine(p.id(), p.name(), p.code(), p.price(), qty, saleUnitLabel(p)));
    }

    private static String saleUnitLabel(Product p) {
        return p.saleUnitDisplay();
    }

    public void setQuantity(long productId, double qty) {
        for (var i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (line.productId() == productId) {
                if (qty <= 0) lines.remove(i);
                else lines.set(i, line.withQuantity(qty));
                return;
            }
        }
    }

    public void remove(long productId) {
        setQuantity(productId, 0);
    }

    private void recalc() {
        double sum = 0.0;
        for (var line : lines) sum += line.lineTotal();
        subtotal.set(sum);
    }
}

