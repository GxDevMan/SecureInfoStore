package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.secureinfostore.model.Validation;
import com.secinfostore.secureinfostore.util.DataStore;
import com.secinfostore.secureinfostore.util.DatabaseHandler;
import com.secinfostore.secureinfostore.util.EncryptionDecryption;
import com.secinfostore.secureinfostore.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class EntryUIController extends BaseController {
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
    private Button encryptorBTN;

    @FXML
    private void initialize() {
        keyTextFieldSkin = new KeyTextFieldSkin(keyTextField);
        keyTextField.setSkin(keyTextFieldSkin);
    }

    @Override
    public void setupSelectedController(Object data) {
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
        } else if (event.getSource().equals(encryptorBTN)) {
            goToEncryptor();
        }
    }

    @FXML
    protected void checkBoxReveal() {
        keyTextFieldSkin.setReveal(revealKeyCheckBox.isSelected());
        keyTextField.setText(keyTextField.getText());
    }

    private void createNewDatabaseAndKey() {
        Optional<SecretKey> keyOptional;
        try {
            keyOptional = FileLoadSaving.createKeyFile();
        } catch (NoSuchAlgorithmException e) {
            ErrorDialog.showErrorDialog(e, "Key Generation Exception", "Something went wrong saving the key");
            return;
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Key Write Error", "Something went wrong writing the key");
            return;
        }
        if(keyOptional.isEmpty()){
            return;
        }
        boolean dbCreated = FileLoadSaving.createDatabase(keyOptional.get());
        if (!dbCreated) {
            ErrorDialog.showErrorDialog(new Exception("Database Creation Error"), "Database Creation Error", "Something went wrong in the creation of the database");
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

    private void loadKeyFromFile() {
        try {
            Optional<SecretKey> keyOptional = FileLoadSaving.loadKey();
            if (keyOptional.isEmpty()) {
                ErrorDialog.showErrorDialog(new Exception("Null Key"), "Key Load Key", "Key is Null");
                return;
            }
            String base64Key = EncryptionDecryption.keyToBase64Text(keyOptional.get());
            keyTextField.setText(base64Key);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Key Load error", "Error loading key");
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

    private void goToSettings() {
        try {
            ComponentFactory.settingsDisplay();
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

        if (dbUrl.equals("")) {
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
        this.mediator.switchTo("mainUIAccount", null);

    }

    private void goToEncryptor() {
        try {
            ComponentFactory.loadEncryptor();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading TextENCDECUI.fxml");
        }
    }
}