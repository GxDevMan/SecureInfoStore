package com.secinfostore.secureinfostore.controller.components;

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
    private CheckBox singleBlockCheckBox;

    @FXML
    private CheckBox multiBlockCheckBox;

    @FXML
    private CheckBox revealKeyCheckBox;

    public void initialize() {
        keyTextFieldSkin = new KeyTextFieldSkin(keyFieldTxtField);
        keyFieldTxtField.setSkin(keyTextFieldSkin);

        computeOutputBTN.setText("Decrypt");
        decCheckbox.setSelected(true);
        encCheckbox.setSelected(false);

        singleBlockCheckBox.setSelected(true);
        multiBlockCheckBox.setSelected(false);
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
        } else if (event.getSource().equals(singleBlockCheckBox)) {
            singleBlockCheckBox.setSelected(true);
            multiBlockCheckBox.setSelected(false);
        } else if (event.getSource().equals(multiBlockCheckBox)) {
            singleBlockCheckBox.setSelected(false);
            multiBlockCheckBox.setSelected(true);
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

        boolean singleBlock = singleBlockCheckBox.isSelected();
        boolean multiBlock = multiBlockCheckBox.isSelected();

        String inputText = inputTextArea.getText();
        String textToDisplay = "";

        try {
            if (decrypt ^ encrypt) {
                if (singleBlock ^ multiBlock) {
                    if (decrypt) {
                        textToDisplay = singleBlock ? singleBlockDec(inputText, key) : multiBlockDec(inputText, key);
                    } else {
                        textToDisplay = singleBlock ? singleBlockEnc(inputText, key) : multiBlockEnc(inputText, key);
                    }
                }
            }
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(new Exception("DEC/ENC failed"), "DEC/ENC Failed", "There was a problem performing this action");
            return;
        }
        outputTextArea.setText(textToDisplay);
    }

    private String singleBlockDec (String encText, SecretKey key) throws Exception {
        String decText = EncryptionDecryption.decryptAESGCM(encText.trim(), key);
        return decText;
    }

    private String singleBlockEnc (String decText, SecretKey key) throws Exception {
        String encText = EncryptionDecryption.encryptAESGCM(decText.trim(), key);
        return encText;
    }

    private String multiBlockEnc (String decText, SecretKey key) throws Exception {
        StringBuilder encryptedText = new StringBuilder();

        // Matches one or more blank or whitespace-only lines
        String[] paragraphs = decText.split("(\\r?\\n\\s*)+");
        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                String encParagraph = EncryptionDecryption.encryptAESGCM(paragraph.trim(), key);
                encryptedText.append(encParagraph).append("\n\n");
            }
        }
        return encryptedText.toString().trim();
    }

    private String multiBlockDec (String encText, SecretKey key) throws Exception {
        StringBuilder decryptedText = new StringBuilder();

        String[] encryptedParagraphs = encText.split("(\\r?\\n\\s*)+");
        for (String encParagraph : encryptedParagraphs) {
            if (!encParagraph.trim().isEmpty()) {
                String decParagraph = EncryptionDecryption.decryptAESGCM(encParagraph.trim(), key);
                decryptedText.append(decParagraph).append("\n\n");
            }
        }

        return decryptedText.toString().trim();
    }
}
