package com.secinfostore.secureinfostore;

import com.secinfostore.secureinfostore.controller.AddUpdateAccountController;
import com.secinfostore.secureinfostore.controller.EntryUIController;
import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.model.Validation;
import com.secinfostore.secureinfostore.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SecureInformationStore extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SecureInformationStore.class.getResource("AddUpdateAccountUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);

        AccountObj objtest = new AccountObj("sample platform name", "sample username","sample email","sample password",null);

        AddUpdateAccountController controller = fxmlLoader.getController();
        controller.setAddUpdateAccount(objtest,stage);

        DataStore dataStore = DataStore.getInstance();
        dataStore.insertObject("default_title", "Secure Information Store");
        stage.setTitle("Secure Information Store");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        testCode();
        launch();
    }

    public static void testCode(){
        DataStore dataStore = DataStore.getInstance();
        Map<String, String> config = ConfigHandler.getConfig();
        dataStore.insertObject("default_db", config.get("default_db"));
        dataStore.insertObject("default_passwordCharSet", config.get("default_passwordCharSet"));
        String dbUrl = (String) dataStore.getObject("default_db");
//        HibernateUtil.getInstance(dbUrl);
//
//        Validation newValidation = new Validation("sampleText");
//        SecretKey key;
//        try {
//            key = EncryptionDecryption.generateAESKey();
//            newValidation.setTestText(EncryptionDecryption.encryptAESGCM(newValidation.getTestText(),key));
//
//            String currentDir = System.getProperty("user.dir");
//            String filePath = currentDir + File.separator + "testKey.key";
//            EncryptionDecryption.saveKeyToFile(key,filePath);
//
//            String password = PassGenerator.generatePassword(config.get("default_passwordCharSet"), 10);
//            System.out.println(String.format("Password: %s", password));
//
//            DatabaseHandler.saveValidation(newValidation);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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