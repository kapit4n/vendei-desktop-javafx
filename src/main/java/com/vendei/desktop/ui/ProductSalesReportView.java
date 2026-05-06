package com.vendei.desktop.ui;

import com.vendei.desktop.app.ProductSalesReport;
import com.vendei.desktop.app.SalesReportPeriod;
import com.vendei.desktop.app.SalesService;
import com.vendei.desktop.domain.ProductSalesRow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.ZoneId;
import java.util.Objects;

public final class ProductSalesReportView extends BorderPane {
    private final SalesService sales;
    private final ZoneId zone = ZoneId.systemDefault();

    private final ComboBox<SalesReportPeriod> period = new ComboBox<>();
    private final Label headline = new Label();
    private final Label summary = new Label();
    private final TableView<ProductSalesRow> table = new TableView<>();

    public ProductSalesReportView(SalesService sales) {
        this.sales = Objects.requireNonNull(sales);

        period.getItems().addAll(SalesReportPeriod.values());
        period.setValue(SalesReportPeriod.TODAY);
        period.setPrefWidth(160);

        var refresh = new Button("Refresh");
        refresh.setOnAction(e -> reload());

        var top = new HBox(12);
        top.setPadding(new Insets(12));
        top.setAlignment(Pos.CENTER_LEFT);
        var periodLabel = new Label("Period:");
        periodLabel.setStyle("-fx-text-fill: #6b7280;");
        top.getChildren().addAll(periodLabel, period, refresh);

        headline.setStyle("-fx-font-size: 16; -fx-font-weight: 800;");
        summary.setStyle("-fx-text-fill: #374151;");
        summary.setWrapText(true);

        var headBox = new VBox(6, headline, summary);
        headBox.setPadding(new Insets(0, 12, 12, 12));

        var colName = new TableColumn<ProductSalesRow, String>("Product");
        colName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().productName()));
        colName.setPrefWidth(260);

        var colQty = new TableColumn<ProductSalesRow, String>("Qty sold");
        colQty.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(formatQty(c.getValue().quantitySold())));
        colQty.setPrefWidth(88);

        var colRev = new TableColumn<ProductSalesRow, String>("Revenue");
        colRev.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(String.format("Bs %.2f", c.getValue().revenue())));
        colRev.setPrefWidth(120);

        table.getColumns().addAll(colName, colQty, colRev);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        var root = new VBox(0, top, headBox);
        VBox.setVgrow(table, Priority.ALWAYS);
        setTop(root);
        setCenter(table);

        period.setOnAction(e -> reload());
        reload();
    }

    private void reload() {
        SalesReportPeriod p = period.getValue();
        if (p == null) return;
        ProductSalesReport r = sales.productSales(p, zone);
        headline.setText("Product sales — " + p.shortLabel());
        summary.setText(String.format(
                "%s · %d orders · Total revenue Bs %.2f · %d product lines",
                r.rangeDescription(),
                r.orderCount(),
                r.totalRevenue(),
                r.rows().size()
        ));
        table.setItems(FXCollections.observableArrayList(r.rows()));
    }

    private static String formatQty(double v) {
        if (Math.abs(v - Math.rint(v)) < 1e-9) return Long.toString(Math.round(v));
        return Double.toString(v);
    }
}
