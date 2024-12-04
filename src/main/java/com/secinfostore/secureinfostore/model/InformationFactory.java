package com.secinfostore.secureinfostore.model;

import com.secinfostore.secureinfostore.util.ImageConversion;
import com.secinfostore.secureinfostore.util.ImageNormalizer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.sql.Timestamp;

public class InformationFactory {
    public static AccountObj newAccount(String platformName, String userName, String email, String password, Image thumbImage){

        BufferedImage thumBfr = SwingFXUtils.fromFXImage(thumbImage, null);
        thumBfr = ImageNormalizer.normalizedImage(thumBfr,512,512);
        thumbImage = SwingFXUtils.toFXImage(thumBfr,null);
        byte[] thumbByte = ImageConversion.convertImageToByteArray(thumbImage);

        return new AccountObj(platformName,userName,email,password,thumbByte);
    }

    public static ChangeLogObj newChangeLog(AccountObj account, Timestamp timeChanged, String platformName, String userName, String email, String password){
        return new ChangeLogObj(account,timeChanged,platformName,userName,email,password);
    }

    public static TextObj newTextEntry(Timestamp timeModified, String textTitle, String textInformation){
        return new TextObj(timeModified,textTitle,textInformation);
    }
}
