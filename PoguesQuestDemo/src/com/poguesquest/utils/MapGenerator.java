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

        // Start the walker at the specified starting position
        int currentX = startX;
        int currentY = startY;

        // Ensure the starting position is within bounds
        if (currentX < 0 || currentX >= width || currentY < 0 || currentY >= height) {
            throw new IllegalArgumentException("Starting position is out of bounds");
        }

        // Carve the initial tile
        map[currentY][currentX] = 1;

        // Perform random walk for the specified number of steps
        for (int i = 0; i < steps; i++) {
            // Choose a random direction: 0 = up, 1 = down, 2 = left, 3 = right
            int direction = random.nextInt(4);

            // Move in the chosen direction
            switch (direction) {
                case 0: // Up
                    if (currentY > 0) currentY--;
                    break;
                case 1: // Down
                    if (currentY < height - 1) currentY++;
                    break;
                case 2: // Left
                    if (currentX > 0) currentX--;
                    break;
                case 3: // Right
                    if (currentX < width - 1) currentX++;
                    break;
            }

            // Carve out a floor tile at the new position
            map[currentY][currentX] = 1;
        }

        return map;
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