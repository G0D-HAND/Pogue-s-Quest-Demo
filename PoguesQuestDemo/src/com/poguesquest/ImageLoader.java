package com.poguesquest;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageLoader {
    public static BufferedImage loadImage(String path) {
        System.out.println("Loading image from path: " + path); // Debugging line
        try {
            return ImageIO.read(ImageLoader.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to load character sprite sheets based on character name
    public static BufferedImage loadCharacterSpriteSheet(String characterName, String animationType) {
        String filePath = characterName + animationType + ".png";  // Build the file path dynamically
        return loadImage("/" + filePath); // Load image using relative path
    }

    // Add a method to load the bullet image
    public static BufferedImage loadBulletImage() {
        return loadImage("/bullet.png"); // Specify the path to the bullet image
    }

    // Add a method to load the hit effect sprite sheet and extract frames
    public static BufferedImage[] loadHitEffectFrames() {
        BufferedImage spriteSheet = loadImage("/hit_effect.png"); // Specify the path to the sprite sheet
        BufferedImage[] frames = new BufferedImage[8];
        int frameWidth = spriteSheet.getWidth() / 8;
        int frameHeight = spriteSheet.getHeight();

        for (int i = 0; i < 8; i++) {
            frames[i] = spriteSheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }
        return frames;
    }
}