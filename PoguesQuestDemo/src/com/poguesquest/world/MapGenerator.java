package com.poguesquest.world;

import java.util.Random;

public class MapGenerator {
    private final int width;
    private final int height;
    private final int[][] map;
    private final Random random;

    // Constructor to initialize the map generator with dimensions and seed
    public MapGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.map = new int[height][width];
        this.random = new Random(seed);
    }

    // Method to generate the map with specified steps and starting position
    public int[][] generateMap(int steps, int startX, int startY) {
        // Ensure the starting position is within bounds
        if (startX < 1 || startX >= width - 1 || startY < 1 || startY >= height - 1) {
            throw new IllegalArgumentException("Starting position is out of bounds");
        }

        // Carve the initial floor area
        carve(startX, startY, 1);

        // Define initial walker positions
        int[][] walkerPositions = {{startX, startY - 1}, {startX - 1, startY}, {startX + 1, startY}, {startX, startY + 1}};
        for (int[] position : walkerPositions) {
            int currentX = position[0];
            int currentY = position[1];

            // Perform random walk to generate the map
            for (int i = 0; i < steps; i++) {
                // Occasionally place a room
                if (random.nextInt(15) == 0) {
                    int roomWidth = random.nextInt(4) + 4;
                    int roomHeight = random.nextInt(4) + 4;
                    placeRoom(currentX, currentY, roomWidth, roomHeight);
                }

                // Randomly choose a direction to walk
                int direction = random.nextInt(4);
                int stepSize = random.nextInt(3) + 1;

                switch (direction) {
                    case 0: // Up
                        currentY = Math.max(1, currentY - stepSize);
                        break;
                    case 1: // Down
                        currentY = Math.min(height - 1 - 1, currentY + stepSize);
                        break;
                    case 2: // Left
                        currentX = Math.max(1, currentX - stepSize);
                        break;
                    case 3: // Right
                        currentX = Math.min(width - 1 - 1, currentX + stepSize);
                        break;
                }

                // Carve a floor at the new position
                carve(currentX, currentY, 1);
            }
        }

        // Initialize the WallGenerator after the floors are carved
        WallGenerator wallGenerator = new WallGenerator(width, height, map);

        // Create boundaries and random hole
        wallGenerator.createBoundaryWalls(startX, startY);
        wallGenerator.createRandomHole(startX, startY);

        // Update wall types
        wallGenerator.updateWallTypes();

        return map;
    }

    // Method to carve out a floor area
    private void carve(int centerX, int centerY, int size) {
        for (int y = centerY - size; y <= centerY + size; y++) {
            for (int x = centerX - size; x <= centerX + size; x++) {
                if (x >= 1 && x < width - 1 && y >= 1 && y < height - 1) {
                    map[y][x] = 1; // Carve floor (1 = floor)
                }
            }
        }
    }

    // Method to place a rectangular room
    private void placeRoom(int centerX, int centerY, int roomWidth, int roomHeight) {
        for (int y = centerY - roomHeight / 2; y <= centerY + roomHeight / 2; y++) {
            for (int x = centerX - roomWidth / 2; x <= centerX + roomWidth / 2; x++) {
                if (x >= 1 && x < width - 1 && y >= 1 && y < height - 1) {
                    map[y][x] = 1; // Carve floor (1 = floor)
                }
            }
        }
    }

    // Method to print the map for debugging
    public void printMap() {
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

    // Getter for the map
    public int[][] getMap() {
        return map;
    }
}