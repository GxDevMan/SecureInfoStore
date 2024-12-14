package com.secinfostore.util;

import com.secinfostore.controller.interfaces.ProgressObserver;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

public class EncryptionDecryption {
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_MODE = "AES/GCM/NoPadding";
    private static final String AES_ECB_MODE = "AES/ECB/PKCS5Padding";
    private static final int KEY_SIZE = 256; // 256 bits
    private static final int GCM_TAG_LENGTH = 128; // in bits or 16 bytes
    private static final int GCM_IV_LENGTH = 12;  // in bytes
    private static final int CHUNK_SIZE = 10 * 1024 * 1024; // 10 MB

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
        byte[] encryptedMessage = new byte[iv.length + encryptedBytes.length]; //<iv><data><tag>
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

    // Per 10 MB has iv length and tag <IV><DATA><TAG>
    public static void encryptFileInChunksAESGCM(File inputFile, String outputDirectory, SecretKey secretKey, ProgressObserver observer, Supplier<Boolean> isCancelled) throws Exception {
        observer.updateStatus("Starting Encryption");

        // Output file name with .enc extension
        String outputFileName = outputDirectory + File.separator + inputFile.getName() + ".enc";
        try (FileInputStream sourceFileBytes = new FileInputStream(inputFile);
             FileOutputStream destinationFileBytes = new FileOutputStream(outputFileName)) {

            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            long totalSize = inputFile.length();
            long totalChunks = (totalSize + CHUNK_SIZE - 1) / CHUNK_SIZE;
            long chunkIndex = 0;

            SecureRandom secureRandom = new SecureRandom();

            while ((bytesRead = sourceFileBytes.read(buffer)) != -1) {
                if (isCancelled.get()) {
                    observer.updateStatus("Encryption Cancelled");
                    throw new CancellationException();
                }

                // Generate a new random IV for this chunk
                byte[] iv = new byte[GCM_IV_LENGTH];
                secureRandom.nextBytes(iv);

                // Write the IV to the output file
                destinationFileBytes.write(iv);

                // Initialize Cipher with the new IV
                Cipher cipher = Cipher.getInstance(AES_GCM_MODE);
                GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

                // Encrypt the chunk
                byte[] encryptedChunk = cipher.doFinal(buffer,0,bytesRead);

                // Write the encrypted chunk to the output file
                destinationFileBytes.write(encryptedChunk);

                // Update progress
                chunkIndex++;
                observer.updateStatus(String.format("Processed chunk %d of %d (%.2f%%)%n", chunkIndex, totalChunks, (chunkIndex * 100.0) / totalChunks));
                observer.updateProgress((double) chunkIndex / totalChunks);
            }
        }

        int fileSeparatorIndex = outputFileName.lastIndexOf('\\');
        outputFileName = outputFileName.substring(fileSeparatorIndex + 1);

        observer.updateStatus(String.format("File encryption completed. Output file: %s", outputFileName));
        observer.updateProgress(1.0);
    }

    public static void decryptFileInChunksAESGCM(File encryptedFile, String outputDirectory, SecretKey secretKey, ProgressObserver observer, Supplier<Boolean> isCancelled) throws Exception {
        observer.updateStatus("Starting Decryption");
        String outputFileName = encryptedFile.getName().replace(".enc", "");
        File outputFile = new File(outputDirectory + File.separator + outputFileName);

        try (FileInputStream sourceFileBytes = new FileInputStream(encryptedFile);
             FileOutputStream destinationFileBytes = new FileOutputStream(outputFile)) {
            byte[] ivRead = new byte[GCM_IV_LENGTH];
            byte[] buffer = new byte[CHUNK_SIZE + (GCM_TAG_LENGTH / 8)];
            int bytesRead;

            long totalSize = encryptedFile.length();
            long chunkIndex = 0;
            long totalChunks = (totalSize + CHUNK_SIZE - 1) / CHUNK_SIZE;

            // Read and decrypt the file in chunks
            while (true) {
                // Read the IV for the current chunk
                if (sourceFileBytes.read(ivRead) != GCM_IV_LENGTH) {
                    break; // End of file
                }

                // Read the encrypted chunk (data + tag)
                bytesRead = sourceFileBytes.read(buffer);
                if (bytesRead == -1) {
                    observer.updateStatus("Unexpected end of file.");
                    throw new IllegalStateException("Unexpected end of file.");
                }

                if (isCancelled.get()) {
                    observer.updateStatus("Decryption Cancelled");
                    throw new CancellationException();
                }

                // Initialize the cipher for decryption
                Cipher cipher = Cipher.getInstance(AES_GCM_MODE);
                GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, ivRead);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

                // Decrypt the chunk
                byte[] decryptedChunk = cipher.doFinal(buffer, 0, bytesRead);

                // Write the decrypted data to the output file
                destinationFileBytes.write(decryptedChunk);

                // Update progress
                chunkIndex++;
                observer.updateStatus(String.format("Processed chunk %d of %d (%.2f%%)%n",
                        chunkIndex, totalChunks, (chunkIndex * 100.0) / totalChunks));
                observer.updateProgress((double) chunkIndex / totalChunks);
            }
        }

        int fileSeparatorIndex = outputFileName.lastIndexOf('\\');
        outputFileName = outputFileName.substring(fileSeparatorIndex + 1);

        observer.updateStatus(String.format("File Decryption completed. Output file: %s", outputFileName));
        observer.updateProgress(1.0);
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
