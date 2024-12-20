package com.secinfostore.cellfactories;

import com.secinfostore.controller.interfaces.ChangeLogContract;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ActionChangeLogCellFactory<S> implements Callback<TableColumn<S, Void>, TableCell<S, Void>> {
    private final ChangeLogContract contract;

    public ActionChangeLogCellFactory(ChangeLogContract contract) {
        this.contract = contract;
    }

    @Override
    public TableCell<S, Void> call(TableColumn<S, Void> param) {
        return new TableCell<S, Void>() {
            private final Button deleteButton = new Button("Delete");
            HBox buttonsBox = new HBox(deleteButton);
            {
                deleteButton.setOnAction(event -> {
                    S rowData =  getTableRow().getItem();
                    contract.deleteChangeLog(rowData);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    buttonsBox.setSpacing(10);
                    setGraphic(buttonsBox);
                }
            }
        };
    }

}
