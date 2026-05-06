package com.vendei.desktop.ui;

import com.vendei.desktop.app.CustomerService;
import com.vendei.desktop.domain.Customer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Objects;

/** Read-only browser for all customers; optional entry point to create clients. */
public final class ClientsView extends BorderPane {
    private final CustomerService customers;
    private final TableView<Customer> table = new TableView<>();
    private final TextField search = new TextField();
    private final Label countLabel = new Label();

    public ClientsView(CustomerService customers) {
        this.customers = Objects.requireNonNull(customers);

        var top = new HBox(10);
        top.setPadding(new Insets(12));
        top.setAlignment(Pos.CENTER_LEFT);
        search.setPromptText("Filter by name or document…");
        HBox.setHgrow(search, Priority.ALWAYS);
        var refresh = new Button("Refresh");
        refresh.setOnAction(e -> reload());
        var add = new Button("New client…");
        add.setOnAction(e -> CreateClientDialog.open().ifPresent(r -> {
            try {
                customers.create(r.name(), r.document());
                reload();
            } catch (Exception ex) {
                var a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Could not create client");
                a.setContentText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
                a.showAndWait();
            }
        }));
        top.getChildren().addAll(new Label("Search:"), search, refresh, add, countLabel);
        setTop(top);

        var colId = new TableColumn<Customer, Long>("ID");
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().id()));
        colId.setPrefWidth(56);
        var colName = new TableColumn<Customer, String>("Name");
        colName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().name()));
        colName.setPrefWidth(260);
        var colDoc = new TableColumn<Customer, String>("Document");
        colDoc.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(nullToEmpty(c.getValue().document())));
        colDoc.setPrefWidth(160);

        table.getColumns().addAll(colId, colName, colDoc);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setCenter(table);

        search.textProperty().addListener((a, b, c) -> reload());
        reload();
    }

    private void reload() {
        var q = search.getText();
        table.setItems(FXCollections.observableArrayList(customers.listAll(q)));
        countLabel.setText(customers.countAll(q) + " clients");
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
