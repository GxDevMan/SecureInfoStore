package com.secinfostore.controller.components;

import com.secinfostore.controller.interfaces.UpdateDeleteViewConfirmContract;
import com.secinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.model.AccountObj;
import com.secinfostore.util.ClipboardHandler;
import com.secinfostore.util.ImageConversion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;

public class AccountsPreviewComponentController {
    private AccountObj account;
    private UpdateDeleteViewConfirmContract contract;

    @FXML
    private Label platformLBL;

    @FXML
    private Button copyUsernameBTN;

    @FXML
    private Label userNameLBL;

    @FXML
    private Button copyEmailBTN;

    @FXML
    private Label emailLBL;

    @FXML
    private Button copyPasswordBTN;

    @FXML
    private PasswordField passwordTxtField;
    private KeyTextFieldSkin passwordSkin;

    @FXML
    private CheckBox revealPassCheckBox;

    @FXML
    private Button updateBTN;

    @FXML
    private Button deleteBTN;

    @FXML
    private ImageView platformLogoView;

    public void initialize() {
        passwordSkin = new KeyTextFieldSkin(passwordTxtField);
        passwordTxtField.setSkin(passwordSkin);
    }

    public void setAccountPreview(AccountObj accountObj, UpdateDeleteViewConfirmContract contract) {
        this.account = accountObj;
        this.contract = contract;
        platformLBL.setText(account.getPlatformName());
        userNameLBL.setText(account.getUserName());
        passwordTxtField.setText(account.getPassword());
        emailLBL.setText(account.getEmail());

        if (account.getPlatformThumbnail() != null) {
            platformLogoView.setImage(ImageConversion.byteArraytoImage(account.getPlatformThumbnail()));
        }
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(copyEmailBTN)) {
            ClipboardHandler.pasteTextToClipboard(emailLBL.getText());
        } else if (event.getSource().equals(copyUsernameBTN)) {
            ClipboardHandler.pasteTextToClipboard(userNameLBL.getText());
        } else if (event.getSource().equals(copyPasswordBTN)) {
            ClipboardHandler.pasteTextToClipboard(passwordTxtField.getText());
        } else if (event.getSource().equals(updateBTN)) {
            this.contract.viewUpdateObj(this.account);
        } else if (event.getSource().equals(deleteBTN)) {
            this.contract.confirmDeleteObj(this.account);
        }
    }

    public void revealPass() {
        passwordSkin.setReveal(revealPassCheckBox.isSelected());
        passwordTxtField.setText(passwordTxtField.getText());
    }
}
