package com.secinfostore.controller.components;

import com.secinfostore.controller.interfaces.ProgressObserver;
import com.secinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.util.ClipboardHandler;
import com.secinfostore.util.EncryptionDecryption;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class EncDecUIController implements ProgressObserver {
    //TAB TEXT ENCRYPTOR
    @FXML
    private Button loadKeyTextBTN;

    @FXML
    private Button computeOutputTextBTN;

    @FXML
    private Button copyOutputTextBTN;

    @FXML
    private Button pasteTextBTN;

    @FXML
    private Button createKeyTextBTN;

    @FXML
    private Button qrInputTextBTN;

    @FXML
    private Button qrOutputTextBTN;

    @FXML
    private PasswordField keyFieldTxtField;
    private KeyTextFieldSkin keyTextFieldSkin;

    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private CheckBox decTextCheckbox;

    @FXML
    private CheckBox encTextCheckbox;

    @FXML
    private CheckBox singleBlockTextCheckBox;

    @FXML
    private CheckBox multiBlockTextCheckBox;

    @FXML
    private CheckBox revealKeyTextCheckBox;


    //TAB FILE ENCRYPTOR
    @FXML
    private Button createKeyFileBTN;

    @FXML
    private Button loadKeyFileBTN;

    @FXML
    private Button setFileDestinationBTN;

    @FXML
    private Button loadFilePointerBTN;

    @FXML
    private Button startencdecFileBTN;

    @FXML
    private Button cancelEncDecBTN;

    @FXML
    private Label encdecFileLBL;

    @FXML
    private TextField filePointerTextField;

    @FXML
    private TextField destinationPointerFileTextField;

    @FXML
    private PasswordField keyFieldTxtFieldFile;
    private KeyTextFieldSkin keyFieldTxtFieldFileSkin;

    @FXML
    private CheckBox revealCheckboxFile;

    @FXML
    private CheckBox decFileCheckBox;

    @FXML
    private CheckBox encFileCheckbox;

    @FXML
    private CheckBox sameSourceFileCheckbox;

    @FXML
    private ProgressBar fileEncryptionProgress;

    @FXML
    private Label statusFILELBL;

    private Thread encryptionThread;

    public void initialize() {
        //TAB TEXT ENCRYPTOR
        keyTextFieldSkin = new KeyTextFieldSkin(keyFieldTxtField);
        keyFieldTxtField.setSkin(keyTextFieldSkin);

        computeOutputTextBTN.setText("Decrypt");
        decTextCheckbox.setSelected(true);
        encTextCheckbox.setSelected(false);

        singleBlockTextCheckBox.setSelected(true);
        multiBlockTextCheckBox.setSelected(false);

        //TAB FILE ENCRYPTOR
        keyFieldTxtFieldFileSkin = new KeyTextFieldSkin(keyFieldTxtFieldFile);
        keyFieldTxtFieldFile.setSkin(keyFieldTxtFieldFileSkin);

        startencdecFileBTN.setText("Start Encryption");
        encdecFileLBL.setText("Encryption Progress: ");
        decFileCheckBox.setSelected(false);
        encFileCheckbox.setSelected(true);

        sameSourceFileCheckbox.setSelected(true);
        destinationPointerFileTextField.setText("");

        cancelEncDecBTN.setDisable(true);
    }

    public void setTextENCDECController(SecretKey key) {
        try {
            String keyBase64 = EncryptionDecryption.keyToBase64Text(key);
            keyFieldTxtField.setText(keyBase64);
            keyFieldTxtFieldFile.setText(keyBase64);
        } catch (Exception e) {
        }
    }

    public void textbuttonClick(ActionEvent event) {
        if (event.getSource().equals(loadKeyTextBTN)) {
            keyFieldTxtField.setText(loadKeyFromFile());
        } else if (event.getSource().equals(computeOutputTextBTN)) {
            computeOutput();
        } else if (event.getSource().equals(copyOutputTextBTN)) {
            ClipboardHandler.pasteTextToClipboard(outputTextArea.getText().trim());
        } else if (event.getSource().equals(pasteTextBTN)) {
            inputTextArea.setText(ClipboardHandler.getTextFromClipboard());
        } else if (event.getSource().equals(createKeyTextBTN)) {
            keyFieldTxtField.setText(createKeyAction());
        } else if (event.getSource().equals(qrInputTextBTN)) {
            qrCodeDisp(inputTextArea.getText().trim());
        } else if (event.getSource().equals(qrOutputTextBTN)) {
            qrCodeDisp(outputTextArea.getText().trim());
        }
    }

    public void checkBoxTextHandle(ActionEvent event) {
        if (event.getSource().equals(decTextCheckbox)) {
            computeOutputTextBTN.setText("Decrypt");
            decTextCheckbox.setSelected(true);
            encTextCheckbox.setSelected(false);
        } else if (event.getSource().equals(encTextCheckbox)) {
            computeOutputTextBTN.setText("Encrypt");
            decTextCheckbox.setSelected(false);
            encTextCheckbox.setSelected(true);
        } else if (event.getSource().equals(revealKeyTextCheckBox)) {
            keyTextFieldSkin.setReveal(revealKeyTextCheckBox.isSelected());
            keyFieldTxtField.setText(keyFieldTxtField.getText());
        } else if (event.getSource().equals(singleBlockTextCheckBox)) {
            singleBlockTextCheckBox.setSelected(true);
            multiBlockTextCheckBox.setSelected(false);
        } else if (event.getSource().equals(multiBlockTextCheckBox)) {
            singleBlockTextCheckBox.setSelected(false);
            multiBlockTextCheckBox.setSelected(true);
        }
    }

    public void filebuttonClick(ActionEvent event) {
        if (event.getSource().equals(createKeyFileBTN)) {
            keyFieldTxtFieldFile.setText(createKeyAction());
        } else if (event.getSource().equals(loadKeyFileBTN)) {
            keyFieldTxtFieldFile.setText(loadKeyFromFile());
        } else if (event.getSource().equals(loadFilePointerBTN)) {
            pointToFile();
        } else if (event.getSource().equals(setFileDestinationBTN)) {
            destinationPointerFileTextField.setText(pointToFolder());
        } else if (event.getSource().equals(startencdecFileBTN)) {
            startDecryptionEncryption();
        } else if (event.getSource().equals(cancelEncDecBTN)) {
            stopEncDecFile();
        }
    }

    public void checkboxFileHandle(ActionEvent event) {
        if (event.getSource().equals(decFileCheckBox)) {
            startencdecFileBTN.setText("Start Decryption: ");
            encdecFileLBL.setText("Decryption Progress: ");
            decFileCheckBox.setSelected(true);
            encFileCheckbox.setSelected(false);

        } else if (event.getSource().equals(encFileCheckbox)) {
            startencdecFileBTN.setText("Start Encryption: ");
            encdecFileLBL.setText("Encryption Progress: ");
            decFileCheckBox.setSelected(false);
            encFileCheckbox.setSelected(true);
        } else if (event.getSource().equals(revealCheckboxFile)) {
            keyFieldTxtFieldFileSkin.setReveal(revealCheckboxFile.isSelected());
            keyFieldTxtFieldFile.setText(keyFieldTxtFieldFile.getText());
        }
    }

    private void qrCodeDisp(String text) {
        try {
            ComponentFactory.displayQRCode(text);
        } catch (IOException e) {
            ErrorDialog.showErrorDialog(e, "Error making QR CODE", "Password QR Code generation error");
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "Error Loading Password QR View");
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

        boolean decrypt = decTextCheckbox.isSelected();
        boolean encrypt = encTextCheckbox.isSelected();

        boolean singleBlock = singleBlockTextCheckBox.isSelected();
        boolean multiBlock = multiBlockTextCheckBox.isSelected();

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

    private String singleBlockDec(String encText, SecretKey key) throws Exception {
        String decText = EncryptionDecryption.decryptAESGCM(encText.trim(), key);
        return decText;
    }

    private String singleBlockEnc(String decText, SecretKey key) throws Exception {
        String encText = EncryptionDecryption.encryptAESGCM(decText.trim(), key);
        return encText;
    }

    private String multiBlockEnc(String decText, SecretKey key) throws Exception {
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

    private String multiBlockDec(String encText, SecretKey key) throws Exception {
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

    private String createKeyAction() {
        try {
            Optional<SecretKey> keyOptional = FileLoadSaving.createKeyFile();
            if (keyOptional.isEmpty())
                return "";

            String base64Txt = EncryptionDecryption.keyToBase64Text(keyOptional.get());
            return base64Txt;
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Key Creation Error", "Error Creating AES KEY");
            return "";
        }
    }

    private String loadKeyFromFile() {
        try {
            Optional<SecretKey> keyOptional = FileLoadSaving.loadKey();
            if (keyOptional.isEmpty()) {
                ErrorDialog.showErrorDialog(new Exception("Null Key"), "Key Load Key", "Key is Null");
                return "";
            }
            String base64key = EncryptionDecryption.keyToBase64Text(keyOptional.get());
            return base64key;
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Key Load Error", "Error loading key");
            return "";
        }
    }

    private void pointToFile() {
        Optional<File> chosenFileOptional = FileLoadSaving.loadFileforEncDec();

        if (!chosenFileOptional.isPresent()) {
            filePointerTextField.setText("File is Not Present");
            return;
        }

        if (!chosenFileOptional.get().exists()) {
            filePointerTextField.setText("File is Not Present");
            return;
        }
        File chosenFile = chosenFileOptional.get();
        filePointerTextField.setText(chosenFile.getAbsolutePath());
    }

    private String pointToFolder() {
        Optional<File> chosenFolderOptional = FileLoadSaving.loadFolderforDestination();
        if (!chosenFolderOptional.isPresent())
            return "";

        if (!chosenFolderOptional.get().exists())
            return "";

        return chosenFolderOptional.get().getAbsolutePath();
    }

    private void startDecryptionEncryption() {
        boolean decryption = decFileCheckBox.isSelected();
        boolean encryption = encFileCheckbox.isSelected();
        boolean sameAsSource = sameSourceFileCheckbox.isSelected();

        if (encryption == decryption)
            return;

        if (filePointerTextField.getText().isEmpty()) {
            ErrorDialog.showErrorDialog(new Exception("Target File not Set"), "File not Set", "Please Select a File");
            return;
        }

        File targetFile = new File(filePointerTextField.getText().trim());
        if (!targetFile.exists()) {
            ErrorDialog.showErrorDialog(new Exception("Target File does not Exist"), "File does not exist", "Please Select a File");
            return;
        }


        String fileDestination;
        if (sameAsSource) {
            int fileSeparatorIndex = targetFile.getAbsolutePath().lastIndexOf('\\');
            fileDestination = targetFile.getAbsolutePath().substring(0, fileSeparatorIndex + 1);
        } else {
            if (destinationPointerFileTextField.getText().isEmpty()) {
                ErrorDialog.showErrorDialog(new Exception("Destination Not Set"), "Empty Destination", "Please Set a Destination folder");
                return;
            }

            fileDestination = destinationPointerFileTextField.getText().trim();
            File fileDirectory = new File(fileDestination);
            if (!fileDirectory.exists()) {
                ErrorDialog.showErrorDialog(new Exception("Directory does not exist"), "Directory Does not Exist!", "Please select an Existing Directory");
                return;
            }
        }

        if (keyFieldTxtFieldFile.getText().isEmpty()) {
            ErrorDialog.showErrorDialog(new Exception("Empty Key"), "Null Key", "Empty Key");
            return;
        }

        SecretKey key;
        try {
            key = EncryptionDecryption.convertStringToSecretKey(keyFieldTxtFieldFile.getText().trim());
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Error Converting base64 to key", "Key Conversion Error");
            return;
        }

        startencdecFileBTN.setDisable(true);
        cancelEncDecBTN.setDisable(false);
        encFileCheckbox.setDisable(true);
        decFileCheckBox.setDisable(true);


        encryptionThread = new Thread(() -> {
            try {
                if (decryption) {
                    EncryptionDecryption.decryptFileInChunksAESGCM(targetFile, fileDestination, key, this, () -> Thread.currentThread().isInterrupted());
                } else {
                    EncryptionDecryption.encryptFileInChunksAESGCM(targetFile, fileDestination, key, this, () -> Thread.currentThread().isInterrupted());
                }
                System.gc();
                Platform.runLater(() ->
                {
                    startencdecFileBTN.setDisable(false);
                    cancelEncDecBTN.setDisable(true);
                    encFileCheckbox.setDisable(false);
                    decFileCheckBox.setDisable(false);
                });
            } catch (Exception e) {
                System.gc();
                Platform.runLater(() ->
                {
                    startencdecFileBTN.setDisable(false);
                    cancelEncDecBTN.setDisable(true);
                    encFileCheckbox.setDisable(false);
                    decFileCheckBox.setDisable(false);
                });
            }
        });

        encryptionThread.start();
    }

    public void stopEncDecFile() {
        if (this.encryptionThread == null)
            return;

        encryptionThread.interrupt();
    }

    @Override
    public void updateProgress(double progress) {
        Platform.runLater(() -> {
            fileEncryptionProgress.setProgress(progress);
        });
    }

    @Override
    public void updateStatus(String status) {
        Platform.runLater(() -> {
            statusFILELBL.setText(String.format("Status: %s", status));
        });
    }
}
