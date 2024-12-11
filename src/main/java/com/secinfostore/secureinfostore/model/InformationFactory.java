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

    public static TextObj newTextEntry(String title, String textInfo, String tags) {
        SecretKey key = getKey();
        Timestamp nowTime = getNowDate();
        try {
            title = EncryptionDecryption.encryptAESGCM(title, key);
            textInfo = EncryptionDecryption.encryptAESGCM(textInfo, key);

            StringBuilder compile = new StringBuilder();
            String[] tagsArr = tags.split(" ");
            for (String tag : tagsArr) {
                compile.append(EncryptionDecryption.encryptAESECB(tag, key)).append(" ");
            }

            if (compile.length() > 0) {
                compile.setLength(compile.length() - 1);
            }

            tags = compile.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new TextObj(nowTime, title, textInfo, tags);
    }

    public static TextObj decTextEntry(TextObj textObj) {
        SecretKey key = getKey();
        try {
            textObj.setTextInformation(EncryptionDecryption.decryptAESGCM(textObj.getTextInformation(), key));
            textObj.setTextTitle(EncryptionDecryption.decryptAESGCM(textObj.getTextTitle(), key));

            StringBuilder compile = new StringBuilder();
            String[] tagsArr = textObj.getTags().split(" ");
            for (String tag : tagsArr) {
                compile.append(EncryptionDecryption.decryptAESECB(tag.trim(), key)).append(" ");
            }

            if (compile.length() > 0) {
                compile.setLength(compile.length() - 1);
            }

            textObj.setTags(compile.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return textObj;
    }

    public static TextObjDTO decPreviewTextEntry(TextObjDTO textObj) {
        SecretKey key = getKey();
        try {
            textObj.setTextTitle(EncryptionDecryption.decryptAESGCM(textObj.getTextTitle(), key));

            if (textObj.getTags() != null) {
                StringBuilder compile = new StringBuilder();
                String[] tagsArr = textObj.getTags().split(" ");
                for (String tag : tagsArr) {
                    compile.append(EncryptionDecryption.decryptAESECB(tag, key)).append(" ");
                }

                if (compile.length() > 0) {
                    compile.setLength(compile.length() - 1);
                }

                textObj.setTags(compile.toString());
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

            StringBuilder compile = new StringBuilder();
            String[] tagsArr = textObj.getTags().split(" ");
            for (String tag : tagsArr) {
                compile.append(EncryptionDecryption.encryptAESECB(tag, key)).append(" ");
            }

            if (compile.length() > 0) {
                compile.setLength(compile.length() - 1);
            }
            textObj.setTags(compile.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return textObj;
    }

    public static AccountObj reEncryptAccountObj(AccountObj accountObj, SecretKey oldKey, SecretKey newKey) {
        try {
            accountObj.setUserName(EncryptionDecryption.decryptAESGCM(accountObj.getUserName(), oldKey));
            accountObj.setPassword(EncryptionDecryption.decryptAESGCM(accountObj.getPassword(), oldKey));
            accountObj.setEmail(EncryptionDecryption.decryptAESGCM(accountObj.getEmail(), oldKey));

            accountObj.setUserName(EncryptionDecryption.encryptAESGCM(accountObj.getUserName(), newKey));
            accountObj.setPassword(EncryptionDecryption.encryptAESGCM(accountObj.getPassword(), newKey));
            accountObj.setEmail(EncryptionDecryption.encryptAESGCM(accountObj.getEmail(), newKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accountObj;
    }


    public static ChangeLogObj reEncryptChangeLog(ChangeLogObj changeLogObj, SecretKey oldKey, SecretKey newKey) {
        try {
            changeLogObj.setUserName(EncryptionDecryption.decryptAESGCM(changeLogObj.getUserName(), oldKey));
            changeLogObj.setPassword(EncryptionDecryption.decryptAESGCM(changeLogObj.getPassword(), oldKey));
            changeLogObj.setEmail(EncryptionDecryption.decryptAESGCM(changeLogObj.getEmail(), oldKey));

            changeLogObj.setUserName(EncryptionDecryption.encryptAESGCM(changeLogObj.getUserName(), newKey));
            changeLogObj.setPassword(EncryptionDecryption.encryptAESGCM(changeLogObj.getPassword(), newKey));
            changeLogObj.setEmail(EncryptionDecryption.encryptAESGCM(changeLogObj.getEmail(), newKey));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return changeLogObj;
    }

    public static TextObj reEncryptTextEntry(TextObj textEntry, SecretKey oldKey, SecretKey newKey) {
        try {
            textEntry.setTextTitle(EncryptionDecryption.decryptAESGCM(textEntry.getTextTitle(), oldKey));
            textEntry.setTextInformation(EncryptionDecryption.decryptAESGCM(textEntry.getTextInformation(), oldKey));

            textEntry.setTextTitle(EncryptionDecryption.encryptAESGCM(textEntry.getTextTitle(), newKey));
            textEntry.setTextInformation(EncryptionDecryption.encryptAESGCM(textEntry.getTextInformation(), newKey));

            StringBuilder compile = new StringBuilder();
            String[] tagsArr = textEntry.getTags().split(" ");
            for (String tag : tagsArr) {
                String individualTag = EncryptionDecryption.decryptAESECB(tag, oldKey);
                individualTag = EncryptionDecryption.encryptAESECB(individualTag, newKey);
                compile.append(individualTag).append(" ");
            }

            if (compile.length() > 0) {
                compile.setLength(compile.length() - 1);
            }

            textEntry.setTags(compile.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return textEntry;
    }

    public static ChangeLogObj newChangeLog(AccountObj account) {
        return new ChangeLogObj(account, getNowDate(), account.getPlatformName(), account.getUserName(), account.getEmail(), account.getPassword());
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
