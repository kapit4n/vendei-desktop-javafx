package com.vendei.desktop;

import com.vendei.desktop.app.AppWiring;
import com.vendei.desktop.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Main extends Application {
    private AppWiring wiring;

    @Override
    public void start(Stage stage) throws Exception {
        wiring = AppWiring.buildDefault();
        var root = new MainView(wiring.catalogService, wiring.ticketService);
        var scene = new Scene(root, 1100, 700);
        stage.setTitle("Vendei POS (JavaFX)");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (wiring != null) wiring.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

