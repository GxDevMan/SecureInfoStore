package com.secinfostore.secureinfostore.controller.components;

import com.secinfostore.secureinfostore.model.CharSet;
import com.secinfostore.secureinfostore.util.ConfigHandler;
import com.secinfostore.secureinfostore.util.DataStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class SettingsUIController {
    private boolean isSet;
    private Stage stage;

    @FXML
    private TextField charsetField;

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
         if (event.getSource().equals(saveBTN)) {
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
        isSet = false;
        this.stage = stage;
        Map<String, String> config = ConfigHandler.getConfig();
        charsetField.setText(config.get("default_passwordCharSet"));
    }

    public void setSettingUIController(Stage stage, String instanceCharset) {
        isSet = true;
        this.stage = stage;
        charsetField.setText(instanceCharset);
    }

    private void saveConfig() {
        Map<String, String> newConfig = new HashMap<>();
        newConfig.put("default_passwordCharSet", charsetField.getText().trim());
        ConfigHandler.createCustomConfigFile(newConfig);

        if(isSet){
            DataStore dataStore = DataStore.getInstance();
            dataStore.insertObject("default_passwordCharSet", charsetField.getText().trim());
        }

        stage.close();
    }
}
