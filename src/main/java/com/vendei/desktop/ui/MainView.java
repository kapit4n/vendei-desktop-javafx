package com.vendei.desktop.ui;

import com.vendei.desktop.app.CatalogService;
import com.vendei.desktop.app.CustomerService;
import com.vendei.desktop.app.TicketService;
import com.vendei.desktop.domain.PaymentMethod;
import com.vendei.desktop.domain.Product;
import com.vendei.desktop.domain.TicketLine;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;

public final class MainView extends BorderPane {
    private final CatalogService catalog;
    private final CustomerService customers;
    private final TicketService ticket;
    private final TilePane productGrid = new TilePane(12, 12);
    private final ScrollPane productScroll = new ScrollPane(productGrid);
    private final ListView<TicketLine> ticketLines = new ListView<>();
    private final TextField search = new TextField();
    private final Label productListCount = new Label();

    public MainView(CatalogService catalog, CustomerService customers, TicketService ticket) {
        this.catalog = catalog;
        this.customers = customers;
        this.ticket = ticket;

        setBackground(new Background(new BackgroundFill(Color.web("#f6f7fb"), CornerRadii.EMPTY, Insets.EMPTY)));

        var root = new HBox(16, buildTicketPane(), buildCatalogPane());
        root.setPadding(new Insets(14));
        HBox.setHgrow(root.getChildren().get(1), Priority.ALWAYS);
        setCenter(root);

        reload();
        search.textProperty().addListener((ignored, ignoredOld, ignoredNew) -> reload());
        search.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) search.clear();
        });
    }

    private void reload() {
        var q = search.getText();
        var items = FXCollections.observableArrayList(catalog.listProducts(q));
        int shown = items.size();
        int totalMatching = catalog.countProducts(q);
        productListCount.setText(String.format("Showing %d of %d products", shown, totalMatching));
        renderProducts(items);
    }

    private Node buildCatalogPane() {
        var wrap = new VBox(12);
        wrap.setPadding(new Insets(12));
        wrap.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(14), Insets.EMPTY)));

        var top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        var home = new Button("⌂");
        home.setFocusTraversable(false);
        home.setMinWidth(38);
        home.setStyle("-fx-background-radius: 10; -fx-background-color: #f3f4f6;");

        search.setPromptText("Search products by name or scan barcode…");
        HBox.setHgrow(search, Priority.ALWAYS);
        search.setStyle("-fx-background-radius: 12; -fx-background-color: #f3f4f6; -fx-border-color: transparent;");

        var quickAdd = new Button("Quick add by code");
        quickAdd.setFocusTraversable(false);
        quickAdd.setStyle("-fx-background-radius: 12; -fx-background-color: #eef2ff; -fx-text-fill: #1d4ed8;");
        quickAdd.setOnAction(e -> quickAddByCode());

        top.getChildren().addAll(home, search, quickAdd);

        var cats = new HBox(8);
        cats.setAlignment(Pos.CENTER_LEFT);
        cats.getChildren().addAll(
                pill("All", true),
                pill("Electronics", false),
                pill("Grocery", false),
                pill("Apparel", false),
                pill("Home", false),
                pill("Sports", false)
        );

        var headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        var catsLabel = new Label("CATEGORIES");
        catsLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: 700;");
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var manage = new Label("Manage");
        manage.setStyle("-fx-text-fill: #6b7280;");
        headerRow.getChildren().addAll(catsLabel, spacer, manage);

        productGrid.setPrefColumns(4);
        productGrid.setTileAlignment(Pos.TOP_LEFT);
        productGrid.setPadding(new Insets(6));

        productScroll.setFitToWidth(true);
        productScroll.setStyle("-fx-background-color:transparent;");
        VBox.setVgrow(productScroll, Priority.ALWAYS);

        var listFrame = new VBox(10);
        listFrame.setPadding(new Insets(12));
        listFrame.setStyle(
                "-fx-background-color: #fafafa; "
                        + "-fx-background-radius: 12; "
                        + "-fx-border-color: #e5e7eb; "
                        + "-fx-border-radius: 12; "
                        + "-fx-border-width: 1;"
        );

        var listTitleRow = new HBox(10);
        listTitleRow.setAlignment(Pos.CENTER_LEFT);
        var listTitle = new Label("PRODUCT LIST");
        listTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #111827;");
        var listTitleSpacer = new Region();
        HBox.setHgrow(listTitleSpacer, Priority.ALWAYS);
        var gridView = new Label("Grid ▾");
        gridView.setStyle("-fx-text-fill: #6b7280;");
        listTitleRow.getChildren().addAll(listTitle, listTitleSpacer, gridView);

        productListCount.setStyle("-fx-text-fill: #6b7280;");
        listFrame.getChildren().addAll(listTitleRow, productListCount, productScroll);
        VBox.setVgrow(productScroll, Priority.ALWAYS);

        wrap.getChildren().addAll(top, headerRow, cats, listFrame);
        VBox.setVgrow(listFrame, Priority.ALWAYS);
        return wrap;
    }

    private Node buildTicketPane() {
        var wrap = new VBox(12);
        wrap.setPrefWidth(360);
        wrap.setMinWidth(320);
        wrap.setPadding(new Insets(12));
        wrap.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(14), Insets.EMPTY)));

        var header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        var title = new Label("CURRENT TICKET");
        title.setStyle("-fx-font-weight: 800;");
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var clear = new Button("Clear");
        clear.setFocusTraversable(false);
        clear.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb;");
        clear.setOnAction(e -> ticket.clear());
        header.getChildren().addAll(title, spacer, clear);

        ticketLines.setItems(ticket.lines());
        ticketLines.setCellFactory(ignored -> new TicketLineCell(ticket));
        ticketLines.setPlaceholder(emptyTicketPlaceholder());
        VBox.setVgrow(ticketLines, Priority.ALWAYS);

        var clientBox = section("CLIENT");
        var clientName = new Label();
        clientName.textProperty().bind(ticket.customerNameProperty());
        clientName.setStyle("-fx-font-weight: 700;");
        var clientCi = new Label();
        clientCi.textProperty().bind(ticket.customerCiProperty());
        clientCi.setStyle("-fx-text-fill: #6b7280;");
        var selectClient = new Button("Select or create client");
        selectClient.setMaxWidth(Double.MAX_VALUE);
        selectClient.setStyle("-fx-background-radius: 10; -fx-background-color: #f3f4f6;");
        selectClient.setOnAction(e -> ClientPickerDialog.show(customers).ifPresent(pick -> {
            switch (pick) {
                case ClientPickerDialog.Pick.Anonymous ignored -> ticket.clearCustomer();
                case ClientPickerDialog.Pick.Selected s -> ticket.setCustomer(s.customer());
            }
        }));
        clientBox.getChildren().addAll(clientName, clientCi, selectClient);

        var paymentBox = section("PAYMENT");
        var tg = new ToggleGroup();
        var cash = new ToggleButton("Cash");
        var qr = new ToggleButton("QR");
        cash.setToggleGroup(tg);
        qr.setToggleGroup(tg);
        cash.setSelected(true);
        styleToggle(cash);
        styleToggle(qr);
        tg.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            if (newV == cash) ticket.paymentMethodProperty().set(PaymentMethod.CASH);
            if (newV == qr) ticket.paymentMethodProperty().set(PaymentMethod.QR);
        });
        ticket.paymentMethodProperty().addListener((obs, oldV, newV) -> {
            if (newV == PaymentMethod.CASH) cash.setSelected(true);
            if (newV == PaymentMethod.QR) qr.setSelected(true);
        });

        var methodRow = new HBox(10, cash, qr);

        var amountLabel = new Label("Amount (Bs)");
        amountLabel.setStyle("-fx-text-fill: #6b7280;");
        var amount = new TextField();
        amount.setPromptText("0.00");
        amount.textProperty().addListener((obs, o, n) -> {
            ticket.amountReceivedProperty().set(parseMoney(n));
        });

        var quickRow = new HBox(8,
                quickAmount(5, amount),
                quickAmount(10, amount),
                quickAmount(20, amount),
                quickAmount(50, amount),
                quickAmount(100, amount),
                quickAmount(200, amount)
        );

        var pay = new Button("Pay");
        pay.setMaxWidth(Double.MAX_VALUE);
        pay.setStyle("-fx-background-radius: 12; -fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: 700;");
        pay.disableProperty().bind(Bindings.isEmpty(ticket.lines()));
        pay.setOnAction(e -> {
            var total = ticket.subtotalProperty().get();
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Paid");
            alert.setContentText(String.format("Total: Bs %.2f (%s)", total, ticket.paymentMethodProperty().get()));
            alert.showAndWait();
            ticket.resetForNextSale();
        });

        var totalRow = new HBox(8);
        totalRow.setAlignment(Pos.CENTER_LEFT);
        var totalLabel = new Label("Total");
        totalLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: 700;");
        var totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);
        var totalValue = new Label();
        totalValue.textProperty().bind(ticket.subtotalProperty().asString("Bs %.2f"));
        totalValue.setStyle("-fx-font-weight: 800;");
        totalRow.getChildren().addAll(totalLabel, totalSpacer, totalValue);

        paymentBox.getChildren().addAll(methodRow, amountLabel, amount, pay, quickRow, totalRow);

        wrap.getChildren().addAll(header, ticketLines, clientBox, paymentBox);
        VBox.setVgrow(ticketLines, Priority.ALWAYS);
        return wrap;
    }

    private static VBox section(String title) {
        var box = new VBox(8);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 12; -fx-border-color: #eef2f7; -fx-border-radius: 12;");
        var label = new Label(title);
        label.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: 800; -fx-font-size: 11;");
        box.getChildren().add(label);
        return box;
    }

    private static Button pill(String text, boolean active) {
        var b = new Button(text);
        b.setFocusTraversable(false);
        if (active) {
            b.setStyle("-fx-background-radius: 999; -fx-background-color: #2563eb; -fx-text-fill: white;");
        } else {
            b.setStyle("-fx-background-radius: 999; -fx-background-color: #f3f4f6; -fx-text-fill: #111827;");
        }
        return b;
    }

    private static void styleToggle(ToggleButton b) {
        b.setFocusTraversable(false);
        b.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(b, Priority.ALWAYS);
        b.setStyle("-fx-background-radius: 10; -fx-background-color: #f3f4f6;");
    }

    private Button quickAmount(int v, TextField amount) {
        var b = new Button(v + " Bs");
        b.setFocusTraversable(false);
        b.setStyle("-fx-background-radius: 10; -fx-background-color: #f3f4f6;");
        b.setOnAction(e -> amount.setText(Integer.toString(v)));
        return b;
    }

    private static Node emptyTicketPlaceholder() {
        var box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30, 10, 30, 10));
        var icon = new Label("🛒");
        icon.setStyle("-fx-font-size: 42; -fx-text-fill: #9ca3af;");
        var title = new Label("No items yet");
        title.setStyle("-fx-font-weight: 800; -fx-text-fill: #6b7280;");
        var subtitle = new Label("Scan or search for a product to add to the ticket");
        subtitle.setWrapText(true);
        subtitle.setStyle("-fx-text-fill: #9ca3af;");
        box.getChildren().addAll(icon, title, subtitle);
        return box;
    }

    private void renderProducts(Iterable<Product> products) {
        productGrid.getChildren().clear();
        for (var p : products) {
            productGrid.getChildren().add(productCard(p));
        }
    }

    private Node productCard(Product p) {
        var card = new VBox(6);
        card.setPrefWidth(170);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 12; -fx-background-radius: 12;");

        var img = new StackPane();
        img.setPrefHeight(92);
        img.setMaxHeight(92);
        img.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10;");
        var cardImg = ProductImages.loadForCard(p.imageUrl(), 154, 92);
        if (cardImg != null) {
            var iv = new ImageView(cardImg);
            iv.setSmooth(true);
            iv.setPreserveRatio(true);
            StackPane.setAlignment(iv, Pos.CENTER);
            img.getChildren().add(iv);
        }

        var price = new Label(String.format("Bs %.2f", p.price()));
        price.setStyle("-fx-font-weight: 800;");
        var name = new Label(p.name());
        name.setStyle("-fx-text-fill: #374151;");
        name.setWrapText(true);

        card.getChildren().addAll(img, price, name);
        card.setOnMouseClicked(e -> ticket.addProduct(p));
        return card;
    }

    private void quickAddByCode() {
        var code = search.getText();
        if (code == null || code.isBlank()) return;
        for (var p : catalog.listProducts(code)) {
            if (p.code() != null && p.code().equalsIgnoreCase(code.trim())) {
                ticket.addProduct(p);
                return;
            }
        }
    }

    private static double parseMoney(String s) {
        if (s == null) return 0.0;
        var t = s.trim().replace(",", ".");
        if (t.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static final class TicketLineCell extends ListCell<TicketLine> {
        private final TicketService ticket;

        private TicketLineCell(TicketService ticket) {
            this.ticket = ticket;
        }

        @Override
        protected void updateItem(TicketLine item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            var root = new HBox(10);
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(8));
            root.setStyle("-fx-background-color: white; -fx-border-color: #eef2f7; -fx-border-radius: 10; -fx-background-radius: 10;");

            var left = new VBox(2);
            var name = new Label(item.name());
            name.setStyle("-fx-font-weight: 800;");
            var meta = new Label(String.format("Bs %.2f  ×  %s", item.unitPrice(), trim(item.quantity())));
            meta.setStyle("-fx-text-fill: #6b7280;");
            left.getChildren().addAll(name, meta);

            var spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            var minus = new Button("−");
            minus.setFocusTraversable(false);
            minus.setMinWidth(28);
            minus.setStyle("-fx-background-radius: 8; -fx-background-color: #f3f4f6;");
            minus.setOnAction(e -> ticket.setQuantity(item.productId(), item.quantity() - 1));

            var qty = new Label(trim(item.quantity()));
            qty.setMinWidth(28);
            qty.setAlignment(Pos.CENTER);

            var plus = new Button("+");
            plus.setFocusTraversable(false);
            plus.setMinWidth(28);
            plus.setStyle("-fx-background-radius: 8; -fx-background-color: #f3f4f6;");
            plus.setOnAction(e -> ticket.setQuantity(item.productId(), item.quantity() + 1));

            var total = new Label(String.format("Bs %.2f", item.lineTotal()));
            total.setStyle("-fx-font-weight: 800;");

            root.getChildren().addAll(left, spacer, minus, qty, plus, total);
            setGraphic(root);
        }

        private static String trim(double v) {
            if (Math.abs(v - Math.rint(v)) < 1e-9) return Long.toString(Math.round(v));
            return Double.toString(v);
        }
    }
}

