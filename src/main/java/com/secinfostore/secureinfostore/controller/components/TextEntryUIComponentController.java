package com.secinfostore.secureinfostore.controller.components;

import com.secinfostore.secureinfostore.controller.interfaces.UpdateDeleteViewConfirmContract;
import com.secinfostore.secureinfostore.model.TextObjDTO;
import com.secinfostore.secureinfostore.util.TimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Date;

public class TextEntryUIComponentController {
    private TextObjDTO textObj;
    private UpdateDeleteViewConfirmContract contract;

    @FXML
    private Label timeLBL;

    @FXML
    private Button viewBTN;

    @FXML
    private Button deleteBTN;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea tagsTextArea;

    public void setTextEntryUIComponentController(UpdateDeleteViewConfirmContract contract, TextObjDTO textObj) {
        this.contract = contract;
        this.textObj = textObj;
        this.titleTextField.setText(textObj.getTextTitle().trim());
        this.tagsTextArea.setText(textObj.getTags().trim());
        Date timeDate = textObj.getTimeModified();

        this.tagsTextArea.setCache(false);
        this.titleTextField.setCache(false);

        String timeDateStr = String.format("%s %s",
                TimeFormatter.getFormattedDate(timeDate),
                TimeFormatter.formatNumTime(timeDate));
        this.timeLBL.setText(String.format(timeLBL.getText(), timeDateStr));
    }

    public void buttonClick(ActionEvent event) {
        if (event.getSource().equals(viewBTN)) {
            contract.viewUpdateObj(textObj);
        } else if (event.getSource().equals(deleteBTN)) {
            contract.confirmDeleteObj(textObj);
        }
    }
}
