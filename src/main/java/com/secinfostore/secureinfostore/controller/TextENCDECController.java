package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.secureinfostore.util.ClipboardHandler;
import com.secinfostore.secureinfostore.util.EncryptionDecryption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.crypto.SecretKey;
import java.util.Optional;

public class TextENCDECController {

    @FXML
    private Button loadKeyBTN;

    @FXML
    private Button computeOutputBTN;

    @FXML
    private Button copyOutputBTN;

    @FXML
    private Button pasteBTN;

    @FXML
    private Button createKeyBTN;

    @FXML
    private PasswordField keyFieldTxtField;
    private KeyTextFieldSkin keyTextFieldSkin;

    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private CheckBox decCheckbox;

    @FXML
    private CheckBox encCheckbox;

    @FXML
    private CheckBox revealKeyCheckBox;

    public void initialize() {
        keyTextFieldSkin = new KeyTextFieldSkin(keyFieldTxtField);
        keyFieldTxtField.setSkin(keyTextFieldSkin);

        computeOutputBTN.setText("Decrypt");
        decCheckbox.setSelected(true);
        encCheckbox.setSelected(false);
    }

    public void setTextENCDECController(SecretKey key) {
        try {
            keyFieldTxtField.setText(EncryptionDecryption.keyToBase64Text(key));
        } catch (Exception e) {
        }
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(loadKeyBTN)) {
            loadKeyFromFile();
        } else if (event.getSource().equals(computeOutputBTN)) {
            computeOutput();
        } else if (event.getSource().equals(copyOutputBTN)) {
            ClipboardHandler.pasteTextToClipboard(outputTextArea.getText().trim());
        } else if (event.getSource().equals(pasteBTN)) {
            inputTextArea.setText(ClipboardHandler.getTextFromClipboard());
        } else if (event.getSource().equals(createKeyBTN)) {
            createKeyAction();
        }
    }

    private void createKeyAction() {
        try {
            Optional<SecretKey> keyOptional = FileLoadSaving.createKeyFile();
            if(keyOptional.isEmpty())
                return;

            String base64Txt = EncryptionDecryption.keyToBase64Text(keyOptional.get());
            keyFieldTxtField.setText(base64Txt);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Key Creation Error","Error Creating AES KEY");
        }
    }

    private void loadKeyFromFile() {
        try {
            Optional<SecretKey> keyOptional = FileLoadSaving.loadKey();
            if (keyOptional.isEmpty()) {
                ErrorDialog.showErrorDialog(new Exception("Null Key"), "Key Load Key", "Key is Null");
                return;
            }
            String base64key = EncryptionDecryption.keyToBase64Text(keyOptional.get());
            keyFieldTxtField.setText(base64key);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Key Load Error", "Error loading key");
        }
    }

    public void checkBoxHandle(ActionEvent event) {
        if (event.getSource().equals(decCheckbox)) {
            computeOutputBTN.setText("Decrypt");
            decCheckbox.setSelected(true);
            encCheckbox.setSelected(false);
        } else if (event.getSource().equals(encCheckbox)) {
            computeOutputBTN.setText("Encrypt");
            decCheckbox.setSelected(false);
            encCheckbox.setSelected(true);
        } else if (event.getSource().equals(revealKeyCheckBox)) {
            keyTextFieldSkin.setReveal(revealKeyCheckBox.isSelected());
            keyFieldTxtField.setText(keyFieldTxtField.getText());
        }
    }

    private void computeOutput() {
        SecretKey key;
        try {
            key = EncryptionDecryption.convertStringToSecretKey(keyFieldTxtField.getText().trim());
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Load Key Error", "There was an error converting the base 64 key to key");
            return;
        }

        boolean decrypt = decCheckbox.isSelected();
        boolean encrypt = encCheckbox.isSelected();

        String textToDisplay = "";

        try {
            if (decrypt && !encrypt) {
                String encText = inputTextArea.getText().trim();
                String decText = EncryptionDecryption.decryptAESGCM(encText, key);
                textToDisplay = decText;
            }
            if (!decrypt && encrypt) {
                String baseTxt = inputTextArea.getText().trim();
                String encText = EncryptionDecryption.encryptAESGCM(baseTxt, key);
                textToDisplay = encText;
            }
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(new Exception("DEC/ENC failed"), "DEC/ENC Failed", "There was a problem performing this action");
            return;
        }
        outputTextArea.setText(textToDisplay);
    }
}
