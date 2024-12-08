package com.secinfostore.secureinfostore;

import com.secinfostore.secureinfostore.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class SecureInformationStore extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("EntryUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);

        DataStore dataStore = DataStore.getInstance();
        dataStore.insertObject("default_title", "Secure Information Store");
        stage.setTitle("Secure Information Store");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        initializationCode();
        launch();
    }

    public static void initializationCode(){
        DataStore dataStore = DataStore.getInstance();
        Map<String, String> config = ConfigHandler.getConfig();
        dataStore.insertObject("default_passwordCharSet", config.get("default_passwordCharSet"));
    }

    public void stop() {
        HibernateUtil util = HibernateUtil.getInstance();
        if (util != null) {
            util.shutdown();
        }
        DataStore dataStore = DataStore.getInstance();
        if (dataStore != null) {
            dataStore.clearData();
            dataStore.DestroyStore();
        }
    }
}