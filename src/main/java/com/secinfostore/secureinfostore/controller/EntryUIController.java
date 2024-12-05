package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.secureinfostore.util.DataStore;
import com.secinfostore.secureinfostore.util.EncryptionDecryption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        } else if (event.getSource().equals(settingsBTN)){
            goToSettings();
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
            Scene scene = new Scene(fxmlLoader.load(), 500,300);

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
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }
}