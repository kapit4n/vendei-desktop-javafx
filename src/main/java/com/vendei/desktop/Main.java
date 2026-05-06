package com.vendei.desktop;

import com.vendei.desktop.app.AppWiring;
import com.vendei.desktop.ui.MainView;
import com.vendei.desktop.ui.ClientsView;
import com.vendei.desktop.ui.RegisteredProductsView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public final class Main extends Application {
    private AppWiring wiring;

    @Override
    public void start(Stage stage) throws Exception {
        wiring = AppWiring.buildDefault();
        var pos = new MainView(wiring.catalogService, wiring.customerService, wiring.ticketService);

        var catalogMenu = new Menu("Catalog");
        var allRegistered = new MenuItem("All registered products…");
        allRegistered.setOnAction(e -> openRegisteredProductsWindow());
        catalogMenu.getItems().add(allRegistered);

        var clientsMenu = new Menu("Clients");
        var allClients = new MenuItem("All clients…");
        allClients.setOnAction(e -> openClientsWindow());
        clientsMenu.getItems().add(allClients);

        var menuBar = new MenuBar(catalogMenu, clientsMenu);

        var shell = new BorderPane();
        shell.setTop(menuBar);
        shell.setCenter(pos);

        var scene = new Scene(shell, 1100, 700);
        stage.setTitle("Vendei POS (JavaFX)");
        stage.setScene(scene);
        stage.show();
    }

    private void openRegisteredProductsWindow() {
        var w = new Stage();
        w.setTitle("Registered products");
        w.setMinWidth(720);
        w.setMinHeight(480);
        w.setScene(new Scene(new RegisteredProductsView(wiring.catalogService, wiring.inventoryService), 960, 560));
        w.show();
    }

    private void openClientsWindow() {
        var w = new Stage();
        w.setTitle("Clients");
        w.setMinWidth(560);
        w.setMinHeight(400);
        w.setScene(new Scene(new ClientsView(wiring.customerService), 720, 480));
        w.show();
    }

    @Override
    public void stop() throws Exception {
        if (wiring != null) wiring.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

