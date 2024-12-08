package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.util.DataStore;
import com.secinfostore.secureinfostore.util.DatabaseHandler;
import com.secinfostore.secureinfostore.util.JsonHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainUIController implements AddUpdateContract, UpdateDeleteViewConfirmContract {

    @FXML
    private Button searchBTN;

    @FXML
    private Button refreshBTN;

    @FXML
    private Button clearsearchBTN;

    @FXML
    private Button addAccountBTN;

    @FXML
    private Button reEncryptBTN;

    @FXML
    private Button goToEntryUIBTN;

    @FXML
    private Button importAccountsJSONBTN;

    @FXML
    private Button exportAccountsJSONBTN;

    @FXML
    private TextField searchTxtField;

    @FXML
    private StackPane passwordgenPositionSTACKP;

    @FXML
    private TilePane accountsViewTilePane;


    public void setMainUIController() {
        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("PasswordgenComponent.fxml"));
            VBox passwordgenComponent = loader.load();
            passwordgenPositionSTACKP.getChildren().add(passwordgenComponent);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML Loading Error", "Error loading password generation utility");
        }
        displayAccounts(DatabaseHandler.getAccounts());
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(addAccountBTN)) {
            addAccount();
        } else if (event.getSource().equals(refreshBTN)) {
            displayAccounts(DatabaseHandler.getAccounts());
        } else if (event.getSource().equals(clearsearchBTN)) {
        } else if (event.getSource().equals(searchBTN)) {
        } else if (event.getSource().equals(importAccountsJSONBTN)) {
            importaccountsFromJson();
        } else if (event.getSource().equals(exportAccountsJSONBTN)) {
            exportAccountstoJson();
        }
    }

    private void exportAccountstoJson() {
        Optional<List<AccountObj>> accountOptionalList = DatabaseHandler.getAccounts();
        if (!accountOptionalList.isPresent()) {
            ErrorDialog.showErrorDialog(new Exception("Empty List"), "Accounts Export Error", "No Accounts to Export");
            return;
        }
        List<AccountObj> accountList = accountOptionalList.get();

        Stage stage = new Stage();
        DataStore dataStore = DataStore.getInstance();
        String dataStoreTitle = (String) dataStore.getObject("default_title");
        String stageTitle = String.format("%s -%s", dataStoreTitle, "Export Accounts To Json");
        stage.setTitle(stageTitle);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json Export File","*.json"));
        fileChooser.setInitialFileName("ExportedAccounts.json");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File jsonFile = fileChooser.showSaveDialog(stage);

        try {
            JsonHandler.writeToJsonToFile(accountList,jsonFile);
        } catch (Exception e){
            ErrorDialog.showErrorDialog(e,"Accounts Export Error", "There was a problem writing to the File");
        }
    }

    private void importaccountsFromJson() {
        Stage stage = new Stage();

        DataStore dataStore = DataStore.getInstance();

        String dataStoreTitle = (String) dataStore.getObject("default_title");
        String stageTitle = String.format("%s -%s", dataStoreTitle, "Import Accounts From Json");
        stage.setTitle(stageTitle);

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("Import Accounts", "*.json");
        fileChooser.getExtensionFilters().add(jsonFilter);

        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        File selectedJSON = fileChooser.showOpenDialog(stage);

        if (selectedJSON == null)
            return;

        try {
            Optional<List<AccountObj>> importedAccountsOpt = JsonHandler.getAccountsFromJson(selectedJSON);

            if (!importedAccountsOpt.isPresent()){
                return;
            }

            saveImportedAccountsToDB(importedAccountsOpt.get());
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "JSON Import Error", "There was an error loading the accounts");
        }
    }

    private void saveImportedAccountsToDB(List<AccountObj> accountList){
        DatabaseHandler.saveAccount(accountList);
        displayAccounts(DatabaseHandler.getAccounts());
    }

    private void addAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AddUpdateAccountUI.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            Stage stage = new Stage();

            DataStore dataStore = DataStore.getInstance();
            String title = (String) dataStore.getObject("default_title");
            stage.setScene(scene);
            stage.setTitle(String.format("%s - %s", title, "New Account Information"));
            stage.initModality(Modality.APPLICATION_MODAL);

            AddUpdateAccountController controller = loader.getController();
            controller.setAddUpdateAccount(stage, this);
            stage.show();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading AddUpdateAccountUI.fxml");
        }
    }

    private void displayAccounts(Optional<List<AccountObj>> accountObjList) {
        if (accountObjList.isEmpty())
            return;

        if (accountObjList.get().isEmpty())
            return;

        accountsViewTilePane.getChildren().clear();
        try {
            for (AccountObj account : accountObjList.get()) {
                FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AccountsPreviewComponent.fxml"));
                AnchorPane anchorPane = loader.load();

                AccountsPreviewComponentController controller = loader.getController();
                controller.setAccountPreview(account, this);

                accountsViewTilePane.getChildren().add(anchorPane);
            }
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Accounts Loading Error", "There was an error displaying the Error");
        }
    }

    @Override
    public void saveAccountToDB(AccountObj account) {
        DatabaseHandler.saveAccount(account);
        displayAccounts(DatabaseHandler.getAccounts());
    }

    @Override
    public void viewUpdateAccount(AccountObj account) {
        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AddUpdateAccountUI.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            Stage stage = new Stage();

            DataStore dataStore = DataStore.getInstance();
            String title = (String) dataStore.getObject("default_title");
            stage.setScene(scene);
            stage.setTitle(String.format("%s - %s", title, "Update Account Information"));
            stage.initModality(Modality.APPLICATION_MODAL);

            AddUpdateAccountController controller = loader.getController();
            controller.setAddUpdateAccount(stage, this, account);
            stage.show();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading AddUpdateAccountUI.fxml");
        }
    }

    @Override
    public void confirmDeleteAccount(AccountObj account) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this account?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(SecureInformationStore.class.getResource("styles/dark-theme.css").toExternalForm());

        VBox vbox = new VBox();
        vbox.setSpacing(10);

        Label accountNameLabel = new Label("Account User Name: " + account.getUserName());
        Label accountEmailLabel = new Label("Account Email: " + account.getEmail());
        vbox.getChildren().addAll(accountNameLabel, accountEmailLabel);

        alert.getDialogPane().setContent(vbox);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if(result == ButtonType.OK){
            DatabaseHandler.deleteAccount(account);
            displayAccounts(DatabaseHandler.getAccounts());
        }
    }
}
