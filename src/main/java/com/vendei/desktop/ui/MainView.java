package com.vendei.desktop.ui;

import com.vendei.desktop.app.CatalogService;
import com.vendei.desktop.app.CustomerService;
import com.vendei.desktop.app.SalesService;
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
import javafx.scene.control.ButtonType;
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

import java.util.Locale;
import java.util.StringJoiner;

public final class MainView extends BorderPane {
    private final CatalogService catalog;
    private final CustomerService customers;
    private final SalesService sales;
    private final TicketService ticket;
    private final TilePane productGrid = new TilePane(12, 12);
    private final ScrollPane productScroll = new ScrollPane(productGrid);
    private final ListView<TicketLine> ticketLines = new ListView<>();
    private final TextField search = new TextField();
    private final Label productListCount = new Label();

    public MainView(CatalogService catalog, CustomerService customers, SalesService sales, TicketService ticket) {
        this.catalog = catalog;
        this.customers = customers;
        this.sales = sales;
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

        var dueCard = new VBox(4);
        dueCard.setPadding(new Insets(10, 10, 12, 10));
        dueCard.setStyle(
                "-fx-background-color: #ffffff; -fx-background-radius: 10; "
                        + "-fx-border-color: #e5e7eb; -fx-border-radius: 10;"
        );
        var dueTitle = new Label("Amount due");
        dueTitle.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11; -fx-font-weight: 700;");
        var dueValue = new Label();
        dueValue.textProperty().bind(ticket.subtotalProperty().asString("Bs %.2f"));
        dueValue.setStyle("-fx-font-size: 26; -fx-font-weight: 800; -fx-text-fill: #111827;");
        dueCard.getChildren().addAll(dueTitle, dueValue);

        var tg = new ToggleGroup();
        var cash = new ToggleButton("Cash");
        var qr = new ToggleButton("QR / digital");
        cash.setToggleGroup(tg);
        qr.setToggleGroup(tg);
        cash.setSelected(true);
        configurePayMethodToggle(cash);
        configurePayMethodToggle(qr);
        Runnable refreshPayToggleStyles = () -> {
            applyPayToggleStyle(cash, cash.isSelected());
            applyPayToggleStyle(qr, qr.isSelected());
        };
        cash.selectedProperty().addListener((o, a, s) -> refreshPayToggleStyles.run());
        qr.selectedProperty().addListener((o, a, s) -> refreshPayToggleStyles.run());
        refreshPayToggleStyles.run();

        tg.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            if (newV == cash) ticket.paymentMethodProperty().set(PaymentMethod.CASH);
            if (newV == qr) ticket.paymentMethodProperty().set(PaymentMethod.QR);
        });
        ticket.paymentMethodProperty().addListener((obs, oldV, newV) -> {
            if (newV == PaymentMethod.CASH) cash.setSelected(true);
            if (newV == PaymentMethod.QR) qr.setSelected(true);
        });

        var methodLabel = new Label("How is the customer paying?");
        methodLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11; -fx-font-weight: 700;");
        var methodRow = new HBox(8, cash, qr);
        methodRow.setAlignment(Pos.CENTER_LEFT);

        var amount = new TextField();
        amount.setPromptText("0.00");
        amount.textProperty().addListener((obs, o, n) -> ticket.amountReceivedProperty().set(parseMoney(n)));

        var tenderLabel = new Label("Customer pays (cash)");
        tenderLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: 700;");
        var tenderHint = new Label("Enter cash received, tap a quick amount, or “Exact total”.");
        tenderHint.setWrapText(true);
        tenderHint.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");

        var exact = new Button("Exact total");
        exact.setMaxWidth(Double.MAX_VALUE);
        exact.setFocusTraversable(false);
        exact.setStyle("-fx-background-radius: 10; -fx-background-color: #e0e7ff; -fx-text-fill: #1e3a8a; -fx-font-weight: 600;");
        exact.setOnAction(e -> amount.setText(String.format(Locale.US, "%.2f", ticket.subtotalProperty().get())));

        var quickTile = new TilePane(8, 8);
        quickTile.setPrefColumns(3);
        for (var v : new int[] {5, 10, 20, 50, 100, 200}) {
            quickTile.getChildren().add(quickAmount(v, amount));
        }

        var cashBlock = new VBox(8, tenderLabel, amount, exact, tenderHint, quickTile);

        var qrHint = new Label(
                "Customer pays the full amount due using a QR code "
                        + "(wallet or banking app). No cash tender needed."
        );
        qrHint.setWrapText(true);
        qrHint.setStyle("-fx-text-fill: #374151; -fx-font-size: 12;");
        var qrBlock = new VBox(8, qrHint);

        Runnable syncPayBlocks = () -> {
            boolean isCash = ticket.paymentMethodProperty().get() == PaymentMethod.CASH;
            cashBlock.setVisible(isCash);
            cashBlock.setManaged(isCash);
            qrBlock.setVisible(!isCash);
            qrBlock.setManaged(!isCash);
        };
        ticket.paymentMethodProperty().addListener((o, a, b) -> syncPayBlocks.run());
        ticket.paymentMethodProperty().addListener((o, oldM, newM) -> {
            if (newM == PaymentMethod.QR) {
                amount.clear();
                ticket.amountReceivedProperty().set(0.0);
            }
        });
        syncPayBlocks.run();

        var status = new Label();
        status.setWrapText(true);
        status.textProperty().bind(Bindings.createStringBinding(() -> {
            double total = ticket.subtotalProperty().get();
            double tender = ticket.amountReceivedProperty().get();
            if (ticket.paymentMethodProperty().get() == PaymentMethod.QR) {
                return total <= 1e-9
                        ? "Add items to the ticket first."
                        : String.format("Ready: customer pays Bs %.2f by QR.", total);
            }
            if (total <= 1e-9) return "Add items to see the amount due.";
            if (tender <= 1e-9) {
                return String.format("Still due: Bs %.2f — enter cash received.", total);
            }
            if (tender + 1e-6 < total) {
                return String.format("Still short: Bs %.2f more needed.", total - tender);
            }
            if (Math.abs(tender - total) < 1e-6) {
                return "Exact cash — you can complete the sale.";
            }
            return String.format("Change to give back: Bs %.2f", tender - total);
        },
                ticket.subtotalProperty(),
                ticket.amountReceivedProperty(),
                ticket.paymentMethodProperty(),
                ticket.lines()));

        status.styleProperty().bind(Bindings.createStringBinding(() -> {
            if (ticket.paymentMethodProperty().get() == PaymentMethod.QR) {
                return "-fx-text-fill: #1d4ed8; -fx-font-weight: 600;";
            }
            double total = ticket.subtotalProperty().get();
            double tender = ticket.amountReceivedProperty().get();
            if (total <= 1e-9) return "-fx-text-fill: #6b7280;";
            if (tender + 1e-6 < total) return "-fx-text-fill: #b45309; -fx-font-weight: 600;";
            return "-fx-text-fill: #15803d; -fx-font-weight: 600;";
        }, ticket.paymentMethodProperty(), ticket.subtotalProperty(), ticket.amountReceivedProperty()));

        var pay = new Button("Complete sale");
        pay.setMaxWidth(Double.MAX_VALUE);
        pay.setStyle(
                "-fx-background-radius: 12; -fx-background-color: #2563eb; -fx-text-fill: white; "
                        + "-fx-font-weight: 700; -fx-padding: 12 16 12 16; -fx-font-size: 14;"
        );
        pay.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                    if (ticket.lines().isEmpty()) return true;
                    if (ticket.paymentMethodProperty().get() == PaymentMethod.QR) return false;
                    double total = ticket.subtotalProperty().get();
                    double tender = ticket.amountReceivedProperty().get();
                    return tender + 1e-6 < total;
                },
                ticket.lines(),
                ticket.subtotalProperty(),
                ticket.amountReceivedProperty(),
                ticket.paymentMethodProperty()));

        pay.setOnAction(e -> {
            var method = ticket.paymentMethodProperty().get();
            var total = ticket.subtotalProperty().get();
            var tender = ticket.amountReceivedProperty().get();
            var confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm payment");
            confirm.setHeaderText(String.format(Locale.US, "Charge Bs %.2f?", total));
            var body = new StringBuilder();
            if (method == PaymentMethod.CASH) {
                body.append(String.format(Locale.US, "Cash tendered: Bs %.2f%n", tender));
                if (tender > total + 1e-6) {
                    body.append(String.format(Locale.US, "Change to return: Bs %.2f%n", tender - total));
                }
            } else {
                body.append("Payment method: QR / digital (full amount).\n");
            }
            body.append("\nComplete this sale and clear the ticket?");
            confirm.setContentText(body.toString());
            confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                try {
                    sales.recordCompletedSale(ticket);
                } catch (Exception ex) {
                    var err = new Alert(Alert.AlertType.ERROR);
                    err.setTitle("Sale not saved");
                    err.setHeaderText("Could not record the sale in the database");
                    err.setContentText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
                    err.showAndWait();
                    return;
                }
                ticket.resetForNextSale();
            });
        });

        paymentBox.getChildren().addAll(
                dueCard,
                methodLabel,
                methodRow,
                cashBlock,
                qrBlock,
                status,
                pay
        );

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

    private static void configurePayMethodToggle(ToggleButton b) {
        b.setFocusTraversable(false);
        b.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(b, Priority.ALWAYS);
    }

    private static void applyPayToggleStyle(ToggleButton b, boolean selected) {
        if (selected) {
            b.setStyle(
                    "-fx-background-radius: 10; -fx-background-color: #2563eb; -fx-text-fill: white; "
                            + "-fx-font-weight: 700;"
            );
        } else {
            b.setStyle(
                    "-fx-background-radius: 10; -fx-background-color: #e5e7eb; -fx-text-fill: #374151; "
                            + "-fx-font-weight: 600;"
            );
        }
    }

    private Button quickAmount(int v, TextField amount) {
        var b = new Button(v + " Bs");
        b.setFocusTraversable(false);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-radius: 10; -fx-background-color: #f3f4f6; -fx-font-weight: 600;");
        b.setOnAction(e -> {
            var cur = parseMoney(amount.getText());
            var sum = cur + v;
            amount.setText(String.format(Locale.US, "%.2f", sum));
        });
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
        var detail = new Label(productCardSubtitle(p));
        detail.setWrapText(true);
        detail.setStyle("-fx-font-size: 11; -fx-text-fill: #6b7280;");

        card.getChildren().addAll(img, price, name, detail);
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

    private static String productCardSubtitle(Product p) {
        var j = new StringJoiner(" · ");
        if (p.brand() != null && !p.brand().isBlank()) j.add(p.brand());
        var u = p.unitSummary();
        if (!u.isBlank()) j.add(u);
        if (p.cost() > 1e-9) j.add(String.format(Locale.US, "cost Bs %.2f", p.cost()));
        var s = j.toString();
        return s.isEmpty() ? "\u00a0" : s;
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
            var unit = item.unitOfMeasure().isBlank() ? "" : " " + item.unitOfMeasure();
            var meta = new Label(String.format(
                    "Bs %.2f  ×  %s%s",
                    item.unitPrice(),
                    trim(item.quantity()),
                    unit
            ));
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

