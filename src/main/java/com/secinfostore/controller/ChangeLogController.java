package com.secinfostore.controller;

import com.secinfostore.SecureInformationStore;
import com.secinfostore.cellfactories.ActionChangeLogCellFactory;
import com.secinfostore.controller.components.ComponentFactory;
import com.secinfostore.model.ChangeLogObj;
import com.secinfostore.cellfactories.PasswordChangeLogCellFactory;
import com.secinfostore.cellfactories.TimeStampChangeLogCellFactory;
import com.secinfostore.controller.interfaces.ChangeLogContract;
import com.secinfostore.util.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class ChangeLogController extends BaseController implements ChangeLogContract<ChangeLogObj> {

    @FXML
    private TableView<ChangeLogObj> databaseTable;

    @FXML
    private TableColumn<ChangeLogObj, Timestamp> timeChange;
    @FXML
    private TableColumn<ChangeLogObj, String> userPlatformColumn;
    @FXML
    private TableColumn<ChangeLogObj, String> userNameColumn;
    @FXML
    private TableColumn<ChangeLogObj, String> userEmailColumn;
    @FXML
    private TableColumn<ChangeLogObj, String> userPasswordColumn;
    @FXML
    private TableColumn<ChangeLogObj, Void> actionColumn;

    public void initialize(){
        TimeStampChangeLogCellFactory<ChangeLogObj> timeStampCellFactory = new TimeStampChangeLogCellFactory<>();
        this.timeChange.setCellValueFactory(new PropertyValueFactory<>("timeChanged"));
        this.timeChange.setCellFactory(timeStampCellFactory);

        this.userPlatformColumn.setCellValueFactory(new PropertyValueFactory<>("platformName"));
        this.userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        this.userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        this.userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        this.userPasswordColumn.setCellFactory(new PasswordChangeLogCellFactory<>());

        ActionChangeLogCellFactory<ChangeLogObj> cellFactory = new ActionChangeLogCellFactory<>(this);
        this.actionColumn.setCellFactory(cellFactory);
    }

    public void deleteAllLogs() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete all records?");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ComponentFactory.setStageIcon(stage);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(SecureInformationStore.class.getResource("styles/dark-theme.css").toExternalForm());

        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == confirmButton) {
                if (DatabaseHandler.deleteAllChangeLog()) {
                    refreshTable(DatabaseHandler.getChangeLogs());
                }
            }
        });
    }

    public void changeScene(ActionEvent event) {
        mediator.switchTo("mainUIAccount", null);
    }

    private void refreshTable(Optional<List<ChangeLogObj>> changeLogObjOptional){
        if(changeLogObjOptional.isEmpty())
            return;

        databaseTable.getItems().clear();
        databaseTable.refresh();

        List<ChangeLogObj> changeLogObjList = changeLogObjOptional.get();
        ObservableList<ChangeLogObj> changeLogObjsObservable = FXCollections.observableList(changeLogObjList);

        databaseTable.setItems(changeLogObjsObservable);
        databaseTable.refresh();
    }

    @Override
    public void deleteChangeLog(ChangeLogObj changeLogObj) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this record?");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ComponentFactory.setStageIcon(stage);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(SecureInformationStore.class.getResource("styles/dark-theme.css").toExternalForm());

        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == confirmButton) {
                if (DatabaseHandler.deleteChangeLog(changeLogObj)) {
                    refreshTable(DatabaseHandler.getChangeLogs());
                }
            }
        });
    }

    @Override
    public void setupSelectedController(Object data) {
        refreshTable(DatabaseHandler.getChangeLogs());
    }
}
