package com.vendei.desktop.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Optional;

final class CreateClientDialog extends Dialog<CreateClientDialog.Result> {

    record Result(String name, String document) {}

    CreateClientDialog() {
        setTitle("New client");
        setHeaderText("Register a client");

        var name = new TextField();
        name.setPromptText("Full name (required)");
        var document = new TextField();
        document.setPromptText("Document / CI (optional)");

        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Name"), name);
        grid.addRow(1, new Label("Document"), document);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        var btOk = (Button) getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, e -> {
            if (name.getText() == null || name.getText().isBlank()) {
                e.consume();
                var a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Name is required.");
                a.showAndWait();
            }
        });

        setResultConverter(bt -> {
            if (bt == null || bt.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                return null;
            }
            return new Result(name.getText().trim(), document.getText().trim());
        });
    }

    static Optional<Result> open() {
        return new CreateClientDialog().showAndWait();
    }
}
