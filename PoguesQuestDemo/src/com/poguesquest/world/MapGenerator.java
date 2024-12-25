package com.poguesquest.world;

import java.util.Random;

public class MapGenerator {
    private final int width;
    private final int height;
    private final int[][] map;
    private final Random random;
    private final WallGenerator wallGenerator;

    // Constructor to initialize the map generator with dimensions and seed
    public MapGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.map = new int[height][width];
        this.random = new Random(seed);
        this.wallGenerator = new WallGenerator(width, height, map);
    }

    // Method to generate the map with obstacles and starting position
    public int[][] generateMap(int startX, int startY) {
        // Ensure the starting position is within bounds
        if (startX < 1 || startX >= width - 1 || startY < 1 || startY >= height - 1) {
            throw new IllegalArgumentException("Starting position is out of bounds");
        }

        // Carve the initial floor area for the whole map
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                map[y][x] = 1; // Carve floor (1 = floor)
            }
        }

        // Add detailed obstacle walls
        addDetailedObstacles();

        // Keep the wall that surrounds the player
        createPlayerBoundary(startX, startY);
        createRandomHole(startX, startY);

        // Update wall types using the WallGenerator
        wallGenerator.generateWalls();
        wallGenerator.updateWallTypes();

        return map;
    }

    // Method to add detailed obstacles
    private void addDetailedObstacles() {
        // Adding multiple T-walls
        for (int i = 0; i < 5; i++) {
            addTWall(random.nextInt(width - 4) + 2, random.nextInt(height - 4) + 2);
        }
        // Adding multiple L-walls
        for (int i = 0; i < 5; i++) {
            addLWall(random.nextInt(width - 4) + 2, random.nextInt(height - 4) + 2);
        }
        // Adding multiple I-walls
        for (int i = 0; i < 5; i++) {
            addIWall(random.nextInt(width - 4) + 2, random.nextInt(height - 4) + 2);
        }
        // Adding multiple plus (+) walls
        for (int i = 0; i < 5; i++) {
            addPlusWall(random.nextInt(width - 4) + 2, random.nextInt(height - 4) + 2);
        }
        // Adding multiple square walls
        for (int i = 0; i < 5; i++) {
            addSquareWall(random.nextInt(width - 4) + 2, random.nextInt(height - 4) + 2);
        }
    }

    // Method to create a T-shaped wall
    private void addTWall(int centerX, int centerY) {
        for (int x = centerX - 1; x <= centerX + 1; x++) {
            map[centerY][x] = 0; // Horizontal part of T
        }
        map[centerY - 1][centerX] = 0; // Vertical part of T
    }

    // Method to create an L-shaped wall
    private void addLWall(int startX, int startY) {
        for (int y = startY; y < startY + 3; y++) {
            map[y][startX] = 0; // Vertical part of L
        }
        map[startY + 2][startX + 1] = 0; // Horizontal part of L
    }

    // Method to create an I-shaped wall
    private void addIWall(int startX, int startY) {
        for (int y = startY; y < startY + 4; y++) {
            map[y][startX] = 0; // Vertical I wall
        }
    }

    // Method to create a plus-shaped (+) wall
    private void addPlusWall(int centerX, int centerY) {
        map[centerY][centerX] = 0;
        map[centerY - 1][centerX] = 0;
        map[centerY + 1][centerX] = 0;
        map[centerY][centerX - 1] = 0;
        map[centerY][centerX + 1] = 0;
    }

    // Method to create a square-shaped wall
    private void addSquareWall(int centerX, int centerY) {
        for (int y = centerY - 1; y <= centerY + 1; y++) {
            for (int x = centerX - 1; x <= centerX + 1; x++) {
                map[y][x] = 0;
            }
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
