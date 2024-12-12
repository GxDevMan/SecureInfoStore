package com.secinfostore.controller.components;

import com.secinfostore.controller.interfaces.AddUpdateContract;
import com.secinfostore.model.InformationFactory;
import com.secinfostore.model.TextObj;
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

        addUpdateBTN.setText("Add");
    }

    public void setAddUpdateTextEntryUIController(Stage stage, AddUpdateContract contract, TextObj textObj) {
        this.contract = contract;
        this.stage = stage;
        this.textObj = textObj;

        titleTextField.setText(textObj.getTextTitle());
        textEntryArea.setText(textObj.getTextInformation());
        tagsEntryArea.setText(textObj.getTags());
        addUpdateBTN.setText("Update");
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(addUpdateBTN)) {
            addRecordToDB();
        } else if (event.getSource().equals(cancelBTN)) {
            stage.close();
        }
    }

    private void addRecordToDB() {

        String title = titleTextField.getText().trim();
        String textEntry = textEntryArea.getText().trim();
        String tagsEntry = tagsEntryArea.getText().trim();

        TextObj entityToSave = null;

        if (this.textObj == null){
            entityToSave = InformationFactory.newTextEntry(title,textEntry,tagsEntry);
        } else {
            this.textObj.setTextTitle(title);
            this.textObj.setTextInformation(textEntry);
            this.textObj.setTags(tagsEntry);
            entityToSave = this.textObj;
        }

        this.contract.saveEntityToDB(entityToSave);
        this.stage.close();
    }
}
