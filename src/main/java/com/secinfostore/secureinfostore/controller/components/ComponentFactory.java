package com.secinfostore.secureinfostore.controller.components;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.controller.interfaces.AddUpdateContract;
import com.secinfostore.secureinfostore.controller.interfaces.UpdateDeleteViewConfirmContract;
import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.model.TextObj;
import com.secinfostore.secureinfostore.model.TextObjDTO;
import com.secinfostore.secureinfostore.util.DataStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.util.Optional;

public class ComponentFactory {

    public static AnchorPane textPreviewComponent(UpdateDeleteViewConfirmContract contract, TextObjDTO textObj) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/TextEntryPreviewComponent.fxml"));
        AnchorPane anchorPane = loader.load();

        TextEntryUIComponentController controller = loader.getController();
        controller.setTextEntryUIComponentController(contract, textObj);

        return anchorPane;
    }

    public static void addUpdateAccountUI(AddUpdateContract contract) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/AddUpdateAccountUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "New Account Information");
        setStageIcon(stage);
        stage.initModality(Modality.APPLICATION_MODAL);

        AddUpdateAccountController controller = loader.getController();
        controller.setAddUpdateAccount(stage, contract);
        stage.show();
    }

    public static void addUpdateAccountUI(AccountObj account, AddUpdateContract contract) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/AddUpdateAccountUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "Update Account Information");
        setStageIcon(stage);
        stage.initModality(Modality.APPLICATION_MODAL);

        AddUpdateAccountController controller = loader.getController();
        controller.setAddUpdateAccount(stage, contract, account);
        stage.show();
    }

    public static void addUpdateTextUI(AddUpdateContract contract) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/AddUpdateTextEntryUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);

        setWindowsTitle(stage, "New Text Entry Information");
        setStageIcon(stage);
        stage.initModality(Modality.APPLICATION_MODAL);

        AddUpdateTextEntryUIController controller = loader.getController();
        controller.setAddUpdateTextEntryUIController(stage, contract);
        stage.show();
    }

    public static void addUpdateTextUI(AddUpdateContract contract, TextObj textObj) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/AddUpdateTextEntryUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);

        setWindowsTitle(stage, "Update Text Entry Information");
        setStageIcon(stage);
        stage.initModality(Modality.WINDOW_MODAL);

        AddUpdateTextEntryUIController controller = loader.getController();
        controller.setAddUpdateTextEntryUIController(stage, contract, textObj);
        stage.show();
    }

    public static void loadEncryptor() throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/TextENCDECUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "Encryptor");
        setStageIcon(stage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

    }

    public static void loadEncryptor(SecretKey key) throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/TextENCDECUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "Encryptor");
        setStageIcon(stage);
        stage.initModality(Modality.WINDOW_MODAL);

        TextENCDECController controller = loader.getController();
        controller.setTextENCDECController(key);
        stage.show();
    }

    public static void loadReEncryptionWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/ReEncryptionUI.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setScene(scene);
        setWindowsTitle(stage, "Re Encrypting");
        setStageIcon(stage);
        stage.initModality(Modality.APPLICATION_MODAL);

        ReEncryptionController controller = loader.getController();
        stage.show();
        controller.setReEncryptionController(stage);
    }

    public static void settingsDisplay() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("Components/SettingsUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        setWindowsTitle(stage, "Settings");
        setStageIcon(stage);

        SettingsUIController controller = fxmlLoader.getController();
        controller.setSettingUIController(stage);

        stage.show();
    }

    public static void settingsDisplaywithCharset() throws Exception {
        DataStore dataStore = DataStore.getInstance();
        String charSet = (String) dataStore.getObject("default_passwordCharSet");
        FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("Components/SettingsUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        setWindowsTitle(stage, "Settings");
        setStageIcon(stage);

        SettingsUIController controller = fxmlLoader.getController();
        controller.setSettingUIController(stage, charSet);

        stage.show();
    }

    public static boolean confirmAccountDeletion(AccountObj account) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this account?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(SecureInformationStore.class.getResource("styles/dark-theme.css").toExternalForm());
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        setStageIcon(stage);

        VBox vbox = new VBox();
        vbox.setSpacing(10);

        Label accountNameLabel = new Label("Account User Name: " + account.getUserName());
        Label accountEmailLabel = new Label("Account Email: " + account.getEmail());
        vbox.getChildren().addAll(accountNameLabel, accountEmailLabel);

        alert.getDialogPane().setContent(vbox);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.OK;
    }

    public static boolean confirmReEncryption() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Re Encryption");
        alert.setHeaderText("Are you sure you want to Re Encrypt the entire Database?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(SecureInformationStore.class.getResource("styles/dark-theme.css").toExternalForm());
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        setStageIcon(stage);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.OK;
    }

    public static boolean confirmTextEntryDeletion(TextObjDTO textObjDTO) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this Text Entry?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(SecureInformationStore.class.getResource("styles/dark-theme.css").toExternalForm());
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        setStageIcon(stage);

        VBox vbox = new VBox();
        vbox.setSpacing(10);

        Label accountNameLabel = new Label("Text Title: " + textObjDTO.getTextTitle());
        vbox.getChildren().addAll(accountNameLabel);

        alert.getDialogPane().setContent(vbox);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.OK;
    }

    public static Boolean showExportDialog(boolean json) {
        String fileFormat = json ? "Json" : "Txt";
        String encryptedOption = "Encrypted";
        String unencryptedOption = "Unencrypted";

        ChoiceDialog<String> dialog = new ChoiceDialog<>(encryptedOption, encryptedOption, unencryptedOption);

        dialog.setTitle("Export Options");
        dialog.setHeaderText("Choose Export Type");
        dialog.setContentText("How would you like to export the data?");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(SecureInformationStore.class.getResource("styles/dark-theme.css").toExternalForm());

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        setWindowsTitle(stage, String.format("Exporting via %s", fileFormat));
        setStageIcon(stage);

        Optional<String> result = dialog.showAndWait();

        return result.map(choice -> choice.equals(encryptedOption)).orElse(null);
    }

    public static VBox getPasswordComponent() throws Exception {
        FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("Components/PasswordgenComponent.fxml"));
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

    public static void setStageIcon(Stage stage) {
        DataStore dataStore = DataStore.getInstance();
        Image icon = (Image) dataStore.getObject("default_icon");
        stage.getIcons().add(icon);
    }
}
