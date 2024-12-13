package com.secinfostore.cellfactories;

import com.secinfostore.controller.components.ComponentFactory;
import com.secinfostore.controller.components.ErrorDialog;
import com.secinfostore.util.ClipboardHandler;
import com.secinfostore.util.ImageConversion;
import com.secinfostore.util.ImageNormalizer;
import io.nayuki.qrcodegen.QrCode;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TextCopyCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {
    @Override
    public TableCell<T, String> call(TableColumn<T, String> column) {
        return new TableCell<>() {
            private Button copyButton;
            private Button qrButton;
            private Label textLabel;
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    copyButton = new Button("Copy");
                    qrButton = new Button("QR");
                    textLabel = new Label(item);
                    copyButton.setOnAction(event -> {
                        ClipboardHandler.pasteTextToClipboard(textLabel.getText());
                    });

                    qrButton.setOnAction(event -> {
                        try {
                            ComponentFactory.displayQRCode(textLabel.getText().trim());
                        } catch (IOException e) {
                            ErrorDialog.showErrorDialog(e, "Error making QR CODE", "Text QR Code generation error");
                        } catch (Exception e) {
                            ErrorDialog.showErrorDialog(e, "FXML loading error", "Text Error Loading QR View");
                        }
                    });

                    HBox hboxButtons = new HBox(copyButton, qrButton);
                    VBox vBox = new VBox(10, textLabel, hboxButtons);
                    setGraphic(vBox);
                }
            }
        };
    }
}
