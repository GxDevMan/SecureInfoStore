package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.cellfactories.ActionAccountsCellFactory;
import com.secinfostore.secureinfostore.cellfactories.ImageLogoCellFactory;
import com.secinfostore.secureinfostore.cellfactories.PasswordAccountCellFactory;
import com.secinfostore.secureinfostore.cellfactories.TextCopyCellFactory;
import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.util.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MainUIController extends BaseController implements AddUpdateContract, UpdateDeleteViewConfirmContract<AccountObj> {

    @FXML
    private Button searchBTN;

    @FXML
    private Button resetBTN;

    @FXML
    private Button clearsearchBTN;

    @FXML
    private Button addAccountBTN;

    @FXML
    private Button reEncryptBTN;

    @FXML
    private Button goToEntryUIBTN;

    @FXML
    private Button encryptorBTN;

    @FXML
    private Button gotoChangelogBTN;

    @FXML
    private Button settingsBTN;

    @FXML
    private Button importAccountsJSONBTN;

    @FXML
    private Button exportAccountsJSONBTN;

    @FXML
    private TextField searchTxtField;

    @FXML
    private StackPane passwordgenPositionSTACKP;

    @FXML
    private TableView<AccountObj> accountsTable;

    @FXML
    private TableColumn<AccountObj, byte[]> logoColumn;

    @FXML
    private TableColumn<AccountObj, String> userPlatformColumn;

    @FXML
    private TableColumn<AccountObj, String> userNameColumn;

    @FXML
    private TableColumn<AccountObj, String> userEmailColumn;

    @FXML
    private TableColumn<AccountObj, String> userPasswordColumn;

    @FXML
    private TableColumn<AccountObj, Void> actionColumn;

    public void initialize() {
        TextCopyCellFactory<AccountObj> textCopyCellFactory = new TextCopyCellFactory<>();

        this.userPlatformColumn.setCellValueFactory(new PropertyValueFactory<>("platformName"));
        this.userPlatformColumn.setCellFactory(textCopyCellFactory);

        this.userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        this.userNameColumn.setCellFactory(textCopyCellFactory);

        this.userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        this.userEmailColumn.setCellFactory(textCopyCellFactory);

        this.userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("platformName"));
        this.logoColumn.setCellValueFactory(new PropertyValueFactory<>("platformThumbnail"));

        PasswordAccountCellFactory<AccountObj> passwordCellfactory = new PasswordAccountCellFactory<>();
        this.userPasswordColumn.setCellFactory(passwordCellfactory);

        ImageLogoCellFactory<AccountObj> imageLogoCellFactory = new ImageLogoCellFactory<>();
        this.logoColumn.setCellFactory(imageLogoCellFactory);

        ActionAccountsCellFactory<AccountObj> accountObjActionsCellFactory = new ActionAccountsCellFactory<>(this);
        this.actionColumn.setCellFactory(accountObjActionsCellFactory);
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(addAccountBTN)) {
            addAccount();
        } else if (event.getSource().equals(resetBTN)) {
            searchTxtField.setText("");
            displayAccounts(DatabaseHandler.getAccounts());
        } else if (event.getSource().equals(clearsearchBTN)) {
            searchTxtField.setText("");
        } else if (event.getSource().equals(searchBTN)) {
            searchAccounts(searchTxtField.getText().trim());
        } else if (event.getSource().equals(importAccountsJSONBTN)) {
            importaccountsFromJson();
        } else if (event.getSource().equals(exportAccountsJSONBTN)) {
            exportAccountstoJson();
        } else if (event.getSource().equals(encryptorBTN)) {
            goToEncryptor();
        } else if (event.getSource().equals(gotoChangelogBTN)) {
            goToChangeLog(event);
        } else if (event.getSource().equals(goToEntryUIBTN)) {
            goToEntryUI();
        } else if (event.getSource().equals(settingsBTN)) {
            goToSettings();
        }
    }

    private void searchAccounts(String searchKey) {
        Thread thread = new Thread(() -> {
            Optional<List<AccountObj>> accountObjListOptional;
            accountObjListOptional = DatabaseHandler.getAccounts(searchKey);
            Optional<List<AccountObj>> finalAccountObjListOptional = accountObjListOptional;
            Platform.runLater(() -> {
                displayAccounts(finalAccountObjListOptional);
            });
        });
        thread.start();
    }

    private void goToEncryptor() {
        try {
            DataStore dataStore = DataStore.getInstance();
            ComponentFactory.loadEncryptor((SecretKey) dataStore.getObject("default_key"));
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading TextENCDECUI.fxml");
        }
    }

    private void goToEntryUI() {
        HibernateUtil hibernateUtil = HibernateUtil.getInstance();
        hibernateUtil.shutdown();
        DataStore dataStore = DataStore.getInstance();

        for (int i = 0; i < 5; i++) {
            SecretKey key = null;
            try {
                key = EncryptionDecryption.generateAESKey();
            } catch (NoSuchAlgorithmException e) {
            }
            dataStore.insertObject("default_key", key);

        }
        dataStore.deleteObject("default_key");
        dataStore.deleteObject("default_db");
        mediator.switchTo("entryUI", null);
    }

    private void goToSettings() {
        try {
            ComponentFactory.settingsDisplaywithCharset();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading Error", "Error loading settings UI");
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
        ComponentFactory.setStageIcon(stage);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json Export File", "*.json"));
        fileChooser.setInitialFileName("ExportedAccounts.json");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File jsonFile = fileChooser.showSaveDialog(stage);

        if (jsonFile != null) {
            try {
                JsonHandler.writeToJsonToFile(accountList, jsonFile);
            } catch (Exception e) {
                ErrorDialog.showErrorDialog(e, "Accounts Export Error", "There was a problem writing to the File");
            }
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
        ComponentFactory.setStageIcon(stage);

        File selectedJSON = fileChooser.showOpenDialog(stage);

        if (selectedJSON == null)
            return;

        try {
            Optional<List<AccountObj>> importedAccountsOpt = JsonHandler.getAccountsFromJson(selectedJSON);

            if (!importedAccountsOpt.isPresent()) {
                return;
            }

            saveImportedAccountsToDB(importedAccountsOpt.get());
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "JSON Import Error", "There was an error loading the accounts");
        }
    }

    private void saveImportedAccountsToDB(List<AccountObj> accountList) {
        AtomicReference<Optional<List<AccountObj>>> accountObjListOptional = new AtomicReference<>(Optional.empty());
        Thread thread = new Thread(() -> {
            DatabaseHandler.saveAccount(accountList);
            accountObjListOptional.set(DatabaseHandler.getAccounts());
            Platform.runLater(() -> {
                displayAccounts(accountObjListOptional.get());
            });
        });
        thread.start();
    }

    private void addAccount() {
        try {
            ComponentFactory.addUpdateAccountUI(this);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading AddUpdateAccountUI.fxml");
        }
    }

    private void displayAccounts(Optional<List<AccountObj>> accountObjList) {
        if (accountObjList.isEmpty())
            return;

        if (accountObjList.get().isEmpty())
            return;

        accountsTable.getItems().clear();
        accountsTable.refresh();

        List<AccountObj> accountList = accountObjList.get();
        try {
            ObservableList<AccountObj> observableAccounts = FXCollections.observableList(accountList);
            accountsTable.setItems(observableAccounts);
            accountsTable.refresh();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Accounts Loading Error", "There was an error displaying the Accounts Preview");
        }
    }

    private void goToChangeLog(ActionEvent event) {
        mediator.switchTo("changeLogUI", null);
    }

    @Override
    public void saveAccountToDB(AccountObj account) {
        Thread thread = new Thread(() -> {
            DatabaseHandler.saveAccount(account);
            Optional<List<AccountObj>> accountObjListOptional;
            accountObjListOptional = DatabaseHandler.getAccounts();
            Optional<List<AccountObj>> finalAccountObjListOptional = accountObjListOptional;
            Platform.runLater(() -> {
                displayAccounts(finalAccountObjListOptional);
            });
        });
        thread.start();
    }

    @Override
    public void viewUpdateAccount(AccountObj account) {
        try {
            ComponentFactory.addUpdateAccountUI(account, this);
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
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ComponentFactory.setStageIcon(stage);

        VBox vbox = new VBox();
        vbox.setSpacing(10);


        Label accountNameLabel = new Label("Account User Name: " + account.getUserName());
        Label accountEmailLabel = new Label("Account Email: " + account.getEmail());
        vbox.getChildren().addAll(accountNameLabel, accountEmailLabel);

        alert.getDialogPane().setContent(vbox);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            Thread thread = new Thread(() -> {
                DatabaseHandler.deleteAccount(account);
                Optional<List<AccountObj>> accountObjListOptional;
                accountObjListOptional = DatabaseHandler.getAccounts();
                Optional<List<AccountObj>> finalAccountObjListOptional = accountObjListOptional;
                Platform.runLater(() -> {
                    displayAccounts(finalAccountObjListOptional);
                });
            });
            thread.start();
        }
    }

    @Override
    public void setupSelectedController(Object data) {
        try {
            VBox passwordgenComponent = ComponentFactory.getPasswordComponent();
            passwordgenPositionSTACKP.getChildren().add(passwordgenComponent);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML Loading Error", "Error loading password generation utility");
        }

        Thread thread = new Thread(() -> {
            Optional<List<AccountObj>> accountObjListOptional;
            accountObjListOptional = DatabaseHandler.getAccounts();
            Optional<List<AccountObj>> finalAccountObjListOptional = accountObjListOptional;
            Platform.runLater(() -> {
                displayAccounts(finalAccountObjListOptional);
            });
        });
        thread.start();
    }
}
