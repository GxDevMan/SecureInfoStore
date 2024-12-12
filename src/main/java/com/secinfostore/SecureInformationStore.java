package com.secinfostore;
import com.secinfostore.controller.AppMediator;
import com.secinfostore.util.ConfigHandler;
import com.secinfostore.util.DataStore;
import com.secinfostore.util.EncryptionDecryption;
import com.secinfostore.util.HibernateUtil;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SecureInformationStore extends Application {
    @Override
    public void start(Stage stage) {
        Image icon = new Image((SecureInformationStore.class.getResource("/com/secinfostore/ico/shield.png").toExternalForm()));
        DataStore dataStore = DataStore.getInstance();
        dataStore.insertObject("default_icon", icon);

        stage.getIcons().add(icon);
        AppMediator mediator = new AppMediator(stage);
        mediator.registerFXMLName("entryUI", "EntryUI.fxml");
        mediator.registerFXMLName("mainUIAccount", "MainUIAccounts.fxml");
        mediator.registerFXMLName("textUIAccount","MainUIText.fxml");
        mediator.registerFXMLName("changeLogUI", "ChangeLogUI.fxml");
        mediator.switchTo("entryUI", null);
    }

    public static void main(String[] args) {
        initializationCode();
        launch();
    }

    public static void initializationCode() {
        DataStore dataStore = DataStore.getInstance();
        Map<String, String> config = ConfigHandler.getConfig();
        dataStore.insertObject("default_passwordCharSet", config.get("default_passwordCharSet"));
        dataStore.insertObject("default_title", "Secure Information Store");
    }

    public void stop() {
        HibernateUtil util = HibernateUtil.getInstance();
        if (util != null) {
            util.shutdown();
        }
        DataStore dataStore = DataStore.getInstance();

        if (dataStore != null) {
            SecretKey key = (SecretKey) dataStore.getObject("default_key");
            if (key != null)
                EncryptionDecryption.securelyClearKey(key);

            for (int i = 0; i < 5; i++) {
                try {
                    key = EncryptionDecryption.generateAESKey();
                } catch (NoSuchAlgorithmException e) {
                }
                dataStore.insertObject("default_key", key);
            }
            dataStore.clearData();
            dataStore.DestroyStore();
        }
    }
}