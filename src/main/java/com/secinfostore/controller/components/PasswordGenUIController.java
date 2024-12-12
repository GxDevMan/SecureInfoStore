package com.secinfostore.controller.components;

import com.secinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.util.ClipboardHandler;
import com.secinfostore.util.DataStore;
import com.secinfostore.util.PassGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PasswordGenUIController {

    @FXML
    private PasswordField genpasswordTxtBox;

    @FXML
    private CheckBox revealCheckBox;
    private KeyTextFieldSkin keyTextFieldSkin;

    @FXML
    private Button generatePassBTN;

    @FXML
    private Button copyPasswordBTN;

    @FXML
    private Spinner<Integer> passwordLengthSPN;


    @FXML
    private void initialize() {
        keyTextFieldSkin = new KeyTextFieldSkin(genpasswordTxtBox);
        genpasswordTxtBox.setSkin(keyTextFieldSkin);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, Integer.MAX_VALUE, 8);
        passwordLengthSPN.setValueFactory(valueFactory);
        passwordLengthSPN.setEditable(false);
    }


    @FXML
    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(generatePassBTN)) {
            generatePass();
        } else if (event.getSource().equals(copyPasswordBTN)) {
            ClipboardHandler.pasteTextToClipboard(genpasswordTxtBox.getText());
        }
    }

    private void generatePass() {
        DataStore dataStore = DataStore.getInstance();
        String generatedPass = PassGenerator.generatePassword(
                (String) dataStore.getObject("default_passwordCharSet")
                , passwordLengthSPN.getValue());
        genpasswordTxtBox.setText(generatedPass);
    }

    public void revealGeneratedPassword(ActionEvent event) {
        keyTextFieldSkin.setReveal(revealCheckBox.isSelected());
        genpasswordTxtBox.setText(genpasswordTxtBox.getText());
    }
}
