package com.secinfostore.controller.components;

import com.secinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.util.*;
import io.nayuki.qrcodegen.QrCode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
    private Button qrBTN;

    @FXML
    private Button clearBTN;

    @FXML
    private Spinner<Integer> passwordLengthSPN;

    @FXML
    private ProgressBar passwordStrengthBar;

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
            keyBoardAction();
        } else if (event.getSource().equals(copyPasswordBTN)) {
            ClipboardHandler.pasteTextToClipboard(genpasswordTxtBox.getText());
        } else if (event.getSource().equals(qrBTN)) {
            generateQR();
        } else if (event.getSource().equals(clearBTN)) {
            genpasswordTxtBox.setText("");
            keyBoardAction();
        }
    }


    public void keyBoardAction() {
        String genPass = genpasswordTxtBox.getText().trim();
        double evaluation = evaluatePasswordStrength(genPass);
        updateProgressBar(evaluation);
    }

    private void generateQR() {
        try {
            ComponentFactory.displayQRCode(genpasswordTxtBox.getText().trim());
        } catch (IOException e) {
            ErrorDialog.showErrorDialog(e, "Error making QR CODE", "Password QR Code generation error");
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "Error Loading Password QR View");
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

    private void updateProgressBar(double strength) {
        passwordStrengthBar.setProgress(strength);
        String color;
        if (strength < 0.3) {
            color = "#FF0000";
        } else if (strength < 0.7) {
            color = "#FFA500";
        } else {
            color = "#00FF00";
        }
        passwordStrengthBar.setStyle(String.format("-fx-background-color: %s;", color));
    }

    public static double evaluatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0.0;
        }

        int length = password.length();
        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        // Check for various character types
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLowercase = true;
            else if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        int charTypeCount = 0;
        if (hasLowercase) charTypeCount++;
        if (hasUppercase) charTypeCount++;
        if (hasDigit) charTypeCount++;
        if (hasSpecial) charTypeCount++;

        double lengthScore = Math.min(1.0, length / 12.0);
        double diversityScore = charTypeCount / 4.0;

        String lowerPassword = password.toLowerCase();
        boolean hasCommonPatterns = lowerPassword.contains("password") ||
                lowerPassword.contains("12345") ||
                lowerPassword.contains("qwerty") ||
                lowerPassword.contains("abc");

        double penalty = hasCommonPatterns ? 0.2 : 0.0;
        double finalScore = (lengthScore * 0.5 + diversityScore * 0.5) - penalty;
        return Math.max(0.0, Math.min(finalScore, 1.0));
    }
}
