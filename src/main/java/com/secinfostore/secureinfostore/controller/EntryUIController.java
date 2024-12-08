package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.secureinfostore.model.Validation;
import com.secinfostore.secureinfostore.util.DataStore;
import com.secinfostore.secureinfostore.util.DatabaseHandler;
import com.secinfostore.secureinfostore.util.EncryptionDecryption;
import com.secinfostore.secureinfostore.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class EntryUIController {
    @FXML
    private PasswordField keyTextField;
    private KeyTextFieldSkin keyTextFieldSkin;

    @FXML
    private CheckBox revealKeyCheckBox;

    @FXML
    private CheckBox loadedCheckbox;

    @FXML
    private Button loadkeyBTN;

    @FXML
    private Button loaddatabaseBTN;

    @FXML
    private Button resetBTN;

    @FXML
    private Button settingsBTN;

    @FXML
    private Button createBTN;

    @FXML
    private Button proceedBTN;

    @FXML
    private void initialize() {
        keyTextFieldSkin = new KeyTextFieldSkin(keyTextField);
        keyTextField.setSkin(keyTextFieldSkin);
    }

    @FXML
    protected void buttonClick(ActionEvent event) {
        if (event.getSource().equals(loadkeyBTN)) {
            loadKeyFromFile();
        } else if (event.getSource().equals(loaddatabaseBTN)) {
            pointToDatabase();
        } else if (event.getSource().equals(resetBTN)) {
            reset();
        } else if (event.getSource().equals(settingsBTN)) {
            goToSettings();
        } else if (event.getSource().equals(createBTN)) {
            createNewDatabaseAndKey();
        } else if (event.getSource().equals(proceedBTN)) {
            goToMainUI(event);

        }
    }

    private void createNewDatabaseAndKey() {
        Stage stage = new Stage();

        DataStore dataStore = DataStore.getInstance();
        stage.setTitle((String) dataStore.getObject("default_title"));

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Key File", "*.key"));
        fileChooser.setInitialFileName("newKey.key");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File keyFile = fileChooser.showSaveDialog(stage);

        if (keyFile == null) {
            return;
        }

        SecretKey key;
        try {
            String keyFilePath = keyFile.getAbsolutePath();
            key = EncryptionDecryption.generateAESKey();
            EncryptionDecryption.saveKeyToFile(key, keyFilePath);
        } catch (NoSuchAlgorithmException e) {
            ErrorDialog.showErrorDialog(e, "Key Generation Exception", "Something went wrong saving the key");
            return;
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Key Write Error", "Something went wrong writing the key");
            return;
        }

        Stage databaseStage = new Stage();
        databaseStage.setTitle((String) dataStore.getObject("default_title"));

        FileChooser fileChooserDb = new FileChooser();
        fileChooserDb.getExtensionFilters().add(new FileChooser.ExtensionFilter("Database File", "*.db"));
        fileChooserDb.setInitialFileName("newDatabase.db");
        fileChooserDb.setInitialDirectory(new File(System.getProperty("user.dir")));
        File dbFile = fileChooserDb.showSaveDialog(databaseStage);

        if (dbFile == null) {
            return;
        }

        try {
            String dbFilePath = dbFile.getAbsolutePath();
            HibernateUtil hibernateUtil = HibernateUtil.getInstance(dbFilePath);
            DatabaseHandler.createValidation(key);
            hibernateUtil.shutdown();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Database Creation Error", "Something went wrong creating the database");
        }
    }

    private void pointToDatabase() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter keyFilter = new FileChooser.ExtensionFilter("Load Database", "*.db");
        fileChooser.getExtensionFilters().add(keyFilter);
        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String filepath = selectedFile.getAbsolutePath();
            DataStore dataStore = DataStore.getInstance();
            dataStore.insertObject("default_db", filepath);
            loadedCheckbox.setSelected(true);
        }
    }

    private void goToSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("SettingsUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            DataStore dataStore = DataStore.getInstance();
            String title = (String) dataStore.getObject("default_title");
            stage.setTitle(title);

            SettingsUIController controller = fxmlLoader.getController();
            controller.setSettingUIController(stage);

            stage.show();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML Loading Error", "Error loading Settings fxml");
        }
    }

    private void goToMainUI(ActionEvent event) {
        DataStore dataStore = DataStore.getInstance();
        String dbUrl = (String) dataStore.getObject("default_db");
        String keyBase64 = keyTextField.getText().trim();

        SecretKey defaultKey;
        try {
            defaultKey = EncryptionDecryption.convertStringToSecretKey(keyBase64);
            dataStore.insertObject("default_key", defaultKey);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Key Conversion Error", "Base64 key conversion to Secret key has failed");
            return;
        }



        if ((dbUrl == null)) {
            ErrorDialog.showErrorDialog(new Exception("URL Error"), "Database Connection Error", "Database has not been set");
            return;
        }

        if(dbUrl.equals("")){
            ErrorDialog.showErrorDialog(new Exception("URL Error"), "Database Connection Error", "Database has not been set");
            return;
        }

        File file = new File(dbUrl);
        if (!file.exists()) {
            ErrorDialog.showErrorDialog(new Exception("URL Error"), "Database Connection Error", "Database has not Been Created");
            return;
        }

        HibernateUtil hibernateUtil = HibernateUtil.getInstance((String) dataStore.getObject("default_db"));
        Optional<Validation> optionalValidation = DatabaseHandler.getValidation();
        if (optionalValidation.isPresent()) {
            Validation validation = optionalValidation.get();
            try {
                EncryptionDecryption.decryptAESGCM(validation.getTestText(), defaultKey);
            } catch (Exception e) {
                ErrorDialog.showErrorDialog(e, "Database Key Mismatch Error", "The key provided does not match the database validation");
                hibernateUtil.shutdown();
                return;
            }
        } else {
            ErrorDialog.showErrorDialog(new Exception("Validation does not exist for this db"), "Validation Existence Error", "Validation check has failed for this database");
            hibernateUtil.shutdown();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("MainUI.fxml"));
            Parent viewParent = fxmlLoader.load();
            Scene viewScene = new Scene(viewParent);
            Stage sourceWin = (Stage) ((Node) event.getSource()).getScene().getWindow();
            sourceWin.setScene(viewScene);

            MainUIController controller = fxmlLoader.getController();
            controller.setMainUIController();

            sourceWin.show();

        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML Loading Error", "Error loading Main UI");
        }
    }

    private void reset() {
        DataStore dataStore = DataStore.getInstance();
        dataStore.insertObject("default_db", "");
        loadedCheckbox.setSelected(false);
        revealKeyCheckBox.setSelected(false);
        keyTextField.setText("");
        checkBoxReveal();
    }

    @FXML
    protected void checkBoxReveal() {
        keyTextFieldSkin.setReveal(revealKeyCheckBox.isSelected());
        keyTextField.setText(keyTextField.getText());
    }

    private void loadKeyFromFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter keyFilter = new FileChooser.ExtensionFilter("Load Key", "*.key");
        fileChooser.getExtensionFilters().add(keyFilter);
        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            try {
                SecretKey key = EncryptionDecryption.loadKeyFromFile(filePath);
                String base64key = EncryptionDecryption.keyToBase64Text(key);
                keyTextField.setText(base64key);
            } catch (Exception e) {
                ErrorDialog.showErrorDialog(new Exception("Key loading error"), "Key Load error", "Error loading key");
            }
        }
    }
}