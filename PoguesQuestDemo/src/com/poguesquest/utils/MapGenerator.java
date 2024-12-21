package com.poguesquest.utils;

import java.util.Random;

public class MapGenerator {
    private final int width;  // Total width of the map (in tiles)
    private final int height; // Total height of the map (in tiles)
    private final int[][] map; // The map grid (1 = floor, 0 = wall)

    private final Random random;

    // Constructor to initialize the map dimensions and random seed
    public MapGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.map = new int[height][width];
        this.random = new Random(seed);
    }

    // Main method to generate the map using a random walker algorithm
    public int[][] generateMap(int steps, int startX, int startY) {
        // Initialize map with walls (0 = wall)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = 0;
            }
        }

        // Ensure the starting position is within bounds
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
            throw new IllegalArgumentException("Starting position is out of bounds");
        }

        // Carve a 3x3 area around the starting position
        carve(startX, startY, 1);

        // Start the walker at the initial position
        int currentX = startX;
        int currentY = startY;

        // Perform random walk
        for (int i = 0; i < steps; i++) {
            // Chance to place a larger room
            if (random.nextInt(15) == 0) {
                placeRoom(currentX, currentY, 6, 6); // Place a 6x6 room
                placeCoverDots(currentX, currentY, 6, 6, 3); // Add cover dots
            }

            // Choose a random direction
            int direction = random.nextInt(4);
            int stepSize = random.nextInt(3) + 1; // Random step size (1-3 tiles)

            switch (direction) {
                case 0: // Up
                    currentY = Math.max(0, currentY - stepSize);
                    break;
                case 1: // Down
                    currentY = Math.min(height - 1, currentY + stepSize);
                    break;
                case 2: // Left
                    currentX = Math.max(0, currentX - stepSize);
                    break;
                case 3: // Right
                    currentX = Math.min(width - 1, currentX + stepSize);
                    break;
            }

            // Carve a wider path
            carve(currentX, currentY, 1);
        }

        return map;
    }

    // Carve a square area of the given size centered at (centerX, centerY)
    private void carve(int centerX, int centerY, int size) {
        for (int y = centerY - size; y <= centerY + size; y++) {
            for (int x = centerX - size; x <= centerX + size; x++) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    map[y][x] = 1; // Carve floor
                }
            }
        }
    }

    // Place a rectangular room centered at (centerX, centerY) with specified width and height
    private void placeRoom(int centerX, int centerY, int roomWidth, int roomHeight) {
        for (int y = centerY - roomHeight / 2; y <= centerY + roomHeight / 2; y++) {
            for (int x = centerX - roomWidth / 2; x <= centerX + roomWidth / 2; x++) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    map[y][x] = 1; // Carve floor
                }
            }
        }
    }

    // Place random cover dots in a room for hiding spots
    private void placeCoverDots(int centerX, int centerY, int roomWidth, int roomHeight, int dotCount) {
        for (int i = 0; i < dotCount; i++) {
            int dotX = centerX + random.nextInt(roomWidth) - roomWidth / 2;
            int dotY = centerY + random.nextInt(roomHeight) - roomHeight / 2;

            if (dotX >= 0 && dotX < width && dotY >= 0 && dotY < height) {
                map[dotY][dotX] = 0; // Place a wall as cover dot
            }
        }
    }

    // Debug method to print the map to the console
    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(map[y][x] == 1 ? "." : "#"); // Floor = '.', Wall = '#'
            }
            System.out.println();
        }
    }

    // Getters
    public int[][] getMap() {
        return map;
    }
}