package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.util.DataStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.crypto.SecretKey;

public class ComponentFactory {

    public static AnchorPane accountPreviewComponent(AccountObj account, UpdateDeleteViewConfirmContract contract) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AccountsPreviewComponent.fxml"));
        AnchorPane anchorPane = loader.load();

        AccountsPreviewComponentController controller = loader.getController();
        controller.setAccountPreview(account, contract);

        return anchorPane;
    }

    public static void addUpdateAccountUI(AddUpdateContract contract) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AddUpdateAccountUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "New Account Information");
        stage.initModality(Modality.APPLICATION_MODAL);

        AddUpdateAccountController controller = loader.getController();
        controller.setAddUpdateAccount(stage, contract);
        stage.show();
    }

    public static void addUpdateAccountUI(AccountObj account, AddUpdateContract contract) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AddUpdateAccountUI.fxml"));
        Scene scene = new Scene(loader.load(), 400, 400);
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "Update Account Information");
        stage.initModality(Modality.APPLICATION_MODAL);

        AddUpdateAccountController controller = loader.getController();
        controller.setAddUpdateAccount(stage, contract, account);
        stage.show();
    }

    public static void loadEncryptor() throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("TextENCDECUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "Encryptor");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

    }

    public static void loadEncryptor(SecretKey key) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("TextENCDECUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "Encryptor");
        stage.initModality(Modality.WINDOW_MODAL);

        TextENCDECController controller = loader.getController();
        controller.setTextENCDECController(key);

        stage.show();
    }

    public static void settingsDisplay() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("SettingsUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        setWindowsTitle(stage);

        SettingsUIController controller = fxmlLoader.getController();
        controller.setSettingUIController(stage);

        stage.show();
    }

    public static void settingsDisplaywithCharset() throws Exception {
        DataStore dataStore = DataStore.getInstance();
        String charSet = (String) dataStore.getObject("default_passwordCharSet");
        FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("SettingsUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        setWindowsTitle(stage);

        SettingsUIController controller = fxmlLoader.getController();
        controller.setSettingUIController(stage, charSet);

        stage.show();
    }

    public static VBox getPasswordComponent() throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("PasswordgenComponent.fxml"));
        VBox passwordgenComponent = loader.load();
        return passwordgenComponent;
    }

    public static void setWindowsTitle(Stage stage) {
        DataStore dataStore = DataStore.getInstance();
        String title = (String) dataStore.getObject("default_title");
        stage.setTitle(title);
    }

    public static void setWindowsTitle(Stage stage, String additional) {
        DataStore dataStore = DataStore.getInstance();
        String title = (String) dataStore.getObject("default_title");
        title = String.format("%s - %s", title, additional);
        stage.setTitle(title);
    }
}
