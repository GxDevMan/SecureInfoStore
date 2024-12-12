package com.secinfostore.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageNormalizer {
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
