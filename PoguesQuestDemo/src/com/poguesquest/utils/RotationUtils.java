package com.poguesquest.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class RotationUtils {
    // Rotate an image around its center
    public static BufferedImage rotateImage(BufferedImage image, double angle) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage rotated = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = rotated.createGraphics();

        // Apply rotation
        AffineTransform transform = new AffineTransform();
        transform.rotate(angle, width / 2.0, height / 2.0); // Rotate around the center
        g2.setTransform(transform);

        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return rotated;
    }
}
