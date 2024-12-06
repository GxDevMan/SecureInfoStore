package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.customskin.KeyTextFieldSkin;
import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.util.ClipboardHandler;
import com.secinfostore.secureinfostore.util.ImageConversion;
import com.secinfostore.secureinfostore.util.ImageNormalizer;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AddUpdateAccountController {
    private AccountObj accountObj;
    private Stage stage;

    @FXML
    private TextField platformTxtField;

    @FXML
    private TextField usernameTxtField;

    @FXML
    private TextField emailTxtField;

    @FXML
    private PasswordField passwordField;
    private KeyTextFieldSkin passwordSkin;

    @FXML
    private CheckBox revealCheckbox;

    @FXML
    private Button pastecopiedBTN;

    @FXML
    private Button pasteimageBTN;

    @FXML
    private Button selectimageBTN;

    @FXML
    private ImageView thumbnailImage;

    @FXML
    private Button addUpdateBTN;

    @FXML
    private Button cancelBTN;

    @FXML
    private StackPane passwordgenSTACKP;


    public void initialize() {
        passwordSkin = new KeyTextFieldSkin(passwordField);
        passwordField.setSkin(passwordSkin);
    }

    public void setAddUpdateAccount(Stage stage) {
        this.stage = stage;
        addUpdateBTN.setText("Add");
        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("PasswordgenComponent.fxml"));
            VBox passwordgenComponent = loader.load();
            passwordgenSTACKP.getChildren().add(passwordgenComponent);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML Loading Error", "Error loading password generation utility");
        }
    }

    public void setAddUpdateAccount(AccountObj account, Stage stage) {
        this.stage = stage;
        accountObj = account;
        platformTxtField.setText(accountObj.getPlatformName());
        usernameTxtField.setText(accountObj.getUserName());
        emailTxtField.setText(account.getEmail());
        passwordField.setText(accountObj.getPassword());
        addUpdateBTN.setText("Update");

        byte[] thumbnailByte = accountObj.getPlatformThumbnail();
        if (thumbnailByte != null) {
            Image image = ImageConversion.byteArraytoImage(thumbnailByte);
            thumbnailImage.setImage(image);
        }

        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("PasswordgenComponent.fxml"));
            VBox passwordgenComponent = loader.load();
            passwordgenSTACKP.getChildren().add(passwordgenComponent);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML Loading Error", "Error loading password generation utility");
        }
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(pastecopiedBTN)) {
            passwordField.setText(ClipboardHandler.getTextFromClipboard());
        } else if (event.getSource().equals(pasteimageBTN)) {
            setThumbnailImageviaClipboard();
        } else if (event.getSource().equals(selectimageBTN)) {
            setThumbnailImageviaFile();
        } else if (event.getSource().equals(addUpdateBTN)) {
        } else if (event.getSource().equals(cancelBTN)) {
            stage.close();
        }
    }

    public void revealPassword() {
        passwordSkin.setReveal(revealCheckbox.isSelected());
        passwordField.setText(passwordField.getText());
    }

    private void setThumbnailImageviaClipboard() {
        Image clipboardImage = ClipboardHandler.getImageFromClipboard();
        setTheImage(clipboardImage);
    }


    private void setThumbnailImageviaFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Thumbnail Image", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);

        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        File selectedImageThumbnailFile = fileChooser.showOpenDialog(null);

        if (selectedImageThumbnailFile != null) {
            Image selectedImage = new Image(selectedImageThumbnailFile.toURI().toString());
            setTheImage(selectedImage);
        }
    }

    private void setTheImage(Image image) {
        if(image == null)
            return;

        BufferedImage bfrImage = SwingFXUtils.fromFXImage(image, null);
        bfrImage = ImageNormalizer.normalizedImage(bfrImage, 215, 215);
        try {
            image = ImageConversion.convertBufferedImageToImage(bfrImage);
        } catch (IOException e) {
            ErrorDialog.showErrorDialog(e, "Image Converstion Failure", "There was a problem converting Buffered Image to JavaFX Image");
            return;
        }
        thumbnailImage.setImage(image);
    }
}