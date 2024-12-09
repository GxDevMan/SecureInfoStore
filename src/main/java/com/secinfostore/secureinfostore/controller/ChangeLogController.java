package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.cellfactories.ActionChangeLogCellFactory;
import com.secinfostore.secureinfostore.cellfactories.PasswordChangeLogCellFactory;
import com.secinfostore.secureinfostore.cellfactories.TimeStampChangeLogCellFactory;
import com.secinfostore.secureinfostore.model.ChangeLogObj;
import com.secinfostore.secureinfostore.util.DatabaseHandler;
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

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();

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
        databaseTable.getItems().clear();
        if(changeLogObjOptional.isEmpty())
            return;

        List<ChangeLogObj> changeLogObjList = changeLogObjOptional.get();
        for(ChangeLogObj changeLogObj : changeLogObjList){
            databaseTable.getItems().add(changeLogObj);
        }
    }

    @Override
    public void deleteChangeLog(ChangeLogObj changeLogObj) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this record?");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();

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
