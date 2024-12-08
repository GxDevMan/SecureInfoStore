package com.secinfostore.secureinfostore.cellfactories;

import com.secinfostore.secureinfostore.customskin.KeyTextFieldSkin;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.util.Callback;


public class PasswordChangeLogCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {
    @Override
    public TableCell<T, String> call(TableColumn<T, String> column) {
        return new TableCell<>() {
            private PasswordField passwordField = new PasswordField();
            private KeyTextFieldSkin textFieldSkin = new KeyTextFieldSkin(passwordField);
            private CheckBox revealCheckBox = new CheckBox();

            @Override
            protected void updateItem(String passwordItem, boolean empty) {
                super.updateItem(passwordItem, empty);
                if (empty || passwordItem == null) {
                    setText(null);
                } else {
                    passwordField.setSkin(textFieldSkin);
                    passwordField.setText(passwordItem);
                    passwordField.setDisable(true);
                    revealCheckBox.setSelected(false);

                    revealCheckBox.selectedProperty().addListener((observable -> {
                        textFieldSkin.setReveal(revealCheckBox.isSelected());
                        passwordField.setText(passwordField.getText());
                    }));

                    HBox hBox = new HBox(10, passwordField, revealCheckBox);
                    setGraphic(hBox);
                }
            }
        };
    }
}
