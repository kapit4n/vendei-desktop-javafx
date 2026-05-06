package com.vendei.desktop.ui;

import com.vendei.desktop.app.CatalogService;
import com.vendei.desktop.app.NewProductDraft;
import com.vendei.desktop.domain.UnitOfMeasure;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

import java.util.Objects;

/** Form to add a new SKU to {@code products} with a reference unit of measure. */
public final class RegisterProductView extends GridPane {
    private final CatalogService catalog;
    private final Runnable onSuccess;

    private final TextField name = new TextField();
    private final TextField code = new TextField();
    private final TextField brand = new TextField();
    private final ComboBox<UnitOfMeasure> unit = new ComboBox<>();
    private final TextField price = new TextField();
    private final TextField cost = new TextField();
    private final TextField stock = new TextField();
    private final TextField imageUrl = new TextField();
    private final CheckBox visible = new CheckBox("Visible in POS");
    private final Button save = new Button("Save product");

    public RegisterProductView(CatalogService catalog, Runnable onSuccess) {
        this.catalog = Objects.requireNonNull(catalog);
        this.onSuccess = onSuccess;

        setPadding(new Insets(16));
        setHgap(10);
        setVgap(10);

        unit.setItems(FXCollections.observableArrayList(catalog.listUnits()));
        unit.setConverter(new StringConverter<>() {
            @Override
            public String toString(UnitOfMeasure u) {
                if (u == null) return "";
                return u.label() + " (" + u.code() + ")";
            }

            @Override
            public UnitOfMeasure fromString(String s) {
                return null;
            }
        });
        if (!unit.getItems().isEmpty()) unit.getSelectionModel().selectFirst();

        visible.setSelected(true);

        int r = 0;
        add(new Label("Name *"), 0, r);
        add(name, 1, r);
        GridPane.setHgrow(name, Priority.ALWAYS);
        r++;
        add(new Label("Code *"), 0, r);
        add(code, 1, r);
        GridPane.setHgrow(code, Priority.ALWAYS);
        r++;
        add(new Label("Brand"), 0, r);
        add(brand, 1, r);
        GridPane.setHgrow(brand, Priority.ALWAYS);
        r++;
        add(new Label("Unit *"), 0, r);
        add(unit, 1, r);
        GridPane.setHgrow(unit, Priority.ALWAYS);
        r++;
        add(new Label("Price (Bs) *"), 0, r);
        add(price, 1, r);
        GridPane.setHgrow(price, Priority.ALWAYS);
        r++;
        add(new Label("Cost (Bs) *"), 0, r);
        add(cost, 1, r);
        GridPane.setHgrow(cost, Priority.ALWAYS);
        r++;
        add(new Label("Initial stock *"), 0, r);
        add(stock, 1, r);
        GridPane.setHgrow(stock, Priority.ALWAYS);
        r++;
        add(new Label("Image URL"), 0, r);
        add(imageUrl, 1, r);
        GridPane.setHgrow(imageUrl, Priority.ALWAYS);
        r++;
        add(new Label(""), 0, r);
        add(visible, 1, r);
        r++;

        var actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.getChildren().add(save);
        add(actions, 0, r, 2, 1);

        save.setOnAction(e -> saveProduct());
    }

    private void saveProduct() {
        var u = unit.getSelectionModel().getSelectedItem();
        if (u == null) {
            alert(Alert.AlertType.WARNING, "Select a unit of measure (add units under Catalog first if the list is empty).");
            return;
        }
        try {
            double p = parseMoney(price.getText());
            double c = parseMoney(cost.getText());
            double st = parseQty(stock.getText());
            var draft = new NewProductDraft(
                    name.getText(),
                    code.getText(),
                    brand.getText(),
                    u.id(),
                    p,
                    c,
                    st,
                    visible.isSelected(),
                    imageUrl.getText()
            );
            long id = catalog.registerProduct(draft);
            if (onSuccess != null) onSuccess.run();
            var a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Product registered");
            a.setContentText("Saved as product #" + id + ".");
            a.showAndWait();
            clearForm();
        } catch (IllegalArgumentException ex) {
            alert(Alert.AlertType.ERROR, ex.getMessage() != null ? ex.getMessage() : ex.toString());
        }
    }

    private void clearForm() {
        name.clear();
        code.clear();
        brand.clear();
        price.clear();
        cost.clear();
        stock.clear();
        imageUrl.clear();
        visible.setSelected(true);
        if (!unit.getItems().isEmpty()) unit.getSelectionModel().selectFirst();
    }

    private static void alert(Alert.AlertType type, String msg) {
        var a = new Alert(type);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static double parseMoney(String s) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException("Enter price and cost.");
        var t = s.trim().replace(",", ".");
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + s);
        }
    }

    private static double parseQty(String s) {
        if (s == null || s.isBlank()) return 0.0;
        var t = s.trim().replace(",", ".");
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid stock quantity: " + s);
        }
    }
}
