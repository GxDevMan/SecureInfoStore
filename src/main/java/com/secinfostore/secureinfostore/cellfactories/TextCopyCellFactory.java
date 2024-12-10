package com.secinfostore.secureinfostore.cellfactories;

import com.secinfostore.secureinfostore.util.ClipboardHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class TextCopyCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {
    @Override
    public TableCell<T, String> call(TableColumn<T, String> column) {
        return new TableCell<>() {
            private Button copyButton;
            private Label textLabel;
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    copyButton = new Button("Copy");
                    textLabel = new Label(item);
                    copyButton.setOnAction(event -> {
                        ClipboardHandler.pasteTextToClipboard(textLabel.getText());
                    });
                    HBox hBox = new HBox(10, textLabel, copyButton);
                    setGraphic(hBox);
                }
            }
        };
    }
}
