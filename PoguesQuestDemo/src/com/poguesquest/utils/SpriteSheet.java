package com.poguesquest.utils;

import java.awt.image.BufferedImage;

public class SpriteSheet {
    private BufferedImage sheet;

    public SpriteSheet(BufferedImage sheet) {
        this.sheet = sheet;
    }

    // General function to get a frame from the sprite sheet
    public BufferedImage getFrame(int index, int frameWidth, int frameHeight) {
        int columns = sheet.getWidth() / frameWidth; // Number of frames in one row

        // Ensure the index is within bounds
        int x = (index % columns) * frameWidth;  // x-coordinate of the frame
        int y = (index / columns) * frameHeight; // y-coordinate of the frame

        // Ensure we do not try to go outside the bounds of the image
        if (x + frameWidth > sheet.getWidth() || y + frameHeight > sheet.getHeight()) {
            System.out.println("Error: Frame " + index + " is out of bounds.");
            return null;  // Return null if the frame is out of bounds
        }

        // Get the subimage of the sprite sheet
        return sheet.getSubimage(x, y, frameWidth, frameHeight);
    }

    // Specific function to get a frame for the player character sprite (35x35)
    public BufferedImage getPlayerFrame(int index) {
        int frameWidth = 35;  // Width of each frame
        int frameHeight = 35; // Height of each frame
        return getFrame(index, frameWidth, frameHeight);
    }
}