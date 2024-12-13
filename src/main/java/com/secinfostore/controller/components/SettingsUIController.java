package com.secinfostore.controller.components;

import com.secinfostore.model.CharSet;
import com.secinfostore.util.ConfigHandler;
import com.secinfostore.util.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private Spinner<Integer> pageSizeSPN;

    @FXML
    private ChoiceBox<String> choiceBoxSort;

    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        pageSizeSPN.setValueFactory(valueFactory);
        pageSizeSPN.setEditable(true);
        addIntegerValidation(pageSizeSPN,1,Integer.MAX_VALUE);

        ObservableList<String> sortingChoices = FXCollections.observableArrayList("Ascending", "Descending");
        choiceBoxSort.setItems(sortingChoices);
        choiceBoxSort.setValue("Descending");
    }

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
        pageSizeSPN.getValueFactory().setValue(Integer.parseInt(config.get("default_pagesize")));
        choiceBoxSort.setValue(config.get("default_sorting"));
    }

    public void setSettingUIController(Stage stage, String instanceCharset) {
        isSet = true;
        this.stage = stage;
        charsetField.setText(instanceCharset);

        Map<String, String> config = ConfigHandler.getConfig();
        charsetField.setText(config.get("default_passwordCharSet"));
        pageSizeSPN.getValueFactory().setValue(Integer.parseInt(config.get("default_pagesize")));
        choiceBoxSort.setValue(config.get("default_sorting"));
    }

    private void saveConfig() {
        Map<String, String> newConfig = new HashMap<>();
        String defaultCharSet = charsetField.getText().trim();
        int defaultpageSize = pageSizeSPN.getValue();
        String defaultSorting = choiceBoxSort.getValue();

        newConfig.put("default_passwordCharSet", defaultCharSet);
        newConfig.put("default_pagesize", String.valueOf(defaultpageSize));
        newConfig.put("default_sorting", defaultSorting.trim());
        ConfigHandler.createCustomConfigFile(newConfig);

        if(isSet){
            DataStore dataStore = DataStore.getInstance();
            dataStore.insertObject("default_passwordCharSet", defaultCharSet);
            dataStore.insertObject("default_pagesize", defaultpageSize);
            dataStore.insertObject("default_sorting", defaultSorting);
        }

        stage.close();
    }

    private void addIntegerValidation(Spinner<Integer> spinner, int min, int max) {
        TextField editor = spinner.getEditor();
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            editor.setText(String.valueOf(newValue));
        });

        editor.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                editor.setText(oldText);
            } else {
                try {
                    int value = Integer.parseInt(newText);
                    if (value < min || value > max) {
                        spinner.getValueFactory().setValue(value < min ? min : max);
                    } else {
                        spinner.getValueFactory().setValue(value);
                    }
                } catch (NumberFormatException e) {
                    spinner.getValueFactory().setValue(min);
                }
            }
        });
    }
}
