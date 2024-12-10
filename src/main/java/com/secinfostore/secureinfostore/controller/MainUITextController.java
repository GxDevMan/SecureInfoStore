package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.model.TextObj;
import com.secinfostore.secureinfostore.util.DataStore;
import com.secinfostore.secureinfostore.util.EncryptionDecryption;
import com.secinfostore.secureinfostore.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class MainUITextController extends BaseController implements AddUpdateContract<TextObj> {

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
    private Button reEncryptBTN;

    @FXML
    private TextField searchTagsTextField;

    @FXML
    private TilePane textEntriesTile;

    public void buttonClick(ActionEvent event) {
        if(event.getSource().equals(goToEntryUIBTN)){
            goToEntryUI();
        } else if(event.getSource().equals(goToAccountsBTN)){
            mediator.switchTo("mainUIAccount", null);
        } else if(event.getSource().equals(addTextEntryBTN)){
            addnewTextEntry();
        }
    }

    private void addnewTextEntry(){
        try {
            ComponentFactory.addUpdateTextUI(this);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML load error", "Error loading add/update text UI");
        }
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
    }


    @Override
    public void saveAccountToDB(TextObj object) {

    }
}
