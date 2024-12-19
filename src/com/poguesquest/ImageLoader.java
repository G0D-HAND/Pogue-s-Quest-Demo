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
}
