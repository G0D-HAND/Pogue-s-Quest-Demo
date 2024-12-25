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

        // Perform random walk to generate the map
        int currentX = startX;
        int currentY = startY;

        for (int i = 0; i < steps; i++) {
            // Place a room at random intervals
            if (random.nextInt(20) == 0) {
                int roomWidth = random.nextInt(4) + 2;
                int roomHeight = random.nextInt(4) + 2;
                placeRoom(currentX, currentY, roomWidth, roomHeight);
            } else {
                // Carve a corridor
                int corridorLength = random.nextInt(1) + 3;
                carveCorridor(currentX, currentY, corridorLength);
            }

            // Randomly choose a direction to walk
            int direction = random.nextInt(4);
            int stepSize = random.nextInt(2) + 1;

            switch (direction) {
                case 0: // Up
                    currentY = Math.max(1, currentY - stepSize);
                    break;
                case 1: // Down
                    currentY = Math.min(height - 2, currentY + stepSize);
                    break;
                case 2: // Left
                    currentX = Math.max(1, currentX - stepSize);
                    break;
                case 3: // Right
                    currentX = Math.min(width - 2, currentX + stepSize);
                    break;
            }

            // Carve a floor at the new position
            carve(currentX, currentY, 1);
        }

        // Initialize the WallGenerator after the floors are carved
        WallGenerator wallGenerator = new WallGenerator(width, height, map);

        // Create boundaries and random hole
        createLevelBoundary();
        createPlayerBoundary(startX, startY);
        createRandomHole(startX, startY);

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

    // Method to carve a corridor
    private void carveCorridor(int startX, int startY, int length) {
        int direction = random.nextInt(4);

        for (int i = 0; i < length; i++) {
            switch (direction) {
                case 0: // Up
                    startY = Math.max(1, startY - 1);
                    break;
                case 1: // Down
                    startY = Math.min(height - 2, startY + 1);
                    break;
                case 2: // Left
                    startX = Math.max(1, startX - 1);
                    break;
                case 3: // Right
                    startX = Math.min(width - 2, startX + 1);
                    break;
            }
            carve(startX, startY, 1);
        }
    }

    // Method to create the boundary walls around the entire map
    public void createLevelBoundary() {
        // Top and bottom boundaries
        for (int x = 0; x < width; x++) {
            map[0][x] = 0; // Top boundary wall
            map[height - 1][x] = 0; // Bottom boundary wall
        }

        // Left and right boundaries
        for (int y = 0; y < height; y++) {
            map[y][0] = 0; // Left boundary wall
            map[y][width - 1] = 0; // Right boundary wall
        }
    }

    // Method to create a boundary wall around a 5x5 area centered on the player
    public void createPlayerBoundary(int startX, int startY) {
        for (int y = startY - 2; y <= startY + 2; y++) {
            for (int x = startX - 2; x <= startX + 2; x++) {
                if (isWithinBounds(x, y)) {
                    if (x == startX - 2 || x == startX + 2 || y == startY - 2 || y == startY + 2) {
                        map[y][x] = 0; // Set boundary tiles to walls
                    } else {
                        map[y][x] = 1; // Set inner tiles to floors
                    }
                }
            }
        }
    }

    // Method to create a random hole in one of the sides of the 5x5 area
    public void createRandomHole(int startX, int startY) {
        int side = random.nextInt(4); // Randomly select a side (0 = top, 1 = bottom, 2 = left, 3 = right)
        int holePosition;

        switch (side) {
            case 0 -> { // Top side (y = startY - 2)
                holePosition = random.nextInt(3) - 1 + startX;
                if (isWithinBounds(holePosition, startY - 2)) {
                    map[startY - 2][holePosition] = 1; // Create hole in the top boundary wall
                }
            }
            case 1 -> { // Bottom side (y = startY + 2)
                holePosition = random.nextInt(3) - 1 + startX;
                if (isWithinBounds(holePosition, startY + 2)) {
                    map[startY + 2][holePosition] = 1; // Create hole in the bottom boundary wall
                }
            }
            case 2 -> { // Left side (x = startX - 2)
                holePosition = random.nextInt(3) - 1 + startY;
                if (isWithinBounds(startX - 2, holePosition)) {
                    map[holePosition][startX - 2] = 1; // Create hole in the left boundary wall
                }
            }
            case 3 -> { // Right side (x = startX + 2)
                holePosition = random.nextInt(3) - 1 + startY;
                if (isWithinBounds(startX + 2, holePosition)) {
                    map[holePosition][startX + 2] = 1; // Create hole in the right boundary wall
                }
            }
        }
    }

    // Helper method to check if coordinates are within map bounds
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
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
