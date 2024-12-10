package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.util.DatabaseHandler;
import com.secinfostore.secureinfostore.util.EncryptionDecryption;
import com.secinfostore.secureinfostore.util.HibernateUtil;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.Optional;

public class FileLoadSaving {

    public static Optional<SecretKey> loadKey() throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter keyFilter = new FileChooser.ExtensionFilter("Load Key", "*.key");
        fileChooser.getExtensionFilters().add(keyFilter);
        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        File selectedFile = fileChooser.showOpenDialog(null);
        SecretKey key;
        if(selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            key = EncryptionDecryption.loadKeyFromFile(filePath);
            return Optional.ofNullable(key);
        }
        return Optional.empty();
    }

    public static Optional<SecretKey> createKeyFile() throws Exception {
        Stage stage = new Stage();
        ComponentFactory.setWindowsTitle(stage);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Key File", "*.key"));
        fileChooser.setInitialFileName("newKey.key");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        File keyFile = fileChooser.showSaveDialog(stage);

        if (keyFile == null) {
            return Optional.empty();
        }

        String keyFilePath = keyFile.getAbsolutePath();
        SecretKey key = EncryptionDecryption.generateAESKey();
        EncryptionDecryption.saveKeyToFile(key, keyFilePath);
        return Optional.of(key);
    }

    public static boolean createDatabase(SecretKey key) {
        Stage databaseStage = new Stage();
        ComponentFactory.setWindowsTitle(databaseStage);

        FileChooser fileChooserDb = new FileChooser();
        fileChooserDb.getExtensionFilters().add(new FileChooser.ExtensionFilter("Database File", "*.db"));
        fileChooserDb.setInitialFileName("newDatabase.db");
        fileChooserDb.setInitialDirectory(new File(System.getProperty("user.dir")));
        File dbFile = fileChooserDb.showSaveDialog(databaseStage);

        if (dbFile == null) {
            return false;
        }

        String dbFilePath = dbFile.getAbsolutePath();
        HibernateUtil hibernateUtil = HibernateUtil.getInstance(dbFilePath);
        boolean dbCreated = DatabaseHandler.createValidation(key);
        hibernateUtil.shutdown();
        return dbCreated;
    }
}
