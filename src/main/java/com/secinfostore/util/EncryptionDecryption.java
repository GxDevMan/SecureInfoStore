package com.secinfostore.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionDecryption {
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_MODE = "AES/GCM/NoPadding";
    private static final String AES_ECB_MODE = "AES/ECB/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128; // in bits
    private static final int GCM_IV_LENGTH = 12;  // in bytes

    // Generate a new AES key
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

    public static SecretKey loadKeyFromFile(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    // Convert a key to Base64 text
    public static String keyToBase64Text(Key key) {
        byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    // Convert a string representation of a key to a SecretKey object
    public static SecretKey convertStringToSecretKey(String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, AES_ALGORITHM);
    }

    // Save the key in text format
    public static void saveKeyTextToFile(String keyText, String filename) throws Exception {
        byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);
        Path filePath = Paths.get(filename);
        Files.write(filePath, keyBytes);
    }

    // Save the key to a file
    public static void saveKeyToFile(Key key, String filename) throws Exception {
        byte[] keyBytes = key.getEncoded();
        Path filePath = Paths.get(filename);
        Files.write(filePath, keyBytes);
    }

    // Encrypt using AES-GCM
    public static String encryptAESGCM(String message, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_MODE);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedMessage = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedMessage, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedMessage, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    // Decrypt using AES-GCM
    public static String decryptAESGCM(String encryptedMessage, SecretKey secretKey) throws Exception {
        byte[] encryptedBytesWithIv = Base64.getDecoder().decode(encryptedMessage);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedBytes = new byte[encryptedBytesWithIv.length - iv.length];

        System.arraycopy(encryptedBytesWithIv, 0, iv, 0, iv.length);
        System.arraycopy(encryptedBytesWithIv, iv.length, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(AES_GCM_MODE);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // Encrypt using AES-ECB
    public static String encryptAESECB(String message, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ECB_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt using AES-ECB
    public static String decryptAESECB(String encryptedMessage, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ECB_MODE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void securelyClearKey(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        Arrays.fill(keyBytes, (byte) 0);
    }
}
