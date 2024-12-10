package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.model.TextObj;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUpdateTextEntryUIController {
    private TextObj textObj;
    private AddUpdateContract contract;
    private Stage stage;

    @FXML
    private Button addUpdateBTN;

    @FXML
    private Button cancelBTN;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea textEntryArea;

    @FXML
    private TextArea tagsEntryArea;



    public void setAddUpdateTextEntryUIController(Stage stage, AddUpdateContract contract) {
        this.contract = contract;
        this.stage = stage;
    }

    public void setAddUpdateTextEntryUIController(Stage stage, AddUpdateContract contract, TextObj textObj) {
        this.contract = contract;
        this.stage = stage;
        this.textObj = textObj;

        titleTextField.setText(textObj.getTextTitle());
        textEntryArea.setText(textObj.getTextInformation());
        tagsEntryArea.setText(textObj.getTags());
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(addUpdateBTN)){
            System.out.println("Adding");
        } else if (event.getSource().equals(cancelBTN)){
            stage.close();
        }
    }
}
