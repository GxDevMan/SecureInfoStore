package com.secinfostore.controller;

import com.secinfostore.cellfactories.ActionAccountsCellFactory;
import com.secinfostore.cellfactories.ImageLogoCellFactory;
import com.secinfostore.cellfactories.PasswordAccountCellFactory;
import com.secinfostore.cellfactories.TextCopyCellFactory;
import com.secinfostore.controller.components.ComponentFactory;
import com.secinfostore.controller.components.ErrorDialog;
import com.secinfostore.controller.components.FileLoadSaving;
import com.secinfostore.controller.interfaces.AddUpdateContract;
import com.secinfostore.controller.interfaces.UpdateDeleteViewConfirmContract;
import com.secinfostore.model.AccountObj;
import com.secinfostore.util.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MainUIAccountsController extends BaseController implements AddUpdateContract<AccountObj>, UpdateDeleteViewConfirmContract<AccountObj> {

    @FXML
    private Button reEncryptBTN;

    @FXML
    private Button searchBTN;

    @FXML
    private Button resetBTN;

    @FXML
    private Button goToTextUIBTN;

    @FXML
    private Button clearsearchBTN;

    @FXML
    private Button addAccountBTN;

    @FXML
    private Button goTouiSelectorBTN;

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
    private Button exportToTxtBTN;

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

        this.userPlatformColumn.setCellValueFactory(new PropertyValueFactory<>("userPlatform"));
        this.userPlatformColumn.setCellFactory(textCopyCellFactory);

        this.userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        this.userNameColumn.setCellFactory(textCopyCellFactory);

        this.userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        this.userEmailColumn.setCellFactory(textCopyCellFactory);

        this.userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("userPassword"));
        PasswordAccountCellFactory<AccountObj> passwordCellfactory = new PasswordAccountCellFactory<>();
        this.userPasswordColumn.setCellFactory(passwordCellfactory);

        this.logoColumn.setCellValueFactory(new PropertyValueFactory<>("platformThumbnail"));
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
            displayAccounts(DatabaseHandler.getAllAccounts());
        } else if (event.getSource().equals(clearsearchBTN)) {
            searchTxtField.setText("");
        } else if (event.getSource().equals(searchBTN)) {
            searchAccounts(searchTxtField.getText().trim());
        } else if (event.getSource().equals(importAccountsJSONBTN)) {
            importAccountsFromJson();
        } else if (event.getSource().equals(exportAccountsJSONBTN)) {
            exportAccountsToJson(true);
        } else if (event.getSource().equals(encryptorBTN)) {
            goToEncryptor();
        } else if (event.getSource().equals(gotoChangelogBTN)) {
            mediator.switchTo("changeLogUI", null);
        } else if (event.getSource().equals(goTouiSelectorBTN)) {
            goToEntryUI(event);
        } else if (event.getSource().equals(settingsBTN)) {
            goToSettings();
        } else if (event.getSource().equals(exportToTxtBTN)) {
            exportAccountsToJson(false);
        } else if (event.getSource().equals(goToTextUIBTN)) {
            mediator.switchTo("textUIAccount", null);
        } else if (event.getSource().equals(reEncryptBTN)) {
            reEncrypt();
        }
    }

    private void reEncrypt() {
        boolean confirmation = ComponentFactory.confirmReEncryption();
        if (!confirmation) {
            return;
        }

        try {
            ComponentFactory.loadReEncryptionWindow();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading Error", "Error loading Re Encryption Window");
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

    private void exportAccountsToJson(boolean json) {
        String fileFormat = json ? "JSON" : "TXT";
        Task<Optional<List<AccountObj>>> task = new Task<>() {
            @Override
            protected Optional<List<AccountObj>> call() {
                return DatabaseHandler.getAllAccounts();
            }
        };

        task.setOnSucceeded(event -> {
            Optional<List<AccountObj>> accountOptionalList = task.getValue();
            if (accountOptionalList.isEmpty()) {
                ErrorDialog.showErrorDialog(new Exception("No Accounts to export"), "Empty List", "No Accounts to Export");
                return;
            }

            try {
                FileLoadSaving.exportToFileFormat(accountOptionalList, json);
            } catch (Exception e) {
                ErrorDialog.showErrorDialog(e, "Export error", String.format("Failed to export accounts to %s", fileFormat));
            }
        });

        task.setOnFailed(event -> {
            ErrorDialog.showErrorDialog(new Exception("Accounts Export Error"), "Accounts Exporting error", "There was a problem exporting the accounts");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void importAccountsFromJson() {
        File selectedJSON = FileLoadSaving.loadedJsonConfig();
        if (selectedJSON == null)
            return;

        try {
            Optional<List<AccountObj>> importedAccountsOpt = DataExporterImporterHandler.getAccountsFromJson(selectedJSON);
            if (importedAccountsOpt.isEmpty()) {
                return;
            }
            Thread thread = new Thread(() -> {
                saveImportedAccountsToDB(importedAccountsOpt.get());
                Optional<List<AccountObj>> refreshedObjList = DatabaseHandler.getAllAccounts();
                Platform.runLater(() -> {
                    displayAccounts(refreshedObjList);
                });
            });
            thread.start();

        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "JSON Import Error", "There was an error loading the accounts");
        }
    }

    private void addAccount() {
        try {
            ComponentFactory.addUpdateAccountUI(this);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading AddUpdateAccountUI.fxml");
        }
    }

    private void saveImportedAccountsToDB(List<AccountObj> accountList) {
        AtomicReference<Optional<List<AccountObj>>> accountObjListOptional = new AtomicReference<>(Optional.empty());
        Thread thread = new Thread(() -> {
            DatabaseHandler.saveAccount(accountList);
            accountObjListOptional.set(DatabaseHandler.getAllAccounts());
            Platform.runLater(() -> {
                displayAccounts(accountObjListOptional.get());
            });
        });
        thread.start();
    }

    private void goToEncryptor() {
        try {
            DataStore dataStore = DataStore.getInstance();
            ComponentFactory.loadEncryptor((SecretKey) dataStore.getObject("default_key"));
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading EncryptionDecryptionUI.fxml");
        }
    }

    private void goToEntryUI(ActionEvent event) {
        HibernateUtil hibernateUtil = HibernateUtil.getInstance();
        hibernateUtil.shutdown();
        DataStore dataStore = DataStore.getInstance();

        try {
            SecretKey key = (SecretKey) dataStore.getObject("default_key");
            EncryptionDecryption.securelyClearKey(key);
            for (int i = 0; i < 5; i++) {
                key = EncryptionDecryption.generateAESKey();
            }
            dataStore.insertObject("default_key", key);
        } catch (NoSuchAlgorithmException e) {
        }
        dataStore.deleteObject("default_key");
        dataStore.deleteObject("default_db");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ComponentFactory.setWindowsTitle(stage);
        mediator.switchTo("entryUI", null);
    }

    private void goToSettings() {
        try {
            ComponentFactory.settingsDisplaywithCharset();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading Error", "Error loading settings UI");
        }
    }

    @Override
    public void saveEntityToDB(AccountObj account) {
        Thread thread = new Thread(() -> {
            DatabaseHandler.saveAccount(account);
            Optional<List<AccountObj>> accountObjListOptional;
            accountObjListOptional = DatabaseHandler.getAllAccounts();
            Optional<List<AccountObj>> finalAccountObjListOptional = accountObjListOptional;
            Platform.runLater(() -> {
                displayAccounts(finalAccountObjListOptional);
            });
        });
        thread.start();
    }

    @Override
    public void viewUpdateObj(AccountObj account) {
        try {
            ComponentFactory.addUpdateAccountUI(account, this);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading AddUpdateAccountUI.fxml");
        }
    }

    @Override
    public void confirmDeleteObj(AccountObj account) {
        if (ComponentFactory.confirmAccountDeletion(account)) {
            Thread thread = new Thread(() -> {
                DatabaseHandler.deleteAccount(account);
                Optional<List<AccountObj>> accountObjListOptional;
                accountObjListOptional = DatabaseHandler.getAllAccounts();
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
            accountObjListOptional = DatabaseHandler.getAllAccounts();
            Optional<List<AccountObj>> finalAccountObjListOptional = accountObjListOptional;
            Platform.runLater(() -> {
                displayAccounts(finalAccountObjListOptional);
            });
        });
        thread.start();
    }

    private void displayAccounts(Optional<List<AccountObj>> accountObjList) {
        if (accountObjList.isEmpty()) {
            accountsTable.getItems().clear();
            accountsTable.refresh();
            return;
        }

        if (accountObjList.get().isEmpty()) {
            accountsTable.getItems().clear();
            accountsTable.refresh();
            return;
        }

        List<AccountObj> accountList = accountObjList.get();
        try {
            ObservableList<AccountObj> observableAccounts = FXCollections.observableList(accountList);
            accountsTable.setItems(observableAccounts);
            accountsTable.refresh();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Accounts Loading Error", "There was an error displaying the Accounts Preview");
        }
    }
}
