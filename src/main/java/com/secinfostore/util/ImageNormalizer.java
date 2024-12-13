package com.secinfostore.util;

import io.nayuki.qrcodegen.QrCode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class ImageNormalizer {

    // Code based on Nayuki -> https://github.com/nayuki/QR-Code-generator/blob/master/java/QrCodeGeneratorDemo.java?ts=4
    public static BufferedImage toQRImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr, "QR code object must not be null");
        if (scale <= 0 || border < 0) {
            throw new IllegalArgumentException("Scale and border must be non-negative integers.");
        }
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale) {
            throw new IllegalArgumentException("Scale or border size is too large.");
        }

        // Calculate image dimensions
        int qrSize = qr.size;
        int imgSize = (qrSize + border * 2) * scale;
        BufferedImage result = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);

        // Render the QR code
        for (int y = 0; y < imgSize; y++) {
            for (int x = 0; x < imgSize; x++) {
                // Determine the corresponding module in the QR code
                int moduleX = x / scale - border;
                int moduleY = y / scale - border;

                // Check if the module is dark or light
                boolean isDark = moduleX >= 0 && moduleX < qrSize && moduleY >= 0 && moduleY < qrSize
                        && qr.getModule(moduleX, moduleY);

                // Set the pixel color
                result.setRGB(x, y, isDark ? darkColor : lightColor);
            }
        }
        return result;
    }


    public static BufferedImage normalizedImage(BufferedImage originalImage, int maxWidth, int maxHeight){
        originalImage = convertToJpgBufferedImage(originalImage);

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double widthScale = (double) maxWidth / originalWidth;
        double heightScale = (double) maxHeight / originalHeight;
        double scaleFactor = Math.min(widthScale, heightScale);

        int newWidth = (int) (originalWidth * scaleFactor);
        int newHeight = (int) (originalHeight * scaleFactor);

        int imageType = originalImage.getType();
        if (imageType == BufferedImage.TYPE_CUSTOM || imageType == 0) {
            imageType = BufferedImage.TYPE_INT_RGB;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    private static BufferedImage convertToJpgBufferedImage(BufferedImage inputImage) {
        BufferedImage jpgImage = new BufferedImage(
                inputImage.getWidth(),
                inputImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = jpgImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, null);
        g2d.dispose();

        return jpgImage;
    }


}
