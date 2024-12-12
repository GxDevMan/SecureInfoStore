package com.secinfostore.controller.components;

import com.secinfostore.controller.interfaces.ProgressObserver;
import com.secinfostore.util.DatabaseHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.util.Optional;

public class ReEncryptionController implements ProgressObserver {
    private Stage stage;

    @FXML
    private Label statusLBL;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button closeBTN;

    public void setReEncryptionController(Stage stage) {
        this.stage = stage;
        Optional<SecretKey> newKeyOptional;
        try {
            newKeyOptional = FileLoadSaving.createKeyFile();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Error Creating Key", "Key Creation Error");
            this.stage.close();
            return;
        }

        if (!newKeyOptional.isPresent()) {
            ErrorDialog.showErrorDialog(new Exception("Key Creation error"), "Error Creating Key", "Key Creation Error");
            this.stage.close();
            return;
        }

        try {
            performReEncryption(newKeyOptional.get());
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, "Re Encryption error", "Error Re encrypting");
            this.stage.close();
        }
    }

    private void performReEncryption(SecretKey newKey) {
        Thread thread = new Thread(() -> {
           DatabaseHandler.reEncryption(this, newKey);
        });
        thread.setDaemon(true);
        thread.start();
    }


    @Override
    public void updateProgress(double progress) {
        Platform.runLater(() -> {
            progressBar.setProgress(progress);
            if (progress >= 1.0) {
                this.closeBTN.setDisable(false);
            }
        });
    }

    @Override
    public void updateStatus(String status) {
        Platform.runLater(() -> {
            statusLBL.setText(String.format("Status: %s", status));
        });

    }

    public void buttonClick(ActionEvent event) {
        stage.close();
    }
}
