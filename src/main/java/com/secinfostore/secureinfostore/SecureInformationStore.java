package com.secinfostore.secureinfostore;

import com.secinfostore.secureinfostore.model.Validation;
import com.secinfostore.secureinfostore.util.ConfigHandler;
import com.secinfostore.secureinfostore.util.DataStore;
import com.secinfostore.secureinfostore.util.DatabaseHandler;
import com.secinfostore.secureinfostore.util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class SecureInformationStore extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Secure Information Store");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        DataStore dataStore = DataStore.getInstance();
        Map<String, String> config = ConfigHandler.getConfig();
        dataStore.insertObject("default_db", config.get("default_db"));
        dataStore.insertObject("default_password", config.get("default_passwordCharSet"));
        String dbUrl = (String) dataStore.getObject("default_db");
        HibernateUtil hibernateUtil = HibernateUtil.getInstance(dbUrl);

        Validation newValidation = new Validation("sampleText");
        DatabaseHandler.saveValidation(newValidation);
        launch();
    }

    public void stop(){
        HibernateUtil util = HibernateUtil.getInstance();
        if(util != null){
            util.shutdown();
        }
        DataStore dataStore = DataStore.getInstance();
        if(dataStore != null){
            dataStore.clearData();
            dataStore.DestroyStore();
        }
    }
}