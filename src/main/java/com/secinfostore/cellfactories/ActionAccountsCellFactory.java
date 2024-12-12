package com.secinfostore.cellfactories;
import com.secinfostore.controller.interfaces.UpdateDeleteViewConfirmContract;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
            private final VBox buttonsBox = new VBox(10, updateButton, deleteButton);

            {
                updateButton.setOnAction(event -> {
                    S rowData = getTableRow().getItem();
                    if (rowData != null) {
                        contract.viewUpdateObj(rowData);
                    }
                });

                deleteButton.setOnAction(event -> {
                    S rowData = getTableRow().getItem();
                    if (rowData != null) {
                        contract.confirmDeleteObj(rowData);
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