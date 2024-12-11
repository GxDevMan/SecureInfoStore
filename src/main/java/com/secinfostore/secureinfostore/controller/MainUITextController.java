package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.controller.components.ComponentFactory;
import com.secinfostore.secureinfostore.controller.components.ErrorDialog;
import com.secinfostore.secureinfostore.controller.interfaces.AddUpdateContract;
import com.secinfostore.secureinfostore.controller.interfaces.UpdateDeleteViewConfirmContract;
import com.secinfostore.secureinfostore.model.TextObj;
import com.secinfostore.secureinfostore.model.TextObjDTO;
import com.secinfostore.secureinfostore.util.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public class MainUITextController extends BaseController implements AddUpdateContract<TextObj>, UpdateDeleteViewConfirmContract<TextObjDTO> {

    @FXML
    private Button goToEntryUIBTN;

    @FXML
    private Button goToAccountsBTN;

    @FXML
    private Button clearSearchBTN;

    @FXML
    private Button searchBTN;

    @FXML
    private Button addTextEntryBTN;

    @FXML
    private Button resetBTN;

    @FXML
    private Button reEncryptBTN;

    @FXML
    private TextField searchTagsTextField;

    @FXML
    private TilePane textEntriesTile;

    @FXML
    private ScrollPane scrollPane;


    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(goToEntryUIBTN)) {
            goToEntryUI();
        } else if (event.getSource().equals(goToAccountsBTN)) {
            mediator.switchTo("mainUIAccount", null);
        } else if (event.getSource().equals(addTextEntryBTN)) {
            addnewTextEntry();
        } else if (event.getSource().equals(searchBTN)) {
            searchEntryTags();
        } else if (event.getSource().equals(clearSearchBTN)) {
            searchTagsTextField.setText("");
        } else if (event.getSource().equals(resetBTN)) {
            resetEntriesList();
        }
    }

    private void addnewTextEntry() {
        try {
            ComponentFactory.addUpdateTextUI(this);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML load error", "Error loading add/update text UI");
        }
    }

    private void searchEntryTags() {
        String searchKey = searchTagsTextField.getText().trim();
        Thread thread = new Thread(() -> {
            Optional<List<TextObjDTO>> textObjListOptional = DatabaseHandler.getTextEntries(searchKey);
            Platform.runLater(() -> {
                displayTextEntries(textObjListOptional);
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void goToEntryUI() {
        HibernateUtil hibernateUtil = HibernateUtil.getInstance();
        hibernateUtil.shutdown();
        DataStore dataStore = DataStore.getInstance();

        for (int i = 0; i < 5; i++) {
            SecretKey key = null;
            try {
                key = EncryptionDecryption.generateAESKey();
            } catch (NoSuchAlgorithmException e) {
            }
            dataStore.insertObject("default_key", key);

        }
        dataStore.deleteObject("default_key");
        dataStore.deleteObject("default_db");
        mediator.switchTo("entryUI", null);
    }

    @Override
    public void setupSelectedController(Object data) {
        resetEntriesList();
    }

    @Override
    public void saveEntityToDB(TextObj textEntry) {
        Thread thread = new Thread(() -> {
            DatabaseHandler.saveTextEntry(textEntry);
            Platform.runLater(() -> {
                resetEntriesList();
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void viewUpdateObj(TextObjDTO textObj) {
        try {
            Optional<TextObj> textObjOptional = DatabaseHandler.getTextEntryById(textObj.getTextId());
            if (!textObjOptional.isPresent()) {
                return;
            }
            ComponentFactory.addUpdateTextUI(this, textObjOptional.get());
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showErrorDialog(e, "FXML Load Error", "Error loading update text UI");
        }
    }

    @Override
    public void confirmDeleteObj(TextObjDTO textEntryObj) {
        try {
            boolean deletion = ComponentFactory.confirmTextEntryDeletion(textEntryObj);
            if (!deletion)
                return;

            Optional<TextObj> realTextObjOptional = DatabaseHandler.getTextEntryById(textEntryObj.getTextId());
            if (!realTextObjOptional.isPresent())
                return;
            DatabaseHandler.deleteTextEntry(realTextObjOptional.get());
            resetEntriesList();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showErrorDialog(e, "Text Entry Deletion Error", "There was an error deleting this text entry");
        }
    }

    private void resetEntriesList() {
        Thread thread = new Thread(() -> {
            Optional<List<TextObjDTO>> textEntryListOptional = DatabaseHandler.getTextEntries();
            Platform.runLater(() -> {
                displayTextEntries(textEntryListOptional);
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void displayTextEntries(Optional<List<TextObjDTO>> textEntriesOptional) {

        textEntriesTile.getChildren().clear();
        if (!textEntriesOptional.isPresent())
            return;

        List<TextObjDTO> textEntryList = textEntriesOptional.get();
        try {
            for (TextObjDTO textEntry : textEntryList) {
                AnchorPane textEntryAnchor = ComponentFactory.textPreviewComponent(this, textEntry);
                textEntriesTile.getChildren().add(textEntryAnchor);
            }

            scrollPane.setCache(false);
            for (Node n : scrollPane.getChildrenUnmodifiable()) {
                n.setCache(false);
            }
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Text Entry Load Error", "Error loading text entries");
        }
    }
}
