package com.vendei.desktop.ui;

import javafx.scene.image.Image;

final class ProductImages {
    private ProductImages() {}

    /**
     * @param imageUrl absolute classpath path (e.g. {@code /assets/vendei/demo-products/apple.jpg}),
     *     or http(s)/file URL string
     */
    static Image loadForCard(String imageUrl, double fitW, double fitH) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }
        var raw = imageUrl.trim();
        try {
            if (raw.startsWith("http://") || raw.startsWith("https://") || raw.startsWith("file:")) {
                return new Image(raw, fitW, fitH, true, true, true);
            }
            var path = raw.startsWith("/") ? raw : "/" + raw;
            var url = ProductImages.class.getResource(path);
            if (url == null) {
                return null;
            }
            return new Image(url.toExternalForm(), fitW, fitH, true, true, true);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
