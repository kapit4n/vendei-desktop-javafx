package com.vendei.desktop.ui;

import com.vendei.desktop.app.CustomerService;
import com.vendei.desktop.domain.Customer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.Objects;
import java.util.Optional;

/**
 * Pick an existing {@link Customer}, create one, or choose walk-in (anonymous).
 */
final class ClientPickerDialog extends Dialog<ClientPickerDialog.Pick> {

    sealed interface Pick permits Pick.Anonymous, Pick.Selected {
        record Anonymous() implements Pick {}

        record Selected(Customer customer) implements Pick {}
    }

    private final CustomerService customers;
    private final TableView<Customer> table = new TableView<>();
    private final TextField search = new TextField();

    ClientPickerDialog(CustomerService customers) {
        this.customers = Objects.requireNonNull(customers);
        setTitle("Client");
        setHeaderText("Select or create a client");

        var walkIn = new ButtonType("Walk-in", ButtonBar.ButtonData.OTHER);
        var createNew = new ButtonType("New client…", ButtonBar.ButtonData.OTHER);
        var useSelected = new ButtonType("Use selected", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(walkIn, createNew, useSelected, ButtonType.CANCEL);

        var colId = new TableColumn<Customer, Long>("ID");
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().id()));
        colId.setPrefWidth(48);
        var colName = new TableColumn<Customer, String>("Name");
        colName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().name()));
        colName.setPrefWidth(200);
        var colDoc = new TableColumn<Customer, String>("Document");
        colDoc.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(docOrDash(c.getValue().document())));
        colDoc.setPrefWidth(120);
        table.getColumns().addAll(colId, colName, colDoc);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        search.setPromptText("Search name or document…");
        HBox.setHgrow(search, Priority.ALWAYS);
        var refresh = new Button("Refresh");
        refresh.setOnAction(e -> reload());
        var top = new HBox(10, new Label("Search:"), search, refresh);
        top.setPadding(new Insets(0, 0, 8, 0));

        var root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setTop(top);
        root.setCenter(table);
        root.setPrefWidth(520);
        root.setPrefHeight(360);
        getDialogPane().setContent(root);

        search.textProperty().addListener((a, b, c) -> reload());
        reload();

        var btUse = (Button) getDialogPane().lookupButton(useSelected);
        btUse.addEventFilter(ActionEvent.ACTION, e -> {
            if (table.getSelectionModel().getSelectedItem() == null) {
                e.consume();
                warn("Select a client from the list, or use Walk-in / New client.");
            }
        });

        var btWalk = (Button) getDialogPane().lookupButton(walkIn);
        btWalk.addEventFilter(ActionEvent.ACTION, e -> {
            e.consume();
            setResult(new Pick.Anonymous());
            close();
        });

        var btNew = (Button) getDialogPane().lookupButton(createNew);
        btNew.addEventFilter(ActionEvent.ACTION, e -> {
            e.consume();
            CreateClientDialog.open().ifPresent(c -> {
                try {
                    var saved = customers.create(c.name(), c.document());
                    reload();
                    table.getSelectionModel().select(saved);
                    setResult(new Pick.Selected(saved));
                    close();
                } catch (Exception ex) {
                    err("Could not create client", ex);
                }
            });
        });

        setResultConverter(bt -> {
            if (bt == null || bt == ButtonType.CANCEL) {
                return null;
            }
            if (bt == useSelected) {
                var sel = table.getSelectionModel().getSelectedItem();
                return sel == null ? null : new Pick.Selected(sel);
            }
            return null;
        });
    }

    private void reload() {
        table.setItems(FXCollections.observableArrayList(customers.listAll(search.getText())));
    }

    private void warn(String msg) {
        var a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void err(String title, Exception ex) {
        var a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(title);
        a.setContentText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
        a.showAndWait();
    }

    private static String docOrDash(String d) {
        return d == null || d.isBlank() ? "—" : d;
    }

    static Optional<Pick> show(CustomerService customers) {
        return new ClientPickerDialog(customers).showAndWait();
    }
}
