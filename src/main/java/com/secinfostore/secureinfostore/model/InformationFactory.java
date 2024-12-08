package com.secinfostore.secureinfostore.model;

import com.secinfostore.secureinfostore.util.DataStore;
import com.secinfostore.secureinfostore.util.EncryptionDecryption;
import com.secinfostore.secureinfostore.util.ImageConversion;
import com.secinfostore.secureinfostore.util.ImageNormalizer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.crypto.SecretKey;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class InformationFactory {
    public static AccountObj newAccount(String platformName, String userName, String email, String password, Image thumbImage) {
        SecretKey key = getKey();

        byte[] thumbByte = null;
        if (thumbImage != null) {
            BufferedImage thumBfr = SwingFXUtils.fromFXImage(thumbImage, null);
            thumBfr = ImageNormalizer.normalizedImage(thumBfr, 215, 215);
            thumbImage = SwingFXUtils.toFXImage(thumBfr, null);
            thumbByte = ImageConversion.convertImageToByteArray(thumbImage);
        }

        try {
            userName = EncryptionDecryption.encryptAESGCM(userName, key);
            email = EncryptionDecryption.encryptAESGCM(email, key);
            password = EncryptionDecryption.encryptAESGCM(password, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new AccountObj(platformName, userName, email, password, thumbByte);
    }

    public static AccountObj updateAccount(AccountObj accountObj) {
        SecretKey key = getKey();

        try {
            accountObj.setEmail(EncryptionDecryption.encryptAESGCM(accountObj.getEmail(), key));
            accountObj.setPassword(EncryptionDecryption.encryptAESGCM(accountObj.getPassword(), key));
            accountObj.setUserName(EncryptionDecryption.encryptAESGCM(accountObj.getUserName(), key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accountObj;
    }

    public static AccountObj decAccount(AccountObj accountObj) {
        SecretKey key = getKey();
        try {
            accountObj.setUserName(EncryptionDecryption.decryptAESGCM(accountObj.getUserName(), key));
            accountObj.setEmail(EncryptionDecryption.decryptAESGCM(accountObj.getEmail(), key));
            accountObj.setPassword(EncryptionDecryption.decryptAESGCM(accountObj.getPassword(), key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accountObj;
    }

    public static ChangeLogObj decChangeLog(ChangeLogObj changeLogObj) {
        SecretKey key = getKey();
        try {
            changeLogObj.setUserName(EncryptionDecryption.decryptAESGCM(changeLogObj.getUserName(), key));
            changeLogObj.setEmail(EncryptionDecryption.decryptAESGCM(changeLogObj.getEmail(), key));
            changeLogObj.setPassword(EncryptionDecryption.decryptAESGCM(changeLogObj.getPassword(), key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return changeLogObj;
    }

    public static ChangeLogObj newChangeLog(AccountObj account) {
        return new ChangeLogObj(account, getNowDate(), account.getPlatformName(), account.getUserName(), account.getEmail(), account.getPassword());
    }

    public static TextObj newTextEntry(String textTitle, String textInformation) {
        return new TextObj(getNowDate(), textTitle, textInformation);
    }

    public static TextObj updateTextEntry(TextObj textObj){
        textObj.setTimeModified(getNowDate());
        return textObj;
    }

    private static SecretKey getKey() {
        DataStore dataStore = DataStore.getInstance();
        SecretKey key = (SecretKey) dataStore.getObject("default_key");
        return key;
    }

    private static java.sql.Timestamp getNowDate(){
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        java.sql.Timestamp date = java.sql.Timestamp.valueOf(localDateTime);
        return date;
    }
}
