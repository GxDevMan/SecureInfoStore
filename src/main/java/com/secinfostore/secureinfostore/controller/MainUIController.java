package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.util.ImageNormalizer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainUIController {

    @FXML
    private StackPane passwordgenPositionSTACKP;


    public void setMainUIController() {
        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("PasswordgenComponent.fxml"));
            VBox passwordgenComponent = loader.load();
            passwordgenPositionSTACKP.getChildren().add(passwordgenComponent);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e,"FXML Loading Error", "Error loading password generation utility");
        }
    }
}
