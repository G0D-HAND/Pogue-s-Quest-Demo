package com.poguesquest.world;


public class WallGenerator {
    private final int[][] map;
    private final int width;
    private final int height;

    // Constructor to initialize the wall generator with the map dimensions and map grid
    public WallGenerator(int width, int height, int[][] map) {
        this.width = width;
        this.height = height;
        this.map = map;
    }

    // Method to initialize the map with walls while respecting pre-existing tiles
    public void generateWalls() {
        // Set all empty tiles (2 = unknown) to walls (0 = wall)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == 2) {
                    map[y][x] = 0;
                }
            }
        }
    }

    // Method to update wall types based on neighboring tiles using the blob tile system
    public void updateWallTypes() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == 0) { // If the current tile is a wall
                    int binaryRepresentation = getBinaryRepresentation(x, y);
                    map[y][x] = getWallTile(binaryRepresentation);
                }
            }
        }
    }

    // Helper method to get the binary representation of a tile based on its neighbors
    private int getBinaryRepresentation(int x, int y) {
        int binaryRepresentation = 0;

        // Check the four cardinal directions for floor tiles and set bits accordingly
        if (y > 0 && map[y - 1][x] == 1) binaryRepresentation |= 1; // Top neighbor
        if (y < height - 1 && map[y + 1][x] == 1) binaryRepresentation |= 2; // Bottom neighbor
        if (x > 0 && map[y][x - 1] == 1) binaryRepresentation |= 4; // Left neighbor
        if (x < width - 1 && map[y][x + 1] == 1) binaryRepresentation |= 8; // Right neighbor

        return binaryRepresentation;
    }

    // Translate binary representation to tileset index using the blob tile system
    int getWallTile(int binaryRepresentation) {
        return switch (binaryRepresentation) {
            case 0b0101 -> 0;
            case 0b0111 -> 1; 
            case 0b1001 -> 2;
            case 0b0100 -> 3;
            case 0b1101 -> 4;
            case 0b0000 -> 5;
            case 0b1110 -> 6;
            case 0b1100 -> 7;
            case 0b0110 -> 8; 
            case 0b1011 -> 9;
            case 0b1010 -> 10;
            case 0b1000 -> 11;
            case 0b0001 -> 12;
            case 0b0011 -> 13;
            case 0b0010 -> 14; 
            case 0b1111 -> 15;
            default -> 15;
        };
    }

    // Helper method to check if a tile is a wall
    public boolean isWall(int tile) {
        return tile == 0;
    }

    // Method to print a variant of floor for debugging
    public void printFloorVariant() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == 1) {
                    System.out.print('.'); // Floor
                } else {
                    System.out.print('#'); // Wall
                }
            }
            System.out.println();
        }
    }
}