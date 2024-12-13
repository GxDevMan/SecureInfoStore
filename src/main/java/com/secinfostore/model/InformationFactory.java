package com.secinfostore.model;

import com.secinfostore.util.DataStore;
import com.secinfostore.util.EncryptionDecryption;
import com.secinfostore.util.ImageConversion;
import com.secinfostore.util.ImageNormalizer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.crypto.SecretKey;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.Timestamp;

public class InformationFactory {

    public static AccountObj newAccount(String platformName, String userName, String email, String password, Image thumbImage) {
        SecretKey key = getKey();

        byte[] thumbByte = null;
        if (thumbImage != null) {
            BufferedImage thumBfr = SwingFXUtils.fromFXImage(thumbImage, null);
            thumBfr = ImageNormalizer.normalizedImage(thumBfr, 100, 100);
            thumbImage = SwingFXUtils.toFXImage(thumBfr, null);
            thumbByte = ImageConversion.convertImageToByteArray(thumbImage);
        }

        try {
            userName = EncryptionDecryption.encryptAESGCM(userName, key);
            email = EncryptionDecryption.encryptAESGCM(email, key);
            password = EncryptionDecryption.encryptAESGCM(password, key);
        } catch (Exception e) {

        }

        return new AccountObj(platformName, userName, email, password, thumbByte);
    }

    public static AccountObj updateAccount(AccountObj accountObj) {
        SecretKey key = getKey();

        try {
            accountObj.setUserEmail(EncryptionDecryption.encryptAESGCM(accountObj.getUserEmail(), key));
            accountObj.setUserPassword(EncryptionDecryption.encryptAESGCM(accountObj.getUserPassword(), key));
            accountObj.setUserName(EncryptionDecryption.encryptAESGCM(accountObj.getUserName(), key));
        } catch (Exception e) {

        }
        return accountObj;
    }

    public static AccountObj decAccount(AccountObj accountObj) {
        SecretKey key = getKey();
        try {
            accountObj.setUserName(EncryptionDecryption.decryptAESGCM(accountObj.getUserName(), key));
            accountObj.setUserEmail(EncryptionDecryption.decryptAESGCM(accountObj.getUserEmail(), key));
            accountObj.setUserPassword(EncryptionDecryption.decryptAESGCM(accountObj.getUserPassword(), key));
        } catch (Exception e) {

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

        }
        return changeLogObj;
    }

    public static TextObj newTextEntry(String title, String textInfo, String tags) {
        SecretKey key = getKey();
        Timestamp nowTime = getNowDate();
        try {
            title = EncryptionDecryption.encryptAESGCM(title, key);
            textInfo = EncryptionDecryption.encryptAESGCM(textInfo, key);
            tags = tagEcbBlockENC(tags, key);
        } catch (Exception e) {

        }

        return new TextObj(nowTime, title, textInfo, tags);
    }

    public static TextObj decTextEntry(TextObj textObj) {
        SecretKey key = getKey();
        try {
            textObj.setTextInformation(EncryptionDecryption.decryptAESGCM(textObj.getTextInformation(), key));
            textObj.setTextTitle(EncryptionDecryption.decryptAESGCM(textObj.getTextTitle(), key));
            textObj.setTags(tagEcbBlockDEC(textObj.getTags(), key));
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return textObj;
    }

    public static TextObjDTO decPreviewTextEntry(TextObjDTO textObj) {
        SecretKey key = getKey();
        try {
            textObj.setTextTitle(EncryptionDecryption.decryptAESGCM(textObj.getTextTitle(), key));

            if (textObj.getTags() != null) {
                textObj.setTags(tagEcbBlockDEC(textObj.getTags(), key));
            }

        } catch (Exception e) {
        }
        return textObj;
    }

    public static TextObj updateTextEntry(TextObj textObj) {
        SecretKey key = getKey();
        textObj.setTimeModified(getNowDate());
        try {
            textObj.setTextTitle(EncryptionDecryption.encryptAESGCM(textObj.getTextTitle(), key));
            textObj.setTextInformation(EncryptionDecryption.encryptAESGCM(textObj.getTextInformation(), key));
            textObj.setTags(tagEcbBlockENC(textObj.getTags(), key));
        } catch (Exception e) {

        }
        return textObj;
    }

    public static AccountObj reEncryptAccountObj(AccountObj accountObj, SecretKey newKey) {
        try {
            accountObj.setUserName(EncryptionDecryption.encryptAESGCM(accountObj.getUserName(), newKey));
            accountObj.setUserPassword(EncryptionDecryption.encryptAESGCM(accountObj.getUserPassword(), newKey));
            accountObj.setUserEmail(EncryptionDecryption.encryptAESGCM(accountObj.getUserEmail(), newKey));
        } catch (Exception e) {

        }
        return accountObj;
    }


    public static ChangeLogObj reEncryptChangeLog(ChangeLogObj changeLogObj, SecretKey newKey) {
        try {
            changeLogObj.setUserName(EncryptionDecryption.encryptAESGCM(changeLogObj.getUserName(), newKey));
            changeLogObj.setPassword(EncryptionDecryption.encryptAESGCM(changeLogObj.getPassword(), newKey));
            changeLogObj.setEmail(EncryptionDecryption.encryptAESGCM(changeLogObj.getEmail(), newKey));

        } catch (Exception e) {

        }
        return changeLogObj;
    }

    public static TextObj reEncryptTextEntry(TextObj textEntry, SecretKey newKey) {
        try {
            textEntry.setTextTitle(EncryptionDecryption.encryptAESGCM(textEntry.getTextTitle(), newKey));
            textEntry.setTextInformation(EncryptionDecryption.encryptAESGCM(textEntry.getTextInformation(), newKey));
            textEntry.setTags(tagEcbBlockENC(textEntry.getTags(),newKey));
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return textEntry;
    }

    public static String tagEcbBlockENC(String tags, SecretKey key) throws Exception {
        StringBuilder compile = new StringBuilder();
        String[] tagsArr =tags.split(" ");
        for (String tag : tagsArr) {
            compile.append(EncryptionDecryption.encryptAESECB(tag, key)).append(" ");
        }

        if (compile.length() > 0) {
            compile.setLength(compile.length() - 1);
        }
        return compile.toString();
    }


    public static String tagEcbBlockDEC(String tags, SecretKey key) throws Exception {
        StringBuilder compile = new StringBuilder();
        String[] tagsArr = tags.split(" ");
        for (String tag : tagsArr) {
            compile.append(EncryptionDecryption.decryptAESECB(tag, key)).append(" ");
        }

        if (compile.length() > 0) {
            compile.setLength(compile.length() - 1);
        }
        return compile.toString();
    }

    public static ChangeLogObj newChangeLog(AccountObj account) {
        return new ChangeLogObj(account, getNowDate(), account.getUserPlatform(), account.getUserName(), account.getUserEmail(), account.getUserPassword());
    }

    private static SecretKey getKey() {
        DataStore dataStore = DataStore.getInstance();
        SecretKey key = (SecretKey) dataStore.getObject("default_key");
        return key;
    }


    private static Timestamp getNowDate() {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Timestamp date = java.sql.Timestamp.valueOf(localDateTime);
        return date;
    }
}
