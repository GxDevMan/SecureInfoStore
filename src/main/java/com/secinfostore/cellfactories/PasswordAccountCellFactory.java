package com.secinfostore.cellfactories;

import com.secinfostore.controller.components.ComponentFactory;
import com.secinfostore.controller.components.ErrorDialog;
import com.secinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.util.ClipboardHandler;
import com.secinfostore.util.ImageConversion;
import com.secinfostore.util.ImageNormalizer;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import io.nayuki.qrcodegen.QrCode;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PasswordAccountCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {
    @Override
    public TableCell<T, String> call(TableColumn<T, String> column) {
        return new TableCell<>() {
            private PasswordField passwordField;
            private KeyTextFieldSkin textFieldSkin;
            private CheckBox revealCheckBox;
            private Button copyButton;
            private Button qrCodeButton;

            @Override
            protected void updateItem(String passwordItem, boolean empty) {
                super.updateItem(passwordItem, empty);
                if (empty || passwordItem == null) {
                    setText(null);
                } else {
                    passwordField = new PasswordField();
                    textFieldSkin = new KeyTextFieldSkin(passwordField);
                    revealCheckBox = new CheckBox();
                    copyButton = new Button("Copy");
                    qrCodeButton = new Button("QR");

                    copyButton.setOnAction(event -> {
                        ClipboardHandler.pasteTextToClipboard(passwordField.getText());
                    });

                    qrCodeButton.setOnAction(event -> {
                        try {
                            ComponentFactory.displayQRCode(passwordField.getText().trim());
                        } catch (IOException e) {
                            ErrorDialog.showErrorDialog(e, "Error making QR CODE", "Password QR Code generation error");
                        } catch (Exception e) {
                            ErrorDialog.showErrorDialog(e, "FXML loading error", "Error Loading Password QR View");
                        }
                    });

                    passwordField.setSkin(textFieldSkin);
                    passwordField.setText(passwordItem);
                    passwordField.setDisable(true);
                    revealCheckBox.setSelected(false);

                    revealCheckBox.selectedProperty().addListener((observable -> {
                        textFieldSkin.setReveal(revealCheckBox.isSelected());
                        passwordField.setText(passwordField.getText());
                    }));

                    HBox hBoxPassword = new HBox(passwordField, revealCheckBox);
                    HBox passButtons = new HBox(copyButton, qrCodeButton);
                    HBox.setHgrow(passwordField, Priority.ALWAYS);
                    passwordField.setMaxWidth(Double.MAX_VALUE);

                    VBox vBox = new VBox(10, hBoxPassword, passButtons);
                    setGraphic(vBox);
                }
            }
        };
    }
}
