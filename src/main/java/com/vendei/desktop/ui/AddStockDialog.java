package com.vendei.desktop.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.Optional;

final class AddStockDialog extends Dialog<AddStockDialog.Result> {

    record Result(double quantity, String lotCode, LocalDate expiry) {}

    AddStockDialog(String productLabel) {
        setTitle("Add stock");
        setHeaderText(productLabel);

        var qty = new TextField();
        qty.setPromptText("e.g. 12");
        var lot = new TextField();
        lot.setPromptText("Lot / batch code (required)");
        var expiry = new DatePicker();
        expiry.setPromptText("Optional");

        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Quantity"), qty);
        grid.addRow(1, new Label("Lot / batch"), lot);
        grid.addRow(2, new Label("Expiry"), expiry);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        var btOk = (Button) getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, e -> {
            var err = validate(qty, lot);
            if (err != null) {
                e.consume();
                var a = new Alert(Alert.AlertType.WARNING);
                a.setHeaderText("Check input");
                a.setContentText(err);
                a.showAndWait();
            }
        });

        setResultConverter(button -> {
            if (button == null || button.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                return null;
            }
            if (validate(qty, lot) != null) {
                return null;
            }
            double q = Double.parseDouble(qty.getText().trim().replace(",", "."));
            var lotCode = lot.getText().trim();
            LocalDate exp = expiry.getValue();
            return new Result(q, lotCode, exp);
        });
    }

    private static String validate(TextField qty, TextField lot) {
        try {
            var q = Double.parseDouble(qty.getText().trim().replace(",", "."));
            if (q <= 0) return "Quantity must be greater than zero.";
        } catch (NumberFormatException e) {
            return "Enter a valid quantity.";
        }
        if (lot.getText() == null || lot.getText().isBlank()) {
            return "Lot / batch code is required.";
        }
        return null;
    }

    static Optional<Result> show(String productLabel) {
        var d = new AddStockDialog(productLabel);
        return d.showAndWait();
    }
}
