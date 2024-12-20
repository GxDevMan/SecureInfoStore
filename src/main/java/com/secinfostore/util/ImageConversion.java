package com.secinfostore.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageConversion {
    public static Image byteArraytoImage(byte[] imageBytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        return new Image(byteArrayInputStream);
    }

    public static byte[] convertImageToByteArray(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            BufferedImage jpegImage = new BufferedImage(
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g = jpegImage.createGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, jpegImage.getWidth(), jpegImage.getHeight());
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();
            javax.imageio.ImageIO.write(jpegImage, "jpeg", byteArrayOutputStream);
            byteArrayOutputStream.close();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return byteArray;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    public static Image convertBufferedImageToImage(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(imageData);

        return new Image(inputStream);
    }


}
