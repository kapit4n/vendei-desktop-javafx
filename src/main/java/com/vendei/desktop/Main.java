package com.vendei.desktop;

import com.vendei.desktop.app.AppWiring;
import com.vendei.desktop.ui.MainView;
import com.vendei.desktop.ui.ClientsView;
import com.vendei.desktop.ui.ProductSalesReportView;
import com.vendei.desktop.ui.RegisterProductView;
import com.vendei.desktop.ui.RegisteredProductsView;
import com.vendei.desktop.ui.UnitsManagementView;
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
        var pos = new MainView(
                wiring.catalogService,
                wiring.customerService,
                wiring.salesService,
                wiring.ticketService
        );

        var catalogMenu = new Menu("Catalog");
        var allRegistered = new MenuItem("All registered products…");
        allRegistered.setOnAction(e -> openRegisteredProductsWindow());
        var registerProduct = new MenuItem("Register product…");
        registerProduct.setOnAction(e -> openRegisterProductWindow(null));
        var units = new MenuItem("Units of measure…");
        units.setOnAction(e -> openUnitsWindow());
        catalogMenu.getItems().addAll(allRegistered, registerProduct, units);

        var clientsMenu = new Menu("Clients");
        var allClients = new MenuItem("All clients…");
        allClients.setOnAction(e -> openClientsWindow());
        clientsMenu.getItems().add(allClients);

        var reportsMenu = new Menu("Reports");
        var productSales = new MenuItem("Product sales…");
        productSales.setOnAction(e -> openProductSalesReportWindow());
        reportsMenu.getItems().add(productSales);

        var menuBar = new MenuBar(catalogMenu, clientsMenu, reportsMenu);

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
        w.setScene(new Scene(new RegisteredProductsView(wiring.catalogService, wiring.inventoryService, this::openRegisterProductWindow, this::openUnitsWindow), 960, 560));
        w.show();
    }

    private void openRegisterProductWindow(Runnable afterSave) {
        var w = new Stage();
        w.setTitle("Register product");
        w.setMinWidth(440);
        w.setMinHeight(420);
        w.setScene(new Scene(new RegisterProductView(wiring.catalogService, afterSave), 520, 460));
        w.show();
    }

    private void openUnitsWindow() {
        var w = new Stage();
        w.setTitle("Units of measure");
        w.setMinWidth(520);
        w.setMinHeight(400);
        w.setScene(new Scene(new UnitsManagementView(wiring.catalogService), 640, 480));
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

    private void openProductSalesReportWindow() {
        var w = new Stage();
        w.setTitle("Product sales report");
        w.setMinWidth(560);
        w.setMinHeight(400);
        w.setScene(new Scene(new ProductSalesReportView(wiring.salesService), 720, 520));
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

