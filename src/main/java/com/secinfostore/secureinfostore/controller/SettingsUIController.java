package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.model.CharSet;
import com.secinfostore.secureinfostore.util.ConfigHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SettingsUIController {
    private Stage stage;

    @FXML
    private TextField textFieldDatabaseLoc;

    @FXML
    private TextField charsetField;

    @FXML
    private Button setDBLocBTN;

    @FXML
    private Button saveBTN;

    @FXML
    private Button cancelBTN;

    @FXML
    private Button lowercaseBTN;

    @FXML
    private Button uppercaseBTN;

    @FXML
    private Button numbersBTN;

    @FXML
    private Button specialcharBTN;

    @FXML
    private Button clearcharBTN;

    @FXML
    protected void buttonClick(ActionEvent event) {
        if (event.getSource().equals(setDBLocBTN)) {
            setDefaultLoc();
        } else if (event.getSource().equals(saveBTN)) {
            saveConfig();
        } else if (event.getSource().equals(cancelBTN)) {
            stage.close();
        } else if (event.getSource().equals(lowercaseBTN)) {
            charsetField.setText(charsetField.getText() + CharSet.LOWERCASE.getCharset());
        } else if (event.getSource().equals(uppercaseBTN)) {
            charsetField.setText(charsetField.getText() + CharSet.UPPERCASE.getCharset());
        } else if (event.getSource().equals(numbersBTN)) {
            charsetField.setText(charsetField.getText() + CharSet.NUMBERS.getCharset());
        } else if (event.getSource().equals(specialcharBTN)){
            charsetField.setText(charsetField.getText() + CharSet.SPECIALCHAR.getCharset());
        } else if (event.getSource().equals(clearcharBTN)){
            charsetField.setText("");
        }

    }

    public void setSettingUIController(Stage stage) {
        this.stage = stage;
        Map<String, String> config = ConfigHandler.getConfig();
        textFieldDatabaseLoc.setText(config.get("default_db"));
        charsetField.setText(config.get("default_passwordCharSet"));
    }

    private void setDefaultLoc() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter keyFilter = new FileChooser.ExtensionFilter("Select Database", "*.db");
        fileChooser.getExtensionFilters().add(keyFilter);
        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String absolutePath = selectedFile.getAbsolutePath();
            textFieldDatabaseLoc.setText(absolutePath);
        }
    }

    private void saveConfig() {
        Map<String, String> newConfig = new HashMap<>();
        newConfig.put("default_db", textFieldDatabaseLoc.getText().trim());
        newConfig.put("default_passwordCharSet", charsetField.getText().trim());
        ConfigHandler.createCustomConfigFile(newConfig);
        stage.close();
    }
}
