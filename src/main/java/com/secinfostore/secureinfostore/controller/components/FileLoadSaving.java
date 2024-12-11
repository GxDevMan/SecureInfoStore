package com.secinfostore.secureinfostore.controller.components;

import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.model.InformationFactory;
import com.secinfostore.secureinfostore.util.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class FileLoadSaving {

    public static File loadedJsonConfig() {
        Stage stage = new Stage();
        ComponentFactory.setWindowsTitle(stage);

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("Import Accounts", "*.json");
        fileChooser.getExtensionFilters().add(jsonFilter);

        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));
        ComponentFactory.setStageIcon(stage);

        File selectedJSON = fileChooser.showOpenDialog(stage);
        return selectedJSON;
    }

    public static void exportToFileFormat(Optional<List<AccountObj>> accountObjListOptional, boolean json) throws Exception {
        if (!accountObjListOptional.isPresent()) {
            ErrorDialog.showErrorDialog(new Exception("Empty List"), "Accounts Export Error", "No Accounts to Export");
            return;
        }

        Boolean exportEnc = ComponentFactory.showExportDialog(json);
        if(exportEnc == null)
            return;

        List<AccountObj> accountList = accountObjListOptional.get();

        Stage stage = new Stage();
        ComponentFactory.setWindowsTitle(stage, "Export Accounts to Json");
        ComponentFactory.setStageIcon(stage);

        String fileFormat = json ? "Json" : "Txt";
        String fileExtension = json ? "json" : "txt";

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(String.format("%s Export File", fileFormat), String.format("*.%s",fileExtension)));
        fileChooser.setInitialFileName(String.format("ExportedAccounts.%s",fileExtension));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showSaveDialog(stage);

        if (file == null)
            return;

        if(exportEnc){
            for(AccountObj decAccount : accountObjListOptional.get()){
                InformationFactory.updateAccount(decAccount);
            }
        }

        if(json){
            DataExporterHandler.writeToJsonToFile(accountList,file);
        } else{
            DataExporterHandler.writeAccountsToTextFile(accountList,file);
        }

    }

    public static Optional<SecretKey> loadKey() throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter keyFilter = new FileChooser.ExtensionFilter("Load Key", "*.key");
        fileChooser.getExtensionFilters().add(keyFilter);
        String currentDir = System.getProperty("user.dir");
        fileChooser.setInitialDirectory(new File(currentDir));

        File selectedFile = fileChooser.showOpenDialog(null);
        SecretKey key;
        if (selectedFile != null) {
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
