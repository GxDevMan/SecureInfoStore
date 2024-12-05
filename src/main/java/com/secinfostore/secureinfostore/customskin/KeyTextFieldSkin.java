package com.secinfostore.secureinfostore.customskin;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

public class KeyTextFieldSkin extends TextFieldSkin {

    private char charMask = '\u2023';
    private boolean reveal = false;


    public KeyTextFieldSkin(TextField textField){
        super(textField);
    }

    @Override
    protected String maskText(String txt){
        if(!reveal) {
            if (getSkinnable() instanceof PasswordField) {
                int wordLength = txt.length();
                StringBuilder maskBuilder = new StringBuilder(wordLength);
                for (int i = 0; i < wordLength; i++) {
                    maskBuilder.append(charMask);
                }
                return maskBuilder.toString();
            } else {
                return txt;
            }
        } else{
            return txt;
        }
    }

    public void setCharMask(char charMask){
        this.charMask = charMask;
    }

    public boolean isReveal() {
        return reveal;
    }

    public void setReveal(boolean reveal) {
        this.reveal = reveal;
    }
}
