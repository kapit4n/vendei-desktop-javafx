package com.vendei.desktop.ui;

import com.vendei.desktop.app.CatalogService;
import com.vendei.desktop.app.InventoryService;
import com.vendei.desktop.domain.Product;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Table of every product row in the database (including hidden SKUs).
 */
public final class RegisteredProductsView extends BorderPane {
    private final CatalogService catalog;
    private final InventoryService inventory;
    private final Consumer<Runnable> openRegisterProduct;
    private final Runnable openUnitsWindow;
    private final TableView<ProductRow> table = new TableView<>();
    private final TextField search = new TextField();
    private final Label countLabel = new Label();

    public RegisteredProductsView(
            CatalogService catalog,
            InventoryService inventory,
            Consumer<Runnable> openRegisterProduct,
            Runnable openUnitsWindow
    ) {
        this.catalog = Objects.requireNonNull(catalog);
        this.inventory = Objects.requireNonNull(inventory);
        this.openRegisterProduct = Objects.requireNonNull(openRegisterProduct);
        this.openUnitsWindow = Objects.requireNonNull(openUnitsWindow);

        var top = new HBox(10);
        top.setPadding(new Insets(12));
        top.setAlignment(Pos.CENTER_LEFT);
        search.setPromptText("Filter by name, code, or brand…");
        HBox.setHgrow(search, Priority.ALWAYS);
        var refresh = new Button("Refresh");
        refresh.setOnAction(e -> reload());
        var register = new Button("Register product…");
        register.setOnAction(e -> openRegisterProduct.accept(this::reload));
        var units = new Button("Units of measure…");
        units.setOnAction(e -> openUnitsWindow.run());
        var addStock = new Button("Add stock…");
        addStock.setOnAction(e -> selectedRow().ifPresent(this::addStockForRow));
        var details = new Button("Product details…");
        details.setOnAction(e -> selectedRow().ifPresent(this::openDetails));
        top.getChildren().addAll(new Label("Search:"), search, refresh, register, units, addStock, details, countLabel);
        setTop(top);

        var colId = new TableColumn<ProductRow, Long>("ID");
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colId.setPrefWidth(56);

        var colName = new TableColumn<ProductRow, String>("Name");
        colName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
        colName.setPrefWidth(220);

        var colCode = new TableColumn<ProductRow, String>("Code");
        colCode.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCode()));
        colCode.setPrefWidth(120);

        var colBrand = new TableColumn<ProductRow, String>("Brand");
        colBrand.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getBrand()));
        colBrand.setPrefWidth(100);

        var colUnit = new TableColumn<ProductRow, String>("Unit");
        colUnit.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getUnit()));
        colUnit.setPrefWidth(88);

        var colCost = new TableColumn<ProductRow, String>("Cost");
        colCost.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCostText()));
        colCost.setPrefWidth(72);

        var colPrice = new TableColumn<ProductRow, String>("Price");
        colPrice.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPriceText()));
        colPrice.setPrefWidth(88);

        var colStock = new TableColumn<ProductRow, String>("Stock");
        colStock.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStockText()));
        colStock.setPrefWidth(72);

        var colVisible = new TableColumn<ProductRow, String>("Visible");
        colVisible.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getVisibleText()));
        colVisible.setPrefWidth(72);

        var colImage = new TableColumn<ProductRow, String>("Image");
        colImage.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getImageShort()));
        colImage.setPrefWidth(160);

        table.getColumns().addAll(colId, colName, colCode, colBrand, colUnit, colCost, colPrice, colStock, colVisible, colImage);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY
                    && e.getClickCount() == 2
                    && table.getSelectionModel().getSelectedItem() != null) {
                openDetails(table.getSelectionModel().getSelectedItem());
            }
        });
        setCenter(table);

        search.textProperty().addListener((a, b, c) -> reload());
        reload();
    }

    private Optional<ProductRow> selectedRow() {
        return Optional.ofNullable(table.getSelectionModel().getSelectedItem());
    }

    private void addStockForRow(ProductRow row) {
        var label = row.getName() + " (" + row.getCode() + ")";
        AddStockDialog.show(label).ifPresent(r -> {
            try {
                inventory.receiveStock(row.getId(), r.quantity(), r.lotCode(), r.expiry());
                reload();
            } catch (Exception ex) {
                var a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Could not add stock");
                a.setContentText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
                a.showAndWait();
            }
        });
    }

    private void openDetails(ProductRow row) {
        var w = new Stage();
        if (getScene() != null && getScene().getWindow() != null) {
            w.initOwner(getScene().getWindow());
        }
        w.setTitle("Product — " + row.getName());
        w.setMinWidth(640);
        w.setMinHeight(420);
        w.setScene(new Scene(new ProductDetailsView(catalog, inventory, row.getId()), 780, 520));
        w.show();
    }

    private void reload() {
        var q = search.getText();
        var rows = catalog.listAllRegistered(q).stream().map(ProductRow::from).toList();
        table.setItems(FXCollections.observableArrayList(rows));
        countLabel.setText(catalog.countAllRegistered(q) + " registered");
    }

    /** Mutable row bean for {@link TableView} binding. */
    public static final class ProductRow {
        private long id;
        private String name;
        private String code;
        private String brand;
        private String unit;
        private String costText;
        private String priceText;
        private String stockText;
        private String visibleText;
        private String imageShort;

        public static ProductRow from(Product p) {
            var r = new ProductRow();
            r.id = p.id();
            r.name = p.name() != null ? p.name() : "";
            r.code = p.code() != null ? p.code() : "";
            r.brand = p.brand() != null && !p.brand().isBlank() ? p.brand() : "—";
            var u = p.unitSummary();
            r.unit = u.isBlank() ? "—" : u;
            r.costText = String.format("Bs %.2f", p.cost());
            r.priceText = String.format("Bs %.2f", p.price());
            r.stockText = formatStock(p.stock());
            r.visibleText = p.visible() ? "Yes" : "No";
            r.imageShort = shorten(p.imageUrl(), 48);
            return r;
        }

        private static String formatStock(double v) {
            if (Math.abs(v - Math.rint(v)) < 1e-9) return Long.toString(Math.round(v));
            return Double.toString(v);
        }

        private static String shorten(String s, int max) {
            if (s == null || s.isBlank()) return "";
            if (s.length() <= max) return s;
            return s.substring(0, max - 1) + "…";
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public String getBrand() {
            return brand;
        }

        public String getUnit() {
            return unit;
        }

        public String getCostText() {
            return costText;
        }

        public String getPriceText() {
            return priceText;
        }

        public String getStockText() {
            return stockText;
        }

        public String getVisibleText() {
            return visibleText;
        }

        public String getImageShort() {
            return imageShort;
        }
    }
}
