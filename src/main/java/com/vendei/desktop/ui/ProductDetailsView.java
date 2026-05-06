package com.vendei.desktop.ui;

import com.vendei.desktop.app.CatalogService;
import com.vendei.desktop.app.InventoryService;
import com.vendei.desktop.domain.InventoryLot;
import com.vendei.desktop.domain.Product;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Product summary plus all {@link InventoryLot} rows (stock receipts) for that SKU.
 */
public final class ProductDetailsView extends BorderPane {
    private final CatalogService catalog;
    private final InventoryService inventory;
    private final long productId;

    private final Label title = new Label();
    private final Label meta = new Label();
    private final TableView<InventoryLot> lotsTable = new TableView<>();

    public ProductDetailsView(CatalogService catalog, InventoryService inventory, long productId) {
        this.catalog = Objects.requireNonNull(catalog);
        this.inventory = Objects.requireNonNull(inventory);
        this.productId = productId;

        var top = new VBox(8);
        top.setPadding(new Insets(12));
        title.setStyle("-fx-font-size: 16; -fx-font-weight: 800;");
        meta.setStyle("-fx-text-fill: #374151;");
        meta.setWrapText(true);

        var toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        var refresh = new Button("Refresh");
        refresh.setOnAction(e -> reload());
        var add = new Button("Add stock…");
        add.setOnAction(e -> AddStockDialog.show(title.getText()).ifPresent(r -> {
            try {
                inventory.receiveStock(productId, r.quantity(), r.lotCode(), r.expiry());
                reload();
            } catch (Exception ex) {
                var a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Could not add stock");
                a.setContentText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
                a.showAndWait();
            }
        }));
        toolbar.getChildren().addAll(refresh, add);

        top.getChildren().addAll(title, meta, toolbar);
        setTop(top);

        var colId = new TableColumn<InventoryLot, Long>("Lot #");
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().id()));
        colId.setPrefWidth(64);

        var colQty = new TableColumn<InventoryLot, String>("Qty");
        colQty.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(formatQty(c.getValue().quantity())));
        colQty.setPrefWidth(72);

        var colBatch = new TableColumn<InventoryLot, String>("Batch / lot");
        colBatch.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(nullToEmpty(c.getValue().batchCode())));
        colBatch.setPrefWidth(140);

        var colExp = new TableColumn<InventoryLot, String>("Expiry");
        colExp.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(nullToEmpty(c.getValue().expiryDate())));
        colExp.setPrefWidth(100);

        var colRecv = new TableColumn<InventoryLot, String>("Received");
        colRecv.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(nullToEmpty(c.getValue().receivedAt())));
        colRecv.setPrefWidth(160);

        lotsTable.getColumns().addAll(colId, colQty, colBatch, colExp, colRecv);
        lotsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setCenter(lotsTable);

        reload();
    }

    private void reload() {
        Product p = catalog.findById(productId).orElse(null);
        if (p == null) {
            title.setText("Product #" + productId);
            meta.setText("Product not found.");
            lotsTable.setItems(FXCollections.observableArrayList());
            return;
        }
        title.setText(p.name());
        meta.setText(String.format(
                "Code: %s  ·  Price: Bs %.2f  ·  Current stock: %s  ·  Visible: %s",
                p.code() != null ? p.code() : "—",
                p.price(),
                formatQty(p.stock()),
                p.visible() ? "yes" : "no"
        ));
        lotsTable.setItems(FXCollections.observableArrayList(inventory.listLots(productId)));
    }

    private static String nullToEmpty(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }

    private static String formatQty(double v) {
        if (Math.abs(v - Math.rint(v)) < 1e-9) return Long.toString(Math.round(v));
        return Double.toString(v);
    }
}
