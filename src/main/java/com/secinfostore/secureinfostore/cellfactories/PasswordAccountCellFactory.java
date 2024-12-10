package com.secinfostore.secureinfostore.cellfactories;

import com.secinfostore.secureinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.secureinfostore.util.ClipboardHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class PasswordAccountCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {
    @Override
    public TableCell<T, String> call(TableColumn<T, String> column) {
        return new TableCell<>() {
            private PasswordField passwordField;
            private KeyTextFieldSkin textFieldSkin;
            private CheckBox revealCheckBox;
            private Button copyButton;

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

                    copyButton.setOnAction(event -> {
                        ClipboardHandler.pasteTextToClipboard(passwordField.getText());
                    });

                    passwordField.setSkin(textFieldSkin);
                    passwordField.setText(passwordItem);
                    passwordField.setDisable(true);
                    revealCheckBox.setSelected(false);

                    revealCheckBox.selectedProperty().addListener((observable -> {
                        textFieldSkin.setReveal(revealCheckBox.isSelected());
                        passwordField.setText(passwordField.getText());
                    }));

                    HBox hBox = new HBox(10, passwordField, revealCheckBox, copyButton);
                    setGraphic(hBox);
                }
            }
        };
    }
}
