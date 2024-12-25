package com.poguesquest.world;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

public class TileSheet {
    private BufferedImage floorSheet;
    private BufferedImage wallSheet;
    private final int tileWidth = 35;
    private final int tileHeight = 35;
    private final int columns = 4;
    private final int rows = 4;

    public TileSheet(String floorSheetPath, String wallSheetPath) {
        try {
            floorSheet = ImageIO.read(Paths.get(floorSheetPath).toFile());
            wallSheet = ImageIO.read(Paths.get(wallSheetPath).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getFloorTile(int index) {
        return getTile(floorSheet, index);
    }

    public BufferedImage getWallTile(int index) {
        return getTile(wallSheet, index);
    }

    private BufferedImage getTile(BufferedImage sheet, int index) {
        int x = (index % columns) * tileWidth;
        int y = (index / columns) * tileHeight;

        if (x + tileWidth > sheet.getWidth() || y + tileHeight > sheet.getHeight()) {
            System.out.println("Error: Tile index " + index + " is out of bounds.");
            return null;
        }

        return sheet.getSubimage(x, y, tileWidth, tileHeight);
    }
}