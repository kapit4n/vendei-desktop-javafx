package com.vendei.desktop.ui;

import com.vendei.desktop.app.CatalogService;
import com.vendei.desktop.domain.UnitOfMeasure;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Objects;
import java.util.Optional;

/** Maintain {@code units_of_measure}: codes used by products and POS display labels. */
public final class UnitsManagementView extends BorderPane {
    private final CatalogService catalog;
    private final TableView<UnitOfMeasure> table = new TableView<>();
    private final TextField code = new TextField();
    private final TextField label = new TextField();
    private final Spinner<Integer> sort = new Spinner<>(0, 9999, 100, 10);

    public UnitsManagementView(CatalogService catalog) {
        this.catalog = Objects.requireNonNull(catalog);

        var form = new GridPane();
        form.setPadding(new Insets(12));
        form.setHgap(8);
        form.setVgap(8);
        int r = 0;
        form.add(new Label("Code"), 0, r);
        form.add(code, 1, r);
        GridPane.setHgrow(code, Priority.ALWAYS);
        r++;
        form.add(new Label("Label"), 0, r);
        form.add(label, 1, r);
        GridPane.setHgrow(label, Priority.ALWAYS);
        r++;
        form.add(new Label("Sort"), 0, r);
        form.add(sort, 1, r);
        r++;

        var add = new Button("Add");
        add.setOnAction(e -> addUnit());
        var update = new Button("Update selected");
        update.setOnAction(e -> updateSelected());
        var del = new Button("Delete selected");
        del.setOnAction(e -> deleteSelected());
        var refresh = new Button("Refresh");
        refresh.setOnAction(e -> reload());
        var actions = new HBox(10, add, update, del, refresh);
        actions.setAlignment(Pos.CENTER_LEFT);
        form.add(actions, 0, r, 2, 1);

        var colId = new TableColumn<UnitOfMeasure, Long>("ID");
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().id()));
        colId.setPrefWidth(56);
        var colCode = new TableColumn<UnitOfMeasure, String>("Code");
        colCode.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().code()));
        colCode.setPrefWidth(100);
        var colLabel = new TableColumn<UnitOfMeasure, String>("Label");
        colLabel.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().label()));
        colLabel.setPrefWidth(220);
        var colSort = new TableColumn<UnitOfMeasure, Integer>("Sort");
        colSort.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().sortOrder()));
        colSort.setPrefWidth(64);

        table.getColumns().addAll(colId, colCode, colLabel, colSort);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getSelectionModel().selectedItemProperty().addListener((a, b, sel) -> {
            if (sel != null) {
                code.setText(sel.code());
                label.setText(sel.label());
                sort.getValueFactory().setValue(sel.sortOrder());
            }
        });

        setTop(form);
        setCenter(table);
        reload();
    }

    private void addUnit() {
        try {
            catalog.createUnit(code.getText(), label.getText(), sort.getValue());
            reload();
        } catch (Exception ex) {
            err("Could not add unit", ex);
        }
    }

    private void updateSelected() {
        var sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Select a row to update.");
            return;
        }
        try {
            catalog.updateUnit(sel.id(), code.getText(), label.getText(), sort.getValue());
            reload();
        } catch (Exception ex) {
            err("Could not update unit", ex);
        }
    }

    private void deleteSelected() {
        var sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Select a row to delete.");
            return;
        }
        var confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete unit?");
        confirm.setContentText("Remove \"" + sel.label() + "\" (" + sel.code() + ")? This fails if any product uses it.");
        Optional<ButtonType> ans = confirm.showAndWait();
        if (ans.isEmpty() || ans.get() != ButtonType.OK) return;
        try {
            catalog.deleteUnit(sel.id());
            reload();
        } catch (Exception ex) {
            err("Could not delete unit", ex);
        }
    }

    private void reload() {
        table.setItems(FXCollections.observableArrayList(catalog.listUnits()));
    }

    private static void err(String header, Exception ex) {
        var a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(header);
        a.setContentText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
        a.showAndWait();
    }

    private static void info(String msg) {
        var a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }
}
