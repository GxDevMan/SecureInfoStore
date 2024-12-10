package com.secinfostore.secureinfostore.cellfactories;
import com.secinfostore.secureinfostore.controller.UpdateDeleteViewConfirmContract;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ActionAccountsCellFactory<S> implements Callback<TableColumn<S, Void>, TableCell<S, Void>> {
    private final UpdateDeleteViewConfirmContract contract;

    public ActionAccountsCellFactory(UpdateDeleteViewConfirmContract contract) {
        this.contract = contract;
    }

    @Override
    public TableCell<S, Void> call(TableColumn<S, Void> param) {
        return new TableCell<S, Void>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsBox = new HBox(10, updateButton, deleteButton);

            {
                updateButton.setOnAction(event -> {
                    S rowData = getTableRow().getItem();
                    if (rowData != null) {
                        contract.viewUpdateAccount(rowData);
                    }
                });

                deleteButton.setOnAction(event -> {
                    S rowData = getTableRow().getItem();
                    if (rowData != null) {
                        contract.confirmDeleteAccount(rowData);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        };
    }
}