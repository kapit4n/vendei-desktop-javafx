package com.vendei.desktop.ui;

import com.vendei.desktop.app.CatalogService;
import com.vendei.desktop.domain.Product;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class MainView extends BorderPane {
    private final CatalogService catalog;
    private final ListView<Product> list = new ListView<>();
    private final TextField search = new TextField();

    public MainView(CatalogService catalog) {
        this.catalog = catalog;

        var top = new HBox(10);
        top.setPadding(new Insets(12));
        search.setPromptText("Search products…");
        HBox.setHgrow(search, Priority.ALWAYS);
        top.getChildren().addAll(new Label("Products"), search);
        setTop(top);

        list.setCellFactory(ignored -> new ProductCell());
        setCenter(list);

        reload();
        search.textProperty().addListener((ignored, ignoredOld, ignoredNew) -> reload());
    }

    private void reload() {
        var items = FXCollections.observableArrayList(catalog.listProducts(search.getText()));
        list.setItems(items);
    }

    private static final class ProductCell extends ListCell<Product> {
        @Override
        protected void updateItem(Product item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            var root = new HBox(12);
            root.setPadding(new Insets(10));
            root.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-border-color: #e5e7eb; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10;"
            );

            var left = new VBox(4);
            var title = new Label(item.name());
            title.setStyle("-fx-font-weight: 700;");
            var subtitle = new Label(String.format("Bs %.2f  ·  Stock: %s", item.price(), trim(item.stock())));
            subtitle.setStyle("-fx-text-fill: #374151;");
            left.getChildren().addAll(title, subtitle);

            root.getChildren().add(left);
            setGraphic(root);
        }

        private static String trim(double v) {
            if (Math.abs(v - Math.rint(v)) < 1e-9) return Long.toString(Math.round(v));
            return Double.toString(v);
        }
    }
}

