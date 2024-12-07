package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.util.DataStore;
import com.secinfostore.secureinfostore.util.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class MainUIController implements AddUpdateContract, UpdateDeleteViewConfirmContract {

    @FXML
    private Button searchBTN;

    @FXML
    private Button refreshBTN;

    @FXML
    private Button clearsearchBTN;

    @FXML
    private Button addAccountBTN;

    @FXML
    private Button reEncryptBTN;

    @FXML
    private Button goToEntryUIBTN;

    @FXML
    private TextField searchTxtField;

    @FXML
    private StackPane passwordgenPositionSTACKP;

    @FXML
    private TilePane accountsViewTilePane;


    public void setMainUIController() {
        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("PasswordgenComponent.fxml"));
            VBox passwordgenComponent = loader.load();
            passwordgenPositionSTACKP.getChildren().add(passwordgenComponent);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML Loading Error", "Error loading password generation utility");
        }
        displayAccounts(DatabaseHandler.getAccounts());
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(addAccountBTN)) {
            addAccount();
        } else if (event.getSource().equals(refreshBTN)) {
            displayAccounts(DatabaseHandler.getAccounts());
        } else if (event.getSource().equals(clearsearchBTN)) {

        } else if (event.getSource().equals(searchBTN)) {

        }
    }

    private void addAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AddUpdateAccountUI.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            Stage stage = new Stage();

            DataStore dataStore = DataStore.getInstance();
            String title = (String) dataStore.getObject("default_title");
            stage.setScene(scene);
            stage.setTitle(String.format("%s - %s", title, "New Account Information"));
            stage.initModality(Modality.APPLICATION_MODAL);

            AddUpdateAccountController controller = loader.getController();
            controller.setAddUpdateAccount(stage, this);
            stage.show();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading AddUpdateAccountUI.fxml");
        }
    }

    private void displayAccounts(Optional<List<AccountObj>> accountObjList) {
        if(accountObjList.isEmpty())
            return;

        if(accountObjList.get().isEmpty())
            return;

        accountsViewTilePane.getChildren().clear();
        try {
            for (AccountObj account : accountObjList.get()) {
                FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AccountsPreviewComponent.fxml"));
                AnchorPane anchorPane = loader.load();

                AccountsPreviewComponentController controller = loader.getController();
                controller.setAccountPreview(account, this);

                accountsViewTilePane.getChildren().add(anchorPane);
            }
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Accounts Loading Error", "There was an error displaying the Error");
        }
    }

    @Override
    public void saveAccountToDB(AccountObj account) {
        DatabaseHandler.saveAccount(account);
        displayAccounts(DatabaseHandler.getAccounts());
    }


    @Override
    public void viewUpdateAccount(AccountObj account) {
        try {
            FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource("AddUpdateAccountUI.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            Stage stage = new Stage();

            DataStore dataStore = DataStore.getInstance();
            String title = (String) dataStore.getObject("default_title");
            stage.setScene(scene);
            stage.setTitle(String.format("%s - %s", title, "Update Account Information"));
            stage.initModality(Modality.APPLICATION_MODAL);

            AddUpdateAccountController controller = loader.getController();
            controller.setAddUpdateAccount(stage, this, account);
            stage.show();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "FXML loading error", "There was an error loading AddUpdateAccountUI.fxml");
        }
    }

    @Override
    public void confirmDeleteAccount(AccountObj account) {
        System.out.println("DELETING ATTEMPT");
    }
}
