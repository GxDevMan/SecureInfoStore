package com.secinfostore.controller.components;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ImageViewUIController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ImageView dispImageView;

    @FXML
    private BorderPane rootPane;

    private double lastMouseX;
    private double lastMouseY;

    private double scale = 1.0;
    private double deltaScale = 1.1;
    private double maxZoom;
    private double minZoom;

    @FXML
    public void initialize() {
        dispImageView.setOnScroll(this::handleZoom);

        dispImageView.setOnMousePressed(this::handleMousePressed);
        dispImageView.setOnMouseDragged(this::handleMouseDragged);

        scrollPane.prefWidthProperty().bind(rootPane.widthProperty());
        scrollPane.prefHeightProperty().bind(rootPane.heightProperty());
    }

    public void buttonClick(ActionEvent event) {
        handleResetView();
    }

    public void setImageViewUIController(Image image, Stage stage) {
        if (image == null) {
            stage.close();
            return;
        }
        else {
            dispImageView.setImage(image);
        }

        double imageWidth = dispImageView.getImage().getWidth();
        double imageHeight = dispImageView.getImage().getHeight();
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        double[] zoomLimits = calculateZoomLimits(imageWidth, imageHeight, viewportWidth, viewportHeight);
        minZoom = zoomLimits[0];
        maxZoom = zoomLimits[1];
        scale = calculateScale(imageWidth,imageHeight,viewportWidth,viewportHeight);

        centerImageInScrollPane();
        Platform.runLater(() -> {
            handleResetView();
        });
        stage.show();
    }

    public static double[] calculateZoomLimits(double imageWidth, double imageHeight, double viewportWidth, double viewportHeight) {
        double minZoomX = viewportWidth / imageWidth;
        double minZoomY = viewportHeight / imageHeight;
        double minZoom = Math.min(minZoomX, minZoomY);

        double maxZoom = 3.0;

        return new double[]{minZoom, maxZoom};
    }

    private void centerImageInScrollPane() {
        Image image = dispImageView.getImage();
        if (image == null) return;

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        double scaleX = viewportWidth / imageWidth;
        double scaleY = viewportHeight / imageHeight;
        double scale = Math.min(scaleX, scaleY);

        dispImageView.setFitWidth(imageWidth * scale);
        dispImageView.setFitHeight(imageHeight * scale);
        dispImageView.setPreserveRatio(true);

        double contentWidth = dispImageView.getBoundsInParent().getWidth();
        double contentHeight = dispImageView.getBoundsInParent().getHeight();

        double hValue = (contentWidth - viewportWidth) / 2 / (contentWidth - viewportWidth);
        double vValue = (contentHeight - viewportHeight) / 2 / (contentHeight - viewportHeight);

        scrollPane.setHvalue(Math.max(0, Math.min(1, hValue)));
        scrollPane.setVvalue(Math.max(0, Math.min(1, vValue)));
    }

    private void handleResetView() {
        if (dispImageView.getImage() == null) return;
        double imageWidth = dispImageView.getImage().getWidth();
        double imageHeight = dispImageView.getImage().getHeight();
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        scale = calculateScale(imageWidth, imageHeight, viewportWidth, viewportHeight);
        setScale(scale);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
    }

    private void handleZoom(ScrollEvent event) {
        if (dispImageView.getImage() == null) return;
        if (event.getDeltaY() > 0) {
            scale *= deltaScale;
        } else {
            scale /= deltaScale;
        }

        if (scale < minZoom) {
            scale = minZoom;
        } else if (scale > maxZoom) {
            scale = maxZoom;
        }

        dispImageView.setScaleX(scale);
        dispImageView.setScaleY(scale);

        scrollPane.setVvalue(scrollPane.getVvalue());
        scrollPane.setHvalue(scrollPane.getHvalue());
        event.consume();
    }

    private void handleMouseDragged(MouseEvent event) {
        if (dispImageView.getImage() == null) return;
        double deltaX = lastMouseX - event.getSceneX();
        double deltaY = lastMouseY - event.getSceneY();

        scrollPane.setHvalue(scrollPane.getHvalue() + deltaX / scrollPane.getContent().getBoundsInLocal().getWidth());
        scrollPane.setVvalue(scrollPane.getVvalue() + deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());

        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
    }

    private void handleMousePressed(MouseEvent event) {
        if (dispImageView.getImage() == null) return;
        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
    }

    private void setScale(double newScale) {
        scale = Math.max(minZoom, Math.min(newScale, maxZoom));
        dispImageView.setScaleX(scale);
        dispImageView.setScaleY(scale);
    }

    private double calculateScale(double imageWidth, double imageHeight, double viewportWidth, double viewportHeight) {
        double scaleX = viewportWidth / imageWidth;
        double scaleY = viewportHeight / imageHeight;
        double scale = Math.min(scaleX, scaleY);
        return scale;
    }
}
