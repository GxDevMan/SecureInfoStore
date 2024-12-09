package com.secinfostore.secureinfostore;

import com.secinfostore.secureinfostore.controller.AppMediator;
import com.secinfostore.secureinfostore.controller.ChangeLogController;
import com.secinfostore.secureinfostore.controller.EntryUIController;
import com.secinfostore.secureinfostore.controller.MainUIController;
import com.secinfostore.secureinfostore.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SecureInformationStore extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AppMediator mediator = new AppMediator(stage);
        mediator.registerController("entryUI", new EntryUIController());
        mediator.registerController("mainUIAccount", new MainUIController());
        mediator.registerController("changeLogUI", new ChangeLogController());
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
            for (int i = 0; i < 5; i++) {
                SecretKey key = null;
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